package hr.fer.oprpp1.custom.scripting.lexer;

/**
 * Predstavlja token koji lexer radi
 * 
 * @author vedran
 *
 */
public class Token {
	private TokenType type;
	private Object value;

	public Token(TokenType type, Object value) {
		this.type = type;
		this.value = value;
	}

	/**
	 * Getter za vrijednost tokena
	 * 
	 * @return vrijednost tokena
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Getter za tip tokena
	 * 
	 * @return tip tokena
	 */
	public TokenType getType() {
		return type;
	}
}