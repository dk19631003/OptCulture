package org.mq.marketer.campaign.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignSchedule;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.model.mobileapp.Lookup;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class OrganizationStoresDao extends AbstractSpringDao{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
    public OrganizationStoresDao(){
    	
    }
    private JdbcTemplate jdbcTemplate;

   	public JdbcTemplate getJdbcTemplate() {
   		return jdbcTemplate;
   	}

   	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
   		this.jdbcTemplate = jdbcTemplate;
   	}
 
    /*public void saveOrUpdate(OrganizationStores stores){
        super.saveOrUpdate(stores);
    }*/
    
public int findByOrganizationstoresCount(Long userOrgId,String storeId, String searchStr){
    	try{
    		
    		String subQueryStr = "";
        	
        	if(!storeId.equals("All") ){
        		
        		subQueryStr += " AND homeStoreId='"+storeId+"'";
        		
        	}
        	if(searchStr != null ) {
        		
        		subQueryStr += " AND storeName like'"+StringEscapeUtils.escapeSql(searchStr)+"%'";
        		
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

public int findByOrgStoresCount(Long userOrgId,String orgId, String sbsId,String storeId,String searchStr){
	try{
		
		String subQueryStr = "";
    	
		if(!orgId.equals("All") ){
    		
    		subQueryStr += " AND domainId='"+orgId+"'";
    		
    	}
		if(!sbsId.equals("All") ){
    		
    		subQueryStr += " AND subsidiaryId='"+sbsId+"'";
    		
    	}
		if(!storeId.equals("All") ){
    		
    		subQueryStr += " AND homeStoreId='"+storeId+"'";
    		
    	}
    	if(searchStr != null ) {
    		
    		subQueryStr += " AND storeName like'%"+StringEscapeUtils.escapeSql(searchStr)+"%'";
    		
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
        public List<OrganizationStores> findByOrganization(Long userOrgId,String orgId,String sbsId,String storeId, String searchStr, int startFrom, int count ){
        	String subQueryStr = "";
        	
        	if(!orgId.equals("All") ){
        		
        		subQueryStr += " AND domainId='"+orgId+"'";
        		
        	}
        	if(!sbsId.equals("All") ){
        		
        		subQueryStr += " AND subsidiaryId='"+sbsId+"'";
        		
        	}
			if(!storeId.equals("All") ){
				
				subQueryStr += " AND homeStoreId='"+storeId+"'";
				
			}
        	if(searchStr != null ) {
        		
        		subQueryStr += " AND storeName like'%"+StringEscapeUtils.escapeSql(searchStr)+"%'";
        		
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
        }
        /*public void saveByCollection(List<OrganizationStores> storeList) {
        	super.saveByCollection(storeList);
        	
        	
        }*/
        
       /* public UserOrganization findByOrgName(String orgShortName) {
        	UserOrganization userOrg = null;
        	
        	List userOrgList = getHibernateTemplate().find("FROM UserOrganization WHERE orgExternalId = '"+orgShortName+"'"); 
        	
        	 if(userOrgList.size()>0){
        		 userOrg = (UserOrganization)userOrgList.get(0);
    		 }
          return userOrg; 
         
        }*/
        public List<OrganizationStores> findBySearchStr(String name, Long orgId) {
        	
        	String hql = "FROM OrganizationStores WHERE storeName " +
             		"like '"+ StringEscapeUtils.escapeSql(name)  + "%' AND  userOrganization="+orgId ;
        	
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
    
  public OrganizationStores getSubNameByOrgUnitId( Long orgnizationId,Long domain ,String subId,String subName){
	  OrganizationStores organizationStores = null;   
	  List storeList = getHibernateTemplate().find("from OrganizationStores where domainId = "+  domain + " AND subsidiaryId=' "+subId+"' AND subsidiaryName=' "+subName+"' AND  userOrganization="+orgnizationId);
	       if(storeList.size()>0){
					organizationStores = (OrganizationStores)storeList.get(0);
			 }
	       return organizationStores; 
	       
  }
  public OrganizationStores getSubIdByOrgUnitId( Long orgnizationId,Long domain,String subId){
	  OrganizationStores organizationStores = null;   
	  List storeList = getHibernateTemplate().find("from OrganizationStores where domainId = "+  domain + " AND subsidiaryId=' "+subId+"' AND userOrganization="+orgnizationId);
	       if(storeList.size()>0){
					organizationStores = (OrganizationStores)storeList.get(0);
			 }
	       return organizationStores; 
	       
  }
  public OrganizationStores getStoreIdBySubsidary( Long orgnizationId,Long domain,String subId,String homeStoreId){
	  OrganizationStores organizationStores = null;   
	  List storeList = getHibernateTemplate().find("from OrganizationStores where domainId = "+  domain + " AND subsidiaryId='"+subId+"' AND homeStoreId='"+homeStoreId+"' AND userOrganization="+orgnizationId);
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
        public OrganizationStores findSubsidaryBy(Long orgnizationId,Long domainId, String sbsID){

		      String qry = "FROM OrganizationStores WHERE userOrganization = "+orgnizationId+" AND domainId in("+domainId+") AND subsidiaryId='"+sbsID+"'";
		      logger.info(qry);
		      List<OrganizationStores> orgStoList = getHibernateTemplate().find(qry);
		      logger.info("list size  "+orgStoList.size());
		      if(orgStoList.size()>0) return orgStoList.get(0);
		      else return null;
		     
		      }
        
       
       public List<OrganizationStores> findStoresNamebySudsidiaryName(String subsidiaryIds,Long orgnizationId )
       {
    	   try {
  				String queryStr = "FROM OrganizationStores WHERE  userOrganization="+orgnizationId + " AND subsidiaryId in("+subsidiaryIds+") ";//  AND  ;
  				return executeQuery(queryStr);
  			 } catch (DataAccessException e) {
  				 logger.error(" Exception : ",(Throwable)e);
  				 return null;
  			 } //catch
  	 
       }
     //get subsidary based on orgUnitId
       public List<OrganizationStores> findSubsidaryByOrgUnitId(Long orgnizationId,String domainId){

    	  String qry = "FROM OrganizationStores WHERE userOrganization = "+orgnizationId+" AND domainId in("+domainId+") group by subsidiaryId";
    	   logger.info(qry);
    	   List<OrganizationStores> orgStoList = getHibernateTemplate().find(qry);
    	   logger.info("list size  "+orgStoList.size());
    	   if(orgStoList.size()>0)
    	   return orgStoList;
    	   else
    		   return null;
       }
      
        public List<OrganizationStores> findAllStores(Long orgnizationId) {
   		 try {
   				String queryStr = "FROM OrganizationStores WHERE userOrganization="+orgnizationId;
   				return getHibernateTemplate().find(queryStr);
   			 } catch (DataAccessException e) {
   				 logger.error(" Exception : ",(Throwable)e);
   				 return null;
   			 } //catch
   	 }
        public List<String> findAllSBS(Long orgnizationId) {
      		 try {
      				String queryStr = "SELECT DISTINCT(subsidiaryName) FROM OrganizationStores WHERE userOrganization="+orgnizationId+" AND subsidiaryName IS NOT NULL";
      				return executeQuery(queryStr);
      			 } catch (Exception e) {
      				 logger.error(" Exception : ",e);
      				 return null;
      			 } //catch
      	 }
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
        
        /*public void delete(OrganizationStores stores) {
            super.delete(stores);
        }*/
        
        
       public String findNameById(String storeId, Long orgId) {
    	   
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
       public OrganizationStores getStoreByStoreId(Long storeId){
    	   OrganizationStores stores = null;
           List storeList = getHibernateTemplate().find("from OrganizationStores where storeId ="+storeId.longValue());
           if(storeList != null && storeList.size()>0){
          	 stores = (OrganizationStores)storeList.get(0);
   		 }
           return stores;   
       }
       public List<Map<String, Object>> findStoreNumberNameMapList(Long orgnizationId) {
       	
       	try {
   			String qry = "SELECT home_store_id, subsidary_id, subsidiary_name, store_name FROM org_stores WHERE org_id ="+orgnizationId+" AND home_store_id IS NOT NULL";
   			
   			logger.info("findStoreNameNumberMapList qry is >>>::"+qry);
   			return jdbcTemplate.queryForList(qry);
   			
   		} catch (DataAccessException e) {
   			logger.error("Exception ::" , e);
   			return null;
   		}
       	
       } // findStoreNameNumberMapList
       public List<Map<String, Object>> findSubsidiaryNameNumberMapList(Long orgnizationId) {
          	
          	try {
          		String qry = "SELECT subsidary_id , subsidiary_name FROM org_stores WHERE org_id ="+orgnizationId+" AND subsidary_id IS NOT NULL";
      			
      			logger.info("findSubsidiaryNameNumberMapList qry is >>>::"+qry);
      			return jdbcTemplate.queryForList(qry);
      			
      		} catch (DataAccessException e) {
      			logger.error("Exception ::" , e);
      			return null;
      		}
          	
          } // findSubsidiaryNameNumberMapList
       
       //get subsidary based on orgUnitId
       public List<OrganizationStores> findSubsidaryByOrgUnitId(Long orgnizationId,Long domainId){

    	   String qry = "FROM OrganizationStores WHERE userOrganization = "+orgnizationId+"  AND domainId = "+domainId;
    	    logger.info("  sub query "+qry);
    	   List<OrganizationStores> orgStoList = getHibernateTemplate().find(qry);
    	   logger.info(" list size "+orgStoList.size());
    	   if(orgStoList.size()>0)
    	   return orgStoList;
    	   else
    		   return null;
       }
       public List<OrganizationStores> findSubsidaryByOrgUnitId(Long domainId){

    	   String qry = "FROM OrganizationStores WHERE domainId = "+domainId+" GROUP BY subsidiaryId" ;
    	   List<OrganizationStores> orgStoList = getHibernateTemplate().find(qry);
    	   if(orgStoList.size()>0)
    	   return orgStoList;
    	   else
    		   return null;
       }
       //get store by subsidary

       public List<OrganizationStores> findStoreBySubsidaryId(Long domainId,String subsidaryId){
    	   String qry = "FROM OrganizationStores WHERE domainId = "+domainId+" AND subsidiaryId='"+subsidaryId+"'";
    	   List<OrganizationStores> orgStoList = getHibernateTemplate().find(qry);
    	   if(orgStoList.size()>0)
    	   return orgStoList;
    	   else
    		   return null;
       }
       public List<OrganizationStores> findStoreBySubsidaryName(Long domainId,String subName){
    	   String qry = "FROM OrganizationStores WHERE domainId = "+domainId+" AND subsidiaryName='"+subName+"'";
    	   logger.info(" sub Name query "+qry);
    	   List<OrganizationStores> orgStoList = getHibernateTemplate().find(qry);
    	   if(orgStoList.size()>0)
    	   return orgStoList;
    	   else
    		   return null;
       }
       public OrganizationStores  getSubsidiaryByStoreId(Long orgnizationId,Object eachStore){
    	   String qry = "FROM OrganizationStores WHERE userOrganization = "+orgnizationId+" AND homeStoreId='"+eachStore+"'";
    	   List<OrganizationStores> orgStoList = getHibernateTemplate().find(qry);
    	   
    	   return orgStoList== null || orgStoList.isEmpty() ? null : orgStoList.get(0);
    	   
       }
       public List<OrganizationStores> findStoreByOrgAndSubId(Long orgnizationId,Long domainId,String subsidaryId){
    	   String qry = "FROM OrganizationStores WHERE userOrganization = "+orgnizationId+" AND  domainId="+domainId+" AND subsidiaryId='"+subsidaryId+"'";
    	   List<OrganizationStores> orgStoList = getHibernateTemplate().find(qry);
    	   if(orgStoList.size()>0)
    	   return orgStoList;
    	   else
    		   return null; 
       }
       
       
       public OrganizationStores findOrgStoreObject(Long orgnizationId,Long domainId, String SBSID, String StoreNo){

    	   /*String qry = "FROM OrganizationStores WHERE userOrganization = "+orgnizationId+"  AND domainId = "+domainId + 
    			   " AND subsidiaryId='"+SBSID+"' AND homeStoreId='"+StoreNo+"'";*/
    	   
    	   String subQry ="";
    	   
    	   subQry += (StoreNo != null && !StoreNo.isEmpty()) ? " AND homeStoreId='"+StoreNo+"'" : "";
    	   subQry += (SBSID != null && !SBSID.isEmpty()) ? " AND subsidiaryId='"+SBSID+"'" : "";
    	   
    	   
    	   String qry = "FROM OrganizationStores WHERE userOrganization = "+orgnizationId+"  AND domainId = "+domainId + 
    			    subQry;
    	   
    	   logger.info("====query  "+qry);
    	   List<OrganizationStores> orgStoList = getHibernateTemplate().find(qry);
    	   if(orgStoList.size()>0)
    	   return orgStoList.get(0);
    	   else
    		   return null;
       }
       public List<OrganizationStores> findOrgStoreByFilters(Long orgnizationId,String domainIdStr, String SBSID,String brandName,
				String locality,String cityName,String country,String storeName, String sortType, String sortOrder){

    	   String qry = "FROM OrganizationStores WHERE userOrganization = "+orgnizationId+" ";
    	   		if(!domainIdStr.isEmpty())
    	   			qry += " AND domainId ='"+domainIdStr+"'"; 
    	   		if(!SBSID.isEmpty())
    	   			qry += " AND subsidiaryId= '"+SBSID+"'";
    	   		if(!brandName.isEmpty())
    	   			qry += " AND storeBrand= '"+StringEscapeUtils.escapeSql(brandName)+"'";
    	   		if(!locality.isEmpty())
    	   			qry	+= " AND locality= '"+locality+"'";
    	   		if(!cityName.isEmpty())
    	   			qry	+= " AND city= '"+cityName+"'";
    	   		if(!country.isEmpty()) 
    	   			qry	+= " AND country= '"+country+"'";
    	   		if(!storeName.isEmpty())
    	   			qry	+= " AND storeName= '"+StringEscapeUtils.escapeSql(storeName)+"'";
    	   		 
    	   
    	   		if(sortType!=null && !sortType.isEmpty()){
    	   			sortType= sortType.equalsIgnoreCase("country") ?
    	   				 "country" :sortType.equalsIgnoreCase("locality") ? "locality" :sortType.equalsIgnoreCase("city") ?
    	   						 "city" :sortType.equalsIgnoreCase("storename") ? "storeName" :sortType.equalsIgnoreCase("brand") ? "storeBrand" :"";
    	   	   }
    	   		
    	   		qry +=(sortType!=null && !sortType.isEmpty())? " order by "+ sortType +" "+ sortOrder :"";	
    	   		
    	   		
    	   logger.info("====query  "+qry);
    	   List<OrganizationStores> orgStoList = executeQuery(qry);
    	  
    	   return orgStoList;
    	  
    		   
       }
       
       
  public OrganizationStores findByStoreId(Long storeId){
	  String qry = "FROM OrganizationStores WHERE storeId="+storeId;
	  List< OrganizationStores> orgStoreList =  getHibernateTemplate().find(qry);
	if(orgStoreList!=null)
	   return orgStoreList.get(0);
	return null;
}
   public List<OrganizationStores> findSubsidiaryBy(Long zoneId){
	   String qry = "FROM OrganizationStores WHERE storeId in(select store_id from zone_store where zone_id="+zoneId+")"; 
	   List<OrganizationStores> orgStoList = getHibernateTemplate().find(qry);
	   if(orgStoList.size()>0)
	   return orgStoList;
	   else
		   return null;
}
   public List<OrganizationStores> findSubsidaryBydomainIds(String domainIds){

	   String qry = "FROM OrganizationStores WHERE domainId IN("+domainIds+")"  ;
	   List<OrganizationStores> orgStoList = getHibernateTemplate().find(qry);
	   if(orgStoList.size()>0)
	   return orgStoList;
	   else
		   return null;
   }
   public List<OrganizationStores> findSubsidaryBydomainIds(Long domainIds){

	   String qry = "FROM OrganizationStores WHERE domainId IN("+domainIds+") " ;
	   List<OrganizationStores> orgStoList = getHibernateTemplate().find(qry);
	   if(orgStoList.size()>0)
	   return orgStoList;
	   else
		   return null;
   }
   public List<OrganizationStores> findStore(Long orgnizationId,String domainId){

   	//   String qry = "FROM OrganizationStores WHERE userOrganization = "+orgnizationId+" AND domainId in("+domainId+") group by subsidiaryId";
   	   String qry = "FROM OrganizationStores WHERE userOrganization = "+orgnizationId+" AND domainId in("+domainId+") group by subsidiaryId";
   	   logger.info(qry);
   	   List<OrganizationStores> orgStoList = getHibernateTemplate().find(qry);
   	   logger.info("list size  "+orgStoList.size());
   	   if(orgStoList.size()>0)
   	   return orgStoList;
   	   else
   		   return null;
      }
   
   public OrganizationStores findOrgByDomain(Long orgnizationId,Long domainId,String StoreNo){

	   String qry = "FROM OrganizationStores WHERE userOrganization = "+orgnizationId+"  AND domainId = "+domainId + 
			   " AND homeStoreId='"+StoreNo+"'";
	   logger.info("====query  "+qry);
	   List<OrganizationStores> orgStoList = getHibernateTemplate().find(qry);
	   if(orgStoList.size()>0)
	   return orgStoList.get(0);
	   else
		   return null;
   }
  
   

   public List<OrganizationStores> findStoreInquiryRequest(Long orgnizationId,Long domainId,
			String country,String city,String locality,String storeId,String storeName,String zipCode,
			String brand,String subId,String subName,String type,String order){
	  String qry="";
	  String subQry=""; 
	   subQry +=(country!=null && !country.isEmpty())?" AND country='"+country+"'" :"";	
	   subQry +=(city!=null && !city.isEmpty())?" AND city='"+city+"'" :"";	   
	   subQry +=(locality!=null && !locality.isEmpty())?" AND locality='"+locality+"'" :"";	
	   subQry +=(zipCode!=null && !zipCode.isEmpty())?" AND zipCode='"+zipCode+"'" :"";
	   subQry +=(brand!=null && !brand.isEmpty())?" AND storeBrand in (" + brand + ") " :"";	 
	   subQry +=(subId!=null && !subId.isEmpty())?" AND subsidiaryId='"+subId+"'" :"";	   
	   subQry +=(subName!=null && !subName.isEmpty())?" AND subsidiaryName='"+subName+"'" :"";	  
	   subQry +=(storeId!=null && !storeId.isEmpty())?" AND homeStoreId='"+storeId+"'" :"";
	   subQry +=(storeName!=null && !storeName.isEmpty())?" AND storeName like '%"+StringEscapeUtils.escapeSql(storeName)+"%'":"";
	   
	   
	   
	   
	   if(type!=null && !type.isEmpty()){
		 type= type.equalsIgnoreCase("country") ?
				 "country" :type.equalsIgnoreCase("locality") ? "locality" :type.equalsIgnoreCase("city") ?
						 "city" :type.equalsIgnoreCase("storename") ? "storeName" :type.equalsIgnoreCase("brand") ? "storeBrand" :"";
	   }
	   subQry +=(type!=null && !type.isEmpty())? " order by "+ type +" "+ order :"";
	   

	   qry="FROM OrganizationStores WHERE userOrganization = "+orgnizationId+"  AND domainId = "+domainId + 
	    subQry ;
	   logger.info("query===="+qry);

	  /* qry="FROM OrganizationStores WHERE userOrganization = "+orgnizationId +subQry;*/
	   List<OrganizationStores> orgStoList = getHibernateTemplate().find(qry);
	   if(orgStoList.size()>0)
	   return orgStoList;
	   else
		   return null;
	   
	   
   }
   public String findStoreNameByStoreId(Long orgnizationId,String homeStoreId){
	   String qry="";
	   qry="SELECT store_name from org_stores where org_id='"+orgnizationId+"' and home_store_id='"+homeStoreId+"'";
	   logger.info("query===="+qry);
	   if(jdbcTemplate.queryForList(qry).size() > 0)
		   return (String)jdbcTemplate.queryForList(qry).get(0).get("store_name");
	   else		   
		   return null;
   } 
   
   //APP-3873-->Fetching Store Name only if there is any Subsidiary ID present in the DB Table 
   public String findStoreNamesPerSubsidiaryIDAndStoreId(Long orgnizationId,String homeStoreId, String subsidiaryNumber){
	   String qry="";
	   qry="SELECT store_name from org_stores where org_id='"+orgnizationId+"' and home_store_id='"+homeStoreId+"' and subsidary_id='"+subsidiaryNumber+"' " ;
	   logger.info("query===="+qry);
	   logger.info(jdbcTemplate.queryForList(qry).size());
	   if(jdbcTemplate.queryForList(qry).size() > 0)
	   {
		 return (String)jdbcTemplate.queryForList(qry).get(0).get("store_name");
	   }
	   else		   
		   return null;
   } 
}

