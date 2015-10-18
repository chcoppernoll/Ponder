package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.FlowLayout;

public class BuilderWindow {

	private JFrame frame;
	
	// TODO Alex
	/*
	 * Get Spawn Area to stack from the bottom for players 2 and 3
	 * Settings and GameList buttons pop up new jPanels over the game board and remove listeners for other objects
	 * Get a JAR Builder
	 * Make JAR out of Artwork
	 * Add JAR to classpath
	 */
	
	// TODO Grayson
	/*
	 * Work on artwork
	 * Work on the api
	 * Move elements into position
	 */
	
	// TODO API
	/*
	 * Be able to click and move pieces
	 * Be able to remove from the spawn area to the board
	 * Need to add a call to clear RefreshQueue
	 * Be able to chain moves
	 * Be able to get a cell from the mouse
	 */

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BuilderWindow window = new BuilderWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public BuilderWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 801, 638);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnSettings = new JButton("Settings");
		btnSettings.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				System.out.println("Clicked Settings!");
			}
		});
		btnSettings.setBounds(17, 29, 114, 23);
		frame.getContentPane().add(btnSettings);
		
		JButton btnGameList = new JButton("Game List");
		btnGameList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("Clicked Game List!");
			}
		});
		btnGameList.setBounds(658, 29, 122, 23);
		frame.getContentPane().add(btnGameList);
		
		JTextPane txtpnPlayer = new JTextPane();
		txtpnPlayer.setText("Player 1");
		txtpnPlayer.setBounds(49, 109, 82, 23);
		frame.getContentPane().add(txtpnPlayer);
		
		JTextPane txtpnPlayer_1 = new JTextPane();
		txtpnPlayer_1.setText("Player 2");
		txtpnPlayer_1.setBounds(49, 537, 82, 23);
		frame.getContentPane().add(txtpnPlayer_1);
		
		JTextPane txtpnPlayer_3 = new JTextPane();
		txtpnPlayer_3.setText("Player 4");
		txtpnPlayer_3.setBounds(658, 109, 82, 23);
		frame.getContentPane().add(txtpnPlayer_3);
		
		JTextPane txtpnPlayer_2 = new JTextPane();
		txtpnPlayer_2.setText("Player 3");
		txtpnPlayer_2.setBounds(658, 537, 82, 23);
		frame.getContentPane().add(txtpnPlayer_2);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.BLACK);
		panel.setBounds(148, 84, 500, 500);
		frame.getContentPane().add(panel);
		panel.setLayout(new GridLayout(9, 9, 1, 1));
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(BuilderWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif")));
		lblNewLabel.setBounds(111, 512, 20, 20);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel label = new JLabel("");
		label.setIcon(new ImageIcon(BuilderWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif")));
		label.setBounds(87, 512, 20, 20);
		frame.getContentPane().add(label);
		
		JLabel label_1 = new JLabel("");
		label_1.setIcon(new ImageIcon(BuilderWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif")));
		label_1.setBounds(42, 512, 20, 20);
		frame.getContentPane().add(label_1);
		
		JLabel label_2 = new JLabel("");
		label_2.setIcon(new ImageIcon(BuilderWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif")));
		label_2.setBounds(66, 512, 20, 20);
		frame.getContentPane().add(label_2);
		
		JLabel label_3 = new JLabel("");
		label_3.setIcon(new ImageIcon(BuilderWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif")));
		label_3.setBounds(111, 143, 20, 20);
		frame.getContentPane().add(label_3);
		
		JLabel label_4 = new JLabel("");
		label_4.setIcon(new ImageIcon(BuilderWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif")));
		label_4.setBounds(87, 143, 20, 20);
		frame.getContentPane().add(label_4);
		
		JLabel label_5 = new JLabel("");
		label_5.setIcon(new ImageIcon(BuilderWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif")));
		label_5.setBounds(66, 143, 20, 20);
		frame.getContentPane().add(label_5);
		
		JLabel label_6 = new JLabel("");
		label_6.setIcon(new ImageIcon(BuilderWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif")));
		label_6.setBounds(42, 143, 20, 20);
		frame.getContentPane().add(label_6);
		
		JLabel label_7 = new JLabel("");
		label_7.setIcon(new ImageIcon(BuilderWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif")));
		label_7.setBounds(727, 143, 20, 20);
		frame.getContentPane().add(label_7);
		
		JLabel label_8 = new JLabel("");
		label_8.setIcon(new ImageIcon(BuilderWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif")));
		label_8.setBounds(703, 143, 20, 20);
		frame.getContentPane().add(label_8);
		
		JLabel label_9 = new JLabel("");
		label_9.setIcon(new ImageIcon(BuilderWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif")));
		label_9.setBounds(682, 143, 20, 20);
		frame.getContentPane().add(label_9);
		
		JLabel label_10 = new JLabel("");
		label_10.setIcon(new ImageIcon(BuilderWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif")));
		label_10.setBounds(658, 143, 20, 20);
		frame.getContentPane().add(label_10);
		
		JLabel label_11 = new JLabel("");
		label_11.setIcon(new ImageIcon(BuilderWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif")));
		label_11.setBounds(658, 512, 20, 20);
		frame.getContentPane().add(label_11);
		
		JLabel label_12 = new JLabel("");
		label_12.setIcon(new ImageIcon(BuilderWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif")));
		label_12.setBounds(682, 512, 20, 20);
		frame.getContentPane().add(label_12);
		
		JLabel label_13 = new JLabel("");
		label_13.setIcon(new ImageIcon(BuilderWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif")));
		label_13.setBounds(703, 512, 20, 20);
		frame.getContentPane().add(label_13);
		
		JLabel label_14 = new JLabel("");
		label_14.setIcon(new ImageIcon(BuilderWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif")));
		label_14.setBounds(727, 512, 20, 20);
		frame.getContentPane().add(label_14);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(76, 174, 55, 162);
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(76, 339, 55, 162);
		frame.getContentPane().add(panel_2);
		panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(658, 339, 55, 162);
		frame.getContentPane().add(panel_3);
		panel_3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel_3.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBounds(658, 174, 55, 162);
		frame.getContentPane().add(panel_4);
		panel_4.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panel_5 = new JPanel();
		panel_5.setBounds(148, 11, 500, 62);
		frame.getContentPane().add(panel_5);
		panel_5.setLayout(null);
		
		JTextPane txtpnGameDataGoes = new JTextPane();
		txtpnGameDataGoes.setText("Game Data Goes Here");
		txtpnGameDataGoes.setBounds(149, 31, 209, 20);
		panel_5.add(txtpnGameDataGoes);
		
		JButton[][] cells = new JButton[9][9];
		
		for (int x = 0; x != cells.length; ++x)
			for (int y = 0; y != cells.length; ++y) {
				cells[x][y] = new JButton(x + ", " + y);
				cells[x][y].setBackground(Color.BLACK);
				cells[x][y].addMouseListener(new MouseAdapter() {
					private boolean clicked = false;
					
					@Override
					public void mouseEntered(MouseEvent e) {
						((JButton)e.getSource()).setBackground(Color.CYAN);
					}
					
					@Override
					public void mouseExited(MouseEvent e) {
						if (!clicked)
							((JButton)e.getSource()).setBackground(Color.BLACK);
					}
					
					public void mouseClicked(MouseEvent e) {
						//add to Queue
						((JButton)e.getSource()).setBackground(Color.GREEN);
					}
					
					public void mousePressed(MouseEvent e) {
						clicked = true;
						((JButton)e.getSource()).setBackground(Color.RED);
					}
					
					public void mouseReleased(MouseEvent e) {
						clicked = false;
						mouseExited(e);
					}
				});
				panel.add(cells[x][y]);
			}
			
		cells[5][4].setText("");
		cells[5][4].setIcon(new ImageIcon(BuilderWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif")));
	}
}
