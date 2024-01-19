package com.demo.oragejobsite.dao;



import org.springframework.data.mongodb.repository.MongoRepository;

import com.demo.oragejobsite.entity.SendMessage;

public interface MessageEntityRepository extends MongoRepository<SendMessage, String> {

    // You can add custom queries here if needed

}
