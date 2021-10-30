// MinimaxAgent.java
// An agent that does a depth-limited minimax search with alpha-beta pruning to find its next move

import java.util.ArrayList;
import java.util.Random;

public class ABMinimaxAgent implements Agent{
	protected int player;
	protected int depthLimit;
	protected int strategy;
	public static final int NUMPIECES = 0;
	public static final int NUMMOVES = 1;
	public static final int DIFFERENCEMOVES = 2; 
	public static final int DCOMPLEX1 = 3; 


	public ABMinimaxAgent(int _player, int _strategy, int _depthLimit){
		player=_player;
		depthLimit=_depthLimit;
		if (!((_strategy == NUMPIECES) || (_strategy == NUMMOVES) || (_strategy == DIFFERENCEMOVES)|| (_strategy==DCOMPLEX1))){
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

	protected int maxValue(GameState g, int depth, int depthLimit, int alpha, int beta){
		
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

	protected int minValue(GameState g, int depth, int depthLimit, int alpha, int beta){
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

	// Static evaluation function 
	protected int e(GameState g){
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
		} else if (strategy==DCOMPLEX1){
			return g.complexScore1(player);
		}
		return 0;
	}

	protected boolean isMax(GameState g){
		return g.turn() == player;
	}

	protected class SearchNode{
		
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