package it.unibo.ai.didattica.competition.tablut.domain;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Abstract class for a State of a game We have a representation of the board
 * and the turn
 * 
 * @author Andrea Piretti
 *
 */
public abstract class State {

	/**
	 * Turn represent the player that has to move or the end of the game(A win
	 * by a player or a draw)
	 * 
	 * @author A.Piretti
	 */
	public enum Turn {
		WHITE("W"), BLACK("B"), WHITEWIN("WW"), BLACKWIN("BW"), DRAW("D");
		private final String turn;

		private Turn(String s) {
			turn = s;
		}

		public boolean equalsTurn(String otherName) {
			return (otherName == null) ? false : turn.equals(otherName);
		}

		public String toString() {
			return turn;
		}
	}

	/**
	 * 
	 * Pawn represents the content of a box in the board
	 * 
	 * @author A.Piretti
	 *
	 */
	public enum Pawn {
		EMPTY("O"), WHITE("W"), BLACK("B"), THRONE("T"), KING("K");
		private final String pawn;

		private Pawn(String s) {
			pawn = s;
		}

		public boolean equalsPawn(String otherPawn) {
			return (otherPawn == null) ? false : pawn.equals(otherPawn);
		}

		public String toString() {
			return pawn;
		}

	}

	protected Pawn board[][];
	protected Turn turn;
	public ArrayList<Coord> whiteCoords;
	public ArrayList<Coord> blackCoords;
	public ArrayList<Coord> whiteWinPos;
	public Coord kingCoord;
	public int nwhite;
	public int nblack;
	public int whitePieces;
	public int blackPieces;
	public int turnNumber;
	

	public State() {
		super();
	}

	public Pawn[][] getBoard() {
		return board;
	}

	public String boardString() {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < this.board.length; i++) {
			for (int j = 0; j < this.board.length; j++) {
				result.append(this.board[i][j].toString());
				if (j == 8) {
					result.append("\n");
				}
			}
		}
		return result.toString();
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();

		// board
		result.append("");
		result.append(this.boardString());

		result.append("-");
		result.append("\n");

		// TURNO
		result.append(this.turn.toString());

		return result.toString();
	}

	public String toLinearString() {
		StringBuffer result = new StringBuffer();

		// board
		result.append("");
		result.append(this.boardString().replace("\n", ""));
		result.append(this.turn.toString());

		return result.toString();
	}

	/**
	 * this function tells the pawn inside a specific box on the board
	 * 
	 * @param row
	 *            represents the row of the specific box
	 * @param column
	 *            represents the column of the specific box
	 * @return is the pawn of the box
	 */
	public Pawn getPawn(int row, int column) {
		return this.board[row][column];
	}
	
	/**
	 * 
	 * @return king's position
	 */
	public Coord getKingPosition() {
		return kingCoord;
	}
	
	public int getTurnNumber() {
		return turnNumber;
	}
	
	/**
	 * 
	 * @param coord
	 * @return the four neighbours of a given coordinate
	 */
	public ArrayList<Coord> getNeighbours(Coord coord) {
		ArrayList<Coord> neighbours = new ArrayList<Coord>();
		neighbours.add(new Coord(coord.x+1, coord.y));
		neighbours.add(new Coord(coord.x-1, coord.y));
		neighbours.add(new Coord(coord.x, coord.y+1));
		neighbours.add(new Coord(coord.x, coord.y-1));
		
		return neighbours;
	}

	/**
	 * this function remove a specified pawn from the board
	 * 
	 * @param row
	 *            represents the row of the specific box
	 * @param column
	 *            represents the column of the specific box
	 * 
	 */
	public void removePawn(int row, int column) {
		this.board[row][column] = Pawn.EMPTY;
	}

	public void setBoard(Pawn[][] board) {
		this.board = board;
	}

	public Turn getTurn() {
		return turn;
	}

	public void setTurn(Turn turn) {
		this.turn = turn;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (this.board == null) {
			if (other.board != null)
				return false;
		} else {
			if (other.board == null)
				return false;
			if (this.board.length != other.board.length)
				return false;
			if (this.board[0].length != other.board[0].length)
				return false;
			for (int i = 0; i < other.board.length; i++)
				for (int j = 0; j < other.board[i].length; j++)
					if (!this.board[i][j].equals(other.board[i][j]))
						return false;
		}
		if (this.turn != other.turn)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.board == null) ? 0 : this.board.hashCode());
		result = prime * result + ((this.turn == null) ? 0 : this.turn.hashCode());
		return result;
	}

	public String getBox(int row, int column) {
		String ret;
		char col = (char) (column + 97);
		ret = col + "" + (row + 1);
		return ret;
	}

	public State clone() {
		Class<? extends State> stateclass = this.getClass();
		Constructor<? extends State> cons = null;
		State result = null;
		
		try {
			cons = stateclass.getConstructor(stateclass);
			result = cons.newInstance(new Object[0]);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

		Pawn oldboard[][] = this.getBoard();
		Pawn newboard[][] = result.getBoard();

		for (int i = 0; i < this.board.length; i++) {
			for (int j = 0; j < this.board[i].length; j++) {
				newboard[i][j] = oldboard[i][j];
			}
		}
		
		result.setBoard(newboard);
		result.setTurn(this.turn);
		
		return result;
	}

	public ArrayList<Coord> getWhiteCoords() {
		return whiteCoords;
	}

	public void setWhiteCoords(ArrayList<Coord> whiteCoords) {
		this.whiteCoords = whiteCoords;
	}

	public ArrayList<Coord> getBlackCoords() {
		return blackCoords;
	}

	public void setBlackCoords(ArrayList<Coord> blackCoords) {
		this.blackCoords = blackCoords;
	}

	public ArrayList<Coord> getWhiteWinPos() {
		return whiteWinPos;
	}

	public void setWhiteWinPos(ArrayList<Coord> whiteWinPos) {
		this.whiteWinPos = whiteWinPos;
	}

	public Coord getKingCoord() {
		return kingCoord;
	}

	public void setKingCoord(Coord kingCoord) {
		this.kingCoord = kingCoord;
	}

	public int getNwhite() {
		return nwhite;
	}

	public void setNwhite(int nwhite) {
		this.nwhite = nwhite;
	}

	public int getNblack() {
		return nblack;
	}

	public void setNblack(int nblack) {
		this.nblack = nblack;
	}

	public int getWhitePieces() {
		return whitePieces;
	}

	public void setWhitePieces(int whitePieces) {
		this.whitePieces = whitePieces;
	}

	public int getBlackPieces() {
		return blackPieces;
	}

	public void setBlackPieces(int blackPieces) {
		this.blackPieces = blackPieces;
	}

	public void setTurnNumber(int turnNumber) {
		this.turnNumber = turnNumber;
	}

	/**
	 * Counts the number of checkers of a specific color on the board. Note: the king is not taken into account for white, it must be checked separately
	 * @param color The color of the checker that will be counted. It is possible also to use EMPTY to count empty cells.
	 * @return The number of cells of the board that contains a checker of that color.
	 */
	public int getNumberOf(Pawn color) {
		int count = 0;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] == color)
					count++;
			}
		}
		return count;
	}
	
	/**
	 * 
	 * @return true if the game is over
	 */
	public boolean gameOver() {
		return (turn == Turn.WHITEWIN || turn == Turn.BLACKWIN);
	}

	/**
	 * Updates the coordinates of the current state
	 */
	public void updateCoords() {
		int i = 0;
		int j = 0;
		boolean king = false;
		this.turnNumber++;
		ArrayList<Coord> tmpWhiteCoords = new ArrayList<Coord>();
		ArrayList<Coord> tmpBlackCoords = new ArrayList<Coord>();
		
		for (i = 0; i < board.length; i++) {
			for (j = 0; j < board.length; j++) {
				if (this.board[i][j] == Pawn.WHITE) 
					tmpWhiteCoords.add(new Coord(i,j));
				if (this.board[i][j] == Pawn.BLACK) 
					tmpBlackCoords.add(new Coord(i,j));
				if (this.board[i][j] == Pawn.KING) {
					this.kingCoord = new Coord(i,j);
					king = true;
				}
			}
		}
		this.whitePieces = tmpWhiteCoords.size();
		this.blackPieces = tmpBlackCoords.size();
		if (king)
			this.whitePieces++;
		this.whiteCoords = tmpWhiteCoords;
		this.blackCoords = tmpBlackCoords;
		
	}
	
	/**
	 * 
	 * @return the opponent of the current turn
	 */
	public Turn getOpponent() {
		if (turn == Turn.WHITE)
			return Turn.BLACK;
		else
			return Turn.WHITE;
	}
	
	/**
	 * 
	 * @param t
	 * @return number of pieces on the board
	 */
	public int getNumberPieces(Turn t) {
		if (t == Turn.WHITE)
			return whitePieces;
		else
			return blackPieces;
	}
	
	/**
	 * 
	 * @return the list of the coordinates of the current player 
	 * (if white player it doesn't return king's coordinates)
	 */
    public ArrayList<Coord> getPlayerCoordSet() 
    {
    	if (this.turn.equals(StateTablut.Turn.WHITE))
    		return whiteCoords;
    	else
    		return blackCoords;
    }

	/**
	 * 
	 * @param rules
	 * @return
	 */
    public ArrayList<Action> getAllLegalActions(Game rules) 
    {
//    	System.out.println("Controllo azione");

        ArrayList<Action> allActions = new ArrayList<>();
//        System.out.println("Insieme di coordinate: " + getPlayerCoordSet().size());
//        System.out.println(getPlayerCoordSet());
        for (Coord pos : getPlayerCoordSet()) {
            allActions.addAll(getLegalMovesForPosition(rules, pos));
        }
        return allActions;
    }
    
    /**
     * 
     * @param rules
     * @param coord
     * @return
     */
    public ArrayList<Action> getLegalMovesForPosition(Game rules, Coord coord)
    {
    	ArrayList<Action> legalMoves = new ArrayList<>();
    	
    	legalMoves.addAll(getLegalMovesInDirection(rules, coord, -1, 0));
    	legalMoves.addAll(getLegalMovesInDirection(rules, coord, 1, 0));
    	legalMoves.addAll(getLegalMovesInDirection(rules, coord, 0, -1));
    	legalMoves.addAll(getLegalMovesInDirection(rules, coord, 0, 1));
    	
    	return legalMoves;
    }
    
    /**
     * 
     * @param rules
     * @param coord
     * @param x
     * @param y
     * @return
     */
    public ArrayList<Action> getLegalMovesInDirection(Game rules, Coord coord, int x, int y)
    {
    	boolean legal = false;
    	ArrayList<Action> legalMovesInDir = new ArrayList<>();
    	assert (!(x != 0 && y != 0));
        int startPos = (x != 0) ? coord.x : coord.y; // starting at x or y
        int incr = (x != 0) ? x : y; // incrementing the x or y value
        int endIdx = (incr == 1) ? board.length - 1 : 0; // moving in the 0 or 8 direction

        // for each possible move creates an action an verify its legality
        for (int i = startPos + incr; incr * i <= endIdx; i += incr) { // increasing/decreasing functionality
            legal = false;
        	Action temp_action = null;
        	
			try {
				temp_action = (x != 0) ? new Action(getBox(coord.x, coord.y), getBox(i, coord.y), turn) : new Action(getBox(coord.x, coord.y), getBox(coord.x, i), turn);
//				System.out.println("Checking Action " + temp_action.toString());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			// check the legality of the action according to the Ashton Tablut rules
        	try {
				rules.checkMove(this, temp_action);
				legal = true;
			} catch (Exception e) {

			}
        	if(legal)
        		legalMovesInDir.add(temp_action);        	
        }

    	return legalMovesInDir;
    }	
    
    /**
     * 
     * @param rules
     * @return all the successors of the current state according to the rules
     */
    public ArrayList<State> getSuccessors(Game rules) 
    {
//    	System.out.println("Profondita 1");
    	ArrayList<State> successors = new ArrayList<State>();
    	List<Action> tmpAllActions = getAllLegalActions(rules);
        List<Action> allActions = new ArrayList<Action>();
		
        Collections.shuffle(tmpAllActions);
        
        // aggiungo le coordinate del re all'inizio in modo che le sue mosse vengono processate per prima
        if(getTurn() == State.Turn.WHITE) {
        	allActions = getLegalMovesForPosition(rules, getKingPosition());
        	allActions.addAll(tmpAllActions);
        }
        else
        	allActions = tmpAllActions;
        
//    	System.out.println(allActions);
    	// TODO: Mischia le mosse
    	// Iterate through all legal moves and apply them to a clone of the board state
    	for (Action action: allActions) {
    		State clonedState = (State) clone();
    		clonedState = rules.processMove(clonedState, action); // apply the operator o and obtain the new game state s.
    		successors.add(clonedState);
    	}
    	return successors;    	
    }
    
}
