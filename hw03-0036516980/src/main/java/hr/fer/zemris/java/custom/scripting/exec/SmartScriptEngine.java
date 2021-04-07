package hr.fer.zemris.java.custom.scripting.exec;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Stack;

import hr.fer.oprpp1.custom.scripting.elems.Element;
import hr.fer.oprpp1.custom.scripting.elems.ElementConstantDouble;
import hr.fer.oprpp1.custom.scripting.elems.ElementConstantInteger;
import hr.fer.oprpp1.custom.scripting.elems.ElementFunction;
import hr.fer.oprpp1.custom.scripting.elems.ElementOperator;
import hr.fer.oprpp1.custom.scripting.elems.ElementString;
import hr.fer.oprpp1.custom.scripting.elems.ElementVariable;
import hr.fer.oprpp1.custom.scripting.nodes.DocumentNode;
import hr.fer.oprpp1.custom.scripting.nodes.EchoNode;
import hr.fer.oprpp1.custom.scripting.nodes.ForLoopNode;
import hr.fer.oprpp1.custom.scripting.nodes.INodeVisitor;
import hr.fer.oprpp1.custom.scripting.nodes.TextNode;
import hr.fer.zemris.java.webserver.RequestContext;

public class SmartScriptEngine {
	private DocumentNode documentNode;
	private RequestContext requestContext;
	private ObjectMultistack multistack = new ObjectMultistack();
	private INodeVisitor visitor = new INodeVisitor() {

		@Override
		public void visitTextNode(TextNode node) {
			try {
				requestContext.write(node.getText());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}

		@Override
		public void visitForLoopNode(ForLoopNode node) {
			String variableName = node.getVariable();
			ValueWrapper start = new ValueWrapper(node.getStartExpression());
			ValueWrapper end = new ValueWrapper(node.getEndExpression());
			ValueWrapper step = new ValueWrapper(node.getStepExpression());

			multistack.push(variableName, start);

			while (multistack.peek(variableName).numCompare(end.getValue()) <= 0) {
				for (int i = 0; i < node.numberOfChildren(); i++) {
					node.getChild(i).accept(visitor);
				}

				multistack.peek(variableName).add(step.getValue());
			}

			multistack.pop(variableName);
		}

		@Override
		public void visitEchoNode(EchoNode node) {
			Stack<ValueWrapper> stack = new Stack<>();

			Element[] elements = node.getElements();

			for (int i = 0; i < elements.length; i++) {
				Element e = elements[i];

				if (e instanceof ElementConstantInteger) {
					stack.push(new ValueWrapper(((ElementConstantInteger) e).getValue()));
				} else if (e instanceof ElementConstantDouble) {
					stack.push(new ValueWrapper(((ElementConstantDouble) e).getValue()));
				} else if (e instanceof ElementString) {
					if(((ElementString) e).asText().equals("\"") ||
							((ElementString) e).asText().equals("\n")) continue;
					stack.push(new ValueWrapper(e.asText()));
				} else if (e instanceof ElementVariable) {
					ValueWrapper val = multistack.peek(e.asText());
					stack.push(new ValueWrapper(val.getValue()));
				} else if (e instanceof ElementOperator) {
					caseOperator((ElementOperator) e, stack);
				} else if(e instanceof ElementFunction) {
					caseFunction((ElementFunction) e, stack);
				}
			}
			
			Stack<Object> tempStack = new Stack<>();
			while (!stack.isEmpty()) {
				tempStack.push(stack.pop());
			}
			while (!tempStack.isEmpty()) {
				try {
					requestContext.write(tempStack.pop().toString());
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		}

		private void caseFunction(ElementFunction e, Stack<ValueWrapper> stack) {
			String function = e.asText().substring(1);

			if (function.equals("sin")) {
				ValueWrapper x = stack.pop();
				Double rad = ((Number) x.getValue()).doubleValue();
				Double deg = Math.PI * rad / 180.0;
				ValueWrapper r = new ValueWrapper(Double.valueOf(Math.sin(deg)));
				stack.push(r);
			} else if (function.equals("decfmt")) {
				ValueWrapper f = stack.pop();
				ValueWrapper x = stack.pop();

				DecimalFormat format = new DecimalFormat((String) f.getValue());

				ValueWrapper r = new ValueWrapper(format.format((double) x.getValue()));
				stack.push(r);
			} else if (function.equals("dup")) {
				ValueWrapper x = stack.pop();
				stack.push(x);
				stack.push(x);
			} else if (function.equals("swap")) {
				ValueWrapper x = stack.pop();
				ValueWrapper y = stack.pop();

				stack.push(x);
				stack.push(y);
			} else if (function.equals("setMimeType")) {
				ValueWrapper x = stack.pop();
				requestContext.setMimeType((String) x.getValue());
			} else if (function.equals("paramGet")) {
				ValueWrapper defValue = stack.pop();
				ValueWrapper name = stack.pop();

				String value = requestContext.getParameter((String) name.getValue());

				stack.push(value == null ? defValue : new ValueWrapper(value));
			} else if (function.equals("pparamGet")) {
				ValueWrapper defValue = stack.pop();
				ValueWrapper name = stack.pop();

				String value = requestContext.getPersistentParameter((String) name.getValue());

				stack.push(value == null ? defValue : new ValueWrapper(value));
			} else if (function.equals("pparamSet")) {
				ValueWrapper name = stack.pop();
				ValueWrapper value = stack.pop();

				requestContext.setPersistentParameter(name.getValue().toString(), value.getValue().toString());
			} else if (function.equals("pparamDel")) {
				ValueWrapper name = stack.pop();

				requestContext.removePersistentParameter((String) name.getValue());
			} else if (function.equals("tparamGet")) {
				ValueWrapper defValue = stack.pop();
				ValueWrapper name = stack.pop();

				String value = requestContext.getTemporaryParameter((String) name.getValue());

				stack.push(value == null ? defValue : new ValueWrapper(value));
			} else if (function.equals("tparamSet")) {
				ValueWrapper name = stack.pop();
				ValueWrapper value = stack.pop();

				requestContext.setTemporaryParameter(name.getValue().toString(), value.getValue().toString());
			} else if (function.equals("tparamDel")) {
				ValueWrapper name = stack.pop();

				requestContext.removeTemporaryParameter((String) name.getValue());
			}
		}

		private void caseOperator(ElementOperator e, Stack<ValueWrapper> stack) {
			ValueWrapper v1 = stack.pop();
			ValueWrapper v2 = stack.pop();

			switch (e.asText()) {
			case "+":
				v1.add(v2.getValue());
				break;
			case "-":
				v1.subtract(v2.getValue());
				break;
			case "*":
				v1.multiply(v2.getValue());
				break;
			case "/":
				v1.divide(v2.getValue());
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + e.asText());
			}

			stack.push(v1);
			
		}

		@Override
		public void visitDocumentNode(DocumentNode node) {
			for (int i = 0; i < node.numberOfChildren(); i++)
				node.getChild(i).accept(this);
		}

	};

	public SmartScriptEngine(DocumentNode documentNode, RequestContext requestContext) {
		super();
		this.documentNode = Objects.requireNonNull(documentNode);
		this.requestContext = Objects.requireNonNull(requestContext);
	}

	public void execute() {
		documentNode.accept(visitor);
	}
}
