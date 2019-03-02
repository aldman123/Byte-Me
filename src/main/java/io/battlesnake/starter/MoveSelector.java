package io.battlesnake.starter;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.lang.Math;

import java.util.ArrayList;

public class MoveSelector {
	private final String up = "up";
	private final String down = "down";
	private final String left = "left";
	private final String right = "right";

	private BoardData boardData;
	private int[][] board;
	private SnakeData self;
	int hungerThreshhold = 90;

	private ArrayList<Coord> moveOptions, scratch, scratch2, foodCoords, adjDirs;
	private String optimalPath = up;
	private ArrayList<Coord> badAdjDirs = new ArrayList<>();
	private int boardWidth;

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
		boardWidth = board.length;

		System.out.println("Self: {" + x + ", " + y + "}");
		moveOptions = boardData.getAdjacent(x, y);
		System.out.println("L1: " + moveOptions.toString());
		if (isThereOptimalPath(moveOptions)) {
			return optimalPath;
		}

		if(self.getSize() > boardWidth*2){
			hungerThreshhold = 30;
		}

		//L2	Don't go adjacent to wall or snake
		//System.out.println("L2: " + moveOptions.toString());
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
		if (scratch.size() > 0) {
			moveOptions = (ArrayList<Coord>) scratch.clone();
		}
		if (isThereOptimalPath(moveOptions)) {
			return optimalPath;
		} else {
			return volumeFormula(moveOptions);
		}

	}

	public void saveToFile(String output, String fileName) {
		String[] arr = {output};
	}

	/*Returns a direction to move if there is more than one option*
	  Returns String - up, down, left, or right*/
	private String volumeFormula(ArrayList<Coord> moveOptions){

		if(self.getHealth() < hungerThreshhold){
			return starvingDirection();
		} else if (self.getSize() > board.length * 2) {
			return squeeze(moveOptions);
		} else {
			return findVolume();
		}
	}

	/*
	 * This method has the snake going left to right and slowly squeezing everyone above us
	 */
	private final int minHunger = 40;

	private String squeeze(ArrayList<Coord> moveOptions) {
		String[] availableDirections = new String[moveOptions.size()];
		for (int i = 0; i < moveOptions.size(); i++) {
			availableDirections[i] = coordToDirection(moveOptions.get(i));
		}

		//Get all the snakes above us
		boolean canGoDown = false;

		for (String s : availableDirections) {
			if (s.equals(down)) {
				canGoDown = true;
			}
		}
		if (!canGoDown) {
			return findVolume();
		}
		SnakeData[] snakes = boardData.getSnakes();
		int lowestSnakeY = 0;
		for (int i = 1; i < snakes.length; i++) {
			for (Coord c : snakes[i].getBody()) {
				if (c.getY() > lowestSnakeY) {
					lowestSnakeY = c.getY();
				}
			}
		}

		if (self.getHead().getY() <= lowestSnakeY) {
			return down;
		}

		//Are we not hungry? Then avoid food
		boolean avoidFood = !(self.getHealth() > minHunger);

		//Have we run out of space above us? Then don't go up
		boolean canGoUp = true; //Temp

		//Else move left, up, right, down in that order of priority
		String optimalPath = availableDirections[0];
		int bestChoiceValue = 0;
		for (String s : availableDirections) {
			if (s.equals(left)) {
				return left;
			} else if (!optimalPath.equals(right) && s.equals(right)) {
				optimalPath = right;
			} else if (optimalPath.equals(down) && s.equals(up)) {
				optimalPath = up;
			}
		}

		return optimalPath;

	}



	//finds the direction with the most open areas for movement
	//returns String direction
	private String findVolume() {
		if (moveOptions.size() < 1) {
			return left;
		}
		int[] directions = new int[moveOptions.size()];
		int max = 0;

		for(int i = 0; i < directions.length; i++){
			directions[i] = valueOfDirection(moveOptions.get(i), 0);
		}

		for(int i = 0; i < directions.length; i++){
			if(directions[max] < directions[i]){
				max = i;
			}
		}

		return coordToDirection(moveOptions.get(max));
	}


	//returns direction to closest food
	//returns String up, down, left, or right
	private String starvingDirection(){
		foodCoords = new ArrayList<Coord>(Arrays.asList(boardData.getFood()));
		return calcStarvingDir();
	}

	//finds the position of the closest piece of food
	//returns direction of food
	private String calcStarvingDir(){
		int dist = 0;
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
		addCheckDir(dirToCheck, 4 );

		ArrayList<Coord> newList = new ArrayList<>();
		newList.add(dirToCheck.get(0));
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

			value++;
		}

		return value;

	}

	//checks the empty spaces where we can move
	//does not return but chenges the elements in the dirToCheck memory location
	private void addCheckDir(ArrayList<Coord> dirs, int count){
		count--;
		if(count < 1){
			return;
		}
		if(dirs.size() < 1){
			return;
		}

		ArrayList<Coord> tempDirs = (ArrayList<Coord>)dirs.clone();

		for(Coord c : tempDirs){
			adjDirs = boardData.getAdjacent(c.getX(), c.getY());
			for(Coord k : adjDirs){
				if((boardData.get(k) < 2 || boardData.get(k) == 4)&& !dirs.contains(k)){
					dirs.add(k);
				} else {
					badAdjDirs.add(k);
				}
			}
			for(Coord j : badAdjDirs){
				adjDirs.remove(j);
			}
			addCheckDir(adjDirs, count);
			for(Coord o : adjDirs){
				dirs.add(o);
			}
			adjDirs.clear();
		}
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
			if(boardData.get(tempCoord) < 2 || boardData.get(tempCoord) == 4){
				return left;
			}
		}
		if (point.getX() - self.getHead().getX() > 0) {
			tempCoord = new Coord(headX + 1, headY);
			if(boardData.get(tempCoord) < 2 || boardData.get(tempCoord) == 4){

				return right;
			}
		}
		if (point.getY() - self.getHead().getY() < 0) {
			tempCoord = new Coord(headX, headY - 1);
			if(boardData.get(tempCoord) < 2 || boardData.get(tempCoord) == 4){

				return up;
			}
		}
		if (point.getY() - self.getHead().getY() > 0) {
			tempCoord = new Coord(headX, headY + 1);
			if(boardData.get(tempCoord) < 2 || boardData.get(tempCoord) == 4){

				return down;
			}
		}

		foodCoords.remove(point);
		if(foodCoords.size() > 0){
			return calcStarvingDir();
		}
		try{
			return coordToDirection(moveOptions.get(0));
		} catch (IndexOutOfBoundsException e){
			return up;
		}
	}



}
