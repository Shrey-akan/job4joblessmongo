package com.demo.oragejobsite.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.demo.oragejobsite.entity.PostJob;


@Repository
public interface PostjobDao extends MongoRepository<PostJob, String>{
	
}

