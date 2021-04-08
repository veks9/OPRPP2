package hr.fer.zemris.java.webserver;

/**
 * Sučelje modelira dispatchera koji služi za interno preusmjeravanje
 * @author vedran
 *
 */
public interface IDispatcher {
	/**
	 * Metoda koja preusmjerava na urlPath
	 * @param urlPath url na koji se želimo preusmjeriti
	 * @throws Exception
	 */
	void dispatchRequest(String urlPath) throws Exception;
}