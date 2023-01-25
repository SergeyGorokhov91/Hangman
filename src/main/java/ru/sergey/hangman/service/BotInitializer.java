package ru.sergey.hangman.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import ru.sergey.hangman.config.BotConfig;
import ru.sergey.hangman.repository.WordRepository;
import ru.sergey.hangman.service.cache.GamesList;
import ru.sergey.hangman.service.messages.MessageSender;
import ru.sergey.hangman.service.states.GameContext;

@Component
public class BotInitializer {
    @Autowired
    Hangman hangman;
       
    @Autowired
    GameContext currentContext;
    
    @Autowired
    BotConfig config;
    
    @Autowired
    GamesList gamesList;
    
    @Autowired
    MessageSender messageSender;
    	
    @EventListener({ContextRefreshedEvent.class})
    public void inti() throws TelegramApiException {
        try {
            TelegramBotsApi tba = new TelegramBotsApi(DefaultBotSession.class);
            tba.registerBot(new Hangman(currentContext, config, messageSender, gamesList));
        } catch (TelegramApiException e) {

        }
    }

}
