// Server.java
// Sets up the board and two agents and facilitates play 

import java.util.Arrays;

class Konane{

	Agent player1;
	Agent player2; 
	Agent[] players;
	public static final boolean printGames = false;
	public static final boolean printInfo = false;
	public static final boolean printHeuristics = false; 
	public static final int numGames = 1;
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
		int i=0; 

		while(!g.isTerminal() && i<4){

			if(printGames){
				System.out.println(g);	
			}
			if(printInfo){
				System.out.println(g.printInfo());
				System.out.println("Player 1 moves: " + Integer.toString(g.numMoves(GameState.PLAYER1)) + "/Player 2 moves: " + Integer.toString(g.numMoves(GameState.PLAYER2)));
				System.out.println("Safe moves: " + Arrays.toString(g.numSafeMoves(GameState.PLAYER1)));
				System.out.println("Safe squares: "+ Arrays.toString(g.numSafeSquares(GameState.PLAYER1)));
				System.out.println("Safe squares 2: "+ Arrays.toString(g.numSafeSquares2(GameState.PLAYER1)));
			}
			if(printHeuristics){
				System.out.println("Player 1 moves: " + Integer.toString(g.numMoves(GameState.PLAYER1)) + "/Player 2 moves: " + Integer.toString(g.numMoves(GameState.PLAYER2)));
				System.out.println("Safe moves: " + Arrays.toString(g.numSafeMoves(GameState.PLAYER1)));
				System.out.println("Complex score 1: " + Integer.toString(g.complexScore1(GameState.PLAYER1)) + "/Complex score 2: " + Integer.toString(g.complexScore1(GameState.PLAYER2)));	
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
			i++;
		}

		if(printGames){
			System.out.println(g);
		}
		if(printInfo){
			System.out.println(g.printInfo());
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
		// No args to run normal random game with computer playing x 
		if(args.length == 0){
			humanVsRandom(true);
		}
		if(args.length == 1){
			if(args[1].equals("o")){
				humanVsRandom(false);
			} else {
				testing();
			}
		}
	}

	public static void testing(){



	}



	/*

	public static void main(String[] args){
		/*
		//		randomGame();

		int p1Count = 0;
		int p2Count = 0; 
		float p1Avg = 0;
		float p2Avg = 0; 
		int p1moves = 0;
		int p2moves = 0; 

		for(int i=0; i<numGames; i++){
			/*
			Konane s = new Konane(new RABMinimaxAgent(GameState.PLAYER1, RABMinimaxAgent.DCOMPLEX1, 6), new RABMinimaxAgent(GameState.PLAYER2, RABMinimaxAgent.DIFFERENCEMOVES, 6), true);
			if (s.winner()== GameState.PLAYER1){
				p1Count++;
			} else {
				p2Count++;
			}
			p1Avg += s.averageTimePerMove(GameState.PLAYER1);
			p2Avg += s.averageTimePerMove(GameState.PLAYER2);
			p1moves += s.numMoves(GameState.PLAYER1);
			p2moves += s.numMoves(GameState.PLAYER2);
			*/ 
		/*
			int a1strategy = RABMinimaxAgent.DCOMPLEX1; 
			int a2strategy = RABMinimaxAgent.DIFFERENCEMOVES;
				
			Konane s; 

			if(i % 2 == 0){

				System.out.println("X is " + Integer.toString(a1strategy) + "|O is " + Integer.toString(a2strategy));

				s = new Konane(new RABMinimaxAgent(GameState.PLAYER1, a1strategy, 2), new RABMinimaxAgent(GameState.PLAYER2, a2strategy, 2), true);
				
				if (s.winner()== GameState.PLAYER1 ){
					p1Count++;
				} else {
					p2Count++;
				}

				p1Avg += s.averageTimePerMove(GameState.PLAYER1);
				p2Avg += s.averageTimePerMove(GameState.PLAYER2);
				p1moves += s.numMoves(GameState.PLAYER1);
				p2moves += s.numMoves(GameState.PLAYER2);
			} else {
				System.out.println("X is " + Integer.toString(a2strategy) + "|O is " + Integer.toString(a1strategy));

				s = new Konane(new RABMinimaxAgent(GameState.PLAYER1, a2strategy, 2), new RABMinimaxAgent(GameState.PLAYER2, a1strategy, 2), true);
				

				if (s.winner()== GameState.PLAYER1 ){
					p2Count++;
				} else {
					p1Count++;
				}

				p2Avg += s.averageTimePerMove(GameState.PLAYER1);
				p1Avg += s.averageTimePerMove(GameState.PLAYER2);
				p2moves += s.numMoves(GameState.PLAYER1);
				p1moves += s.numMoves(GameState.PLAYER2);
			
			}

		}

		System.out.println("p1 won " + Float.toString(((float)p1Count)/numGames) + " of games");
		System.out.println("p1 took avg " + Float.toString(((float)p1Avg)/(numGames*1000)));
		System.out.println("p1 made avg " + Float.toString((float)p1moves/(float)numGames) + " moves");
		System.out.println("p2 won " + Float.toString(((float)p2Count)/numGames) + " of games");		
		System.out.println("p2 took avg " + Float.toString(((float)p2Avg)/(numGames*1000)));
		System.out.println("p2 made avg " + Float.toString((float)p2moves/(float)numGames) + " moves");
		
	}
	*/

}