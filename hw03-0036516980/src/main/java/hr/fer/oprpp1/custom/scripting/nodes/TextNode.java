package hr.fer.oprpp1.custom.scripting.nodes;

/**
 * Predstavlja tekstualne podatke
 * 
 * @author vedran
 *
 */
public class TextNode extends Node {
	private String text;

	public TextNode(String s) {
		text = s;
	}

	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return getText();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TextNode other = (TextNode) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

	public void accept(INodeVisitor visitor) {
		visitor.visitTextNode(this);
	}
}