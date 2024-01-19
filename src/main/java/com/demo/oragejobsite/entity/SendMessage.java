package com.demo.oragejobsite.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



@Document(collection = "sendmessage")
public class SendMessage {
    @Id
    private String messageId;

    private String messageTo;
    private String messageFrom;
    private String message;
    private String isSender;
    private Date sendTime;
    
	public SendMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SendMessage(String messageId, String messageTo, String messageFrom, String message, String isSender,
			Date sendTime) {
		super();
		this.messageId = messageId;
		this.messageTo = messageTo;
		this.messageFrom = messageFrom;
		this.message = message;
		this.isSender = isSender;
		this.sendTime = sendTime;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getMessageTo() {
		return messageTo;
	}

	public void setMessageTo(String messageTo) {
		this.messageTo = messageTo;
	}

	public String getMessageFrom() {
		return messageFrom;
	}

	public void setMessageFrom(String messageFrom) {
		this.messageFrom = messageFrom;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getIsSender() {
		return isSender;
	}

	public void setIsSender(String isSender) {
		this.isSender = isSender;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	

    
   
}
