package hr.fer.oprpp1.custom.scripting.nodes;

/**
 * Predstavlja cijeli dokument
 * 
 * @author vedran
 *
 */
public class DocumentNode extends Node {

	public DocumentNode() {
		super();
	}

	@Override
	public String toString() {
		String s = "";

		for (int i = 0; i < numberOfChildren(); i++) {
			s += getChild(i).toString();
		}

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

		DocumentNode other = (DocumentNode) obj;

		if (numberOfChildren() != other.numberOfChildren())
			return false;

		boolean ret = true;
		for (int i = 0; i < numberOfChildren(); i++) {
			if (!(getChild(i).equals(other.getChild(i)))) {
				ret = false;
				break;
			}
		}

		return ret;
	}

	public void accept(INodeVisitor visitor) {
		visitor.visitDocumentNode(this);
	}
}