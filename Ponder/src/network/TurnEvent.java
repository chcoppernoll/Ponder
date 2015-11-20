package network;

public class TurnEvent implements Event {
	static final long serialVersionUID = -1;
	public final int player;
	
	public TurnEvent(int player) {
		this.player = player;
	}
	
	public void run() {

	}
}
