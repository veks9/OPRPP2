package hr.fer.zemris.java.webserver.workers;

import java.util.Set;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Klasa predstavlja workera koji ispisuje tablicu sa parametrima iz contexta
 * @author vedran
 *
 */
public class EchoParams implements IWebWorker {

	@Override
	public void processRequest(RequestContext context) throws Exception {
		context.setMimeType("text/html");
		
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body>");
		sb.append("<table>");

		Set<String> keys = context.getParameterNames();
		for(String s : keys) {
			String param = context.getParameter(s);
			sb.append("\t<tr>\r\n" + "\t\t<th>"+ s +"</th>\r\n" + "\t\t<th>" + param + "</th>\r\n" + "\t</tr>\r\n"
			);
		}
		sb.append("</table>");
		sb.append("</body></html>");
		
		context.write(sb.toString());
	}
}
