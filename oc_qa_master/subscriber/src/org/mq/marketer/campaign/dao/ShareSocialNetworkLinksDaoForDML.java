package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ShareSocialNetworkLinks;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;


public class ShareSocialNetworkLinksDaoForDML extends AbstractSpringDaoForDML {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public ShareSocialNetworkLinksDaoForDML() {}
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

   /* public ShareSocialNetworkLinksDao find(Long id) {
        return (ShareSocialNetworkLinksDao) super.find(ShareSocialNetworkLinksDao.class, id);
    }*/

    public void saveOrUpdate(ShareSocialNetworkLinks shareSocialNetworkLink) {
        super.saveOrUpdate(shareSocialNetworkLink);
    }

    public void delete(ShareSocialNetworkLinks shareSocialNetworkLink) {
        super.delete(shareSocialNetworkLink);
    }


   /* public long findShareCountByCrId(long crId){
    	String qry="SELECT COUNT(id) FROM ShareSocialNetworkLinks WHERE crId="+crId+"  ";
		
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
		
		return count;
		
    }
    
    public List<ShareSocialNetworkLinks> getSharedByCrId(Long crId,int firstResult,int maxResults){
    	try {
			String query = "from ShareSocialNetworkLinks where crId =" + crId.longValue() +"  GROUP BY sentId,sourceType";
			logger.info("qry is"+query);
			List<ShareSocialNetworkLinks> list =  executeQuery(query, firstResult, maxResults);
			return list;
		}catch (Exception e) {
			logger.error("** Error : " + e.getMessage() + " **");
			return null;
		} 
    }
    
    public int findShareCount(long crId){
    	String qry="SELECT COUNT(DISTINCT sentId) FROM ShareSocialNetworkLinks WHERE crId="+crId+"  ";
		
		int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
		
		return count;
		
    }
    
    public List findDistinctSentIdsByCrId(Long crId,int firstResult,int maxResults) {
    	
    	String query = "select distinct(sentId) from ShareSocialNetworkLinks where crId =" + crId.longValue();
		logger.info("qry is :: "+query);
//		List<Long> list = getHibernateTemplate().find(query);
		List list =  executeQuery(query, firstResult, maxResults);
		return list;
    	
    } // findDistinctSentIdsByCrId
    
    public List<ShareSocialNetworkLinks> getSharedByCrId(Long crId,String sentIdStr){
    	try {
			String query = "from ShareSocialNetworkLinks where crId =" + crId.longValue() +"  and sentId in("+sentIdStr+")";
			logger.info("qry is"+query);
			List<ShareSocialNetworkLinks> list =  executeQuery(query);
			return list;
		}catch (Exception e) {
			logger.error("** Error : " + e.getMessage() + " **");
			return null;
		} 
    }*/
}