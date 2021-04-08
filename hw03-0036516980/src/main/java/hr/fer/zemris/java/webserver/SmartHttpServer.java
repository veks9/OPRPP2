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

/**
 * Klasa predstavlja poslužitelj koji obrađuje zahtjeve klijenta
 * @author vedran
 *
 */
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

	/**
	 * Pomoćna metoda koja inicijalizira varijable iz config datoteke
	 * @param p
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
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

	/**
	 * Pomoćna metoda koja čita workere iz config datoteke
	 * @param p
	 * @throws IOException
	 */
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

	/**
	 * Pomoćna metoda koja vraća workera ako posotji
	 * @param fqcn
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private IWebWorker getWebWorker(String fqcn)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> referenceToClass = this.getClass().getClassLoader().loadClass(fqcn);
		@SuppressWarnings("deprecation")
		Object newObject = referenceToClass.newInstance();
		IWebWorker iww = (IWebWorker) newObject;
		return iww;
	}

	/**
	 * Pomoćna metoda koja čita mime types iz config datoteke
	 * @param p properties
	 * @throws IOException
	 */
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

	/**
	 * Metoda pokreće rad poslužitelja ako već nije pokrenut i  dretve za čišćenje
	 */
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

	/**
	 * Metoda zaustavlja rad poslužitelja
	 */
	protected synchronized void stop() {
		serverThread.run = false;
		sessionCleaningThread.run = false;
		threadPool.shutdown();
	}

	/**
	 * Klasa predstavlja serversku dretvu koja radi i pokreće klijentske dretve 
	 * @author vedran
	 *
	 */
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

	/**
	 * Klasa predstavlja dretvu koja čisti zastarjele sesije
	 * @author vedran
	 *
	 */
	private class SessionCleaningThread extends Thread {
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

	/**
	 * Klasa predstavlja radnika za klijenta
	 * @author vedran
	 *
	 */
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

		/**
		 * Metoda iz headersa iz cookiesa saznaje je li klijent već bio spojen,
		 * ako je onda vrati njegovu sesiju. Ako nije bio spojen, registrira korisnika i 
		 * napravi novi cookie
		 * @param headers
		 */
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

		/**
		 * Pomoćna metoda koja registrira klijenta
		 * @return
		 */
		private SessionMapEntry registerClient() {
			SID = generateSID();
			SessionMapEntry entry = new SessionMapEntry(SID, host, System.currentTimeMillis() / 1000 + sessionTimeout,
					new ConcurrentHashMap<>());
			sessions.put(SID, entry);
			outputCookies.add(new RCCookie("sid", SID, null, host, "/", true));
			return entry;
		}

		/**
		 * Pomoćna metoda koja se zove kad se pronađe kandidat za sid te se ispituje
		 * je li valjan. Vraća se entry ako je valjan, inače null
		 * @param sidCandidate
		 * @return
		 */
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

		/**
		 * Pomoćna metoda koja generira SID. On se sastoji od 20 velikih slova
		 * @return sid
		 */
		private String generateSID() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 20; i++) {
				char c = (char) (sessionRandom.nextInt(26) + 'A');
				sb.append(c);
			}
			return sb.toString();
		}

		/**
		 * Pomoćna metoda koja u cookiesima traži i vraća vrijednost od "sid=vrijednost", null ako se ne nađe
		 * @param cookies
		 * @return
		 */
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

		/**
		 * Pomoćna metoda koja parsira parametre predane u url-u
		 * @param paramString
		 */
		private void parseParameters(String paramString) {
			String[] parameters = paramString.split("&");
			for (int i = 0; i < parameters.length; i++) {
				String[] pair = parameters[i].split("=");
				params.put(pair[0], pair[1]);
			}

		}

		/**
		 * Pomoćna metoda koja u headersima nađe Host: i spremi vrijednost hosta
		 * @param headers
		 */
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

		/**
		 * Pomoćna metoda koja iz {@link InputStream}a is čita bajtove
		 * @param is
		 * @return
		 * @throws IOException
		 */
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

		/**
		 * Pomoćna metoda koja primi headerse u obliku stringa i splita ih i vrati listu
		 * @param requestHeader headeri
		 * @return
		 */
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

		/**
		 * Pomoćna metoda koja šalje odgovor kad dođe do greške
		 * @param cos outputstream
		 * @param statusCode statusni kod
		 * @param statusText statusni tekst
		 * @throws IOException
		 */
		private void sendEmptyResponse(OutputStream cos, int statusCode, String statusText) throws IOException {
			cos.write((version + " " + statusCode+" "+statusText+"\r\n"+
					"Server: simple java server\r\n"+
					"Content-Type: text/plain;charset=UTF-8\r\n"+
					"Content-Length: 0\r\n"+
					"Connection: close\r\n"+
					"\r\n").getBytes(StandardCharsets.US_ASCII));
			cos.flush();
		}

		@Override
		public void dispatchRequest(String urlPath) throws Exception {
			internalDispatchRequest(urlPath, false);
		}

		/**
		 * Pomoćna metoda koja preusmjerava na predani urlPath. DirectCall zastavica 
		 * služi za provvjeravanje je li netko izvana pozvao nešto što se ne smije
		 * @param urlPath gdje se mora ići
		 * @param directCall zastavica 
		 * @throws Exception
		 */
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
				return;
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

		/**
		 * Pomoćna metoda koja vraća statični sadržaj tj. sve što nije .smscr
		 * @param requestedPath
		 * @param mimeType
		 * @throws IOException
		 */
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

		/**
		 * Pomoćna metoda koja parsira i izvršava .smscr datoteke
		 * @param requestedPath
		 * @param mimeType
		 * @throws IOException
		 */
		private void parseSmartScript(Path requestedPath, String mimeType) throws IOException {
			String docBody = new String(Files.readAllBytes(requestedPath), StandardCharsets.UTF_8);
			checkRequestContext();
			rc.setMimeType("text/html");

			SmartScriptEngine engine = new SmartScriptEngine(new SmartScriptParser(docBody).getDocumentNode(), rc);
			engine.execute();
			ostream.flush();
			csocket.close();
		}

		/**
		 * Pomoćna metoda koja stvara context ako je prazan
		 */
		private void checkRequestContext() {
			if (rc == null)
				rc = new RequestContext(ostream, params, permPrams, outputCookies, this, tempParams, SID);
		}
	}

	/**
	 * Klasa predstavlja entry mape sesija
	 * @author vedran
	 *
	 */
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
