package hr.fer.zemris.webapp.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hr.fer.zemris.webapp.util.Band;
import hr.fer.zemris.webapp.util.Result;
import hr.fer.zemris.webapp.util.Util;

/**
 * Klasa predstavlja servlet koji dohvaća svježe podatke iz "baze podataka":
 * Te podatke sprema u sesijsku varijablu i traži pobjednike koje također sprema u
 * sesijsku varijablu.
 *  
 * @author vedran
 *
 */
@WebServlet("/glasanje-rezultati")
public class GlasanjeRezultatiServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String fileName = req.getServletContext().getRealPath("/WEB-INF/glasanje-rezultati.txt");
		@SuppressWarnings("unchecked")
		Map<Integer, Band> bands = (Map<Integer, Band>) req.getSession().getAttribute("bands");
		if (bands == null) {
			bands = Util.getBands(req);
		}
		if (Files.exists(Paths.get(fileName))) {
			try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
				while (true) {
					String l = br.readLine();
					if (l == null)
						break;
					String[] arr = l.split("\t");
					Band b = bands.get(Integer.parseInt(arr[0]));
					b.setNumberOfVotes(Integer.parseInt(arr[1]));
				}
			}
		} else {
			new File(fileName).createNewFile();
		}

		List<Result> result = new ArrayList<>();
		for (Band b : bands.values())
			result.add(new Result(b.getBandName(), b.getNumberOfVotes()));
		result.sort((r1, r2) -> r2.getNumberOfVotes() - r1.getNumberOfVotes());
		req.getSession().setAttribute("result", result);
		req.getSession().setAttribute("winners", getWinners(bands));

		req.getRequestDispatcher("/WEB-INF/pages/glasanjeRez.jsp").forward(req, resp);
	}

	/**
	 * Pomoćna metoda koja vraća listu pobjednika u glasanju
	 * @param bands mapa bendova(baza podataka)
	 * @return lista pobjednika
	 */
	private List<Band> getWinners(Map<Integer, Band> bands) {
		List<Band> winners = new ArrayList<>();
		int maxVotes = 0;

		for (Band b : bands.values()) {
			if (b.getNumberOfVotes() == maxVotes) {
				winners.add(b);
			}

			if (b.getNumberOfVotes() > maxVotes) {
				maxVotes = b.getNumberOfVotes();
				winners = new ArrayList<>();
				winners.add(b);
			}
		}

		return winners;
	}
}