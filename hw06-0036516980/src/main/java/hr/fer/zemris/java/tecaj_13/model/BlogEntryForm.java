package hr.fer.zemris.java.tecaj_13.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class BlogEntryForm {
	
	private String id;
	private String title;
	private String text;
	Map<String, String> errors = new HashMap<>();

	public BlogEntryForm() {
		title = "";
		text = "";
	}

	public String getError(String errorName) {
		return errors.get(errorName);
	}

	
	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	public boolean hasError(String errorName) {
		return errors.containsKey(errorName);
	}

	
	public Map<String, String> getErrors() {
		return errors;
	}

	public void setError(String errorName, String errorMessage) {
		errors.put(errorName, errorMessage);
	}

	private String prepare(String s) {
		if (s == null)
			return "";
		return s.trim();
	}

	public void validate() {
		errors.clear();

		if (this.title.isEmpty()) {
			errors.put("title", "Objava mora imati naslov!");
		}

		if (this.text.isEmpty()) {
			errors.put("text", "Objava ne smije biti prazna!");
		}
	}

	public void fromBlogEntry(BlogEntry entry) {
		this.id = entry.getId().toString();
		this.title = entry.getTitle();
		this.text = entry.getText();
	}

	public void fromHttpRequest(HttpServletRequest req) {
		this.id = prepare(req.getParameter("id"));
		this.title = prepare(req.getParameter("title"));
		this.text = prepare(req.getParameter("text"));
	}

	public void toBlogEntry(BlogEntry e) {
		e.setTitle(this.title);
		e.setText(this.text);
		e.setLastModifiedAt(new Date());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}