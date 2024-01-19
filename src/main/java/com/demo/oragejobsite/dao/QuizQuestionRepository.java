package com.demo.oragejobsite.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.demo.oragejobsite.entity.QuizQuestion;

@Repository
public interface QuizQuestionRepository extends MongoRepository<QuizQuestion, String>{

}	

