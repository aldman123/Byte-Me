/*
 * This class is used to convert a board array to a 2D linkedlist
 * of Nodes to be used in pathfinding algorithms.
 * The origin will always be the head of our snake.
 * By Alexander Aldridge
 */

public class BoardDataNodes {
	final int left = 0;
	final int right = 1;
	final int up = 2;
	final int down = 3;
	Node ourSnake;
	int[][] boardArray;
	public BoardDataNode(BoardData data) {
		boardArray = data.getBoard();
		int[][] boardArrayClone = new int[boardArray.length][boardArray[0].length];
		for (int x = 0; i < boardArrayClone.length; x++) {
			for (int y = 0; i < boardArrayClone[0].length; y++) {
				boardArrayClone[x][y] = boardArray[x][y];
			}
		}
		
		this.boardArray = boardArrayClone;
		Coord origin = data.getSelf().getHead();
		Node origin = new Node(origin.getX(), origin.getY(), 2); //Our head has value of 2
		this.boardArray[origin.getX()][origin.getY()] == -1;
		generateNode(origin, 0);
		generateNode(origin, 1);
		generateNode(origin, 2);
		generateNode(origin, 3);
	}
	
	public getOrigin() {
		return origin;
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
		
	}
}