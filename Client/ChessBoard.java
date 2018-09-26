package players;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;

public class ChessBoard extends JFrame {

	private JPanel contentPane;
	private JButton[][] squares = new JButton[20][20];
	Border border = BorderFactory.createLineBorder(Color.WHITE,1);
	Border borderB = BorderFactory.createLineBorder(Color.BLACK,1);
	
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChessBoard frame = new ChessBoard();
					frame.setUndecorated(true);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ChessBoard() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 720, 720);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		
		JPanel panel = new JPanel();
		panel.setBounds(6, 36, 500, 500);
		
		ButtonHandler buttonHandler = new ButtonHandler();
		
		panel.setLayout((new GridLayout(20,20)));
		for (int i = 0; i<20; i++)
		{
			for (int j=0; j<20; j++)
			{
				squares[i][j] = new JButton();
				squares[i][j].setBackground(new Color(242, 229, 210));
				squares[i][j].setOpaque(true);
				squares[i][j].setBorderPainted(true);
				squares[i][j].setBorder(border);
        
				panel.add(squares[i][j]);
				squares[i][j].addActionListener(buttonHandler);
			}
		}
		
		setResizable(false);
		setLocationRelativeTo(null);
		panel.setVisible(true);
		
		contentPane.add(panel);
		
		JLabel lblPleaseVote = new JLabel("Do you think “_” is a word? Please vote... ");
		lblPleaseVote.setForeground(new Color(0, 102, 255));
		lblPleaseVote.setFont(new Font("Skia", Font.PLAIN, 16));
		lblPleaseVote.setBounds(16, 548, 490, 16);
		contentPane.add(lblPleaseVote);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		panel_1.setBounds(6, 576, 500, 116);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		JButton btnNewButton = new JButton("Yes!");
		btnNewButton.setFont(new Font("Monaco", Font.BOLD, 20));
		btnNewButton.setBounds(6, 41, 212, 69);
		panel_1.add(btnNewButton);
		
		JButton btnNo = new JButton("No..");
		btnNo.setFont(new Font("Monaco", Font.BOLD, 20));
		btnNo.setBounds(255, 41, 212, 69);
		panel_1.add(btnNo);
		
		JLabel lblVResult = new JLabel();
		lblVResult.setBounds(518, 576, 196, 116);
		contentPane.add(lblVResult);
		
		//if voting result is no
		ImageIcon icon = new ImageIcon(ChessBoard.class.getResource("/players/img/sad.png"));
		Image image = icon.getImage();
		Image newimg = image.getScaledInstance(110, 110, java.awt.Image.SCALE_SMOOTH);
		lblVResult.setIcon(new ImageIcon(newimg));
		
		//if voting result is yes
		ImageIcon icon2 = new ImageIcon(ChessBoard.class.getResource("/players/img/smile.png"));
		Image image2 = icon2.getImage();
		Image newimg2 = image2.getScaledInstance(110, 110, java.awt.Image.SCALE_SMOOTH);
		lblVResult.setIcon(new ImageIcon(newimg2));
		
		//could add a ? with when no result need to be displayed
		
		JLabel lblVotingResult = new JLabel("Voting Result:");
		lblVotingResult.setForeground(new Color(255, 153, 204));
		lblVotingResult.setFont(new Font("Apple Braille", Font.BOLD, 18));
		lblVotingResult.setBounds(518, 538, 196, 35);
		contentPane.add(lblVotingResult);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(new Color(204, 255, 255));
		panel_2.setBounds(518, 36, 185, 500);
		contentPane.add(panel_2);
		panel_2.setLayout(null);
		
		JLabel lblPlayers = new JLabel("Players");
		lblPlayers.setFont(new Font("Muna", Font.BOLD, 16));
		lblPlayers.setBounds(6, 6, 173, 24);
		panel_2.add(lblPlayers);
		
		JLabel lblNewLabel = new JLabel("Please place a character…");
		lblNewLabel.setForeground(new Color(0, 153, 255));
		lblNewLabel.setFont(new Font("Skia", Font.ITALIC, 16));
		lblNewLabel.setBounds(6, 8, 500, 16);
		contentPane.add(lblNewLabel);
	}
	
	private class ButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			
			for (int i = 0; i<20; i++){
				for (int j=0; j<20; j++){
					if (source == squares[i][j]){
						processClick(i,j);
						return;
					}
				}
			}
		}
	}
	
	
	
	//is valid if no character placed before
	private boolean isValidMove(int i, int j)
	{
		return true;
	}
	
	//the effect of border and others..
	private void processClick(int row, int col)
	{
		if (isValidMove(row, col) == false)
		{
			return;
		}
		for (int i = 0; i<20; i++){
			for (int j=0; j<20; j++){
				squares[i][j].setBorderPainted(true);
				squares[i][j].setBorder(border);
			}
		}
		squares[row][col].setBorderPainted(true);
		squares[row][col].setBorder(borderB);
		
		squares[row][col].addKeyListener(new KeyListener(){
			  @Override
        public void keyPressed(KeyEvent e) {
            char input = e.getKeyChar();
            System.out.println(e.getKeyChar());
            ImageIcon icon = new ImageIcon(ChessBoard.class.getResource("/players/img/"+input+".png"));
            Image image = icon.getImage();
            Image newimg = image.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
            squares[row][col].setIcon(new ImageIcon(newimg));
        }
			  @Override
        public void keyTyped(KeyEvent e) {
				
			  }
			  @Override
        public void keyReleased(KeyEvent e) {
				    squares[row][col].setBorderPainted(true);
			  }
		});
		
		
		//squares[row][col].setIcon(letter);
		
		//squares[row][col].setIcon(null);
		//squares[i][j].setIcon(letter);
//		row = i;
//		col = j;
	}
}
