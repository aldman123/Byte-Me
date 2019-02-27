package io.battlesnake.starter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.battlesnake.starter.GameData.*;

public class CustomTestCases {
	private static final String up = "up";
	private static final String down = "down";
	private static final String left = "left";
	private static final String right = "right";
	
	private static ObjectMapper mapper;
	private static MoveSelector byteMe;
	
	public CustomTestCases() {
		mapper = new ObjectMapper();
		byteMe = new MoveSelector();
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
			ObjectNode foodCoords = mapper.createObjectNode();
				ArrayNode food = mapper.createArrayNode();
				food.put("x", 1);
				food.put("y", 1);
				foodCoords.add(food);
			board.putArray("food", foodCoords);
			ArrayNode snakes = mapper.createArrayNode();
				
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
			you.putArray("body", ourBody);
		snakes.add(you);
			
			
		
		/*
		PsudoSnake you = new PsudoSnake("1", "Self_Case1", 100, ourBody);
		PsudoSnake[] snakes = {you};
		Coord[] food = {new Coord(0,2), new Coord(1,1)};
		Board board = new Board(3, 3, food, snakes);
		Game game = new Game("Case1");
		CustomGameState data = new CustomGameState(game, 10, board, you);
		*/
		//JsonNode jsonNode = mapper.valueToTree(data);
		objectMapper.writeValue(new File("target/scratch.json"), data);
		
		return jsonNode;
	}
	
}