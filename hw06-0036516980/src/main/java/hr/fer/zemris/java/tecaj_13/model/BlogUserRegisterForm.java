package hr.fer.zemris.java.tecaj_13.model;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import hr.fer.zemris.java.tecaj_13.dao.DAOProvider;

public class BlogUserRegisterForm {

	private String id;
	private String firstName;
	private String lastName;
	private String nickName;
	private String email;
	private String passwordHash;

	Map<String, String> errors = new HashMap<>();

	public BlogUserRegisterForm() {
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

		if (this.firstName.isEmpty()) {
			errors.put("firstName", "Polje ime je obavezno!");
		}

		if (this.lastName.isEmpty()) {
			errors.put("lastName", "Polje prezime je obavezno!");
		}
		
		if (this.email.isEmpty()) {
			errors.put("email", "Polje email je obavezno");
		} else {
			int l = email.length();
			int p = email.indexOf('@');
			if (l < 3 || p == -1 || p == 0 || p == l - 1) {
				errors.put("email", "Email nije ispravnog formata.");
			}
		}

		if (this.nickName.isEmpty()) {
			errors.put("nickName", "Polje nadimak je obavezno!");
		} else {
			BlogUser user = DAOProvider.getDAO().getUserByNickName(nickName);
			if (user != null) {
				errors.put("nickName", "Osoba s nadimkom " + nickName + " već postoji.");
				nickName = "";
			}
		}

		if (this.passwordHash.equals(Util.getHashOfPassword(""))) {
			errors.put("password", "Polje zaporka je obavezno!");
		}


	}

	public void fillFromBlogUser(BlogUser user) {
		this.id = user.getId().toString();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.nickName = user.getNickName();
		this.email = user.getEmail();
		this.passwordHash = user.getPasswordHash();
	}

	public void fillFromHttpRequest(HttpServletRequest req) {
		this.firstName = prepare(req.getParameter("firstName"));
		this.lastName = prepare(req.getParameter("lastName"));
		this.nickName = prepare(req.getParameter("nickName"));
		this.email = prepare(req.getParameter("email"));
		this.passwordHash = Util.getHashOfPassword(prepare(req.getParameter("password")));
	}

	public void toBlogUser(BlogUser u) {
		u.setFirstName(this.firstName);
		u.setLastName(this.lastName);
		u.setNickName(this.nickName);
		u.setEmail(this.email);
		u.setPasswordHash(this.passwordHash);
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setNick(String nickName) {
		this.nickName = nickName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getNickName() {
		return nickName;
	}

	public String getEmail() {
		return email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}
}