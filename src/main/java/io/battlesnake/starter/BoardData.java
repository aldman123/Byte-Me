package io.battlesnake.starter;
import com.fasterxml.jackson.databind.JsonNode;
/*
 * This class takes a BattleSnakes JsonNode as input,
 * and creates an object to let us analyse the data
 * Created by Alexander Aldridge
 */
 public class BoardData {
	private String gameID;
	private int[][] board; //0 = free, 1 = food, 2 = our head, 3 = snake head, 4 = snake body
	private int turnNumber;
	private SnakeData[] snakes;
	private SnakeData self;
	private Coord[] food;
	
	public BoardData(JsonNode rawData) {
		gameID = rawData.get("game").get("id").asText();
		JsonNode rawBoard = rawData.get("board");
		JsonNode rawFood = rawBoard.get("food");
		board = new int[rawBoard.get("width").asInt()][rawBoard.get("height").asInt()];
		
		food = new Coord[rawFood.size()];
		for (int i = 0; i < food.length; i++) {
			food[i] = new Coord(
				rawFood.get(i).get("x").asInt(),
				rawFood.get(i).get("y").asInt()
			);
		}
		
		snakes = new SnakeData[rawBoard.get("snakes").size()];
		for (int i = 0; i < snakes.length; i++) {
			snakes[i] = new SnakeData(rawBoard.get("snakes").get(i));
		}
		
		self = new SnakeData(rawData.get("you"));
		updateBoard();
	}
	
	private void updateBoard() {
		for (SnakeData s : snakes) {
			//Sets all snake parts to be 4
			for (Coord p : s.getBody()) {
				board[p.getX()][p.getY()] = 4;
			}
			
			//Sets enemy snake heads to be 3
			board[s.getHead().getX()][s.getHead().getY()] = 3;
		}
		
		
		//Sets our head to be 2
		board[self.getHead().getX()][self.getHead().getY()] = 2;
		
		//Sets food to be 1
		for (Coord p : food){
			board[p.getX()][p.getY()] = 1;
		}
	}
	
	protected String getID() {
		return gameID;
	}
	
	protected int getTurnNumber() {
		return turnNumber;
	}
	
	protected SnakeData[] getSnakes() {
		return snakes;
	}
	
	protected int[][] getBoard() {
		return board;
	}
	
	protected SnakeData getSelf() {
		return self;
	}
	
	/*
	 * Returns the value associated with that square on the board
	 * If invalid coordinates, then it returns 4 (to symbolize a wall)
	 */
	protected int get(int x, int y) {
		if (0 > x || x >= board.length()) {
			return 4;
		}
		if (0 > y || y >= board[0].length) {
			return 4
		}
		
		return board[x][y];
	}
 }