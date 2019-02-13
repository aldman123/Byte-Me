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
	private static JsonNodeFactory;
	private static MoveSelector byteMe;
	
	public CustomTestCases() {
		mapper = new ObjectMapper();
		byteMe = new MoveSelector();
		jsonNodeFacotry = new JsonNodeFactory();
	}
	
	
	public JsonNode case1() {
		ArrayNode ourBody, scratch;
		ourBody = jsonNodeFactory.ArrayNode();
		scratch = jsonNodeFactory.ArrayNode();
		scratch.add(1);	//X
		scratch.add(0);	//Y
		ourBody.add(scratch);
		scratch = jsonNodeFactory.ArrayNode();
		scratch.add(2);	//X
		scratch.add(0);	//Y
		ourBody.add(scratch);
		scratch = jsonNodeFactory.ArrayNode();
		scratch.add(2);	//X
		scratch.add(1);	//Y
		ourBody.add(scratch);
		scratch = jsonNodeFactory.ArrayNode();
		scratch.add(2);	//X
		scratch.add(2);	//Y
		ourBody.add(scratch);
		scratch = jsonNodeFactory.ArrayNode();
		scratch.add(1);	//X
		scratch.add(2);	//Y
		ourBody.add(scratch);
		scratch = jsonNodeFactory.ArrayNode();
		
		System.out.println(ourBody);
		
		ArrayNode you = jsonNodeFactory;
		
		PsudoSnake you = new PsudoSnake("1", "Self_Case1", 100, ourBody);
		PsudoSnake[] snakes = {you};
		Coord[] food = {new Coord(0,2), new Coord(1,1)};
		Board board = new Board(3, 3, food, snakes);
		Game game = new Game("Case1");
		CustomGameState data = new CustomGameState(game, 10, board, you);
		//JsonNode jsonNode = mapper.valueToTree(data);
		objectMapper.writeValue(new File("target/scratch.json"), data);
		
		return jsonNode;
	}
	
}