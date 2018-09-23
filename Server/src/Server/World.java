package Server;

import javax.net.ServerSocketFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class World {

    private static final int port = 8080;
    private ArrayList<Player> players = new ArrayList<Player>();

    public World() {

        ServerSocketFactory factory = ServerSocketFactory.getDefault();

        try(ServerSocket server = factory.createServerSocket(port)){
            System.out.println("Waiting for client connection..");

            // Wait for connections.
            while(true){
                Socket client = server.accept();
                System.out.println("Client: Applying for connection!");


                // Start a new thread for a connection
                Thread t = new Thread(() -> serveClient(client));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void serveClient(Socket client){
        try(Socket clientSocket = client){

            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));

            String msg = null;
            while ((msg = in.readLine()) != null){
                // if request connection
                Player player = new Player(client);
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
