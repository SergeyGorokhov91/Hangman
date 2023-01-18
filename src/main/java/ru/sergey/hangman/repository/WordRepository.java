package ru.sergey.hangman.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.sergey.hangman.service.Word;

@Repository
public interface WordRepository extends CrudRepository<Word,Long> {
	
}
