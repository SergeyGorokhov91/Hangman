package ru.sergey.hangman.service.states;

import org.springframework.stereotype.Component;

import ru.sergey.hangman.repository.WordRepository;
import ru.sergey.hangman.service.Hangman;
import ru.sergey.hangman.service.cache.Game;
import ru.sergey.hangman.service.cache.GamesList;
import ru.sergey.hangman.service.messages.MessageSender;

@Component
public class GameContext {
	
	Hangman executor;
	
	private State state;
	
	private Game game;
	
	private WordRepository wordRepo;
	
	private GamesList gamesList;
	
	private MessageSender send;
	
	public GameContext(WordRepository wordRepo, GamesList gamesList) {
		this.setWordRepo(wordRepo);
		this.setGamesList(gamesList);
	}
	
	public void startGame() {
		this.state.startGame(this);
	}
	
	public void win() {
		this.state.win(this);
	}
	
	public void lose() {
		this.state.lose(this);
	}
	
	public void deleteGame() {
		this.state.deleteGame(this);
	}
	
	public void rightLetter(Character letter) {
		this.state.rightLetter(letter,this);
	}
	
	public void wrongLetter(Character letter) {
		this.state.wrongLetter(letter,this);
	}
	
	public void alreadyGuessed(Character letter) {
		this.state.alreadyGuessed(letter,this);
	}
	
	public void sendHangman() {
		this.state.sendHangman(this);
	}
	
	public void setState(State state) {
		this.state = state;
	}
	
	public State getState() {
		return this.state;
	}
	
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	public Game getGame() {
		return this.game;
	}

	public WordRepository getWordRepo() {
		return wordRepo;
	}

	public void setWordRepo(WordRepository wordRepo) {
		this.wordRepo = wordRepo;
	}

	public GamesList getGamesList() {
		return gamesList;
	}

	public void setGamesList(GamesList gamesList) {
		this.gamesList = gamesList;
	}

	public Hangman getMessageExecutor() {
		return this.executor;
	}

	public void setMessageExecutor(Hangman executor) {
		this.executor = executor;
	}
	
	public void setSend(MessageSender send) {
		this.send = send;
		send.setMessageExecutor(executor);
		send.setContext(this);
	}
	
	public MessageSender getSend() {
		return send;
	}
	
	
}
