package org.mq.optculture.model.updatecontacts;

import javax.xml.bind.annotation.XmlElement;

public class User {

		private String userName;
		private String organizationId;
		private String token;
		private String sessionID;
		
		public User() {
		}
		
		public User(String userName, String organizationId, String token) {
			super();
			this.userName = userName;
			this.organizationId = organizationId;
			this.token = token;
		}


		public String getUserName() {
			return userName;
		}

		@XmlElement(name = "userName")
		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getOrganizationId() {
			return organizationId;
		}

		@XmlElement(name = "organizationId")
		public void setOrganizationId(String organizationId) {
			this.organizationId = organizationId;
		}

		public String getToken() {
			return token;
		}

		@XmlElement(name = "token")
		public void setToken(String token) {
			this.token = token;
		}

		public String getSessionID() {
			return sessionID;
		}

		public void setSessionID(String sessionID) {
			this.sessionID = sessionID;
		}

}
