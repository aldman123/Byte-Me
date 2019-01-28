package io.battlesnake.starter;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;

public class MoveSelector {
	private final String up = "up";
	private final String down = "down";
	private final String left = "left";
	private final String right = "right";
	
	private PrintWriter printWriter = null;
	private File file;
	private BoardData board;
	
	public String selectMove(JsonNode moveRequest) {
		String turn = moveRequest.get("turn").asText();
		board = new BoardData(moveRequest);
		saveToFile(board, "Board_On_Move_" + turn);
		return right;
	}
	
	public void saveToFile(String output, String fileName) {
		String[] arr = {output};
		saveToFile(arr, fileName);
	}
	
	
	private void saveToFile(String[] output, String fileName) {
		PrintWriter file = createSaveFile(fileName);
		for (String s : output) {
			printWriter.println(s);
		}
		printWriter.close();
	}
	
	private void saveToFile(BoardData board, String fileName) {
		PrintWriter file = createSaveFile(fileName);
		file.println("Current State of Board");
		int[][] rawBoard = board.getBoard();
		for (int j = 0; j < rawBoard[0].length; j++) {
			String line = "{";
			for (int i = 0; i < rawBoard.length - 1; i++) {
				line += rawBoard[i][j] + ", ";
			}
			line += rawBoard[rawBoard.length - 1][j] + "}";
			
			file.println(line);
		}
		
		file.close();
		
	}
	
	private PrintWriter createSaveFile(String fileName) {
		file = new File (
		"C:\\Users\\alexa\\Documents\\BattleSnakes" +
			"\\starter-snake-java\\src\\main\\java\\io\\battlesnake\\starter" +
			 "\\JsonNodeLog\\" + fileName + ".txt"
			);
		try {
			printWriter = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return printWriter;
	}
		
}