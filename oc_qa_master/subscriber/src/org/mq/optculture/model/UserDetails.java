package org.mq.optculture.model;

import javax.xml.bind.annotation.XmlElement;
public class UserDetails {

		private String userName;
		private String organizationId;
		private String token;
		
		public UserDetails() {
		}

		

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getOrganizationId() {
			return organizationId;
		}

		public void setOrganizationId(String organizationId) {
			this.organizationId = organizationId;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}
		// Added for new Fields will be removed in future.


		private String USERNAME;
		private String ORGID;
		private String TOKEN;
		private String username;
		
		
		public UserDetails(String uSERNAME, String oRGID, String tOKEN) {
			USERNAME = uSERNAME;
			ORGID = oRGID;
			TOKEN = tOKEN;
		}
		public String getUSERNAME() {
			return USERNAME;
		}
		@XmlElement(name = "USERNAME")
		public void setUSERNAME(String uSERNAME) {
			USERNAME = uSERNAME;
		}
		public String getORGID() {
			return ORGID;
		}
		@XmlElement(name = "ORGID")
		public void setORGID(String oRGID) {
			ORGID = oRGID;
		}
		public String getTOKEN() {
			return TOKEN;
		}
		@XmlElement(name = "TOKEN")
		public void setTOKEN(String tOKEN) {
			TOKEN = tOKEN;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		
		
		}
