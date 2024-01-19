package com.demo.oragejobsite.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.demo.oragejobsite.entity.ResumeUpload;

public interface ResumeUploadRepository extends MongoRepository<ResumeUpload, String> {
    // Add custom queries or methods if needed
	   ResumeUpload findByUid(String uid);
}

