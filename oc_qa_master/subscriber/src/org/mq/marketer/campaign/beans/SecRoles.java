package org.mq.marketer.campaign.beans;

import java.util.HashSet;
import java.util.Set;

public class SecRoles implements java.io.Serializable, Comparable<SecRoles> {
	
	private Long role_id;
    private String name;
    private String description;
    private String version;
    private String type;
    
    private Set<SecGroups> groupsSet = new HashSet<SecGroups>();
    
	public Long getRole_id() {
		return role_id;
	}

	public void setRole_id(Long role_id) {
		this.role_id = role_id;
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

	public Set<SecGroups> getGroupsSet() {
		return groupsSet;
	}

	public void setGroupsSet(Set<SecGroups> groupsSet) {
		this.groupsSet = groupsSet;
	}

	@Override
	public int compareTo(SecRoles role) {
		return role.getName().compareTo(this.name);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
