package io.battlesnake.starter.GameData;

public class CustomGameState {
	Game game;
	int turn;
	Board board;
	PsudoSnake you;
	
	public CustomGameState(Game game, int turn, Board board, PsudoSnake you) {
		this.game = game;
		this.turn = turn;
		this.board = board;
		this.you = you;
	}
}