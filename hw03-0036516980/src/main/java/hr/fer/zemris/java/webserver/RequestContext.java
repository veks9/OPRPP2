package hr.fer.zemris.java.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Klasa predstavlja kontekst koji upravlja cookijima i parametrima.
 * Također, zapisuje podatke na outputstream.
 * @author vedran
 *
 */
public class RequestContext {
	private String DEFAULT_ENCODING = "UTF-8";
	private int DEFAULT_STATUS_CODE = 200;
	private String DEFAULT_STATUS_TEXT = "OK";
	private String DEFAULT_MIME_TYPE = "text/html";
	private static final Charset DEFAULT_HEADER_CHARSET = StandardCharsets.ISO_8859_1;
	private OutputStream outputStream;
	private Charset charset = Charset.forName(DEFAULT_ENCODING);
	private String encoding = DEFAULT_ENCODING;
	private int statusCode = DEFAULT_STATUS_CODE;
	private String statusText = DEFAULT_STATUS_TEXT;
	private String mimeType = DEFAULT_MIME_TYPE;
	private Long contentLength = null;
	private Map<String, String> parameters;
	private Map<String, String> temporaryParameters = new HashMap<>();
	private Map<String, String> persistentParameters;
	private List<RCCookie> outputCookies;
	private boolean headerGenerated = false;
	private IDispatcher dispatcher;
	private String SID;

	public RequestContext(OutputStream outputStream, Map<String, String> parameters,
			Map<String, String> persistentParameters, List<RCCookie> outputCookies) {
		this(outputStream, parameters, persistentParameters, outputCookies, null, null, null);
	}

	public RequestContext(OutputStream outputStream,  Map<String,String> parameters, 
			Map<String,String> persistentParameters, List<RCCookie> outputCookies, 
			IDispatcher dispatcher, Map<String, String> temporaryParameters, String SID) {
		
		this.outputStream = Objects.requireNonNull(outputStream);
		this.parameters = parameters == null ? new HashMap<>() : parameters;
		this.persistentParameters = persistentParameters == null ?
				new HashMap<>() : persistentParameters;
		this.outputCookies = outputCookies == null ? 
				new ArrayList<>() : outputCookies;
		this.dispatcher = dispatcher;
		this.temporaryParameters = temporaryParameters == null ? 
				new HashMap<>() : temporaryParameters;
		this.SID = SID;
	}

	/**
	 * Setter za kodnu stranicu. Baca {@link RuntimeException} ako je header već generiran
	 * @param encoding
	 */
	public void setEncoding(String encoding) {
		isGeneratedHeader();
		this.encoding = Objects.requireNonNull(encoding, "Encoding can't be null!");
		this.charset = Charset.forName(encoding);
	}

	/**
	 * Setter za statusni kod. Baca {@link RuntimeException} ako je header već generiran
	 * @param statusCode
	 */
	public void setStatusCode(int statusCode) {
		isGeneratedHeader();
		this.statusCode = statusCode;
	}

	/**
	 * Setter za status tekst. Baca {@link RuntimeException} ako je header već generiran
	 * @param statusText
	 */
	public void setStatusText(String statusText) {
		isGeneratedHeader();
		this.statusText = statusText;
	}

	/**
	 * Setter za mime type. Baca {@link RuntimeException} ako je header već generiran
	 * @param mimeType
	 */
	public void setMimeType(String mimeType) {
		isGeneratedHeader();
		this.mimeType = mimeType;
	}

	/**
	 * Setter za duljinu sadržaja. Baca {@link RuntimeException} ako je header već generiran
	 * @param contentLength
	 */
	public void setContentLength(Long contentLength) {
		isGeneratedHeader();
		this.contentLength = contentLength;
	}

	/**
	 * Metoda dohvaća vrijednost parametra pod imenom name
	 * @param name
	 * @return vrijednost parametra pod imenom name
	 */
	public String getParameter(String name) {
		return parameters.get(name);
	}

	/**
	 * Metoda dohvaća nazive parametara i vraća ih kao unmodifiable Set
	 * @return nazivi parametara i vraća ih kao unmodifiable Set
	 */
	public Set<String> getParameterNames() {
		return Collections.unmodifiableSet(new HashSet<>(parameters.keySet()));
	}

	/**
	 * Metoda dohvaća vrijednost parametra pod imenom name
	 * @param name
	 * @return vrijednost parametra pod imenom name
	 */
	public String getPersistentParameter(String name) {
		return persistentParameters.get(name);
	}

	/**
	 * Metoda dohvaća nazive parametara i vraća ih kao unmodifiable Set
	 * @return nazivi parametara i vraća ih kao unmodifiable Set
	 */
	public Set<String> getPersistentParameterNames() {
		return Collections.unmodifiableSet(new HashSet<>(persistentParameters.values()));
	}

	/**
	 * Metoda dodaje u mapu vrijednost value pod ključem name
	 * @param name ključ
	 * @param value vrijednost
	 */
	public void setPersistentParameter(String name, String value) {
		persistentParameters.put(name, value);
	}

	/**
	 * Metoda miče ključ name i vrijednost pod tim ključem iz mape
	 * @param name
	 */
	public void removePersistentParameter(String name) {
		persistentParameters.remove(name);
	}

	/**
	 * Metoda dohvaća vrijednost parametra pod imenom name
	 * @param name
	 * @return vrijednost parametra pod imenom name
	 */
	public String getTemporaryParameter(String name) {
		return temporaryParameters.get(name);
	}

	/**
	 * Metoda dohvaća nazive parametara i vraća ih kao unmodifiable Set
	 * @return nazivi parametara i vraća ih kao unmodifiable Set
	 */
	public Set<String> getTemporaryParameterNames() {
		return Collections.unmodifiableSet(new HashSet<>(temporaryParameters.values()));
	}

	/**
	 * Metoda vraća id sesije
	 * @return
	 */
	public String getSessionID() {
		return SID;
	}

	/**
	 * Metoda dodaje u mapu vrijednost value pod ključem name
	 * @param name ključ
	 * @param value vrijednost
	 */
	public void setTemporaryParameter(String name, String value) {
		temporaryParameters.put(name, value);
	}

	/**
	 * Metoda miče ključ name i vrijednost pod tim ključem iz mape
	 * @param name
	 */
	public void removeTemporaryParameter(String name) {
		temporaryParameters.remove(name);
	}

	/**
	 * Metoda prima polje bajtova i piše ga u outputstream
	 * @param data polje bajtova
	 * @return this
	 * @throws IOException
	 */
	public RequestContext write(byte[] data) throws IOException {
		return write(data, 0, data.length);
	}

	/**
	 * Metoda prima polje bajtova, pomak i duljinu i piše ga u outputstream. Prije nego što
	 * se upišu podaci, ako nije jos generiran header, on se generira
	 * @param data polje bajtova
	 * @return this
	 * @throws IOException
	 */
	public RequestContext write(byte[] data, int offset, int length) throws IOException {
		if (!headerGenerated) {
			generateHeader();
			headerGenerated = true;
		}
		outputStream.write(data, offset, length);
		outputStream.flush();
		return this;
	}

	/**
	 * Metoda prima string i piše ga u outputstream
	 * @param data polje bajtova
	 * @return this
	 * @throws IOException
	 */
	public RequestContext write(String text) throws IOException {
		byte[] data = text.getBytes(charset);
		return write(data);
	}

	/**
	 * Metoda dodaje cookie u internu listu cookiea
	 * @param cookie
	 */
	public void addRCCookie(RCCookie cookie) {
		isGeneratedHeader();
		outputCookies.add(cookie);
	}

	/**
	 * Pomoćna metoda koja generira header
	 * @throws IOException
	 */
	private void generateHeader() throws IOException {
		charset = Charset.forName(encoding);
		StringBuilder sb = new StringBuilder();
		sb.append("HTTP/1.1 " + statusCode + " " + statusText + "\r\n");
		sb.append("Content-Type: " + mimeType);
		if (mimeType.startsWith("text/")) {
			sb.append("; charset=" + encoding);
		}
		sb.append("\r\n");
		sb.append(contentLength == null ? "" : String.valueOf(contentLength) + "\r\n");
		appendCookies(sb);
		sb.append("\r\n");

		String header = sb.toString();
		byte[] bytes = header.getBytes(DEFAULT_HEADER_CHARSET);
		outputStream.write(bytes, 0, bytes.length);
	}

	/**
	 * Pomoćna metoda koja dodaje cookieje u header
	 * @param sb
	 */
	private void appendCookies(StringBuilder sb) {
		for (RCCookie c : outputCookies) {
			sb.append("Set-Cookie: " + c.getName() + "=\"" + c.getValue() + "\"");
			if (c.getDomain() != null)
				sb.append("; Domain=" + c.getDomain());
			if (c.getPath() != null)
				sb.append("; Path=" + c.getPath());
			if (c.getMaxAge() != null)
				sb.append("; Max-Age=" + c.getMaxAge());
			if(c.isHttpOnly()) sb.append("; HttpOnly");

			sb.append("\r\n");
		}
	}

	/**
	 * Metoda baca {@link RuntimeException} ako je header već generiran
	 */
	private void isGeneratedHeader() {
		if (headerGenerated)
			throw new RuntimeException("Header is already generated!");
	}

	/**
	 * Getter za dispatchera
	 * @return dispatcher
	 */
	public IDispatcher getDispatcher() {
		return dispatcher;
	}


	/**
	 * Klasa predstavlja implementaciju cookiea
	 * @author vedran
	 *
	 */
	public static class RCCookie {
		private String name;
		private String value;
		private String domain;
		private String path;
		private Integer maxAge;
		private boolean httpOnly;
		
		public RCCookie(String name, String value, Integer maxAge, String domain, String path, boolean httpOnly) {
			super();
			this.name = Objects.requireNonNull(name, "Name can't be null!");
			this.value = Objects.requireNonNull(value, "Value can't be null!");
			this.domain = domain;
			this.path = path;
			this.maxAge = maxAge;
			this.httpOnly = httpOnly;
		}
		
		public RCCookie(String name, String value, Integer maxAge, String domain, String path) {
			super();
			this.name = Objects.requireNonNull(name, "Name can't be null!");
			this.value = Objects.requireNonNull(value, "Value can't be null!");
			this.domain = domain;
			this.path = path;
			this.maxAge = maxAge;
		}

		/**
		 * Getter za naziv cookiea
		 * @return naziv cookiea
		 */
		public String getName() {
			return name;
		}

		/**
		 * Getter za vrijednost cookiea
		 * @return vrijednost cookiea
		 */
		public String getValue() {
			return value;
		}

		/**
		 * Getter za domenu cookiea
		 * @return domena cookiea
		 */
		public String getDomain() {
			return domain;
		}

		/**
		 * Getter za path cookiea
		 * @return path cookiea
		 */
		public String getPath() {
			return path;
		}

		/**
		 * Getter za trajanje cookiea
		 * @return trajanje cookiea
		 */
		public Integer getMaxAge() {
			return maxAge;
		}

		/**
		 * Getter za zastavicu httpOnly
		 * @return <code>true</code> ako je, inače <code>false</code>
		 */
		public boolean isHttpOnly() {
			return httpOnly;
		}
	}
}
