package oprpp2.hw02.client;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import oprpp2.hw02.client.gui.Chat;
import oprpp2.hw02.packetNames.Ack;
import oprpp2.hw02.packetNames.Bye;
import oprpp2.hw02.packetNames.Hello;
import oprpp2.hw02.packetNames.InMsg;
import oprpp2.hw02.packetNames.Message;

/**
 * Klasa predstavlja implementaciju klijenta za chat aplikaciju.
 * Komunikacija započinje slanjem {@link Hello} paketa te se onda nakon
 * primljenog {@link Ack} paketa otvara GUI iz kojeg se mogu slati i primati
 * poruke između spojenih korisnika.s
 * 
 * @author vedran
 *
 */
public class Main {
	private static DatagramSocket socket;
	private static int port;
	private static InetAddress address;
	private static long messageNumber;
	private static long messageRecievedNumber = 1;
	private static long UID;
	private static long randKey;
	private static String name;
	private static Chat chat;
	private static BlockingQueue<Ack> recievedAcks = new LinkedBlockingQueue<Ack>();	 

	/**
	 * Getter za redni broj poruke
	 * @return redni broj poruke
	 */
	public static long getMessageNumber() {
		return messageNumber;
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		if (args.length != 3) {
			System.out.println("There has to be 3 inputs(ip, port, name).");
			return;
		}

		try {
			address = InetAddress.getByName(args[0]);
		} catch (UnknownHostException e) {
			System.out.println("Cannot find a host: " + args[0]);
			return;
		}

		port = Integer.parseInt(args[1]);
		name = args[2].strip();

		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			System.out.println("Cannot open a socket!");
			return;
		}

		Random random = new Random();
		randKey = random.nextLong();

		Message msg = new Hello(messageNumber, name, randKey);
		int counter = 0;
		while (counter < 10) {
			counter++;
			DatagramPacket packet = msg.createDatagram(address, port);

			try {
				socket.send(packet);
			} catch (IOException e) {
				System.out.println("Cannot send the message.");
				break;
			}

			System.out.println("Sending: " + msg);
			
			byte[] buf = new byte[4000];
			DatagramPacket packet2 = new DatagramPacket(buf, buf.length);

			socket.setSoTimeout(5000);
			try {
				socket.receive(packet2);
			} catch (SocketTimeoutException ex2) {
				System.out.println("Timeout happend!");
				continue;
			}

			ByteArrayInputStream bis = new ByteArrayInputStream(packet2.getData(), packet2.getOffset(),
					packet2.getLength());
			DataInputStream dis = new DataInputStream(bis);

			byte code = 0;
			try {
				code = dis.readByte();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (code == Ack.getCode()) {
				Ack ack = new Ack();
				ack.deconstructDatagram(packet, dis);
				
				System.out.println("Recieved: " + ack);
				
				if(ack.getNumber() == 0) {
					UID = ack.getUID();
				} else 
					continue;
				
				chat = new Chat(name, UID/*, address, port, socket*/);
				SwingUtilities.invokeLater(() -> chat.setVisible(true));
				System.out.println("Connected. UID: " + UID);

				new Thread(() -> recieveMessage()).start();
//				recieveMessage(); jel ovo dovoljno ili da otvaram dretvu?
				messageNumber++;
				break;
			}
			if (counter == 9) {
				System.out.println("Could not connect!");
				socket.close();
				return;
			}

		}
	}

	/**
	 * Metoda u kojoj se izvodi usmjeravanje primljenog paketa od poslužitelja.
	 * Primljeni paketi moraju biti ili tipa {@link Ack} ili tipa {@link InMsg}, 
	 * inače se ignoriraju. Metoda je beskonačna petlja, ali završava kada se 
	 * zatvori pristupna točka.
	 */
	private static void recieveMessage() {
		while (true) {
			byte[] buf = new byte[4000];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);

			try {
				socket.setSoTimeout(0);
			} catch (SocketException e1) {
				if (socket.isClosed()) {
					System.out.println("Goodbye!");
					return;
				}
				continue;
			}
			
			try {
				socket.receive(packet);
			} catch (IOException e) {
				if (socket.isClosed()) {
					System.out.println("Goodbye!");
					return;
				}
				continue;
			}

			ByteArrayInputStream bis = new ByteArrayInputStream(packet.getData(), packet.getOffset(),
					packet.getLength());
			DataInputStream dis = new DataInputStream(bis);
			
			byte code = 0;
			try {
				code = dis.readByte();
			} catch (IOException e) {
			}

			if (code == Ack.getCode()) {
				recievedAck(packet, dis);
			} else if (code == InMsg.getCode()) {
				recievedInMsg(packet, dis);
			}
		}

	}

	/**
	 * Pomoćna metoda koja se bavi obradom primljenog {@link InMsg} paketa.
	 * Metoda ispisuje primljenu poruku na GUI i šalje ack paket prema poslužitelju
	 * @param packet primljeni paket
	 * @param dis {@link DataInputStream} stream za čitanje podataka u paketu
	 */
	private static void recievedInMsg(DatagramPacket packet, DataInputStream dis) {
		InMsg inMsg = new InMsg();
		inMsg.deconstructDatagram(packet, dis);

		System.out.println("Recieved: " + inMsg);
		
		if(inMsg.getNumber() == messageRecievedNumber) {
			chat.printToLabel(
					"[" + packet.getSocketAddress() + "] Message from: " + inMsg.getName() + "\n" + inMsg.getMessage());
			messageRecievedNumber++;
		}
		
		Message msg = new Ack(inMsg.getNumber(), UID);
		DatagramPacket packet2 = msg.createDatagram(address, port);

		try {
			socket.send(packet2);
		} catch (IOException e) {
			System.out.println("Cannot send the message.");
		}
		
		System.out.println("Sending: " + msg);
	}

	/**
	 * Pomoćna metoda koja se bavi obradom primljenog {@link Ack} paketa.
	 * Metoda gurne primljenu ack poruku u red primljenih ack poruka
	 * @param packet primljeni paket
	 * @param dis {@link DataInputStream} stream za čitanje podataka u paketu
	 */
	private static void recievedAck(DatagramPacket packet, DataInputStream dis) {
		Ack ack = new Ack();
		ack.deconstructDatagram(packet, dis);
		
		System.out.println("Recieved: " + ack);
		
		try {
			recievedAcks.put(ack);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Metoda koja služi za slanje poruke msg. Nakon što pošalje poruku čeka 5 sekundi 
	 * potvrdu poruke da se pojavi u redu, ako se ne pojavi događa se retransmisija. Nakon 10 retransmisija
	 * se pristupna točka zatvara jer se smatra da komunikacija nije moguća
	 * @param msg poruka koja se šalje
	 */
	public synchronized static void sendMessage(Message msg) {
		int counter = 0;
		while (counter < 10) {
			counter++;
			DatagramPacket packet = msg.createDatagram(address, port);

			try {
				socket.send(packet);
			} catch (IOException e) {
				System.out.println("Cannot send the message.");
				break;
			}
			System.out.println("Sending: " + msg);
			
			Ack ack = null;
			try {
				ack = recievedAcks.poll(5L, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				continue;
			}
			
			if(ack == null) // ako prode 5 sec i nista ne dode
				continue;
			
			if(ack.getNumber() != messageNumber) {
				System.out.println("Acknowledgement numbers are not equal! Recieved: " + ack.getNumber() + " Expected: " + messageNumber);
				continue;
			}
			
			if(msg instanceof Bye) {
				socket.close();
				return;
			}
			
			messageNumber++;
			return;			
		}
		System.out.println("Failed to send: " + msg);
		socket.close();
		messageNumber++;
	}
}
