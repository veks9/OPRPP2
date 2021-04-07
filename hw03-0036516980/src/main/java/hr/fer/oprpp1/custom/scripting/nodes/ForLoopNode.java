package hr.fer.oprpp1.custom.scripting.nodes;

import hr.fer.oprpp1.custom.scripting.elems.Element;
import hr.fer.oprpp1.custom.scripting.elems.ElementVariable;
import hr.fer.oprpp1.custom.scripting.parser.SmartScriptParserException;

/**
 * Predstavlja jednu for petlju
 * 
 * @author vedran
 *
 */
public class ForLoopNode extends Node {
	private ElementVariable variable;
	private Element startExpression;
	private Element endExpression;
	private Element stepExpression;

	public ForLoopNode(ElementVariable variable, Element startExpression, Element endExpression,
			Element stepExpression) {
		if (variable == null || startExpression == null || endExpression == null)
			throw new SmartScriptParserException("Neki od obaveznih parametara for petlje je null!");

		this.variable = variable;
		this.startExpression = startExpression;
		this.endExpression = endExpression;
		this.stepExpression = stepExpression;
	}

	/**
	 * Getter varijable
	 * 
	 * @return varijabla
	 */
	public String getVariable() {
		return variable.asText();
	}

	/**
	 * Getter od startExpression
	 * 
	 * @return startExpression
	 */
	public String getStartExpression() {
		return startExpression.asText();
	}

	/**
	 * Getter od endExpression
	 * 
	 * @return endExpression
	 */
	public String getEndExpression() {
		return endExpression.asText();
	}

	/**
	 * Getter od stepExpression
	 * 
	 * @return stepExpression
	 */
	public String getStepExpression() {
		return stepExpression.asText();
	}

	@Override
	public String toString() {
		String s = "";

		s += "{$ FOR ";
		s += getVariable() + " ";
		s += getStartExpression() + " ";
		s += getEndExpression() + " ";
		if (stepExpression != null)
			s += getStepExpression();
		s += " $}";
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
		ForLoopNode other = (ForLoopNode) obj;
		if (endExpression == null) {
			if (other.endExpression != null)
				return false;
		} else if (!endExpression.equals(other.endExpression))
			return false;
		if (startExpression == null) {
			if (other.startExpression != null)
				return false;
		} else if (!startExpression.equals(other.startExpression))
			return false;
		if (stepExpression == null) {
			if (other.stepExpression != null)
				return false;
		} else if (!stepExpression.equals(other.stepExpression))
			return false;
		if (variable == null) {
			if (other.variable != null)
				return false;
		} else if (!variable.equals(other.variable))
			return false;
		return true;
	}
	
	public void accept(INodeVisitor visitor) {
		visitor.visitForLoopNode(this);
		
	}

}