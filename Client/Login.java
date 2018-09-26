package players;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import server.Player;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.UIManager;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.WindowConstants;

public class Login extends JFrame {

	private static JFrame frame;
	private JPanel contentPane;
	private JTextField username;
	private int xx, xy;
	private Socket client;
	private String host;
	private int port;
	private static String Msg;
	private ArrayList<PlayerC> potentialPlayers = new ArrayList<PlayerC>();

	public void setClient(Socket client, String host, int port) {
		this.client = client;
		this.port = port;
		this.host = host;
	}

	/*
	 * public static void main(String[] args) { EventQueue.invokeLater(new
	 * Runnable() { public void run() { try { Login frame = new Login();
	 * frame.setUndecorated(true); frame.setVisible(true); } catch (Exception e)
	 * { e.printStackTrace(); } } }); }
	 */

	/**
	 * Create the frame.
	 */
	public Login() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 686, 426);
		contentPane = new JPanel();

		contentPane.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				int x = e.getXOnScreen();
				int y = e.getYOnScreen();
				Login.this.setLocation(x - xx, y - xy);
			}
		});
		contentPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				xx = e.getX();
				xy = e.getY();
			}
		});
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel label = new JLabel("");
		label.setBounds(0, 27, 359, 358);
		contentPane.add(label);
		label.setIcon(new ImageIcon(Login.class.getResource("/players/img/scrabble.jpg")));

		username = new JTextField();
		username.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		username.setBounds(371, 170, 271, 50);
		contentPane.add(username);
		username.setColumns(10);

		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				established(); // establish connect with sever
				// send the username to server
				int rand=0;
				try {
					rand = sendMessage(username.getText().toString());
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 			
				// Launch the member pool.
				Room r = new Room(username.getText(), potentialPlayers, rand,client); 
				// pass the text of username to room
				r.setUndecorated(true);
				r.setVisible(true);
				dispose();
			}
		});
		btnLogin.setFont(new Font("Lucida Grande", Font.BOLD, 14));
		btnLogin.setForeground(UIManager.getColor("FormattedTextField.foreground"));
		btnLogin.setBackground(new Color(65, 105, 225));
		btnLogin.setBounds(412, 265, 182, 61);
		contentPane.add(btnLogin);

		JLabel lblNewLabel = new JLabel("Username");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		lblNewLabel.setBounds(376, 121, 154, 37);
		contentPane.add(lblNewLabel);

		JLabel lbl_close = new JLabel("");
		lbl_close.setIcon(new ImageIcon(Login.class.getResource("/players/img/shutdown.png")));
		lbl_close.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
			}
		});
		lbl_close.setFont(new Font("Lucida Grande", Font.BOLD, 28));
		lbl_close.setBounds(622, 6, 45, 55);
		contentPane.add(lbl_close);
	}

	public boolean established() {
		try {
			client = new Socket(host, port);
			return true;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	public int sendMessage(String message) throws JSONException {
		Random rand = new Random();
		int randomNum = rand.nextInt((10 - 1) + 1) + 1;
		try {
			if (client != null) {
				BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
				Writer output = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
				output.write("{\"name\":" + "\"" + message + "\"," + "\"method\":" + "\"" + "join game" + "\","
						+ "\"img\":" + "\"" + "/players/img/h" + randomNum + ".png\"," + "}" + "\n");
				output.flush();
				String msgStr = null;
				msgStr = input.readLine();
				//{"method":"join game","players":[{"score":0,"port":52276,"ready":0,"ip":"127.0.0.1","name":"123"}]}
				JSONObject msg = new JSONObject(msgStr);
				JSONArray arr = msg.getJSONArray("players");
				for (int i = 0; i < arr.length(); i++)
				{
					String name = arr.getJSONObject(i).getString("name");
					if(!name.equals(message)){
						int score = arr.getJSONObject(i).getInt("score");
					    //String img = arr.getJSONObject(i).getString("img");
						PlayerC player = new PlayerC(client, msg);
						player.setName(name);
						player.setScore(score);
						potentialPlayers.add(player);
					}					
				}
			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return randomNum;
	}
}