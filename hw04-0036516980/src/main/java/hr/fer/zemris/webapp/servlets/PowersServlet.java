package hr.fer.zemris.webapp.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Servlet predstavlja klasu koja generira excel dokument 
 * koji ima n listova na kojem se nalaze potencije na n-tu brojeva
 * između a i b
 * @author vedran
 *
 */
@WebServlet("/powers")
public class PowersServlet extends HttpServlet {
	private int a;
	private int b;
	private int n;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String aString = req.getParameter("a");
		String bString = req.getParameter("b");
		String nString = req.getParameter("n");

		if(!checkParams(aString, bString, nString, req, resp))
			return;

		HSSFWorkbook workbook = new HSSFWorkbook();
		int rowCount = 0;
		HSSFSheet[] sheets = new HSSFSheet[n];
		for (int i = 1; i <= n; i++) {
			sheets[i - 1] = workbook.createSheet(Integer.toString(i));
			for (int j = a; j <= b; j++) {
				HSSFRow row = sheets[i - 1].createRow((short) rowCount++);
				row.createCell((short) 0).setCellValue(j);
				row.createCell((short) 1).setCellValue(Math.pow(j, i));
			}
			rowCount = 0;
		}
		resp.setContentType("application/vnd.ms-excel");
		resp.setHeader("Content-Disposition", "atachment; filename=\"powers.xls\"");
		ServletOutputStream sos = resp.getOutputStream();
		workbook.write(sos);
		sos.flush();	
	}

	/**
	 * Pomoćna metoda koja ispituje jesu li uneseni parametri valjani.
	 * Ako jesu, parisra ih, ako nisu, zahtjev se proslijeđuje na invalidParameters.jsp
	 * @param aString a kao tekst
	 * @param bString b kao tekst
	 * @param nString n kao tekst
	 * @param req request
	 * @param resp response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	private boolean checkParams(String aString, String bString, String nString, HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		if (aString != null && bString != null && nString != null) {
			a = Integer.parseInt(aString);
			b = Integer.parseInt(bString);
			n = Integer.parseInt(nString);
			if (a >= -100 && a <= 100 && b >= -100 && b <= 100 && n >= 1 && n <= 5) {
				return true;
			}
		}
		req.getRequestDispatcher("WEB-INF/pages/invalidParameter.jsp").forward(req, resp);
		return false;
	}

}
