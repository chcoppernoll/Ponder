package gui;

import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import game.Event;
import game.MoveEvent;
import game.PonderLogic;
import game.SpawnEvent;
import game.TurnEvent;

public class SwingGraphics {

	private JFrame frame = new JFrame();
	private JPanel[] players = new JPanel[4];					// Player name tags
	private ImageIcon[][] theme = new ImageIcon[4][2];			// Current theme set
	private JPanel gameHeader = new JPanel();					// Game header
	private JButton[][] cells = new JButton[9][9];				// Tiles
	private JLabel mseLbl = new JLabel();						// Mouse Label
	
	private PonderLogic logic;
	
	private boolean inSettings = false, inGameList = false, allow_local_input = true;
	private JPanel settings = new JPanel(), gameList = new JPanel(), grid = new JPanel();		// grid??

	// TODO Grayson
	/*
	 * Work on artwork
	 * Ensure that the game works on friend's computers
	 * Rework the PonderLogic API (it's getting slightly unruly)
	 * 	Improve PonderLogic implementation (especially spawn logic)
	 * Set up a JAR build system for the artwork
	 */
	
	// TODO Game Logic
	/*
	 * Prevent jumping after sliding
	 * No immediate backward jumps (This stuff needs events to work)
	 * Undo moves
	 * Able to jump after sliding
	 * Be able to play events in client code
	 * Rework system to enable game-switching
	 * Possible to spawn a piece and not be able to end turn (very rare)
	 */
	
	// TODO Game Improvements
	/*
	 * Add a line showing move direction ???
	 * Abstract the graphics and logic by having all communication done with events (?)
	 */

	// Sprint 2
	/*
	 * Event classes
	 * Basic server-client communication within the main game
	 * Server/Database improvements
	 * Rewrite SwingGraphics and PonderLogic
	 */

	/**
	 * Create the application.
	 */
	public SwingGraphics(PonderLogic instance) {
		logic = instance;
		
		theme[0] = new ImageIcon[] {
			new ImageIcon(SwingGraphics.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif")),
			new ImageIcon(SwingGraphics.class.getResource("/com/sun/java/swing/plaf/windows/icons/JavaCup32.png"))
			//piece_with_flag
		};
		theme[1] = new ImageIcon[] {
			new ImageIcon(SwingGraphics.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif")),
			new ImageIcon(SwingGraphics.class.getResource("/com/sun/java/swing/plaf/windows/icons/JavaCup32.png"))
		};
		theme[2] = new ImageIcon[] {
			new ImageIcon(SwingGraphics.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif")),
			new ImageIcon(SwingGraphics.class.getResource("/com/sun/java/swing/plaf/windows/icons/JavaCup32.png"))
		};
		theme[3] = new ImageIcon[] {
			new ImageIcon(SwingGraphics.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif")),
			new ImageIcon(SwingGraphics.class.getResource("/com/sun/java/swing/plaf/windows/icons/JavaCup32.png"))		
		};

		initialize();
	}
	
	/**
	 * Setup a new game state
	 */
	public void reset() {		
		logic.init(cells);
		
		// Game Data Text
		((JLabel)gameHeader.getComponent(0)).setText("Game Data Goes Here");
		
		// Delete old pieces ?
		
		for (int i = 0; i != players.length; ++i)
			setUpPieces(i);
		
		setUpFlags();
	}

	public void setVisible(boolean vis) {
		frame.setVisible(vis);
	}
	
	/**
	 * Highlight spawnable areas for a given player
	 * @param player
	 * @param color
	 */
	private void highlight(int player, Color color) {
		for (JButton[] row : cells)
			for (JButton cell : row)
				if (logic.canSpawn(cell, player))
					logic.setColor(cell, color);
	}
	
	/**
	 * Color the entire grid
	 * @param bg
	 */
	public void color(Color bg) {
		for (JButton[] row : cells)
			for (JButton cell : row)
				logic.setColor(cell, bg);
	}

	/**
	 * Open the settings window
	 */
	private void openSettings() {
		grid.setVisible(false);
		settings.setVisible(true);
		inSettings = true;
	}
	
	/**
	 * Close the settings window
	 */
	private void closeSettings() {
		grid.setVisible(true);
		settings.setVisible(false);
		inSettings = false;
	}
	
	/**
	 * Open the game browser
	 */
	private void openGameList() {
		grid.setVisible(false);
		gameList.setVisible(true);
		inGameList = true;
	}
	
	/**
	 * Close the game browser
	 */
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
				if (allow_local_input && logic.canPlayerSpawn(0)) {
					System.out.println("Clicked P1");
					
					boolean clicked = logic.getStack() == getStack(0);
					logic.setStack(clicked ? null : getStack(0));
					self.highlight(0, clicked ? Color.BLACK : Color.WHITE);
				}
			}
		});
		panel_2.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (allow_local_input && logic.canPlayerSpawn(1)) {
					System.out.println("Clicked P2");

					boolean clicked = logic.getStack() == getStack(1);
					logic.setStack(clicked ? null : getStack(1));
					self.highlight(1, clicked ? Color.BLACK : Color.WHITE);
				}
			}
		});
		panel_3.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (allow_local_input && logic.canPlayerSpawn(2)) {
					System.out.println("Clicked P3");

					boolean clicked = logic.getStack() == getStack(2);
					logic.setStack(clicked ? null : getStack(2));
					self.highlight(2, clicked ? Color.BLACK : Color.WHITE);
				}
			}
		});
		panel_4.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (allow_local_input && logic.canPlayerSpawn(3)) {
					System.out.println("Clicked P4");
					
					boolean clicked = logic.getStack() == getStack(3);
					logic.setStack(clicked ? null : getStack(3));
					self.highlight(3, clicked ? Color.BLACK : Color.WHITE);
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


		// Mouse Labeling
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
					private void updateMouseText(JButton cell) {
						String out = logic.positionOf(cell) + " [ ";
						
						boolean[] flags = logic.flagsOn(cell);
						for (int i = 0; i != flags.length; ++i)
							out += (flags[i] ? i + 1 : "X") + (i != flags.length - 1 ? ", " : " ");
						
						mseLbl.setText(out + "] P" + (logic.getCurrPlayer() + 1));// + " " + logic.turnOver());
					}
					
					public void mouseEntered(MouseEvent e) {
						JButton src = (JButton)e.getSource();
						
						updateMouseText(src);
						src.setBackground(Color.CYAN);
					}
					
					public void mouseExited(MouseEvent e) {
						JButton src = (JButton)e.getSource();
						src.setBackground(logic.getColor(src));
					}
					
					public void mouseClicked(MouseEvent e) {
						if (!allow_local_input) return;
						
						JButton src = (JButton)e.getSource();
						
						switch (e.getButton()) {
							case MouseEvent.BUTTON1:			// LEFT-CLICK
								// Select contextual operations
								switch (logic.click(src)) {
									case PonderLogic.SPAWN_CLICK:
										if (logic.spawn(src, logic.getCurrPlayer())) {
											logic.addEvent(new SpawnEvent(logic.positionOf(src), logic.getCurrPlayer(), false));
											runEvent(logic.lastEvent());
										}
										
										break;
									case PonderLogic.SELECT_CLICK:										
										System.out.println("Select Piece");
										
										logic.select(src);
										logic.setColor(src, Color.BLUE);
										
										break;
									case PonderLogic.MOVE_CLICK:
										JButton from = logic.getFocus();
										
										if (logic.getPieceOwner(src) != -1) return;									// Can't move to a tile where a piece exists
										
										if (logic.canSlide(from, src)) {
											if (logic.hasMoved(from) || !logic.canCapFlag(from, src)) return;		// Movement ended
											logic.addEvent(new MoveEvent(logic.positionOf(from), logic.positionOf(src), true));
											
										} else {
											JButton jmpd = logic.jmpPiece(from, src);
											
											// If jumping a piece (doesn't care about hasMoved since you can't select a piece that has moved)
											if (jmpd != null) {
												if (logic.getPieceOwner(jmpd) != logic.getPieceOwner(from)) {			// Despawn the piece
													logic.addEvent(new SpawnEvent(logic.positionOf(jmpd), logic.getPieceOwner(jmpd), true));
													runEvent(logic.lastEvent());
												}
												
												logic.addEvent(new MoveEvent(logic.positionOf(from), logic.positionOf(src), false));
												
											} else
												return;																// Movement ended

										}

										System.out.println("Moving");
										runEvent(logic.lastEvent());
										updateMouseText(src);						// Update text
										
										break;
									default:
										System.out.println("Defocusing");
										logic.setColor(src, logic.getColor(src) == Color.BLACK ? Color.GREEN : Color.BLACK);
										logic.select(null);
								}
								
								break;
							case MouseEvent.BUTTON2:			// MIDDLE-CLICK
								JButton[][] adj = logic.adjacents(logic.positionOf(src));
								
								for (JButton imm : adj[0])
									if (imm != null) imm.setBackground(Color.YELLOW);
								
								for (JButton nimm : adj[1])
									if (nimm != null) nimm.setBackground(Color.MAGENTA);

								break;
								
							case MouseEvent.BUTTON3:			// RIGHT-CLICK
								break;
								
							default:
						}
						
					}
				});
				
				grid.add(cells[y][x]);
			}
		}
	}
	
	/**
	 * Setup all pieces for the given player
	 * @param player
	 */
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
	
	/**
	 * Create a new piece at the given location
	 * @param block
	 * @param player
	 */
	private void newPiece(JButton block, int player) {
		block.setText("");
		block.setIcon(theme[player][0]);
		logic.addPiece(block, player);
	}
	
	/**
	 * Setup all flags
	 */
	private void setUpFlags() {
		newFlag(cells[1][1], 0);
		newFlag(cells[7][1], 1);
		newFlag(cells[1][7], 2);
		newFlag(cells[7][7], 3);
	}
	
	/**
	 * Create a new flag at the given location
	 * @param block
	 * @param player
	 */
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
	
	/**
	 * Get the given player's nametag
	 * 
	 * @param player
	 */
	public JLabel getLabel(int player) {
		return (JLabel)players[player].getComponents()[0];
	}
	
	/**
	 * Get the game header
	 * @return
	 */
	public JPanel getHeader() {
		return gameHeader;
	}
	
	/**
	 * Get the set of flag icons (the four computers) for the given player
	 * @param player
	 * @return
	 */
	public JLabel[] getFlagIcons(int player) {
		JLabel[] ret = new JLabel[4];
		
		Component[] subs = players[player].getComponents();
		for (int i = 0; i != ret.length; ++i)
			ret[i] = (JLabel)subs[i + 1];
		
		return ret;
	}
	
	/**
	 * Get the spawn stack for a given player
	 * @param player
	 * @return
	 */
	public JPanel getStack(int player) {
		return (JPanel)players[player].getComponents()[5];
	}

	/**
	 * Get the game logic class
	 * @return
	 */
	public PonderLogic getLogic() {
		return logic;
	}

	/**
	 * Move a piece from one component to another (move/exile/spawn)
	 * Also performs necessary logic calls to update the game state
	 * @param from The component where the piece is currently located
	 * @param to The component the piece is being moved to
	 */
	public void move(JComponent from, JComponent to) {
		if (from instanceof JButton) {
			JButton f = (JButton)from;
			
			// Move from cell to cell
			if (to instanceof JButton) {
				JButton t = (JButton)to;
				
				t.setText(null);
				t.setIcon(f.getIcon());
				f.setIcon(null);

				boolean[] flags = logic.flagsOn(f);
				logic.addPiece(t, logic.getPieceOwner(f));			// logic.getCurrPlayer() should also work
				for (int i = 0; i != flags.length; ++i) {			// Currently doesn't allow for dropable flags
					if (flags[i]) {
						logic.removeFlag(f, i);
						logic.addFlag(t, i);
					}
				}
				
			// Move from cell to spawn stack
			} else if (to instanceof JPanel) {
				if (to != getStack(logic.getPieceOwner(f))) throw new ArrayIndexOutOfBoundsException();
				
				logic.addToSpawn(logic.getPieceOwner(f));
				((JPanel)to).add(new JLabel(f.getIcon()));
				f.setIcon(null);
				
				// re-add flag icons
				JButton[] flags = logic.getFlags();
				for (int i = 0; i != flags.length; ++i) {
					if (f == flags[i])
						f.setIcon(theme[i][1]);
				}
			}
			
			logic.removePiece(f);
			
		// Move from spawn stack to cell
		} else if (from instanceof JPanel) {
			JButton t = (JButton)to;
			
			t.setText(null);
			
			// get the icon to switch on
			t.setIcon(((JLabel)from.getComponent(0)).getIcon());
			logic.addPiece(t, logic.getCurrPlayer());					// Relies on current player (Subject to abuse depending on how stack selection works)
			logic.popFromSpawn(logic.getCurrPlayer());
			
			from.remove(0);
			from.revalidate();
			from.repaint();
		}
	}

	/**
	 * Start accepting local input
	 */
	public void acceptInput() {
		allow_local_input = true;
	}
	
	/**
	 * Stop accepting local input
	 */
	public void stopInput() {
		allow_local_input = false;
	}

	// Placeholder for running non-local moves
	public void runEvent(Event e) {
		if (e instanceof MoveEvent) {
			MoveEvent event = (MoveEvent)e;
			JButton src = logic.getPiece(event.to);
			
			logic.enterMovePhase();						// Prevent spawning actions from occurring
			move(logic.getPiece(event.from), src);							// Perform the move

			logic.setColor(src, Color.GREEN);
			logic.select(src);
			
		} else if (e instanceof SpawnEvent) {
			SpawnEvent event = (SpawnEvent)e;
			
			if (event.exiled) {
				move(logic.getPiece(event.pos), getStack(event.owner));
				
			} else {
				move(logic.getStack(), logic.getPiece(event.pos));				// Spawn the piece
				logic.setStack(null);
				color(Color.BLACK);
			}
		} else if (e instanceof TurnEvent) {
			TurnEvent event = (TurnEvent)e;
		}
	}
	
	public void undoEvent(Event e) {
		if (e instanceof MoveEvent) {
			
		} else if (e instanceof SpawnEvent) {
			
		}
	}
}
