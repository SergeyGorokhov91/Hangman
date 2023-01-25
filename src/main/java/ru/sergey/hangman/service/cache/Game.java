package ru.sergey.hangman.service.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;

import ru.sergey.hangman.service.states.GameContext;
import ru.sergey.hangman.service.states.LetterGuessingState;
import ru.sergey.hangman.service.states.State;

public class Game {
	
	
	/**
	 * чат с игрой (место, куда сласть сообщеньки)
	 */
	long chatId;
	
	LetterGuessingState startState;
	
	/**
	 * Базовое состояние начатой игры
	 */
	private State state;
	
	/**
	 * Загаданное слово
	 */
    private String hiddenWord;
    
    /**
     * Загаданное слово как массив
     */
    private List<Character> wordAsArray = new ArrayList<>();
    
    /**
     * отгаданные буквы в нужном порядке
     */
    private List<Character> rightAnswers = new ArrayList<>();
    
    /**
     * пул ошибок
     */
    private Set<Character> guessPool = new ConcurrentSkipListSet<>();
    
    /**
     * количество ошибок (если больше 7, то игру надо заканчивать
     */
    private AtomicInteger mistakesCtr = new AtomicInteger();

    public Game(long chatId) {
    	this.chatId = chatId;
    	setState(new LetterGuessingState());
    }
    
    public void setState(LetterGuessingState startState) {
    	this.startState = startState;
    }

    public long getGameId() {
    	return this.chatId;
    }
    
    public void setGameState(State state) {
    	this.state = state;
    }
    
    public State getGameState() {
    	return this.state;
    }
     
    public void setHiddenWord(String hiddenWord) {
        this.hiddenWord=hiddenWord;
        setWordAsArray();
    }
    
    public String getHiddenWord() {
    	return this.hiddenWord;
    }
    
    public boolean hiddeWordIs(String guessedWord) {
        return this.hiddenWord.equals(guessedWord);

    }
    
    public void setWordAsArray() {
   	 	for (Character letter:hiddenWord.toCharArray()) {
        	wordAsArray.add(letter);
        	rightAnswers.add('_');
        }
    }
    
    public List<Character> getWordAsArray() {
        return wordAsArray;
    }
    
    public void setRightAnswer(String str) {
    	rightAnswers.clear();
        char[] arr = str.toCharArray();
        for (Character c: arr) {
        	rightAnswers.add(c);
        	
        }
    }
    
    public Set<Character> getGuessPool() {
        return guessPool;
    }
    
    
    
    public int incrementMistakesCtr() {
        return mistakesCtr.incrementAndGet();
    }
    
    public AtomicInteger getMistakesCtr() {
        return mistakesCtr;
    } 

    public boolean wordContainsLetter(Character letter) {
        if(wordAsArray.contains(letter)) {
            return true;
        }
        return false;
    }

    public void putLetterInGuessPool(Character letter) {
        guessPool.add(letter);
    }

   
    public void putLetterToRightAnswer(Character letter) {
        for (int i = 0; i < wordAsArray.size(); i++) {
            if(wordAsArray.get(i).equals(letter)) {
            	rightAnswers.set(i,letter);
            }
        }
    }

    public String getRightAnswerWithSpaces() {
        StringBuilder str = new StringBuilder();
        for (Character c: rightAnswers) {
            str.append(c);
            str.append(' ');
        }
        return str.toString().trim();
    }

    public String getGuessPoolWithSpaces() {
    	StringBuilder str = new StringBuilder();
        for (Character c: guessPool) {
        	if(!wordAsArray.contains(c)) {
	            str.append(c);
	            str.append(' ');
        	}
        }
        return str.toString().trim();
    }
    
    public boolean wordIsGuessed() {
        return wordAsArray.equals(rightAnswers);
    }
}
