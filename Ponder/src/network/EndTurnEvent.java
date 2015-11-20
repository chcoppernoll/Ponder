package network;

public class EndTurnEvent implements Event {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6103652760997412303L;
	@Override
	public String toString() {
		return "EndTurnEvent [player=" + player + "]";
	}

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
