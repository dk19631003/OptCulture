package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mq.marketer.campaign.beans.AutoProgram;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.SMSCampaignSent;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class AutoProgramDao extends AbstractSpringDao {

	
	public AutoProgramDao() {
		
	}
	
	/* public void saveOrUpdate(AutoProgram autoProgram) {
	        super.saveOrUpdate(autoProgram);
	    }*/
	
	
	public List<AutoProgram> getUserPrograms(Long userId) {
		
		return getHibernateTemplate().find("from AutoProgram where user="+userId+" ORDER BY modifiedDate DESC");
		
	}
	
	public boolean isProgramNameExists(String programName, Long userId) {
		
		List existList = getHibernateTemplate().find("from AutoProgram where programName='"+programName+"' and user ="+userId);
		boolean exists = (existList.size()>0) ? true : false;
		return exists;
		
		
	}
	
	/* public void delete(AutoProgram autoProgram) {
	        super.delete(autoProgram);
	    }*/
	
	
	
	
	public String getStatusById(Long programId, Long userId) {
		
		
		String qry = "SELECT status FROM AutoProgram WHERE programId="+programId+" AND user="+userId;
		
		List<String> retLst = getHibernateTemplate().find(qry);
		if(retLst.size()>0) {
			return (String)retLst.get(0);
		}
		else return null;
		
		
	}//getStatusById
	
	
	
}
