package hr.fer.zemris.java.webapp.model;

/**
 * Klasa modelira anketu u web aplikaciji
 * @author vedran
 *
 */
public class Poll {
	private long id;
	private String title;
	private String message;
	
	public Poll() {
	
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Poll [id=" + id + ", title=" + title + ", message=" + message + "]";
	}
}
