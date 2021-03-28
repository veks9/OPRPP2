package hr.fer.zemris.java.fractals;

import java.util.concurrent.atomic.AtomicBoolean;

import hr.fer.zemris.java.math.Complex;
import hr.fer.zemris.java.math.ComplexPolynomial;
import hr.fer.zemris.java.math.ComplexRootedPolynomial;

/**
 * Klasa radi posao izračuna boja za ispis na ekranu.
 * @author vedran
 *
 */
public class Calculate implements Runnable {

	private double reMin;
	private double reMax;
	private double imMin;
	private double imMax;
	private int width;
	private int height;
	private int yMin;
	private int yMax;
	private int m;
	private short[] data;
	private AtomicBoolean cancel;
	public final static Calculate NO_JOB = new Calculate();
	private ComplexRootedPolynomial rootedPolynomial;
	private final double CONVERGENCE_TRESHOLD = 1E-3;
	private final double ROOT_TRESHOLD = 0.02;


	private Calculate() {
		
	}
	
	public Calculate(double reMin, double reMax, double imMin, double imMax, int width, int height, int yMin, int yMax,
			int m, short[] data, AtomicBoolean cancel, ComplexRootedPolynomial polynom) {
		super();
		this.reMin = reMin;
		this.reMax = reMax;
		this.imMin = imMin;
		this.imMax = imMax;
		this.width = width;
		this.height = height;
		this.yMin = yMin;
		this.yMax = yMax;
		this.m = m;
		this.data = data;
		this.cancel = cancel;
		this.rootedPolynomial = polynom;
	}

	@Override
	public void run() {
		ComplexPolynomial polynomial = rootedPolynomial.toComplexPolynom();
		ComplexPolynomial derived = polynomial.derive();

		for (int y = yMin; y <= yMax; y++) {
			if (cancel.get())
				break;
			for (int x = 0; x < width; x++) {
				double cre = x / (width - 1.0) * (reMax - reMin) + reMin;
				double cim = (height - 1.0 - y) / (height - 1) * (imMax - imMin) + imMin;
				Complex zn = new Complex(cre, cim);
				double module = 0.;
				int iters = 0;
				do {
					Complex numerator = polynomial.apply(zn);
					Complex denominator = derived.apply(zn);
					Complex znold = zn;
					Complex fraction = numerator.divide(denominator);
					zn = zn.sub(fraction);
					module = znold.sub(zn).module();
					iters++;
				} while (iters < m && module > CONVERGENCE_TRESHOLD);
				int offset = (y % height)*width + x;
				data[offset] = (short) (rootedPolynomial.indexOfClosestRootFor(zn, ROOT_TRESHOLD) + 1);
			}
		}
	}

}