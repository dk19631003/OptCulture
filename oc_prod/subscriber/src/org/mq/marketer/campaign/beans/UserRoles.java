package org.mq.marketer.campaign.beans;
// Generated 19 Jun, 2009 12:37:56 PM by Hibernate Tools 3.2.0.CR1


import java.util.HashSet;
import java.util.Set;

/**
 * UserRoles generated by hbm2java
 */
@SuppressWarnings({"serial","unchecked"})
public class UserRoles  implements java.io.Serializable {


     private Integer roleId;
     private String roleName;
     private Set users = new HashSet(0);

    public UserRoles() {
    }

    public UserRoles(String roleName, Set users) {
       this.roleName = roleName;
       this.users = users;
    }
   
    public Integer getRoleId() {
        return this.roleId;
    }
    
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
    public String getRoleName() {
        return this.roleName;
    }
    
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    public Set getUsers() {
        return this.users;
    }
    
    public void setUsers(Set users) {
        this.users = users;
    }

}


