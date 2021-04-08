package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Klasa predstavlja workera koji ažurira pozadinu
 * @author vedran
 *
 */
public class BgColorWorker implements IWebWorker {

	@Override
	public void processRequest(RequestContext context) throws Exception {
		String bgcolor = context.getParameter("bgcolor");
		if (bgcolor.trim().length() == 6 && isHexNumber(bgcolor)) {
			context.setPersistentParameter("bgcolor", bgcolor);
			context.setTemporaryParameter("colorChanged", "The color was updated.");
		} else {
			context.setTemporaryParameter("colorChanged", "The color was not updated.");
		}
		context.getDispatcher().dispatchRequest("/private/pages/colorChanged.smscr");
	}

	/**
	 * Pomoćna metoda koja ispituje je li broj u hexadekadskom zapisu duljine 6
	 * @param bgcolor
	 * @return
	 */
	private boolean isHexNumber(String bgcolor) {
		char[] arr = bgcolor.toCharArray();
		if (arr.length != 6)
			return false;
		for (char c : arr) {
			if (!((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f'))) {
				return false;
			}
		}
		return true;
	}
}
