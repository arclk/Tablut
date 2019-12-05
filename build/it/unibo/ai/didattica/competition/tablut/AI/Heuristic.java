package it.unibo.ai.didattica.competition.tablut.AI;


import java.util.ArrayList;
import java.util.List;


import it.unibo.ai.didattica.competition.tablut.domain.*;


/**
 * 
 * @author Arcangelo Alberico
 * 
 * Class in which there are all the methods used for the evaluation 
 * of a certain state
 * 
 */
public class Heuristic {
	
	
	/**
     * The evaluation function for the board state. Although several
     * different functions were considered and tested, along with various weights
     * for the different variables, the evaluation function below turned out to
     * work best. An analysis of evaluation functions is included in the report.
     */
	public static double evaluation(State state, State.Turn player) {
		int turn;
		turn = state.turnNumber;
		State.Turn opponent;
		if(player == State.Turn.WHITE)
			opponent = State.Turn.BLACK;
		else
			opponent = State.Turn.WHITE;
        double yourPieceValue = (double) state.getNumberPieces(player);
        double oppPieceValue = (double) state.getNumberPieces(opponent);
		
        /*
		 * The evaluation function is different when the player is White vs when the player is Black.
		 * 
		 * The following differences in player priority were determined to be the most effective:
		 * White care less about the number of enemy pieces surrounding the king and more about the King's
		 * proximity to a corner (in number of moves).
		 * 
		 * The Black care more about the piece count difference as the game progresses and less about
		 * the king's proximity to a corner.
		 */
		double evaluationValue = 0.0;
    	if (player == State.Turn.WHITE) 
    	{
			/*
			 * The White evaluation function becomes more aggressive as the game progresses in terms of:
			 * the number of moves for the king to reach a corner
			 */
    		if (state.getTurn() == State.Turn.WHITEWIN)
    			return Double.POSITIVE_INFINITY;
    		if (state.getTurn() == State.Turn.BLACKWIN)
    			return Double.NEGATIVE_INFINITY;
    		
/*			if (turn < 40) {
				evaluationValue = yourPieceValue - oppPieceValue + kingMovesToEscape(state);
			} else if (turn < 70) {
				evaluationValue = yourPieceValue - oppPieceValue + 2.0 * kingMovesToEscape(state);
    		} else {
    			evaluationValue = yourPieceValue - oppPieceValue + 3.0 * kingMovesToEscape(state);
    		}
*/
    		if (turn < 40) 
    			evaluationValue = yourPieceValue - oppPieceValue - enemyPiecesAroundKing(state);
    		else if (turn < 70) 
    			evaluationValue = yourPieceValue - oppPieceValue - 2.0 * enemyPiecesAroundKing(state);
    		else 
    			evaluationValue = yourPieceValue - oppPieceValue - 3.0 * enemyPiecesAroundKing(state);
		} 
    	else 
		{
			/*
			 * The Black evaluation function becomes more aggressive as the game progresses in terms of:
			 * the number of enemy pieces around the king and
			 * the number of moves for the king to reach a corner
			 */
			if (state.getTurn() == State.Turn.BLACKWIN)
				return Double.POSITIVE_INFINITY;
    		if (state.getTurn() == State.Turn.WHITEWIN)
    			return Double.NEGATIVE_INFINITY;
/*			if (turn < 40) {
			evaluationValue = yourPieceValue - oppPieceValue + piecesAroundCorners(state) - kingMovesToEscape(state) + enemyPiecesAroundKing(state);
		} else if (turn < 70) {
			evaluationValue = 2 * (yourPieceValue - oppPieceValue) + piecesAroundCorners(state) - 1.5 * kingMovesToEscape(state) + 6 * enemyPiecesAroundKing(state);
		} else {
			evaluationValue = 3 * (yourPieceValue - oppPieceValue) + piecesAroundCorners(state) - 1.5 * kingMovesToEscape(state) + 12 * enemyPiecesAroundKing(state);
		}
*/
			if (turn < 40) 
				evaluationValue = yourPieceValue - oppPieceValue + 2*enemyPiecesAroundKing(state);
			else if (turn < 70) 
				evaluationValue = 2 * (yourPieceValue - oppPieceValue) + 6 * enemyPiecesAroundKing(state);
			else 
				evaluationValue = 3 * (yourPieceValue - oppPieceValue) + 12 * enemyPiecesAroundKing(state);
		}

    	return evaluationValue;
    }
	
	/**
	 * Given a game state, this method returns a value used in board state evaluation
	 * which represents the number of enemy pieces surrounding the White king.
	 */
	public static double enemyPiecesAroundKing(State state) {
		double numPieces = 0.0;
		
		List<Coord> kingNeighbors = state.getNeighbours(state.getKingPosition());
//		System.out.println(kingNeighbors);
		for (Coord c : kingNeighbors) {
			if (state.getPawn(c.x, c.y) == State.Pawn.BLACK) {
				numPieces += 0.25;
			}
		}
		
		return numPieces;
	}
	
	/**
	 * Given a game state, this method returns a value used in board state evaluation for the
	 * number of black pieces in key strategic locations near the corners.
	 * 
	 * These locations were determined to be useful for the Black in
	 * maintaining an advantage during the game.
	 */
	public static double piecesInStrategicPos(State state) {
		double value = 0.0;
		
		// Top left corner
		if (state.getPawn(1, 1) == State.Pawn.BLACK) {
			value += 0.25;
		}
		
		// Top right corner
		if (state.getPawn(1, 7) == State.Pawn.BLACK) {
			value += 0.25;
		}
		
		// Bottom left corner
		if (state.getPawn(7, 1) == State.Pawn.BLACK) {
			value += 0.25;
		}
		
		// Bottom right corner
		if (state.getPawn(7, 7) == State.Pawn.BLACK) {
			value += 0.1;
		}
		
/*		if (state.getPawn(8, 6) == State.Pawn.BLACK) {
			value += 0.1;
		}
		
		if (state.getPawn(6, 8) == State.Pawn.BLACK) {
			value += 0.1;
		}
		
		if (state.getPawn(0, 2) == State.Pawn.BLACK) {
			value += 0.1;
		}
		
		if (state.getPawn(2, 0) == State.Pawn.BLACK) {
			value += 0.1;
		}
		
		if (state.getPawn(0, 6) == State.Pawn.BLACK) {
			value += 0.1;
		}
		
		if (state.getPawn(6, 0) == State.Pawn.BLACK) {
			value += 0.1;
		}
		
		if (state.getPawn(8, 2) == State.Pawn.BLACK) {
			value += 0.1;
		}
		
		if (state.getPawn(2, 8) == State.Pawn.BLACK) {
			value += 0.1;
		}
*/		
		return value;
	}
	
	/**
	 * This method returns a value representing the number of king moves to all the escapes.
	 * Given a state, the method checks the min number of moves to each escape, and returns
	 * a positive value if we are within 1-2 moves to a certain escape, and an even higher
	 * value if we are withing 1-2 moves to more than one escape.
	 * 
	 */
    public static double kingMovesToEscape(State state) {
    	Coord kingPosition = state.getKingPosition();
    	
    	// Retrieves all legal moves for the king based on its current position
    	ArrayList<Action> kingMoves = state.getLegalMovesForPosition(AlphaBetaSearch.game, kingPosition);
    	
    	double moveDistanceValue = 0.0;
    	if (!kingMoves.isEmpty()) { // If the king actually has moves        	
        	
    		// Stores the min number of moves to reach each of the 4 corners
    		int [] distances = new int [16];

    		// Iterate through all corners, calculating the min number of moves to reach each one
    		int cornerIdx = 0;
        	for (Coord corner : state.whiteWinPos) {
        		distances[cornerIdx] = calcMinMovesToCorner(state, corner, 1, kingPosition);
        		cornerIdx++;
        	}
        	// Generate the move's value based on proximity to the corner
        	for (int i = 0; i < distances.length; i++) {
        		switch (distances[i]) {
                case 1:  moveDistanceValue += 15; // Being 1 move away is much more valuable
                         break;
                case 2:  moveDistanceValue += 1;
                         break;
                default: moveDistanceValue += 0;
                         break;
        		}
        	}
    	}
    	    	
    	return moveDistanceValue;
    }
    
    /**
     * This method calculates the min number of moves for the king to reach a given escape position.
     * 
     * This is done by ignoring opponent moves. We simply care about how many consecutive moves it would
     * take the king to reach a specific corner. This is because it becomes very difficult (and costly) to
     * predict opponent moves as well.
     * 
     * This method projects a move onto the board state and recursively goes to the following move, but does
     * not actually process the move in order to be more efficient (time and memory-wise).
     */
    public static int calcMinMovesToCorner(State state, Coord corner, int moveCt, Coord kingPosition) {    	
    	// Termination condition - either we're in a corner or it takes too many moves and thus becomes irrelevant
    	if (moveCt == 3 || state.whiteWinPos.contains(kingPosition)) {
    		return moveCt;
    	}
    	
    	ArrayList<Action> kingMoves = state.getLegalMovesForPosition(AlphaBetaSearch.game, kingPosition);
    	
    	// We'll store the counts for each move here
    	int [] moveCounts = new int[kingMoves.size()];

    	// Iterate through current possible king moves and see how much closer we can get to a corner
    	int moveIdx = 0;
    	for (Action action : kingMoves) {
			// If move brings you closer to the corner, attempt it
			if (action.getFromPosition().distance(corner) > action.getToPosition().distance(corner)) {            	
                moveCounts[moveIdx] = calcMinMovesToCorner(state, corner, moveCt + 1, action.getToPosition());
                moveIdx++;
			}
    	}
    	
    	// Find the min number of moves to reach the corner, or return 50 if unreachable
    	int min = 50;
    	for (int i = 0; i < moveCounts.length; i++) {
    		int current = moveCounts[i];
    		if (current != 0 && current < min) {
    			min = current;
    		}
    	}
    	return min;
    }

	
	
	
}
