package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

public class DigitalReceiptMyTemplate  implements java.io.Serializable  {
	
	  private Long myTemplateId;
	  private String name;
	  private String content;
	  private Calendar createdDate;
	  private Long userId; 
	  
	  public DigitalReceiptMyTemplate() {
		// TODO Auto-generated constructor stub
	  }
	  
	public DigitalReceiptMyTemplate(String name, String content, Calendar createdDate, Long userId) {
		
	       this.name = name;
	       this.content = content;
	       this.createdDate = createdDate;
	       this.userId = userId;
	}

	public Long getMyTemplateId() {
		return myTemplateId;
	}

	public void setMyTemplateId(Long myTemplateId) {
		this.myTemplateId = myTemplateId;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Calendar getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
  

}
