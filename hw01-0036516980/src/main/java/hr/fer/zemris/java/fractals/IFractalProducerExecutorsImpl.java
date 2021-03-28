package hr.fer.zemris.java.fractals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import hr.fer.zemris.java.fractals.viewer.IFractalProducer;
import hr.fer.zemris.java.fractals.viewer.IFractalResultObserver;
import hr.fer.zemris.java.math.ComplexRootedPolynomial;

/**
 * Klasa predstavlja tvornicu fraktala koja koristi okruženje Executors ispod površine
 * @author vedran
 *
 */
public class IFractalProducerExecutorsImpl implements IFractalProducer {

	private ComplexRootedPolynomial rootedPolynomial;
	private int workers;
	private int tracks;
	private ExecutorService pool;
	private final int DEFAULT_WORKERS = Runtime.getRuntime().availableProcessors();
	private final int DEFAULT_TRACKS = 4 * Runtime.getRuntime().availableProcessors();
	

	public IFractalProducerExecutorsImpl(ComplexRootedPolynomial rootedPolynomial, int workers, int tracks) {
		this.rootedPolynomial = rootedPolynomial;
		if (workers == 0) {
			this.workers = DEFAULT_WORKERS;
		} else {
			this.workers = workers;
		}
		if (tracks == 0) {
			this.tracks = DEFAULT_TRACKS;
		} else {
			this.tracks = tracks;
		}
	}

	@Override
	public void produce(double reMin, double reMax, double imMin, double imMax, int width, int height, long requestNo,
			IFractalResultObserver observer, AtomicBoolean cancel) {
		System.out.println("Zapocinjem izracun...");
		int m = 16 * 16 * 16;
		short[] data = new short[width * height];
		final int numberOfTracks = tracks > height ? height : tracks;
		int numberOfYByTrack = height / numberOfTracks;

		List<Future<?>> threadInfo = new ArrayList<>();

		for (int i = 0; i < numberOfTracks; i++) {
			int yMin = i * numberOfYByTrack;
			int yMax = (i + 1) * numberOfYByTrack - 1;
			if (i == numberOfTracks - 1) {
				yMax = height - 1;
			}

			Calculate job = new Calculate(reMin, reMax, imMin, imMax, width, height, yMin, yMax, m, data, cancel,
					rootedPolynomial);

			threadInfo.add(pool.submit(job));
		}

		for (Future<?> f : threadInfo) {
			while (true) {
				try {
					f.get();
					break;
				} catch (InterruptedException | ExecutionException e) {
				}
			}
		}

		System.out.println("Racunanje gotovo. Idem obavijestiti promatraca tj. GUI!");
		observer.acceptResult(data, (short) (rootedPolynomial.toComplexPolynom().order() + 1), requestNo);
	}

	@Override
	public void close() {
		pool.shutdown();
	}

	@Override
	public void setup() {
		pool = Executors.newFixedThreadPool(workers);
		System.out.println("workers: " + workers);
		System.out.println("tracks: " + tracks);
	}
}
