// MinimaxAgent.java
// An agent that does a depth-limited minimax search to find its next move

import java.util.ArrayList;
import java.util.Random;

// POSSIBLE TODO LATER:
// Save some information while searching 
// Evaluate performance against random agent
// Iterative deepening 

public class ABMinimaxAgent implements Agent{
	int player;
	int depthLimit;
	int strategy;
	public static final int NUMPIECES = 0;
	public static final int NUMMOVES = 1;
	public static final int DIFFERENCEMOVES = 2; 
	public static final int DSAFEMOVES = 3; 
	public static final int DSAFESQUARES = 4; 

	public ABMinimaxAgent(int _player, int _strategy, int _depthLimit){
		player=_player;
		depthLimit=_depthLimit;
		if (!((_strategy == NUMPIECES) || (_strategy == NUMMOVES) || (_strategy == DIFFERENCEMOVES)|| (_strategy == DSAFEMOVES) || (_strategy == DSAFESQUARES))){
			throw new IllegalArgumentException("Invalid strategy");
		}
		strategy = _strategy;

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
			//System.out.println(move);
			GameState result = g.applyMove(move);
			//System.out.println(result);

			//Apply minimax to each to determine expected value 
			int value = minValue(result, 1, depthLimit, Integer.MIN_VALUE, Integer.MAX_VALUE);

			if(value > bestMoveValue){
				bestMoveValue = value;
				bestMove = move;
			}

		}
		return bestMove;
	}

	private int maxValue(GameState g, int depth, int depthLimit, int alpha, int beta){
		
		// Depth/terminal cutoff 
		if(g.isTerminal() || depth == depthLimit){
			return e(g);
		}

		// Generate successors
		ArrayList<Move> successors = g.getPossibleMoves();
		for(int i=0; i<successors.size(); i++){
			// Evaluate this successor
			GameState result = g.applyMove(successors.get(i));
			int value = minValue(result, depth+1, depthLimit, alpha, beta);
			
			// Update alpha 
			if(value > alpha){
				alpha = value;
			}

			//Possible cutoff 
			if(alpha >= beta){
				return beta;
			}
		}
		return alpha; 
	}

	private int minValue(GameState g, int depth, int depthLimit, int alpha, int beta){
		// Depth/terminal cutoff 
		if(g.isTerminal() || depth == depthLimit){
			return e(g);
		}

		// Generate successors
		ArrayList<Move> successors = g.getPossibleMoves();
		for(int i=0; i<successors.size(); i++){
			// Evaluate this successor
			GameState result = g.applyMove(successors.get(i));
			int value = maxValue(result, depth+1, depthLimit, alpha, beta);
			
			// Update alpha 
			if(value < beta){
				beta = value;
			}

			//Possible cutoff 
			if(beta <= alpha){
				return alpha;
			}
		}
		return beta; 
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

		if(strategy == NUMPIECES){
			return g.numPieces(player);
		} else if(strategy == NUMMOVES){
			return g.numMoves(player);
		} else if(strategy == DIFFERENCEMOVES){
			return g.numMoves(player) - g.numMoves(GameState.OPPOSITE_PLAYER[player]);
		} else if (strategy == DSAFEMOVES){
			int[] safeMoves = g.numSafeMoves(player);
			return safeMoves[0] - safeMoves[1];
		} else if (strategy == DSAFESQUARES){
			int[] safeMoves = g.numSafeSquares(player);
			return safeMoves[0] - safeMoves[1];
		}
		return 0;
	}

	private boolean isMax(GameState g){
		return g.turn() == player;
	}

	private class SearchNode{
		
		SearchNode parent;
		Move lastAction;
		GameState g; 
		int depth; 
		
		SearchNode(GameState _g, SearchNode _p, Move _l, int _d){
			parent = _p;
			lastAction = _l;
			g = _g; 
			depth = _d;
		}
	}
}