package server;

public class Server {

    public static void main(String[] args) {
    	if (args.length == 1) {
			int serverPort = Integer.parseInt(args[0]);
	        World world = new World(serverPort);
		}else {
			System.out.println("Not correct number of inputs.");
			System.out.println("java -jar server.jar [port]");
		}

    }

}


