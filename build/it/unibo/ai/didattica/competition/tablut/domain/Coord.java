package it.unibo.ai.didattica.competition.tablut.domain;

public class Coord {
	public int x;
	public int y;
	
	public Coord(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "Coord [x=" + x + ", y=" + y + "]";
	}
	
    // Computes manhattan distance between this coord and the other.
    public int distance(Coord c) {
        return Math.abs(this.x - c.x) + Math.abs(this.y - c.y);
    }

}
