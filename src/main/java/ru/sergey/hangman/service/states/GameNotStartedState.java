package ru.sergey.hangman.service.states;

import java.util.Random;

public class GameNotStartedState implements State {
	
    private static final String ADVICE = "Чтобы предложить свою букву, просто отправьте её в чат";
    private static final String GAME_NOT_STARTED = "Игра не начата. Удалять нечего.";
	
	@Override
	public void startGame(GameContext context) {
		String word = getWordFromDatabase(context);
        putWordInCurrentGame(context,word);
        start(context);
        context.setState(new LetterGuessingState());
	}
	
	@Override
	public void win(GameContext context) {
	}

	@Override
	public void lose(GameContext context) {
	}

	@Override
	public void deleteGame(GameContext context) {
		context.getSend().simpleMessage(GAME_NOT_STARTED);
	}
	
    @Override
	public void rightLetter(Character letter,GameContext context) {
	}

	@Override
	public void wrongLetter(Character letter,GameContext context) {
	}

	@Override
	public void alreadyGuessed(Character letter, GameContext context) {
	}
	
	@Override
	public void sendHangman(GameContext context) {
		context.getSend().hangmanMessage();
	}
	
	/**
	 * Private methods
	 */
	
	private String getWordFromDatabase(GameContext context) {
        return context.getWordRepo().findById(new Random().nextLong(51350)+1).get().getWord();
    }
    private void putWordInCurrentGame(GameContext context,String word) {
    	context.getGame().setHiddenWord(word);    
    }
    private void start(GameContext context) {
    	context.getSend().simpleMessage(ADVICE);
    	context.getSend().hangmanMessage();
    }

	

	

	
    
    
    

}
