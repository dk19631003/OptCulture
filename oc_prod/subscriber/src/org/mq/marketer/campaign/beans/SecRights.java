package org.mq.marketer.campaign.beans;

public class SecRights implements java.io.Serializable {
	
	private Long right_id;
	private String type;
    private String name;
    private String description;
    private String version;
    
	public Long getRight_id() {
		return right_id;
	}
	public void setRight_id(Long right_id) {
		this.right_id = right_id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	
    
   
}
