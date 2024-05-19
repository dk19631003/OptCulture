package org.mq.marketer.campaign.beans;

import java.util.HashSet;
import java.util.Set;

public class SecGroups implements java.io.Serializable{
	
	private Long group_id;
    private String name;
    private String description;
    private String version;
    private String type;
    
    private Set<SecRights> rightsSet  = new HashSet<SecRights>();
    
	public Long getGroup_id() {
		return group_id;
	}
	
	public void setGroup_id(Long group_id) {
		this.group_id = group_id;
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

	public Set<SecRights> getRightsSet() {
		return rightsSet;
	}

	public void setRightsSet(Set<SecRights> rightsSet) {
		this.rightsSet = rightsSet;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
   
}
