package io.battlesnake.starter;
/*
 * This class is used to convert a board array to a 2D linkedlist
 * of Nodes to be used in pathfinding algorithms.
 * The origin will always be the head of our snake.
 * By Alexander Aldridge
 */

public class BoardDataNodes {
<<<<<<< HEAD
	private final int left = 0;
=======
/*	private final int left = 0;
>>>>>>> tryFoodOptions
	private final int right = 1;
	private final int up = 2;
	private final int down = 3;
	private int size = 0;
	private Node ourSnake;
	private int[][] boardArray;
	
	public BoardDataNodes(BoardData data) {
		boardArray = data.getBoard();
		int[][] boardArrayClone = new int[boardArray.length][boardArray[0].length];
<<<<<<< HEAD
		for (int x = 0; x < boardArrayClone.length; x++) {
			for (int y = 0; y < boardArrayClone[0].length; y++) {
=======
		for (int x = 0; i < boardArrayClone.length; x++) {
			for (int y = 0; i < boardArrayClone[0].length; y++) {
>>>>>>> tryFoodOptions
				boardArrayClone[x][y] = boardArray[x][y];
			}
		}
		
		this.boardArray = boardArrayClone;
		Coord origin = data.getSelf().getHead();
<<<<<<< HEAD
		Node ourSnake = new Node(origin.getX(), origin.getY(), 2); //Our head has value of 2
		this.boardArray[origin.getX()][origin.getY()] = -1;
		size++;
		generateNode(ourSnake, 0);
		generateNode(ourSnake, 1);
		generateNode(ourSnake, 2);
		generateNode(ourSnake, 3);
		this.ourSnake = ourSnake;
	}
	
	public Node getOrigin() {
		return ourSnake;
=======
		Node origin = new Node(origin.getX(), origin.getY(), 2); //Our head has value of 2
		this.boardArray[origin.getX()][origin.getY()] = -1;
		size++;
		generateNode(origin, 0);
		generateNode(origin, 1);
		generateNode(origin, 2);
		generateNode(origin, 3);
	}
	
	public Node getOrigin() {
		return origin;
>>>>>>> tryFoodOptions
	}
	
	public int getSize() {
		return size;
	}
	
	
	
	private void generateNode(Node origin, int direction) {
		Node newNode;
		int x, y;
		if (direction == left) {
			x = origin.getX() - 1;
			y = origin.getY();
		} else if (direction == right) {
			x = origin.getX() + 1;
			y = origin.getY();
		} else if (direction == up) {
			x = origin.getX();
			y = origin.getY() - 1;
		} else if (direction == down) {
			x = origin.getX();
			y = origin.getY() + 1;
		} else {
			System.out.println("Faulty Direction");
			return;
		}
		
		//Is this a proper location?
		if (x < 0 || boardArray.length <= x || y < 0 || boardArray[0].length <= x) {
			return;
		}
		
		//Has this spot already been visited?
		if (boardArray[x][y] == -1) {
			return;
		} else {
			newNode = new Node(x, y, boardArray[x][y]);
			size++;
			if (direction == left) {
				origin.linkRight(newNode);
			} else if (direction == right) {
				origin.linkLeft(newNode);
			} else if (direction == up) {
				origin.linkUp(newNode);
			} else if (direction == down) {
				origin.linkDown(newNode);
			}
			
			boardArray[x][y] = -1;
			for (int i = 0; i < 4; i++) {
				if (i != direction) {
					generateNode(origin, i);
				}
			}
			return;
		}
		
<<<<<<< HEAD
	}
=======
	}*/
>>>>>>> tryFoodOptions
}