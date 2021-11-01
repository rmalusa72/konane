// MinimaxAgent.java
// An agent that does a depth-limited minimax search to find its next move

import java.util.ArrayList;
import java.util.Random;

public class MinimaxAgent implements Agent{
	int player;
	int depthLimit;

	public MinimaxAgent(int _player){
		player=_player;
		depthLimit=5;
	}

	// Find and return minimax-recommended move 
	public Move getMove(GameState g, Move lastMove){
		// Generate possible successors 
		ArrayList<Move> successors = g.getPossibleMoves();
		
		int bestMoveValue = Integer.MIN_VALUE;
		Move bestMove = null;

		for(int i=0; i<successors.size(); i++){
			// Get gamestate resulting from each 
			Move move = successors.get(i);
			GameState result = g.applyMove(move);

			//Apply minimax to each to determine expected value 
			int value = minimaxRecursive(result, 1, depthLimit);

			if(value > bestMoveValue){
				bestMoveValue = value;
				bestMove = move;
			}

		}
		return bestMove;
	}

	private int minimaxRecursive(GameState g, int depth, int depthLimit){
		
		// If we have reached the depth limit or a terminal state, return evaluation value
		if(g.isTerminal() || depth == depthLimit){
			return e(g);
		}

		// Otherwise, recurse
		if(isMax(g)){
			int maxMoveValue = Integer.MIN_VALUE;

			ArrayList<Move> successors = g.getPossibleMoves();
			for(int i=0; i<successors.size(); i++){
				GameState result = g.applyMove(successors.get(i));
				//Apply minimax to each to determine expected value 
				int value = minimaxRecursive(result, depth+1, depthLimit);

				if(value > maxMoveValue){
					maxMoveValue = value;
				}				
			}
			return maxMoveValue; 

		} else {
			int minMoveValue = Integer.MAX_VALUE;

			ArrayList<Move> successors = g.getPossibleMoves();
			for(int i=0; i<successors.size(); i++){
				GameState result = g.applyMove(successors.get(i));
				//Apply minimax to each to determine expected value 
				int value = minimaxRecursive(result, depth+1, depthLimit);

				if(value < minMoveValue){
					minMoveValue = value;
				}				
			}
			return minMoveValue; 
		}
	}

	// Static evaluation function 
	private int e(GameState g){
		if(g.isTerminal()){
			if(g.winner() == player){
				return Integer.MAX_VALUE-1;
			} else {
				return Integer.MIN_VALUE+1;
			}
		}

		return g.numMoves(player) - g.numMoves(GameState.OPPOSITE_PLAYER[player]);
	}

	private boolean isMax(GameState g){
		return g.turn() == player;
	}

}