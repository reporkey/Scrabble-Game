package players;

import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.Color;
import javax.swing.UIManager;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;

public class Room extends JFrame {

	private JPanel contentPane;
	private String name;
	private int xx, xy;
	private int randomNum;
	private Socket client;
	private static ArrayList<PlayerC> potentialPlayers;
	private static ArrayList<PlayerC> players;
	private static JPanel panelPlayer2, panelPlayer3, panelPlayer4, panelPlayer5, panelPlayer6, panelPlayer7;
	private static JLabel P2, P3, P4, P5, P6, P7;
	static messLisCall messageLisCall = new messLisCall();
	static boolean flag = false;
	private int count = 0;

	/**
	 * Create the frame.
	 */
	public Room(String name, ArrayList<PlayerC> potentialPlayers, int randomNum, Socket client) {
		this.potentialPlayers = potentialPlayers;
		this.client = client;
		this.randomNum = randomNum;
		this.name = name;
		initialize();
	}

	public void initialize() {
		messageLisCall.setvalue(potentialPlayers);
		count = potentialPlayers.size();
		Thread mythread = new messLis(name, client);
		mythread.start();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 719, 358);
		contentPane = new JPanel();
		contentPane.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				int x = e.getXOnScreen();
				int y = e.getYOnScreen();
				Room.this.setLocation(x - xx, y - xy);
			}
		});
		contentPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				xx = e.getX();
				xy = e.getY();
			}
		});
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(new Color(71, 120, 197));
		panel_1.setBounds(0, 0, 719, 51);
		contentPane.add(panel_1);
		panel_1.setLayout(null);

		JLabel logout = new JLabel("");
		logout.setBounds(677, 14, 24, 24);
		panel_1.add(logout);
		logout.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
			}
		});
		logout.setIcon(new ImageIcon(Room.class.getResource("/players/img/logout.png")));

		JLabel lblScrabble = new JLabel("SCRABBLE");
		lblScrabble.setForeground(Color.WHITE);
		lblScrabble.setFont(new Font("Charlemagne Std", Font.BOLD, 16));
		lblScrabble.setBounds(18, 14, 121, 31);
		panel_1.add(lblScrabble);

		JPanel panel_2 = new JPanel();
		panel_2.setBackground(new Color(71, 120, 197));
		panel_2.setBounds(0, 40, 190, 296);
		contentPane.add(panel_2);
		panel_2.setLayout(null);

		JPanel panel_3 = new JPanel();
		panel_3.setBackground(new Color(120, 168, 252));
		panel_3.setBounds(-6, 6, 195, 56);
		panel_2.add(panel_3);
		panel_3.setLayout(null);

		JLabel usernameLable = new JLabel("");
		usernameLable.setBounds(12, 20, 177, 19);
		panel_3.add(usernameLable);
		usernameLable.setFont(new Font("Lithos Pro", Font.PLAIN, 15));
		usernameLable.setForeground(Color.WHITE);
		usernameLable.setText("Welcome, " + name);

		JPanel panel_4 = new JPanel();
		panel_4.setBackground(new Color(84, 127, 206));
		panel_4.setBounds(0, 61, 189, 121);
		panel_2.add(panel_4);
		GridBagLayout gbl_panel_4 = new GridBagLayout();
		gbl_panel_4.columnWidths = new int[] { 94, 0, 94, 0 };
		gbl_panel_4.rowHeights = new int[] { 70, 0, 0, 0, 70, 0 };
		gbl_panel_4.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel_4.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel_4.setLayout(gbl_panel_4);

		JLabel label = new JLabel("");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.fill = GridBagConstraints.BOTH;
		gbc_label.insets = new Insets(0, 0, 5, 0);
		gbc_label.gridx = 2;
		gbc_label.gridy = 0;
		panel_4.add(label, gbc_label);

		JLabel profile = new JLabel("");
		profile.setIcon(new ImageIcon(Room.class.getResource("/players/img/h" + randomNum + ".png")));
		GridBagConstraints gbc_profile = new GridBagConstraints();
		gbc_profile.fill = GridBagConstraints.BOTH;
		gbc_profile.insets = new Insets(0, 0, 5, 5);
		gbc_profile.gridx = 1;
		gbc_profile.gridy = 2;
		panel_4.add(profile, gbc_profile);

		JLabel label_1 = new JLabel("");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.fill = GridBagConstraints.BOTH;
		gbc_label_1.gridx = 2;
		gbc_label_1.gridy = 4;
		panel_4.add(label_1, gbc_label_1);

		JButton btnNewButton = new JButton("Scrabble");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Board game = new Board(players, client);
				game.setUndecorated(true);
				game.setVisible(true);
			}
		});
		btnNewButton.setBounds(39, 209, 117, 29);
		panel_2.add(btnNewButton);

		JPanel panel_5 = new JPanel();
		panel_5.setBackground(Color.WHITE);
		panel_5.setBounds(192, 40, 527, 296);
		contentPane.add(panel_5);
		panel_5.setLayout(null);

		panelPlayer2 = new JPanel();
		panelPlayer2.setBounds(52, 48, 100, 93);
		panel_5.add(panelPlayer2);
		panelPlayer2.setLayout(null);
		panelPlayer2.setVisible(false);

		P2 = new JLabel("");
		P2.setBounds(19, 71, 61, 16);
		panelPlayer2.add(P2);

		JCheckBox C1 = new JCheckBox("");
		C1.setBounds(0, 0, 28, 23);
		panelPlayer2.add(C1);

		JLabel R2 = new JLabel("");
		R2.setFont(new Font("Lithos Pro", Font.PLAIN, 13));
		R2.setForeground(Color.RED);
		R2.setBounds(33, 43, 61, 16);
		panelPlayer2.add(R2);

		panelPlayer5 = new JPanel();
		panelPlayer5.setBounds(52, 176, 100, 93);
		panel_5.add(panelPlayer5);
		panelPlayer5.setLayout(null);
		panelPlayer5.setVisible(false);

		P5 = new JLabel("");
		P5.setBounds(19, 71, 61, 16);
		panelPlayer5.add(P5);

		JCheckBox C4 = new JCheckBox("");
		C4.setBounds(0, 0, 28, 23);
		panelPlayer5.add(C4);

		JLabel R5 = new JLabel("");
		R5.setFont(new Font("Lithos Pro", Font.PLAIN, 13));
		R5.setForeground(Color.RED);
		R5.setBounds(33, 43, 61, 16);
		panelPlayer5.add(R5);

		panelPlayer6 = new JPanel();
		panelPlayer6.setBounds(222, 176, 100, 93);
		panel_5.add(panelPlayer6);
		panelPlayer6.setLayout(null);
		panelPlayer6.setVisible(false);

		P6 = new JLabel("");
		P6.setBounds(20, 71, 61, 16);
		panelPlayer6.add(P6);

		JCheckBox C5 = new JCheckBox("");
		C5.setBounds(0, 2, 28, 23);
		panelPlayer6.add(C5);

		JLabel R6 = new JLabel("");
		R6.setFont(new Font("Lithos Pro", Font.PLAIN, 13));
		R6.setForeground(Color.RED);
		R6.setBounds(33, 43, 61, 16);
		panelPlayer6.add(R6);

		panelPlayer7 = new JPanel();
		panelPlayer7.setBounds(387, 176, 100, 93);
		panel_5.add(panelPlayer7);
		panelPlayer7.setLayout(null);
		panelPlayer7.setVisible(false);

		P7 = new JLabel("");
		P7.setBounds(21, 71, 61, 16);
		panelPlayer7.add(P7);

		JCheckBox C6 = new JCheckBox("");
		C6.setBounds(0, 0, 28, 23);
		panelPlayer7.add(C6);

		JLabel R7 = new JLabel("");
		R7.setFont(new Font("Lithos Pro", Font.PLAIN, 13));
		R7.setForeground(Color.RED);
		R7.setBounds(33, 42, 61, 16);
		panelPlayer7.add(R7);

		panelPlayer4 = new JPanel();
		panelPlayer4.setBounds(387, 48, 100, 93);
		panel_5.add(panelPlayer4);
		panelPlayer4.setLayout(null);
		panelPlayer4.setVisible(false);

		P4 = new JLabel("");
		P4.setBounds(21, 71, 61, 16);
		panelPlayer4.add(P4);

		JCheckBox C3 = new JCheckBox("");
		C3.setBounds(0, 0, 28, 23);
		panelPlayer4.add(C3);

		JLabel R4 = new JLabel("");
		R4.setFont(new Font("Lithos Pro", Font.PLAIN, 13));
		R4.setForeground(Color.RED);
		R4.setBounds(33, 43, 61, 16);
		panelPlayer4.add(R4);

		panelPlayer3 = new JPanel();
		panelPlayer3.setBounds(222, 48, 100, 93);
		panel_5.add(panelPlayer3);
		panelPlayer3.setLayout(null);
		panelPlayer3.setVisible(false);

		P3 = new JLabel("");
		P3.setBounds(18, 71, 61, 16);
		panelPlayer3.add(P3);

		JCheckBox C2 = new JCheckBox("");
		C2.setBounds(0, 0, 28, 23);
		panelPlayer3.add(C2);

		JLabel R3 = new JLabel("");
		R3.setFont(new Font("Lithos Pro", Font.PLAIN, 13));
		R3.setForeground(Color.RED);
		R3.setBounds(33, 43, 61, 16);
		panelPlayer3.add(R3);

		JButton btnInvite = new JButton("Invite");
		btnInvite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//potentialPlayers=messageLisCall.getvalue();
				String sendMsg = "";
				try {
					// {"method":"join
					// game","players":[{"score":0,"port":52276,"ready":0,"ip":"127.0.0.1","name":"123"}]}
					Writer output = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
					if (C1.isSelected()) {
						sendMsg += "\"name\":" + "\"" + potentialPlayers.get(0).getName() + "\"," + "\"port\":" + "\""
								+ Integer.toString(potentialPlayers.get(0).getPort())  + "\"," + "\"score\":" + "\""
								+ Integer.toString(potentialPlayers.get(0).getScore())  + "\"," + "\"reday\":" + "\""
								+ Boolean.toString(potentialPlayers.get(0).getReady()) + "\"," + "\"ip\":" + "\""
								+ potentialPlayers.get(0).getIp().toString() + "\",";
						R2.setText("WAIT");
					}
					if (C2.isSelected()) {
						sendMsg += "\"name\":" + "\"" + potentialPlayers.get(1).getName() + "\"," + "\"port\":" + "\""
								+ Integer.toString(potentialPlayers.get(1).getPort())  + "\"," + "\"score\":" + "\""
								+ Integer.toString(potentialPlayers.get(1).getScore())  + "\"," + "\"reday\":" + "\""
								+ Boolean.toString(potentialPlayers.get(1).getReady()) + "\"," + "\"ip\":" + "\""
								+ potentialPlayers.get(1).getIp().toString() + "\",";
						R3.setText("WAIT");
					}
					if (C3.isSelected()) {
						sendMsg += "\"name\":" + "\"" + potentialPlayers.get(2).getName() + "\"," + "\"port\":" + "\""
								+ Integer.toString(potentialPlayers.get(2).getPort())  + "\"," + "\"score\":" + "\""
								+ Integer.toString(potentialPlayers.get(2).getScore())  + "\"," + "\"reday\":" + "\""
								+ Boolean.toString(potentialPlayers.get(2).getReady()) + "\"," + "\"ip\":" + "\""
								+ potentialPlayers.get(2).getIp().toString() + "\",";
						R4.setText("WAIT");
					}
					if (C4.isSelected()) {
						sendMsg += "\"name\":" + "\"" + potentialPlayers.get(3).getName() + "\"," + "\"port\":" + "\""
								+ Integer.toString(potentialPlayers.get(3).getPort())  + "\"," + "\"score\":" + "\""
								+ Integer.toString(potentialPlayers.get(3).getScore())  + "\"," + "\"reday\":" + "\""
								+ Boolean.toString(potentialPlayers.get(3).getReady()) + "\"," + "\"ip\":" + "\""
								+ potentialPlayers.get(3).getIp().toString() + "\",";
						R5.setText("WAIT");
					}
					if (C5.isSelected()) {
						sendMsg += "\"name\":" + "\"" + potentialPlayers.get(4).getName() + "\"," + "\"port\":" + "\""
								+ Integer.toString(potentialPlayers.get(4).getPort())  + "\"," + "\"score\":" + "\""
								+ Integer.toString(potentialPlayers.get(4).getScore())  + "\"," + "\"reday\":" + "\""
								+ Boolean.toString(potentialPlayers.get(4).getReady()) + "\"," + "\"ip\":" + "\""
								+ potentialPlayers.get(4).getIp().toString() + "\",";
						R6.setText("WAIT");
					}
					if (C6.isSelected()) {
						sendMsg += "\"name\":" + "\"" + potentialPlayers.get(5).getName() + "\"," + "\"port\":" + "\""
								+ Integer.toString(potentialPlayers.get(5).getPort())  + "\"," + "\"score\":" + "\""
								+ Integer.toString(potentialPlayers.get(5).getScore())  + "\"," + "\"reday\":" + "\""
								+ Boolean.toString(potentialPlayers.get(5).getReady()) + "\"," + "\"ip\":" + "\""
								+ potentialPlayers.get(5).getIp().toString() + "\",";
						R7.setText("WAIT");
					}
					output.write("{" + "\"method\":" + "\"" + "request invite" + "\"," + "\"players\":[{" + sendMsg
							+ "}]}" + "\n");
					output.flush();
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnInvite.setBounds(39, 250, 117, 29);
		panel_2.add(btnInvite);
		if (count > 0)
			checkPlayerSize(potentialPlayers);
	}

	public static void checkPlayerSize(ArrayList<PlayerC> potentialPlayers) {
		panelPlayer2.setVisible(false);
		panelPlayer3.setVisible(false);
		panelPlayer4.setVisible(false);
		panelPlayer5.setVisible(false);
		panelPlayer6.setVisible(false);
		panelPlayer7.setVisible(false);
		if (potentialPlayers.size() == 1) {
			panelPlayer2.setVisible(true);
			P2.setText(potentialPlayers.get(0).getName());
		} else if (potentialPlayers.size() == 2) {
			panelPlayer2.setVisible(true);
			P2.setText(potentialPlayers.get(0).getName());
			panelPlayer3.setVisible(true);
			P3.setText(potentialPlayers.get(1).getName());
		} else if (potentialPlayers.size() == 3) {
			panelPlayer2.setVisible(true);
			P2.setText(potentialPlayers.get(0).getName());
			panelPlayer3.setVisible(true);
			P3.setText(potentialPlayers.get(1).getName());
			panelPlayer4.setVisible(true);
			P4.setText(potentialPlayers.get(2).getName());
		} else if (potentialPlayers.size() == 4) {
			panelPlayer2.setVisible(true);
			P2.setText(potentialPlayers.get(0).getName());
			panelPlayer3.setVisible(true);
			P3.setText(potentialPlayers.get(1).getName());
			panelPlayer4.setVisible(true);
			P4.setText(potentialPlayers.get(2).getName());
			panelPlayer5.setVisible(true);
			P5.setText(potentialPlayers.get(3).getName());
		} else if (potentialPlayers.size() == 5) {
			panelPlayer2.setVisible(true);
			P2.setText(potentialPlayers.get(0).getName());
			panelPlayer3.setVisible(true);
			P3.setText(potentialPlayers.get(1).getName());
			panelPlayer4.setVisible(true);
			P4.setText(potentialPlayers.get(2).getName());
			panelPlayer5.setVisible(true);
			P5.setText(potentialPlayers.get(3).getName());
			panelPlayer6.setVisible(true);
			P6.setText(potentialPlayers.get(4).getName());
		} else if (potentialPlayers.size() == 6) {
			panelPlayer2.setVisible(true);
			P2.setText(potentialPlayers.get(0).getName());
			panelPlayer3.setVisible(true);
			P3.setText(potentialPlayers.get(1).getName());
			panelPlayer4.setVisible(true);
			P4.setText(potentialPlayers.get(2).getName());
			panelPlayer5.setVisible(true);
			P5.setText(potentialPlayers.get(3).getName());
			panelPlayer6.setVisible(true);
			P6.setText(potentialPlayers.get(4).getName());
			panelPlayer7.setVisible(true);
			P7.setText(potentialPlayers.get(5).getName());
		} else
			System.out.print("Wrong size" + potentialPlayers.size());
	}

}

class messLisCall {
	private ArrayList<PlayerC> potentialPlayers = new ArrayList<PlayerC>();

	public ArrayList<PlayerC> getvalue() {
		return potentialPlayers;
	}

	public void setvalue(ArrayList<PlayerC> p) {
		this.potentialPlayers = p;
	}
}

class messLis extends Thread {
	static messLisCall messageLisCall = new messLisCall();

	public messLis(String name, Socket client) {
		this.name = name;
		this.client = client;
	}

	private ArrayList<PlayerC> potentialPlayers;
	private Socket client;
	private String name;

	@Override
	public void run() {
		String msgStr = null;

		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
			while ((msgStr = input.readLine()) != null) {
				ArrayList<PlayerC> potentialPlayers = new ArrayList<PlayerC>();
				JSONObject msg = new JSONObject(msgStr);
				switch (msg.getString("method")) {
				case "join game": {
					JSONArray arr = msg.getJSONArray("players");
					for (int i = 0; i < arr.length(); i++) {
						String name2 = arr.getJSONObject(i).getString("name");
						if (!name2.equals(name)) {
							int score = arr.getJSONObject(i).getInt("score");
							// String img =
							// arr.getJSONObject(i).getString("img");
							PlayerC player = new PlayerC(client, msg);
							player.setName(name2);
							player.setScore(score);
							potentialPlayers.add(player);
						}
					}
					messageLisCall.setvalue(potentialPlayers);
					Room.checkPlayerSize(potentialPlayers);
				}
				case "request invite": {
					// {"method":"request invite","players":[{null"name":"2",}]}
					// {"method":"join
					// game","players":[{"score":0,"port":52276,"ready":0,"ip":"127.0.0.1","name":"123"}]}

				}
				case "response invite": {

				}
				case "ready": {

				}
				}
			}
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
