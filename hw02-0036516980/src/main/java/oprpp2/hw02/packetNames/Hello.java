package oprpp2.hw02.packetNames;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Klasa je implementacija Hello poruke u chat aplikaciji.
 * Koristi se kao poruka prijave. Kada se klijent uključi,
 * šalje se poruka hello kako bi se dalo poslužitelju 
 * do znanja da se novi klijent spojio.
 * @author vedran
 *
 */
public class Hello extends Message{
	
	private final static byte code = 1;
	private String name;
	private long randKey;
	
	public Hello(long number, String name, long randKey) {
		super(number);
		this.name = name;
		this.randKey = randKey;
	}
	public Hello() {
	
	}

	/**
	 * Getter za jedinstveni kod poruke
	 * @return jedinstveni kod poruke
	 */
	public static byte getCode() {
		return code;
	}
	
	/**
	 * Getter za ime korisnika koji je poslao poruku
	 * @return ime korisnika koji je poslao poruku
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter za slučajno generirani ključ sa klijentske strane.
	 * @return slučajno generirani ključ sa klijentske strane
	 */
	public long getRandKey() {
		return randKey;
	}
	
	@Override
	public DatagramPacket createDatagram(InetAddress address, int port) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeByte(getCode());
			dos.writeLong(getNumber());
			dos.writeUTF(name);
			dos.writeLong(randKey);
			dos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] buf = bos.toByteArray();
		
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		packet.setAddress(address);
		packet.setPort(port);
		return packet;
	}
	
	@Override
	public String toString() {
		return "HELLO(" + getNumber() + ", name=\"" + name 
				+ "\", randkey=\"" + randKey + "\")";
	}

	@Override
	public void deconstructDatagram(DatagramPacket packet, DataInputStream dis) {
		try {
			setNumber(dis.readLong());
			name = dis.readUTF();
			randKey = dis.readLong();
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
}
