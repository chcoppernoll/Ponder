package game;

public class MoveEvent implements Event {
	public final Position from, to;
	public final boolean slide;
	
	public MoveEvent(Position from, Position to, boolean slide) {
		this.from = from;
		this.to = to;
		this.slide = slide;
	}
	
	public void run() {

	}

	public void undo() {

	}

}
