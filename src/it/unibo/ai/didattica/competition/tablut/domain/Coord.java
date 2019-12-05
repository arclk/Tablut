package it.unibo.ai.didattica.competition.tablut.domain;

/**
 * Class used to give a structure the the coordinates of th pawns
 * 
 * @author archi
 *
 */
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
	
    /**
     * Computes manhattan distance between this coordinate and the passed one.
     * @param c
     * @return distance between coordinates
     */
	public int distance(Coord c) {
        return Math.abs(this.x - c.x) + Math.abs(this.y - c.y);
    }

}
