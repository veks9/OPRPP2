package hr.fer.zemris.java.webapp.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import hr.fer.zemris.java.webapp.dao.DAOProvider;
import hr.fer.zemris.java.webapp.model.PollOption;

/**
 * Klasa predstavlja servlet koji dohvaća svježe podatke iz "baze podataka":
 * Te podatke sprema u sjedničku varijablu i traži pobjednike koje također sprema u
 * sjedničku varijablu.
 *  
 * @author vedran
 *
 */
@WebServlet("/servleti/glasanje-rezultati")
public class GlasanjeRezultatiServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		long pollID = (long) session.getAttribute("pollID");
		List<PollOption> result  = DAOProvider.getDao().getPollOptionsForPollID(pollID);

		req.getSession().setAttribute("result", result);
		req.getSession().setAttribute("winners", getWinners(result));

		req.getRequestDispatcher("/WEB-INF/pages/glasanjeRez.jsp").forward(req, resp);
	}

	/**
	 * Pomoćna metoda koja vraća listu pobjednika u glasanju
	 * @param pollOptions lista stavki ankete
	 * @return lista pobjednika
	 */
	private List<PollOption> getWinners(List<PollOption> pollOptions) {
		List<PollOption> winners = new ArrayList<>();
		long maxVotes = 0;

		for (PollOption p : pollOptions) {
			if (p.getNumberOfVotes() == maxVotes) {
				winners.add(p);
			}

			if (p.getNumberOfVotes() > maxVotes) {
				maxVotes = p.getNumberOfVotes();
				winners = new ArrayList<>();
				winners.add(p);
			}
		}

		return winners;
	}
}