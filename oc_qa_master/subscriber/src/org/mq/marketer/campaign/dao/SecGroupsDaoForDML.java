package org.mq.marketer.campaign.dao;
 
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SecGroups;
import org.mq.marketer.campaign.beans.SecRoles;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class SecGroupsDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
    public SecGroupsDaoForDML(){}

    /*public SecGroups find(Long id){
        return (SecGroups) super.find(SecGroups.class, id);
    }*/

    private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public void saveOrUpdate(SecGroups role){
        super.saveOrUpdate(role);
    }
    
    public void delete(Long id) {
    	String queryStr = "DELETE FROM SecGroups WHERE group_id="+id;
    	executeUpdate(queryStr);
    }
    
    /*public List<SecGroups> findAll() {
        return super.findAll(SecGroups.class);
    }

    public List<SecGroups> findAllOrderByName() {
        return getHibernateTemplate().find("FROM SecGroups ORDER BY name");
    }

   public List<SecGroups> findByRoleId(Long roleId) {
    	
        String query = "SELECT DISTINCT g FROM SecGroups g WHERE g.group_id IN ( " +
        		"SELECT DISTINCT gs.group_id FROM SecRoles r JOIN r.groupsSet gs WHERE r.role_id= " + roleId.longValue() +" ) ";

        return getHibernateTemplate().find(query);
    }
   
   public SecGroups findByGroupId(long groupId){
   	List<SecGroups> list= getHibernateTemplate().find("FROM SecGroups WHERE group_id="+groupId);
   	if(list != null && list.size() > 0){
   		return list.get(0);
   	}
   	else return null;
   	
   }*/

}
