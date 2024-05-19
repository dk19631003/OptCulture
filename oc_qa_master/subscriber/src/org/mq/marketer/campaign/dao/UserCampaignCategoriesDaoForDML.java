package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.DRSent;
import org.mq.marketer.campaign.beans.DefaultCategories;
import org.mq.marketer.campaign.beans.UserCampaignCategories;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserCampaignCategoriesDaoForDML extends AbstractSpringDaoForDML {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	/*public UserCampaignCategories find(Long id) {
		return (UserCampaignCategories) super.find(UserCampaignCategories.class, id);
	}
*/
	public void saveOrUpdate(UserCampaignCategories userCampaignCategories) {
		super.saveOrUpdate(userCampaignCategories);
	}

	public void delete(UserCampaignCategories userCampaignCategories) {
		super.delete(userCampaignCategories);
	}
	
/*	 public List findAll() {
	        return super.findAll(UserCampaignCategories.class);
	    }
	
		
		public List<UserCampaignCategories> findByUserId(long userId){
		try {
//			logger.info("start "+startindex +"end index isa"+endIndex);

			String qry=" FROM UserCampaignCategories WHERE userId = "+userId;
			List<UserCampaignCategories> usersCategoriesList = getHibernateTemplate().find(qry);
			//List<UserCampaignCategories> usersCategoriesList = executeQuery(qry,startindex,endIndex);
			logger.info("usersCategoriesListt >>>>>>>>>>>>>>>>>>>>> "+usersCategoriesList.size());
			if(usersCategoriesList.size() > 0){
						return usersCategoriesList;
			}else{
						return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
		
		
	}
	
	public List<UserCampaignCategories> findCatByUserId(long userId){
		try {
			//List<UserCampaignCategories> usersCategoriesList=null;
			String qry=" FROM UserCampaignCategories WHERE userId = "+userId+" AND isVisible = true";
			List<UserCampaignCategories> usersCategoriesList = executeQuery(qry);
			if(usersCategoriesList.size() > 0){
						return usersCategoriesList;
			}else{
						return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
		
		
	}
	
	public List<UserCampaignCategories> findById(String catIdStr,Long userId){
		List<UserCampaignCategories> userCampaCatList = null;
		String qry = " FROM UserCampaignCategories  WHERE userId="+userId.longValue()+" AND  id IN("+catIdStr+")";
		
		try {
			userCampaCatList = executeQuery(qry);
					
			if ( userCampaCatList != null && userCampaCatList.size() > 0) {
				return userCampaCatList;
			}
		} catch (Exception e) {
			logger.error(" ** Exception : ", e ); 
		}
		
		
		return null;
		
		
	}
	public UserCampaignCategories findByCatId(Long catIdStr,Long userId ){
		List<UserCampaignCategories> userCampaCatList = null;
		String qry = " FROM UserCampaignCategories  WHERE userId="+userId.longValue()+" AND  id IN("+catIdStr+")";
		
		try {
			userCampaCatList = executeQuery(qry);
					
			if ( userCampaCatList != null && userCampaCatList.size() > 0) {
				return userCampaCatList.get(0);
			}
		} catch (Exception e) {
			logger.error(" ** Exception : ", e ); 
		}
		
		
		return null;
		
		
	}
	// added for send now
	
		public UserCampaignCategories findById(String catIdStr){
			List<UserCampaignCategories> userCampaCatList = null;
			String qry = " FROM UserCampaignCategories  WHERE  id IN("+catIdStr+")";
			
			try {
				userCampaCatList = executeQuery(qry);
						
				if ( userCampaCatList != null && userCampaCatList.size() > 0) {
					return userCampaCatList.get(0);
				}
			} catch (Exception e) {
				logger.error(" ** Exception : ", e ); 
			}
			
			
			return null;
			
			
		}
	
	
		
		public int findTotCount(long userId){
		try {
			//List<UserCampaignCategories> usersCategoriesList=null;
			String qry=" FROM UserCampaignCategories WHERE userId = "+userId ;
			List<UserCampaignCategories> usersCategoriesList = executeQuery(qry);
			if(usersCategoriesList.size() > 0){
						return usersCategoriesList.size();
			}else{
						return 0;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return 0;
		}
		
		
	}
	*/
	
}
