package com.demo.oragejobsite.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notification")
public class Notification {
	@Id
	private String nid;
	private String nhead;
	private String nsubhead;
	private String ndescription;
	private String notisend;
	private String notifyuid;
	public Notification() {
		super();
		// TODO Auto-generated constructor stub
	}








	
	public Notification(String nid, String nhead, String nsubhead, String ndescription, String notisend,
			String notifyuid) {
		super();
		this.nid = nid;
		this.nhead = nhead;
		this.nsubhead = nsubhead;
		this.ndescription = ndescription;
		this.notisend = notisend;
		this.notifyuid = notifyuid;
	}









	public String getNid() {
		return nid;
	}






	public void setNid(String nid) {
		this.nid = nid;
	}






	public String getNhead() {
		return nhead;
	}
	public void setNhead(String nhead) {
		this.nhead = nhead;
	}
	public String getNsubhead() {
		return nsubhead;
	}
	public void setNsubhead(String nsubhead) {
		this.nsubhead = nsubhead;
	}
	public String getNdescription() {
		return ndescription;
	}
	public void setNdescription(String ndescription) {
		this.ndescription = ndescription;
	}

	public String getNotisend() {
		return notisend;
	}

	public void setNotisend(String notisend) {
		this.notisend = notisend;
	}









	public String getNotifyuid() {
		return notifyuid;
	}









	public void setNotifyuid(String notifyuid) {
		this.notifyuid = notifyuid;
	}


	
}

