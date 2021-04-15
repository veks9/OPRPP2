package hr.fer.zemris.webapp.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Klasa predstavlja servlet koji postavlja boju pozadine i sprema tu boju
 *  u sesijsku varijablu. 
 * @author vedran
 *
 */
@WebServlet("/setcolor")
public class SetColorServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String color = req.getParameter("color");
		if(color != null)
			req.getSession().setAttribute("pickedBgCol", color);

		req.getRequestDispatcher("/WEB-INF/pages/colors.jsp").forward(req, resp);
	}
}
