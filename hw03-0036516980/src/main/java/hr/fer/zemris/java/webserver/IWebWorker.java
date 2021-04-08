package hr.fer.zemris.java.webserver;

/**
 * Sučelje koje modelira workere koji služe za procesiranje zahtjeva za klijenta
 * @author vedran
 *
 */
public interface IWebWorker {
	/**
	 * Metoda služi za procesiranje zahtjeva
	 * @param context kontekst
	 * @throws Exception
	 */
	public void processRequest(RequestContext context) throws Exception;
}