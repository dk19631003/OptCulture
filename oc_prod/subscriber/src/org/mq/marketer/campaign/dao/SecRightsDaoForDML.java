package org.mq.marketer.campaign.dao;
 
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SecGroups;
import org.mq.marketer.campaign.beans.SecRights;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class SecRightsDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
    public SecRightsDaoForDML(){}

   /* public SecRights find(Long id){
        return (SecRights) super.find(SecRights.class, id);
    }*/

    private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public void saveOrUpdate(SecRights role){
        super.saveOrUpdate(role);
    }
    
    public void delete(Long id) {
    	String queryStr = "DELETE FROM SecRights WHERE right_id="+id;
    	executeUpdate(queryStr);
    }
    
    /*public List<SecRights> findAll() {
        return super.findAll(SecRights.class);
    }

    public List<SecRights> findAllOrderByName() {
        return getHibernateTemplate().find("FROM SecRights ORDER BY name");
    }

   public List<SecRights> findByGroupId(Long groupId) {
    	
        String query = "SELECT DISTINCT rt FROM SecRights rt WHERE rt.right_id IN ( " +
        		"SELECT DISTINCT rs.right_id FROM SecGroups g JOIN g.rightsSet rs WHERE g.group_id= " + groupId.longValue() +" ) ";

        return getHibernateTemplate().find(query);
    }*/


}
