package hr.fer.oprpp1.custom.collections;

/**
 * Predstavlja iznimku koja se baca kada je prazan stog i pokuša se skinuti sa
 * stoga
 * 
 * @author vedran
 *
 */
public class EmptyStackException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public EmptyStackException() {
		super();
	}

	public EmptyStackException(String s) {
		super(s);
	}

	public EmptyStackException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EmptyStackException(String message, Throwable cause) {
		super(message, cause);
	}

	public EmptyStackException(Throwable cause) {
		super(cause);
	}
}
