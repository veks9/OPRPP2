package hr.fer.zemris.webapp.util;

/**
 * Klasa predstavlja bend koji ima id, ime, url pjesme i broj glasova
 * @author vedran
 *
 */
public class Band {
	private int id;
	private String bandName;
	private String songUrl;
	private int numberOfVotes;

	public Band(int id, String bandName, String songUrl, int numberOfVotes) {
		super();
		this.id = id;
		this.bandName = bandName;
		this.songUrl = songUrl;
		this.numberOfVotes = numberOfVotes;
	}

	public Band(String line) {
		String[] lineArray = line.split("\t");
		this.id = Integer.parseInt(lineArray[0].trim());
		this.bandName = lineArray[1].trim();
		this.songUrl = lineArray[2].trim();
		this.numberOfVotes = 0;
	}

	/**
	 * Getter za id
	 * @return id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Getter za ime benda
	 * @return ime benda
	 */
	public String getBandName() {
		return bandName;
	}

	/**
	 * Getter za url pjesme
	 * @return url pjesme
	 */
	public String getSongUrl() {
		return songUrl;
	}

	@Override
	public String toString() {
		return id + "\t" + numberOfVotes + "\n";
	}

	/**
	 * Getter za broj glasova
	 * @return broj glasova
	 */
	public int getNumberOfVotes() {
		return numberOfVotes;
	}

	/**
	 * Setter za broj glasova
	 * @param numberOfVotes novi broj glasova
	 */
	public void setNumberOfVotes(int numberOfVotes) {
		this.numberOfVotes = numberOfVotes;
	}

}