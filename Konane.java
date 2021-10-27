// Server.java
// Sets up the board and two agents and facilitates play 

import java.util.Arrays;

class Konane{

	Agent player1;
	Agent player2; 
	Agent[] players;
	public static final boolean printGames = true;
	public static final int numGames = 10;
	int winner; 
	long[] totalMoveTimes;
	int[] numMoves;

	public Konane(Agent _p1, Agent _p2, boolean remove){
		
		// Setup players and game state 
		player1 = _p1;
		player2 = _p2;
		players = new Agent[]{player1, player2};
		numMoves = new int[]{0,0};
		totalMoveTimes = new long[]{0,0};

		GameState g = new GameState();
		Move lastMove = null;

		if(remove){
			g.applyMoveInPlace(new Move(new int[]{3,3}, GameState.PLAYER1));
			g.applyMoveInPlace(new Move(new int[]{3,4}, GameState.PLAYER2));
		}

		// Continue alternating turns until game is completed 
		while(!g.isTerminal()){

			if(printGames){
				System.out.println(g);	
			}
			

			Agent currentPlayer = players[g.turn()];
			long startTimeMillis = System.currentTimeMillis();
			Move newMove = currentPlayer.getMove(g, lastMove);
			totalMoveTimes[g.turn()] += System.currentTimeMillis()-startTimeMillis;
			numMoves[g.turn()] += 1;
			System.out.println(newMove.toString());
			boolean applied = g.applyMoveInPlace(newMove);
			if(!applied){
				System.out.println("Move is invalid!");
				continue;
			} 

			lastMove = newMove;
		}

		if(printGames){
			System.out.println(g);
		}
		System.out.println(GameState.PLAYER_SYMBOL[g.winner()] + " has won!");
		winner= g.winner(); 

		System.out.println(Arrays.toString(numMoves));
		System.out.println(Arrays.toString(totalMoveTimes));

		return;
	}

	public int winner(){
		return winner;
	}

	public float averageTimePerMove(int player){
		return ((float)totalMoveTimes[player])/numMoves[player];
	}

	public float numMoves(int player){
		return numMoves[player];
	}

	public static void humanGame(){
		Konane s = new Konane(new HumanAgent(GameState.PLAYER1), new HumanAgent(GameState.PLAYER2), false);
	}

	public static void randomGame(){
		Konane s = new Konane(new RandomAgent(GameState.PLAYER1), new RandomAgent(GameState.PLAYER2), false);
	}

	public static void humanVsRandom(boolean computerIsX){
		if(computerIsX){
			Konane s = new Konane(new RandomAgent(GameState.PLAYER1), new HumanAgent(GameState.PLAYER2), false);
		} else {
			Konane s = new Konane(new HumanAgent(GameState.PLAYER1), new RandomAgent(GameState.PLAYER2), false);
		}
		
	}

	public static void main(String[] args){
		
		int p1Count = 0;
		int p2Count = 0; 
		float p1Avg = 0;
		float p2Avg = 0; 
		int p1moves = 0;
		int p2moves = 0; 

		for(int i=0; i<numGames; i++){
			Konane s = new Konane(new IDABMinimaxAgent(GameState.PLAYER1, ABMinimaxAgent.DIFFERENCEMOVES, 10), new RandomAgent(GameState.PLAYER2), true);
			//Konane s = new Konane(new IDABMinimaxAgent(GameState.PLAYER1, ABMinimaxAgent.DIFFERENCEMOVES, 5), new ABMinimaxAgent(GameState.PLAYER2, ABMinimaxAgent.DIFFERENCEMOVES, 6), true);
			if (s.winner()== GameState.PLAYER1){
				p1Count++;
			} else {
				p2Count++;
			}
			p1Avg += s.averageTimePerMove(GameState.PLAYER1);
			p2Avg += s.averageTimePerMove(GameState.PLAYER2);
			p1moves += s.numMoves(GameState.PLAYER1);
			p2moves += s.numMoves(GameState.PLAYER2);
		}

		System.out.println("p1 won " + Float.toString(((float)p1Count)/numGames) + " of games");
		System.out.println("p1 took avg " + Float.toString(((float)p1Avg)/(numGames*1000)));
		System.out.println("p1 made avg " + Float.toString((float)p1moves/(float)numGames) + " moves");
		System.out.println("p2 won " + Float.toString(((float)p2Count)/numGames) + " of games");		
		System.out.println("p2 took avg " + Float.toString(((float)p2Avg)/(numGames*1000)));
		System.out.println("p2 made avg " + Float.toString((float)p2moves/(float)numGames) + " moves");
	}

}