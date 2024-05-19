package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.ApplicationProperties;
import org.mq.marketer.campaign.beans.UserDesignedCustomRows;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

@SuppressWarnings({"unused"})
public class UserDesignedCustomRowsDao extends AbstractSpringDao{


	private JdbcTemplate jdbcTemplate;
	
	public UserDesignedCustomRowsDao() {}
	
    public UserDesignedCustomRows find(Long templateRowId) {
        return (UserDesignedCustomRows) super.find(UserDesignedCustomRows.class, templateRowId);
    }
    
    public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	 @SuppressWarnings("unchecked")
	 public List<UserDesignedCustomRows> findTemplatesFromUserId(Long userId, String saveRowType) {
			try {
				String queryStr = null;
				if(userId!=null && saveRowType !=null) {
					queryStr = "FROM UserDesignedCustomRows where userId= '"+userId+"' and rowCategory = '"+saveRowType+"' and rowJsonData is not null";
				}/*else if(userId!=null){
					queryStr = "select userId as userId,rowCategory as rowCategory, templateName as templateName FROM UserDesignedCustomRows rows where userId= "+userId+" and rowJsonData is not null group by rowCategory";
				}*/	
	    		List<UserDesignedCustomRows> list = executeQuery(queryStr);
	    		if(list!= null && list.size() > 0) {
	    			return list;
	    		}else {
	    			return null;
	    		}
			} catch (DataAccessException e) {
				logger.error("Exception ::" , e);
				return null;
			}catch (Exception e) {
				logger.error("Exception ::" , e);
				return null;
			}
		}
	 
	 
	 
	 public List<String> findTemplatesFromUserId(Long userId) {

			try {
				String queryStr = null;
				if(userId!=null) {
					queryStr = "select rowCategory FROM UserDesignedCustomRows where userId= '"+userId+"' and rowJsonData is not null group by rowCategory";
				}	
	    		List<String> list = executeQuery(queryStr);
	    		if(list!= null && list.size() > 0) {
	    			return list;
	    		}else {
	    			return null;
	    		}
			} catch (DataAccessException e) {
				logger.error("Exception ::" , e);
				return null;
			}catch (Exception e) {
				logger.error("Exception ::" , e);
				return null;
			}
		
	 }
	 
	 
	 @SuppressWarnings("unchecked")
	 public UserDesignedCustomRows getUserDesignedCustomRowsBasedonTemplateAndCategoryName(Long userId, String templateName, String categoryName) {
			try {
	    		String queryStr = "FROM UserDesignedCustomRows WHERE userId= "+userId+" AND templateName ='"+templateName+"' AND rowCategory = '"+categoryName+"'";
				List<UserDesignedCustomRows> list = executeQuery(queryStr);
	    		if(list!= null && list.size() > 0) {
	    			return list.get(0);
	    		}else {
	    			return null;
	    		}
			} catch (DataAccessException e) {
				logger.error("Exception ::" , e);
				return null;
			}catch (Exception e) {
				logger.error("Exception ::" , e);
				return null;
			}
		}
	 
	 public List<String> getRowCategoriesFromUserId(Long userId) {
			try {
	    		String queryStr = "select distinct rowCategory FROM UserDesignedCustomRows where userId= "+userId+" and rowJsonData is not null";
	    		List<String> list = executeQuery(queryStr);
	    		if(list!= null && list.size() > 0) {
	    			return list;
	    		}else {
	    			return null;
	    		}
			} catch (DataAccessException e) {
				logger.error("Exception ::" , e);
				return null;
			}catch (Exception e) {
				logger.error("Exception ::" , e);
				return null;
			}
		}

	public List<ApplicationProperties> getDefaultTemplate(String beeEditorType) {
		try {
			String filter = null;
			if(beeEditorType.equals("MyTemplate")) {
				filter = "'Email Campaign Footer','Auto-email Footer'";
			}else if(beeEditorType.equals("autoEmail")) {
				filter = "'Auto-email Footer'";
			}else if(beeEditorType.equals("campaign")) {
				filter = "'Email Campaign Footer'";
			}
		String queryStr = "FROM ApplicationProperties where key in("+filter+") and value is not null";
		List<ApplicationProperties> list = executeQuery(queryStr);
		if(list!= null && list.size() > 0) {
			return list;
		}else {
			return null;
		}
		}catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}

}

