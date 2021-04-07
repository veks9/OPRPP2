package hr.fer.oprpp1.custom.scripting.elems;

/**
 * Klasa nasljeđuje {@link Element} i kao varijablu ima {@link String}
 * 
 * @author vedran
 *
 */
public class ElementString extends Element {
	private String value;

	public ElementString(String v) {
		value = v;
	}

	@Override
	public String asText() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElementString other = (ElementString) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
