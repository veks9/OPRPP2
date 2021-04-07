package hr.fer.oprpp1.custom.scripting.elems;

/**
 * Klasa nasljeđuje {@link Element} i sprema operator kao {@link String}
 * 
 * @author vedran
 *
 */
public class ElementOperator extends Element {
	private String symbol;

	public ElementOperator(String s) {
		symbol = s;
	}

	@Override
	public String asText() {
		return symbol;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElementOperator other = (ElementOperator) obj;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		return true;
	}

}
