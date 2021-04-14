package hr.fer.zemris.webapp.servlets;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hr.fer.zemris.webapp.util.Band;
import hr.fer.zemris.webapp.util.Util;

@WebServlet("/glasanje-glasaj")
public class GlasanjeGlasajServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Object semaphore = new Object();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String fileName = req.getServletContext().getRealPath("/WEB-INF/glasanje-rezultati.txt");
		synchronized (semaphore) {
			@SuppressWarnings("unchecked")
			Map<Integer, Band> bands = (Map<Integer, Band>) req.getSession().getAttribute("bands");
			if(bands == null) {
				bands = Util.getBands(req);
			}
			int id = Integer.parseInt(req.getParameter("id"));
			if (Files.exists(Paths.get(fileName))) {
				try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
					while(true) {
						String l = br.readLine();
						if(l == null) 
							break;
						String[] arr = l.split("\t");
						Band b = bands.get(Integer.parseInt(arr[0]));
						b.setNumberOfVotes(Integer.parseInt(arr[1]));
					}
					Band b = bands.get(id);
					b.setNumberOfVotes(b.getNumberOfVotes() + 1);
				}
			} else {
				bands.forEach((bandId,band) -> band.setNumberOfVotes((bandId == id ? 1 : 0)));
			}
			
			File file = new File(fileName);
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bands.forEach((bandId,band) -> {
				try {
					bw.write(band.toString());
				} catch (IOException e) {
					
				}
			});
			bw.flush();
			bw.close();
		}
		
		resp.sendRedirect(req.getContextPath() + "/glasanje-rezultati");
	}
}
