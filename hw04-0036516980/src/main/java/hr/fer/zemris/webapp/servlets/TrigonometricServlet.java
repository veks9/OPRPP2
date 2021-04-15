package hr.fer.zemris.webapp.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Klasa predstavlja servlet koji računa sinus i kosinus te rezultat pohranjuje
 * kao sesijsku varijablu
 * @author vedran
 *
 */
@WebServlet("/trigonometric")
public class TrigonometricServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private int a;
	private int b;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String aString = req.getParameter("a");
		String bString = req.getParameter("b");

		checkParams(aString, bString);

		List<TrigonometricEntry> list = new ArrayList<>();
		for (int i = a; i < b; i++) {
			list.add(new TrigonometricEntry(i));
		}

		req.getSession().setAttribute("list", list);
		req.getRequestDispatcher("/WEB-INF/pages/trigonometric.jsp").forward(req, resp);
	}

	/**
	 * Pomoćna metoda koja ispituje jesu li parametri validni
	 * @param aString
	 * @param bString
	 */
	private void checkParams(String aString, String bString) {
		a = aString == null ? 0 : Integer.parseInt(aString);
		b = bString == null ? 360 : Integer.parseInt(bString);

		if (a > b)
			swap();
		if (b > a + 720)
			b = a + 720;
	}

	/**
	 * Pomoćna metoda koja mijenja vrijednosti od klasnih varijabli a i b
	 */
	private void swap() {
		int temp = a;
		a = b;
		b = temp;
	}

	/**
	 * Pomoćna klasa koja modelira rezultat koji se treba ispisati na stranici.
	 * Sastoji se od broja, njegovog sinusa i kosinusa
	 * @author vedran
	 *
	 */
	public static class TrigonometricEntry {
		public int number;
		public double sin;
		public double cos;

		public TrigonometricEntry(int number) {
			super();
			this.number = number;
			this.sin = Math.sin(Math.toRadians(number));
			this.cos = Math.cos(Math.toRadians(number));;
		}

		/**
		 * Getter za broj
		 * @return broj
		 */
		public int getNumber() {
			return number;
		}

		/**
		 * Getter za sinus
		 * @return sinus
		 */
		public double getSin() {
			return sin;
		}

		/**
		 * Getter za kosinus
		 * @return kosinus
		 */
		public double getCos() {
			return cos;
		}

		@Override
		public String toString() {
			return "TrigonometricEntry [number=" + number + ", sin=" + sin + ", cos=" + cos + "]";
		}
		
	}
}
