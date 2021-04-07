package hr.fer.oprpp1.custom.scripting.nodes;

import java.util.Arrays;

import hr.fer.oprpp1.custom.scripting.elems.Element;

/**
 * Prestavlja naredbu koja generira neki tekst dinamički
 * 
 * @author vedran
 *
 */
public class EchoNode extends Node {
	private Element[] elements;

	public EchoNode(Element[] e) {
		elements = e;
	}

	public Element[] getElements() {
		return elements;
	}



	@Override
	public String toString() {
		String s = "";

		s += "{$ = ";

		for (int i = 0; i < elements.length; i++) {
			s += elements[i].asText() + " ";
		}

		s += "$}";

		return s;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EchoNode other = (EchoNode) obj;

		if (!Arrays.equals(elements, other.elements))
			return false;
		return true;
	}
	
	public void accept(INodeVisitor visitor) {
		visitor.visitEchoNode(this);
	}

}