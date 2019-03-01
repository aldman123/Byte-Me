public class Node {
	protected Node up, down, left, right;
	int type, x, y;
	public Node(int x, int y, int type) {
		this.type = type;
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getType() {
		return type;
	}
	
	protected void linkDown(Node node) {
		this.down = node;
		node.up = this;
	}
	
	protected void linkUp(Node node) {
		this.up = node;
		node.down = this;
	}
	
	protected void linkRight(Node node) {
		this.right = node;
		node.left = this;
	}
	
	protected void linkLeft(Node node) {
		this.left = node;
		node.right = this;
	}
	
}