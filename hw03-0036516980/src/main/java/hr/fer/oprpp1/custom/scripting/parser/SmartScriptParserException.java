package hr.fer.oprpp1.custom.scripting.parser;

/**
 * Predstavlja iznimku koja se dogodi kad se u parseru dogodi greška
 * 
 * @author vedran
 *
 */
public class SmartScriptParserException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SmartScriptParserException() {
		// TODO Auto-generated constructor stub
	}

	public SmartScriptParserException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public SmartScriptParserException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public SmartScriptParserException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public SmartScriptParserException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}