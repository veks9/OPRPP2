package hr.fer.zemris.webapp.util;

public class Result {
	private String bandName;
	private int numberOfVotes;

	public Result(String bandName, int numberOfVotes) {
		super();
		this.bandName = bandName;
		this.numberOfVotes = numberOfVotes;
	}

	public String getBandName() {
		return bandName;
	}

	public int getNumberOfVotes() {
		return numberOfVotes;
	}

}