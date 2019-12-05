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
 * @author Arcangelo Alberico
 *
 */
public class AlphaBetaSearch {

    public static Game game;
    protected int currDepthLimit;
    private Timer timer;
    public State.Turn turn;
    boolean winMove;
    boolean iterative;

    /**
     * Creates a new search object for a given game.
     *
     * @param game    		the game
     * @param time    		maximal computation time in seconds
     * @param iterative		iterative version of the algorithm
     * @param depthLimit	depth to which expand if not iterative
     */
    public AlphaBetaSearch(Game game, int time, boolean iterative, int depthLimit) {
        AlphaBetaSearch.game = game;
        this.timer = new Timer(time);
        this.iterative = iterative;
        this.currDepthLimit = depthLimit;
    }

    /**
     * Execute the algorithm on the passed state in order to return 
     * the best action to be perfomed.
     * 
     * It shuffles any time the allowed actions in order to have better perfomance.
     * In case of the white player the king's actions are placed at the beginning 
     * of the array in order to be evaluated first.
     * 
     * @param state 	state from which calculate the action according the algorithm
     * @return the action to be performed
     */
    public Action makeDecision(State state) {
    	
    	int actionIndex = 0;
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
     * @param alpha 	aplha value for pruning
     * @param beta		beta value for pruning
     * @param depth		current depth of the tree
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
     * @param alpha 	aplha value for pruning
     * @param beta		beta value for pruning
     * @param depth		current depth of the tree
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
     * Evaluate the state according the heuristic function given by the Heuristic class.
     * 
     * @param state		state to be evaluated
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
     * Given the array of the action values returns the first one among the highest.
     * 
     * @param actionVal		array of action values
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
    /**
     * Static class to take time into account in the algorithm
     * 
     * @author Arcangelo Alberico
     */
    private static class Timer {
        private long duration;
        private long startTime;

        /**
         * Creates a new timer.
         * 
         * @param maxSeconds 	max time in seconds to execute the algorithm
         */
        Timer(int maxSeconds) {
            this.duration = 1000 * maxSeconds;
        }

        /**
         * Starts the timer.
         */
        void start() {
            startTime = System.currentTimeMillis();
        }

        /**
         * Check if the time is passed.
         * 
         * @return true if the time is passed else false
         */
        boolean timeOutOccurred() {
            return System.currentTimeMillis() > startTime + duration;
        }
        
        /**
         * Count the milliseconds passed from the calling of the start method
         * @return milliseconds from the start
         */
        long timePassed() {
        	return System.currentTimeMillis() - startTime;
        }
    }
    
}