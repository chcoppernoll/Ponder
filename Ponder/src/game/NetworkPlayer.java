package game;

import java.awt.event.MouseAdapter;
import java.util.LinkedList;

import javax.swing.JFrame;

import gui.SwingGraphics;
import network.Client;

class ClickDelay extends MouseAdapter {
	public boolean clicked = false;
	
	public void onMouseClicked() {
		clicked = true;
	}
}

public class NetworkPlayer implements Player {
	private LinkedList<Event> move;

	public void onTurnStart(SwingGraphics graphics, Client net) {
		//send a message
		move = new LinkedList<>();
	}

	public void onTurnEnd(SwingGraphics graphics, Client net) {
		for (Event e : move) {
			graphics.runEvent(e);
			
			try {
				Thread.sleep(1000);							// Needs tuning
				
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		
		// Delay next turn until player gives okay
		ClickDelay m = new ClickDelay();
		JFrame frame = graphics.getFrame();
		frame.addMouseListener(m);
		
		while (!m.clicked) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e1) {}
		}
		
		frame.removeMouseListener(m);
	}

	// Communicate with server/load "unseen" events into move
	public boolean turnOver(SwingGraphics graphics, Client net) {
		
		
		return move.getLast() instanceof TurnEvent;
	}

}
