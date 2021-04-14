package hr.fer.zemris.webapp.util;

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

	public int getId() {
		return id;
	}

	public String getBandName() {
		return bandName;
	}

	public String getSongUrl() {
		return songUrl;
	}

	@Override
	public String toString() {
		return id + "\t" + numberOfVotes + "\n";
	}

	public int getNumberOfVotes() {
		return numberOfVotes;
	}

	public void setNumberOfVotes(int numberOfVotes) {
		this.numberOfVotes = numberOfVotes;
	}

}