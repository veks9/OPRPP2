package hr.fer.zemris.java.webapp.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Klasa predstavlja servlet koji preusmjerava na homepage ako se pošalje
 * url sa / ili /index.html
 * @author vedran
 *
 */
@WebServlet(urlPatterns = { "/index.html" , "/" })
public class HomePageServlet extends HttpServlet {

	/**The default serial version UID*/
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		resp.sendRedirect("servleti/index.html");
	}
}
