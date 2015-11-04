package network;

public class Position {
	public final int x, y;

	public Position(int _x, int _y) {
		x = _x;
		y = _y;
	}

	public Position add(int dx, int dy) {
		return new Position(x + dx, y + dy);
	}

	public double distance(Position a) {
		int dx = x - a.x, dy = y - a.y;

		return Math.sqrt(dx * dx + dy * dy);
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Position))
			return false;

		Position other = (Position) obj;
		return other.x == x && other.y == y;
	}

	public int hashCode() {
		int hash = ((17 + x) << 5) - (17 + x);
		return ((hash + y) << 5) - (hash + y);
	}

	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}