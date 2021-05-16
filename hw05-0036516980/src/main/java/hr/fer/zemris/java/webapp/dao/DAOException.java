package hr.fer.zemris.java.webapp.dao;

/**
 * Klasa modelira iznimku koja se baca ako dođe do greške u DAO
 * @author vedran
 *
 */
public class DAOException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DAOException() {
	}

	public DAOException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DAOException(String message, Throwable cause) {
		super(message, cause);
	}

	public DAOException(String message) {
		super(message);
	}

	public DAOException(Throwable cause) {
		super(cause);
	}
}