package hr.fer.zemris.java.webapp.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import hr.fer.zemris.java.webapp.model.PollOption;

/**
 * Klasa predstavlja servlet koji generira excel dokument rezultata glasanja
 * @author vedran
 *
 */
@WebServlet("/servleti/glasanje-xls")
public class GlasanjeXLSServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		List<PollOption> result = (List<PollOption>) session.getAttribute("result");
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		HSSFRow header = sheet.createRow(0);
		header.createCell((short) 0).setCellValue((String) session.getAttribute("pollSubject"));
		header.createCell((short) 1).setCellValue("Number of votes");

		int size = result.size();
		for (int i = 1; i <= size; i++) {
			HSSFRow row = sheet.createRow(i);
			row.createCell((short) 0).setCellValue(result.get(i - 1).getTitle());
			row.createCell((short) 1).setCellValue(result.get(i - 1).getNumberOfVotes());
		}

		resp.setContentType("application/vnd.ms-excel");
		resp.setHeader("Content-Disposition", "atachment; filename=\"votingResults.xls\"");
		ServletOutputStream sos = resp.getOutputStream();
		workbook.write(sos);
		sos.flush();
		
		workbook.close();
	}
}
