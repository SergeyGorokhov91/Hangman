package ru.sergey.hangman.service.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

@Component
public class GamesList {
    ConcurrentMap<Long, Game> gameList = new ConcurrentHashMap<>();
    
    public Game getGame(long chatId) {
    	return gameList.get(chatId);
    }
    
   
    public Game createGame(long chatId) {
    	return gameList.put(chatId, new Game(chatId));
    }
    
    
    public boolean containsGameWithId(long chatId) {
    	return gameList.containsKey(chatId);
    }
    
    public void removeGameWithId(long chatId) {
    	gameList.remove(chatId);
    }

}
