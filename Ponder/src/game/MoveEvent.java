package game;

public class MoveEvent implements Event {
	public final Position from, to;
	public final boolean isSlide;
	
	public MoveEvent(Position from, Position to, boolean slide) {
		this.from = from;
		this.to = to;
		this.isSlide = slide;
	}
}
