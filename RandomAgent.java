// RandomAgent.java 
// An agent that makes random moves

import java.util.ArrayList;
import java.util.Random;

public class RandomAgent implements Agent{
	int player;
	Random r;

	public RandomAgent(int _player){
		player=_player;
		r = new Random();
	}

	public Move getMove(GameState g, Move lastMove){
		ArrayList<Move> possibleMoves = g.getPossibleMoves();
		int randomChoice = r.nextInt(possibleMoves.size());
		return possibleMoves.get(randomChoice);
	}
}