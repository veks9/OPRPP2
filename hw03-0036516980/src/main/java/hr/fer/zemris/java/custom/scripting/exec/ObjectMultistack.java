package hr.fer.zemris.java.custom.scripting.exec;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ObjectMultistack {
	private Map<String, MultistackEntry> map;

	public ObjectMultistack() {
		map = new HashMap<>();
	}

	public void push(String keyName, ValueWrapper valueWrapper) {
		Objects.requireNonNull(keyName, "Key can't be null!");

		map.put(keyName, new MultistackEntry(valueWrapper, map.get(keyName)));
	}

	public ValueWrapper pop(String keyName) {
		ValueWrapper value = peek(keyName);
		MultistackEntry entry = map.get(keyName).next;
		map.remove(keyName);
		map.put(keyName, entry);
		return value;
	}

	public ValueWrapper peek(String keyName) {
		Objects.requireNonNull(keyName, "Key can't be null!");
		
		if(!map.containsKey(keyName)) {
			throw new EmptyStackException();
		}
		
		return map.get(keyName).data;
	}

	public boolean isEmpty(String keyName) {
		Objects.requireNonNull(keyName, "Key can't be null!");
		
		return map.get(keyName) == null;
	}

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