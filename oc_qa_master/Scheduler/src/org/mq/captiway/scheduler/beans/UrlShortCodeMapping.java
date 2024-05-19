package org.mq.captiway.scheduler.beans;


public class UrlShortCodeMapping implements java.io.Serializable {
	
	// Fields    

    private long shortCodeId;
    private String shortCode;
    private String urlContent;
    private String urlType;
    private Long userId;


   // Constructors

   /** default constructor */
   public UrlShortCodeMapping() {
   }

	
   /** full constructor */
   public UrlShortCodeMapping( String shortCode, String urlContent, String urlType, Long userId) {
      // this.shortCodeId = shortCodeId;
       this.shortCode = shortCode;
       this.urlContent = urlContent;
       this.urlType = urlType;
       this.userId = userId;
   }
   

  
   // Property accessors

   public long getShortCodeId() {
       return this.shortCodeId;
   }
   
   public void setShortCodeId(long shortCodeId) {
       this.shortCodeId = shortCodeId;
   }

   public String getShortCode() {
       return this.shortCode;
   }
   
   public void setShortCode(String shortCode) {
       this.shortCode = shortCode;
   }

   public String getUrlContent() {
       return this.urlContent;
   }
   
   public void setUrlContent(String urlContent) {
       this.urlContent = urlContent;
   }

   public String getUrlType() {
       return this.urlType;
   }
   
   public void setUrlType(String urlType) {
       this.urlType = urlType;
   }

   public Long getUserId() {
       return this.userId;
   }
   
   public void setUserId(Long userId) {
       this.userId = userId;
   }
  

}

