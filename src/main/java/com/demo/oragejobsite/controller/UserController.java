package com.demo.oragejobsite.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

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
import io.jsonwebtoken.security.Keys;
import com.demo.oragejobsite.dao.RefreshTokenRepository;
import com.demo.oragejobsite.dao.UserDao;
import com.demo.oragejobsite.entity.Employer;
import com.demo.oragejobsite.entity.RefreshToken;
import com.demo.oragejobsite.entity.User;
import com.demo.oragejobsite.util.JwtTokenUtil;
import com.demo.oragejobsite.util.TokenProvider;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;

@CrossOrigin(origins = "https://job4jobless.com")
@RestController
public class UserController {

@Autowired
private UserDao ud;
	@Autowired
    private JwtTokenUtil jwtTokenUtil;

	private final byte[] refreshTokenSecret = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	private final TokenProvider tokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	@Autowired
	public UserController(TokenProvider tokenProvider, RefreshTokenRepository refreshTokenRepository) {
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


@CrossOrigin(origins = "https://job4jobless.com")
@PostMapping("/insertusermail")
public ResponseEntity<Object> insertusermail(@RequestBody User c1) {
   try {
            String pass=c1.getUserPassword();
            pass=hashPassword(pass);
            c1.setUserPassword(pass);
            c1.setVerified(false);
            User existingUser = ud.findByUserName(c1.getUserName());
            if (existingUser != null) {
            	return ResponseEntity.status(HttpStatus.CONFLICT).body("User with this username already exists");
            } else {
            	ud.save(c1);
            	System.out.println("User Created Successfully");
            	return ResponseEntity.status(HttpStatus.CREATED).body(c1);
       }
   } catch (DataAccessException e) {
       e.printStackTrace();
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error occurred");
   } catch (Exception e) {
       e.printStackTrace();
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request");
   }
}





@CrossOrigin(origins = "https://job4jobless.com")
@GetMapping("/fetchuser")
public ResponseEntity<List<User>> fetchuser() {
   try {
       List<User> users = ud.findAll();
       if (users.isEmpty()) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
       } else {
           return ResponseEntity.ok(users);
       }
   } catch (Exception e) {
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
   }
}


@CrossOrigin(origins = "https://job4jobless.com")
@GetMapping("/fetchuserById/{uid}")
public ResponseEntity<User> fetchUserById(@PathVariable String uid) {
    try {
        Optional<User> userOptional = ud.findById(uid);

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(null);
        }
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(null);
    }
}


@CrossOrigin(origins = "https://job4jobless.com")
    @PostMapping("/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody User updatedUser) {
        try {
        String uid = updatedUser.getUid();
             System.out.println("Received UID: " + uid);
             Optional<User> existingUserOptional = ud.findById(uid);
             System.out.println("Existing User Optional: " + existingUserOptional);
            if (existingUserOptional.isPresent()) {
                User existingUser = existingUserOptional.get();
                if (updatedUser.getProfile() != null) {
                    existingUser.setProfile(updatedUser.getProfile());
                }
                if (updatedUser.getUserName() != null) {
                    existingUser.setUserName(updatedUser.getUserName());
                }
                if (updatedUser.getUserFirstName() != null) {
                    existingUser.setUserFirstName(updatedUser.getUserFirstName());
                }
                if (updatedUser.getUserLastName() != null) {
                    existingUser.setUserLastName(updatedUser.getUserLastName());
                }
                if (updatedUser.getCompanyuser() != null) {
                    existingUser.setCompanyuser(updatedUser.getCompanyuser());
                }
                if (updatedUser.getUserphone() != null) {
                    existingUser.setUserphone(updatedUser.getUserphone());
                }
                if (updatedUser.getUsercountry() != null) {
                    existingUser.setUsercountry(updatedUser.getUsercountry());
                }
                if (updatedUser.getUserstate() != null) {
                    existingUser.setUserstate(updatedUser.getUserstate());
                }
                if (updatedUser.getUsercity() != null) {
                    existingUser.setUsercity(updatedUser.getUsercity());
                }
                if (updatedUser.getWebsiteuser() != null) {
                    existingUser.setWebsiteuser(updatedUser.getWebsiteuser());
                }
                if (updatedUser.isVerified() != false) {
                    existingUser.setVerified(updatedUser.isVerified());
                }
                User updatedRecord = ud.save(existingUser);
                System.out.println("Updated Record: " + updatedRecord.toString());

                return ResponseEntity.ok(updatedRecord);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with UID " + updatedUser.getUid() + " not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request: " + e.getMessage());
        }
    }






@CrossOrigin(origins = "https://job4jobless.com")
@PostMapping("/logincheck")
public ResponseEntity<?> logincheck(@RequestBody User c12, HttpServletResponse response) {
    try {
        String checkemail = c12.getUserName();
        String checkpass = c12.getUserPassword();
        checkpass = hashPassword(checkpass);

        User checkmail = checkMailUser(checkemail, checkpass);

        if (checkmail != null) {
            Cookie userCookie = new Cookie("user", checkemail);
            userCookie.setMaxAge(3600);
            userCookie.setPath("/");
            response.addCookie(userCookie);
         String refreshToken = tokenProvider.generateRefreshToken(checkemail, checkmail.getUid());
            RefreshToken refreshTokenEntity = new RefreshToken();
            refreshTokenEntity.setTokenId(refreshToken);
            refreshTokenEntity.setUsername(checkmail.getUid());
            refreshTokenEntity.setExpiryDate(tokenProvider.getExpirationDateFromRefreshToken(refreshToken));
            refreshTokenRepository.save(refreshTokenEntity);
            String accessToken = tokenProvider.generateAccessToken(checkmail.getUid());
            System.out.println(refreshToken);
            System.out.println(accessToken);
            System.out.println("checking the value of refresh token and access token");
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("accessToken", accessToken);
            responseBody.put("refreshToken", refreshToken);
            responseBody.put("uid", checkmail.getUid());
            
            return ResponseEntity.ok(responseBody);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request");
    }
}



@CrossOrigin(origins = "https://job4jobless.com")
@PostMapping("/logincheckgmail")
public ResponseEntity<?> logincheckgmail(@RequestBody User c12, HttpServletResponse response) {
   try {
       String checkemail = c12.getUserName();
       boolean emailExists = checkIfEmailExists(checkemail);

       if (emailExists) {

           Optional<User> userOptional = Optional.ofNullable(ud.findByUserName(checkemail));
           if (userOptional.isPresent()) {
               User user = userOptional.get();
               Cookie userCookie = new Cookie("user", checkemail);
               userCookie.setMaxAge(3600);
               userCookie.setPath("/");
               response.addCookie(userCookie);
               

               String refreshToken = tokenProvider.generateRefreshToken(checkemail, user.getUid());

               RefreshToken refreshTokenEntity = new RefreshToken();
               refreshTokenEntity.setTokenId(refreshToken);
               refreshTokenEntity.setUsername(user.getUid());

               refreshTokenEntity.setExpiryDate(tokenProvider.getExpirationDateFromRefreshToken(refreshToken));
               refreshTokenRepository.save(refreshTokenEntity);


               String accessToken = tokenProvider.generateAccessToken(user.getUid());


               Map<String, Object> responseBody = new HashMap<>();
               responseBody.put("accessToken", accessToken);
               responseBody.put("refreshToken", refreshToken);
               responseBody.put("uid", user.getUid());
               responseBody.put("userName", user.getUserName());
               responseBody.put("userFirstName", user.getUserFirstName());
               responseBody.put("userLastName", user.getUserLastName());
               responseBody.put("usercountry", user.getUsercountry());
               responseBody.put("usercity", user.getUsercity());
               responseBody.put("userstate", user.getUserstate());
               responseBody.put("websiteuser", user.getWebsiteuser());
               return ResponseEntity.ok(responseBody);
           } else {
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch UID");
           }
       } else {
           // Email doesn't exist, return an unauthorized response
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
       }
   } catch (Exception e) {
       e.printStackTrace();
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
   }
}


public boolean checkIfEmailExists(String email) {
   Optional<User> userOptional = Optional.ofNullable(ud.findByUserName(email));
   return userOptional.isPresent();
}


    private User checkMailUser(String checkemail, String checkpass) {
        // TODO Auto-generated method stub
        List<User> allMails = ud.findAll();
        for (User u1 : allMails) {
        System.out.println("Checking the password"+checkpass);
        if (u1.getUserName().equals(checkemail) && u1.getUserPassword().equals(checkpass) && u1.isVerified()) {
         System.out.println("Checking the password"+u1.getUserPassword());
                return u1;
            }
        }
        return null;
    }

   
    @CrossOrigin(origins = "https://job4jobless.com")
    @PostMapping("/verifyUser")
    public ResponseEntity<?> verifyUser(@RequestBody Map<String, String> request) {
        try {
            String userName = request.get("userName");
            User user = ud.findByUserName(userName);

            if (user != null) {

                user.setVerified(true);

                ud.save(user);
                Map<String, Object> response = new HashMap<>();
           response.put("status", "User verified successfully");
           response.put("employer", user);
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with userName " + userName + " not found.");
            }
        } catch (Exception e) {

            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request: " + e.getMessage());
        }
    }




    @CrossOrigin(origins = "https://job4jobless.com")
    @DeleteMapping("/deleteUser/{uid}")
    public ResponseEntity<Object> deleteUserByUid(@PathVariable String uid) {
        try {
            Optional<User> existingUserOptional = ud.findById(uid);

            if (existingUserOptional.isPresent()) {
                ud.delete(existingUserOptional.get());

                return ResponseEntity.status(HttpStatus.OK).body(true);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with UID " + uid + " not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request: " + e.getMessage());
        }
    }
    @CrossOrigin(origins = "https://job4jobless.com")
    @PostMapping("/resetPassword")
    public ResponseEntity<Boolean> resetPassword(@RequestBody Map<String, String> request) {

        try {
            String userName = request.get("userName");
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");
            User user = ud.findByUserName(userName);
            if (user != null) {
                String hashedOldPassword = hashPassword(oldPassword);
                if (hashedOldPassword.equals(user.getUserPassword())) {
                    String hashedNewPassword = hashPassword(newPassword);
                    user.setUserPassword(hashedNewPassword);
                    ud.save(user);
                    return ResponseEntity.ok(true);
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
   
    @CrossOrigin(origins = "https://job4jobless.com")
    @PostMapping("/resetPasswordUser")
    public ResponseEntity<Boolean> resetPasswordUser(@RequestBody Map<String, String> request) {
        try {
            String userName = request.get("userName");
            String newPassword = request.get("newPassword");
            User user = ud.findByUserName(userName);
            if (user != null && user.isVerified()) {
                String hashedNewPassword = hashPassword(newPassword);
                user.setUserPassword(hashedNewPassword);
                ud.save(user);
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

   
    @CrossOrigin(origins = "https://job4jobless.com")
    @GetMapping("/checkuser")
    public ResponseEntity<Object> checkUser(@RequestParam String userName) {
        try {
            User user = ud.findByUserName(userName);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"message\": \"User with userName " + userName + " does not exist.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"message\": \"An error occurred while processing your request.\"}");
        }
    }


    @CrossOrigin(origins = "https://job4jobless.com")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        try {
            Cookie empCookie = new Cookie("uid", null);
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
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during logout");
        }
    }

    

    @CrossOrigin(origins = "https://job4jobless.com")
    @PostMapping("/createOrGetUser")
    public ResponseEntity<Map<String, Object>> createOrGetUser(@RequestBody Map<String, String> requestBody, HttpServletResponse response) {
        try {
            String userName = requestBody.get("userName");
            String fullName = requestBody.get("userFirstName");
            String[] nameParts = fullName.split("\\s+", 2);
            String userFirstName = nameParts.length > 0 ? nameParts[0] : "";
            String userLastName = nameParts.length > 1 ? nameParts[1] : "";
            userFirstName = userFirstName.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
            userLastName = userLastName.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
            userName = userName.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
            User existingUser = ud.findByUserName(userName);

            if (existingUser != null) {
                String accessToken = tokenProvider.generateAccessToken(existingUser.getUid());
                String refreshToken = tokenProvider.generateRefreshToken(userName, existingUser.getUid());
                RefreshToken refreshTokenEntity = new RefreshToken();
                refreshTokenEntity.setTokenId(refreshToken);
                refreshTokenEntity.setUsername(existingUser.getUid());
                refreshTokenEntity.setExpiryDate(tokenProvider.getExpirationDateFromRefreshToken(refreshToken));
                refreshTokenRepository.save(refreshTokenEntity);

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("userName", userName);
                responseBody.put("userFirstName", existingUser.getUserFirstName()); // Include userFirstName from existing user
                responseBody.put("accessToken", accessToken);
                responseBody.put("refreshToken", refreshToken);
                responseBody.put("uid", existingUser.getUid());
                responseBody.put("userName", existingUser.getUserName());
                responseBody.put("userLastName", existingUser.getUserLastName());
                responseBody.put("usercountry", existingUser.getUsercountry());
                responseBody.put("usercity", existingUser.getUsercity());
                responseBody.put("userstate", existingUser.getUserstate());
                responseBody.put("websiteuser", existingUser.getWebsiteuser());
                Cookie userCookie = new Cookie("user", userName);
                userCookie.setMaxAge(3600);
                userCookie.setPath("/");
                response.addCookie(userCookie);

                return ResponseEntity.ok(responseBody);
            } else {
                User newUser = createUser(userName, userFirstName,userLastName, true); // Pass userFirstName to createUser method
                String refreshToken = tokenProvider.generateRefreshToken(userName, newUser.getUid());
                RefreshToken refreshTokenEntity = new RefreshToken();
                refreshTokenEntity.setTokenId(refreshToken);
                refreshTokenEntity.setUsername(newUser.getUid());
                refreshTokenEntity.setExpiryDate(tokenProvider.getExpirationDateFromRefreshToken(refreshToken));
                refreshTokenRepository.save(refreshTokenEntity);
                String accessToken = tokenProvider.generateAccessToken(newUser.getUid());
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("userName", userName);
                responseBody.put("userFirstName", newUser.getUserFirstName());
                responseBody.put("accessToken", accessToken);
                responseBody.put("refreshToken", refreshToken);
                responseBody.put("uid", newUser.getUid());
                responseBody.put("userName", newUser.getUserName());
                responseBody.put("userLastName", newUser.getUserLastName());
                responseBody.put("usercountry", newUser.getUsercountry());
                responseBody.put("usercity", newUser.getUsercity());
                responseBody.put("userstate", newUser.getUserstate());
                responseBody.put("websiteuser", newUser.getWebsiteuser());
                Cookie userCookie = new Cookie("user", userName);
                userCookie.setMaxAge(3600);
                userCookie.setPath("/");
                response.addCookie(userCookie);
                return ResponseEntity.ok(responseBody);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "User creation and login failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    public User createUser(String userName, String userFirstName,String userLastName, boolean verified) {
        User newUser = new User();
        newUser.setUserName(userName);
        newUser.setUserFirstName(userFirstName);
        newUser.setUserLastName(userLastName);
        newUser.setVerified(verified);
        System.out.println("Received userName: " + userName);
        System.out.println("Received userFirstName: " + userFirstName);
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replaceAll("-", "");
        newUser.setUid(uuid);
        User savedUser = ud.save(newUser);
        System.out.println("Saved user with userName: " + savedUser.getUserName() + ", userFirstName: " + savedUser.getUserFirstName());
        return savedUser;
    }


    


@CrossOrigin(origins = "https://job4jobless.com")
@PostMapping("/applogin")
public ResponseEntity<?> applogin(@RequestBody User c12, HttpServletResponse response) {
    try {
        String checkemail = c12.getUserName();
        String checkpass = c12.getUserPassword();
        checkpass = hashPassword(checkpass);

        User checkmail = checkMailUser(checkemail, checkpass);

        if (checkmail != null) {
  
            Cookie userCookie = new Cookie("user", checkemail);
            userCookie.setMaxAge(3600); 
            userCookie.setPath("/");
            response.addCookie(userCookie);

         String refreshToken = tokenProvider.generateRefreshToken(checkemail, checkmail.getUid());
            RefreshToken refreshTokenEntity = new RefreshToken();
            refreshTokenEntity.setTokenId(refreshToken);
            refreshTokenEntity.setUsername(checkmail.getUid());
            refreshTokenEntity.setExpiryDate(tokenProvider.getExpirationDateFromRefreshToken(refreshToken));
            refreshTokenRepository.save(refreshTokenEntity);
            String accessToken = tokenProvider.generateAccessToken(checkmail.getUid());
            System.out.println(refreshToken);
            System.out.println(accessToken);
            System.out.println("checking the value of refresh token and access token");
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("accessToken", accessToken);
            responseBody.put("refreshToken", refreshToken);
            responseBody.put("uid", checkmail.getUid());
            responseBody.put("userName", checkmail.getUserName());
            responseBody.put("userFirstName", checkmail.getUserFirstName());
            responseBody.put("userLastName", checkmail.getUserLastName());
            responseBody.put("usercountry", checkmail.getUsercountry());
            responseBody.put("usercity", checkmail.getUsercity());
            responseBody.put("userstate", checkmail.getUserstate());
            responseBody.put("websiteuser", checkmail.getWebsiteuser());
            return ResponseEntity.ok(responseBody);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request");
    }
}


}
