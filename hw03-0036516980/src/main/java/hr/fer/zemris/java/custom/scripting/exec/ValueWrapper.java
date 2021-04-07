package hr.fer.zemris.java.custom.scripting.exec;

import java.util.function.DoubleBinaryOperator;

public class ValueWrapper {
	private Object value;
	private static final Integer NULL = Integer.valueOf(0);

	public ValueWrapper(Object value) {
		super();
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void add(Object incValue) {
		this.value = operation(this.value, incValue, (n1, n2) -> n1 + n2);
	}

	public void subtract(Object decValue) {
		this.value = operation(this.value, decValue, (n1, n2) -> n1 - n2);
	}

	public void multiply(Object mulValue) {
		this.value = operation(this.value, mulValue, (n1, n2) -> n1 * n2);
	}

	public void divide(Object divValue) {
		this.value = operation(this.value, divValue, (n1, n2) -> n1 / n2);
	}

	public int numCompare(Object withValue) {
		if (value == null && withValue == null)
			return 0;
		return operation(this.value, withValue, (n1, n2) -> n1 - n2).intValue();
	}

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

	private void checkValue(Object value) {
		if (!(value instanceof Integer) && !(value instanceof Double) && !(value instanceof String) && value != null) {
			throw new IllegalArgumentException("The content must be of instance of"
					+ " Integer, Double, String or null to perform arithmetic operations.");
		}
	}

	private Number getNumber(Object number) {
		if (number == null)
			return NULL;

		if (number instanceof Integer || number instanceof Double) {
			return (Number) number;
		}

		return parseString((String) number);
	}

	private Number parseString(String s) {
		if (isDecimal(s)) {
			return parseDecimal(s);
		} else {
			return parseInteger(s);
		}
	}

	private boolean isDecimal(String s) {
		return s.contains(".") || s.contains("E");
	}

	private Number parseDecimal(String s) {
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Decimal number couldn't be parsed!");
		}
	}

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
