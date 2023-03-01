package ru.sergey.hangman.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

public class Game {
    private List<Character> wordItself = new ArrayList<>();
    private List<Character> wordGuesses = new ArrayList<>();
    private Set<Character> guessPool = new ConcurrentSkipListSet<>();
    private AtomicInteger mistakesCtr = new AtomicInteger();
    private String word;

    public Game() {}

    public void setWord(String word) {
        this.word=word;
        for (Character letter:word.toCharArray()) {
            wordItself.add(letter);
            wordGuesses.add('_');
        }
    }

    public Set<Character> getGuessPool() {
        return guessPool;
    }

    public List<Character> getWordItself() {
        return wordItself;
    }

    public String getWord() {
        return word;
    }

    public boolean wordContainsLetter(Character letter) {
        if(wordItself.contains(letter)) {
            return true;
        }
        return false;
    }

    public void putLetterInCache(long chatId, Character letter) {
        guessPool.add(letter);
    }

    public int raiseMistakesCtr() {
        return mistakesCtr.incrementAndGet();
    }

    public AtomicInteger getMistakesCtr() {
        return mistakesCtr;
    }

    public void setLetter(Character letter) {
        for (int i = 0; i < wordItself.size(); i++) {
            if(wordItself.get(i).equals(letter)) {
                wordGuesses.set(i,letter);
            }
        }
    }

    public String getGuessedWord() {
        StringBuilder str = new StringBuilder();
        for (Character c: wordGuesses) {
            str.append(c);
            str.append(' ');
        }
        return str.toString().trim();
    }

    public String getGuessPoolAsString() {
    	StringBuilder str = new StringBuilder();
        for (Character c: guessPool) {
        	if(!wordItself.contains(c)) {
	            str.append(c);
	            str.append(' ');
        	}
        }
        return str.toString().trim();
    }
    
    public void setGuessedWord(String str) {
        wordGuesses.clear();
        char[] arr = str.toCharArray();
        for (Character c: arr) {
        	wordGuesses.add(c);
        	
        }
    }



    public boolean wordIsGuessed() {
        return wordItself.equals(wordGuesses);
    }
}
