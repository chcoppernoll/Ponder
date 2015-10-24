package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextPane;

import game.GameLogic;
import game.GraphicsLogic;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.FlowLayout;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.SwingConstants;

// Move setup to SwingGraphicsRaw?
public class SwingGraphics {

	private JFrame frame = new JFrame();
	private JPanel[] players = new JPanel[4];					// Player name tags
	private ImageIcon[][] theme = new ImageIcon[4][2];			// Current theme set
	private JPanel gameHeader = new JPanel();					// Game header
	private JButton[][] cells = new JButton[9][9];				// Tiles
	private JLabel mseLbl = new JLabel();
	
	private GraphicsLogic logic;
	
	private boolean inSettings = false, inGameList = false;
	private JPanel settings = new JPanel(), gameList = new JPanel(), grid = new JPanel();		// grid??
	
	// TODO Alex
	/*
	 * Get Spawn Area to stack from the bottom for players 2 and 3
	 * Set up a JAR build system for the artwork
	 */
	
	// TODO Grayson
	/*
	 * Start working on game logic and integration with the graphics
	 * Work on artwork
	 */
	
	// TODO Add in logic hooks for graphics (once we start implementing game logic)
	
	// PROBLEM
	// How to represent Flags and pieces on the same tile

	/**
	 * Create the application.
	 */
	public SwingGraphics(GraphicsLogic instance) {
		logic = instance;
		
		ImageIcon piece = new ImageIcon(SwingGraphics.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif"));
		ImageIcon flag = new ImageIcon(SwingGraphics.class.getResource("/com/sun/java/swing/plaf/windows/icons/JavaCup32.png"));
		
		theme[0] = new ImageIcon[] {
			piece,
			flag
			//piece_with_flag
		};
		theme[1] = new ImageIcon[] {
			piece,
			flag
		};
		theme[2] = new ImageIcon[] {
			piece,
			flag
		};
		theme[3] = new ImageIcon[] {
			piece,
			flag		
		};

		initialize();
	}
	
	public void reset() {		
		// Game Data Text
		((JLabel)gameHeader.getComponent(0)).setText("Game Data Goes Here");
		
		// Testing piece display
		for (int i = 0; i != players.length; ++i)
			setUpPieces(i);
		
		setUpFlags();
		
		move(cells[6][7], cells[6][6]);
		move(cells[7][6], getStack(3));
		move(cells[3][7], getStack(2));
		move(cells[7][2], cells[7][1]);
		move(cells[6][1], getStack(1));
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		GraphicsLogic logic = new GraphicsLogic();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingGraphics window = new SwingGraphics(logic);
					window.reset();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	// Highlight spawnable areas
		// Doesn't handle dual spawning
	private void highlight(int player) {
		if (logic.getMana() == 0.5) {
			
			
			return;
		}
		
		for (int y = 0; y != cells.length; ++y)
			for (int x = 0; x != cells.length; ++x)
				if (cells[y][x].getIcon() == null && logic.canSpawn(getAdjacent(x, y)[0], player))
					cells[y][x].setBackground(Color.WHITE);

	}

	// Tests if p is outside of the grid range
	private boolean outOfRange(int p) {
		return p < 0 || p > 8;
	}
	
	// Tests if (x, y) is an invalid grid position
	private boolean invalidPoint(int x, int y) {
		return outOfRange(x) || outOfRange(y);
	}

	// Open/Close the settings window
	private void openSettings() {
		grid.setVisible(false);
		settings.setVisible(true);
		inSettings = true;
	}
	
	private void openGameList() {
		grid.setVisible(false);
		gameList.setVisible(true);
		inGameList = true;
	}
	
	// Open/Close the game list window
	private void closeSettings() {
		grid.setVisible(true);
		settings.setVisible(false);
		inSettings = false;
	}
	
	private void closeGameList() {
		grid.setVisible(true);
		gameList.setVisible(false);
		inGameList = false;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame.setResizable(false);
		frame.setBounds(100, 100, 810, 640);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		// Settings button
		JButton btnSettings = new JButton("Settings");
		btnSettings.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				System.out.println("Clicked Settings");
				
				if(inGameList) {
					closeGameList();
				}
				if(inSettings) {
					closeSettings();
				} else {
					openSettings();
					inSettings = true;
				}
			}
		});
		btnSettings.setBounds(20, 30, 110, 25);
		frame.getContentPane().add(btnSettings);
		
		// Game List button
		JButton btnGameList = new JButton("Game List");
		btnGameList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				System.out.println("Clicked Game List");
				
				if(inSettings) {
					//close settings menu before proceeding
					closeSettings();
				}
				if(inGameList) {
					closeGameList();
				} else {
					openGameList();
					inGameList = true;
				}
			}
		});
		btnGameList.setBounds(670, 30, 110, 25);
		frame.getContentPane().add(btnGameList);

		// Set Up Player Quadrants
		for (int i = 0; i != players.length; ++i) {
			players[i] = new JPanel();
			players[i].setBounds(i < 2 ? 41 : 670, i % 2 == 0 ? 84 : 363, 89, 227);
			players[i].setLayout(null);
			
			// Add player label
			JLabel player_name = new JLabel("Player " + (i + 1));
			player_name.setBounds(i < 2 ? 7 : 0, i % 2 == 0 ? 0 : 198, 82, 23);
			if (i < 2) player_name.setHorizontalAlignment(SwingConstants.RIGHT);
			players[i].add(player_name);
			
			// Add flag control icons
			for (int j = 0; j != 4; ++j) {
				JLabel flag_icon = new JLabel(new ImageIcon(SwingGraphics.class.getResource("/com/sun/java/swing/plaf/windows/icons/Computer.gif")));
				
				if (i < 2)
					flag_icon.setBounds(69 - 22 * j, i % 2 == 0 ? 34 : 173, 20, 20);
				else
					flag_icon.setBounds(0 + 22 * j, i % 2 == 0 ? 34 : 173, 20, 20);
				
				players[i].add(flag_icon);
			}
			
			// Add Spawn Stack
			// Waiting for work on spawn stack
			
			frame.getContentPane().add(players[i]);
		}
		
		// Player "Spawn" stack
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(34, 65, 55, 162);
		players[0].add(panel_1);
		panel_1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		JPanel panel_2 = new JPanel();
		panel_2.setBounds(34, 0, 55, 162);
		players[1].add(panel_2);
		panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(0, 65, 55, 162);
		players[2].add(panel_3);
		panel_3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JPanel panel_4 = new JPanel();
		panel_4.setBounds(0, 0, 55, 162);
		players[3].add(panel_4);
		panel_4.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		// Spawn code
		SwingGraphics self = this;
		panel_1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (panel_1.getComponents().length != 0) {
					System.out.println("Clicked P1");
					self.highlight(0);
				}
			}
		});
		panel_2.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (panel_2.getComponents().length != 0) {
					System.out.println("Clicked P2");
					self.highlight(1);
				}
			}
		});
		panel_3.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (panel_3.getComponents().length != 0) {
					System.out.println("Clicked P3");
					self.highlight(2);
				}
			}
		});
		panel_4.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (panel_4.getComponents().length != 0) {
					// add in some call to GraphicsLogic
					System.out.println("Clicked P4");
					self.highlight(3);
				}
			}
		});

		
		// Set up game data header
		gameHeader.setBounds(150, 11, 500, 62);
		frame.getContentPane().add(gameHeader);
		gameHeader.setLayout(null);
		
		JLabel txtpnGameDataGoes = new JLabel();
		txtpnGameDataGoes.setBounds(149, 31, 209, 20);
		gameHeader.add(txtpnGameDataGoes);
		
		// Settings window
		settings.setVisible(false);;
		settings.setBounds(150, 84, 500, 500);
		frame.getContentPane().add(settings);
		settings.setBackground(Color.WHITE);
		settings.setLayout(new GridLayout(9, 9, 1, 1));
		

		// Game List window
		gameList.setVisible(false);
		gameList.setBounds(150, 84, 500, 500);
		frame.getContentPane().add(gameList);
		gameList.setBackground(Color.WHITE);
		gameList.setLayout(new GridLayout(9, 9, 1, 1));
		
		JPanel gameList = new JPanel();
		gameList.setVisible(false);
		gameList.setBounds(150, 84, 500, 500);
		frame.getContentPane().add(gameList);
		gameList.setBackground(Color.WHITE);
		gameList.setLayout(new GridLayout(9, 9, 1, 1));

		
		mseLbl.setHorizontalAlignment(SwingConstants.CENTER);
		mseLbl.setBounds(313, 590, 175, 14);
		frame.getContentPane().add(mseLbl);
		
		// Game Grid
		grid.setBackground(Color.BLACK);
		grid.setBounds(150, 84, 500, 500);
		frame.getContentPane().add(grid);
		grid.setLayout(new GridLayout(9, 9, 1, 1));
		
		for (int y = 0; y != cells.length; ++y) {
			for (int x = 0; x != cells.length; ++x) {
				cells[y][x] = new JButton(x + ", " + y);
				cells[y][x].setBackground(Color.BLACK);
				
				cells[y][x].addMouseListener(new MouseAdapter() {
					public void mouseEntered(MouseEvent e) {
						JButton src = (JButton)e.getSource();
						
						mseLbl.setText(String.format("(%d, %d) %s", src.getX() / 55, src.getY() / 55, Arrays.toString(logic.getFlags(src))));
						
						src.setBackground(Color.CYAN);
					}
					
					public void mouseExited(MouseEvent e) {
						JButton src = (JButton)e.getSource();
						src.setBackground(logic.isClicked(src) ? Color.GREEN : Color.BLACK);
					}
					
					public void mouseClicked(MouseEvent e) {
						JButton src = (JButton)e.getSource();
						
						//System.out.println(logic.getPiece(src) + ": " + Arrays.toString(logic.getFlags(src)));
						
						switch (e.getButton()) {
							case MouseEvent.BUTTON1:
								logic.click(src);
								src.setBackground(logic.isClicked(src) ? Color.GREEN : Color.CYAN);
								
								break;
							case MouseEvent.BUTTON3:
								JButton[][] adj = getAdjacent(src.getX() / 55, src.getY() / 55);
								
								for (JButton imm : adj[0])
									if (imm != null) imm.setBackground(Color.YELLOW);
								
								for (JButton nimm : adj[1])
									if (nimm != null) nimm.setBackground(Color.MAGENTA);
								
								break;
								
							default:
						}
						
					}
				});
				
				grid.add(cells[y][x]);
				logic.add(cells[y][x], y, x);
			}
		}
		
	}
	
	private void setUpPieces(int player) {
		int x = player < 2 ? 1 : 7;
		int y = player % 2 == 0 ? 1 : 7;
		int dx = player < 2 ? 1 : -1;
		int dy = player % 2 == 0 ? 1 : -1;
		
		newPiece(cells[y + dy][x], player);
		newPiece(cells[y + 2 * dy][x], player);
		newPiece(cells[y][x + dx], player);
		newPiece(cells[y][x + 2 * dx], player);
	}
	
	private void newPiece(JButton block, int player) {
		block.setText("");
		block.setIcon(theme[player][0]);
		logic.addPiece(block, player);
	}
	
	private void setUpFlags() {
		newFlag(cells[1][1], 0);
		newFlag(cells[1][7], 1);
		newFlag(cells[7][1], 2);
		newFlag(cells[7][7], 3);
	}
	
	private void newFlag(JButton block, int player) {
		block.setText("");
		block.setIcon(theme[player][1]);
		logic.addFlag(block, player);
	}
	
	/**
	 * Sets the theming set used for pieces
	 * @param icons A 2d array filled by the following rules
	 * The four columns correspond to the four players
	 * The columns are filled accordingly
	 * 		0 - Piece Icon
	 * 		1 - Flag Icon
	 * 		2 - Piece/Flag Icon
	 */
	public void setTheme(ImageIcon[][] icons) {
		theme = icons;
	}
	
	// Get the given player's name tag
	public JLabel getLabel(int player) {
		return (JLabel)players[player].getComponents()[0];
	}
	
	// Get the game header
	public JPanel getHeader() {
		return gameHeader;
	}
	
	// Get the set of flag icons for the given player
	public JLabel[] getFlagIcons(int player) {
		JLabel[] ret = new JLabel[4];
		
		Component[] subs = players[player].getComponents();
		for (int i = 0; i != ret.length; ++i)
			ret[i] = (JLabel)subs[i + 1];
		
		return ret;
	}
	
	// Get the spawn stack for the given player
	public JPanel getStack(int player) {
		return (JPanel)players[player].getComponents()[5];
	}

	// Get the GraphicsLogic object
	public GraphicsLogic getLogic() {
		return logic;
	}
	
	/**
	 * @param x
	 * @param y
	 * @return The tile at the given grid position
	 */
	public JButton getCell(int x, int y) {
		return !invalidPoint(x, y) ? cells[y][x] : null;
	}

	/**
	 * Get all adjacent tiles of the given grid position
	 * @param x
	 * @param y
	 * @return A 2d array filed by the following rules
	 * The first column is for slide adjacent tiles
	 * The second column is for jump adjacent tiles
	 * 
	 * The indices of the columns correspond to this chart
	 * 0 3 5 		So the tile at index 0 corresponds to the 
	 * 1 X 6		Tile to the NW of the current position
	 * 2 4 7
	 */
	public JButton[][] getAdjacent(int x, int y) {
		if (invalidPoint(x, y)) return null;
		
		JButton[][] ret = new JButton[2][8];
		int count = 0;
		
		for (int dx = -1; dx != 2; ++dx) {
			for (int dy = -1; dy != 2; ++dy) {
				if (!(dy == 0 && dx == 0)) {
					ret[0][count] = getCell(x + dx, y + dy);
					ret[1][count++] = getCell(x + 2 * dx, y + 2 * dy);
				}
			}
		}
		
		return ret;
	}

	// Whether tile b can be reached from tile a
	public boolean canReach(JButton a, JButton b) {
		return canSlide(a, b) || canJump(a, b);
	}

	/**
	 * @param a
	 * @param b
	 * @return Whether tile b can be reached by sliding from tile a
	 */
	public boolean canSlide(JButton a, JButton b) {
		int x0 = a.getX() / 55, x1 = b.getX() / 55;
		int y0 = a.getY() / 55, y1 = b.getY() / 55;
		int dx = Math.abs(x0 - x1), dy = Math.abs(y0 - y1);
		
		return dx == 1 || dy == 1;
	}
	
	/**
	 * @param a
	 * @param b
	 * @return Whether tile b can be reached by jumping from tile a
	 */
	public boolean canJump(JButton a, JButton b) {
		int x0 = a.getX() / 55, x1 = b.getX() / 55;
		int y0 = a.getY() / 55, y1 = b.getY() / 55;
		int dx = Math.abs(x0 - x1), dy = Math.abs(y0 - y1);
		
		return dx == 2 || dy == 2;
	}
	
	// Will need to add in hooks once we start implementing game logic
	/**
	 * Move a "graphical piece" from one component to another
	 * @param from The component where the piece is currently located
	 * @param to The component the piece is being moved to
	 */
	public void move(JComponent from, JComponent to) {
		if (from instanceof JButton) {
			JButton f = (JButton)from;
			
			// Move from cell to cell
			if (to instanceof JButton) {
				JButton t = (JButton)to;
				
				t.setIcon(f.getIcon());
				t.setText("");
				f.setIcon(null);
				logic.addPiece(t, logic.getPiece(f));			// logic.getCurrPlayer() should also work
				// move flags
				
			// Move from cell to spawn stack
			} else if (to instanceof JPanel) {
				JLabel spawn = new JLabel(f.getIcon());
				((JPanel)to).add(spawn);
				f.setIcon(null);
			}
			
			logic.removePiece(f);
			
		// Move from spawn stack to cell
		} else if (from instanceof JPanel) {
			JButton t = (JButton)to;
			
			// get the icon to switch on
			t.setIcon(((JButton)from.getComponent(0)).getIcon());
			logic.addPiece(t, logic.getCurrPlayer());
			from.remove(0);
		}
	}
}
