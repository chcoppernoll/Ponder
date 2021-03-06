package game;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import javax.swing.JButton;
import javax.swing.JPanel;

import gui.SwingGraphics;
import network.Event;
import network.MoveEvent;
import network.Position;
import network.SpawnEvent;

class GridData {
	public int owner = -1, num_flags = 0;
	public boolean[] has_flag = new boolean[4];
	public Color color = Color.BLACK;
	public boolean has_moved = false;
	
	public String toString() {
		return "owner: " + owner + ", num_flags: " + num_flags + ", has_moved: " + has_moved;
	}
}

public class PonderLogic {
	private double mana = 1.;
	private int curr_player = -1, curr_turn = -1;
	private boolean in_move_phase = false;
	private JButton[] flags = new JButton[4];
	private JPanel stack;
	private JButton focus = null;
	private HashMap<JButton, GridData> data = new HashMap<>();
	private HashMap<Position, JButton> grid = new HashMap<>();		// If two objects have the same position only one is stored
	
	@SuppressWarnings("unchecked")
	private PriorityQueue<Integer>[] spawn_sets = (PriorityQueue<Integer>[])new PriorityQueue[4];
	private LinkedList<Event> curr_move = new LinkedList<>();
	
	public void reset() {
		mana = 1.;
		curr_player = curr_turn = -1;
		in_move_phase = false;
		stack = null;
		focus = null;
		curr_move.clear();
		for (PriorityQueue<Integer> q : spawn_sets)
			q.clear();
	}
	
	/**
	 * @param pos
	 * @return whether pos is a valid grid position
	 */
	private boolean invalidPoint(Position pos) {
		return (pos.y < 0 || pos.y > 8) || (pos.x < 0 || pos.y > 8);
	}
	
	/**
	 * Get the list of adjacent points to a given position
	 * [0] is the list of all points p where canSlide(pos, p) is true
	 * [1] is the list of all points p where canJmp(pos, p) is true
	 * 
	 * The elements in the array are arranged as so
	 * 0 3 5
	 * 1 X 6
	 * 2 4 7
	 * @param pos
	 * @return
	 */
	public JButton[][] adjacents(Position pos) {
		if (invalidPoint(pos)) return null;
		
		JButton[][] ret = new JButton[2][8];
		int count = 0;
		
		for (int dx = -1; dx != 2; ++dx) {
			for (int dy = -1; dy != 2; ++dy) {
				if (!(dy == 0 && dx == 0)) {
					ret[0][count] = grid.get(pos.add(dx, dy));
					ret[1][count++] = grid.get(pos.add(2 * dx, 2 * dy));
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * Get the position of the given tile
	 * @param cell
	 * @return
	 */
	public Position positionOf(JButton cell) {
		return new Position(cell.getX() / 55, cell.getY() / 55);
	}
	
	/**
	 * Create a new position object with the given coordinates
	 */
	public Position makePosition(int x, int y) {
		return new Position(x, y);
	}
	
	/**
	 * Determine whether it is physically possible for the player to spawn at the given position
	 * @param pos
	 * @param player
	 * @return
	 */
	private boolean canSpawn(Position pos, int player) {
		if (spawn_sets[player].isEmpty() || spawn_sets[player].peek() != 0) return false;
		
		for (JButton adj : adjacents(pos)[0]) {
			if (adj != null) {
				int cell_piece = data.get(adj).owner;
				boolean[] _flags = data.get(adj).has_flag;

				if (cell_piece == -1 ? (_flags[0] || _flags[1] || _flags[2] || _flags[3]) : cell_piece != player) return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Determine whether the player can spawn at the given tile, accounting for mana and other pieces
	 * @param a
	 * @param player
	 * @return
	 */
	public boolean canSpawn(JButton a, int player) {
		if (mana < .5 || getPieceOwner(a) != -1 || flags[0] == a || flags[1] == a || flags[2] == a || flags[3] == a)
			return false;
		
		boolean spawnable = canSpawn(positionOf(a), player);
		
		// This is only true if the player has control of their flag
		if (spawnable && mana == .5) spawnable = spawnable && canSlide(a, flags[player]);
		
		return spawnable;
	}
	
	/**
	 * Determine the piece jumped by moving from a -> b
	 * @param a
	 * @param b
	 * @return null if a->b is an illegal move
	 */
    public JButton jmpPiece(JButton a, JButton b) {
		if (!canJmp(a, b) || !canCapFlag(a, b) || isBackJmp(a, b) || wasSlide()) return null;
		
		// Add in check to prevent backwards move
		
		Position a_p = positionOf(a), b_p = positionOf(b);
		int dx = a_p.x - b_p.x, dy = a_p.y - b_p.y;
		
		JButton ret = grid.get(b_p.add(dx / 2, dy / 2));		
		return getPieceOwner(ret) != -1 ? ret : null;
	}
		
    /**
     * @param a
     * @param b
     * @return whether a and b are in sliding distance of each other
     */
	public boolean canSlide(JButton a, JButton b) {
		double dist = positionOf(a).distance(positionOf(b));
		
		return dist == 1 || dist == Math.sqrt(2);
	}
	
	/**
	 * @param a
	 * @param b
	 * @return whether a and b are in jumping distance of each other
	 */
	public boolean canJmp(JButton a, JButton b) {
		double dist = positionOf(a).distance(positionOf(b));
		
		return dist == 2 || dist == Math.sqrt(8);
	}
	
	/**
	 * Init the logic state to the passed board
	 * @param board
	 */
	public void init(JButton[][] board) {
		if (data.size() > 0) data.clear();
		if (grid.size() > 0) grid.clear();
		
		for (int y = 0; y != board.length; ++y) {
			for (int x = 0; x != board[y].length; ++x) {
				board[y][x].setIcon(null);
				data.put(board[y][x], new GridData());
				grid.put(new Position(x, y), board[y][x]);
			}
		}
		
		for (int i = 0; i != spawn_sets.length; ++i)
			spawn_sets[i] = new PriorityQueue<>(4);
	}
	
	/**
	 * Add a piece at the given position. The piece is controlled by the given player
	 * @param a
	 * @param player
	 */
	public void addPiece(JButton a, int player) {
		data.get(a).owner = player;
		data.get(a).has_moved = true;
	}
	
	/**
	 * Remove the piece at the given position
	 * This can be chained with addPiece to implement movement
	 * @param a
	 */
	public void removePiece(JButton a) {
		addPiece(a, -1);
	}
	
	/**
	 * Add the given player's flag at the given tile
	 * @param a
	 * @param player
	 */
	public void addFlag(JButton a, int player) {
		data.get(a).has_flag[player] = true;
		data.get(a).num_flags++;
		flags[player] = a;
	}
	
	/**
	 * Remove the given player's flag from the position
	 * Can be chained with addFlag to implement movement
	 * @param a
	 * @param player
	 */
	public void removeFlag(JButton a, int player) {
		data.get(a).has_flag[player] = false;
		data.get(a).num_flags--;
		flags[player] = null;
	}
	
	/**
	 * Determine the id of the winner of the game
	 * @return -1 if no victor
	 */
	public int victor() {
		int[] num_cont = new int[4];
		
		for (JButton flag : flags) {
			if (data.get(flag).owner != -1)
				num_cont[data.get(flag).owner]++;
		}
		
		for (int i = 0; i != num_cont.length; ++i)
			if (num_cont[i] >= 3) return i;
		
		return -1;
	}

	/**
	 * Attempt to spawn the player's piece at the given cell
	 * @param cell
	 * @param player
	 * @return success
	 */
	public boolean spawn(JButton cell, int player) {
		System.out.println("Spawning");
		if (canSpawn(positionOf(cell), player)) {
			double cost = canSlide(cell, flags[player]) ? 0.5 : 1.0;
			
			if (cost > mana) return false;
			
			mana -= cost;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Get the current player's available mana
	 */
	public double getMana() {
		return mana;
	}
	
	/**
	 * Get the graphical color of the given cell
	 */
	public Color getColor(JButton cell) {
		return data.get(cell).color;
	}
	
	/**
	 * Set the graphical color of the given cell
	 */
	public void setColor(JButton cell, Color color) {
		cell.setBackground(data.get(cell).color = color);
	}

	/**
	 * Get the current selected spawn stack
	 * @return
	 */
	public JPanel getStack() {
		return stack;
	}
	
	/**
	 * Load up the given spawn stack
	 * @param n_stack
	 */
	public void setStack(JPanel n_stack) {
		stack = n_stack;
	}

	/**
	 * @param cell
	 * @return the list of flags on the given tile
	 */
	public boolean[] flagsOn(JButton cell) {
		return data.get(cell).has_flag;
	}
	
	/**
	 * @return all flag tiles
	 */
	public JButton[] getFlags() {
		return flags;
	}
	
	/**
	 * Returns whether the selected piece is capable of moving
	 */
	public boolean canMove(JButton cell) {
		return getPieceOwner(cell) == curr_player && !data.get(cell).has_moved;
	}
	
	/**
	 * Returns whether a move can occur between from and to
	 */
	public boolean canMove(JButton from, JButton to) {
		return (canSlide(from, to) && !data.get(from).has_moved) || jmpPiece(from, to) != null;
	}
	
	/**
	 * Prevent the player from spawning after moving
	 */
	public void enterMovePhase() {
		in_move_phase = true;
	}
	
	/**
	 * @param player
	 * @return whether the given player can spawn
	 */
	public boolean canPlayerSpawn(int player) {
		return !in_move_phase
			 && curr_player == player
			 && mana > 0
			 && !spawn_sets[player].isEmpty()
			 && spawn_sets[player].peek() <= 0;
	}
	
	/**
	 * Add a piece to the given player's spawn stack
	 * @param player
	 */
	public void addToSpawn(int player) {
		spawn_sets[player].add(2);
	}

	/**
	 * Remove a piece from the given player's spawn stack
	 * @param player
	 */
	public void popFromSpawn(int player) {
		spawn_sets[player].poll();
	}
	
	/**
	 * @param piece
	 * @param tile
	 * @return whether the piece can move onto the given tile considering flag locations
	 */
	public boolean canCapFlag(JButton piece, JButton tile) {
		switch (data.get(piece).num_flags) {
			case 0:													// Piece has no flags
				if (data.get(tile).num_flags <= 1)					// If the target tile has 1 or less flags
					return true;
			case 1:													// Piece has 1 flag
				int owner = getPieceOwner(piece);
				if (data.get(tile).num_flags >= 1)					// Determine if one of the tiles has the players flag
					return flagsOn(tile)[owner] || flagsOn(piece)[owner];
			default:												// Piece has 2+ flags
				return true;										// One of the flags must be the players
		}
	}
		
	/**
	 * Checks whether a piece has moved already
	 */
	public boolean hasMoved(JButton cell) {
		return data.get(cell).has_moved;
	}
	
	/**
	 * Select the given tile (for movement purposes)
	 * @param cell
	 */
	public void select(JButton cell) {
		focus = cell;
	}
	
	/**
	 * @return the currently selected tile
	 */
	public JButton getFocus() {
		return focus;
	}

	/**
	 * Decrement the spawn delay for all pieces in the given player's spawn stack
	 * @param player
	 */
	public void decStackDelay(int player) {
		if (spawn_sets[player].isEmpty()) return;
		
		PriorityQueue<Integer> replacement = new PriorityQueue<>(4);
		
		for (Integer i : spawn_sets[player])
			replacement.add(i > 0 ? --i : i);
		
		spawn_sets[player] = replacement;
	}
	
	/**
	 * Determine whether the given piece is surrounded.
	 * A piece is surrounded when there is no tile p, where canSlide(piece, p),
	 * That does not have a piece controlled by an opponent on it
	 * @param piece
	 * @return
	 */
	public boolean isSurrounded(JButton piece) {
		int owner = getPieceOwner(piece);
		
		if (owner == -1) return false;
		
		for (JButton adj : adjacents(positionOf(piece))[0])
			if (adj != null) {
				int __ = getPieceOwner(adj);
				
				if (__ == -1 || __ == owner) return false;
			}
		
		return true;
	}
	
	/**
	 * @return the list of all pieces that are currently surrounded
	 */
	public List<JButton> getSurrounded() {
		List<JButton> ret = new LinkedList<>();
		
		for (JButton piece : grid.values())
			if (piece != null && isSurrounded(piece))
				ret.add(piece);
		
		return ret;
	}
	
	/**
	 * @param cell
	 * @return the owner of the piece at the given tile
	 */
	public int getPieceOwner(JButton cell) {
		return cell != null ? data.get(cell).owner : -1;
	}
	
	public int getCurrPlayer() {
		return curr_player;
	}
	
	/**
	 * @param elem
	 * @return whether the given tile is the current focus
	 */
	public boolean isClicked(JButton elem) {
		return elem == focus;
	}
	
	/**
	 * Process context-sensitive input to the given tile
	 * @param elem
	 * @return
	 */
	public int click(JButton elem) {
		if (stack != null) return SPAWN_CLICK;
		if (focus == null && canMove(elem)) return SELECT_CLICK;
		if (focus != null && focus != elem) return MOVE_CLICK;
		
		return 0;
	}
	
	/** 
	 * Update the logic member for the next turn
	 */
	public void nextTurn() {
		mana = 1;
		
		++curr_turn;
		curr_player = (curr_player + 1) % 4;
		decStackDelay(curr_player);
		
		in_move_phase = false;
		curr_move.clear();
		
		for (JButton piece : grid.values())
			if (piece != null)
				data.get(piece).has_moved = false;
	}
	
	/**
	 * Return whether the turn is over
	 * @return
	 */
	public boolean turnOver() {
		//player has pieces to move || player has pieces to spawn
		return in_move_phase ? focus == null
							 : spawn_sets[curr_player].size() < 4 ? (spawn_sets[curr_player].size() == 3 && mana < 1)
									 							  : !canPlayerSpawn(curr_player);
	}
	
	public void debug() {
		System.out.println(spawn_sets[curr_player].toString());
	}
	
	/**
	 * Add the event to the event queue
	 * @param e
	 * @return
	 */
	public List<Event> addEvent (Event e) {
		curr_move.addLast(e);
		return curr_move;
	}
	
	/**
	 * Clear the event queue
	 */
	public void clearEvents() {
		curr_move.clear();
	}

	/**
	 * Check if the given move is a back jump
	 */
	public boolean isBackJmp(JButton start, JButton end) {
		if (curr_move.isEmpty()) return false;
		
		Event lastEvent = curr_move.getLast();
		
		return (lastEvent instanceof MoveEvent) && positionOf(end).equals(((MoveEvent)lastEvent).from);
	}
	
	/**
	 * Check if the last move was a slide
	 */
	public boolean wasSlide() {
		if (curr_move.isEmpty()) return false;
		
		Event last = curr_move.getLast();
		return last instanceof MoveEvent && ((MoveEvent)last).isSlide;
	}
	
	/**
	 * Undo the last event. Undoes two events if the last was an exile
	 * @param view
	 */
	public void undoEvent(SwingGraphics view) {
		if (curr_move.isEmpty()) return;
		Event e = curr_move.removeLast();
		
		if (e instanceof MoveEvent) {
			MoveEvent event = (MoveEvent)e;
			JButton start_tile = getPiece(event.from), end_tile = getPiece(event.to);
			
			// Undo movement action
			view.move(end_tile, start_tile);
			select(start_tile);
			setColor(end_tile, Color.BLACK);
			
			// prevent cap'd flags from moving
				// NOTE: This won't be necessary if I add in pick/drop functionality (move would only move the attached flags)
					// That would entail the creation of another event though
			boolean fs[] = flagsOn(start_tile);
			boolean found = false;
			for (int i = 0; i != 4; ++i)
				if (fs[i] != event.movedFlags[i]) {
					removeFlag(start_tile, i);
					addFlag(end_tile, i);
					
					if (!found) {
						end_tile.setIcon(view.getTheme()[i][1]);
						found = true;
					}
				}
			
			// Check whether the last move exiled a piece
			if (!curr_move.isEmpty() && curr_move.getLast() instanceof SpawnEvent && ((SpawnEvent)curr_move.getLast()).exiled)
				undoEvent(view);
				
			// Allow for respawning
			else if (curr_move.isEmpty() || !(curr_move.getLast() instanceof MoveEvent)){
				in_move_phase = false;
				data.get(start_tile).has_moved = false;
			}
			
		} else if (e instanceof SpawnEvent) {
			SpawnEvent event = (SpawnEvent)e;

			// Undo piece exiling 
			if (event.exiled) {
				view.move(view.getStack(event.owner), grid.get(event.pos));
				data.get(getPiece(event.pos)).owner = event.owner;

			// Undo piece spawning
			} else {
				PriorityQueue<Integer> tmp = spawn_sets[curr_player];
				spawn_sets[curr_player] = new PriorityQueue<>();							// Use temporary spawn stack (move pushes 2 onto the queue)
				view.move(getPiece(event.pos), view.getStack(curr_player));
				spawn_sets[curr_player] = tmp;
				spawn_sets[curr_player].add(0);
				
				// Reset mana cost
				mana += canSlide(getPiece(event.pos), flags[curr_player]) ? 0.5 : 1.0;
			}
		}
			
	}
		
	/**
	 * Return the last added event
	 * @return
	 */
	public Event lastEvent() {
		return curr_move.isEmpty() ? null : curr_move.getLast();
	}
	
	public LinkedList<Event> getMove() {
		return curr_move;
	}
	
	/**
	 * Get the piece at the given position
	 * @param a
	 * @return
	 */
	public JButton getPiece(Position a) {
		return grid.get(a);
	}
	
	/**
	 * Return the current turn number
	 * @return
	 */
	public int getCurrTurn() {
		return curr_turn;
	}
	
	public static final int SPAWN_CLICK = 1, SELECT_CLICK = 2, MOVE_CLICK = 3;
}
