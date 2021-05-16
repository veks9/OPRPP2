package hr.fer.zemris.java.webapp.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hr.fer.zemris.java.webapp.dao.DAOProvider;
import hr.fer.zemris.java.webapp.model.Poll;

/**
 * Servlet koji generira homepage
 * @author vedran
 *
 */
@WebServlet("/servleti/index.html")
public class IndexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Poll> polls = DAOProvider.getDao().getPolls();
		req.setAttribute("polls", polls);
		
		req.getRequestDispatcher("/index.jsp").forward(req, resp);
	}
}
