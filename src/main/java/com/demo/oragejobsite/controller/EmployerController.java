package com.demo.oragejobsite.controller;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.oragejobsite.dao.EmployerDao;
import com.demo.oragejobsite.dao.RefreshTokenRepository;
import com.demo.oragejobsite.entity.Employer;
import com.demo.oragejobsite.entity.RefreshToken;

import com.demo.oragejobsite.util.TokenProvider;



@CrossOrigin(origins = "https://job4jobless.com")
@RestController
public class EmployerController {
@Autowired
private EmployerDao ed;




	private static final Logger logger = LoggerFactory.getLogger(EmployerController.class);
	private final TokenProvider tokenProvider; // Inject your TokenProvider here
	private final RefreshTokenRepository refreshTokenRepository;
   
   @Autowired
   public EmployerController(TokenProvider tokenProvider, RefreshTokenRepository refreshTokenRepository) {
       this.tokenProvider = tokenProvider;
       this.refreshTokenRepository = refreshTokenRepository;
   }
   
   private static  String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] hashedPasswordBytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPasswordBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

// Insert Employer API
@CrossOrigin(origins = "https://job4jobless.com")
@PostMapping("/insertEmployer")
public ResponseEntity<Object> insertEmployer(@RequestBody Employer emp) {
   try {
	   		String pass=emp.getEmppass();
            pass=hashPassword(pass);
            emp.setEmppass(pass);
       Employer existingEmployer = ed.findByEmpmailid(emp.getEmpmailid());
       if (existingEmployer != null) {
           return ResponseEntity.status(HttpStatus.CONFLICT).body("Employer with this name already exists");
       } else {
           ed.save(emp);
           System.out.println("Employer Created Successfully");
           return ResponseEntity.status(HttpStatus.CREATED).body(emp);
       }
   } catch (DataAccessException e) {
       e.printStackTrace();
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error occurred");
   } catch (Exception e) {
       e.printStackTrace();
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request");
   }
}



// Fetch Employer API
@CrossOrigin(origins = "https://job4jobless.com")
@GetMapping("/fetchemployer")
public ResponseEntity<List<Employer>> fetchemployer() {
   try {
       List<Employer> users = ed.findAll();
       if (users.isEmpty()) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
       } else {
           return ResponseEntity.ok(users);
       }
   } catch (Exception e) {
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
   }
}

// Fetch Employer By empid
@CrossOrigin(origins = "https://job4jobless.com")
@GetMapping("/fetchempById/{empid}")
public ResponseEntity<Employer> fetchEmpById(@PathVariable String empid) {
    try {
        Optional<Employer> employerOptional = ed.findById(empid);
        if (employerOptional.isPresent()) {
            return ResponseEntity.ok(employerOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}


// Update Employer Details 
@CrossOrigin(origins = "https://job4jobless.com")
@PostMapping("/updateEmployee")
public ResponseEntity<?> updateEmployee(@RequestBody Employer updatedEmployer) {
   try {
    String empid = updatedEmployer.getEmpid();
             System.out.println("Received UID: " + empid);
       Optional<Employer> existingEmployerOptional = ed.findById(updatedEmployer.getEmpid());
       if (existingEmployerOptional.isPresent()) {
           Employer existingEmployer = existingEmployerOptional.get();
           if (updatedEmployer.getEmpfname() != null) {
               existingEmployer.setEmpfname(updatedEmployer.getEmpfname());
           }
           if (updatedEmployer.getEmplname() != null) {
               existingEmployer.setEmplname(updatedEmployer.getEmplname());
           }
           if (updatedEmployer.getEmpcompany() != null) {
               existingEmployer.setEmpcompany(updatedEmployer.getEmpcompany());
           }
           if (updatedEmployer.getEmpphone() != null) {
               existingEmployer.setEmpphone(updatedEmployer.getEmpphone());
           }
           if (updatedEmployer.getEmpcountry() != null) {
               existingEmployer.setEmpcountry(updatedEmployer.getEmpcountry());
           }
           if (updatedEmployer.getEmpstate() != null) {
               existingEmployer.setEmpstate(updatedEmployer.getEmpstate());
           }
           if (updatedEmployer.getEmpcity() != null) {
               existingEmployer.setEmpcity(updatedEmployer.getEmpcity());
           }
           if (updatedEmployer.getDescriptionemp() != null) {
               existingEmployer.setDescriptionemp(updatedEmployer.getDescriptionemp());
           }
                if (updatedEmployer.isVerifiedemp() != false) {
                existingEmployer.setVerifiedemp(updatedEmployer.isVerifiedemp());
                }
           Employer updatedRecord = ed.save(existingEmployer);
           System.out.println("Updated Record: " + updatedRecord.toString());
           return ResponseEntity.ok(updatedRecord);
       } else {
           Employer newEmployer = ed.save(updatedEmployer);
           return ResponseEntity.status(HttpStatus.CREATED).body(newEmployer);
       }
   } catch (Exception e) {
       e.printStackTrace();
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request: " + e.getMessage());
   }
}


// Employer Login Check Google Sign In
@CrossOrigin(origins = "https://job4jobless.com")
@PostMapping("/employerLoginCheck")
public ResponseEntity<?> employerLoginCheck(@RequestBody Employer employer, HttpServletResponse response) {
   try {
       String checkEmail = employer.getEmpmailid();
       boolean emailExists = checkIfEmailExists(checkEmail);
       if (emailExists) {
           Optional<Employer> employerOptional = Optional.ofNullable(ed.findByEmpmailid(checkEmail));
           if (employerOptional.isPresent()) {
               Employer foundEmployer = employerOptional.get();
               Cookie employerCookie = new Cookie("emp", checkEmail);
               employerCookie.setMaxAge(3600);
               employerCookie.setPath("/");
               response.addCookie(employerCookie);
               String accessToken = tokenProvider.generateAccessToken(foundEmployer.getEmpid());
               String refreshToken = tokenProvider.generateRefreshToken(checkEmail, foundEmployer.getEmpid());
               RefreshToken refreshTokenEntity = new RefreshToken();
               refreshTokenEntity.setTokenId(refreshToken);
               refreshTokenEntity.setUsername(foundEmployer.getEmpid());
               refreshTokenEntity.setExpiryDate(tokenProvider.getExpirationDateFromRefreshToken(refreshToken));
               refreshTokenRepository.save(refreshTokenEntity);
               Map<String, Object> responseBody = new HashMap<>();
               responseBody.put("accessToken", accessToken);
               responseBody.put("refreshToken", refreshToken);
               responseBody.put("empid", foundEmployer.getEmpid());
               responseBody.put("empfname", foundEmployer.getEmpfname());
               responseBody.put("emplname", foundEmployer.getEmplname());
               responseBody.put("empmailid", foundEmployer.getEmpmailid());
               responseBody.put("empcountry", foundEmployer.getEmpcountry());
               responseBody.put("empstate", foundEmployer.getEmpstate());
               responseBody.put("empcity", foundEmployer.getEmpcity());
               return ResponseEntity.ok(responseBody);
           } else {
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch employer data");
           }
       } else {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
       }
   } catch (Exception e) {
       e.printStackTrace();
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
   }
}


public boolean checkIfEmailExists(String email) {
   Employer existingEmployer = ed.findByEmpmailid(email);
   return existingEmployer != null;
}


// Employer Login
@CrossOrigin(origins = "https://job4jobless.com")
@PostMapping("/logincheckemp")
public ResponseEntity<?> logincheckemp(@RequestBody Employer e12, HttpServletResponse response) {
   try {
       String checkemail = e12.getEmpmailid();
       String checkpass = e12.getEmppass();
       checkpass = hashPassword(checkpass);
       System.out.println(checkemail + " " + checkpass);
       Employer checkmail = checkMailUser(checkemail, checkpass);
       if (checkmail != null) {
           Cookie employerCookie = new Cookie("emp", checkmail.toString());
           employerCookie.setMaxAge(3600);
           employerCookie.setPath("/");
           response.addCookie(employerCookie);
                String accessToken = tokenProvider.generateAccessToken(checkmail.getEmpid());
                String refreshToken = tokenProvider.generateRefreshToken(checkemail, checkmail.getEmpid());
                RefreshToken refreshTokenEntity = new RefreshToken();
                refreshTokenEntity.setTokenId(refreshToken);
                refreshTokenEntity.setUsername(checkmail.getEmpid());
                refreshTokenEntity.setExpiryDate(tokenProvider.getExpirationDateFromRefreshToken(refreshToken));
                refreshTokenRepository.save(refreshTokenEntity);
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("accessToken", accessToken);
                responseBody.put("refreshToken", refreshToken);
                responseBody.put("empid", checkmail.getEmpid());
                responseBody.put("empfname", checkmail.getEmpfname());
                responseBody.put("emplname", checkmail.getEmplname());
                responseBody.put("empmailid", checkmail.getEmpmailid());
                responseBody.put("empcountry", checkmail.getEmpcountry());
                responseBody.put("empstate", checkmail.getEmpstate());
                responseBody.put("empcity", checkmail.getEmpcity());
                return ResponseEntity.ok(responseBody);
       }
       return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
   } catch (Exception e) {
       e.printStackTrace();
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
   }
}


private Employer checkMailUser(String checkemail, String checkpass) {
   System.out.println("hello");
   List<Employer> allMails = ed.findAll();
   for (Employer u1 : allMails) {
       System.out.println(checkemail);
       System.out.println("Checking the password"+checkpass);
       if (u1.getEmpmailid() != null && u1.getEmpmailid().equals(checkemail) && u1.getEmppass() != null && u1.getEmppass().equals(checkpass) && u1.isVerifiedemp()) {
           System.out.println("inside");
           System.out.println("Checking the password"+u1.getEmppass());
           return u1;
       }
   }
   return null;
}

// Verify Employer
@CrossOrigin(origins = "https://job4jobless.com")
@PostMapping("/verifyEmployer")
public ResponseEntity<Object> verifyEmployer(@RequestBody Map<String, String> request) {
   try {
       String empmailid = request.get("empmailid");
       Employer employer = ed.findByEmpmailid(empmailid);
       if (employer != null) {
           employer.setVerifiedemp(true);
           ed.save(employer);
           Map<String, Object> response = new HashMap<>();
           response.put("status", "Employer verified successfully");
           response.put("employer", employer);
           return ResponseEntity.ok(response);
       } else {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employer with empmailid " + empmailid + " not found.");
       }
   } catch (Exception e) {
       // Handle any exceptions that may occur
       e.printStackTrace();
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request: " + e.getMessage());
   }
}


// Delete Employer By empid
@CrossOrigin(origins = "https://job4jobless.com")
@DeleteMapping("/deleteEmployer/{empid}")
public ResponseEntity<?> deleteEmployer(@PathVariable String empid) {
   try {
       Optional<Employer> existingEmployerOptional = ed.findById(empid);
       if (existingEmployerOptional.isPresent()) {
           ed.delete(existingEmployerOptional.get());
           return ResponseEntity.status(HttpStatus.OK).body(true);
       } else {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employer with empid " + empid + " not found.");
       }
   } catch (Exception e) {
       e.printStackTrace();
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request: " + e.getMessage());
   }
}


// Reset Password with old password
@CrossOrigin(origins = "https://job4jobless.com")
@PostMapping("/resetPasswordEmp")
public ResponseEntity<Boolean> resetPasswordEmp(@RequestBody Map<String, String> request) {
   try {
       String empmailid = request.get("empmailid");
       String oldPassword = request.get("oldPassword");
       String newPassword = request.get("newPassword");
       Employer employer = ed.findByEmpmailid(empmailid);
       if (employer != null) {
           if (employer.getEmppass().equals(hashPassword(oldPassword))) {
               String hashedPassword = hashPassword(newPassword);
               employer.setEmppass(hashedPassword);
               ed.save(employer);
               return ResponseEntity.status(HttpStatus.OK).body(true);
           } else {
               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
           }
       } else {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
       }
   } catch (Exception e) {
       e.printStackTrace();
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
   }
}


// create new password
@CrossOrigin(origins = "https://job4jobless.com")
@PostMapping("/resetPasswordEmpverify")
public ResponseEntity<Boolean> resetPasswordEmpverify(@RequestBody Map<String, String> request) {
   try {
       String empmailid = request.get("empmailid");
       String newPassword = request.get("newPassword");
       Employer employer = ed.findByEmpmailid(empmailid);
       if (employer != null && employer.isVerifiedemp()) {
           // Hash the new password
           String hashedPassword = hashPassword(newPassword);
           employer.setEmppass(hashedPassword);
           ed.save(employer);
           return ResponseEntity.status(HttpStatus.OK).body(true);
       } else {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
       }
   } catch (Exception e) {
       e.printStackTrace();
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
   }
}

	// google sign up api
	@CrossOrigin(origins = "https://job4jobless.com")
    @GetMapping("/checkEmployer")
    public ResponseEntity<Object> checkEmployer(@RequestParam String empmailid) {
        try {
            Employer employer = ed.findByEmpmailid(empmailid);
            if (employer != null) {
                return ResponseEntity.ok(employer);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"message\": \"User with userName " + empmailid + " does not exist.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"message\": \"An error occurred while processing your request.\"}");
        }
    }

	
	//logout employer
@CrossOrigin(origins = "https://job4jobless.com")
@PostMapping("/logoutEmployer")
public ResponseEntity<String> logoutEmployer(HttpServletResponse response) {
   Cookie empCookie = new Cookie("emp", null);
   empCookie.setMaxAge(0);
   empCookie.setPath("/");
   response.addCookie(empCookie);
   Cookie accessTokenCookie = new Cookie("accessToken", null);
   accessTokenCookie.setMaxAge(0);
   accessTokenCookie.setPath("/"); 
   response.addCookie(accessTokenCookie);
   Cookie refreshTokenCookie = new Cookie("refreshToken", null);
   refreshTokenCookie.setMaxAge(0);
   refreshTokenCookie.setPath("/");
   response.addCookie(refreshTokenCookie);
   return ResponseEntity.ok("Logout successful");
}

//sign up employerusing google
@CrossOrigin(origins = "https://job4jobless.com")
@PostMapping("/createOrGetEmployer")
public ResponseEntity<Map<String, Object>> createOrGetEmployer(@RequestBody Map<String, String> requestBody, HttpServletResponse response) {
    try {
        String empmailid = requestBody.get("empmailid");
        String empname = requestBody.get("empfname");
      String[] nameParts = empname.split("\\s+", 2);
      String empfname = nameParts.length > 0 ? nameParts[0] : "";
      String emplname = nameParts.length > 1 ? nameParts[1] : "";
      empfname = empfname.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
      emplname = emplname.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
        Employer existingEmployer = ed.findByEmpmailid(empmailid);
        if (existingEmployer != null) {
  Cookie employerCookie = new Cookie("emp", empmailid);
            employerCookie.setMaxAge(3600);
            employerCookie.setPath("/");
            response.addCookie(employerCookie);
            String accessToken = tokenProvider.generateAccessToken(existingEmployer.getEmpid());
            String refreshToken = tokenProvider.generateRefreshToken(empmailid, existingEmployer.getEmpid());
            RefreshToken refreshTokenEntity = new RefreshToken();
            refreshTokenEntity.setTokenId(refreshToken);
            refreshTokenEntity.setUsername(existingEmployer.getEmpid());
            refreshTokenEntity.setExpiryDate(tokenProvider.getExpirationDateFromRefreshToken(refreshToken));
            refreshTokenRepository.save(refreshTokenEntity);
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("empmailid", empmailid);
            responseBody.put("accessToken", accessToken);
            responseBody.put("refreshToken", refreshToken);
            responseBody.put("empid", existingEmployer.getEmpid());
            responseBody.put("empfname", existingEmployer.getEmpfname());
            responseBody.put("emplname", existingEmployer.getEmplname());
            responseBody.put("empmailid", existingEmployer.getEmpmailid());
            responseBody.put("empcountry", existingEmployer.getEmpcountry());
            responseBody.put("empstate", existingEmployer.getEmpstate());
            responseBody.put("empcity", existingEmployer.getEmpcity());
            return ResponseEntity.ok(responseBody);
        } else {
            Employer newEmployer = createEmployer(empmailid, empfname,emplname, true);
            Cookie employerCookie = new Cookie("emp", empmailid);
            employerCookie.setMaxAge(3600);
            employerCookie.setPath("/");
            response.addCookie(employerCookie);
            String accessToken = tokenProvider.generateAccessToken(newEmployer.getEmpid());
            String refreshToken = tokenProvider.generateRefreshToken(empmailid, newEmployer.getEmpid());
            RefreshToken refreshTokenEntity = new RefreshToken();
            refreshTokenEntity.setTokenId(refreshToken);
            refreshTokenEntity.setUsername(newEmployer.getEmpid());
            refreshTokenEntity.setExpiryDate(tokenProvider.getExpirationDateFromRefreshToken(refreshToken));
            refreshTokenRepository.save(refreshTokenEntity);
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("empmailid", empmailid);
            responseBody.put("accessToken", accessToken);
            responseBody.put("refreshToken", refreshToken);
            responseBody.put("empid", newEmployer.getEmpid());
            responseBody.put("empfname", newEmployer.getEmpfname());
            responseBody.put("emplname", newEmployer.getEmplname());
            responseBody.put("empmailid", newEmployer.getEmpmailid());
            responseBody.put("empcountry", newEmployer.getEmpcountry());
            responseBody.put("empstate", newEmployer.getEmpstate());
            responseBody.put("empcity", newEmployer.getEmpcity());
            return ResponseEntity.ok(responseBody);
        }
    } catch (Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Employer creation and login failed");
        errorResponse.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}


   public Employer createEmployer(String empmailid, String empfname ,String emplname , boolean verified) {
	   Employer newEmployer = new Employer();
	   newEmployer.setEmpmailid(empmailid);
	   newEmployer.setEmpfname(empfname);
	   newEmployer.setEmplname(emplname);
	   newEmployer.setVerifiedemp(verified);
	    System.out.println("Received userName: " + empmailid);
	    System.out.println("Received userFirstName: " + empfname);
	    String uuid = UUID.randomUUID().toString();
	    uuid = uuid.replaceAll("-", "");
	    newEmployer.setEmpid(uuid);
	    Employer savedEmployer = ed.save(newEmployer);
	    System.out.println("Saved user with userName: " + savedEmployer.getEmpmailid() + ", userFirstName: " + savedEmployer.getEmpfname());
	    return savedEmployer;
	}

   
   
// Mobile App Login API fo Employer
@CrossOrigin(origins = "https://job4jobless.com")
@PostMapping("/apploginemployer")
public ResponseEntity<?> apploginemployer(@RequestBody Employer e12, HttpServletResponse response) {
   try {
       String checkemail = e12.getEmpmailid();
       String checkpass = e12.getEmppass();
       checkpass = hashPassword(checkpass);
       System.out.println(checkemail + " " + checkpass);

       Employer checkmail = checkMailUser(checkemail, checkpass);
       if (checkmail != null) {
           Cookie employerCookie = new Cookie("emp", checkmail.toString());
           employerCookie.setMaxAge(3600);
           employerCookie.setPath("/");
           response.addCookie(employerCookie);
                String accessToken = tokenProvider.generateAccessToken(checkmail.getEmpid());
                String refreshToken = tokenProvider.generateRefreshToken(checkemail, checkmail.getEmpid());
                RefreshToken refreshTokenEntity = new RefreshToken();
                refreshTokenEntity.setTokenId(refreshToken);
                refreshTokenEntity.setUsername(checkmail.getEmpid()); 
                refreshTokenEntity.setExpiryDate(tokenProvider.getExpirationDateFromRefreshToken(refreshToken));
                refreshTokenRepository.save(refreshTokenEntity);
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("accessToken", accessToken);
                responseBody.put("refreshToken", refreshToken);
                responseBody.put("empid", checkmail.getEmpid());
                responseBody.put("empfname", checkmail.getEmpfname());
                responseBody.put("emplname", checkmail.getEmplname());
                responseBody.put("empmailid", checkmail.getEmpmailid());
                responseBody.put("empcountry", checkmail.getEmpcountry());
                responseBody.put("empstate", checkmail.getEmpstate());
                responseBody.put("empcity", checkmail.getEmpcity());
                return ResponseEntity.ok(responseBody);
       }
       return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
   } catch (Exception e) {
       e.printStackTrace();
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
   }
}


}
