package hr.fer.zemris.java.fractals;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import hr.fer.zemris.java.fractals.viewer.FractalViewer;
import hr.fer.zemris.java.math.Complex;
import hr.fer.zemris.java.math.ComplexRootedPolynomial;

/**
 * Klasa predstavlja ulaznu točku za program koji iscrtava
 * fraktale uz pomoć višedretvenosti i ForkJoinPool-a
 * @author vedran
 *
 */
public class NewtonP2 {
	
	public static void main(String[] args) {

		int mintracks = parseArgs(args);
		
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

		FractalViewer.show(new IFractalProducerForkJoinImpl(rootedPolynomial, mintracks));

	}

	/**
	 * Pomoćna metoda koja parsira argumente predane pri pokretanju programa
	 * @param args argumenti predani pri pokretanju programa
	 * @return polje intova parsiranih iz argumenata programa
	 */
	private static int parseArgs(String[] args) {
		if (args.length == 0)
			return 0;

		int mintracks = 0;

		if (args[0].startsWith("--")) {
			String[] arr = args[0].split("=");
			if (arr[0].equalsIgnoreCase("--mintracks")) {
				mintracks = Integer.parseInt(arr[1]);
			} else {
				throw new IllegalArgumentException("Arguments are not valid!");
			}
		} else if (args[0].startsWith("-")) {
			if (args[0].equalsIgnoreCase("-m")) {
				mintracks = Integer.parseInt(args[1]);
			} else {
				throw new IllegalArgumentException("Arguments are not valid!");
			}
		} else {
			throw new IllegalArgumentException("Arguments are not valid!");
		}
		
		if(mintracks <= 0)
			throw new IllegalArgumentException("Mintracks has to be a positive number");
		return mintracks;
	}
}
