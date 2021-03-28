package oprpp2.hw02.server;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import oprpp2.hw02.packetNames.Ack;
import oprpp2.hw02.packetNames.Bye;
import oprpp2.hw02.packetNames.Hello;
import oprpp2.hw02.packetNames.InMsg;
import oprpp2.hw02.packetNames.Message;
import oprpp2.hw02.packetNames.OutMsg;

/**
 * Klasa predstavlja implementaciju poslužitelja za chat aplikaciju ostvarenu
 * preko UDP protokola. Poslužitelju je 
 * potrebno predati port kroz naredbeni redak pri pokretanju programa. U svojoj
 * glavnoj dretvi poslužitelj čeka na UDP paket, raspakira podatke te shodno poruci
 * koja se nalazi u paketu odlučuje kako postupiti.
 * @author vedran
 *
 */
public class Main {

	private static int port;
	private static DatagramSocket socket;
	private static long clientUIDs;
	private static List<ClientData> connectedClients = new ArrayList<>();

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("There has to be 1 input(port).");
			return;
		}

		try {
			port = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			System.out.println("Not a number: " + args[0]);
		}

		if (port < 1 || port > 65535) {
			System.out.println("Port has to be a number between 1 and 65535.");
			return;
		}

		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			System.out.println("Cannot open a socket!");
			return;
		}

		System.out.println("Server is on port: " + port);
		Random random = new Random();
		clientUIDs = random.nextLong();

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
				e.printStackTrace();
			}

			if (code == Hello.getCode()) {
				recievedHello(packet, dis);
			} else if (code == Ack.getCode()) {
				recievedAck(packet, dis);
			} else if (code == Bye.getCode()) {
				recievedBye(packet, dis);
			} else if (code == OutMsg.getCode()) {
				recievedOutMsg(packet, dis);
			} else {
				System.out.println("Wrong message code. Ignoring packet...");
			}
		}

	}

	/**
	 * Pomoćna metoda koja obrađuje slučaj kad se primi UDP datagram u kojem
	 * se nalazi {@link OutMsg} poruka. Poslužitelj provjeri radi li se 
	 * o dobrom rednom broju te šalje {@link Ack} poruku klijentu koji je poslao
	 * paket. Također napravi objekt tipa {@link InMsg} od teksta primljene
	 * poruke i imena klijenta te ga gurne u red za slanje svakom od trenutno 
	 * spojenih klijenata
	 * @param packet UDP paket koji je poslužitelj primio
	 * @param dis {@link DataInputStream} stream za čitanje podataka u paketu
	 */
	private static void recievedOutMsg(DatagramPacket packet, DataInputStream dis) {
		OutMsg outMsg = new OutMsg();
		outMsg.deconstructDatagram(packet, dis);
		
		System.out.println("Recieved: "+ outMsg);
		
		ClientData cd = findClientDataByUID(outMsg.getUID());
		if (cd == null)
			System.out.println("No client with given UID: " + outMsg.getUID());

		sendAck(outMsg.getNumber(), cd);
		
		if(outMsg.getNumber() == cd.messageRecievedNumber - 1L) {
			return;
		}
		
		InMsg inMsg = new InMsg(cd.messageSendingNumber, cd.name, outMsg.getMessage());
		for (ClientData clientData : connectedClients) {
			try {
				clientData.msgToSendQueue.put(inMsg);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			clientData.messageSendingNumber++;
		}
		
		cd.messageRecievedNumber++;
	}

	/**
	 * Pomoćna metoda koja obrađuje slučaj kad se primi UDP datagram u kojem
	 * se nalazi {@link Bye} poruka. Poslužitelj iz svoje interne liste
	 * spojenih klijenata miče klijenta koji je poslao paket te završava njegovu
	 * dretvu u kojem se izvodi.
	 * @param packet UDP paket koji je poslužitelj primio
	 * @param dis {@link DataInputStream} stream za čitanje podataka u paketu
	 */
	private static void recievedBye(DatagramPacket packet, DataInputStream dis) {
		Bye bye = new Bye();
		bye.deconstructDatagram(packet, dis);
		
		System.out.println("Recieved: " + bye);
		
		ClientData cd = findClientDataByUID(bye.getUID());
		if (cd == null)
			System.out.println("There is no one to say BYE to!");
		connectedClients.remove(cd);
		cd.thread.interrupt();
		
		sendAck(bye.getNumber(), cd);
	}

	/**
	 * Pomoćna metoda koja obrađuje slučaj kad se primi UDP datagram u kojem
	 * se nalazi {@link Ack} poruka. Poslužitelj gurne primljeni ack u 
	 * red klijenta koji ga je poslao.
	 * @param packet UDP paket koji je poslužitelj primio
	 * @param dis {@link DataInputStream} stream za čitanje podataka u paketu
	 */
	private static void recievedAck(DatagramPacket packet, DataInputStream dis) {
		Ack ack = new Ack();
		ack.deconstructDatagram(packet, dis);
		
		ClientData cd = findClientDataByUID(ack.getUID());
		try {
			cd.msgRecievedQueue.put(ack);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Pomoćna metoda koja obrađuje slučaj kad se primi UDP datagram u kojem
	 * se nalazi {@link Hello} poruka. Poslužitelj inicijalizira objekt tipa {@link ClientData}
	 * te ga puni s podacima primljenim u paketu. Dodaje objekt u internu listu spojenih
	 * klijenata te započinje dretvu u kojoj se izvodi posao clientjob
	 * @param packet UDP paket koji je poslužitelj primio
	 * @param dis {@link DataInputStream} stream za čitanje podataka u paketu
	 */
	private static void recievedHello(DatagramPacket packet, DataInputStream dis) {
		Hello hello = new Hello();
		hello.deconstructDatagram(packet, dis);
		ClientData cd = findClientDataByRandKey(packet.getAddress(), packet.getPort(), hello.getRandKey());
		if (cd == null) {
			final ClientData cData = new ClientData(); // ovo je workaround 
			cData.address = packet.getAddress();
			cData.port = packet.getPort();
			cData.name = hello.getName();
			cData.randKey = hello.getRandKey();
			cData.UID = clientUIDs++;
			cData.thread = new Thread(() -> clientJob(cData));
			cData.thread.start();
	
			connectedClients.add(cData);
			cd = cData;
		} 

		sendAck(hello.getNumber(), cd);
	}

	/**
	 * Pomoćna metoda koja predstavlja posao koji se bavi klijentom cd
	 * @param cd klijent
	 */
	private static void clientJob(ClientData cd) {
		while (!cd.thread.isInterrupted()) {
			InMsg inMsg = null;
			try {
				inMsg = (InMsg) cd.msgToSendQueue.take();
			} catch (InterruptedException e1) {
				return;
			}
			int counter = 0;
			while (counter < 10) {
				counter++;
				DatagramPacket packet = inMsg.createDatagram(cd.address, cd.port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					System.out.println("Cannot send the message.");
					continue;
				}

				System.out.println("Sending: " + inMsg);
				
				Message msg = null;
				try {
					msg = cd.msgRecievedQueue.poll(5L, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					continue;
				}
				
				if (msg instanceof Ack) {
					if(msg.getNumber() != inMsg.getNumber())
						continue;
					System.out.println("Recieved: " + msg);
					break;
				} else {
					continue;
				}
			}
		}
	}

	/**
	 * Pomoćna metoda koja radi i šalje potvrdu {@link Ack}
	 * @param number redni broj poruke ack
	 * @param cd entitet klijenta
	 */
	private static void sendAck(long number, ClientData cd) {
		Ack ack = new Ack(number, cd.UID);
		DatagramPacket packet = ack.createDatagram(cd.address, cd.port);

		try {
			socket.send(packet);
		} catch (IOException e) {
			System.out.println("Cannot send the message.");
		}
		System.out.println("Sending: " +ack);
	}

	/**
	 * Pomoćna metoda koja u internoj listi spojenih klijenata vraća
	 * klijenta sa predanim UID-jem
	 * @param UID UID čiji se klijent želi naći
	 * @return klijent ili <code>null</code> ako ne postoji
	 */
	private static ClientData findClientDataByUID(long UID) {
		for (ClientData cd : connectedClients) {
			if (cd.UID == UID)
				return cd;
		}
		return null;
	}

	/**
	 * Pomoćna metoda koja u internoj listi spojenih klijenata vraća
	 * klijenta sa predanom kombinacijom ip adresa, port i slučajno generirani ključ 
	 * sa klijentske strane
	 * @param address ip adresa
	 * @param port port
	 * @param randKey slučajno generirani ključ 
	 * sa klijentske strane
	 * @return klijent ili <code>null</code> ako ne postoji
	 */
	private static ClientData findClientDataByRandKey(InetAddress address, int port, long randKey) {
		for (ClientData cd : connectedClients) {
			if (cd.address.equals(address) && cd.port == port && cd.randKey == randKey)
				return cd;
		}
		return null;
	}

	/**
	 * Pomoćna klasa koja opisuje spojenog klijenta
	 * @author vedran
	 *
	 */
	private static class ClientData {
		private InetAddress address;
		private int port;
		private String name;
		private long randKey;
		private long UID;
		private Thread thread;
		private BlockingQueue<Message> msgToSendQueue = new LinkedBlockingQueue<>();
		private BlockingQueue<Message> msgRecievedQueue = new LinkedBlockingQueue<>();
		private long messageRecievedNumber = 1;
		private long messageSendingNumber = 1;
	}

}
