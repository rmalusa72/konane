// RABMinimaxAgent.java
// An agent that does a depth-limited minimax search to find its next move
// Alpha-beta pruning, random ordering of first moves

import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;
import java.util.Arrays;

public class RABMinimaxAgent extends ABMinimaxAgent implements Agent{
	
	public RABMinimaxAgent(int _player, int _strategy, int _depthLimit){
		super(_player, _strategy, _depthLimit);
	}

	// Find and return minimax-recommended move 
	public Move getMove(GameState g, Move lastMove){
		// Generate possible successors 
		ArrayList<Move> successors = g.getPossibleMoves();
		Collections.shuffle(successors);

		int bestMoveValue = Integer.MIN_VALUE;
		Move bestMove = null;

		for(int i=0; i<successors.size(); i++){
			// Get gamestate resulting from each 
			Move move = successors.get(i);
			GameState result = g.applyMove(move);
			
			//Apply minimax to each to determine expected value 
			int value = minValue(result, 1, depthLimit, Integer.MIN_VALUE, Integer.MAX_VALUE);

			if(value > bestMoveValue){
				bestMoveValue = value;
				bestMove = move;
			}

		}
		return bestMove;
	}
}