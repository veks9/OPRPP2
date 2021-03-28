package hr.fer.zemris.java.math;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa predstavlja implementaciju polinoma u obliku z0*(z-z1)*(z-z2)*(z-z3)
 * @author vedran
 *
 */
public class ComplexRootedPolynomial {

	private Complex constant;
	private List<Complex> roots = new ArrayList<>();;

	public ComplexRootedPolynomial(Complex constant, Complex... roots) {
		for (Complex root : roots) {
			this.roots.add(root);
		}
		this.constant = constant;
	}

	/**
	 * Metoda računa vrijednost f(z) za dani z.
	 * 
	 * @param z kompleksni broj za čiju vrijednost se želi izračunati d(z)
	 * @return kompleksni broj - vrijednost f(z)
	 */
	public Complex apply(Complex z) {
		Complex result = new Complex(constant.getRe(), constant.getIm());

		for (Complex c : roots) {
			result = result.multiply(z.sub(c));
		}

		return result;
	}

	/**
	 * Metoda pretvara polinom iz oblika {@link ComplexRootedPolynomial} u
	 * {@link ComplexPolynomial}
	 * 
	 * @return novi objekt tipa {@link ComplexPolynomial}
	 */
	public ComplexPolynomial toComplexPolynom() {
		ComplexPolynomial factors = new ComplexPolynomial(roots.get(0).negate(), Complex.ONE);

		for (int i = 1; i < roots.size(); i++) {
			ComplexPolynomial cp = new ComplexPolynomial(roots.get(i).negate(), Complex.ONE);
			factors = factors.multiply(cp);
		}

		return factors.multiply(new ComplexPolynomial(constant));
	}

	@Override
	public String toString() {
		String s = "(" + constant + ")";
		for (Complex c : roots) {
			s += "*(z-" + "(" + c + ")" + ")";
		}
		return s;
	}

	// finds index of closest root for given complex number z that is within
	// treshold; if there is no such root, returns -1
	// first root has index 0, second index 1, etc
	/**
	 * Traži indeks najbliže nul-točke za dani kompleksni broj z koji je unutar
	 * granice <code>treshold</code>. Ako takva nul-točka ne postoji, vraća se -1.
	 * 
	 * @param z
	 * @param treshold granica
	 * @return indeks ili -1 ako ne postoji takva nul-tokča
	 */
	public int indexOfClosestRootFor(Complex z, double treshold) {
		int index = 0;
		double distance = roots.get(index).sub(z).module();

		for (int i = 1; i < roots.size(); i++) {
			double d = roots.get(i).sub(z).module();

			if (d < distance) {
				index = i;
				distance = d;
			}
		}

		return distance < treshold ? index : -1;
	}
}
