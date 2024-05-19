package org.mq.marketer.campaign.dao;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mq.marketer.campaign.beans.DRSent;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.OrganizationZone;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class ZoneDao extends AbstractSpringDao implements Serializable{
	private JdbcTemplate jdbcTemplate;
	public JdbcTemplate getJdbcTemplate(){
		return jdbcTemplate;
		}
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {	
		this.jdbcTemplate = jdbcTemplate;
		}
	
	
	
	public List<OrganizationStores> getSubNameByOrgUnitId( Long orgnizationId,Long domain ){
		List<OrganizationStores> organizationStores = null;   
		  
		organizationStores = getHibernateTemplate().find("from OrganizationStores where domainId = "+  domain + " AND  userOrganization="+orgnizationId);
		     if(organizationStores!=null && organizationStores.size()==0)
		    	 return organizationStores;
		     return null;
		       
	  }
	public OrganizationZone getZoneNameByOrgUnitId(Long orgnizationId, String zonename){
		String qry = "FROM OrganizationZone WHERE domainId = "+orgnizationId+" AND zoneName= '"+zonename+"' AND deleteStatus=false";
		
		logger.info("zone name qry"+qry);
 	   List<OrganizationZone> zoneNameList = getHibernateTemplate().find(qry);
 	   if(zoneNameList!=null && zoneNameList.size()>0)
 	   return zoneNameList.get(0);
 	   else
 		   return null;
	}
	
	
	public List<OrganizationZone> getZoneDetailsBydomainId(String domainId,int start,int count){
		
		String qry="FROM OrganizationZone WHERE domainId IN("+domainId+") AND deleteStatus=false";
		logger.info("zone query "+qry);
		List<OrganizationZone> zoneList =executeQuery(qry, start, count);
	   if(zoneList!=null && zoneList.size()>0)
		return zoneList;
	   return null;
		
	}
	public OrganizationZone getSelectedZoneDetailsByZoneId(Long zoneId)
	{
		String qry="FROM OrganizationZone WHERE zoneId="+zoneId+" AND deleteStatus=false";
		List<OrganizationZone> selectedZoneList = getHibernateTemplate().find(qry);
		logger.info("==========selectedZoneList"+selectedZoneList);   
		if(selectedZoneList!=null && selectedZoneList.size()>0)
			   return selectedZoneList.get(0);
		   
		   return null;
	}
	
	public int getZoneCount(String domainId){
		String qry="FROM OrganizationZone WHERE domainId IN("+domainId+") AND deleteStatus=false";
		logger.info("zone query "+qry);
		List<OrganizationZone> zoneList = getHibernateTemplate().find(qry);
	   if(zoneList!=null && zoneList.size()>0)
		return zoneList.size();
	   return 0;
	}
	
	
	/*public List<OrganizationZone> getZoneStoresBydomainId(Long domainId)
	{
		String qry="FROM OrganizationZone WHERE domainId ="+domainId+"";
		List<OrganizationZone> selectedStoreList = getHibernateTemplate().find(qry);
		logger.info("==========selectedZoneList"+selectedStoreList);   
		if(selectedStoreList!=null && selectedStoreList.size()>0)
			   return selectedStoreList;
		   
		   return null;
	}
	*/
	
	public List<OrganizationZone> getZoneStoresBydomainId(String domainId)
	{
		String qry="FROM OrganizationZone WHERE domainId IN("+domainId+") AND deleteStatus=false order by zoneName asc";
		List<OrganizationZone> selectedStoreList = getHibernateTemplate().find(qry);
		logger.info("==========selectedZoneList"+selectedStoreList);   
		if(selectedStoreList!=null && selectedStoreList.size()>0)
			   return selectedStoreList;
		   
		   return null;
	}	
	public List<OrganizationZone> getAllZoneBydomainId(String domainId)
	{
		String qry="FROM OrganizationZone WHERE domainId IN("+domainId+")";
		List<OrganizationZone> selectedStoreList = getHibernateTemplate().find(qry);
		logger.info("==========selectedZoneList"+selectedStoreList);   
		if(selectedStoreList!=null && selectedStoreList.size()>0)
			   return selectedStoreList;
		   
		   return null;
	}	
	
	
	
	
	public List<OrganizationStores> getGroupOfDetailByZone(Long zoneId){
		
		if(zoneId==null )return null;
		List<OrganizationStores> storeList=null;
		StringBuilder sb=new StringBuilder();
		  String qry="Select store_id from zone_store where zone_id="+zoneId;
		Long storeId=null;
		 logger.info("qry "+qry);
		 List<Long> storeIdList= jdbcTemplate.query(qry, new RowMapper() {
				
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 logger.info(" Store id "+rs.getLong("store_id"));
				return  rs.getLong("store_id");
			 }
		 } );
		          
		 if(storeIdList!=null)
		  for(Long storeIds:storeIdList){
			  if(sb.length()>0)
					sb.append(",");
			storeId=  storeIds ;
			sb.append(storeId);
		  }
		 String st=sb.toString();
		/* if(st!=null)
		 st=st.substring(0,st.length()-1);
		 */
		String query ="FROM  OrganizationStores WHERE storeId in("+st +")";
		logger.info("query  "+query);
		storeList= getHibernateTemplate().find(query);
		if(storeList!=null && storeList.size()>0)
			return storeList;
		return null;
	}
public List<OrganizationStores> getAllSubAndStoreByZone(String  zoneId){
		
		List<OrganizationStores> storeList=null;
		StringBuilder sb=new StringBuilder();
		if(zoneId==null || zoneId.trim().isEmpty())return storeList;
		  String qry="Select store_id from zone_store where zone_id IN("+zoneId+")";
		Long storeId=null;
		 logger.info("qry "+qry);
		 List<Long> storeIdList= jdbcTemplate.query(qry, new RowMapper() {
				
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				return  rs.getLong("store_id");
			 }
		 } );
		          
		 if(storeIdList!=null)
		  for(Long storeIds:storeIdList){
			storeId=  storeIds ;
			sb.append(storeId);
			sb.append(",");
		  }
		 String st=sb.toString();
		 st=st.substring(0,st.length()-1);
		 
		String query ="FROM  OrganizationStores WHERE storeId in("+st +") order by subsidiaryId asc";
		logger.info("query  "+query);
		storeList= getHibernateTemplate().find(query);
		if(storeList!=null && storeList.size()>0)
			return storeList;
		return null;
	}


/*public Long getAllStoreByZone(String  zoneId){
	
	 try {
		String qry="Select store_id from zone_store where zone_id IN("+zoneId+")";
		Long storeId=null;
		 logger.info("qry "+qry);
		 List<Long> storeIdList= jdbcTemplate.query(qry, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				return  rs.getLong("store_id");
			 }
		 } );
		return null;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::" , e);
		return null;
	}
}*/



	public  OrganizationZone findBy(Long orgStoreId) {
		
		String query = " SELECT  DISTINCT u FROM OrganizationZone u JOIN u.stores ud WHERE ud IN("+orgStoreId+") AND u.deleteStatus=false"; 
		
		List<OrganizationZone> retList =  executeQuery(query);
		
		if(retList != null && retList.size()>0) return retList.get(0);
		
		else return null;
	}
	public  List<OrganizationZone> findByOrgID(Long orgStoreId) {//APP-2216
		
		String query = " SELECT  DISTINCT u FROM OrganizationZone u JOIN u.stores ud WHERE ud IN("+orgStoreId+") AND u.deleteStatus=false"; 
		
		List<OrganizationZone> retList =  executeQuery(query);
		
		if(retList != null) return retList;
		
		else return null;
	}
	
	
}


