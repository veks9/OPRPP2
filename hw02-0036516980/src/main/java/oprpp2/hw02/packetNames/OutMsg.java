package oprpp2.hw02.packetNames;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class OutMsg extends Message{
	
	private static byte code = 4;
	private long UID;
	private String message;
	
	public OutMsg(long number, long UID, String message) {
		super(number);
		this.UID = UID;
		this.message = message;
	}
	
	public OutMsg() {
	
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
			dos.writeLong(UID);
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
		return "OUTMESSAGE("+ getNumber() + ", uid=" + UID + 
				", text=\"" + message +  "\"" + ")";
	}

	@Override
	public void deconstructDatagram(DatagramPacket packet, DataInputStream dis) {
		try {
			setNumber(dis.readLong());			
			UID = dis.readLong();
			message = dis.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
}
