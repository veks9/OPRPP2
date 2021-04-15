package hr.fer.zemris.webapp.util;

/**
 * Klasa modelira rezultat glasanja koji ima ime benda i broj glasova
 * @author vedran
 *
 */
public class Result {
	private String bandName;
	private int numberOfVotes;

	public Result(String bandName, int numberOfVotes) {
		super();
		this.bandName = bandName;
		this.numberOfVotes = numberOfVotes;
	}

	/**
	 * Getter za ime benda
	 * @return
	 */
	public String getBandName() {
		return bandName;
	}

	/**
	 * Getter za broj glasova
	 * @return broj glasova
	 */
	public int getNumberOfVotes() {
		return numberOfVotes;
	}

}