package org.mq.marketer.campaign.dao;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignSchedule;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class OrganizationStoresDaoForDML extends AbstractSpringDaoForDML{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
    public OrganizationStoresDaoForDML(){
    	
    }
    private JdbcTemplate jdbcTemplate;

   	public JdbcTemplate getJdbcTemplate() {
   		return jdbcTemplate;
   	}

   	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
   		this.jdbcTemplate = jdbcTemplate;
   	}

    public void saveOrUpdate(OrganizationStores stores){
        super.saveOrUpdate(stores);
    }
    
    public void upadteSubName(Long orgId ,Long domainId,String subsID,String subName){
    	try{
    	String qry ="UPDATE org_stores SET subsidiary_name='"+subName+"' Where org_id="+orgId+" AND domain_id="+domainId+" AND subsidary_id='"+subsID+"'"; 
    	logger.info(" update query "+qry);
    	executeJdbcUpdateQuery(qry);
    	}
    	catch (Exception e) {
			logger.error("Exception ",e);
		}
    
    }
    
/*public int findByOrganizationstoresCount(Long userOrgId,String storeId, String searchStr){
    	try{
    		
    		String subQueryStr = "";
        	
        	if(!storeId.equals("All") ){
        		
        		subQueryStr += " AND homeStoreId='"+storeId+"'";
        		
        	}
        	if(searchStr != null ) {
        		
        		subQueryStr += " AND storeName like'"+searchStr+"%'";
        		
        	}
    		
    		
    		
    		
    	String query = " SELECT count(*) FROM OrganizationStores where userOrganization ="+userOrgId+subQueryStr;
    	List storeList = getHibernateTemplate().find(query);
		if(storeList.size()>0)
			return ((Long)storeList.get(0)).intValue();
		else
			return 0;
	}catch(Exception e){
		logger.error("**Exception :"+e+"**");
		return 0;
	}
    }

public int findByOrgStoresCount(Long userOrgId, String searchStr){
	try{
		
		String subQueryStr = "";
    	
    
    	if(searchStr != null ) {
    		
    		subQueryStr += " AND storeName like'"+searchStr+"%'";
    		
    	}
		
		
		
		
	String query = " SELECT count(*) FROM OrganizationStores where userOrganization ="+userOrgId+subQueryStr;
	List storeList = getHibernateTemplate().find(query);
	if(storeList.size()>0)
		return ((Long)storeList.get(0)).intValue();
	else
		return 0;
}catch(Exception e){
	logger.error("**Exception :"+e+"**");
	return 0;
}
}



      
        public List findAll(){
            return super.findAll(OrganizationStores.class);
        }  
        public List<OrganizationStores> findByOrganization(Long userOrgId){
        	
        	String queryStr = "from OrganizationStores where userOrganization ="+userOrgId;
        	return getHibernateTemplate().find(queryStr);
        	
        }
        public List<OrganizationStores> findByOrganization(Long userOrgId, String storeId, String searchStr, int startFrom, int count ){
        	String subQueryStr = "";
        	
        	if(!storeId.equals("All") ){
        		
        		subQueryStr += " AND homeStoreId='"+storeId+"'";
        		
        	}
        	if(searchStr != null ) {
        		
        		subQueryStr += " AND storeName like'%"+searchStr+"%'";
        		
        	}
        	
        	String queryStr = "from OrganizationStores where userOrganization ="+userOrgId+subQueryStr+" Order By storeName "  ;
        	return executeQuery(queryStr, startFrom, count);
        	
        }    
        public List<OrganizationStores> findByOrgByOrgId(Long userOrgId ){
        	
        	String queryStr = "from OrganizationStores where userOrganization ="+userOrgId;
        	return executeQuery(queryStr);
        	
        }    
        public List <OrganizationStores>findOrderbyAllStores(){
        	logger.info("just entered-----");
        	return getHibernateTemplate().find("from OrganizationStores order by storeName");
        }*/
        public void saveByCollection(List<OrganizationStores> storeList) {
        	super.saveByCollection(storeList);
        	
        	
        }
        
       /* public UserOrganization findByOrgName(String orgShortName) {
        	UserOrganization userOrg = null;
        	
        	List userOrgList = getHibernateTemplate().find("FROM UserOrganization WHERE orgExternalId = '"+orgShortName+"'"); 
        	
        	 if(userOrgList.size()>0){
        		 userOrg = (UserOrganization)userOrgList.get(0);
    		 }
          return userOrg; 
         
        }*/
       /* public List<OrganizationStores> findBySearchStr(String name, Long orgId) {
        	
        	String hql = "FROM OrganizationStores WHERE storeName " +
             		"like '"+  name + "%' AND  userOrganization="+orgId ;
        	
        	logger.info("hql ::"+hql);
             List<OrganizationStores> storeList = getHibernateTemplate().find(hql);
           
             logger.info("storeList ::"+storeList.size());

             
             return storeList; 
             
        }                          
  public List<OrganizationStores> findByStoreId(String name, Long orgId) {
        	
        	String hql = "FROM OrganizationStores WHERE homeStoreId = '"+  name + "' AND userOrganization="+orgId;
        	
        	logger.info("hql ::"+hql);
             List<OrganizationStores> storeList = getHibernateTemplate().find(hql);
           
             logger.info("storeList ::"+storeList.size());

             
             return storeList; 
             
        }  
  
  public OrganizationStores findByStoresId(String name, Long orgnizationId){
  	OrganizationStores organizationStores = null;
       List storeList = getHibernateTemplate().find("from OrganizationStores where homeStoreId = '"+  name + "' AND userOrganization="+orgnizationId);
       if(storeList.size()>0){
				organizationStores = (OrganizationStores)storeList.get(0);
		 }
       return organizationStores; 
       
  }
    
  
  
  
  public List<OrganizationStores> findByStoreId(String name, Long orgId, String store) {
  	String hql = "FROM OrganizationStores WHERE homeStoreId = '"+  name + "' AND userOrganization="+orgId+ "  OR storeName='"+store+"'";
  	
  	logger.info("hql ::"+hql);
       List<OrganizationStores> storeList = getHibernateTemplate().find(hql);
     
       logger.info("storeList ::"+storeList.size());

       
       return storeList; 
       
  }         
        public OrganizationStores findByStoreName (String name, Long orgnizationId){
        	OrganizationStores stores = null;
             List storeList = getHibernateTemplate().find("from OrganizationStores where storeName = '"+  name + "' AND userOrganization="+orgnizationId);
             if(storeList.size()>0){
            	 stores = (OrganizationStores)storeList.get(0);
    		 }
             return stores; 
             
        }    
        
       
        
      
        public List<OrganizationStores> findAllStores(Long orgnizationId) {
   		 try {
   				String queryStr = "FROM OrganizationStores WHERE userOrganization="+orgnizationId;
   				return getHibernateTemplate().find(queryStr);
   			 } catch (DataAccessException e) {
   				 logger.error(" Exception : ",(Throwable)e);
   				 return null;
   			 } //catch
   	 }*/
       /* public List<UserOrganization> findAllOrganizations() {
    		try {
    			List orgList = getHibernateTemplate().find("FROM UserOrganization ORDER BY organizationName"); 
    			
    			if(orgList == null || orgList.size() == 0) return null;
    			else return orgList;
    		} catch (DataAccessException e) {
    			logger.error("Exception ::" , e);
    			return null;
    		}
    	}
        */
        
        public void delete(OrganizationStores stores) {
            super.delete(stores);
        }
        
        
      /* public String findNameById(String storeId, Long orgId) {
    	   
    	   String qry = "FROM OrganizationStores WHERE userOrganization = "+orgId+" AND storeId="+storeId;
    	   
    	   List<OrganizationStores> orgStoList = getHibernateTemplate().find(qry);
    	   
    	   if(orgStoList != null && orgStoList.size() > 0) {
    		   
    		   OrganizationStores store = orgStoList.get(0);
    		   
    		   String storeName = null;
    		   
    		   if(store.getStoreName() != null) storeName = store.getStoreName();
    		   
    		   return storeName;
    	   }
    	   
    	   else {
    		   
    		   return null;
    	   }
    	   
    	   
    	   
       }
        
       
       public OrganizationStores findByStoreLocationId (Long orgnizationId, String locationId){
       	OrganizationStores stores = null;
            List storeList = getHibernateTemplate().find("from OrganizationStores where userOrganization="+orgnizationId+" AND homeStoreId='"+locationId+"'");
            if(storeList.size()>0){
           	 stores = (OrganizationStores)storeList.get(0);
   		 }
            return stores; 
            
       }

       public OrganizationStores getStore(Long orgId, String value) {
    	OrganizationStores stores = null;
        List storeList = getHibernateTemplate().find("from OrganizationStores where homeStoreId = '"+  value + "' AND userOrganization="+orgId.longValue());
        if(storeList != null && storeList.size()>0){
       	 stores = (OrganizationStores)storeList.get(0);
		 }
        return stores; 
        
       }
       
       public List<Map<String, Object>> findStoreNumberNameMapList(Long orgnizationId) {
       	
       	try {
   			String qry = "SELECT home_store_id , store_name FROM org_stores WHERE org_id ="+orgnizationId+" AND home_store_id IS NOT NULL";
   			
   			logger.info("findStoreNameNumberMapList qry is >>>::"+qry);
   			return jdbcTemplate.queryForList(qry);
   			
   		} catch (DataAccessException e) {
   			logger.error("Exception ::" , e);
   			return null;
   		}
       	
       }*/ // findStoreNameNumberMapList
       
        
}
