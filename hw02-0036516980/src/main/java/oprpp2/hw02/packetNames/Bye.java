package oprpp2.hw02.packetNames;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Klasa je implementacija Bye poruke u chat aplikaciji.
 * Koristi se kao poruka za odjavu klijenta koja se šalje od klijenta 
 * do poslužitelja kada se zatvara char prozor.
 * @author vedran
 *
 */
public class Bye extends Message{

	private static byte code = 3;
	private long UID;
	
	public Bye(long number, long UID) {
		super(number);
		this.UID = UID;
	}
	
	public Bye() {
	
	}
	
	/**
	 * Getter za jedinstveni kod poruke
	 * @return jedinstveni kod poruke
	 */
	public static byte getCode() {
		return code;
	}
	
	/**
	 * Getter za jedinstveni identifikator koji je napravio poruku 
	 * @return jedinstveni identifikator koji je napravio poruku
	 */
	public long getUID() {
		return UID;
	}
	
	@Override
	public DatagramPacket createDatagram(InetAddress address, int port) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeByte(getCode());
			dos.writeLong(getNumber());
			dos.writeLong(UID);
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
		return "BYE(" + getNumber() + ", uid=" + UID + ")";
	}

	@Override
	public void deconstructDatagram(DatagramPacket packet, DataInputStream dis) {
		try {
			setNumber(dis.readLong());
			UID = dis.readLong();
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
}
