package hr.fer.zemris.java.custom.scripting.exec;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Klasa predstavlja adapter na mapu koja kao ključ ima string, a kao vrijednost stog u 
 * koji se pohranjuju {@link MultistackEntry}
 * @author vedran
 *
 */
public class ObjectMultistack {
	private Map<String, MultistackEntry> map;

	public ObjectMultistack() {
		map = new HashMap<>();
	}

	/**
	 * Metoda dodaje na stog ključa keyName vrijednost valueWrapper. Kreira se novi
	 * {@link MultistackEntry} i dodaje se na vrh stoga
	 * @param keyName ključ na čiji stog se želi dodati vrijednost valueWrapper
	 * @param valueWrapper vrijednost 
	 */
	public void push(String keyName, ValueWrapper valueWrapper) {
		Objects.requireNonNull(keyName, "Key can't be null!");

		map.put(keyName, new MultistackEntry(valueWrapper, map.get(keyName)));
	}

	/**
	 * Metoda miče prvi element sa stoga pod ključem keyName i vraća vrijednost koja je maknuta
	 * @param keyName ključ čiji se element sa stoga želi maknuti
	 * @return prvi element sa stoga
	 */
	public ValueWrapper pop(String keyName) {
		ValueWrapper value = peek(keyName);
		MultistackEntry entry = map.get(keyName).next;
		map.remove(keyName);
		map.put(keyName, entry);
		return value;
	}

	/**
	 * Metoda vraća, ali ne miče prvi element sa stoga pod ključem keyName
	 * @param keyName ključ čiji se element sa stoga želi vidjeti
	 * @return prvi element sa stoga
	 */
	public ValueWrapper peek(String keyName) {
		Objects.requireNonNull(keyName, "Key can't be null!");
		
		if(!map.containsKey(keyName)) {
			throw new EmptyStackException();
		}
		
		return map.get(keyName).data;
	}

	/**
	 * Metoda vraća <code>true</code> ako je stog na ključu keyName prazan, inače <code>false</code>
	 * @param keyName ključ čiji se stog želi ispitati je li prazan
	 * @return <code>true</code> ako je stog na ključu keyName prazan, inače <code>false</code>
	 */
	public boolean isEmpty(String keyName) {
		Objects.requireNonNull(keyName, "Key can't be null!");
		
		return map.get(keyName) == null;
	}

	/**
	 * Klasa predstavlja implementaciju vrijednosti koja se ponaša kao node u jednostruko
	 * povezanoj listi. Služi nam kako bi simulirali stog u mapi
	 * @author vedran
	 *
	 */
	private static class MultistackEntry {
		private ValueWrapper data;
		private MultistackEntry next;

		public MultistackEntry(ValueWrapper data, MultistackEntry next) {
			super();
			this.data = data;
			this.next = next;
		}

		public MultistackEntry(ValueWrapper data) {
			this(data, null);
		}
	}
}