package com.demo.oragejobsite.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "refreshtoken")
public class RefreshToken {
    @Id
    private String id;
    private String tokenId;
    private String username;
    private Date expiryDate;
    
    
    
	public RefreshToken() {
		super();
		// TODO Auto-generated constructor stub
	}
	public RefreshToken(String id, String tokenId, String username, Date expiryDate) {
		super();
		this.id = id;
		this.tokenId = tokenId;
		this.username = username;
		this.expiryDate = expiryDate;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTokenId() {
		return tokenId;
	}
	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

    // Constructors, getters, setters
   
}

