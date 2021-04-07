package hr.fer.oprpp1.custom.scripting.nodes;

import hr.fer.oprpp1.custom.collections.ArrayIndexedCollection;

/**
 * Temeljna klasa za sve graph nodeove
 * 
 * @author vedran
 *
 */
public class Node {
	private ArrayIndexedCollection col;

	public void addChildNode(Node child) {
		if (col == null)
			col = new ArrayIndexedCollection();

		col.add(child);
	}

	public int numberOfChildren() {
		return col.size();
	}

	public Node getChild(int index) {

		return (Node) col.get(index);
	}

	public ArrayIndexedCollection getCol() {
		return col;
	}
	
	public void accept(INodeVisitor visitor) {
		throw new UnsupportedOperationException("This method is not "
				+ "supported for the Node class!");
	}
}