// IDABMinimaxAgent.java
// An agent that does an iteratively deepened ABminimax search to find its next move, randomly ordering moves at first level

import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

public class RABIDMinimaxAgent extends IDABMinimaxAgent implements Agent{

	public RABIDMinimaxAgent(int _player, int _strategy, int _timeLimit){
		super(_player, _strategy, _timeLimit);
	}

	// Find and return minimax-recommended move 
	protected Move getMove(GameState g, Move lastMove, int timeInSeconds){
		
		long startTimeMillis = System.currentTimeMillis();

		// Generate possible successors 
		ArrayList<Move> successors = g.getPossibleMoves();
		Collections.shuffle(successors);
		
		int overallBestMoveValue = Integer.MIN_VALUE;
		Move overallBestMove = null;
		int depth = 2; 

		while((System.currentTimeMillis() - startTimeMillis < SECPERMIL*timeInSeconds)){
			
			int bestMoveValue = Integer.MIN_VALUE;
			Move bestMove = null;
			boolean timecutoff = false;
			idCutoff = false;

			for(int i=0; i<successors.size(); i++){
				
				if(System.currentTimeMillis() - startTimeMillis > SECPERMIL*timeInSeconds){
					System.out.println("Cutoff mid-depth " + Integer.toString(depth));
					timecutoff = true;
					break;
				}

				// Get gamestate resulting from each 
				Move move = successors.get(i);
				//System.out.println(move);
				GameState result = g.applyMove(move);
				//System.out.println(result);

				//Apply minimax to each to determine expected value 
				int value = minValue(result, 1, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);

				if(value > bestMoveValue){
					bestMoveValue = value;
					bestMove = move;
				}

			}	

			if(!idCutoff){	
				overallBestMoveValue = bestMoveValue;
				overallBestMove = bestMove; 
				System.out.println("Explored whole move tree. Move " + bestMove.toString() + " at depth " + Integer.toString(depth) + " in " + Long.toString(System.currentTimeMillis() - startTimeMillis));					
				break; 
			}

			if(!timecutoff){
				
			
				overallBestMoveValue = bestMoveValue;
				overallBestMove = bestMove; 

				System.out.println("Move " + bestMove.toString() + " at depth " + Integer.toString(depth) + " in " + Long.toString(System.currentTimeMillis() - startTimeMillis));					
			
				depth++; 	
			}

		}
		return overallBestMove;
	}
}