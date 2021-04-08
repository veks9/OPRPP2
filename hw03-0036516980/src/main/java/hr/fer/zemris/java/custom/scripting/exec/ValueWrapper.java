package hr.fer.zemris.java.custom.scripting.exec;

import java.util.function.DoubleBinaryOperator;

/**
 * Klasa predstavlja objekt koji sadrži vrijednost i nad kojim se mogu zvati aritmetičke operacije
 * @author vedran
 *
 */
public class ValueWrapper {
	private Object value;
	private static final Integer NULL = Integer.valueOf(0);

	public ValueWrapper(Object value) {
		super();
		this.value = value;
	}
	
	/**
	 * Getter za vrijednost
	 * @return vrijednost
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Setter za vrijednost
	 * @param value vrijednost
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * Metoda zbraja this.value i incValue i rezultat pohranjuje u this.value
	 * @param incValue 
	 */
	public void add(Object incValue) {
		this.value = operation(this.value, incValue, (n1, n2) -> n1 + n2);
	}

	/**
	 * Metoda oduzima this.value i decValue i rezultat pohranjuje u this.value
	 * @param decValue 
	 */
	public void subtract(Object decValue) {
		this.value = operation(this.value, decValue, (n1, n2) -> n1 - n2);
	}

	/**
	 * Metoda množi this.value i mulValue i rezultat pohranjuje u this.value
	 * @param mulValue 
	 */
	public void multiply(Object mulValue) {
		this.value = operation(this.value, mulValue, (n1, n2) -> n1 * n2);
	}

	/**
	 * Metoda dijeli this.value i divValue i rezultat pohranjuje u this.value
	 * @param divValue 
	 */
	public void divide(Object divValue) {
		this.value = operation(this.value, divValue, (n1, n2) -> n1 / n2);
	}

	/**
	 * Metoda uspoređuje this.value i withValue. Ako je this.value veći, retultat je pozitivan,
	 * ako je manji, negativan, a ako su jednaki onda je 0
	 * @param withValue
	 * @return ako je this.value veći, retultat je pozitivan,
	 * ako je manji, negativan, a ako su jednaki onda je 0
	 */
	public int numCompare(Object withValue) {
		if (value == null && withValue == null)
			return 0;
		return operation(this.value, withValue, (n1, n2) -> n1 - n2).intValue();
	}

	/**
	 * Pomoćna metoda koja napravi predanu operaciju i vrati rezultat
	 * @param number1 prvi operand
	 * @param number2 drugi operand
	 * @param operator operacija koja se treba napraviti
	 * @return rezultat
	 */
	private Number operation(Object number1, Object number2, DoubleBinaryOperator operator) {
		checkValue(number1);
		checkValue(number2);
		Number n1 = getNumber(number1);
		Number n2 = getNumber(number2);

		Double result = operator.applyAsDouble(n1.doubleValue(), n2.doubleValue());
		if (n1 instanceof Double || n2 instanceof Double) {
			return result;
		}

		return result.intValue();
	}

	/**
	 * Metoda provjerava je li predani value instanca {@link Integer}a, {@link Double}a, {@link String}a 
	 * ili null. Ako nije ništa od navedenog, baca iznimku
	 * @param value
	 */
	private void checkValue(Object value) {
		if (!(value instanceof Integer) && !(value instanceof Double) && !(value instanceof String) && value != null) {
			throw new IllegalArgumentException("The content must be of instance of"
					+ " Integer, Double, String or null to perform arithmetic operations.");
		}
	}

	/**
	 * Metoda prima objekt tipa Object, parsira ga ako je potrebno i vrati broj zamotan u Number
	 * @param number broj koji je potrebno zamotati
	 * @return broj
	 */
	private Number getNumber(Object number) {
		if (number == null)
			return NULL;

		if (number instanceof Integer || number instanceof Double) {
			return (Number) number;
		}

		return parseString((String) number);
	}

	/**
	 * Pomoćna metoda koja parsira string s i vraća broj tipa Number
	 * @param s string kojeg treba parsirati
	 * @return
	 */
	private Number parseString(String s) {
		if (isDecimal(s)) {
			return parseDecimal(s);
		} else {
			return parseInteger(s);
		}
	}

	/**
	 * Pomoćna metoda koja ispituje je li broj decimalan
	 * @param s broj
	 * @return <code>true</code> ako je decimalan, inače <code>false</code>
	 */
	private boolean isDecimal(String s) {
		return s.contains(".") || s.contains("E");
	}

	/**
	 * Pomoćna metoda koja parsira decimalni broj
	 * @param s string koji je potrebno parsirati
	 * @return parsirani broj tipa Number
	 */
	private Number parseDecimal(String s) {
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Decimal number couldn't be parsed!");
		}
	}

	/**
	 * Pomoćna metoda koja parsira cijeli broj
	 * @param s string koji je potrebno parsirati
	 * @return parsirani broj tipa Number
	 */
	private Number parseInteger(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("The integer number couldn't be parsed!");
		}
	}
	
	@Override
	public String toString() {
		return value.toString();
	}
}
