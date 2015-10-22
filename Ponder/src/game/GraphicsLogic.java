package game;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.JButton;

public class GraphicsLogic implements GameLogic<JButton> {
	private static class LogicStruct {
		public boolean clicked;
	}
	
	private HashMap<JButton, LogicStruct> check = new HashMap<>();
	
	public GraphicsLogic() {}
	
	public void add(JButton elem) {
		LogicStruct in = new LogicStruct();
		
		check.put(elem, in);
	}
	
	public boolean isClicked(JButton elem) {
		return check.get(elem).clicked;
	}
	
	public void click(JButton elem) {
		check.get(elem).clicked = !check.get(elem).clicked;
	}
	
}
