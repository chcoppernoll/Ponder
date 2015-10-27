package game;

import java.awt.Color;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;

import javax.swing.JButton;
import javax.swing.JPanel;

class GridData {
	public int owner = -1;
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
	
	//private Queue<JButton> event_queue = null;
	@SuppressWarnings("unchecked")
	private PriorityQueue<Integer>[] spawn_sets = (PriorityQueue<Integer>[])new PriorityQueue[4];
	
	private boolean invalidPoint(Position pos) {
		return (pos.y < 0 || pos.y > 8) || (pos.x < 0 || pos.y > 8);
	}
	
	// Returns the list of adjacent tiles to the given position
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
	
	public Position positionOf(JButton cell) {
		return new Position(cell.getX() / 55, cell.getY() / 55);
	}
	
	public Position makePosition(int x, int y) {
		return new Position(x, y);
	}
	
	private boolean canSpawn(Position pos, int player) {		
		for (JButton adj : adjacents(pos)[0]) {
			if (adj != null) {
				int cell_piece = data.get(adj).owner;
				boolean[] _flags = data.get(adj).has_flag;
	
				if (cell_piece == -1 ? (_flags[0] || _flags[1] || _flags[2] || _flags[3]) : cell_piece != player) return false;
			}
		}
		
		return true;
	}
	
	// Returns whether the player can spawn at the given position
	public boolean canSpawn(JButton a, int player) {
		if (mana < .5 || data.get(a).owner != -1 || (flags[0] == a || flags[1] == a || flags[2] == a || flags[3] == a)) return false;
		
		boolean spawnable = canSpawn(positionOf(a), player);
		
		// This is only true if the player has control of their flag
		if (mana == .5) spawnable = spawnable && canSlide(a, flags[player]);
		
		return spawnable;
	}
	
	// Returns the piece jumped by a->b (null if none or illegal)
    public JButton jmpPiece(JButton a, JButton b) {
		if (!canJmp(a, b)) return null;
		
		// Add in check to prevent backwards move
		
		Position a_p = positionOf(a), b_p = positionOf(b);
		int dx = a_p.x - b_p.x, dy = a_p.y - b_p.y;
		
		JButton ret = grid.get(b_p.add(dx / 2, dy / 2));		
		return getPieceOwner(ret) != -1 ? ret : null;
	}
		
    // Returns whether a and b are in sliding distance
	public boolean canSlide(JButton a, JButton b) {
		double dist = positionOf(a).distance(positionOf(b));
		
		return dist == 1 || dist == Math.sqrt(2);
	}
	
	// Returns whether a and b are in jumping distance
	public boolean canJmp(JButton a, JButton b) {
		double dist = positionOf(a).distance(positionOf(b));
		
		return dist == 2 || dist == Math.sqrt(8);
	}
	
	public void init(JButton[][] board) {
		if (data.size() > 0) data.clear();
		if (grid.size() > 0) grid.clear();
		
		for (int y = 0; y != board.length; ++y) {
			for (int x = 0; x != board[y].length; ++x) {
				data.put(board[y][x], new GridData());
				grid.put(new Position(x, y), board[y][x]);
			}
		}
	}
	
	// Add a piece at the given position controlled by the given player
	public void addPiece(JButton a, int player) {
		data.get(a).owner = player;
		data.get(a).has_moved = true;
		grid.put(positionOf(a), a);
	}
	
	// Remove the piece at the given position
	public void removePiece(JButton a) {
		addPiece(a, -1);
		grid.put(positionOf(a), null);
	}
	
	// Add a flag at the given position for the given player
	public void addFlag(JButton a, int player) {
		data.get(a).has_flag[player] = true;
		flags[player] = a;
	}
	
	// Remove the flag at the given position of the given player
	public void removeFlag(JButton a, int player) {
		data.get(a).has_flag[player] = false;
		flags[player] = null;
	}
	
	// Returns the player_id of the victor or -1 if no winners
	public int victor() {
		int[] num_cont = new int[4];
		
		for (JButton flag : flags)
			if (data.get(flag).owner != -1)
				num_cont[data.get(flag).owner]++;
		
		for (int i = 0; i != num_cont.length; ++i)
			if (num_cont[i] >= 3) return i;
		
		return -1;
	}

	// Attempt to spawn a player's piece at the given cell and return success
	public boolean spawn(JButton cell, int player) {
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

	public JPanel getStack() {
		return stack;
	}
	
	public void setStack(JPanel n_stack) {
		stack = n_stack;
	}

	public boolean[] has_flags(JButton cell) {
		return data.get(cell).has_flag;
	}
	
	public JButton[] getFlags() {
		return flags;
	}
	
	
	// Workspace methods
	/**
	 * Returns whether the selected piece is capable of moving
	 */
	public boolean canMove(JButton cell) {
		return getPieceOwner(cell) != -1 && !data.get(cell).has_moved;
	}
	
	/**
	 * Returns whether a move can occur between from and to
	 */
	public boolean canMove(JButton from, JButton to) {
		return (canSlide(from, to) && !data.get(from).has_moved) || jmpPiece(from, to) != null;
	}
	
	public void enterMovePhase() {
		in_move_phase = true;
	}
	
	public boolean canPlayerSpawn(int player) {
		return !in_move_phase && curr_player == player && mana > 0;
	}
	
	/**
	 * Checks whether a piece has moved already
	 */
	public boolean hasMoved(JButton cell) {
		return data.get(cell).has_moved;
	}
	
	public void select(JButton cell) {
		focus = cell;
	}
	
	public JButton getFocus() {
		return focus;
	}

	
	// temporary methods for compatability
	public int getPieceOwner(JButton cell) {
		return cell != null ? data.get(cell).owner : -1;
	}
	
	public int getCurrPlayer() {
		return curr_player;
	}
	
	public boolean isClicked(JButton elem) {
		return elem == focus;
	}
	
	public int click(JButton elem) {
		if (stack != null) return 1;						// SPAWN_EVENT
		if (focus == null && canMove(elem)) return 2;		// SELECT_EVENT
		if (focus != null && focus != elem) return 3;		// MOVE_EVENT
		
		return 0;
	}
	
	public void nextTurn() {
		mana = 1;
		curr_player = (curr_player + 1) % 4;
		in_move_phase = false;
		
		for (JButton piece : grid.values())
			if (piece != null)
				data.get(piece).has_moved = false;
	}
}
