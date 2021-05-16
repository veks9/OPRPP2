package hr.fer.zemris.java.webapp.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import hr.fer.zemris.java.webapp.dao.DAO;
import hr.fer.zemris.java.webapp.dao.DAOProvider;
import hr.fer.zemris.java.webapp.model.PollOption;

/**
 * Klasa predstavlja servlet koji dohvaća opcije za koje se može glasati i postavlja
 * sjedničke varijable
 * @author vedran
 *
 */
@WebServlet("/servleti/glasanje")
public class GlasanjeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		long pollID = Long.parseLong(req.getParameter("pollID"));
		DAO dao = DAOProvider.getDao();
		String pollTitle = dao.getPollTitleForPollID(pollID).trim();
		String[] pollSubjectArr = pollTitle.split(" ");
		String pollSubject = pollSubjectArr[pollSubjectArr.length - 1];
		pollSubject = pollSubject.substring(0, 1).toUpperCase() + pollSubject.substring(1);
		String pollMessage = dao.getPollMessageForPollID(pollID);
		List<PollOption> pollOptions = dao.getPollOptionsForPollID(pollID);

		HttpSession session = req.getSession();
		session.setAttribute("pollID", pollID);
		session.setAttribute("pollTitle", pollTitle);
		session.setAttribute("pollSubject", pollSubject);
		session.setAttribute("pollMessage", pollMessage);
		session.setAttribute("pollOptions", pollOptions);
		
		req.getRequestDispatcher("/WEB-INF/pages/glasanjeIndex.jsp").forward(req, resp);
	}
}
