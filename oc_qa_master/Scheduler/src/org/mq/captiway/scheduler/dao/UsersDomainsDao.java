package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.mq.captiway.scheduler.beans.Authorities;
import org.mq.captiway.scheduler.beans.UsersDomains;
import org.springframework.jdbc.core.JdbcTemplate;

public class UsersDomainsDao extends AbstractSpringDao{



	public UsersDomainsDao() {	}

	public UsersDomains find(Long id){
        return (UsersDomains) super.find(UsersDomains.class, id);
    }

    private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

  /*  public void saveOrUpdate(UsersDomains usersDomains){
        super.saveOrUpdate(usersDomains);
    }
    
    public void saveOrUpdate(Authorities authority){
        super.saveOrUpdate(authority);
    }*/
    
    public List<UsersDomains> FindByOrgId(Long orgId) {
    	
    	String qry = "FROM UsersDomains WHERE userOrganization="+orgId;
    	
    	return getHibernateTemplate().find(qry);
    	
    	
    }
	
 public UsersDomains findBydomainName(String DomainName, Long orgId) {
    	
    	String qry = "FROM UsersDomains WHERE domainName='"+DomainName+"' AND userOrganization="+orgId;
    	UsersDomains usersDomains = null;
    	List userDomainList = getHibernateTemplate().find(qry);
    	
    	 if(userDomainList.size()>0){
    		 usersDomains = (UsersDomains)userDomainList.get(0);
		 }
    	 return usersDomains; 
      
    	
    	
    	
    	
    }
	
	

	
	
	
	
}
