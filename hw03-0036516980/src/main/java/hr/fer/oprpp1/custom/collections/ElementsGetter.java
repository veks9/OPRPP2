package hr.fer.oprpp1.custom.collections;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/**
 * Predstavlja sučelje koje dohvaća elemente
 * 
 * @author vedran
 *
 */
public interface ElementsGetter {
	/**
	 * Metoda koja ispituje postoji li idući element u kolekciji
	 * 
	 * @return <code>true</code> ako postoji, inače <code>false</code>
	 * @throws ConcurrentModificationException ako se polje promijeni dok
	 *                                         {@link ElementsGetter} radi po njemu
	 */
	boolean hasNextElement();

	/**
	 * Metoda koja dohvaća idući element u kolekciji
	 * 
	 * @return idući element u kolekciji
	 * @throwsConcurrentModificationException ako se polje promijeni dok
	 *                                        {@link ElementsGetter} radi po njemu
	 * @throws NoSuchElementException ako se pokuša dohvatiti idući element koji je
	 *                                <code>null</code>
	 */
	Object getNextElement();

	/**
	 * Metoda koja nad svim preostalim elementima kolekcije poziva zadani procesor
	 * 
	 * @param p zadani procesor
	 */
	default void processRemaining(Processor p) {
		while (hasNextElement())
			p.process(getNextElement());
	}
}
