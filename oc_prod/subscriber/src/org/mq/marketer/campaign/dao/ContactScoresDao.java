package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.ContactScores;
import org.springframework.jdbc.core.JdbcTemplate;
import org.mq.marketer.campaign.beans.ContactScores;

public class ContactScoresDao extends AbstractSpringDao {
	
	public ContactScoresDao(){}
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	 public ContactScores find(Long id) {
	        return (ContactScores) super.find(ContactScores.class, id);
    }

  /*  public void saveOrUpdate(ContactScores contactScores) {
        super.saveOrUpdate(contactScores);
    }

    public void delete(ContactScores contactScores) {
        super.delete(contactScores);
    }*/
	
    public ContactScores getContactScoreByEmailId(Long userId, String emailId) {
    	logger.debug("user id is :"+userId + " email id :"+ emailId);
    	List<ContactScores> list = getHibernateTemplate().find("FROM ContactScores WHERE user="+ userId + " and emailId='" + emailId + "'" );
    	if(list == null || list.size() <1) {
    		return null;
    	}
    	return list.get(0);
    }
   
  /* public Object getTotalScoreOfEmailId(String emailId,Long userId){
	   ContactScores contactScores;
	   return contactScores=(ContactScores)getHibernateTemplate().find("FROM ContactScores WHERE user="+ userId + " and emailId='" + emailId + "'" );
	   
   }*/
	
}
