package io.battlesnake.starter.GameData;
import io.battlesnake.starter.Coord;

public class Board {
	int height, width;
	Coord[] food;
	PsudoSnake[] snakes;
	
	public Board(int height, int width, Coord[] food, PsudoSnake[] snakes) {
		this.height = height;
		this.width = width;
		this.food = food;
		this.snakes = snakes;
	}
}