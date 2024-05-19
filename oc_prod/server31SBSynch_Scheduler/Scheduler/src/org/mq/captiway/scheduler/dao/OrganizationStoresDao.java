package org.mq.captiway.scheduler.dao;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.OrganizationStores;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.Utility;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class OrganizationStoresDao extends AbstractSpringDao{
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
    public OrganizationStoresDao(){
    	
    }
    private JdbcTemplate jdbcTemplate;

   	public JdbcTemplate getJdbcTemplate() {
   		return jdbcTemplate;
   	}

   	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
   		this.jdbcTemplate = jdbcTemplate;
   	}

   /* public void saveOrUpdate(OrganizationStores stores){
        super.saveOrUpdate(stores);
    }*/
    
      
        public List findAll(){
            return super.findAll(OrganizationStores.class);
        }    
        public List<OrganizationStores> findByOrganization(Long userOrgId ) {
        	
        	String hql = "from OrganizationStores where userOrganization ="+userOrgId.longValue();
        	
        	List<OrganizationStores> storelist = getHibernateTemplate().find(hql);
        	
        	return storelist;
        	//return getHibernateTemplate().find("from OrganizationStores where userOrganization ="+userOrgId.longValue());
        }    
        
        public List <OrganizationStores>findOrderbyAllStores(){
        	if(logger.isDebugEnabled()) logger.debug("just entered-----");
        	return getHibernateTemplate().find("from OrganizationStores order by storeName");
        }
        /*public void saveByCollection(List<OrganizationStores> storeList) {
        	super.saveOrUpdateAll(storeList);
        	
        	
        }*/
        
       /* public UserOrganization findByOrgName(String orgShortName) {
        	UserOrganization userOrg = null;
        	
        	List userOrgList = getHibernateTemplate().find("FROM UserOrganization WHERE orgExternalId = '"+orgShortName+"'"); 
        	
        	 if(userOrgList.size()>0){
        		 userOrg = (UserOrganization)userOrgList.get(0);
    		 }
          return userOrg; 
         
        }*/
        
        public OrganizationStores findByStoreName (String name, Long orgnizationId){
        	OrganizationStores stores = null;
             List storeList = getHibernateTemplate().find("from OrganizationStores where storeName = '"+  name + "' AND userOrganization="+orgnizationId);
             if(storeList.size()>0){
            	 stores = (OrganizationStores)storeList.get(0);
    		 }
             return stores; 
             
        }
        
        
        public OrganizationStores findByStoreLocationId (Long orgnizationId, String locationId){
        	OrganizationStores stores = null;
             List storeList = getHibernateTemplate().find("from OrganizationStores where userOrganization="+orgnizationId+" AND homeStoreId='"+locationId+"'");
             if(storeList.size()>0){
            	 stores = (OrganizationStores)storeList.get(0);
    		 }
             return stores; 
             
        }
        /*public List<OrganizationStores> findAllByIds(Set<Long> storeIds) {
        	
        	String storeIdsStr = Utility.getUserIdsAsString(storeIds);
        	
        	return getHibernateTemplate().find("FROM OrganizationStores Where storeIds IN ("+storeIdsStr+")");
        	
        }*/
        public List findAllStores() {
   		 try {
   				String queryStr = "SELECT storeId,storeName FROM OrganizationStores ORDER BY storeName";
   				return getHibernateTemplate().find(queryStr);
   			 } catch (DataAccessException e) {
   				 if(logger.isErrorEnabled()) logger.error(" Exception : ",(Throwable)e);
   				 return null;
   			 } //catch
   	 }
       /* public List<UserOrganization> findAllOrganizations() {
    		try {
    			List orgList = getHibernateTemplate().find("FROM UserOrganization ORDER BY organizationName"); 
    			
    			if(orgList == null || orgList.size() == 0) return null;
    			else return orgList;
    		} catch (DataAccessException e) {
    			logger.error("Exception ::::" , e);
    			return null;
    		}
    	}
        */
        
        /*public void delete(OrganizationStores stores) {
            super.delete(stores);
        }*/
        
        
        
        
        
}
