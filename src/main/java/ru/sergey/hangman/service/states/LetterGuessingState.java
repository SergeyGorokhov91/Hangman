package ru.sergey.hangman.service.states;

public class LetterGuessingState implements State {
	
	private static final String END_GAME = "Игра удалена.";
    private static final String GAME_ALREADY_STARTED = "Игра уже начата.\nУдалите или продолжите существующую.";
    private static final String LETTER_ALREADY_IN_CACHE = "Вы уже пробовали эту букву.\nПопробуйте другую.";
	
	@Override
	public void startGame(GameContext context) {
		context.getSend().simpleMessage(GAME_ALREADY_STARTED);
	}

	@Override
	public void win(GameContext context) {
	}

	@Override
	public void lose(GameContext context) {
	}
	
	@Override
	public void deleteGame(GameContext context) {
		context.getSend().simpleMessage(END_GAME);
		context.getGamesList().removeGameWithId(context.getGame().getGameId());
        context.setState(new GameNotStartedState());
	}

	
	@Override
	public void rightLetter(Character letter,GameContext context) {
		context.getGame().putLetterInGuessPool(letter);
		context.getGame().putLetterToRightAnswer(letter);
	}

	@Override
	public void wrongLetter(Character letter,GameContext context) {
		context.getGame().putLetterInGuessPool(letter);
		context.getGame().incrementMistakesCtr();		
	}

	@Override
	public void alreadyGuessed(Character letter,GameContext context) {
		context.getSend().simpleMessage(LETTER_ALREADY_IN_CACHE);
	}

	@Override
	public void sendHangman(GameContext context) {
		// TODO Auto-generated method stub
		context.getSend().hangmanMessage();
	}
	

}
