package oprpp2.hw02.client.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import oprpp2.hw02.client.Main;
import oprpp2.hw02.packetNames.Bye;
import oprpp2.hw02.packetNames.OutMsg;

/** 
 * Klasa predstavlja GUI chat klijenta koji ima mjesto 
 * za unos poruke i display na koji dolaze poruke. 
 * Poruka se šalje klikom na enter.
 * 
 * @author vedran
 *
 */
public class Chat extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String frameTitle = "Chat client: ";
	private JTextArea printArea;
	private JTextField textField;
	private long UID;

	public Chat(String titleName, long UID) {
		this.frameTitle += titleName;
		this.UID = UID;
		initGui();
		setSize(500, 200);
	}

	/**
	 * Pomoćna metoda za inicijalizaciju chat prozora.
	 */
	private void initGui() {
		setTitle(frameTitle);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyTyped(e);
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String text = textField.getText();
					textField.setText("");
					new Thread(() -> Main.sendMessage(new OutMsg(Main.getMessageNumber(), UID, text))).start();;
				}
			}
		});
		cp.add(textField, BorderLayout.PAGE_START);
		printArea = new JTextArea();
		printArea.setEditable(false);
		cp.add(new JScrollPane(printArea), BorderLayout.CENTER);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				new Thread(() -> Main.sendMessage(new Bye(Main.getMessageNumber(), UID))).start();
			}
		});
	}

	/**
	 * Metoda ispisuje dobiveni {@link String} na display
	 * @param text poruka koja se treba ispisati na display
	 */
	public void printToLabel(String text) {
		printArea.setText(printArea.getText() + text + "\n\n");
	}

}
