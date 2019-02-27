package io.battlesnake.starter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomTestCases {
	private static final String up = "up";
	private static final String down = "down";
	private static final String left = "left";
	private static final String right = "right";
	
	private static ObjectMapper mapper;
	
	public CustomTestCases() {
		mapper = new ObjectMapper();
	}
	
	
	public JsonNode case1() {
		ObjectNode gameState = mapper.createObjectNode();
		
		ObjectNode game = mapper.createObjectNode();
		game.put("id", "Test Case 1");
		gameState.put("game", game);
		
		gameState.put("turn", 10);
		
		ObjectNode board = mapper.createObjectNode();
			board.put("height", 3);
			board.put("width", 3);
			ArrayNode foodCoords = mapper.createArrayNode();
				ObjectNode food = mapper.createObjectNode();
				food.put("x", 1);
				food.put("y", 1);
				foodCoords.add(food);
			board.put("food", foodCoords);
			ArrayNode snakes = mapper.createArrayNode();
			board.put("snakes", snakes);
		gameState.put("board", board);
				
		ObjectNode you = mapper.createObjectNode();
			you.put("id", "self");
			you.put("name", "Byte-Me");
			you.put("health", 90);
			
			ObjectNode body = mapper.createObjectNode();
			ArrayNode ourBody = mapper.createArrayNode();
			body.put("x", 1);
			body.put("y", 0);
			ourBody.add(body);
		
			body = mapper.createObjectNode();
			body.put("x", 2);
			body.put("y", 0);
			ourBody.add(body);
		
			body = mapper.createObjectNode();
			body.put("x", 2);
			body.put("y", 1);
			ourBody.add(body);
		
			body = mapper.createObjectNode();
			body.put("x", 2);
			body.put("y", 2);
			ourBody.add(body);
		
			body = mapper.createObjectNode();
			body.put("x", 1);
			body.put("y", 2);
			ourBody.add(body);
			you.put("body", ourBody);
		snakes.add(you);
		gameState.put("you", you);
		gameState.put("desiredOutcome", "down");
		
		return gameState;
	}
	
}