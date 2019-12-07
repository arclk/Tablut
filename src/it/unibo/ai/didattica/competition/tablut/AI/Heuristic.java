package it.unibo.ai.didattica.competition.tablut.AI;


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
    		// White evaluation function
    		// check if the current state is a winning or losing one and in the case return immediately
    		if (state.getTurn() == State.Turn.WHITEWIN)
    			return Double.POSITIVE_INFINITY;
    		if (state.getTurn() == State.Turn.BLACKWIN)
    			return Double.NEGATIVE_INFINITY;
    		
    		// White evaluation function becomes more aggressive as the game progresses
    		if (turn < 40) 
    			evaluationValue = yourPieceValue - oppPieceValue - enemyPiecesAroundKing(state) + kingInStrategicPos(state);
    		else if (turn < 70) 
    			evaluationValue = yourPieceValue - oppPieceValue - 2.0 * enemyPiecesAroundKing(state) + kingInStrategicPos(state);
    		else 
    			evaluationValue = yourPieceValue - oppPieceValue - 3.0 * enemyPiecesAroundKing(state) + kingInStrategicPos(state);
		} 
    	else 
		{
    		// Black evaluation function
    		// check if the current state is a winning or losing one and in the case return immediately
			if (state.getTurn() == State.Turn.BLACKWIN)
				return Double.POSITIVE_INFINITY;
    		if (state.getTurn() == State.Turn.WHITEWIN)
    			return Double.NEGATIVE_INFINITY;

    		// Black evaluation function becomes more aggressive as the game progresses
			if (turn < 40) 
				evaluationValue = yourPieceValue - oppPieceValue + 3*enemyPiecesAroundKing(state);
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
		
		if (state.getPawn(8, 6) == State.Pawn.BLACK) {
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
		
		return value;
	}
	
	/**
	 * This method checks if the king is in a crutial position in order to win the game.
	 * Given the state the moethod checks if the king is into an escape row or column,
	 * then checks if thath row or column hasn't any other pawns and if the king in that
	 * position is threatened from any black pawn.
	 * 
	 */
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
	
}
