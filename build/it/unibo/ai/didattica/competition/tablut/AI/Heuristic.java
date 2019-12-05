package it.unibo.ai.didattica.competition.tablut.AI;


import java.util.ArrayList;
import java.util.List;


import it.unibo.ai.didattica.competition.tablut.domain.*;


/**
 * Class in which there are all the methods used for the evaluation 
 * of a certain state
 * 
 * @author Arcangelo Alberico
 * 
 */
public class Heuristic {
	
	
	/**
	 * The evaluation function for the board state.
	 * 
	 * @param state 	state to be evaluated
	 * @param player	player according which we have to calculate the heuristic
	 * @return the heuristic value of the given state
	 */
	public static double evaluation(State state, State.Turn player) {
		int turn;
		turn = state.turnNumber;
		State.Turn opponent;
		double val = 0.0;
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
    		// check if the current state is a winning or losing one and in the case return immediately
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
			/*
			 * The White evaluation function becomes more aggressive as the game progresses in terms of:
			 * the number of moves for the king to reach an escape position
			 */
    		val = kingInStrategicPos(state);
    		if(val != 0.0) {
    			System.out.println(state);
    			System.out.println("Heuristic: " + val);
    		}
    		if (turn < 40) 
    			evaluationValue = yourPieceValue - oppPieceValue - enemyPiecesAroundKing(state) + val;
    		else if (turn < 70) 
    			evaluationValue = yourPieceValue - oppPieceValue - 2.0 * enemyPiecesAroundKing(state) + val;
    		else 
    			evaluationValue = yourPieceValue - oppPieceValue - 3.0 * enemyPiecesAroundKing(state) + val;
		} 
    	else 
		{
    		// check if the current state is a winning or losing one and in the case return immediately
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
			/*
			 * The Black evaluation function becomes more aggressive as the game progresses in terms of:
			 * the number of enemy pieces around the king and
			 * the number of moves for the king to reach an escape position
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
	
	public static double kingInStrategicPos(State state) {
		double value = 0.0;
		Coord kingPosition = state.getKingPosition();
		int i = 0;
		int length = state.getBoard().length;
		boolean occupied = false;
		boolean threatened = false;
		
		if(kingPosition.x == 2) {
			for(i = 0; i < length; i++) 
				// se le caselle della stessa riga non sono vuote e non c'è il re saranno occupate da una pedina
				if ((state.getPawn(kingPosition.x, i) != State.Pawn.EMPTY) && (state.getPawn(kingPosition.x, i) != State.Pawn.KING))
					occupied = true;
			// se la riga non è occupata da nessuno
			if(!occupied) {
				// se non c'è una pedina nera sopra e sotto e il re non si trova sotto il campo nemico
				if ((state.getPawn(kingPosition.x-1, kingPosition.y) != State.Pawn.BLACK) && 
						(state.getPawn(kingPosition.x+1, kingPosition.y) != State.Pawn.BLACK) && (kingPosition.y != 4)) 
					return 500;
				else {
					// verifico le caselle sotto destra
					for(i = kingPosition.y; i < length; i++) {
						// se trova una pedina bianca è coperto
						if (state.getPawn(kingPosition.x+1, i) == State.Pawn.WHITE)
							break;
						// se trova una pedina nera è minacciato
						if (state.getPawn(kingPosition.x+1, i) == State.Pawn.BLACK)
							threatened = true;
					}
					// verifico le caselle sotto sinistra
					for(i = kingPosition.y; i >= 0; i--) {
						if (state.getPawn(kingPosition.x+1, i) == State.Pawn.WHITE)
							break;
						if (state.getPawn(kingPosition.x+1, i) == State.Pawn.BLACK)
							threatened = true;
					}
					// verifico tutte le caselle sotto
					for (i = kingPosition.x+1; i < length; i++) {
						if (state.getPawn(i, kingPosition.y) == State.Pawn.WHITE)
							break;
						if (state.getPawn(i, kingPosition.y) == State.Pawn.BLACK)
							threatened = true;
					}
					if(!threatened)
						return 500;
				}
			}		
		}
		
		occupied = false;
		threatened = false;		
		if(kingPosition.x == 6) {
			for(i = 0; i < length; i++) 
				// se le caselle della stessa riga non sono vuote e non c'è il re saranno occupate da una pedina
				if ((state.getPawn(kingPosition.x, i) != State.Pawn.EMPTY) && (state.getPawn(kingPosition.x, i) != State.Pawn.KING))
					occupied = true;
			// se la riga non è occupata da nessuno
			if(!occupied) {
				// se non c'è una pedina nera sopra e sotto e il re non si trova sopra il campo nemico
				if ((state.getPawn(kingPosition.x-1, kingPosition.y) != State.Pawn.BLACK) && 
						(state.getPawn(kingPosition.x+1, kingPosition.y) != State.Pawn.BLACK) && (kingPosition.y != 4)) 
					return 500;
				else {
					// verifico le caselle sopra destra
					for(i = kingPosition.y; i < length; i++) {
						// se trova una pedina bianca è coperto
						if (state.getPawn(kingPosition.x-1, i) == State.Pawn.WHITE)
							break;
						// se trova una pedina nera è minacciato
						if (state.getPawn(kingPosition.x-1, i) == State.Pawn.BLACK)
							threatened = true;
					}
					// verifico le caselle sopra sinistra
					for(i = kingPosition.y; i >= 0; i--) {
						if (state.getPawn(kingPosition.x-1, i) == State.Pawn.WHITE)
							break;
						if (state.getPawn(kingPosition.x-1, i) == State.Pawn.BLACK)
							threatened = true;
					}
					// verifico tutte le caselle sopra
					for (i = kingPosition.x-1; i >= 0; i--) {
						if (state.getPawn(i, kingPosition.y) == State.Pawn.WHITE)
							break;
						if (state.getPawn(i, kingPosition.y) == State.Pawn.BLACK)
							threatened = true;
					}
					if(!threatened)
						return 500;
				}
			}		
		}
		
		occupied = false;
		threatened = false;
		if(kingPosition.y == 2) {
			for(i = 0; i < length; i++) 
				// se le caselle della stessa colonna non sono vuote e non c'è il re saranno occupate da una pedina
				if ((state.getPawn(i, kingPosition.y) != State.Pawn.EMPTY) && (state.getPawn(i, kingPosition.y) != State.Pawn.KING))
					occupied = true;
			// se la colonna non è occupata da nessuno
			if(!occupied) {
				// se non c'è una pedina nera a sinistra e destra e il re non si trova a sinistra il campo nemico
				if ((state.getPawn(kingPosition.x, kingPosition.y-1) != State.Pawn.BLACK) && 
						(state.getPawn(kingPosition.x, kingPosition.y+1) != State.Pawn.BLACK) && (kingPosition.x != 4)) 
					return 500;
				else {
					// verifico le caselle a destra sotto
					for(i = kingPosition.x; i < length; i++) {
						// se trova una pedina bianca è coperto
						if (state.getPawn(i, kingPosition.y+1) == State.Pawn.WHITE)
							break;
						// se trova una pedina nera è minacciato
						if (state.getPawn(i, kingPosition.y+1) == State.Pawn.BLACK)
							threatened = true;
					}
					// verifico le caselle a destra sopra
					for(i = kingPosition.x; i >= 0; i--) {
						if (state.getPawn(i, kingPosition.y+1) == State.Pawn.WHITE)
							break;
						if (state.getPawn(i, kingPosition.y+1) == State.Pawn.BLACK)
							threatened = true;
					}
					// verifico tutte le caselle a destra
					for (i = kingPosition.y+1; i < length; i++) {
						if (state.getPawn(kingPosition.x, i) == State.Pawn.WHITE)
							break;
						if (state.getPawn(kingPosition.x, i) == State.Pawn.BLACK)
							threatened = true;
					}
					if(!threatened)
						return 500;
				}
			}		
		}
		
		occupied = false;
		threatened = false;
		if(kingPosition.y == 6) {
			for(i = 0; i < length; i++) 
				// se le caselle della stessa colonna non sono vuote e non c'è il re saranno occupate da una pedina
				if ((state.getPawn(i, kingPosition.y) != State.Pawn.EMPTY) && (state.getPawn(i, kingPosition.y) != State.Pawn.KING))
					occupied = true;
			// se la colonna non è occupata da nessuno
			if(!occupied) {
				// se non c'è una pedina nera a sinistra e destra e il re non si trova a destra il campo nemico
				if ((state.getPawn(kingPosition.x, kingPosition.y-1) != State.Pawn.BLACK) && 
						(state.getPawn(kingPosition.x, kingPosition.y+1) != State.Pawn.BLACK) && (kingPosition.x != 4)) 
					return 500;
				else {
					// verifico le caselle a sinistra sotto
					for(i = kingPosition.x; i < length; i++) {
						// se trova una pedina bianca è coperto
						if (state.getPawn(i, kingPosition.y-1) == State.Pawn.WHITE)
							break;
						// se trova una pedina nera è minacciato
						if (state.getPawn(i, kingPosition.y-1) == State.Pawn.BLACK)
							threatened = true;
					}
					// verifico le caselle a sinistra sopra
					for(i = kingPosition.x; i >= 0; i--) {
						if (state.getPawn(i, kingPosition.y-1) == State.Pawn.WHITE)
							break;
						if (state.getPawn(i, kingPosition.y-1) == State.Pawn.BLACK)
							threatened = true;
					}
					// verifico tutte le caselle a sinistra
					for (i = kingPosition.y-1; i >= 0; i--) {
						if (state.getPawn(kingPosition.x, i) == State.Pawn.WHITE)
							break;
						if (state.getPawn(kingPosition.x, i) == State.Pawn.BLACK)
							threatened = true;
					}
					if(!threatened)
						return 500;
				}
			}		
		}
		
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
    	if (!kingMoves.isEmpty()) 
    	{ // If the king actually has moves        	
        	
    		// Stores the min number of moves to reach each of the 16 escape positions
    		int [] distances = new int [16];

    		// Iterate through all the escape positions, calculating the min number of moves to reach each one
    		int escapeIdx = 0;
        	for (Coord escape : state.whiteWinPos) {
        		distances[escapeIdx] = calcMinMovesToEscape(state, escape, 1, kingPosition);
        		escapeIdx++;
        	}
        	// Generate the move's value based on proximity to the corner
        	for (int i = 0; i < distances.length; i++) {
        		switch (distances[i]) {
                case 1:  moveDistanceValue += 15; // Being 1 move away is much more valuable
                         break;
                case 2:  moveDistanceValue += 2;
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
     * take the king to reach a specific escape position.
     * 
     * This method projects a move onto the board state and recursively goes to the following move, but does
     * not actually process the move in order to be more efficient (time and memory-wise).
     * 
     */
    public static int calcMinMovesToEscape(State state, Coord escape, int moveCt, Coord kingPosition) {    	
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
			if (action.getFromPosition().distance(escape) > action.getToPosition().distance(escape)) {            	
                moveCounts[moveIdx] = calcMinMovesToEscape(state, escape, moveCt + 1, action.getToPosition());
                moveIdx++;
			}
    	}
    	
    	// Find the min number of moves to reach the escape, or return 50 if unreachable
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
