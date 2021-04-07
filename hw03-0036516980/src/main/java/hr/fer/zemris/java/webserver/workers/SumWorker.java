package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

public class SumWorker implements IWebWorker {
	private final static String EVEN_PHOTO = "../../images/rick.gif";
	private final static String ODD_PHOTO = "../../images/morty.gif";
	@Override
	public void processRequest(RequestContext context) throws Exception {
		int a = getParameter("a", context);
		int b = getParameter("b", context);
		int sum = a + b;
		context.setTemporaryParameter("varA", Integer.toString(a));
		context.setTemporaryParameter("varB", Integer.toString(b));
		context.setTemporaryParameter("zbroj", Integer.toString(sum));
		String imgName = sum % 2 == 0 ? EVEN_PHOTO : ODD_PHOTO;
		context.setTemporaryParameter("imgName", imgName);
		context.getDispatcher().dispatchRequest("/private/pages/calc.smscr");
	}

	private int getParameter(String variable, RequestContext context) {
		String p = context.getParameter(variable);
		return p == null ? 1 : Integer.parseInt(p);
	}
}
