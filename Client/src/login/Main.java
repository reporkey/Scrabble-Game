package login;

import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

public class Main {
	private static String serverIp;
	private static int serverPort;
	private static Socket server;
	

	public static void main(String[] args){
		if (args.length == 2) {
			serverIp = args[0];
			serverPort = Integer.parseInt(args[1]);
		}else {
			System.out.println("Not correct number of inputs.");
			System.out.println("java -jar client.jar ip [port]");
		}
		if(createServerSocket(serverIp, serverPort)) {
			Login window = new Login(server);
			window.setUndecorated(true);
			window.setVisible(true);
		}
	}
	
	private static boolean createServerSocket(String serverIp, int serverPort) {
		try {
			server = new Socket(serverIp, serverPort);
			return true;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			JOptionPane.showMessageDialog(null, "Cannot connect to server.", "Connection refused",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
	}

}
