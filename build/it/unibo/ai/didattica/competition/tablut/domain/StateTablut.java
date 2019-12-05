package it.unibo.ai.didattica.competition.tablut.domain;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * This class represents a state of a match of Tablut (classical or second
 * version)
 * 
 * @author A.Piretti
 * 
 */
public class StateTablut extends State implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public StateTablut() {
		super();
		this.board = new Pawn[9][9];
		this.blackCoords = new ArrayList<>();
		this.whiteCoords = new ArrayList<>();
		this.whiteWinPos = new ArrayList<>(16);
		this.nblack = 16;
		this.nwhite = 8;
		this.whitePieces = 8;
		this.blackPieces = 16;
		this.turnNumber = 0;

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				this.board[i][j] = Pawn.EMPTY;
			}
		}

		this.board[4][4] = Pawn.THRONE;

		this.turn = Turn.BLACK;

		this.board[4][4] = Pawn.KING;
		this.kingCoord = new Coord(4,4);

		this.board[2][4] = Pawn.WHITE;
		this.board[3][4] = Pawn.WHITE;
		this.board[5][4] = Pawn.WHITE;
		this.board[6][4] = Pawn.WHITE;
		this.board[4][2] = Pawn.WHITE;
		this.board[4][3] = Pawn.WHITE;
		this.board[4][5] = Pawn.WHITE;
		this.board[4][6] = Pawn.WHITE;
		whiteCoords.add(new Coord(2,4));
		whiteCoords.add(new Coord(3,4));
		whiteCoords.add(new Coord(5,4));
		whiteCoords.add(new Coord(6,4));
		whiteCoords.add(new Coord(4,2));
		whiteCoords.add(new Coord(4,3));
		whiteCoords.add(new Coord(4,5));
		whiteCoords.add(new Coord(4,6));
		
		this.board[0][3] = Pawn.BLACK;
		this.board[0][4] = Pawn.BLACK;
		this.board[0][5] = Pawn.BLACK;
		this.board[1][4] = Pawn.BLACK;
		this.board[3][0] = Pawn.BLACK;
		this.board[3][8] = Pawn.BLACK;
		this.board[4][0] = Pawn.BLACK;
		this.board[4][1] = Pawn.BLACK;
		this.board[4][7] = Pawn.BLACK;
		this.board[4][8] = Pawn.BLACK;
		this.board[5][0] = Pawn.BLACK;
		this.board[5][8] = Pawn.BLACK;
		this.board[7][4] = Pawn.BLACK;
		this.board[8][3] = Pawn.BLACK;
		this.board[8][4] = Pawn.BLACK;
		this.board[8][5] = Pawn.BLACK;
		blackCoords.add(new Coord(0,3));
		blackCoords.add(new Coord(0,4));
		blackCoords.add(new Coord(0,5));
		blackCoords.add(new Coord(1,4));
		blackCoords.add(new Coord(3,0));
		blackCoords.add(new Coord(3,8));
		blackCoords.add(new Coord(4,0));
		blackCoords.add(new Coord(4,1));
		blackCoords.add(new Coord(4,7));
		blackCoords.add(new Coord(4,8));
		blackCoords.add(new Coord(5,0));
		blackCoords.add(new Coord(5,8));
		blackCoords.add(new Coord(7,4));
		blackCoords.add(new Coord(8,3));
		blackCoords.add(new Coord(8,4));
		blackCoords.add(new Coord(8,5));
		
		whiteWinPos.add(new Coord(0,1));
		whiteWinPos.add(new Coord(0,2));
		whiteWinPos.add(new Coord(0,6));
		whiteWinPos.add(new Coord(0,7));
		whiteWinPos.add(new Coord(1,0));
		whiteWinPos.add(new Coord(2,0));
		whiteWinPos.add(new Coord(6,0));
		whiteWinPos.add(new Coord(7,0));
		whiteWinPos.add(new Coord(8,1));
		whiteWinPos.add(new Coord(8,2));
		whiteWinPos.add(new Coord(8,6));
		whiteWinPos.add(new Coord(8,7));
		whiteWinPos.add(new Coord(1,8));
		whiteWinPos.add(new Coord(2,8));
		whiteWinPos.add(new Coord(6,8));
		whiteWinPos.add(new Coord(7,8));

	}

	public StateTablut clone() {
		StateTablut result = new StateTablut();

		Pawn oldboard[][] = this.getBoard();
		Pawn newboard[][] = result.getBoard();

		for (int i = 0; i < this.board.length; i++) {
			for (int j = 0; j < this.board[i].length; j++) {
				newboard[i][j] = oldboard[i][j];
			}
		}

		result.setBoard(newboard);
		result.setTurn(this.turn);
		result.setBlackCoords(this.blackCoords);
		result.setWhiteCoords(this.whiteCoords);
		result.setKingCoord(this.kingCoord);
		result.setBlackPieces(this.blackPieces);
		result.setWhitePieces(this.whitePieces);
		result.setTurnNumber(this.turnNumber);
		return result;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		StateTablut other = (StateTablut) obj;
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

}
