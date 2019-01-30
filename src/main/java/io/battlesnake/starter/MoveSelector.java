package io.battlesnake.starter;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;

public class MoveSelector {
	private final String up = "up";
	private final String down = "down";
	private final String left = "left";
	private final String right = "right";
	
	private PrintWriter printWriter = null;
	private File file;
	private BoardData boardData;
	private int[][] board;
	private SnakeData self;
	private ArrayList<Coord> moveOptions, scratch, scratch2;
	private String optimalPath = up;
	
	public String selectMove(JsonNode moveRequest) {
		String turn = moveRequest.get("turn").asText();
		this.boardData = new BoardData(moveRequest);
		board = boardData.getBoard();
		self = boardData.getSelf();
		saveToFile(boardData, "Board_On_Move_" + turn);
		return valueRanking();
	}
	
	private String valueRanking() {
		int x = self.getHead().getX();
		int y = self.getHead().getY();
		System.out.println("Self: {" + x + ", " + y + "}");
		moveOptions = boardData.getAdjacent(x, y);
		System.out.println("L1: " + moveOptions.toString());
		if (isThereOptimalPath(moveOptions)) {
			return optimalPath;
		}
		
		//L2	Don't go adjacent to wall or snake
		System.out.println("L2: " + moveOptions.toString());
		scratch = new ArrayList<Coord>();
		//For each space from L1
		for (Coord c : moveOptions) {
			boolean isSafe = true;
			scratch2 = boardData.getAdjacent(c);
			//For each space adjacent to the direction being considered
			for (Coord c2 : scratch2) {
				//If the space 2 turns away from us is a bad snake
				if (boardData.get(c2) == 3) {
					//If the snake is our size or larger, it can kill us
					if (boardData.getSnake(c2).getSize() > self.getSize()) {
						isSafe = false;
					}
				}
			}
			if (isSafe) {
				scratch.add(c);
			}
		}
		
		if (isThereOptimalPath(scratch)) {
			return optimalPath;
		} else if (scratch.size() < moveOptions.size()) {
			moveOptions = scratch;
		}
		
		//L3	Are we adjacent to any food?
		System.out.println("L3: " + moveOptions.toString());
		scratch.clear();
		for (Coord c : moveOptions) {
			if (boardData.get(c) == 1) {
				scratch.add(c);
			}
		}
		
		if (isThereOptimalPath(scratch)) {
			return optimalPath;
		} else if (scratch.size() < moveOptions.size()) {
			moveOptions = scratch;
		}
		
		scratch.clear();
		//L4	Else pick a direction not adjacent to a wall
		System.out.println("L4: " + moveOptions.toString());
		for (Coord c : moveOptions) {
			boolean isSafe = true;
			scratch2 = boardData.getAdjacent(c);
			//For each space adjacent to the direction being considered
			for (Coord c2 : scratch2) {
				//If the space 2 turns away from us is a wall/tail
				if (boardData.get(c2) > 3) {
					isSafe = false;
				}
			}
			if (isSafe) {
				scratch.add(c);
			}
		}
		
		if (isThereOptimalPath(scratch)) {
			return optimalPath;
		} else {
			return coordToDirection(scratch.get(0));
		}
		
	}
	
	private boolean isThereOptimalPath(ArrayList<Coord> moveOptions) {
		switch (moveOptions.size()) {
		case 0:
			optimalPath = up;
			return false;
		case 1:
			optimalPath = coordToDirection(moveOptions.get(0));
			System.out.println("Optimal Path Found: " + optimalPath + ", " + moveOptions.get(0));
			return true;
		}
		
		return false;
		
	}
	
	private String coordToDirection(Coord point) {
		if (point.getX() - self.getHead().getX() < 0) {
			return left;
		} else if (point.getX() - self.getHead().getX() > 0) {
			return right;
		} else if (point.getY() - self.getHead().getY() < 0) {
			return up;
		} else if (point.getY() - self.getHead().getY() > 0) {
			return down;
		} else {
			System.out.println('\n' + "CoordToDirection Error" + '\n');
			return up;
		}
	}
	
	
	public void saveToFile(String output, String fileName) {
		String[] arr = {output};
		saveToFile(arr, fileName);
	}
	
	
	private void saveToFile(String[] output, String fileName) {
		PrintWriter file = createSaveFile(fileName);
		for (String s : output) {
			printWriter.println(s);
		}
		printWriter.close();
	}
	
	private void saveToFile(BoardData boardData, String fileName) {
		PrintWriter file = createSaveFile(fileName);
		file.println("Current State of Board");
		int[][] rawBoard = boardData.getBoard();
		for (int j = 0; j < rawBoard[0].length; j++) {
			String line = "{";
			for (int i = 0; i < rawBoard.length - 1; i++) {
				line += rawBoard[i][j] + ", ";
			}
			line += rawBoard[rawBoard.length - 1][j] + "}";
			
			file.println(line);
		}
		
		file.close();
		
	}
	
	private PrintWriter createSaveFile(String fileName) {
		file = new File (
		"C:\\Users\\alexa\\Documents\\BattleSnakes" +
			"\\starter-snake-java\\src\\main\\java\\io\\battlesnake\\starter" +
			 "\\JsonNodeLog\\" + fileName + ".txt"
			);
		try {
			printWriter = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return printWriter;
	}
		
}