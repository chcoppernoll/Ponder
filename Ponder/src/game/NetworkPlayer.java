package game;

import java.awt.event.MouseAdapter;
import java.util.LinkedList;

import javax.swing.JFrame;

import gui.SwingGraphics;
import network.Client;
import network.Event;
import network.MoveEvent;
import network.SpawnEvent;

/**
 * Small adapter class to delay program execution until a mouse click is "heard"
 */
class ClickDelay extends MouseAdapter {
	public boolean clicked = false;
	
	public void onMouseClicked() {
		clicked = true;
	}
}

public class NetworkPlayer implements Player {
	private LinkedList<Event> move;
	private LinkedList<Event> old;

	/**
	 * Polymorphic method for the beginning of a player's turn
	 */
	public void onTurnStart(SwingGraphics graphics, Client net) {
		graphics.stopInput();
		old = net.getGame();
		graphics.printMove(old);
		move = null;
	}

	/**
	 * Polymorphic method for the end of a player's turn
	 */
	public void onTurnEnd(SwingGraphics graphics, Client net) {
		graphics.printMove(move);
		
		for (int i = 0; i < move.size() - 1; ++i) {
			Event e = move.get(i);
			graphics.runEvent(e);
			
			// Perform jump and despawning concurrently
			if (e instanceof SpawnEvent)
				if (((SpawnEvent)e).exiled && i != move.size() - 2 && (move.get(i + 1) instanceof MoveEvent))
					graphics.runEvent(move.get(++i));
			
			// Delay so user understands movement
			try {
				Thread.sleep(500);							// Needs tuning
				
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		
		// Delay next turn until player gives okay
		/*
		ClickDelay m = new ClickDelay();
		JFrame frame = graphics.getFrame();
		frame.addMouseListener(m);
		
		while (!m.clicked) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e1) {}
		}
		
		frame.removeMouseListener(m);
		//*/
	}

	/**
	 * Polymorphic method for determining when to switch from the "turn" phase to the "end turn" phase
	 */
	// Communicate with server/load "unseen" events into move
	public boolean turnOver(SwingGraphics graphics, Client net) {
		LinkedList<Event> tmp = net.getGame();
		
		System.out.println("Checking for Updates");
		
		// Hackish solution. Have to do for now
		if (tmp.size() != old.size()) {
			int o_siz = old.size();
			move = new LinkedList<>();
			
			for (; o_siz != tmp.size(); ++o_siz)
				move.add(tmp.get(o_siz));
		}
		
		//if (net.hasGameUpdate(graphics.getLogic().getCurrTurn()))
			//move = net.getGame();
		
		return move != null;
	}

}
