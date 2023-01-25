package ru.sergey.hangman.service.messages;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ru.sergey.hangman.service.Hangman;
import ru.sergey.hangman.service.states.GameContext;

@Component
public class MessageSender{
	
	private static final String HANGMAN_0 = " _______|\n        |\n        |\n        |\n        |\n _______|\n|_______|";
    private static final String HANGMAN_1 = " _______|\n    |   |\n        |\n        |\n        |\n _______|\n|_______|";
    private static final String HANGMAN_2 = " _______|\n    |   |\n    0   |\n        |\n        |\n _______|\n|_______|";
    private static final String HANGMAN_3 = " _______|\n    |   |\n    0   |\n    |   |\n        |\n _______|\n|_______|";
    private static final String HANGMAN_4 = " _______|\n    |   |\n    0   |\n   /|   |\n        |\n _______|\n|_______|";
    private static final String HANGMAN_5 = " _______|\n    |   |\n    0   |\n   /|\\  |\n        |\n _______|\n|_______|";
    private static final String HANGMAN_6 = " _______|\n    |   |\n    0   |\n   /|\\  |\n   /    |\n _______|\n|_______|";
    private static final String HANGMAN_7 = " _______|\n    |   |\n    0   |\n   /|\\  |\n   / \\  |\n _______|\n|_______|";
        
    Hangman executor;
    
    GameContext context;
    
    public void setMessageExecutor(Hangman executor) {
    	this.executor = executor;
    }
    
    public void setContext(GameContext context) {
    	this.context = context;
    }
    
	public void simpleMessage(String text) {
		SendMessage message = new SendMessage();
		message.setText(text);
		message.setChatId(String.valueOf(context.getGame().getMistakesCtr()));
		try {
			executor.execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
	
	
	public void hangmanMessage() {
		SendMessage message = new SendMessage();
        String str = "<code>"+hangmanPic(context.getGame().getMistakesCtr())+
        		"</code>\nСлово: "+context.getGame().getRightAnswerWithSpaces()+"\n\nОшибки: "+
        		context.getGame().getGuessPoolWithSpaces();
        message.setParseMode("HTML");
        message.setText(str);
        message.setChatId(String.valueOf(context.getGame().getGameId()));
		try {
			executor.execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
	
	
	private String hangmanPic(AtomicInteger mistakesCtr) {
        String result = "";
        switch (mistakesCtr.get()) {
            case 0 -> result = HANGMAN_0;
            case 1 -> result = HANGMAN_1;
            case 2 -> result = HANGMAN_2;
            case 3 -> result = HANGMAN_3;
            case 4 -> result = HANGMAN_4;
            case 5 -> result = HANGMAN_5;
            case 6 -> result = HANGMAN_6;
            case 7 -> result = HANGMAN_7;
        }
        return result;
    }
	

}
