package hr.fer.zemris.java.webserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hr.fer.oprpp1.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.custom.scripting.exec.SmartScriptEngine;
import hr.fer.zemris.java.webserver.RequestContext.RCCookie;

public class SmartHttpServer {
	private String address;
	private String domainName;
	private int port;
	private int workerThreads;
	private int sessionTimeout;
	private Map<String, String> mimeTypes = new HashMap<>();
	private ServerThread serverThread;
	private ExecutorService threadPool;
	private Path documentRoot;
	private Map<String, IWebWorker> workersMap = new HashMap<>();
	private Map<String, SessionMapEntry> sessions = new HashMap<String, SmartHttpServer.SessionMapEntry>();
	private Random sessionRandom = new Random();
	private SessionCleaningThread sessionCleaningThread;

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("One argument was expected. The argument is"
					+ "the config file name which is in the root directory" + "of this project.");
			return;
		}
		SmartHttpServer server = new SmartHttpServer(args[0]);
		server.start();
	}

	public SmartHttpServer(String configFileName) {
		parseProperties(configFileName);
	}

	private void parseProperties(String configFileName) {
		Properties p = new Properties();
		try {
			p.load(Files.newInputStream(Paths.get(configFileName)));
			initializeVariablesFromProperties(p);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	private void initializeVariablesFromProperties(Properties p)
			throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
		address = p.getProperty("server.address").trim();
		domainName = p.getProperty("server.domainName").trim();
		port = Integer.parseInt(p.getProperty("server.port"));
		workerThreads = Integer.parseInt(p.getProperty("server.workerThreads"));
		documentRoot = Paths.get(p.getProperty("server.documentRoot"));
		sessionTimeout = Integer.parseInt(p.getProperty("session.timeout"));
		initializeMimeTypesFromProperties(p);
		initializeWorkersFromProperties(p);
	}

	private void initializeWorkersFromProperties(Properties p) throws IOException {
		String path = p.getProperty("server.workers");
		Path workersConfig = Paths.get(path);
		List<String> lines = Files.readAllLines(workersConfig);
		for (String l : lines) {
			if (l.trim().startsWith("#"))
				continue;
			String[] pathName = l.split("=");
			String name = pathName[0].trim();
			String fqcn = pathName[1].trim();

			if (workersMap.containsKey(name)) {
				throw new IllegalArgumentException("The same property " + "can not be defined twice.");
			}
			IWebWorker iww = null;
			try {
				iww = getWebWorker(fqcn);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			workersMap.put(pathName[0].trim(), iww);

		}
	}

	private IWebWorker getWebWorker(String fqcn)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> referenceToClass = this.getClass().getClassLoader().loadClass(fqcn);
		@SuppressWarnings("deprecation")
		Object newObject = referenceToClass.newInstance();
		IWebWorker iww = (IWebWorker) newObject;
		return iww;
	}

	private void initializeMimeTypesFromProperties(Properties p) throws IOException {
		Path mimeConfigPath = Paths.get(p.getProperty("server.mimeConfig"));
		List<String> lines = Files.readAllLines(mimeConfigPath);
		for (String l : lines) {
			if (l.trim().startsWith("#"))
				continue;
			String[] nameValue = l.split("=");
			mimeTypes.put(nameValue[0].trim(), nameValue[1].trim());
		}
	}

	protected synchronized void start() {
		if (serverThread != null && serverThread.isAlive())
			return;
		serverThread = new ServerThread();
		threadPool = Executors.newFixedThreadPool(workerThreads);
		serverThread.start();
		sessionCleaningThread = new SessionCleaningThread();
		sessionCleaningThread.setDaemon(true);
		sessionCleaningThread.start();
	}

	protected synchronized void stop() {
		System.out.println("tu sam");
		serverThread.run = false;
		sessionCleaningThread.run = false;
		threadPool.shutdown();
	}

	protected class ServerThread extends Thread {
		private boolean run = true;

		@Override
		public void run() {
			ServerSocket serverSocket = null;
			try {
				serverSocket = new ServerSocket();
				serverSocket.bind(new InetSocketAddress(InetAddress.getByName(address), port));
//				serverSocket.setSoTimeout(1000);
				while (run) {
					try {
						Socket client = serverSocket.accept();
						ClientWorker cw = new ClientWorker(client);
						threadPool.submit(cw);
					} catch (Exception e) {
						System.out.println(e.getMessage());
						break;
					}

				}
				serverSocket.close();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	protected class SessionCleaningThread extends Thread {
		private boolean run = true;

		@Override
		public void run() {
			while (run) {
				Set<String> keys = new HashSet<>(sessions.keySet());
				for (String s : keys) {
					SessionMapEntry entry = sessions.get(s);
					if (entry.validUntil < System.currentTimeMillis() / 1000) {
						sessions.remove(s);
					}
				}
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private class ClientWorker implements Runnable, IDispatcher {
		private static final String SCRIPT_EXTENSION = "smscr";
		private static final String WORKERS_PACKET = "hr.fer.zemris.java.webserver.workers.";
		private Socket csocket;
		private InputStream istream;
		private OutputStream ostream;
		private String version;
		private String method;
		private String host;
		private Map<String, String> params = new HashMap<String, String>();
		private Map<String, String> tempParams = new HashMap<String, String>();
		private Map<String, String> permPrams = new HashMap<String, String>();
		private List<RCCookie> outputCookies = new ArrayList<RequestContext.RCCookie>();
		private String SID;
		private RequestContext rc = null;

		public ClientWorker(Socket csocket) {
			super();
			this.csocket = csocket;
		}

		@Override
		public void run() {
			try {
				istream = new BufferedInputStream(csocket.getInputStream());
				ostream = new BufferedOutputStream(csocket.getOutputStream());
				Optional<byte[]> request = readRequest(istream);
				if (request.isEmpty()) {
					return;
				}
				String requestStr = new String(request.get(), StandardCharsets.US_ASCII);
				List<String> headers = extractHeaders(requestStr);
				String[] firstLine = headers.isEmpty() ? null : headers.get(0).split(" ");
				if (firstLine == null || firstLine.length != 3) {
					sendEmptyResponse(ostream, 400, "Bad request");
					return;
				}

				String method = firstLine[0].toUpperCase().trim();
				if (!method.equals("GET")) {
					sendEmptyResponse(ostream, 405, "Method Not Allowed");
					return;
				}

				String version = firstLine[2].toUpperCase().trim();
				if (!(version.equals("HTTP/1.1") || version.equals("HTTP/1.0"))) {
					sendEmptyResponse(ostream, 505, "HTTP Version Not Supported");
					return;
				}

				findHostName(headers);

				checkSession(headers);

				String requestedPathString = firstLine[1];
				String path;
				if (requestedPathString.indexOf('?') >= 0) {
					String[] pathParamString = requestedPathString.split("\\?");
					path = pathParamString[0];
					String paramString = pathParamString[1];
					parseParameters(paramString);
				} else {
					path = requestedPathString;
				}

				internalDispatchRequest(path, true);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		private synchronized void checkSession(List<String> headers) {
			String sidCandidate = null, sid = null;
			for (String line : headers) {
				if (!line.startsWith("Cookie:"))
					continue;
				String[] cookies = line.substring(7).trim().split(";");
				sidCandidate = getSidCandidate(cookies);
			}
			SessionMapEntry entry = null;
			if (sidCandidate != null) {
				entry = foundCandidate(sidCandidate);
			}
			if (entry == null) {
				entry = registerClient();
			} else {
				entry.validUntil = System.currentTimeMillis() / 1000 + sessionTimeout;

			}
			permPrams = entry.map;
		}

		private SessionMapEntry registerClient() {
			SID = generateSID();
			SessionMapEntry entry = new SessionMapEntry(SID, host, System.currentTimeMillis() / 1000 + sessionTimeout,
					new ConcurrentHashMap<>());
			sessions.put(SID, entry);
			outputCookies.add(new RCCookie("sid", SID, null, host, "/"));
			return entry;
		}

		private SessionMapEntry foundCandidate(String sidCandidate) {
			SessionMapEntry entry = sessions.get(sidCandidate);
			if (entry == null)
				return null;
			if (!entry.host.equals(host))
				return null;
			if (entry.validUntil < System.currentTimeMillis() / 1000) {
				sessions.remove(sidCandidate);
				return null;
			}
			return entry;
		}

		private String generateSID() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 20; i++) {
				char c = (char) (sessionRandom.nextInt(26) + 'A');
				sb.append(c);
			}
			return sb.toString();
		}

		private String getSidCandidate(String[] cookies) {
			String sidCandidate = null;
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].trim().startsWith("sid")) {
					sidCandidate = cookies[i].split("=")[1];
					sidCandidate = sidCandidate.substring(1, sidCandidate.length() - 1);
					break;
				}
			}
			return sidCandidate;
		}

		private void parseParameters(String paramString) {
			String[] parameters = paramString.split("&");
			for (int i = 0; i < parameters.length; i++) {
				String[] pair = parameters[i].split("=");
				params.put(pair[0], pair[1]);
			}

		}

		private void findHostName(List<String> headers) {
			for (String line : headers) {
				if (!line.startsWith("Host:"))
					continue;
				host = line.substring(5).trim().split(":")[0];
				break;
			}
			if (host == null)
				host = domainName;
		}

		private Optional<byte[]> readRequest(InputStream is) throws IOException {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int state = 0;
			l: while (true) {
				int b = is.read();
				if (b == -1) {
					if (bos.size() != 0) {
						throw new IOException("Incomplete header received.");
					}
					return Optional.empty();
				}
				if (b != 13) {
					bos.write(b);
				}
				switch (state) {
				case 0:
					if (b == 13) {
						state = 1;
					} else if (b == 10)
						state = 4;
					break;
				case 1:
					if (b == 10) {
						state = 2;
					} else
						state = 0;
					break;
				case 2:
					if (b == 13) {
						state = 3;
					} else
						state = 0;
					break;
				case 3:
					if (b == 10) {
						break l;
					} else
						state = 0;
					break;
				case 4:
					if (b == 10) {
						break l;
					} else
						state = 0;
					break;
				}
			}
			return Optional.of(bos.toByteArray());
		}

		private List<String> extractHeaders(String requestHeader) {
			List<String> headers = new ArrayList<String>();
			String currentLine = null;
			for (String s : requestHeader.split("\n")) {
				if (s.isEmpty())
					break;
				char c = s.charAt(0);
				if (c == 9 || c == 32) {
					currentLine += s;
				} else {
					if (currentLine != null) {
						headers.add(currentLine);
					}
					currentLine = s;
				}
			}
			if (!currentLine.isEmpty()) {
				headers.add(currentLine);
			}
			return headers;
		}

		private void sendResponseWithData(OutputStream cos, int statusCode, String statusText, String contentType,
				byte[] data) throws IOException {
			ostream.write(data);
			ostream.flush();
		}

		private void sendEmptyResponse(OutputStream cos, int statusCode, String statusText) throws IOException {
			sendResponseWithData(cos, statusCode, statusText, "text/plain;charset=UTF-8", new byte[0]);
		}

		@Override
		public void dispatchRequest(String urlPath) throws Exception {
			internalDispatchRequest(urlPath, false);
		}

		private void internalDispatchRequest(String urlPath, boolean directCall) throws Exception {
			checkRequestContext();

			IWebWorker worker = workersMap.get(urlPath);
			if (worker != null) {
				worker.processRequest(rc);
				ostream.flush();
				csocket.close();
				return;
			}

			if (urlPath.startsWith("/ext/")) {
				String className = urlPath.substring("/ext/".length()).trim();
				IWebWorker iww = getWebWorker(WORKERS_PACKET + className);
				iww.processRequest(rc);
				ostream.flush();
				csocket.close();
				return;
			}

			if ((urlPath.equals("/private") || urlPath.startsWith("/private/")) && directCall) {
				sendEmptyResponse(ostream, 404, "Bad request");
				return;
			}

			Path requestedPath = documentRoot.resolve(urlPath.substring(1));
			if (documentRoot.startsWith(requestedPath)) {
				sendEmptyResponse(ostream, 403, "Forbidden");
			}

			if (!Files.exists(requestedPath) || !Files.isReadable(requestedPath)) {
				sendEmptyResponse(ostream, 404, "File not found");
				return;
			}

			String fileName = requestedPath.getFileName().toString();
			String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
			String type = mimeTypes.get(extension);
			String mimeType = type == null ? "application/octet-stream" : type;

			if (extension.equals(SCRIPT_EXTENSION)) {
				parseSmartScript(requestedPath, mimeType);
			} else {
				returnStaticContent(requestedPath, mimeType);
			}
		}

		private void returnStaticContent(Path requestedPath, String mimeType) throws IOException {
			checkRequestContext();
			rc.setMimeType(mimeType);
			rc.setStatusCode(200);
			rc.setContentLength(requestedPath.toFile().length());

			try (InputStream fis = Files.newInputStream(requestedPath)) {
				byte[] buf = new byte[1024];
				while (true) {
					int r = fis.read(buf);
					if (r < 1)
						break;
					rc.write(buf, 0, r);
				}

				ostream.flush();
				csocket.close();
			}

		}

		private void parseSmartScript(Path requestedPath, String mimeType) throws IOException {
			String docBody = new String(Files.readAllBytes(requestedPath), StandardCharsets.UTF_8);
			checkRequestContext();
			rc.setMimeType("text/html");

			SmartScriptEngine engine = new SmartScriptEngine(new SmartScriptParser(docBody).getDocumentNode(), rc);
			engine.execute();
			ostream.flush();
			csocket.close();
		}

		private void checkRequestContext() {
			if (rc == null)
				rc = new RequestContext(ostream, params, permPrams, outputCookies, this, tempParams, SID);
		}
	}

	private static class SessionMapEntry {
		String SID;
		String host;
		long validUntil;
		Map<String, String> map;

		public SessionMapEntry(String SID, String host, long validUntil, Map<String, String> map) {
			super();
			this.SID = SID;
			this.host = host;
			this.validUntil = validUntil;
			this.map = map;
		}
	}
}
