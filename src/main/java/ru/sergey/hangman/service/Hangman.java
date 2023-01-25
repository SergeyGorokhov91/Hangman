package ru.sergey.hangman.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ru.sergey.hangman.config.BotConfig;
import ru.sergey.hangman.service.cache.GamesList;
import ru.sergey.hangman.service.messages.MessageSender;
import ru.sergey.hangman.service.states.GameContext;
import ru.sergey.hangman.service.states.GameNotStartedState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@Component
public class Hangman extends TelegramLongPollingBot {

    private static final Pattern RU_LETTER_PATTERN = Pattern.compile("[\\p{IsCyrillic}]");

    final BotConfig config;
            
    GameContext currentContext;
    
    MessageSender send;
    
    GamesList gamesList;

    public Hangman(GameContext currentContext,BotConfig config, MessageSender send, GamesList gamesList) {
    	this.currentContext = currentContext;
    	this.config = config;
    	this.send = send;
    	this.gamesList = gamesList;
        menuExecute();
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String message = update.getMessage().getText().toLowerCase();
        	currentContext.setMessageExecutor(this);
            
            if(gamesList.containsGameWithId(chatId)) {
            	currentContext.setState(gamesList.getGame(chatId).getGameState());
            	currentContext.setGame(gamesList.getGame(chatId));
            } else {
            	currentContext.setState(new GameNotStartedState());
            }
                       
            
            if((message.equals("/start") || message.equals("/start@hangman_palachino_bot"))) {
            	currentContext.setGame(gamesList.createGame(chatId));
            	currentContext.startGame();
            } else if(message.equals("/delete") || message.equals("/delete@hangman_palachino_bot")) {
            	currentContext.deleteGame();
            } else if(gamesList.getGame(chatId).hiddeWordIs(message)){
                currentContext.win();
            } else if(gamesList.containsGameWithId(chatId) && RU_LETTER_PATTERN.matcher(message).matches()) {
            	
                Character letter = message.toLowerCase().charAt(0);

	            if(gamesList.getGame(chatId).getGuessPool().contains(letter)) {
	            	currentContext.alreadyGuessed(letter);
	            } else if(gamesList.getGame(chatId).wordContainsLetter(letter)) {
	            	currentContext.rightLetter(letter);
	            	currentContext.sendHangman();
                    if(gamesList.getGame(chatId).wordIsGuessed()) {
                    	currentContext.win();
                    }else {
                    }
                } else if(!gamesList.getGame(chatId).wordContainsLetter(letter)) {
	            	currentContext.wrongLetter(letter);
	            	currentContext.sendHangman();
                    if(gamesList.getGame(chatId).getMistakesCtr().equals(7)) {
                    	currentContext.lose();
                    } else {
                    }
                }
            }
        }
    }
    
    
    

    private void menuExecute() {
        List<BotCommand> listOfCommands = new ArrayList<>();

        listOfCommands.add(new BotCommand("/start","начать игру"));
        listOfCommands.add(new BotCommand("/delete","удалить игру"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(),null));
        } catch(TelegramApiException e) {
            //log.error("Error setting bot`s command list: "+e);
        }
    }
    
    
    
	
	public MessageSender getSend() {
		return send;
	}
}
