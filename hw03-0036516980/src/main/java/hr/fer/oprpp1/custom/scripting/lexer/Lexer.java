package hr.fer.oprpp1.custom.scripting.lexer;

import static java.lang.Character.*;

import hr.fer.oprpp1.custom.scripting.parser.SmartScriptParser;

/**
 * Klasa predstavlja lexer koji dobiva tekst i razbija ga u tokene
 * 
 * @author vedran
 *
 */
public class Lexer {
	private char[] data;
	private Token token;
	private int currentIndex;
	private LexerState state = LexerState.BASIC;

	public Lexer(String s) {
		data = s.toCharArray();
		createNextToken();
	}

	/**
	 * Metoda vraća trenutni token(isti dok se ne pozove nextToken())
	 * 
	 * @return trenutni token
	 */
	public Token getToken() {
		return token;
	}

	/**
	 * Metoda vraća stanje u kojem se lexer nalazi
	 * 
	 * @return stanje u kojem se lexer nalazi
	 */
	public LexerState getState() {
		return state;
	}

	/**
	 * Metoda dohvaća novi token
	 * 
	 * @return novi token
	 */
	public Token nextToken() {
		createNextToken();
		return token;

	}

	/**
	 * Metoda postavlja stanje lexera
	 * 
	 * @param state novo stanje lexera
	 */
	public void setState(LexerState state) {
		if (state == null)
			throw new NullPointerException();
		this.state = state;
	}

	/**
	 * Metoda stvara novi token
	 */
	private void createNextToken() {
		if (token != null && token.getType() == TokenType.EOF)
			throw new LexerException("Ne može se napraviti novi token ako je prošli bio null");

		

		if (data.length <= currentIndex) {
			token = new Token(TokenType.EOF, null);
			return;
		}

		if (state == LexerState.BASIC) {

			if (isLetter(data[currentIndex])) {
				wordToken();
				return;
			} else if (data[currentIndex] == '\\') {

				wordToken();
				return;
			} else if (isDigit(data[currentIndex])) {
				numberToken();
				return;
			} else {
				if (data[currentIndex] == '-' && isDigit(data[currentIndex + 1])) {
					numberToken();
					return;
				}
				if (data[currentIndex] == '{' && data[currentIndex + 1] == '$') {
					currentIndex += 2;
					token = new Token(TokenType.TAGBEGINSYB, "{$");
					return;
				}

				token = new Token(TokenType.SYMBOL, data[currentIndex++]);
				return;
			}
		} else if (state == LexerState.TAG) {
			jumpOverBlanks();
			if (isOperator()) {

				if (data[currentIndex] == '-' && isDigit(data[currentIndex + 1])) {
					numberToken();
					return;
				}

				token = new Token(TokenType.OPERATOR, data[currentIndex++]);
				return;
			} else if (toUpperCase(data[currentIndex]) == 'F' && toUpperCase(data[currentIndex + 1]) == 'O'
					&& toUpperCase(data[currentIndex + 2]) == 'R') {

				currentIndex += 3;
				token = new Token(TokenType.FOR, "FOR");
			} else if (toUpperCase(data[currentIndex]) == 'E' && toUpperCase(data[currentIndex + 1]) == 'N'
					&& toUpperCase(data[currentIndex + 2]) == 'D') {

				currentIndex += 3;
				token = new Token(TokenType.END, "END");
				return;
			} else if (SmartScriptParser.inStringInTag == true) {
				if(data[currentIndex] == '"') {
					token = new Token(TokenType.SYMBOL, data[currentIndex++]);
					return;
				}
				stringToken();
				return;
			} else if (isLetter(data[currentIndex])) {
				String s = functionSlashVariableToken("varijable");
				token = new Token(TokenType.NAMEOFVARIABLE, s);
				return;
			} else if (data[currentIndex] == '\\') {
				if (SmartScriptParser.inStringInTag == false)
					throw new LexerException("Krivo korištenje escapeanja u tagu");

				
				if (data[currentIndex + 1] == 'n' || data[currentIndex + 1] == 'r' || data[currentIndex + 1] == 't') {
					token = new Token(TokenType.SYMBOL, "\\" + data[currentIndex + 1]);
					currentIndex += 2;
					return;
				}

				currentIndex++;
				isCurrentEscapeLegalTag();
				wordToken();
				return;
			} else if (data[currentIndex] == '@') {
				String s = functionSlashVariableToken("funkcije");
				token = new Token(TokenType.NAMEOFFUNCTION, s);
				return;
			} else if (isDigit(data[currentIndex])) {
				numberToken();
				return;
			} else if (data[currentIndex] == '=') {

				currentIndex++;
				token = new Token(TokenType.EMPTY, '=');
				return;
			
			} else {
				if (data[currentIndex] == '$' && data[currentIndex + 1] == '}') {
					currentIndex += 2;
					token = new Token(TokenType.TAGENDSYMB, "$}");
					return;
				}
				token = new Token(TokenType.SYMBOL, data[currentIndex++]);
				return;
			}
		}
	}

	private void stringToken() {
		String s = "";
		do {
			s = concatWhitespace(s);
			if(data[currentIndex] == '"') break;
			s += data[currentIndex++];
			if (data.length == currentIndex)
				break;
		} while (data[currentIndex] != '"');
		
		token = new Token(TokenType.WORD, s);
	}
	
	private String concatWhitespace(String s){
		while ((data[currentIndex] == '\\') && (data[currentIndex + 1] == 'n' || data[currentIndex + 1] == 'r' || data[currentIndex + 1] == 't')) {
			switch(data[currentIndex+1]) {
			case 'n':
				s += "\n";
				currentIndex += 2;
				break;
			case 'r':
				s += "\r";
				currentIndex += 2;
				break;
			case 't':
				s += "\t";
				currentIndex += 2;
				break;
			}
			
		}
		return s;
	}

	/**
	 * Metoda vraća legalan naziv funkcije ili varijable. Ako je pogrešan baca
	 * iznimku
	 * 
	 * @param tip "funkcije" ako se radi o funkciji "varijable" ako se radi o
	 *            varijabli
	 * @return legalan naziv funkcije ili varijable
	 * @throws LexerException ako je nedopušteni naziv varijable ili funkcije
	 */
	private String functionSlashVariableToken(String tip) {
		String s = "";

		if (tip == "funkcije") {
			s += '@';
			currentIndex++;
		}

		if (isLetter(data[currentIndex])) {
			do {
				if (isWhitespace(data[currentIndex]))
					break;

				if (isLetter(data[currentIndex]) || isDigit(data[currentIndex]) || data[currentIndex] == '_') {

					s += data[currentIndex++];

				} else
					break;
			} while (true);

		} else
			throw new LexerException("Naziv " + tip + " mora počinjati sa slovom");

		return s;
	}

	/**
	 * Metoda stvara novi token tipa number
	 */
	private void numberToken() {
		String s = "";

		do {
			s += data[currentIndex++];
			if (data.length == currentIndex)
				break;
		} while (isDigit(data[currentIndex]) || data[currentIndex] == '.');

		double n;
		try {
			n = Double.parseDouble(s);
		} catch (NumberFormatException e) {
			throw new LexerException("Greška u parsiranju!");
		}
		if (n % 1 == 0) {
			token = new Token(TokenType.NUMBER, (int) n); //tu je problem sto 0.0 pretvori u 0
			return;
		}
		token = new Token(TokenType.NUMBER, n);

	}

	/**
	 * Metoda stvara novi token tipa word
	 */
	private void wordToken() {
		String s = "";
		do {
			if (data[currentIndex] == '\\') {
				currentIndex++;
				if (state == LexerState.BASIC)
					isCurrentEscapeLegalBasic();
				if (state == LexerState.TAG)
					isCurrentEscapeLegalTag();
			}

			s += data[currentIndex++];
			if (data.length == currentIndex)
				break;
		} while (isLetter(data[currentIndex]) || data[currentIndex] == '\\' || isWhitespace(data[currentIndex]));

		token = new Token(TokenType.WORD, s);

	}
	
	private boolean isWhitespace(char symbol) {
		if(symbol == ' ' || symbol == '\n' || symbol == '\t' || symbol == '\r')
			return true;
		return false;
	}

	/**
	 * Metoda preskače sve vrste bjelina
	 */
	private void jumpOverBlanks() {
		while (currentIndex < data.length) {
			char symbol = data[currentIndex];
			if (symbol == ' ' /*|| symbol == '\n' || symbol == '\t' || symbol == '\r'*/) {
				currentIndex++;
				continue;
			}
			break;
		}

	}

	/**
	 * Metoda ispituje jel trenutni znak operator
	 * 
	 * @return <code>true</code> ako je, inače <code>false</code>
	 */
	private boolean isOperator() {
		char symbol = data[currentIndex];
		if (symbol == '+' || symbol == '-' || symbol == '*' || symbol == '/' || symbol == '^') {
			return true;
		}
		return false;
	}

	/**
	 * Metoda ispituje je li escapeanje u basic načinu rada legalno
	 * 
	 * @return <code>true</code> ako je
	 * @throws LexerException ako nije
	 */
	private boolean isCurrentEscapeLegalBasic() {
		if (currentIndex == data.length || !(data[currentIndex] == '\\' || data[currentIndex] == '{'))
			throw new LexerException();
		return true;
	}

	/**
	 * Metoda ispituje je li escapeanje u basic načinu rada legalno
	 * 
	 * @return <code>true</code> ako je
	 * @throws LexerException ako nije
	 */
	private boolean isCurrentEscapeLegalTag() {
		if (currentIndex == data.length || !(data[currentIndex] == '\\' || data[currentIndex] == '"'
				|| data[currentIndex] == 'n' || data[currentIndex] == 'r' || data[currentIndex] == 't'))
			throw new LexerException();
		return true;
	}
}
