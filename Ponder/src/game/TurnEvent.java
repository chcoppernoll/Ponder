package game;

public class TurnEvent implements Event {
	public final int player;
	
	public TurnEvent(int player) {
		this.player = player;
	}
	
	public void run() {

	}
}
