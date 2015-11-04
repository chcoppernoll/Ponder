package network;

public class MoveEvent implements Event {
	public final Position from, to;

	public MoveEvent(Position from, Position to) {
		this.from = from;
		this.to = to;
	}

	public void run() {

	}

	public void undo() {

	}

}
