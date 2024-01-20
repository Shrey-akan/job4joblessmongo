package com.demo.oragejobsite.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.oragejobsite.dao.ApplyDao;
import com.demo.oragejobsite.entity.ApplyJob;

@CrossOrigin(origins = "https://job4jobless.com")
@RestController
public class ApplyController {
	@Autowired
	private ApplyDao apd;

	@CrossOrigin(origins = "https://job4jobless.com")
	@PostMapping("/insertapplyjob")
	public ResponseEntity<?> insertapplyjob(@RequestBody ApplyJob applyjob) {
	    try {
	        String randomString = UUID.randomUUID().toString();
	        randomString = randomString.replaceAll("-", "");
    	 applyjob.setJuid(randomString);
	        ApplyJob savedApplyJob = apd.save(applyjob);
	        return ResponseEntity.status(HttpStatus.CREATED).body(savedApplyJob);
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error occurred: " + e.getMessage());
	    } catch (Exception e) {
	        // Handle any other exceptions that may occur
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request: " + e.getMessage());
	    }
	}

	
	
	@CrossOrigin(origins = "https://job4jobless.com")
	@GetMapping("/fetchapplyform")
	public ResponseEntity<?> fetchapplyform() {
	    try {
	        List<ApplyJob> applyJobs = apd.findAll();
	        return ResponseEntity.ok(applyJobs);
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error occurred: " + e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request: " + e.getMessage());
	    }
	}
	
	
	
	@CrossOrigin(origins = "https://job4jobless.com")
	@PostMapping("/updateProfileUpdate")
	public ResponseEntity<?> updateProfileUpdate(@RequestBody ApplyJob applyJob) {
	    try {
	        ApplyJob existingApplyJob = apd.findByJuid(applyJob.getJuid());
	        if (existingApplyJob != null) {
	            existingApplyJob.setProfileupdate(applyJob.getProfileupdate());
	            ApplyJob updatedApplyJob = apd.save(existingApplyJob);
	            return ResponseEntity.ok(updatedApplyJob);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ApplyJob not found for UID: " + applyJob.getJuid());
	        }
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error occurred: " + e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request: " + e.getMessage());
	    }
	}
	
	
	@CrossOrigin(origins = "https://job4jobless.com")
	@GetMapping("/fetchapplyformbyjobid")
	public ResponseEntity<?> fetchApplyFormByJobId(
	        @RequestParam(name = "empid") String empid,
	        @RequestParam(name = "jobid") String jobid
	) {
	    try {
	        List<ApplyJob> applyJobs = apd.findByEmpidAndJobid(empid, jobid);
	        return ResponseEntity.ok(applyJobs);
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error occurred: " + e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request: " + e.getMessage());
	    }
	}


}