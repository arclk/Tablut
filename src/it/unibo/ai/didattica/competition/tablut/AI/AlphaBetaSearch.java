package it.unibo.ai.didattica.competition.tablut.AI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import it.unibo.ai.didattica.competition.tablut.domain.*;

/**
 * Implements an iterative Minimax search with alpha-beta pruning which
 * at every iteration increase the depth of the search in order to have
 * an aprroximation of all the lower depth values in case of timeout and
 * action ordering. Maximal computation time is specified in seconds.
 * 
 * @author archi
 *
 */
public class AlphaBetaSearch {

    public static Game game;
    protected int currDepthLimit;
    private Timer timer;
    public State.Turn turn;
    boolean winMove;

    /**
     * Creates a new search object for a given game.
     *
     * @param game    The game.
     * @param time    Maximal computation time in seconds.
     */
    public AlphaBetaSearch(Game game, int time) {
        AlphaBetaSearch.game = game;
        this.timer = new Timer(time);
    }

    /**
     * execute the algorithm on the passed state in order to return 
     * the best action to be perfomed
     * 
     * @param actualState
     * @return the action to be performed
     */
    public Action makeDecision(State state) {
    	
    	int actionIndex = 0;
    	boolean iterative = true;
        this.turn = state.getTurn();
    	State clonedState;
        List<Action> tmpAllActions = state.getAllLegalActions(game);
        List<Action> allActions = new ArrayList<Action>();
//      System.out.println("Azioni: " + allActions.size());
//    	System.out.println(state.getPlayerCoordSet());
		System.out.println("Turno: " + state.getTurnNumber());

        Collections.shuffle(tmpAllActions);

        // aggiungo le coordinate del re all'inizio in modo che le sue mosse vengono processate per prima
        if(state.getTurn() == State.Turn.WHITE) {
        	allActions = state.getLegalMovesForPosition(game, state.getKingPosition());
        	allActions.addAll(tmpAllActions);
        }
        else
        	allActions = tmpAllActions;
        	
//      System.out.println(allActions);
        double[] actionVal = new double [allActions.size()];
        winMove = false;
        timer.start();
        if(iterative)
        	currDepthLimit = 0;
        else
        	currDepthLimit = 3;
        
        do
        {
        	if(iterative)
        		currDepthLimit++;
//	        int i = 0;
	        actionIndex = 0;
	        for (Action action : allActions) 
	        {
//	        	System.out.println("Idx: " + i);
//	        	i++;
	        	
	            clonedState = state.clone();
	            actionVal[actionIndex] = minValue(game.processMove(clonedState, action), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1);
	            if (timer.timeOutOccurred()) {
	            	System.out.println("Timer occurred");
	                break; // exit from action loop
	            }
	            actionIndex++;
	        }
	        System.out.println(Arrays.toString(actionVal));
//	      actionVal = tmpActionVal.clone();
        } while(!timer.timeOutOccurred() && iterative && !winMove);
        
        // finds the maximum action value in order to perform it
        int maxVal = 0;    
        try {
//        	System.out.println(Arrays.toString(actionVal));
        	maxVal = getHighestValue(actionVal);
        	System.out.println("Action value: " + actionVal[maxVal]);
        	
        }catch(ArrayIndexOutOfBoundsException e) {
        	System.out.println(state.getPlayerCoordSet());
        	System.out.println(state.getAllLegalActions(game));
        }
        
        // prints the duration of the decision in order to test
        System.out.println("Duration of the move: " + timer.timePassed() + "ms");
        return allActions.get(maxVal);
    }

    /**
     * 
     * @param state
     * @param alpha
     * @param beta
     * @param depth
     * @return an utility value
     */
    public double maxValue(State state, double alpha, double beta, int depth) {
        if (state.gameOver() || depth >= currDepthLimit || timer.timeOutOccurred()) {
            return eval(state);
        } else {
            double value = Double.NEGATIVE_INFINITY;
            ArrayList<State> successors = state.getSuccessors(game);
            
            for (State sucState : successors) 
            {
                value = Math.max(value, minValue(sucState, alpha, beta, depth + 1));
                if (value >= beta)
                    return value;
                alpha = Math.max(alpha, value);
            }
            return value;
        }
    }

    /**
     * 
     * @param state
     * @param alpha
     * @param beta
     * @param depth
     * @return an utility value
     */
    public double minValue(State state, double alpha, double beta, int depth) {
        if (state.gameOver() || depth >= currDepthLimit || timer.timeOutOccurred()) {
            return eval(state);
        } else {
        	
            double value = Double.POSITIVE_INFINITY;
            ArrayList<State> successors = state.getSuccessors(game);
            
            for (State sucState : successors) 
            {
                value = Math.min(value, maxValue(sucState, alpha, beta, depth + 1));
                if (value <= alpha)
                    return value;
                beta = Math.min(beta, value);
            }
            return value;
        }
    }

    /**
     * 
     * @param state
     * @return the heuristic value of the given state
     */
    protected double eval(State state) {
    	
    	double value = Heuristic.evaluation(state, turn);
    	if (value == Double.POSITIVE_INFINITY)
    		this.winMove = true;
//    	System.out.println(state);
//    	System.out.println("Heuristic evaluation: " + value);
    	return value;
    }

    /**
     * Given the array of the action values returns the first one among the highest
     * @param actionVal
     * @return index of the most convenient action
     */
    public int getHighestValue(double[] actionVal) {
    	int maxIdx = 0; 
    	System.out.println("Array da cui prendere il massimo");
    	System.out.println(Arrays.toString(actionVal));

    	try {
	    	double maxValue = actionVal[0];
	    	for(int i = 1; i < actionVal.length; i++) {
	    		if (actionVal[i] > maxValue) {
	    			maxValue = actionVal[i];
	    			maxIdx = i;
	    		}
	    	}
    	}catch(ArrayIndexOutOfBoundsException e) {
    		System.out.println("Eccezione: ");
    	}
    	System.out.println("Massimo indice: " + maxIdx);
    	return maxIdx;
    }

    // nested helper classes
    private static class Timer {
        private long duration;
        private long startTime;

        Timer(int maxSeconds) {
            this.duration = 1000 * maxSeconds;
        }

        void start() {
            startTime = System.currentTimeMillis();
        }

        boolean timeOutOccurred() {
            return System.currentTimeMillis() > startTime + duration;
        }
        
        long timePassed() {
        	return System.currentTimeMillis() - startTime;
        }
    }
    
}