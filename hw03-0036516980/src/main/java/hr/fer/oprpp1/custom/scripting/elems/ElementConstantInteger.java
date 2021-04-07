package hr.fer.oprpp1.custom.scripting.elems;

/**
 * Klasa nasljeđuje {@link Element} i kao varijablu ima {@link Integer}
 * vrijednost
 * 
 * @author vedran
 *
 */
public class ElementConstantInteger extends Element {

	private int value;

	public ElementConstantInteger(int v) {
		value = v;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String asText() {
		return Integer.toString(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElementConstantInteger other = (ElementConstantInteger) obj;
		if (value != other.value)
			return false;
		return true;
	}

}
