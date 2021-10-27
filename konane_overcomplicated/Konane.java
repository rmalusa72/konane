// Server.java
// Sets up the board and two agents and facilitates play 

class Konane{

	Agent player1;
	Agent player2; 
	Agent[] players;
	public static final boolean printGames = false;

	public Konane(Agent _p1, Agent _p2){
		
		// Setup players and game state 
		player1 = _p1;
		player2 = _p2;
		players = new Agent[]{player1, player2};

		GameState g = new GameState();
		Move lastMove = null;

		// Continue alternating turns until game is completed 
		while(!g.isTerminal()){

			if(printGames){
				System.out.println(g);	
			}
			

			Agent currentPlayer = players[g.turn()];
			Move newMove = currentPlayer.getMove(g, lastMove);

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

		return;
	}

	public static void humanGame(){
		Konane s = new Konane(new HumanAgent(GameState.PLAYER1), new HumanAgent(GameState.PLAYER2));
	}

	public static void randomGame(){
		Konane s = new Konane(new RandomAgent(GameState.PLAYER1), new RandomAgent(GameState.PLAYER2));
	}

	public static void humanVsRandom(boolean computerIsX){
		if(computerIsX){
			Konane s = new Konane(new RandomAgent(GameState.PLAYER1), new HumanAgent(GameState.PLAYER2));
		} else {
			Konane s = new Konane(new HumanAgent(GameState.PLAYER1), new RandomAgent(GameState.PLAYER2));
		}
		
	}

	public static void main(String[] args){
		humanVsRandom(true);
		/*for(int i=0; i<10; i++){
			Konane s = new Konane(new ABMinimaxAgent(GameState.PLAYER1, ABMinimaxAgent.DIFFERENCEMOVES), new ABMinimaxAgent(GameState.PLAYER2, ABMinimaxAgent.NUMMOVES));
		}*/
	}

}