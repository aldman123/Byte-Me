package io.battlesnake.starter;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.HashSet;
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
	private Queue<Coord> checkBoard = new LinkedList<>();
	
	public String selectMove(JsonNode moveRequest) {
		String turn = moveRequest.get("turn").asText();
		this.boardData = new BoardData(moveRequest);
		board = boardData.getBoard();
		self = boardData.getSelf();
		//saveToFile(boardData, "Board_On_Move_" + turn);
		return valueRanking();
	}
	
	private String valueRanking() {
		int x = self.getHead().getX();
		int y = self.getHead().getY();
		checkBoard.add(new Coord(x,y));
		//System.out.println("Self: {" + x + ", " + y + "}");
		moveOptions = boardData.getAdjacent(x, y);
		//System.out.println("L1: " + moveOptions.toString());
		if (isThereOptimalPath(moveOptions)) {
			return optimalPath; 
		}
		
		//L2	Don't go adjacent to wall or snake
		//System.out.println("L2: " + moveOptions.toString());
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
			moveOptions = (ArrayList<Coord>) scratch.clone();
		}
		scratch.clear();
		
		//L3	Are we adjacent to any food?
		//System.out.println("L3: " + moveOptions.toString());
		for (Coord c : moveOptions) {
			if (boardData.get(c) == 1) {
				scratch.add(c);
			}
		}
		
		if (isThereOptimalPath(scratch)) {
			return optimalPath;
		} else if (scratch.size() > 1) {
			moveOptions = (ArrayList<Coord>) scratch.clone();
		}
		scratch.clear();
		
		//L4	Else pick a direction not adjacent to a wall
		//System.out.println("L4: " + moveOptions.toString());
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
		
		if (scratch.size() > 0) {
			moveOptions = (ArrayList<Coord>) scratch.clone();
		}
		if (isThereOptimalPath(moveOptions)) {
			return optimalPath;
			
		
		} else {
			//return coordToDirection(moveOptions.get(0));
			return volumeFormula(moveOptions);
		}
		
		
		
		
	}
	
	/*Returns a direction to move if there is more than one option*
	  Returns String - up, down, left, or right*/
	private String volumeFormula(ArrayList<Coord> moveOptions){
		
		
		if(self.getHealth() < 25){
			for(Coord c : moveOptions){
			checkBoard.add(c);
		}
			return starvingDirection();
		}
		
		return findVolume();
		//return coordToDirection(moveOptions.get(0));
	}
	
	
	
	//finds the direction with the most open areas for movement
	//returns String direction
	private String findVolume(){
		int[] directions = new int[moveOptions.size()];
		int max = 0;
			
		for(int i = 0; i < directions.length; i++){
			directions[i] = valueOfDirection(moveOptions.get(i), 0);
			System.out.println("\n DIRECTIONS: " + moveOptions.get(i) + " VALUE: " +directions[i] + "\n");
		}
		
		for(int i = 0; i < directions.length; i++){
			if(directions[max] < directions[i]){
				max = i;
			}
		}
		System.out.println("\n \n MOVE OPTIONS : " + moveOptions + "\n");
		return coordToDirection(moveOptions.get(max));
		
	}
	
	//returns direction to closest food
	//returns String up, down, left, or right
	private String starvingDirection(){
		Coord tempCoord = checkBoard.remove();
		if(boardData.get(tempCoord) == 1){
			return coordToDirection(tempCoord);
		}
		
		ArrayList<Coord> tempArray= boardData.getAdjacent(tempCoord.getX(), tempCoord.getY());
				
		for(Coord c : tempArray){
			checkBoard.add(c);
		}
		return starvingDirection();
	}
	
	//finds the interger value of the volume in that direction
	//reterns int, the higher the value the better
	
	private int valueOfDirection(Coord direction, int value){
		
		
		ArrayList<Coord> dirToCheck= boardData.getAdjacent(direction.getX(), direction.getY());
		
		ArrayList<Coord> tempDirToCheck= (ArrayList<Coord>) dirToCheck.clone();
		
		dirToCheck.add(direction);
				
		for(Coord c : tempDirToCheck){
			ArrayList<Coord> tempArray= boardData.getAdjacent(c.getX(), c.getY());
			for(Coord k : tempArray){
				dirToCheck.add(k);
			}
		}
		
		Set<Coord> set = new HashSet<>(dirToCheck);
		dirToCheck.clear();
		dirToCheck.addAll(set);
		
		
		for(Coord c : dirToCheck){
			switch(boardData.get(c)){
				case 0:
					value++;
					break;
				case 1:
					value+=2;
					break;	
			}
		}
		
		return value;
		
		
	}
	
	private boolean isThereOptimalPath(ArrayList<Coord> moveOptions) {
		switch (moveOptions.size()) {
		case 0:
			optimalPath = up;
			return false;
		case 1:
			optimalPath = coordToDirection(moveOptions.get(0));
			//System.out.println("Optimal Path Found: " + optimalPath + ", " + moveOptions.get(0));
			return true;
		}
		optimalPath = coordToDirection(moveOptions.get(0));
		return false;
		
	}
	
	private String coordToDirection(Coord point) {
		int headY = self.getHead().getY();
		int headX = self.getHead().getX();
		Coord tempCoord;
		
		if (point.getX() - self.getHead().getX() < 0)  {
			tempCoord = new Coord(headX - 1, headY);
			if(boardData.get(tempCoord) == 1 || boardData.get(tempCoord) == 0)
				return left;
		}
		if (point.getX() - self.getHead().getX() > 0) {
			tempCoord = new Coord(headX + 1, headY);
			if(boardData.get(tempCoord) == 1 || boardData.get(tempCoord) == 0)
				return right;
		} 
		if (point.getY() - self.getHead().getY() < 0) {
			tempCoord = new Coord(headX, headY - 1);
			if(boardData.get(tempCoord) == 1 || boardData.get(tempCoord) == 0)
				return up;
		}
		if (point.getY() - self.getHead().getY() > 0) {
			tempCoord = new Coord(headX, headY + 1);
			if(boardData.get(tempCoord) == 1 || boardData.get(tempCoord) == 0)
				return down;
		}
		System.out.println('\n' + "CoordToDirection Error" + '\n');
		return up;
		
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
		file = new File (fileName + ".txt");
		try {
			printWriter = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return printWriter;
	}
		
}