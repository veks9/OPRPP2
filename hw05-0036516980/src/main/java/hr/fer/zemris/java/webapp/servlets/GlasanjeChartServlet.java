package hr.fer.zemris.java.webapp.servlets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import hr.fer.zemris.java.webapp.model.PollOption;

/**
 * Klasa predstavlja servlet koji generira pie chart rezultata glasanja
 * @author vedran
 *
 */
@WebServlet("/servleti/glasanje-grafika")
public class GlasanjeChartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("image/png");

		OutputStream outputStream = resp.getOutputStream();
		@SuppressWarnings("unchecked")
		List<PollOption> results = (List<PollOption>) req.getSession().getAttribute("result");
		
		JFreeChart chart = getChart(results);
		int width = 400;
		int height = 400;
		ChartUtils.writeChartAsPNG(outputStream, chart, width, height);
	}

	/**
	 * Pomoćna metoda koja generira pie chart
	 * @param results
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JFreeChart getChart(List<PollOption> results) {
		@SuppressWarnings("rawtypes")
		DefaultPieDataset dataset = new DefaultPieDataset();
		results.forEach(p -> dataset.setValue(p.getTitle(), p.getNumberOfVotes()));

		boolean legend = true;
		boolean tooltips = false;
		boolean urls = false;

		JFreeChart chart = ChartFactory.createPieChart("Voting results", dataset, legend, tooltips, urls);

		chart.setBorderPaint(Color.GREEN);
		chart.setBorderStroke(new BasicStroke(5.0f));
		chart.setBorderVisible(true);

		return chart;
	}
}
