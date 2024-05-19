package org.mq.marketer.campaign.dao;
 
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SecRoles;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class SecRolesDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
    public SecRolesDaoForDML(){}
/*
    public SecRoles find(Long id){
        return (SecRoles) super.find(SecRoles.class, id);
    }*/

    private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public void saveOrUpdate(SecRoles role){
        super.saveOrUpdate(role);
    }
    
    public void delete(Long id) {
    	String queryStr = "DELETE FROM SecRoles WHERE role_id="+id;
    	executeUpdate(queryStr);
    }
    /* 
    public List<SecRoles> findAll() {
        return super.findAll(SecRoles.class);
    }

    public List<SecRoles> findAllOrderByName() {
        return getHibernateTemplate().find("FROM SecRoles ORDER BY name");
    }

    public List<SecRoles> findByUserId(Long userId) {
    	
        String query = " SELECT DISTINCT r FROM SecRoles r WHERE r.role_id IN ( " +
        		" SELECT DISTINCT ur.role_id FROM Users u JOIN u.roles ur WHERE u.userId= " + userId.longValue() +" ) ";

        return getHibernateTemplate().find(query);
    }


    public List<SecRoles> findByGroupId(Long groupId) {
        String query = "SELECT DISTINCT r FROM SecRoles r JOIN r.groupsSet gs where gs.group_id= " + groupId;
        return getHibernateTemplate().find(query);
    }
    public SecRoles findByRolesId(long rolesId){
    	List<SecRoles> list= getHibernateTemplate().find("FROM SecRoles WHERE role_id="+rolesId);
    	if(list != null && list.size() > 0){
    		return list.get(0);
    	}
    	else return null;
    	
    }
    public SecRoles findBy(String RoleName) throws Exception{
    	 String query = "FROM SecRoles WHERE name='"+RoleName+"' ";
    	 List<SecRoles> list= getHibernateTemplate().find(query);
     	if(list != null && list.size() > 0){
     		return list.get(0);
     	}
     	else return null;
    	 
    }

    
    
    
   
    // select roles based on type
    public  List<SecRoles> findRoleByType(String types){
     	try {
 			String qry="FROM SecRoles WHERE type in("+types+")";
 			return getHibernateTemplate().find(qry);
 		} catch (DataAccessException e) {
 			// TODO Auto-generated catch block
 			logger.error("Exception ::" , e);
 			return null;
 		} 
     	
     }*/
    
}
