package ru.sergey.hangman.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ru.sergey.hangman.config.BotConfig;
import ru.sergey.hangman.repository.WordRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@Component
public class Hangman extends TelegramLongPollingBot {

    private static final String GAME_ALREADY_STARTED = "Игра уже начата";
    private static final String LETTER_ALREADY_IN_CACHE = "Вы уже пробовали эту букву.\nПопробуйте другую.";
    private static final String END_GAME_WIN = "Игра окончена.\nВы выиграли.";
    private static final String END_GAME_LOSE = "Игра окончена.\nВы проиграли.";
    private static final String HANGMAN_0 = " _______|\n        |\n        |\n        |\n        |\n _______|\n|_______|";
    private static final String HANGMAN_1 = " _______|\n    |   |\n        |\n        |\n        |\n _______|\n|_______|";
    private static final String HANGMAN_2 = " _______|\n    |   |\n    0   |\n        |\n        |\n _______|\n|_______|";
    private static final String HANGMAN_3 = " _______|\n    |   |\n    0   |\n    |   |\n        |\n _______|\n|_______|";
    private static final String HANGMAN_4 = " _______|\n    |   |\n    0   |\n   /|   |\n        |\n _______|\n|_______|";
    private static final String HANGMAN_5 = " _______|\n    |   |\n    0   |\n   /|\\  |\n        |\n _______|\n|_______|";
    private static final String HANGMAN_6 = " _______|\n    |   |\n    0   |\n   /|\\  |\n   /    |\n _______|\n|_______|";
    private static final String HANGMAN_7 = " _______|\n    |   |\n    0   |\n   /|\\  |\n   / \\  |\n _______|\n|_______|";
    private static final String ADVICE = "Чтобы предложить свою букву, просто отправьте её в чат";
    private static final String GAME_DELETED = "Игра удалена";
    private static final String WIN_EARLY = "Вау, вы угадали слово!";
    private static final Pattern RU_LETTER_PATTERN = Pattern.compile("[\\p{IsCyrillic}]");


    @Autowired
    private WordRepository wordRepo;
    
    @Autowired
    final BotConfig config;

    ConcurrentMap<Long, Game> game = new ConcurrentHashMap<>();//watch spring concurrency model

    public Hangman(WordRepository wordRepo,BotConfig config) {
    	this.wordRepo = wordRepo;
    	this.config = config;
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
            if(game.containsKey(chatId) && message.equals(game.get(chatId).getWord())){
                game.get(chatId).setGuessedWord(message);
                endGame(chatId,WIN_EARLY);
            }
            else if(game.containsKey(chatId) && RU_LETTER_PATTERN.matcher(message).matches()) {
                Character letter = message.toLowerCase().charAt(0);
                if(isLetterInCache(chatId,letter)) {
                    letterAlreadyInCacheMessage(chatId);
                } else if(isLetterInHiddenWord(chatId,letter)) {
                    addLetterToCache(chatId,letter);
                    setRightAnswer(chatId,letter);
                    if(game.get(chatId).wordIsGuessed()) {
                        endGame(chatId,END_GAME_WIN);
                    }else {
                    	sendHangman(chatId);
                    }

                } else {
                    addLetterToCache(chatId,letter);
                    int mistake = raiseMistakesCtr(chatId);
                    if(mistake == 7) {
                        endGame(chatId,END_GAME_LOSE);
                    } else {
                    	sendHangman(chatId);
                    }
                }
            } else if(message.equals("/delete") || message.equals("/delete@hangman_palachino_bot")) {
                game.remove(chatId);
                sendSimpleMessage(chatId,GAME_DELETED);
            } else if(message.equals("/start") || message.equals("/start@hangman_palachino_bot")) {
                if(!gameIsStarted(chatId)) {
                    putGameInCache(chatId);
                    putWordInCurrentGame(chatId);
                    startGame(chatId);
                }else {
                    gameAlreadyStartedMessage(chatId);
                }
            }
        }
    }

    private void startGame(long chatId) {
        sendSimpleMessage(chatId,ADVICE);
        sendHangman(chatId);
    }

    private void endGame(long chatId,String endGame) {
        sendHangman(chatId);
        sendSimpleMessage(chatId,endGame);
        game.remove(chatId);
    }

    private void sendHangman(long chatId) {
        SendMessage message = new SendMessage();
        String str = "<code>"+hangmanPic(game.get(chatId).getMistakesCtr())+"</code>\nСлово: "+getGuessedWord(chatId)+"\n\nОшибки: "+game.get(chatId).getGuessPoolAsString();
        message.setParseMode("HTML");
        message.setText(str);
        message.setChatId(String.valueOf(chatId));
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String getGuessedWord(long chatId) {
        return game.get(chatId).getGuessedWord();
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


    private int raiseMistakesCtr(long chatId) {
        return game.get(chatId).raiseMistakesCtr();

    }

    public void setRightAnswer(long chatId,Character letter) {
        game.get(chatId).setLetter(letter);
    }


    private void addLetterToCache(long chatId, Character letter) {
        game.get(chatId).putLetterInCache(chatId,letter);
    }

    private boolean isLetterInHiddenWord(long chatId,Character letter) {
        return game.get(chatId).wordContainsLetter(letter);
    }

    private void letterAlreadyInCacheMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setText(LETTER_ALREADY_IN_CACHE);
        message.setChatId(String.valueOf(chatId));
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private boolean isLetterInCache(long chatId,Character letter) {
        return game.get(chatId).getGuessPool().contains(letter);
    }

    private void gameAlreadyStartedMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setText(GAME_ALREADY_STARTED);
        message.setChatId(String.valueOf(chatId));
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendSimpleMessage(long chatId,String str) {
        SendMessage message = new SendMessage();
        message.setText(str);
        message.setChatId(String.valueOf(chatId));
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void putWordInCurrentGame(long chatId) {
    	Random random = new Random();
        String word = wordRepo.findById(random.nextLong(51350)+1).get().getWord();
        game.get(chatId).setWord(word);
    }

    private void putGameInCache(long chatId) {
        game.put(chatId,new Game());
    }

    private boolean gameIsStarted(long chatId) {
        return game.containsKey(chatId);
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


}
