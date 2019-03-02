package io.battlesnake.starter;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
/*
 * This class takes a BattleSnakes JsonNode as input,
 * and creates an object to let us analyse the data
 * Created by Alexander Aldridge
 */
 public class BoardData {
	private final int SNAKE_BODY = 5;
	private final int SNAKE_TAIL = 4;
	private final int SNAKE_HEAD = 3;
	private final int OUR_HEAD = 2;
	private final int FOOD = 1;
	private final int FREE = 0;
	private String gameID;
	private int[][] board;
	private int turnNumber;
	private SnakeData[] snakes;
	private SnakeData self;
	private Coord[] food;

	public BoardData(JsonNode rawData) {
		gameID = rawData.get("game").get("id").asText();
		JsonNode rawBoard = rawData.get("board");
		JsonNode rawFood = rawBoard.get("food");
		turnNumber = rawData.get("turn").asInt();
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
				board[p.getX()][p.getY()] = SNAKE_BODY;
			}

			//Sets enemy snake heads to be 3
			board[s.getHead().getX()][s.getHead().getY()] = SNAKE_HEAD;
			if (turnNumber > 3) {
				board[s.getTail().getX()][s.getTail().getY()] = SNAKE_TAIL;
			}
		}


		//Sets our head to be 2
		board[self.getHead().getX()][self.getHead().getY()] = OUR_HEAD;

		//Sets food to be 1
		for (Coord p : food){
			board[p.getX()][p.getY()] = FOOD;
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

	protected SnakeData getSnake(Coord c) {
		for (SnakeData s : snakes) {
			for (Coord c2 : s.getBody()) {
				if (c.getX() == c2.getX() && c.getY() == c2.getY()) {
					return s;
				}
			}
		}
		return null;
	}

	protected int[][] getBoard() {
		return board;
	}

	protected SnakeData getSelf() {
		return self;
	}

	/*
	 * Returns the value associated with that square on the board
	 * If invalid coordinates, then it returns 5
	 */
	protected int get(int x, int y) {
		if (0 > x || x >= board.length || 0 > y || y >= board[0].length) {
			return 5;
		} else {
			return board[x][y];
		}
	}

	protected int get(Coord c) {
		return get(c.getX(), c.getY());
	}

	protected ArrayList<Coord> getAdjacent(Coord c) {
		return getAdjacent(c.getX(), c.getY());
	}

	protected Coord[] getFood(){
		return food;
	}

	protected ArrayList<Coord> getAdjacent(int x, int y) {
		ArrayList<Coord> scratch = new ArrayList<Coord>();
		if (get(x-1,y) < SNAKE_HEAD) {
			scratch.add(new Coord(x-1,y));
		}
		if (get(x+1,y) < SNAKE_HEAD) {
			scratch.add(new Coord(x+1,y));
		}
		if (get(x,y-1) < SNAKE_HEAD) {
			scratch.add(new Coord(x, y-1));
		}
		if (get(x,y+1) < SNAKE_HEAD) {
			scratch.add(new Coord(x, y+1));
		}
		return scratch;
	}
 }
