package hr.fer.oprpp1.custom.scripting.elems;

/**
 * Klasa nasljeđuje {@link Element} i posprema naziv zvarijable u {@link String}
 * 
 * @author vedran
 *
 */
public class ElementVariable extends Element {
	private String name;

	public ElementVariable(String p) {
		name = p;
	}

	@Override
	public String asText() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElementVariable other = (ElementVariable) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
