package hr.fer.oprpp1.custom.collections;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/**
 * Implementacija kolekcije koja ima u pozadini polje promjenjive veličine.
 * Duplikati su dopušteni, ali <code>null</code> vrijednosti nisu
 * 
 * @author vedran
 *
 */
public class ArrayIndexedCollection implements List {

	private static final int DEFAULT_INITIAL_CAPACITY = 16;
	private int size; // broj elemenata uistinu pohranjen
	private Object[] elements;
	private long modificationCount = 0L;

	/**
	 * Klasa koja nasljeđuje {@link ElementsGetter} i zadaća joj je vraćati element
	 * po element na korisnikov zahtjev
	 * 
	 * @author vedran
	 *
	 */
	private static class ArrayElementsGetter implements ElementsGetter {
		private int index;
		ArrayIndexedCollection col;
		private long savedModificationCount;

		public ArrayElementsGetter(ArrayIndexedCollection arrayCol, long count) {
			col = arrayCol;
			savedModificationCount = count;
		}

		@Override
		public boolean hasNextElement() {
			if (col.modificationCount != savedModificationCount)
				throw new ConcurrentModificationException("Kolekcija je mijenjana!");

			if (index < col.size())
				return true;
			else
				return false;
		}

		@Override
		public Object getNextElement() {
			if (col.modificationCount != savedModificationCount)
				throw new ConcurrentModificationException("Kolekcija je mijenjana!");
			if (col.elements[index] == null)
				throw new NoSuchElementException("Ne postoji idući element u kolekciji");

			return col.elements[index++];
		}
	}

	/**
	 * Pretpostavljeni konstruktor koji radi polje elemenata kapaciteta 16
	 */
	public ArrayIndexedCollection() {
		this(DEFAULT_INITIAL_CAPACITY);
	}

	/**
	 * Konstruktor koji prima kapacitet i radi polje elemenata tog kapaciteta
	 * 
	 * @param initialCapacity kapacitet polja kolekcije
	 * @throws IllegalArgumentException ako je predani kapacitet manji od 1
	 */
	public ArrayIndexedCollection(int initialCapacity) {
		if (initialCapacity < 1)
			throw new IllegalArgumentException("Kapacitet ne smije biti manji od 1!!!");

		size = 0;
		this.elements = new Object[initialCapacity];
	}

	/**
	 * Konstruktor koji prima ne-<code>null</code> referencu na
	 * <code>Collection</code> čiji se elementi kopiraju u novo-napravljenu
	 * kolekciju
	 * 
	 * @param collection kolekcija čiji se elementi žele kopirati u novo-napravljenu
	 *                   kolekciju
	 * @throws NullPointerException ako je predana kolekcija <code>null</code>
	 */
	public ArrayIndexedCollection(Collection collection) {
		this(collection, collection.size());
	}

	/**
	 * Konstruktor koji prima ne-<code>null</code> referencu na
	 * <code>Collection</code> čiji se elementi kopiraju u novo-napravljenu
	 * kolekciju. Ako je <code>initialCapacity</code> manji od veličine kolekcije iz
	 * koje se kopira, onda se koristi veličina kolekcije iz koje se kopira kao
	 * veličina novo-napravljene kolekcije, a inače se koristi
	 * <code>initialCapacity</code>
	 * 
	 * @param collection      kolekcija čiji se elementi žele kopirati u
	 *                        novo-napravljenu kolekciju
	 * @param initialCapacity kapacitet polja kolekcije
	 */
	public ArrayIndexedCollection(Collection collection, int initialCapacity) {
		if (collection == null)
			throw new NullPointerException();

		if (initialCapacity < collection.size()) {
			this.elements = new Object[collection.size()];
			addAll(collection);
		} else {
			this.elements = new Object[initialCapacity];
			addAll(collection);
		}
		size = collection.size();
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void add(Object value) {
		if (value == null)
			throw new NullPointerException();

		ensureCapacity();

		insert(value, size);
	}

	/**
	 * Metoda povećava kapacitet polja za duplo ako je <code>size == capacity</code>
	 */
	private void ensureCapacity() {
		if (elements.length == size) {
			int capacity = elements.length;
			capacity *= 2;
			elements = Arrays.copyOf(elements, capacity);
		}
	}

	@Override
	public boolean contains(Object value) {
		if (indexOf(value) < 0)
			return false;
		else
			return true;
	}

	@Override
	public boolean remove(Object value) {
		int index = indexOf(value);
		if (index >= 0) {
			remove(index);
			return true;
		}
		return false;
	}

	@Override
	public Object[] toArray() {
		return Arrays.copyOf(elements, size);

	}

	@Override
	public void clear() {
		for (int i = 0; i < size; i++) {
			elements[i] = null;
		}
		size = 0;
		modificationCount++;
	}

	@Override
	public Object get(int index) {
		if (index < 0 || index > size - 1)
			throw new IndexOutOfBoundsException("Predani indeks je izvan dopuštenog intervala!");

		return elements[index];

	}

	@Override
	public void insert(Object value, int position) {
		if (value == null)
			throw new NullPointerException();
		if (position < 0 || position > size)
			throw new IndexOutOfBoundsException("Predana pozicija je izvan dopuštenog intervala!");

		ensureCapacity();

		for (int i = size - 1; i >= position; i--) {
			elements[i + 1] = elements[i];
		}

		elements[position] = value;
		size++;
		modificationCount++;
	}

	@Override
	public int indexOf(Object value) {
		if (value == null)
			return -1;

		for (int i = 0; i < size; i++) {
			if (elements[i].equals(value))
				return i;
		}
		return -1;
	}

	@Override
	public void remove(int index) {
		if (index < 0 || index > size - 1)
			throw new IndexOutOfBoundsException("Predani indeks je izvan dopuštenog intervala!");

		for (int i = index; i < size - 1; i++) {
			elements[i] = elements[i + 1];
		}

		elements[(size - 1)] = null;
		size--;
		modificationCount++;

	}

	/**
	 * Metoda koja stvara i vraća novi objekt tipa {@link ElementsGetter}
	 * 
	 * @return novi objekt tipa {@link ElementsGetter}
	 */
	public ElementsGetter createElementsGetter() {
		return new ArrayElementsGetter(this, modificationCount);
	}
}
