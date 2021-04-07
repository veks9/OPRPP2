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

	public void setEncoding(String encoding) {
		isGeneratedHeader();
		this.encoding = Objects.requireNonNull(encoding, "Encoding can't be null!");
		this.charset = Charset.forName(encoding);
	}

	public void setStatusCode(int statusCode) {
		isGeneratedHeader();
		this.statusCode = statusCode;
	}

	public void setStatusText(String statusText) {
		isGeneratedHeader();
		this.statusText = statusText;
	}

	public void setMimeType(String mimeType) {
		isGeneratedHeader();
		this.mimeType = mimeType;
	}

	public void setContentLength(Long contentLength) {
		isGeneratedHeader();
		this.contentLength = contentLength;
	}

	public String getParameter(String name) {
		return parameters.get(name);
	}

	public Set<String> getParameterNames() {
		return Collections.unmodifiableSet(new HashSet<>(parameters.keySet()));
	}

	public String getPersistentParameter(String name) {
		return persistentParameters.get(name);
	}

	public Set<String> getPersistentParameterNames() {
		return Collections.unmodifiableSet(new HashSet<>(persistentParameters.values()));
	}

	public void setPersistentParameter(String name, String value) {
		persistentParameters.put(name, value);
	}

	public void removePersistentParameter(String name) {
		persistentParameters.remove(name);
	}

	public String getTemporaryParameter(String name) {
		return temporaryParameters.get(name);
	}

	public Set<String> getTemporaryParameterNames() {
		return Collections.unmodifiableSet(new HashSet<>(temporaryParameters.values()));
	}

	public String getSessionID() {
		return "";
	}

	public void setTemporaryParameter(String name, String value) {
		temporaryParameters.put(name, value);
	}

	public void removeTemporaryParameter(String name) {
		temporaryParameters.remove(name);
	}

	public RequestContext write(byte[] data) throws IOException {
		return write(data, 0, data.length);
	}

	public RequestContext write(byte[] data, int offset, int length) throws IOException {
		if (!headerGenerated) {
			generateHeader();
			headerGenerated = true;
		}
		outputStream.write(data, offset, length);
		outputStream.flush();
		return this;
	}

	public RequestContext write(String text) throws IOException {
		byte[] data = text.getBytes(charset);
		return write(data);
	}

	public void addRCCookie(RCCookie cookie) {
		isGeneratedHeader();
		outputCookies.add(cookie);
	}

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

	private void appendCookies(StringBuilder sb) {
		for (RCCookie c : outputCookies) {
			sb.append("Set-Cookie: " + c.getName() + "=\"" + c.getValue() + "\"");
			if (c.getDomain() != null)
				sb.append("; Domain=" + c.getDomain());
			if (c.getPath() != null)
				sb.append("; Path=" + c.getPath());
			if (c.getMaxAge() != null)
				sb.append("; Max-Age=" + c.getMaxAge());
			sb.append("\r\n");
		}
	}

	private void isGeneratedHeader() {
		if (headerGenerated)
			throw new RuntimeException("Header is already generated!");
	}

	public IDispatcher getDispatcher() {
		return dispatcher;
	}

	public String getSID() {
		return SID;
	}

	public static class RCCookie {
		private String name;
		private String value;
		private String domain;
		private String path;
		private Integer maxAge;

		public RCCookie(String name, String value, Integer maxAge, String domain, String path) {
			super();
			this.name = Objects.requireNonNull(name, "Name can't be null!");
			this.value = Objects.requireNonNull(value, "Value can't be null!");
			this.domain = domain;
			this.path = path;
			this.maxAge = maxAge;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

		public String getDomain() {
			return domain;
		}

		public String getPath() {
			return path;
		}

		public Integer getMaxAge() {
			return maxAge;
		}
	}
}
