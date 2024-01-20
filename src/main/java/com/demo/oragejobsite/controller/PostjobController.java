package com.demo.oragejobsite.controller;

import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.demo.oragejobsite.dao.PostjobDao;
import com.demo.oragejobsite.entity.PostJob;

@CrossOrigin(origins = "https://job4jobless.com")
@RestController
public class PostjobController {
	@Autowired
	private PostjobDao pjd;
	
	
	@CrossOrigin(origins = "https://job4jobless.com")
	@PostMapping("/jobpostinsert")
	public ResponseEntity<String> jobpostinsert(@RequestBody PostJob pj) {
	    try {
	        PostJob savedPostJob = pjd.save(pj);
	        return ResponseEntity.status(HttpStatus.CREATED).body("Job post saved successfully");
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error occurred: " + e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request: " + e.getMessage());
	    }
	}

	
	@CrossOrigin(origins = "https://job4jobless.com")
	@GetMapping("/fetchjobpost")
	public ResponseEntity<List<PostJob>> fetchjobpost() {
	    try {
	        List<PostJob> jobPosts = pjd.findAll();
	        return ResponseEntity.ok(jobPosts);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}
	
	@CrossOrigin(origins = "https://job4jobless.com")
	@GetMapping("/fetchJobPostById/{jobId}")
	public ResponseEntity<PostJob> fetchJobPostById(@PathVariable String jobId) {
	    try {
	        Optional<PostJob> jobPost = pjd.findById(jobId);
	        if (jobPost.isPresent()) {
	            return ResponseEntity.ok(jobPost.get());
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}

	 
	@CrossOrigin(origins = "https://job4jobless.com")
    @PutMapping("/jobpostupdate/{jobid}")
    public ResponseEntity<Object> jobpostupdate(@PathVariable String jobid, @RequestBody PostJob updatedJob) {
        try {
            Optional<PostJob> existingJob = pjd.findById(jobid);
            if (existingJob.isPresent()) {
                PostJob currentJob = existingJob.get();
                currentJob.setJobtitle(updatedJob.getJobtitle());
                currentJob.setCompanyforthisjob(updatedJob.getCompanyforthisjob());
                currentJob.setNumberofopening(updatedJob.getNumberofopening());
                currentJob.setLocationjob(updatedJob.getLocationjob());
                currentJob.setJobtype(updatedJob.getJobtype());
                currentJob.setSchedulejob(updatedJob.getSchedulejob());
                currentJob.setPayjob(updatedJob.getPayjob());
                currentJob.setPayjobsup(updatedJob.getPayjobsup());
                currentJob.setDescriptiondata(updatedJob.getDescriptiondata());
                pjd.save(currentJob);
                return ResponseEntity.status(HttpStatus.OK).body(currentJob);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error occurred: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request: " + e.getMessage());
        }
    }
	
	
	
	@CrossOrigin(origins = "https://job4jobless.com", methods = { RequestMethod.PUT })
	@PutMapping("/updateJobStatus/{jobid}")
	public ResponseEntity<Object> updateJobStatus(@PathVariable String jobid, @RequestBody PostJob updatedJob) {
	    try {
	        Optional<PostJob> existingJob = pjd.findById(jobid);
	        if (existingJob.isPresent()) {
	            PostJob currentJob = existingJob.get();
	            currentJob.setUid(updatedJob.getUid());
	            currentJob.setStatus(true);
	            pjd.save(currentJob);
	            return ResponseEntity.status(HttpStatus.OK).body(currentJob);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
	        }
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error occurred: " + e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request: " + e.getMessage());
	    }
	}

}

