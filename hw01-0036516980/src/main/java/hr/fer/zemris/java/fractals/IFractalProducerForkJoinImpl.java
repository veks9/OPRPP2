package hr.fer.zemris.java.fractals;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;

import hr.fer.zemris.java.fractals.viewer.IFractalProducer;
import hr.fer.zemris.java.fractals.viewer.IFractalResultObserver;
import hr.fer.zemris.java.math.Complex;
import hr.fer.zemris.java.math.ComplexPolynomial;
import hr.fer.zemris.java.math.ComplexRootedPolynomial;

/**
 * Klasa predstavlja tvornicu fraktala koja koristi ForkJoinPool ispod površine
 * @author vedran
 *
 */
public class IFractalProducerForkJoinImpl implements IFractalProducer {

	private ComplexRootedPolynomial rootedPolynomial;
	private ForkJoinPool pool;
	private final int mintracks;
	private final int DEFAULT_MINTRACKS = 16;
	
	public IFractalProducerForkJoinImpl(ComplexRootedPolynomial rootedPolynomial, int mintracks) {
		this.rootedPolynomial = rootedPolynomial;
		if (mintracks == 0) {
			this.mintracks = DEFAULT_MINTRACKS;
		} else {
			this.mintracks = mintracks;
		}
	}

	@Override
	public void close() {
		pool.shutdown();
	}

	@Override
	public void produce(double reMin, double reMax, double imMin, double imMax, int width, int height, long requestNo,
			IFractalResultObserver observer, AtomicBoolean cancel) {

		class Calculate extends RecursiveAction {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private int tracks;
			private int yMin;
			private int yMax;
			private short[] data;
			private int m;
			private ComplexRootedPolynomial rootedPolynomial;
			private final double CONVERGENCE_TRESHOLD = 1E-3;
			private final double ROOT_TRESHOLD = 0.02;

			public Calculate(int tracks, int yMin, int yMax, short[] data, int m, ComplexRootedPolynomial rootedPolynomial) {
				this.tracks = tracks;
				this.yMin = yMin;
				this.yMax = yMax;
				this.data = data;
				this.m = m;
				this.rootedPolynomial = rootedPolynomial;
			}

			@Override
			protected void compute() {
				if (tracks <= mintracks) {
					computeDirect();
				} else {
					Calculate c1 = new Calculate(tracks / 2, yMin, yMin + (yMax - yMin) / 2, data, m, rootedPolynomial);
					Calculate c2 = new Calculate(tracks - tracks / 2, yMin + (yMax - yMin) / 2 + 1, yMax, data, m, rootedPolynomial);
					invokeAll(c1, c2);
				}
			}

			private void computeDirect() {
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
						int offset = (y % height) * width + x;
						data[offset] = (short) (rootedPolynomial.indexOfClosestRootFor(zn, ROOT_TRESHOLD) + 1);
					}
				}
			}
		}
		System.out.println("Zapocinjem izracun...");
		int m = 16 * 16 * 16;
		short[] data = new short[width * height];

		Calculate c = new Calculate(height, 0, height, data, m, rootedPolynomial);
		pool.invoke(c);

		System.out.println("Racunanje gotovo. Idem obavijestiti promatraca tj. GUI!");
		observer.acceptResult(data, (short) (rootedPolynomial.toComplexPolynom().order() + 1), requestNo);

	}

	@Override
	public void setup() {
		pool = new ForkJoinPool();
		System.out.println("mintracks: " + mintracks);

	}

}
