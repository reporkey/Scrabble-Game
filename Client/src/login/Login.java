package login;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.json.*;

import room.Room;
import utilities.MyArrayList;
import utilities.Player;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.UIManager;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class Login extends JFrame {

	private static JFrame frame;
	private JPanel contentPane;
	private JTextField username;
	private int xx, xy;
	private String path;
	private Socket server;
	private String serverIp;
	private int serverPort;
	private static String Msg;
	private MyArrayList<Player> potentialPlayers = new MyArrayList<Player>();

	public void setSocket (Socket server) {
		this.server = server;
		this.serverPort = server.getPort();
		this.serverIp = server.getInetAddress().toString();
		
	}
	
	// Create the frame.
	public Login(Socket server) {
		
		setSocket(server);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 686, 426);
		contentPane = new JPanel();
		
		// set panel
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// set image
		JLabel label = new JLabel("");
		label.setBounds(0, 27, 359, 358);
		contentPane.add(label);
		label.setIcon(new ImageIcon(Login.class.getResource("/img/scrabble.jpg")));

		// set user name text field
		username = new JTextField();
		username.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		username.setBounds(371, 170, 271, 50);
		contentPane.add(username);
		username.setColumns(10);
		
		// set login button
		JButton btnLogin = new JButton("Login");
		btnLogin.setFont(new Font("Lucida Grande", Font.BOLD, 14));
		btnLogin.setForeground(UIManager.getColor("FormattedTextField.foreground"));
		btnLogin.setBackground(new Color(65, 105, 225));
		btnLogin.setBounds(412, 265, 182, 61);
		contentPane.add(btnLogin);

		// set user name label
		JLabel lblNewLabel = new JLabel("Username");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		lblNewLabel.setBounds(376, 121, 154, 37);
		contentPane.add(lblNewLabel);

		// set window close label
		JLabel lbl_close = new JLabel("");
		lbl_close.setIcon(new ImageIcon(Login.class.getResource("/img/shutdown.png")));
		lbl_close.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
			}
		});
		lbl_close.setFont(new Font("Lucida Grande", Font.BOLD, 28));
		lbl_close.setBounds(622, 6, 45, 55);
		contentPane.add(lbl_close);

		// mouse motion listener
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
		
		// set login button
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// send the user name to server
				Boolean validName;
				try {
					Random rand = new Random();
					int randomNum = rand.nextInt((10 - 1) + 1) + 1;
					path = "/img/h" + randomNum + ".png";
					validName = sendName(username.getText().toString(),path);
					// pass the text of user name to room				
					Room r = new Room(username.getText(), server,path);
					r.setUndecorated(true);
					r.setVisible(true);
					dispose();
				} catch (JSONException error) {
					error.printStackTrace();
				} 											
				
			}
		});
	}

	// if the name if occurred, then it's not valid
	public boolean sendName(String name,String path) throws JSONException {
		try {
			if (server != null) {
				Writer output = new OutputStreamWriter(server.getOutputStream(), "UTF-8");
				
				JSONObject send = new JSONObject();
				send.put("method", "join game");
				send.put("path", path);
				send.put("name", name);
				
				output.write(send.toString() + "\n");
				output.flush();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return true;
	}
}