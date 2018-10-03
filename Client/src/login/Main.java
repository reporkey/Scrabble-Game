package login;

import java.io.IOException;
import java.net.Socket;

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
		}
		createServerSocket(serverIp, serverPort);
		Login window = new Login(server);
		window.setUndecorated(true);
		window.setVisible(true);
	}
	
	private static void createServerSocket(String serverIp, int serverPort) {
		try {
			server = new Socket(serverIp, serverPort);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}
