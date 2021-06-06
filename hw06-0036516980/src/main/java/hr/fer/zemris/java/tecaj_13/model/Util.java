package hr.fer.zemris.java.tecaj_13.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
	private static final String[] hex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e",
			"f" };
	private static final String ALGORITHM = "sha-1";

	
	public static String getHashOfPassword(String pswd) {
		try {
			MessageDigest md = MessageDigest.getInstance(ALGORITHM);
			byte[] bytes = pswd.getBytes();
			md.update(bytes, 0, bytes.length);
			byte hash[] = md.digest();
			return byteToHex(hash);
		} catch (NoSuchAlgorithmException ignorable) {
		}
		return null;
	}
	/**
	 * Metoda prima String koji je u heksadekadskom zapisu i vraća polje bajtova.
	 * 
	 * @param keyText String u heksadekadskom zapisu
	 * @return polje bajtova koje reprezentira taj ulazni String
	 * @throws NullPointerException     ako je ulazni tekst <code>null</code>
	 * @throws IllegalArgumentException ako je duljina neparna ili su nedozvoljeni
	 *                                  znakovi u Stringu
	 */
	public static byte[] hexToByte(String keyText) {
		if (keyText == null)
			throw new NullPointerException("Ulazni tekst ne smije biti null!");
		if (keyText.length() % 2 != 0)
			throw new IllegalArgumentException("Duljina ulaznog teksta mora biti parni broj!");
		if (!isValid(keyText))
			throw new IllegalArgumentException("Nedozvoljeni znakovi u ulaznom tekstu!");

		int length = keyText.length();
		byte[] byteArray = new byte[length / 2];

		for (int i = 0; i < length; i += 2) {
			String first = keyText.substring(i, i + 1).toLowerCase();
			String second = keyText.substring(i + 1, i + 2).toLowerCase();

			byte firstByte = getByteFromString(first);
			byte secondByte = getByteFromString(second);

			byteArray[i / 2] = (byte) ((firstByte << 4) + secondByte);
		}

		return byteArray;
	}

	/**
	 * Metoda prima String i vraća njegovu reprezentaciju u bajtovima
	 * 
	 * @param input znak koji se obrađuje
	 * @return reprezentacija ulaznog Stringa u bajtovima
	 */
	private static byte getByteFromString(String input) {
		for (int i = 0; i < hex.length; i++) {
			if (hex[i].equals(input)) {
				return (byte) i;
			}
		}
		return -1;
	}

	/**
	 * Metoda ispituje jesu li u ulaznom Stringu samo dozvoljeni znakovi(0...9 i
	 * a...f)
	 * 
	 * @param keyText ulazni String
	 * @return <code>true</code> ako jesu, inače <code>false</code>
	 */
	private static boolean isValid(String keyText) {
		for (char c : keyText.toCharArray()) {
			c = Character.toLowerCase(c);
			if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f'))) {
				return false;
			}
		}
		return true;

	}

	/**
	 * Metoda prima polje bajtova i vraća njegovu reprezentaciju u obliku Stringa
	 * 
	 * @param byteArray ulazno polje bajtova
	 * @return reprezentacija polja bajtova u obliku Stringa
	 * @throws NullPointerException ako je ulazno polje <code>null</code>
	 */
	public static String byteToHex(byte[] byteArray) {
		if (byteArray == null)
			throw new NullPointerException("Ulazno polje ne smije biti null!");

		String s = "";
		for (int i = 0; i < byteArray.length; i++) {
			int v = byteArray[i] & 0xFF;
			s += hex[v >>> 4] + hex[v & 0x0F];
		}
		return s;
	}
}
