package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.AutoSMS;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class AutoSMSDao extends AbstractSpringDao {
	  public AutoSMSDao() {}

		private JdbcTemplate jdbcTemplate;

		public JdbcTemplate getJdbcTemplate() {
			return jdbcTemplate;
		}

		public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}

	   /* public void saveOrUpdate(AutoSMS autoSms) {
	        super.saveOrUpdate(autoSms);
	    }

	    public void delete(AutoSMS autoSms) {
	        super.delete(autoSms);
	    }*/
	    
		public List<AutoSMS> getTemplatesByType(Long userId, String type) {
			
			String qry=" FROM AutoSMS WHERE userId= " +userId+ " AND autoSmsType= '"+type+"'" ;
			List<AutoSMS> list= getHibernateTemplate().find(qry);
			if(list != null && list.size() > 0){
				
				return list;
			}else{
				return null;
			}
		}

		public boolean isTemplateNameExistByUserId(Long userId,	String templateName) {
			
			try {
				List<AutoSMS> autoSmsList = null;
				
				autoSmsList = getHibernateTemplate().find("FROM AutoSMS where userId="+userId+" AND templateName='"+templateName+"'");
				if(autoSmsList != null && autoSmsList.size()>0)
					return true;
				else	return false;
			} catch (DataAccessException e) {
				logger.error("Exception ::" , e);
				return false;
			}
		}

	public String Ereceiptmsgtype(Long tempId) {
		try {
			List<String> autoSmsList = null;
			
		String	query = " SELECT messageContent FROM AutoSMS where autoSmsId="+tempId+"  AND autoSmsType='"+OCConstants.ERECEIPTMESSAGES+"'";
		
	
		autoSmsList= executeQuery(query);
		logger.info("inquery====>"+autoSmsList);
		if(autoSmsList!= null && autoSmsList.size()>0)

		return autoSmsList.get(0);
		
		else return null;
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			
		}
		return null;
	}
		
				
public List<AutoSMS> findByTemplateName(Long userId,	String templateName) {
			
	List<AutoSMS> autoSmsList = null;
			try {
				
				autoSmsList = getHibernateTemplate().find("FROM AutoSMS where userId="+userId+" AND templateName='"+templateName+"'");
				
			} catch (DataAccessException e) {
				logger.error("Exception ::" , e);
			}
			return autoSmsList;
		}

		public AutoSMS getAutoSmsTemplateById(Long tempId) {
			
			String hql = "FROM AutoSMS WHERE autoSmsId=" + tempId;
			List<AutoSMS> tempList = getHibernateTemplate().find(hql);
			
			logger.info("tempList-----"+tempList);
			if(tempList != null && tempList.size() > 0)return tempList.get(0);
			else return null;
		}
		public  AutoSMS getIssuecouponsmsmsg(Long tempId) {
			try {
				List<AutoSMS> autoSmsList = null;
			
			autoSmsList = getHibernateTemplate().find("FROM AutoSMS where autoSmsId="+tempId+" AND autoSmsType='"+OCConstants.ISSUECOUPONSMS+"'");
			logger.info("inquery====>"+autoSmsList);
			if(autoSmsList!= null && autoSmsList.size()>0)

			return autoSmsList.get(0);
			
			else return null;
			} catch (DataAccessException e) {
				logger.error("Exception ::" , e);
				
			}
			return null;
		}



		public List<AutoSMS> getTemplatesByStatus(Long userId, String type) {
			
			String qry=" FROM AutoSMS WHERE userId= " +userId+ " AND autoSmsType= '"+type+"' AND status='"+OCConstants.AUTO_SMS_TEMPLATE_STATUS_APPROVED+"'" ;
			List<AutoSMS> list= getHibernateTemplate().find(qry);
			if(list != null && list.size() > 0){
				
				return list;
			}else{
				return null;
			}
		}
		
		public List<AutoSMS> getAutoSmsTemplateByIds(String commaSeparatedAutoSmsIds) {
			
			String qry=" FROM AutoSMS WHERE autoSmsId IN ("+commaSeparatedAutoSmsIds+") " ;
			
			logger.info("hql qry is >>> "+qry);
			List<AutoSMS> list= getHibernateTemplate().find(qry);
			if(list != null && list.size() > 0){
				
				return list;
			}else{
				return null;
			}
		}
		

}
