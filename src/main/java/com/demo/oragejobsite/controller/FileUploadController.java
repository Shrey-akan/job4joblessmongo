package com.demo.oragejobsite.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.demo.oragejobsite.dao.ResumeUploadRepository;
import com.demo.oragejobsite.entity.ResumeUpload;

import antlr.collections.List;

@RestController
@CrossOrigin(origins = "https://job4jobless.com")
public class FileUploadController {

    @Autowired
    private ResumeUploadRepository resumeUploadRepository; // Create a repository for the ResumeUpload entity

    @PostMapping("/uploadPdf")
    @CrossOrigin(origins = "https://job4jobless.com")
    public ResponseEntity<?> uploadPdf(@RequestParam("file") MultipartFile file, @RequestParam("uid") String uid) {
        if (!file.isEmpty()) {
            try {
                String uploadDirectory = "/root/folder_name/upload_pdf/";               
                String originalFileName = uid + ".pdf";
                File directory = new File(uploadDirectory);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                String filePath = uploadDirectory + originalFileName;

             
                file.transferTo(new File(filePath));

            
                ResumeUpload resumeUpload = new ResumeUpload();
                resumeUpload.setUid(uid);
                resumeUpload.setFileName(originalFileName);
              
                resumeUploadRepository.save(resumeUpload);

//                return "File uploaded successfully with UID: " + uid;
                return ResponseEntity.ok(resumeUpload);
            } catch (IOException e) {
//                return "File upload failed: " + e.getMessage();
            	  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error occurred: " + e.getMessage());
            }
        }
        else {
//            return "File is empty."
        	  return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resume not found for the user : ");
        }
    }

    
    @GetMapping("/fetchByUid")
    @CrossOrigin(origins = "https://job4jobless.com")
    public ResponseEntity<ResumeUpload> fetchByUid(@RequestParam("uid") String uid) {
        try {
            ResumeUpload resumeUpload = resumeUploadRepository.findByUid(uid);

            if (resumeUpload != null) {
                return ResponseEntity.ok(resumeUpload);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/fetchAll")
    @CrossOrigin(origins = "https://job4jobless.com")
    public ResponseEntity<java.util.List<ResumeUpload>> fetchAll() {
        try {
            java.util.List<ResumeUpload> resumeUploads = resumeUploadRepository.findAll();

            if (!resumeUploads.isEmpty()) {
                return ResponseEntity.ok(resumeUploads);
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getPdfByUid/{uid}")
    @CrossOrigin(origins = "https://job4jobless.com")
    public void getPdfByUid(@PathVariable("uid") String uid, HttpServletResponse response) {
        try {
            // Retrieve the ResumeUpload object by UID
            ResumeUpload resumeUpload = resumeUploadRepository.findByUid(uid);

            if (resumeUpload != null) {
                String filePath = "/root/folder_name/upload_pdf/" + uid + ".pdf";

                // Set the content type and headers for the response
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "inline; filename=" + uid + ".pdf");

                // Read the PDF file and write it to the response output stream
                FileInputStream fileInputStream = new FileInputStream(filePath);
                IOUtils.copy(fileInputStream, response.getOutputStream());
                response.flushBuffer();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle errors as needed
        }
    }
    @GetMapping("/getPdfByUi/{uid}")
    public ResponseEntity<byte[]> getPdfByUi(@PathVariable("uid") String uid) {
        try {
            // Construct the file path
            String filePath = "/root/folder_name/upload_pdf/" + uid + ".pdf";

            // Read the PDF file as bytes
            File pdfFile = new File(filePath);
            byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());

            // Create response headers
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=" + uid + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle errors as needed
            return ResponseEntity.notFound().build();
        }
    }
    
}
