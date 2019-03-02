package io.battlesnake.starter;
import com.fasterxml.jackson.databind.JsonNode;

public class SnakeData {
	private String id;
	private String name;
	private int health;
	private Coord[] body;

	public SnakeData(JsonNode rawData) {
		id = rawData.get("id").asText();
		name = rawData.get("name").asText();
		health = rawData.get("health").asInt();

		body = new Coord[rawData.get("body").size()];
		JsonNode bodypart;
		for (int i = 0; i < body.length; i++) {
			bodypart = rawData.get("body").get(i);
			body[i] = new Coord(bodypart.get("x").asInt(), bodypart.get("y").asInt());
		}
	}


	public String getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getHealth() {
		return health;
	}

	public int getSize() {
		return body.length;
	}

	public Coord[] getBody() {
		return body;
	}

	public Coord getHead() {
		return body[0];
	}

	public Coord getTail() {
		return body[body.length - 1];
	}
}
