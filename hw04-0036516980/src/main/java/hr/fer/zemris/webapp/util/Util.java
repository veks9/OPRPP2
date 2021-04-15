package hr.fer.zemris.webapp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Util klasa za web aplikaciju glasanja
 * @author vedran
 *
 */
public class Util {

	/**
	 * Metoda dohvaća bendove iz datoteke na disku ili ako datoteka ne postoji
	 * kreira ju. Vraća mapu sa idjevima kao ključevima i bendom kao vrijednosti
	 * @param req request
	 * @return mapa
	 * @throws IOException
	 */
	public static Map<Integer, Band> getBands(HttpServletRequest req) throws IOException {
		String fileName = req.getServletContext().getRealPath("/WEB-INF/glasanje-definicija.txt");
		File file = new File(fileName);
		if (!file.exists())
			file.createNewFile();
		Map<Integer, Band> bands = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			while (true) {
				String l = br.readLine();
				if (l == null)
					break;
				String[] arr = l.split("\t");
				bands.put(Integer.parseInt(arr[0]), new Band(l));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bands;
	}

	/**
	 * Metoda puni mapu bendova bands sa vrijednostima glasanja iz datoteke
	 * koja se nalazi na fileName putanji
	 * @param fileName putanja do datoteke na disku 
	 * @param bands mapa bendova
	 * @return mapa bendova sa ažuriranim glasovima
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Map<Integer, Band> fillWithVotes(String fileName, Map<Integer, Band> bands) throws FileNotFoundException, IOException {
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
		}
		return bands;
	}
}
