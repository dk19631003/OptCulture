package org.mq.marketer.campaign.beans;
import java.util.Calendar;

public class DRSMSSent implements java.io.Serializable{

		private long id;
		private long userId;
		private Long contactId;
		private String mobile;
		private String status;
		private Calendar sentDate;
		private int opens;
	    private int clicks;
	    private String message;
	    private Long drJsonObjId;
	    private String docSid;
	    private int sentCount;
	    private String storeNumber;
	    private String sbsNumber;
	    private String messageId;
	    private String dlrStatus;
	    
        private boolean sentOnWA;//sent over WA
	    
	    public boolean isSentOnWA() {
			return sentOnWA;
		}

		public void setSentOnWA(boolean sentOnWA) {
			this.sentOnWA = sentOnWA;
		}
		
			    
	    public String getMessageId() {
			return messageId;
		}

		public void setMessageId(String messageId) {
			this.messageId = messageId;
		}

		public String getDlrStatus() {
			return dlrStatus;
		}

		public void setDlrStatus(String dlrStatus) {
			this.dlrStatus = dlrStatus;
		}
	    
	    public String getSbsNumber() {
			return sbsNumber;
		}

		public void setSbsNumber(String sbsNumber) {
			this.sbsNumber = sbsNumber;
		}

	    private Long zoneId;
	    private String htmlStr;
	    private String originalShortCode;
	    private String shortUrl;
	    private String originalUrl;
	    private String generatedShortCode;
	    private long uniqueOpens;
	    private long uniqueClicks;
	    
	    public Long getZoneId() {
			return zoneId;
		}

		public void setZoneId(Long zoneId) {
			this.zoneId = zoneId;
		}


		public Long getDrJsonObjId() {
			return drJsonObjId;
		}

		public void setDrJsonObjId(Long drJsonObjId) {
			this.drJsonObjId = drJsonObjId;
		}

		public DRSMSSent(){
	    	
	    }
	    
	    public DRSMSSent(String status,String mobile,Calendar sentDate,long userId,Long drJsonObjId) {
	        this.status = status;
	        this.mobile = mobile;
	        this.sentDate = sentDate;
	        this.userId = userId;
	        this.drJsonObjId= drJsonObjId;
	    }
	    
	    
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		public long getUserId() {
			return userId;
		}
		public void setUserId(long userId) {
			this.userId = userId;
		}
		public int getClicks() {
			return clicks;
		}
		public void setClicks(int clicks) {
			this.clicks = clicks;
		}
		public int getOpens() {
			return opens;
		}

		public void setOpens(int opens) {
			this.opens = opens;
		}

		public Long getContactId() {
			return contactId;
		}
		public void setContactId(Long contactId) {
			this.contactId = contactId;
		}
		
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public Calendar getSentDate() {
			return sentDate;
		}
		public void setSentDate(Calendar sentDate) {
			this.sentDate = sentDate;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getDocSid() {
			return docSid;
		}

		public void setDocSid(String docSid) {
			this.docSid = docSid;
		}

		public int getSentCount() {
			return sentCount;
		}

		public void setSentCount(int sentCount) {
			this.sentCount = sentCount;
		}

		public String getStoreNumber() {
			return storeNumber;
		}

		public void setStoreNumber(String storeNumber) {
			this.storeNumber = storeNumber;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getHtmlStr() {
			return htmlStr;
		}

		public void setHtmlStr(String htmlStr) {
			this.htmlStr = htmlStr;
		}

		public String getOriginalShortCode() {
			return originalShortCode;
		}

		public void setOriginalShortCode(String originalShortCode) {
			this.originalShortCode = originalShortCode;
		}


		public String getShortUrl() {
			return shortUrl;
		}

		public void setShortUrl(String shortUrl) {
			this.shortUrl = shortUrl;
		}

		public String getOriginalUrl() {
			return originalUrl;
		}

		public void setOriginalUrl(String originalUrl) {
			this.originalUrl = originalUrl;
		}

		public String getGeneratedShortCode() {
			return generatedShortCode;
		}

		public void setGeneratedShortCode(String generatedShortCode) {
			this.generatedShortCode = generatedShortCode;
		}

		public long getUniqueOpens() {
			return uniqueOpens;
		}

		public void setUniqueOpens(long uniqueOpens) {
			this.uniqueOpens = uniqueOpens;
		}

		public long getUniqueClicks() {
			return uniqueClicks;
		}

		public void setUniqueClicks(long uniqueClicks) {
			this.uniqueClicks = uniqueClicks;
		}

		

	}

