package hr.fer.zemris.java.webapp.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hr.fer.zemris.java.webapp.dao.DAOProvider;

/**
 * Klasa predstavlja servlet koji dohvaća podatke iz baze i 
 * ažurira ih. Kada je gotov s tim preusmjeruje na glasanje-rezultati
 * @author vedran
 *
 */
@WebServlet("/servleti/glasanje-glasaj")
public class GlasanjeGlasajServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Object semaphore = new Object();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		long pollOptionID = Long.parseLong(req.getParameter("pollOptionID"));
		long pollID = (long) req.getSession().getAttribute("pollID");
		synchronized (semaphore) {
			DAOProvider.getDao().incrementVote(pollID, pollOptionID);
		}
		
		resp.sendRedirect(req.getContextPath() + "/servleti/glasanje-rezultati");
	}
}
