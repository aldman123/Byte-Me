package io.battlesnake.starter.GameData;
import io.battlesnake.starter.Coord;

public class PsudoSnake {
	String id, name;
	int health;
	Coord[] body;
	
	public PsudoSnake(String id, String name, int health, Coord[] body) {
		this.id = id;
		this.name = name;
		this.health = health;
		this.body = body;
	}
}