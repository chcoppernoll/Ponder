package game;

public class EndTurnEvent implements Event {
	public final String player;

	public EndTurnEvent(String player) {
		this.player = player;
	}

	public void run() {

	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub

	}

}
