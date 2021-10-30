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

		while(!g.isTerminal()){

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

	// Run two games pitting ABMinimax with agent1strategy against agent2strategy:
	// one where a1 is x and one where a2 is x
	public static void ABTesting(int agent1Strategy, int agent2Strategy, int depth){
		float a1Time = 0;
		float a2Time = 0; 
		float a1wins = 0; 
		float a2wins = 0; 

		Konane s = new Konane(new ABMinimaxAgent(GameState.PLAYER1, agent1Strategy, depth), new ABMinimaxAgent(GameState.PLAYER2, agent2Strategy, depth), true);
		if (s.winner()== GameState.PLAYER1){
			a1wins++;
		} else {
			a2wins++;
		}

		a1Time += s.averageTimePerMove(GameState.PLAYER1);
		a2Time += s.averageTimePerMove(GameState.PLAYER2);

		s = new Konane(new ABMinimaxAgent(GameState.PLAYER1, agent2Strategy, depth), new ABMinimaxAgent(GameState.PLAYER2, agent1Strategy, depth), true);
		if (s.winner()== GameState.PLAYER1){
			a2wins++;
		} else {
			a1wins++;
		}

		a2Time += s.averageTimePerMove(GameState.PLAYER1);
		a1Time += s.averageTimePerMove(GameState.PLAYER2);

		System.out.println("a1 won " + Float.toString(((float)a1wins)/2) + " of games");
		System.out.println("a1 took avg " + Float.toString(((float)a1Time)/(2*1000)));
		System.out.println("a2 won " + Float.toString(((float)a2wins)/2) + " of games");		
		System.out.println("p2 took avg " + Float.toString(((float)a2Time)/(2*1000)));

	}

	// Run two*rounds games pitting RABMinimax with agent1strategy against agent2strategy:
	// one where a1 is x and one where a2 is x
	public static void RABTesting(int agent1Strategy, int agent2Strategy, int depth, int rounds){
		float a1Time = 0;
		float a2Time = 0; 
		float a1wins = 0; 
		float a2wins = 0; 

		for(int i=0; i<rounds; i++){

			Konane s = new Konane(new RABMinimaxAgent(GameState.PLAYER1, agent1Strategy, depth), new RABMinimaxAgent(GameState.PLAYER2, agent2Strategy, depth), true);
			if (s.winner()== GameState.PLAYER1){
				a1wins++;
			} else {
				a2wins++;
			}

			a1Time += s.averageTimePerMove(GameState.PLAYER1);
			a2Time += s.averageTimePerMove(GameState.PLAYER2);

			s = new Konane(new RABMinimaxAgent(GameState.PLAYER1, agent2Strategy, depth), new RABMinimaxAgent(GameState.PLAYER2, agent1Strategy, depth), true);
			if (s.winner()== GameState.PLAYER1){
				a2wins++;
			} else {
				a1wins++;
			}

			a2Time += s.averageTimePerMove(GameState.PLAYER1);
			a1Time += s.averageTimePerMove(GameState.PLAYER2);

		}

		System.out.println("a1 won " + Float.toString(((float)a1wins)/(2*rounds)) + " of games");
		System.out.println("a1 took avg " + Float.toString(((float)a1Time)/(2*rounds*1000)));
		System.out.println("a2 won " + Float.toString(((float)a2wins)/(2*rounds)) + " of games");		
		System.out.println("p2 took avg " + Float.toString(((float)a2Time)/(2*rounds*1000)));

	}

	// Run two*rounds games pitting ABMinimax with agent1strategy against RABMinimax w agent2strategy:
	// one where a1 is x and one where a2 is x
	public static void ABRABTesting(int agent1Strategy, int agent2Strategy, int depth, int rounds){
		float a1Time = 0;
		float a2Time = 0; 
		float a1wins = 0; 
		float a2wins = 0; 

		for(int i=0; i<rounds; i++){

			Konane s = new Konane(new ABMinimaxAgent(GameState.PLAYER1, agent1Strategy, depth), new RABMinimaxAgent(GameState.PLAYER2, agent2Strategy, depth), true);
			if (s.winner()== GameState.PLAYER1){
				a1wins++;
			} else {
				a2wins++;
			}

			a1Time += s.averageTimePerMove(GameState.PLAYER1);
			a2Time += s.averageTimePerMove(GameState.PLAYER2);

			s = new Konane(new RABMinimaxAgent(GameState.PLAYER1, agent2Strategy, depth), new ABMinimaxAgent(GameState.PLAYER2, agent1Strategy, depth), true);
			if (s.winner()== GameState.PLAYER1){
				a2wins++;
			} else {
				a1wins++;
			}

			a2Time += s.averageTimePerMove(GameState.PLAYER1);
			a1Time += s.averageTimePerMove(GameState.PLAYER2);

		}

		System.out.println("a1 won " + Float.toString(((float)a1wins)/2*rounds) + " of games");
		System.out.println("a1 took avg " + Float.toString(((float)a1Time)/(2*rounds*1000)));
		System.out.println("a2 won " + Float.toString(((float)a2wins)/2*rounds) + " of games");		
		System.out.println("p2 took avg " + Float.toString(((float)a2Time)/(2*rounds*1000)));

	}


	// Run two*rounds games pitting 
	// agent 1: ABMinimax with agent1strategy and depth limit depth, against
	// agent 2: IDABMinimax with agent2strategy and time limit time 
	// half where a1 is x and one where a2 is x
	public static void ABIDTesting(int agent1Strategy, int agent2Strategy, int depth, int timelimit, int rounds){
		float a1Time = 0;
		float a2Time = 0; 
		float a1wins = 0; 
		float a2wins = 0; 

		for(int i=0; i<rounds; i++){

			Konane s = new Konane(new ABMinimaxAgent(GameState.PLAYER1, agent1Strategy, depth), new IDABMinimaxAgent(GameState.PLAYER2, agent2Strategy, timelimit), true);
			if (s.winner()== GameState.PLAYER1){
				a1wins++;
			} else {
				a2wins++;
			}

			a1Time += s.averageTimePerMove(GameState.PLAYER1);
			a2Time += s.averageTimePerMove(GameState.PLAYER2);

			s = new Konane(new IDABMinimaxAgent(GameState.PLAYER1, agent2Strategy, timelimit), new ABMinimaxAgent(GameState.PLAYER2, agent1Strategy, depth), true);
			if (s.winner()== GameState.PLAYER1){
				a2wins++;
			} else {
				a1wins++;
			}

			a2Time += s.averageTimePerMove(GameState.PLAYER1);
			a1Time += s.averageTimePerMove(GameState.PLAYER2);

		}

		System.out.println("a1 won " + Float.toString(((float)a1wins)/2*rounds) + " of games");
		System.out.println("a1 took avg " + Float.toString(((float)a1Time)/(2*rounds*1000)));
		System.out.println("a2 won " + Float.toString(((float)a2wins)/2*rounds) + " of games");		
		System.out.println("p2 took avg " + Float.toString(((float)a2Time)/(2*rounds*1000)));

	}

	// Run two*rounds games pitting 
	// agent 1: RABMinimax with agent1strategy and depth limit depth, against
	// agent 2: IDABMinimax with agent2strategy and time limit time 
	// half where a1 is x and one where a2 is x
	public static void RABIDTesting(int agent1Strategy, int agent2Strategy, int depth, int timelimit, int rounds){
		float a1Time = 0;
		float a2Time = 0; 
		float a1wins = 0; 
		float a2wins = 0; 

		for(int i=0; i<rounds; i++){

			Konane s = new Konane(new RABMinimaxAgent(GameState.PLAYER1, agent1Strategy, depth), new IDABMinimaxAgent(GameState.PLAYER2, agent2Strategy, timelimit), true);
			if (s.winner()== GameState.PLAYER1){
				a1wins++;
			} else {
				a2wins++;
			}

			a1Time += s.averageTimePerMove(GameState.PLAYER1);
			a2Time += s.averageTimePerMove(GameState.PLAYER2);

			s = new Konane(new IDABMinimaxAgent(GameState.PLAYER1, agent2Strategy, timelimit), new RABMinimaxAgent(GameState.PLAYER2, agent1Strategy, depth), true);
			if (s.winner()== GameState.PLAYER1){
				a2wins++;
			} else {
				a1wins++;
			}

			a2Time += s.averageTimePerMove(GameState.PLAYER1);
			a1Time += s.averageTimePerMove(GameState.PLAYER2);

		}

		System.out.println("a1 won " + Float.toString(((float)a1wins)/2*rounds) + " of games");
		System.out.println("a1 took avg " + Float.toString(((float)a1Time)/(2*rounds*1000)));
		System.out.println("a2 won " + Float.toString(((float)a2wins)/2*rounds) + " of games");		
		System.out.println("p2 took avg " + Float.toString(((float)a2Time)/(2*rounds*1000)));

	}

	public static void main(String[] args){
		// No args to run normal random game with computer playing x 
		if(args.length == 1){
			if(args[1].equals("o")){
				humanVsRandom(false);
			} else {
				humanVsRandom(true);
			}
		}
		ABRABTesting(ABMinimaxAgent.DIFFERENCEMOVES, ABMinimaxAgent.DCOMPLEX2, 6, 2);
	}

}