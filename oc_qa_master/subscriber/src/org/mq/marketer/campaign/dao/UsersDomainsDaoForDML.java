package org.mq.marketer.campaign.dao;

import java.util.List;
import java.util.Set;

import org.mq.marketer.campaign.beans.Authorities;
import org.mq.marketer.campaign.beans.UserSMSSenderId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.springframework.jdbc.core.JdbcTemplate;

public class UsersDomainsDaoForDML extends AbstractSpringDaoForDML{

	public UsersDomainsDaoForDML() {	}

	/*public UsersDomains find(Long id){
        return (UsersDomains) super.find(UsersDomains.class, id);
    }*/

    private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public void saveOrUpdate(UsersDomains usersDomains){
        super.saveOrUpdate(usersDomains);
    }
    
    public void saveOrUpdate(Authorities authority){
        super.saveOrUpdate(authority);
    }
    
   /* public List<UsersDomains> FindByOrgId(Long orgId) {
    	
    	String qry = "FROM UsersDomains WHERE userOrganization="+orgId;
    	
    	return getHibernateTemplate().find(qry);
    	
    	
    }*/
	
 /*public UsersDomains findBydomainName(String DomainName, Long orgId) {
    	
    	//String qry = "FROM UsersDomains WHERE domainName='"+DomainName+"' AND userOrganization="+orgId;
    	

    	String qry = "FROM UsersDomains WHERE domainName='"+DomainName+"'  AND userOrganization="+orgId+" ESCAPE '\\' ";
    	
    	logger.info("qry===>"+qry);
    	UsersDomains usersDomains = null;
    	List userDomainList = getHibernateTemplate().find(qry);
    	
    	 if(userDomainList.size()>0){
    		 usersDomains = (UsersDomains)userDomainList.get(0);
		 }
    	 return usersDomains; 
      
    	
    	
    	
    	
    }
	
 
 */
    
    //public List<Long> getSharedLists(String domainIds, Long userID) {
    	/* public List<Long> getSharedLists(String domainIds) {
    	
    	//select distinct c from Contacts c join c.mlSet ml where ml.listId= " + listId	+ " order by c.emailId";
    	//String Query ="SELECT list_id FROM userdomain WHERE domain_id IN("+domainIds+")";
    	//select COUNT(ml.listId) from Contacts c JOIN c.mlSet ml WHERE m.listId=ml.listId
    	
    	String subQry = "";
    	if(userID != null) {
    		
    		subQry = " AND ml.userId="+userID.longValue();
    	}
    	
    	String Query ="SELECT  ml.listId  FROM UsersDomains ud JOIN ud.mailingLists ml WHERE ud.domainId IN("+domainIds+")";//+subQry;
    	return executeQuery(Query);
    	
    	
    }
    
   // public List<Long> getSharedSegments(String domainIds, Long userID) {
	public List<Long> getSharedSegments(String domainIds) {
    	
    	//SELECT  ml.listId  FROM UsersDomains ud JOIN ud.mailingLists ml WHERE ud.domainId IN("+domainIds+")";
    	String subQry = "";
    	if(userID != null) {
    		
    		subQry = " AND ml.userId="+userID.longValue();
    	}
    	
    	String Query ="SELECT  s.segRuleId  FROM UsersDomains ud JOIN ud.segments s WHERE ud.domainId IN("+domainIds+")";//+subQry;
    	return executeQuery(Query);
    	
    	
    	
    }
    
    
 public int findBydomainName(String DomainName, Long orgId) {
 	
 	//String qry = "FROM UsersDomains WHERE domainName='"+DomainName+"' AND userOrganization="+orgId;
 	

 	String qry = "select count(domain_id) FROM userdomain WHERE domain_name='"+DomainName+"'  AND user_organization="+orgId;
 	
 	logger.info("qry===>"+qry);
 	 int count = jdbcTemplate.queryForInt(qry);
 	 
 	 logger.info("count===>:"+count);
 	 return count; 
   
 	
 	
 	
 	
 }
 
 
 public List<UsersDomains> findByIds(Set IdsSet) {
	 String IdsStr = "";
	 
	 for (Object object : IdsSet) {
		if(object instanceof Long) {
			
			if(IdsStr.length() > 0) IdsStr += ",";
			IdsStr += ((Long)object).longValue()+"";
			
			
		}else if(object instanceof UsersDomains) {
			
			if(IdsStr.length() > 0) IdsStr += ",";
			IdsStr += ((UsersDomains)object).getDomainId().longValue()+"";
			
			
		}
		 
	}
	 
	 if(IdsStr.isEmpty()) return null;
	 
	 String qry = "FROM UsersDomains WHERE domainId IN("+IdsStr+")";
	 
	 List<UsersDomains> domainList = getHibernateTemplate().find(qry);
	 if(domainList != null && domainList.size() > 0) return domainList;
	 else return null;
	 
	 
 }
 
 */
	
}
