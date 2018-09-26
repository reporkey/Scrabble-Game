package players;

import java.net.Socket;

import players.Login;

public class Main {
	private static String host;
	private static int port;
	private static Socket client;

	public static void main(String[] args){
		try {
			host=args[0];
			port=Integer.parseInt(args[1]);
			Login window = new Login();
			window.setClient(client,host,port);
			window.setUndecorated(true);
			window.setVisible(true);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
