package org.mq.captiway.scheduler.beans;

public class Authorities implements java.io.Serializable{


    private Long authorityId;
    private String authority;
    private String username;

   public Authorities() {
   }

   public Authorities(String authority, String username) {
      this.authority = authority;
      this.username = username;
   }
  
   public Long getAuthorityId() {
       return this.authorityId;
   }
   
   public void setAuthorityId(Long authorityId) {
       this.authorityId = authorityId;
   }
   public String getAuthority() {
       return this.authority;
   }
   
   public void setAuthority(String authority) {
       this.authority = authority;
   }
   public String getUsername() {
       return this.username;
   }
   
   public void setUsername(String username) {
       this.username = username;
   }




}
