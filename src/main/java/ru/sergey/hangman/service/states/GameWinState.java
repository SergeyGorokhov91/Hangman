package ru.sergey.hangman.service.states;

public class GameWinState implements State {
	
	private static final String END_GAME_WIN = "Игра окончена.\nВы выиграли.";
	
	
	@Override
	public void startGame(GameContext context) {
		
	}

	@Override
	public void win(GameContext context) {
		
	}

	@Override
	public void lose(GameContext context) {
		context.getSend().simpleMessage(END_GAME_WIN);
		context.getGamesList().removeGameWithId(context.getGame().getGameId());
		context.setState(new GameNotStartedState());
	}

	@Override
	public void deleteGame(GameContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rightLetter(Character letter,GameContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wrongLetter(Character letter,GameContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void alreadyGuessed(Character letter,GameContext context) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void sendHangman(GameContext context) {
		// TODO Auto-generated method stub
		context.getSend().hangmanMessage();
	}

}
