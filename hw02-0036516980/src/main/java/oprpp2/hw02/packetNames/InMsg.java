package oprpp2.hw02.packetNames;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Klasa je implementacija InMessage poruke u chat aplikaciji.
 * Koristi ju poslužitelj za broadcast svim spojenim klijentima 
 * poruku koji je neki klijent poslao.
 * @author vedran
 *
 */
public class InMsg extends Message{
	
	private static byte code = 5;
	private String name;
	private String message;
	
	public InMsg(long number, String name, String message) {
		super(number);
		this.name = name;
		this.message = message;
	}
	
	public InMsg() {
		super();
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
	 * Getter poruke koja je poslana 
	 * @return poruka koja je poslana
	 */
	public String getMessage() {
		return message;
	}
	
	@Override
	public DatagramPacket createDatagram(InetAddress address, int port) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeByte(getCode());
			dos.writeLong(getNumber());
			dos.writeUTF(name);
			dos.writeUTF(message);
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
		return "INMESSAGE(" + getNumber() + ", name=\"" + name 
				+ "\", text=\"" + message + "\")";
	}

	@Override
	public void deconstructDatagram(DatagramPacket packet, DataInputStream dis) {
		try {
			setNumber(dis.readLong());			
			name = dis.readUTF();
			message = dis.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
}
