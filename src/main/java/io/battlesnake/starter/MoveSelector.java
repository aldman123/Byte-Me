package io.battlesnake.starter;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.ArrayList;
import java.lang.Math;

public class MoveSelector {
	private final String up = "up";
	private final String down = "down";
	private final String left = "left";
	private final String right = "right";
	

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
		
		
		if(self.getHealth() < 30){
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
		System.out.println("\n \n \n MOVE OPTIONS ( " + moveOptions + ": AT " + max + " IS " + moveOptions.get(max) + "\n \n");
		return coordToDirection(moveOptions.get(max));
		
	}
	
	//returns direction to closest food
	//returns String up, down, left, or right
	private String starvingDirection(){
		ArrayList<Coord> foodCoords = new ArrayList<>();
		int dist = 0;
		
		for(int x = 0; x < board.length; x++){
			for(int y = 0; y < board[x].length; y++){
				if(boardData.get(x,y) == 1){
					foodCoords.add(new Coord(x,y));
				}
			}
		}
		
		int[] absDist = new int[foodCoords.size()];
		int num = 0;
		for(int i = 0; i < absDist.length; i++){
			num = (Math.abs(self.getHead().getX() - foodCoords.get(i).getX()) + Math.abs(self.getHead().getY() - foodCoords.get(i).getY()));
			absDist[i] = num;
		}
		
		for(int i = 0; i < absDist.length; i++){
			if(absDist[dist] > absDist[i]){
				dist = i;
			}
		}
		
		return coordToDirection(foodCoords.get(dist));
		
	}
	
	//finds the interger value of the volume in that direction
	//reterns int, the higher the value the better
	
	private int valueOfDirection(Coord direction, int value){
		
		ArrayList<Coord> dirToCheck= boardData.getAdjacent(direction.getX(), direction.getY());
		addCheckDir(dirToCheck, 4);
		
		ArrayList<Coord> newList = new ArrayList<>();
		newList.add(dirToCheck.get(1));
		boolean isIn;
		for(Coord c : dirToCheck){
			isIn = false;
			for(Coord k : newList){
				if(c.equels(k)){
					isIn = true;
				}
			}
			if(!isIn){
				newList.add(c);
			}
		}
		
		dirToCheck.clear();
		dirToCheck.addAll(newList);
		
		for(Coord c : dirToCheck){
			if(boardData.get(c) < 2){
				System.out.println("\n COORDANATE OF VALUE ADD " + c);
				value++;
			}
		}
		
		return value;
		
	}
	
	
	private ArrayList<Coord> adjDirs = new ArrayList<>();
	private ArrayList<Coord> badAdjDirs = new ArrayList<>();
	
	private void addCheckDir(ArrayList<Coord> dirs, int count){
		count--;
		if(count < 1){
			return;
		}
		if(dirs.size() < 1){
			return;
		}
		
		ArrayList<Coord> tempDirs = (ArrayList<Coord>) dirs.clone();
		
		for(Coord c : tempDirs){
			adjDirs = boardData.getAdjacent(c.getX(), c.getY());
			for(Coord k : adjDirs){
				if(boardData.get(k) < 2 && !dirs.contains(k)){
					dirs.add(k);
				} else {
					badAdjDirs.add(k);
				}
					
			}
			for(Coord j : badAdjDirs){
				adjDirs.remove(j);
			}
			
			addCheckDir(adjDirs, count);
		}
		/*
		for(Coord c : tempDirs){
			adjDirs = boardData.getAdjacent(c.getX(), c.getY());
			for(Coord k : adjDirs){
				if(boardData.get(k) < 2){
					dirs.add(k);
					adj2Dirs = boardData.getAdjacent(c.getX(), c.getY());
					for(Coord l : adj2Dirs){
						if(boardData.get(l) < 2){
							dirs.add(l);
						}
					}
				}
			}
		}
		*/
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
			if(boardData.get(tempCoord) < 2)
				return left;
		}
		if (point.getX() - self.getHead().getX() > 0) {
			tempCoord = new Coord(headX + 1, headY);
			if(boardData.get(tempCoord) < 2)
				return right;
		} 
		if (point.getY() - self.getHead().getY() < 0) {
			tempCoord = new Coord(headX, headY - 1);
			if(boardData.get(tempCoord) < 2)
				return up;
		}
		if (point.getY() - self.getHead().getY() > 0) {
			tempCoord = new Coord(headX, headY + 1);
			if(boardData.get(tempCoord) < 2)
				return down;
		}
		System.out.println('\n' + "CoordToDirection Error" + '\n');
		return up;
		
	}
	
	
		
}