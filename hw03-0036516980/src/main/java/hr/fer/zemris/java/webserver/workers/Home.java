package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Klasa predstavlja workera koji postavlja boju pozadine
 * 
 * @author vedran
 *
 */
public class Home implements IWebWorker {
	private final static String DEFAULT_BACKGROUND_COLOR = "7F7F7F";

	@Override
	public void processRequest(RequestContext context) throws Exception {
		String bgColor = context.getPersistentParameter("bgcolor");
		context.setTemporaryParameter("background", bgColor == null ? DEFAULT_BACKGROUND_COLOR : bgColor);

		context.getDispatcher().dispatchRequest("/private/pages/home.smscr");
	}
}
