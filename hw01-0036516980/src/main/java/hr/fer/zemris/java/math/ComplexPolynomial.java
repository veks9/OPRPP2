package hr.fer.zemris.java.math;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa predstavlja implementaciju polinoma u obliku zn*z^n+(zn1)*z^(n-1)+...+z2*z^2+z1*z+z0
 * @author vedran
 *
 */
public class ComplexPolynomial {

	private List<Complex> factors = new ArrayList<>();

	public ComplexPolynomial(Complex... factors) {
		if (factors == null)
			throw new NullPointerException("Faktori ne smiju biti null!");
		if (factors.length == 0)
			throw new IllegalArgumentException("Mora se predati barem jedan faktor!");

		for (Complex factor : factors) {
			this.factors.add(factor);
		}
	}

	// returns order of this polynom; eg. For (7+2i)z^3+2z^2+5z+1 returns 3
	/**
	 * Metoda vraća stupanj polinoma. npr (7+2i)z^3+2z^2+5z+1 vraća 3
	 * 
	 * @return stupanj polinoma
	 */
	public short order() {
		return (short) (factors.size() - 1);
	}

	/**
	 * Metoda množi dva polinoma tipa {@link ComplexPolynomial} i vraća novi objekt
	 * 
	 * @param p drugi polinom
	 * @return produkt kao novi objekt
	 */
	public ComplexPolynomial multiply(ComplexPolynomial p) {
		Complex[] result = new Complex[this.order() + 1 + p.order() + 1 - 1];

		for (int i = 0; i < result.length; i++) {
			result[i] = Complex.ZERO;
		}

		for (int i = 0; i < factors.size(); i++) {
			for (int j = 0; j < p.factors.size(); j++) {
				result[i + j] = result[i + j].add(factors.get(i).multiply(p.factors.get(j)));
			}
		}

		return new ComplexPolynomial(result);

	}

	/**
	 * Metoda derivira this {@link ComplexPolynomial}. npr (7+2i)z^3+2z^2+5z+1 vraća
	 * (21+6i)z^2+4z+5
	 * 
	 * @return
	 */
	public ComplexPolynomial derive() {
		Complex[] arr = new Complex[factors.size() - 1];

		for (int i = 1; i < factors.size(); i++) {
			arr[i - 1] = factors.get(i).multiply(new Complex(i, 0));
		}

		if (arr.length == 0)
			return new ComplexPolynomial(Complex.ZERO);

		return new ComplexPolynomial(arr);
	}

	/**
	 * Metoda računa vrijednost f(z) za dani z.
	 * 
	 * @param z kompleksni broj za čiju vrijednost se želi izračunati d(z)
	 * @return kompleksni broj - vrijednost f(z)
	 */
	public Complex apply(Complex z) {
		Complex result = Complex.ZERO;

		for (int i = 0; i < factors.size(); i++) {
			result = result.add(factors.get(i).multiply(z.power(i)));
		}
		return result;
	}

	@Override
	public String toString() {
		String s = "";

		for (int i = factors.size() - 1; i >= 0; i--) {
			s += "(" + factors.get(i) + ")";
			if (i != 0) {
				s += "*z^" + i + "+";
			}
		}
		return s;
	}
}