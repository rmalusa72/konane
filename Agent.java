// Agent.java
// Abstract class defining a game-playing agent

public abstract interface Agent{

	// An agent must be able to provide a next move
	public abstract Move getMove(GameState g, Move lastMove);
}