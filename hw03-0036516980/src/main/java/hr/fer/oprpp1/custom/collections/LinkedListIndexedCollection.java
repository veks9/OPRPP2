package hr.fer.oprpp1.custom.collections;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/**
 * Implementacija kolekcije koja ima u pozadini povezanu listu. Duplikati su
 * dopušteni, ali <code>null</code> vrijednosti nisu
 * 
 * @author vedran
 *
 */
public class LinkedListIndexedCollection implements List {

	private int size; // broj elemenata uistinu pohranjen, broj nodeova u listi
	private ListNode first;
	private ListNode last;
	private long modificationCount = 0L;

	/**
	 * Jedan čvor povezane liste. Sastoji se od reference na idući, reference na
	 * prošli član i podataka
	 * 
	 * @author vedran
	 *
	 */
	private static class ListNode {
		private ListNode previous;
		private ListNode next;
		private Object data;

		ListNode(Object data) {
			this.data = data;
		}
	}

	/**
	 * Klasa koja nasljeđuje {@link ElementsGetter} i zadaća joj je vraćati element
	 * po element na korisnikov zahtjev
	 * 
	 * @author vedran
	 *
	 */
	private static class LinkedListElementsGetter implements ElementsGetter {
		private ListNode node;
		private LinkedListIndexedCollection col;
		private long savedModificationCount;

		public LinkedListElementsGetter(LinkedListIndexedCollection linkedListCol, long count) {
			col = linkedListCol;
			node = col.first;
			savedModificationCount = count;
		}

		@Override
		public boolean hasNextElement() {
			if (col.modificationCount != savedModificationCount)
				throw new ConcurrentModificationException("Kolekcija je mijenjana!");

			if (node == null)
				return false;
			else
				return true;
		}

		@Override
		public Object getNextElement() {
			if (node == null)
				throw new NoSuchElementException("Ne postoji idući objekt");
			if (col.modificationCount != savedModificationCount)
				throw new ConcurrentModificationException("Kolekcija je mijenjana!");

			Object ret = node.data;
			node = node.next;
			return ret;
		}
	}

	/**
	 * Pretpostavljeni konstruktor
	 */
	public LinkedListIndexedCollection() {
		super();
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
	public LinkedListIndexedCollection(Collection collection) {
		if (collection == null)
			throw new NullPointerException();

		addAll(collection);
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

		ListNode newListNode = new ListNode(value);

		if (first == null) {
			first = newListNode;
			last = newListNode;
			size++;
			return;
		}

		last.next = newListNode;
		newListNode.previous = last;
		last = newListNode;
		size++;
		modificationCount++;
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
		Object[] array = new Object[size];
		ListNode node = first;

		for (int i = 0; i < size; i++) {
			array[i] = node.data;
			node = node.next;
		}
		return array;
	}

	@Override
	public void clear() {
		first = null;
		last = null;
		size = 0;
		modificationCount++;
	}

	@Override
	public Object get(int index) {
		if (index < 0 || index > size - 1)
			throw new IndexOutOfBoundsException("Predani indeks je izvan dopuštenog intervala!");

		if (index == 0)
			return first.data;
		if (index == size - 1)
			return last.data;

		int halfSize = size / 2;
		if (index <= halfSize) {
			int n = 0;
			ListNode node = first;
			while (n <= halfSize) {
				if (n == halfSize)
					return node.data;
				else {
					node = node.next;
					n++;
				}
			}
		} else {
			int n = size - 1;
			ListNode node = last;
			while (n > halfSize) {
				if (n == halfSize) {
					return node.data;
				} else {
					node = node.previous;
					n--;
				}
			}
		}
		return null;
	}

	@Override
	public void insert(Object value, int position) {
		if (value == null)
			throw new NullPointerException();
		if (position < 0 || position > size)
			throw new IndexOutOfBoundsException("Predana pozicija je izvan dopuštenog intervala!");

		ListNode node = first;
		for (int i = 0; i < size; i++) {
			if (position == 0) {
				ListNode newListNode = new ListNode(value);

				node.previous = newListNode;
				newListNode.next = node;

				first = newListNode;
				size++;
				modificationCount++;

				break;
			} else if (position == size) {
				add(value);

				break;
			} else if (i == position) {
				ListNode prevNode = node.previous;
				ListNode newListNode = new ListNode(value);

				prevNode.next = newListNode;
				newListNode.previous = prevNode;
				newListNode.next = node;
				node.previous = newListNode;
				size++;
				modificationCount++;

				break;
			}
			node = node.next;
		}
	}

	@Override
	public int indexOf(Object value) {
		if (value == null)
			return -1;

		ListNode node = first;
		for (int i = 0; i < size; i++) {
			if (value.equals(node.data)) {
				return i;
			}
			node = node.next;
		}
		return -1;
	}

	@Override
	public void remove(int index) {
		if (index < 0 || index > size - 1)
			throw new IndexOutOfBoundsException("Predani indeks je izvan dopuštenog intervala!");

		ListNode node = first;
		for (int i = 0; i < size; i++) {
			if (index == 0) {
				ListNode nextNode = node.next;

				nextNode.previous = null;
				first = nextNode;

				break;
			} else if (index == size - 1) {
				node = last;
				ListNode prevNode = node.previous;

				prevNode.next = null;
				last = prevNode;

				break;
			} else if (i == index) {
				ListNode prevNode = node.previous;
				ListNode nextNode = node.next;

				prevNode.next = nextNode;
				nextNode.previous = prevNode;

				break;
			}
			node = node.next;
		}
		size--;
		modificationCount++;
	}

	@Override
	public ElementsGetter createElementsGetter() {
		return new LinkedListElementsGetter(this, modificationCount);
	}

}
