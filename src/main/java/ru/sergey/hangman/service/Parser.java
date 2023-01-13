package ru.sergey.hangman.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class Parser {
	@Autowired
    WordRepository wordRepo;

    public Parser(WordRepository wordRepo) {
        this.wordRepo = wordRepo;
        this.textFileToDBSaveLogic();
    }

    public void textFileToDBSaveLogic() {
    	if(wordRepo.findById((long) 1).isEmpty()) {
	        try {
	            File file = new File("C:\\Users\\Сергей\\Desktop\\russian_nouns.txt");
	            FileReader fileReader = new FileReader(file);
	            BufferedReader reader = new BufferedReader(fileReader);
	            String line = reader.readLine();
	            while(line != null) {
	                Word word = new Word(line);
	                if(word.getWord().indexOf('-') == -1) {
	                	String newStr = word.getWord().replaceAll("ё", "е");
	                	word.setWord(newStr);
		                wordRepo.save(word);
	                }
	                line = reader.readLine();
	            }
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
    	}
    }
}
