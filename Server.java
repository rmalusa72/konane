// Server.java
// Sets up the board and two agents and facilitates play 

class Server{

	Agent player1;
	Agent player2; 
	Agent[] players = {player1, player2};

	public Server(Agent _p1, Agent _p2){
		
		// Setup players and game state 
		player1 = _p1;
		player2 = _p2;

		GameState g = new GameState();
		Move lastMove = null;

		// Continue alternating turns until game is completed 
		while(!g.isTerminal()){

			System.out.println(g);

			Agent currentPlayer = players[g.turn()];
			Move newMove = currentPlayer.getMove(g, lastMove);

			boolean applied = g.applyMoveInPlace(newMove);
			if(!applied){
				System.out.println("Move is invalid!");
				continue;
			} 

			lastMove = newMove;
		}

		System.out.println(g);
		System.out.println(GameState.PLAYER_SYMBOL[g.winner()] + " has won!");

		return;
	}

}