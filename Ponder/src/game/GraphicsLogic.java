package game;

import java.util.HashMap;

import javax.swing.JButton;

public class GraphicsLogic implements GameLogic<JButton> {
	private static class LogicStruct {
		public boolean clicked;
		public int piece = -1, x, y;
		public boolean[] flags = new boolean[4];
	}
	
	private HashMap<JButton, LogicStruct> check = new HashMap<>();
	//private JButton[] all_flags = new JButton[4];
	private int curr_player;
	private double mana = 1;
	
	public GraphicsLogic() {}
	
	public void add(JButton elem, int x, int y) {
		LogicStruct in = new LogicStruct();
		in.x = x;
		in.y = y;
		
		check.put(elem, in);
	}
	
	public void addPiece(JButton elem, int player) {
		check.get(elem).piece = player;
	}
	
	public void removePiece(JButton elem) {
		check.get(elem).piece = -1;
	}
	
	public void addFlag(JButton elem, int player) {
		check.get(elem).flags[player] = true;
		//all_flags[player] = elem;
	}
	
	public void removeFlag(JButton elem, int player) {
		check.get(elem).flags[player] = false;
	}
	
	public boolean isClicked(JButton elem) {
		return check.get(elem).clicked;
	}
	
	public void click(JButton elem) {
		check.get(elem).clicked = !check.get(elem).clicked;
	}
	
	public void nextTurn() {
		mana = 1;
		curr_player = (curr_player + 1) % 4;
	}
	
	public int getCurrPlayer() {
		return curr_player;
	}
	
	public double getMana() {
		return mana;
	}
	
	public int getPiece(JButton cell) {
		return check.get(cell).piece;
	}
	
	public boolean[] getFlags(JButton cell) {
		return check.get(cell).flags;
	}
	
	public boolean canSpawn(JButton[] grid, int player) {
		if (mana <= 0) return false;
		if (mana <= .5);
		
		for (JButton cell : grid) {
			if (cell != null) {
				int cell_piece = check.get(cell).piece;
				boolean[] flags = check.get(cell).flags;
				
				if (cell_piece == -1 ? (flags[0] || flags[1] || flags[2] || flags[3]) : cell_piece != player) return false;
			}
		}
		
		return true;
	}
	
}
