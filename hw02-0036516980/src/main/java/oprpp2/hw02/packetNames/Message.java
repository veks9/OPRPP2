package oprpp2.hw02.packetNames;

import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Klasa predstavlja nadklasu svim porukama koje se šalju u chat aplikaciji.
 * @author vedran
 *
 */
public abstract class Message {
	private long number;	

	/**
	 * Getter za redni broj poruke
	 * @return redni broj poruke
	 */
	public long getNumber() {
		return number;
	}
	
	/**
	 * Setter za redni broj poruke
	 * @param number redni broj poruke
	 */
	void setNumber(long number) {
		this.number = number;
	}

	public Message(long number) {
		super();
		this.number = number;
	}
	
	Message() {
	
	}

	/**
	 * Metoda radi {@link DatagramPacket} tako da iz poruke nad kojom je 
	 * pozvana izvuče podatke te podesi adresu i port koji se predaju kao argumenti
	 * @param address ip adresa odredišta
	 * @param port port odredišta 
	 * @return novi paket
	 */
	public abstract DatagramPacket createDatagram(InetAddress address, int port);

	/**
	 * Metoda prima {@link DatagramPacket} i {@link DataInputStream} te 
	 * učita podatke iz njega u objekt nad kojim je pozvana metoda
	 * @param packet paket koji se želi analizirati
	 * @param dis stream za čitanje podataka
	 */
	public abstract void deconstructDatagram(DatagramPacket packet, DataInputStream dis);
	
}
