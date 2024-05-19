package org.mq.captiway.scheduler.beans;

import java.util.Calendar;
import java.util.Set;

import org.mq.captiway.scheduler.utility.Constants;


@SuppressWarnings({"unchecked","serial"})
public class CustomTemplates implements java.io.Serializable {
	
	 private Long templateId;
     private Users userId;
     private String templateName;
     private String htmlText;
     private String iframeLink;
     private String type;
     
     private String fromName;
     private String fromEmail;
     private String subject;
    
     
     
     
     private boolean webLinkFlag;
     private String webLinkText;
     private String webLinkUrlText;
     private boolean permissionRemainderFlag;
     private String permissionRemainderText;
     
     private String editorType;
     private Long myTemplateId;
     
     private String includeBeforeStr;

	 private boolean includeOrg;
     private boolean includeOrgUnit;
     private String replyToEmail;
     private boolean customizeFooter;
     private boolean downloadPdf;
     
     private Set<String> urls;
     
    
     public boolean isIncludeOrg() {
    	 return includeOrg;
     }
     
     public void setIncludeOrg(boolean includeOrg) {
    	 this.includeOrg = includeOrg;
     }
     
     public boolean isIncludeOrgUnit() {
    	 return includeOrgUnit;
     }
     
     public void setIncludeOrgUnit(boolean includeOrgUnit) {
    	 this.includeOrgUnit = includeOrgUnit;
     }

	private boolean addressFlag;
     
     private Address address;
     private String addressStr;
     
     
     private boolean personalizeTo;
     private String toName;
     
     
     public CustomTemplates(Users userId,String templateName,String htmlText, String type) {
    	 this.userId = userId;
    	 this.htmlText = htmlText;
    	 this.type = type;
    	 this.templateName = templateName;
     }
     
     public CustomTemplates() {
	 }
     
     public void setTemplateId(Long templateId) {
    	 this.templateId = templateId;
     }
     
     public Long getTemplateId() {
    	 return this.templateId;
     }
     
     public void setTemplateName(String templateName) {
    	 this.templateName = templateName;
     }
     
     public String getTemplateName() {
    	 return templateName;
     }
     
     public void setUserId(Users userId) {
    	 this.userId = userId;
     }
     
     public Users getUserId() {
    	 return this.userId;
     }
     
     public void setHtmlText(String htmlText) {
    	 this.htmlText = htmlText;
     }
     
     public String getHtmlText() {
    	 return this.htmlText;
     }
     
     public void setType(String type) {
    	 this.type = type;
     }
     
     public String getType() {
    	 return this.type;
     }

	public String getIframeLink() {
		return iframeLink;
	}

	public void setIframeLink(String iframeLink) {
		this.iframeLink = iframeLink;
	}

	
	
	
	
	
public Address getAddress() {
		
		if(address == null) {
			address = new Address();
			try {
				
				if(addressStr == null)	return address;
				
				String[] addrArr = addressStr.split(Constants.ADDR_COL_DELIMETER);
				
				address.setAddressOne((addrArr[0]== null)?"":addrArr[0]);
				address.setAddressTwo((addrArr[1]== null)?"":addrArr[1]);
				address.setCity((addrArr[2]== null)?"":addrArr[2]);
				address.setState((addrArr[3] == null)?"":addrArr[3]);
				address.setCountry((addrArr[4] == null)?"":addrArr[4]);
				
				if(addrArr.length > 5 && addrArr[5] != null) {
//					address.setPin(Long.parseLong(addrArr[5]));
					address.setPin(addrArr[5]);
				}
				
				if(addrArr.length > 6 ) {
					address.setPhone((addrArr[6] == null)?"":addrArr[6]);
				}
			} catch (NumberFormatException e) {
				
			} catch (ArrayIndexOutOfBoundsException iob) {
				
			} catch (Exception ex) {
				
			}
		}
		return address;
	}


	public void setAddress(Address address) {
		this.address = address;
		StringBuffer sb = new StringBuffer();
		
		sb.append(((address.getAddressOne() == null)?"": address.getAddressOne()));
		sb.append(Constants.ADDR_COL_DELIMETER+((address.getAddressTwo() == null) ?"":address.getAddressTwo()));
		sb.append(Constants.ADDR_COL_DELIMETER+((address.getCity() == null)?"":address.getCity()));
		sb.append(Constants.ADDR_COL_DELIMETER+((address.getState() == null)?"":address.getState()));
		sb.append(Constants.ADDR_COL_DELIMETER+((address.getCountry() == null)?"":address.getCountry()));
		sb.append(Constants.ADDR_COL_DELIMETER+((address.getPin() == null)?"":address.getPin()));
		sb.append(Constants.ADDR_COL_DELIMETER+((address.getPhone() == null)?"":address.getPhone()));
		this.addressStr = sb.toString();
	}


	public String getAddressStr() {
		return addressStr;
	}


	public void setAddressStr(String addressStr) {
		this.addressStr = addressStr;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public boolean isWebLinkFlag() {
		return webLinkFlag;
	}

	public void setWebLinkFlag(boolean webLinkFlag) {
		this.webLinkFlag = webLinkFlag;
	}

	public String getWebLinkText() {
		return webLinkText;
	}

	public void setWebLinkText(String webLinkText) {
		this.webLinkText = webLinkText;
	}

	public String getWebLinkUrlText() {
		return webLinkUrlText;
	}

	public void setWebLinkUrlText(String webLinkUrlText) {
		this.webLinkUrlText = webLinkUrlText;
	}

	public boolean isPermissionRemainderFlag() {
		return permissionRemainderFlag;
	}

	public void setPermissionRemainderFlag(boolean permissionRemainderFlag) {
		this.permissionRemainderFlag = permissionRemainderFlag;
	}

	public String getPermissionRemainderText() {
		return permissionRemainderText;
	}

	public void setPermissionRemainderText(String permissionRemainderText) {
		this.permissionRemainderText = permissionRemainderText;
	}

	public boolean isAddressFlag() {
		return addressFlag;
	}

	public void setAddressFlag(boolean addressFlag) {
		this.addressFlag = addressFlag;
	}

	public boolean isPersonalizeTo() {
		return personalizeTo;
	}

	public void setPersonalizeTo(boolean personalizeTo) {
		this.personalizeTo = personalizeTo;
	}

	public String getToName() {
		return toName;
	}

	public void setToName(String toName) {
		this.toName = toName;
	}

	public String getIncludeBeforeStr() {
		return includeBeforeStr;
	}

	public void setIncludeBeforeStr(String includeBeforeStr) {
		this.includeBeforeStr = includeBeforeStr;
	}

	public Set<String> getUrls() {
		return urls;
	}

	public void setUrls(Set<String> urls) {
		this.urls = urls;
	}

	public String getReplyToEmail() {
		return replyToEmail;
	}

	public void setReplyToEmail(String replyToEmail) {
		this.replyToEmail = replyToEmail;
	}

	public String getEditorType() {
		return editorType;
	}

	public void setEditorType(String editorType) {
		this.editorType = editorType;
	}

	public Long getMyTemplateId() {
		return myTemplateId;
	}

	public void setMyTemplateId(Long myTemplateId) {
		this.myTemplateId = myTemplateId;
	}

	public boolean isCustomizeFooter() {
		return customizeFooter;
	}

	public void setCustomizeFooter(boolean customizeFooter) {
		this.customizeFooter = customizeFooter;
	}

	public boolean isDownloadPdf() {
		return downloadPdf;
	}

	public void setDownloadPdf(boolean downloadPdf) {
		this.downloadPdf = downloadPdf;
	}
	
}
