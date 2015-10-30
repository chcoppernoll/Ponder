package game;

public class EndTurnEvent implements Event {
	public final int player;
	
	public EndTurnEvent(int player) {
		this.player = player;
	}
	
	public void run() {

	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub

	}

}
