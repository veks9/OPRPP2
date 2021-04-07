package hr.fer.oprpp1.custom.scripting.parser;

import hr.fer.oprpp1.custom.collections.ArrayIndexedCollection;
import hr.fer.oprpp1.custom.collections.ObjectStack;
import hr.fer.oprpp1.custom.scripting.elems.Element;
import hr.fer.oprpp1.custom.scripting.elems.ElementConstantDouble;
import hr.fer.oprpp1.custom.scripting.elems.ElementConstantInteger;
import hr.fer.oprpp1.custom.scripting.elems.ElementFunction;
import hr.fer.oprpp1.custom.scripting.elems.ElementOperator;
import hr.fer.oprpp1.custom.scripting.elems.ElementString;
import hr.fer.oprpp1.custom.scripting.elems.ElementVariable;
import hr.fer.oprpp1.custom.scripting.lexer.Lexer;
import hr.fer.oprpp1.custom.scripting.lexer.LexerState;
import hr.fer.oprpp1.custom.scripting.lexer.Token;
import hr.fer.oprpp1.custom.scripting.lexer.TokenType;
import hr.fer.oprpp1.custom.scripting.nodes.DocumentNode;
import hr.fer.oprpp1.custom.scripting.nodes.EchoNode;
import hr.fer.oprpp1.custom.scripting.nodes.ForLoopNode;
import hr.fer.oprpp1.custom.scripting.nodes.Node;
import hr.fer.oprpp1.custom.scripting.nodes.TextNode;

/**
 * Predstavlja parser koji od lexera dobiva tokene i od njih radi nodeove
 * 
 * @author vedran
 *
 */
public class SmartScriptParser {
	private Lexer lexer;
	private ObjectStack stack;
	private DocumentNode documentNode = new DocumentNode();
	public static boolean inStringInTag = false;

	public SmartScriptParser(String s) {
		lexer = new Lexer(s);
		stack = new ObjectStack();

		try {
			parse();
		} catch (Exception e) {
			throw new SmartScriptParserException(e.getMessage());
		}
	}

	/**
	 * Metoda parsira tokene u nodeove i napravi stablo
	 */
	private void parse() {
		Token token = lexer.getToken();
		stack.push(documentNode);

		while (token.getType() != TokenType.EOF) {

			if (token.getType() == TokenType.TAGBEGINSYB) {

				lexer.setState(LexerState.TAG);

				token = lexer.nextToken();
				if (token.getType() == TokenType.FOR) {
					Object[] arr = new Object[4];

					token = lexer.nextToken();

					if (token.getType() != TokenType.NAMEOFVARIABLE)
						throw new SmartScriptParserException("U for petlji prvi argument mora biti varijabla");
					ElementVariable first = new ElementVariable(token.getValue().toString());

					arr[0] = first;

					int i = 1;

					do {
						boolean flag = true;
						token = lexer.nextToken();
						if (token.getValue() == "$}")
							break;
						Element element = null;
						if (token.getValue() instanceof Integer) {
							element = new ElementConstantInteger((int) token.getValue());
						}
						if (token.getValue() instanceof Double) {
							element = new ElementConstantDouble((double) token.getValue());
						}
						if (token.getValue() instanceof String) {
							element = new ElementString((String) token.getValue());
						}
						if (token.getValue() instanceof Character) {
							if ((char) token.getValue() == '"') {
								inStringInTag = !inStringInTag;
								flag = !flag;
								i--;
							}
							element = new ElementString(String.valueOf(token.getValue()));
						}
						if (i == 4)
							throw new SmartScriptParserException("Previse argumenata u for petlji!");
						if (flag)
							arr[i] = element;
						i++;
					} while (true);

					if (i < 3)
						throw new SmartScriptParserException("Premalo argumenata u for petlji!");

					ForLoopNode forLoopNode = new ForLoopNode((ElementVariable) arr[0], (Element) arr[1],
							(Element) arr[2], (Element) arr[3]);

					Node node = (Node) stack.peek();
					node.addChildNode(forLoopNode);
					stack.push(forLoopNode);
					lexer.setState(LexerState.BASIC);
					
					token = lexer.nextToken();
					continue;
				}

				if (token.getType() == TokenType.EMPTY) {
					ArrayIndexedCollection arr = new ArrayIndexedCollection();
					do {
						token = lexer.nextToken();
						Element element = null;
						if (token.getValue() instanceof Integer) {
							element = new ElementConstantInteger((int) token.getValue());
						}
						if (token.getValue() instanceof Double) {
							element = new ElementConstantDouble((double) token.getValue());
						}
						if (token.getValue() instanceof String && token.getValue() != "$}") {
							if(((String) token.getValue()).startsWith("@")) {
								element = new ElementFunction(((String) token.getValue()).substring(1));
							} else if(token.getType() == TokenType.NAMEOFVARIABLE) {
								element = new ElementVariable((String) token.getValue());
							} else
								element = new ElementString((String) token.getValue());
						}
						if (token.getValue() instanceof Character) {
							if ((char) token.getValue() == '"')
								inStringInTag = !inStringInTag;
							element = new ElementString(String.valueOf(token.getValue()));
						}
						if (token.getType() == TokenType.OPERATOR/*token.getValue() == "+" || token.getValue() == "-" || token.getValue() == "*"
								|| token.getValue() == "/" || token.getValue() == "^"*/) {
							element = new ElementOperator(token.getValue().toString());
						}

						if (token.getValue() != "$}")
							arr.add(element);
					} while (token.getValue() != "$}");

					Element[] ele = new Element[arr.size()];

					for (int i = 0; i < arr.size(); i++) {
						ele[i] = (Element) arr.get(i);
					}

					EchoNode echoNode = new EchoNode(ele);

					Node node = (Node) stack.peek();
					node.addChildNode(echoNode);

					lexer.setState(LexerState.BASIC);

					token = lexer.nextToken();
					continue;
				}

				if (token.getType() == TokenType.END) {
					token = lexer.nextToken();
					if(token.getType() != TokenType.TAGENDSYMB)
						throw new SmartScriptParserException("End tag se mora zatvoriti");
					lexer.setState(LexerState.BASIC);
					token = lexer.nextToken();
					stack.pop();
					continue;
				}

			} else if (lexer.getState() == LexerState.BASIC) {
				String s = "";
				do {
					s += token.getValue().toString()/*+ " */; //ako ima razmaka ne valja jedno, ako nema ne valja drugo
					token = lexer.nextToken();
				} while (token.getType() != TokenType.EOF && token.getValue() != "{$");

				TextNode textNode = new TextNode(s);
				Node node = (Node) stack.peek();
				node.addChildNode(textNode);
				continue;

			}
		}
		
	}

	/**
	 * Getter za documentNode
	 * 
	 * @return documentNode
	 */
	public DocumentNode getDocumentNode() {
		return documentNode;
	}
}