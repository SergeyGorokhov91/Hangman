package ru.sergey.hangman.service.states;

public interface State {
	void startGame(GameContext context);
	void win(GameContext context);
	void lose (GameContext context);
	void deleteGame(GameContext context);
	void rightLetter(Character letter,GameContext context);
	void wrongLetter(Character letter,GameContext context);
	void alreadyGuessed(Character letter,GameContext context);
	void sendHangman(GameContext context);

}
