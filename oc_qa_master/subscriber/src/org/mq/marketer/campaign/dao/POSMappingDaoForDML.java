package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class POSMappingDaoForDML extends AbstractSpringDaoForDML {

    public POSMappingDaoForDML() {}

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    /*public POSMapping find(Long id) {
        return (POSMapping) super.find(POSMapping.class, id);
    }*/

    public void saveOrUpdate(POSMapping posCustField) {
        super.saveOrUpdate(posCustField);
    }
    
    public void delete(POSMapping posCustField) {
        super.delete(posCustField);
    }
    
   /* public List<POSMapping> findAllByUserId(long userId) {
    	try {
    		logger.info("userId is :"+userId);
    		List<POSMapping> list = null;
			list = getHibernateTemplate().find("from POSMapping where userId = " + userId);
			
			if(list.size() >0) return list;
			else return null;
			
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    	
    }
   
    public POSMapping findRecord(String scopeStr,String posAttrStr,String custFieldStr, long userId) {
    	
    	try {
			List<POSMapping> list = getHibernateTemplate().find("from POSMapping where mappingType ='"+scopeStr+"' and " +
										"posAttribute='"+posAttrStr+"' and customFieldName = '"+custFieldStr+"' and userId = " + userId);
    		
    		List<POSMapping> list = getHibernateTemplate().find("from POSMapping where user_id ="+userId+" mappingType ='"+scopeStr+"' and " +
					"posAttribute='"+posAttrStr+"' and customFieldName = '"+custFieldStr+"' ");
    		
			if(list.size() >0) return list.get(0);
			else return null;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    }
    
    public List<POSMapping> findByType(String mappingType, Long userId) {
    	
    	try {
			return getHibernateTemplate().find("FROM POSMapping Where userId="+userId+ " AND mappingType in("+mappingType+")");
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    	
    	
    }
    
 public List<POSMapping> findOnlyByGenType(String mappingType, Long userId) {
    	
    	try {
			return getHibernateTemplate().find("FROM POSMapping Where userId="+userId+ " AND mappingType ='"+mappingType+"' AND customFieldName NOT LIKE 'UDF%'");
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    	
    	
    }
    
 public List<POSMapping> findOnlyByType(String mappingType, Long userId) {
    	
    	try {
			return getHibernateTemplate().find("FROM POSMapping Where userId="+userId+ " AND mappingType ='"+mappingType+"' AND customFieldName LIKE 'UDF%'");
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    	
    	
    }
    
    
    public POSMapping findRecordByPOSAttribute(String posAttr,String mappingType ,Long userId) {
    	try {
			List<POSMapping> list = getHibernateTemplate().find("from POSMapping where userId = " + userId +" and  " +
																"mappingType ='"+mappingType+"' and posAttribute='"+posAttr+"'");
			if(list.size() >0) return list.get(0);
			else return null;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    }
    
    public List<POSMapping> getPriorityMapByUserId(Long userId, String mappingType) {
    	
    	try {
    		
    		logger.info("userId is ::" +userId);
			List<POSMapping> list = getHibernateTemplate().find("from POSMapping where userId = " + userId +" and  " +
																"mappingType  in ("+mappingType+") and uniquePriority is not null ");
			if(list.size() >0) return list;
			else return null;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    	
    } // getPriorityMapByUserId
    
    
    public String getSKUItemSidMapByUserId(long userId,String mappingType,String fileAttribute) {
    	String qry = "from POSMapping where userId = " + userId +" and  " +
				"mappingType ='"+mappingType+"' and  posAttribute='"+fileAttribute+"'";
    	
    	logger.info(">>>>>>>>> qry is ::"+qry);
    	List<POSMapping> list = getHibernateTemplate().find(qry);
    	if(list.size() >0) {
    		return ((POSMapping)list.get(0)).getCustomFieldName();
    	}
    	return null;
    }
    
    
    
    //added for new event trigger
    public List<POSMapping> findContactDateMappings(String mappingType, Long userId) {
    	
    	try {
			String hql = "FROM POSMapping where userId = " + userId +" AND mappingType ='"+mappingType+"' " +
							"AND customFieldName LIKE 'UDF%' AND dataType LIKE 'Date(%' ";
			
			List<POSMapping> retList = executeQuery(hql);
			
			if(retList != null && retList.size() > 0) {
				
				return retList;
			}else return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    	
    }//findContactDateMappings
*/    
}
