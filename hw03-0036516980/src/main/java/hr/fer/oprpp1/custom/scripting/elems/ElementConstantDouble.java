package hr.fer.oprpp1.custom.scripting.elems;

/**
 * Klasa nasljeđuje {@link Element} i kao varijablu ima {@link Double}
 * vrijednost
 * 
 * @author vedran
 *
 */
public class ElementConstantDouble extends Element {

	private double value;

	public ElementConstantDouble(double v) {
		value = v;
	}

	public double getValue() {
		return value;
	}

	@Override
	public String asText() {
		return Double.toString(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElementConstantDouble other = (ElementConstantDouble) obj;
		if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value))
			return false;
		return true;
	}

}