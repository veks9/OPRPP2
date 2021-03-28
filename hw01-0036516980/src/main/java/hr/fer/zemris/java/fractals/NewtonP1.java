package hr.fer.zemris.java.fractals;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import hr.fer.zemris.java.fractals.viewer.FractalViewer;
import hr.fer.zemris.java.math.Complex;
import hr.fer.zemris.java.math.ComplexRootedPolynomial;

/**
 * Klasa predstavlja ulaznu točku za program koji iscrtava
 * fraktale uz pomoć višedretvenosti i okruženja Executors
 * @author vedran
 *
 */
public class NewtonP1 {

	public static void main(String[] args) {

		int[] workerTracks = parseArgs(args);
		
		System.out.println("Welcome to Newton-Raphson iteration-based fractal viewer.");
		System.out.println("Please enter at least two roots, one root per line. Enter 'done' when done.");
		Scanner sc = new Scanner(System.in);

		int counter = 1;
		System.out.print("Root " + counter + "> ");
		List<Complex> list = new ArrayList<>();
		while (sc.hasNext()) {
			String input = sc.nextLine().trim();
			if (input.equalsIgnoreCase("done")) {
				if (counter < 2) {
					System.out.println("Please enter at least two roots! The last entry is deleted.");
					list.clear();
					counter = 0;
					continue;
				}
				System.out.println("Image of fractal will appear shortly. Thank you.");
				break;
			}

			Complex z = Complex.parse(input);
			list.add(z);
			System.out.print("Root " + ++counter + "> ");
		}
		sc.close();

		Complex[] arr = new Complex[list.size()];
		for (int i = 0; i < list.size(); i++) {
			arr[i] = list.get(i);
		}

		ComplexRootedPolynomial rootedPolynomial = new ComplexRootedPolynomial(Complex.ONE, arr);

		FractalViewer.show(new IFractalProducerExecutorsImpl(rootedPolynomial, workerTracks[0], workerTracks[1]));

	}

	/**
	 * Pomoćna metoda koja parsira argumente predane pri pokretanju programa
	 * @param args argumenti predani pri pokretanju programa
	 * @return polje intova parsiranih iz argumenata programa
	 */
	private static int[] parseArgs(String[] args) {
		if (args.length == 0)
			return new int[] { 0, 0 };
		
		int index = 0;
		int[] retArray = new int[2];
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("--")) {
				String[] arr = args[i].split("=");
				switch(arr[0]) {
					case "--workers":
						index = 0;
						break;
					case "--tracks":
						index = 1;
						break;
					default:
						throw new IllegalArgumentException("Arguments are not valid!");
				}
				retArray[index] = Integer.parseInt(arr[1]);
			} else if (args[i].startsWith("-")) {
				
				switch(args[i]) {
				case "-w":
					index = 0;
					break;
				case "-t":
					index = 1;
					break;
				default:
					throw new IllegalArgumentException("Arguments are not valid!");
			}
				retArray[index] = Integer.parseInt(args[i + 1]);
				if(retArray[index] <= 0)
					throw new IllegalArgumentException("Inputs must be greather than or equals 1");
				i++;
			} else {
				throw new IllegalArgumentException("Arguments are not valid!");
			}
		}
		return retArray;
	}

	

}

