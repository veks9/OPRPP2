package hr.fer.oprpp1.custom.scripting.elems;

/**
 * Klasa nasljeđuje {@link Element} i posprema naziv funkcije u varijablu tipa
 * {@link String}
 * 
 * @author vedran
 *
 */
public class ElementFunction extends Element {
	private String name;

	public ElementFunction(String s) {
		name = s;
	}

	@Override
	public String asText() {
		return "@" + name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElementFunction other = (ElementFunction) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
