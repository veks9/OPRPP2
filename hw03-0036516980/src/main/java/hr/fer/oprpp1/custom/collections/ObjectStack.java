package hr.fer.oprpp1.custom.collections;

/**
 * Klasa predstavlja implementaciju stoga uz pomoć polja
 * 
 * @author vedran
 *
 */
public class ObjectStack {
	private ArrayIndexedCollection arrIndxCol = new ArrayIndexedCollection();

	/**
	 * Metoda provjerava je li stog prazan
	 * 
	 * @return <code>true</code> ako je prazan inače <code>false</code>
	 */
	public boolean isEmpty() {
		return arrIndxCol.isEmpty();
	}

	/**
	 * Metoda vraća broj objekata na stogu
	 * 
	 * @return broj objekata na stogu
	 */
	public int size() {
		return arrIndxCol.size();
	}

	/**
	 * Metoda stavlja objekt na stog
	 * 
	 * @param value objekt koji se stavlja na stog
	 */
	public void push(Object value) {
		arrIndxCol.add(value);
	}

	/**
	 * Metoda skida objekt sa stoga
	 * 
	 * @return Objekt koji je skinut sa stoga
	 */
	public Object pop() {
		if (size() == 0)
			throw new EmptyStackException("Stog je prazan!");

		Object ret = arrIndxCol.get(size() - 1);
		arrIndxCol.remove(size() - 1);
		return ret;
	}

	/**
	 * Metoda pogleda vraća zadnji objekt sa stoga, ali ga ne skine sa stoga
	 * 
	 * @return zadnji objekt na stogu
	 */
	public Object peek() {
		if (size() == 0)
			throw new EmptyStackException("Stog je prazan!");

		return arrIndxCol.get(size() - 1);
	}

	/**
	 * Metoda prazni stog
	 */
	public void clear() {
		arrIndxCol.clear();
	}

}
