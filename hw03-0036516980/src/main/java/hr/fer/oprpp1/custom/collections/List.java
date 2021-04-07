package hr.fer.oprpp1.custom.collections;

public interface List extends Collection {
	/**
	 * Vraća objekt koji je pohranjen na predanom <code>index</code>-u. Valjani
	 * indexi su iz intervala 0 do <code>size</code>-1
	 * 
	 * @param index index čiji se objekt traži
	 * @return objekt koji je na poziciji <code>index</code>
	 * @throws IndexOutOfBoundsException ako je <code>index</code> nevaljan
	 */
	Object get(int index);

	/**
	 * Metoda inserta <code>value</code> na <code>position</code> u polje. Ne
	 * overwrita nego pomakne sve od pozicije na koju se želi insertati udesno.
	 * Legalne pozicije su iz intervala od 0 do <code>size</code>
	 * 
	 * @param value    objekt koji se želi insertati na određenu poziciju u polju
	 * @param position pozicija na koju se objekt želi insertati
	 * @throws IndexOutOfBoundsException ako je <code>position</code> nevaljan
	 */
	void insert(Object value, int position);

	/**
	 * 
	 * @param value objekt čiji se index traži
	 * @return index prvog pojavljivanja predanog objekta ili -1 ako nije nađen(ili
	 *         <code>null</code>)
	 */
	int indexOf(Object value);

	/**
	 * Uklanja element iz kolekcije na predanom indeksu. Element koji je bio na
	 * lokaciji <code>index</code>+1 se nakon ove operacije prebacuje na
	 * <code>index</code> itd. Indeksi koji se mogu predati su iz intervala od 0 do
	 * <code>size</code>-1
	 * 
	 * @param index indeks elementa koji se uklanja iz kolekcije
	 * @throws IndexOutOfBoundsException ako je predan indeks izvan dopuštenog
	 *                                   intervala
	 */
	void remove(int index);
}
