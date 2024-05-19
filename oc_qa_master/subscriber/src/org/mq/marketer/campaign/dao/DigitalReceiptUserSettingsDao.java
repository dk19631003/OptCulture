package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.DigitalReceiptUserSettings;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class DigitalReceiptUserSettingsDao extends AbstractSpringDao {
	
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	JdbcTemplate jdbcTemplate;
	public DigitalReceiptUserSettingsDao(){
		
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	 /* public void saveOrUpdate(DigitalReceiptUserSettings digitalReceiptUserSettings){
	        super.saveOrUpdate(digitalReceiptUserSettings);
	    }
	
	
	 public void addDigiRecptUserSelectedTemplate(Long userId,String templateName) {
		 templateName = StringEscapeUtils.escapeSql(templateName);
	    	String sql ="insert into digital_receipt_user_settings(user_id,selected_template_name) values ("+ userId +",'"+ templateName  +"')";
	    	jdbcTemplate.execute(sql);
	 } */
	    
	 public String findUserSelectedTemplate(Long userId) {
	   try {
	    		logger.info("************** User id is  : "+ userId);
	    		int i = getJdbcTemplate().queryForInt("select count(*) from digital_receipt_user_settings where user_id="+userId);
	    		if(i==1) {
	    			String sql = "select selected_template_name from digital_receipt_user_settings where user_id=?" ;
	    			String name = (String)getJdbcTemplate().queryForObject(sql, new Object[] { userId }, String.class);
	    			logger.info(""+name);
	    			return name;
	    		} else return null;	
	    	} catch(Exception e) {
	    		return null;
	    	}
	    }
	    
	   /* public int updateDigiUserSettings(Long userId,String templateName) {
	    	 templateName = StringEscapeUtils.escapeSql(templateName);
	    	String sql ="update digital_receipt_user_settings  set selected_template_name='" +templateName  +"' where user_id=" + userId +" ";
	    	return jdbcTemplate.update(sql);
	    }
	*/
	/*public String findByUserId(Long UserId){
		String sql="select selected_template_name from digital_receipt_user_settings where user_id=?";
		getHibernateTemplate().find(sql);
		return null;
	}*/
	    public DigitalReceiptUserSettings findByUserId(Long userId){
	    	logger.info("User Id  >>"+userId);
	    	List<DigitalReceiptUserSettings> list= getHibernateTemplate().find("FROM DigitalReceiptUserSettings WHERE userId="+userId);
	    	logger.info("list >>"+list.size());
	    	if(list != null && list.size() > 0 ){
	    		return list.get(0);
	    	}
	    	return null;
	    }
	   
	    
	    
	    public boolean findIsUserEnabled(Long userId) {
	    	
	    	List<Boolean> list= getHibernateTemplate().find(" SELECT enabled FROM DigitalReceiptUserSettings WHERE userId="+userId);
	    	//logger.info("list >>"+list.size());
	    	if(list != null && list.size() > 0 ){
	    		return (list.get(0)).booleanValue();
	    	}
	    	return false;
	    	
	    }
	    public boolean findIsSMSEnabled(Long userId) {
	    	
	    	List<Boolean> list= getHibernateTemplate().find(" SELECT smsEnabled FROM DigitalReceiptUserSettings WHERE userId="+userId);
	    	if(list != null && list.size() > 0 ){
	    		return (list.get(0)).booleanValue();
	    	}
	    	return false;
	    }
	    
	    //unused
	    public int findTemplateByName(Long userId, String name){
	    	//name = StringEscapeUtils.escapeSql(name);
	    	List<DigitalReceiptUserSettings> list=null;
	    	String qry = "SELECT id FROM DigitalReceiptUserSettings WHERE userId = "+userId+"  AND selectedTemplateName like'%:"+name+"' ";
	    	logger.info("query ==="+qry);
	    	list=getHibernateTemplate().find(qry);
	    	if(list!=null && list.size()>0)
	    	 return list.size();
			return 0;
	    }
	    
	    //unused
	    public String findUserSelTemplate(Long templateId ) {
	 	   try {
	 	    		
	 	    			String sql = "select selected_template_name from digital_receipt_user_settings where id=?" ;
	 	    			String name = (String)getJdbcTemplate().queryForObject(sql, new Object[] { templateId }, String.class);
	 	    			logger.info(""+name);
	 	    			return name;
	 	    		
	 	    	} catch(Exception e) {
	 	    		return null;
	 	    	}
	 	    }
	    
	    
	    public boolean isTemplateConfigured(Long templateId) {
	 	   try {
	 	    	List<DigitalReceiptUserSettings> list= getHibernateTemplate().find("FROM DigitalReceiptUserSettings WHERE myTemplateId="+templateId);
	 	    	logger.info("list >>"+list.size());
	 	    	/*if(list != null && list.size() > 0 ){
	 	    		return list;
	 	    	}*/
	 	    	if(list.size()>0)
	 	    		return true;
	 	    	else return false;	
	 	    	} catch(Exception e) {
	 	    		return false;
	 	    	}
	 	    } 
	    
	    public boolean isZoneConfigured(long zoneId){
	    	try{
	    		String query="FROM DigitalReceiptUserSettings WHERE zoneId="+zoneId;
	    		List<DigitalReceiptUserSettings> list= getHibernateTemplate().find(query);
	    		if(list.size()>0)
	    			return true;
	    		else return false;
	    	}
	    	catch(Exception e) {
	    		return false;
	    	}
	    }
	    
	    
	    public DigitalReceiptUserSettings findUserSelectedTemplateBy(Long userId, Long zoneId) {
	    	try {

	    		// String subQry = SBSNo != null && !SBSNo.isEmpty() ? (" AND ((SBSNo='"+SBSNo+"' AND StoreNo IS NULL) OR (SBSNo='"+SBSNo+"'"+ (storeNo != null ? (" AND StoreNo='"+storeNo+"'))") : ("))") ) ) : "";


	    		//subQry += storeNo != null && !storeNo.isEmpty() ? (" AND storeNo='"+storeNo+"'") : "";
	    		String qry = "FROM DigitalReceiptUserSettings WHERE userId="+userId +" AND zoneId="+zoneId+" AND myTemplateId is not null ";
	    		logger.info("************** User id is  : "+ userId + "   subquery...."+qry);

	    		List<DigitalReceiptUserSettings> list= getHibernateTemplate().find(qry);
	    		logger.info("list >>"+list.size());
	    		if(list != null && list.size() > 0 ){
	    			return list.get(0);
	    		}
	    		return null;


	    	} catch(Exception e) {
	    		logger.error("Exception daoooo",e);
	    		return null;
	    	}
	    }


		 public DigitalReceiptUserSettings findUserSelectedTemplateByZone(Long userId, String storeNo, Long zoneId) {
			   try {
				   
				   //String subQry = zoneId != null ? (" AND ((zoneId="+zoneId+" AND StoreNo IS NULL) OR (zoneId="+zoneId+ (storeNo != null ? (" AND StoreNo='"+storeNo+"'))") : ("))") ) ) : "";
				  // String subQry = zoneId != null ? (" AND ((zoneId="+zoneId+" AND StoreNo IS NULL) OR (zoneId="+zoneId+ (storeNo != null ? (" AND StoreNo='"+storeNo+"'))") : ("))") ) ) : "";
	 
				   
				   String subQry = zoneId != null ? " AND zoneId="+zoneId : "";

						   
						   //subQry += storeNo != null && !storeNo.isEmpty() ? (" AND storeNo='"+storeNo+"'") : "";
			    		String qry = "FROM DigitalReceiptUserSettings WHERE userId="+userId +" AND myTemplateId is not null "+ subQry;
			    		logger.info("************** User id is  : "+ userId + "   subquery...."+qry);
			    		
			    		List<DigitalReceiptUserSettings> list= getHibernateTemplate().find(qry);
				    	logger.info("list >>"+list.size());
				    	if(list != null && list.size() > 0 ){
				    		return list.get(0);
				    	}
				    	return null;
				    
			    		
			    	} catch(Exception e) {
			    		logger.error("Exception daoooo",e);
			    		return null;
			    	}
			    }
		 
		 public List<DigitalReceiptUserSettings> getTemplatesByUserId(Long userId,int startIndx,int endIndx){
			 if(userId==null)return null;
			 //String query="FROM DigitalReceiptUserSettings WHERE userId ="+userId+" AND zoneId is not null";
			 String query="SELECT drus FROM DigitalReceiptUserSettings drus, OrganizationZone zone WHERE drus.zoneId=zone.zoneId and drus.userId="+userId+" and zone.deleteStatus=false and drus.zoneId is not null";
			 return executeQuery(query, startIndx, endIndx);
		 }  
		/*public int getTotalCountBy(Long userId){
			List<DigitalReceiptUserSettings> list= executeQuery("FROM DigitalReceiptUserSettings DRUS Join  WHERE userId ="+userId+" AND zoneId is not null");
			if(list!=null && list.size()>0)
				return list.size();
			return 0;
		}*/
		                 

		public int getTotalCountBy(Long userId){

			//String query = "select * from digital_receipt_user_settings drus join org_zones zone on drus.zone_id=zone.zone_id where drus.user_id="+userId+" and zone.delete_status=false and drus.zone_id is not null;"; 
			String query = "SELECT drus FROM DigitalReceiptUserSettings drus, OrganizationZone zone WHERE drus.zoneId=zone.zoneId and drus.userId="+userId+" and zone.deleteStatus=false and drus.zoneId is not null";
			List<DigitalReceiptUserSettings> list = executeQuery(query);
			///List<DigitalReceiptUserSettings> list= getHibernateTemplate().find("FROM DigitalReceiptUserSettings WHERE userId ="+userId+" AND zoneId is not null");
			if(list!=null && list.size()>0)
				return list.size();
			return 0;
		}
		 
		
		public DigitalReceiptUserSettings findUserSelectedTemplate(Long userId, String storeNo, String SBSNo) {
			   try {
				   
				   String subQry = SBSNo != null && !SBSNo.isEmpty() ? (" AND ((SBSNo='"+SBSNo+"' AND StoreNo IS NULL) OR (SBSNo='"+SBSNo+"'"+ (storeNo != null ? (" AND StoreNo='"+storeNo+"'))") : ("))") ) ) : "";
						 
						   
						   //subQry += storeNo != null && !storeNo.isEmpty() ? (" AND storeNo='"+storeNo+"'") : "";
			    		String qry = "FROM DigitalReceiptUserSettings WHERE userId="+userId +" AND myTemplateId is not null "+ subQry;
			    		logger.info("************** User id is  : "+ userId + "   subquery...."+qry);
			    		
			    		List<DigitalReceiptUserSettings> list= getHibernateTemplate().find(qry);
				    	logger.info("list >>"+list.size());
				    	if(list != null && list.size() > 0 ){
				    		return list.get(0);
				    	}
				    	return null;
				    
			    		
			    	} catch(Exception e) {
			    		logger.error("Exception daoooo",e);
			    		return null;
			    	}
			    }
		
		
		public DigitalReceiptUserSettings findUserSelectedTemplateByUserId(Long userId) {
			try {

				String qry = "FROM DigitalReceiptUserSettings WHERE userId="+userId;
				logger.info("************** User id is  : "+ userId + "   query...."+qry);

				List<DigitalReceiptUserSettings> list= getHibernateTemplate().find(qry);
				logger.info("list >>"+list.size());
				if(list != null && list.size() > 0 ){
					return list.get(0);
				}
				return null;


			} catch(Exception e) {
				logger.error("Exception dao",e);
				return null;
			}
		}
		 
	    /*public void setNewTemplateName(long userId, String newTemplateName, String selectedTemplate){
	    	 newTemplateName = StringEscapeUtils.escapeSql(newTemplateName);
			  selectedTemplate = StringEscapeUtils.escapeSql(selectedTemplate);

					String query = "Update DigitalReceiptUserSettings set selectedTemplateName='MY_TEMPLATE:"+newTemplateName+"' WHERE selectedTemplateName='"+selectedTemplate+"' AND userId="+userId;
					int count= executeUpdate(query);
				
	    		 
	    	 }//setNewTemplateName()*/
	   }
