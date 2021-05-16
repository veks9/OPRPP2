package hr.fer.zemris.java.webapp.model;

/**
 * Klasa modelira stavku ankete u web aplikaciji
 * @author vedran
 *
 */
public class PollOption {
	private long id;
	private String title;
	private String songUrl;
	private long pollId;
	private long numberOfVotes;
	
	public PollOption() {
	
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSongUrl() {
		return songUrl;
	}

	public void setSongUrl(String songUrl) {
		this.songUrl = songUrl;
	}

	public long getPollId() {
		return pollId;
	}

	public void setPollId(long pollId) {
		this.pollId = pollId;
	}

	public long getNumberOfVotes() {
		return numberOfVotes;
	}

	public void setNumberOfVotes(long numberOfVotes) {
		this.numberOfVotes = numberOfVotes;
	}

	@Override
	public String toString() {
		return "PollOption [id=" + id + ", title=" + title + ", link=" + songUrl + ", pollId=" + pollId + ", votesCount="
				+ numberOfVotes + "]";
	}
}
