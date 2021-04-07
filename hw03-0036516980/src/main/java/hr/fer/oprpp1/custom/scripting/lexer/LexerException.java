package hr.fer.oprpp1.custom.scripting.lexer;

/**
 * Predstavlja iznimku koja se dogodi prilikom korištenja lexera
 * 
 * @author vedran
 *
 */
public class LexerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LexerException() {
		// TODO Auto-generated constructor stub
	}

	public LexerException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public LexerException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public LexerException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public LexerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
