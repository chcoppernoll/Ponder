package game;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import javax.swing.JButton;
import javax.swing.JPanel;

class GridData {
	public int owner = -1, num_flags = 0;
	public boolean[] has_flag = new boolean[4];
	public Color color = Color.BLACK;
	public boolean has_moved = false;
	
}

class Position {
	public final int x, y;
	
	public Position(int _x, int _y) {
		x = _x; y = _y;
	}
	
	public Position add(int dx, int dy) {
		return new Position(x + dx, y + dy);
	}

	public double distance(Position a) {
		int dx = x - a.x, dy = y - a.y;
		
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof Position)) return false;
		
		Position other = (Position)obj;
		return other.x == x && other.y == y;
	}

	public int hashCode() {
        int hash = ((17 + x) << 5) - (17 + x);
        return ((hash + y) << 5) - (hash + y);
	}
	
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}

public class PonderLogic implements GameLogic<JButton> {
	private double mana = 1.;
	private int curr_player = -1;
	private boolean in_move_phase = false;
	private JButton[] flags = new JButton[4];
	private JPanel stack;
	private JButton focus = null;
	private HashMap<JButton, GridData> data = new HashMap<>();
	private HashMap<Position, JButton> grid = new HashMap<>();		// If two objects have the same position only one is stored
	
	@SuppressWarnings("unchecked")
	private PriorityQueue<Integer>[] spawn_sets = (PriorityQueue<Integer>[])new PriorityQueue[4];
	private LinkedList<Event> curr_move = new LinkedList<>();
	
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
	
	public double getMana() {
		return mana;
	}
	
	//graphics help
	public Color getColor(JButton cell) {
		return data.get(cell).color;
	}
	
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
	
	
	// Workspace methods
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
		return !in_move_phase && curr_player == player && mana > 0 && !spawn_sets[player].isEmpty() && spawn_sets[player].peek() <= 0;
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
	
	// Non-workspace
	
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
		
		curr_player = (curr_player + 1) % 4;
		decStackDelay(curr_player);
		
		in_move_phase = false;
		curr_move.clear();
		
		for (JButton piece : grid.values())
			if (piece != null)
				data.get(piece).has_moved = false;
	}
	
	public boolean turnOver() {
		//player has pieces to move || player has pieces to spawn
		return in_move_phase ? focus == null
							 : spawn_sets[curr_player].size() < 4 ? false
									 							  : !canPlayerSpawn(curr_player);
	}
	
	/**
	 * Determines if the last move was a slide
	 * @return True if the last move was a slide, false otherwise
	 */
	public boolean isSlide() {
		if(curr_move.isEmpty()) {
			return false;
		}
		
		Event lastEvent = curr_move.getLast();
		if(curr_move.getLast() instanceof MoveEvent) {
			return ((MoveEvent) lastEvent).isSlide;
		} else {
			return false;
		}
	}
	
	public List<Event> addEvent (Event e) {
		curr_move.addLast(e);
		return curr_move;
	}
	
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
		
		if (last instanceof SpawnEvent)
			return true;
		
		else if (last instanceof MoveEvent)
			return ((MoveEvent)last).isSlide;
		
		return false;
	}
	
	public static final int SPAWN_CLICK = 1, SELECT_CLICK = 2, MOVE_CLICK = 3;
}
