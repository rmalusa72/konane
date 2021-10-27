// HumanAgent.java
// Represents a human player 

import java.util.NoSuchElementException;
import java.util.InputMismatchException;
import java.io.Console;
import java.util.Scanner;
import java.util.ArrayList;

public class HumanAgent implements Agent{

	int player;

	public HumanAgent(int _player){
		player = _player;
	}

	public Move getMove(GameState g, Move lastMove){
		
		Console c = System.console();
		
		boolean validMoveSubmitted = false; 
		while(!validMoveSubmitted){

			ArrayList<int[]> coordinates = new ArrayList<int[]>();
			boolean gotStartCoordinates = false;

			while (!gotStartCoordinates){
				String input = c.readLine("Player " + GameState.PLAYER_SYMBOL[player] + ", which piece would you like to move? Enter as 'row column'"); 
				try{
					Scanner s = new Scanner(input);
					int row = s.nextInt() - 1;
					int col = s.nextInt() - 1;
					s.close();

					coordinates.add(new int[]{row, col});
					gotStartCoordinates = true;
				} catch (InputMismatchException e){
					System.out.println("Invalid input format");
					continue;
				} catch (NoSuchElementException e){
					System.out.println("Invalid input format");
					continue;
				}

			}

			boolean moveComplete = false;
			while (!moveComplete){
				String input = c.readLine("Enter another set of coordinates, or nothing to finish move");
				if(input.isEmpty()){
					moveComplete = true;
					break; 
				}
				try{
					Scanner s = new Scanner(input);
					int row = s.nextInt() - 1;
					int col = s.nextInt() - 1;
					s.close();

					coordinates.add(new int[]{row, col});
					gotStartCoordinates = true;
				} catch (InputMismatchException e){
					System.out.println("Invalid input format");
					continue;
				} catch (NoSuchElementException e){
					System.out.println("Invalid input format");
					continue;
				}			 
			}

			Move tryMove = new Move(coordinates.size()-1, coordinates, player);
			if(g.isValid(tryMove)){
				return tryMove;
			} else {
				System.out.println("That move is invalid");
			}
		}
		return null;
	}

}