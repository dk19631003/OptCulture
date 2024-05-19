package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.mail.event.FolderListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
//import org.mq.marketer.campaign.beans.Account;
import org.mq.marketer.campaign.beans.MyFolders;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.OCConstants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


@SuppressWarnings({ "unchecked", "serial","unused"})

public class MyFoldersDao extends AbstractSpringDao {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public MyFolders find(Long id) {
		return ( MyFolders) super.find(MyFolders.class, id);
	}

	public List<MyFolders> findByOrgId(Long orgId,String type) throws Exception{
		String qry="FROM MyFolders  WHERE orgId="+orgId+" and type='"+type+"' order by folderName";
		return executeQuery(qry);
		
	}
	
	public List<MyFolders> findByOrgIdOrderByFolderNames(Long orgId,String type) throws Exception{
		String qry="FROM MyFolders  WHERE orgId="+orgId+" and type='"+type+"' order by folderName";
		return executeQuery(qry);
		
	}
	
	public List<MyFolders> findPUFolders(String myFolderIds){
		logger.info("myFolderIdsssss :"+myFolderIds);
		String qry="FROM MyFolders  WHERE folderId in("+myFolderIds+")";
		return executeQuery(qry);

	}

	
	public List<MyFolders> findUserFolders(Users currentUser) throws Exception{
		
		List<Object[]> list = null;
	try{
		
		String qry="FROM MyFolders  WHERE createdBy="+currentUser.getUserId();
		return executeQuery(qry);
		
		/*commented by venkata 
		 * String sql= "select distinct mf.folder_id as ids from my_folders mf, digital_receipt_my_templates drmt, shared_zone sz,"
				+ " shared_to_users su where drmt.org_id="+currentUser.getUserOrganization().getUserOrgId()+" and mf.type='"+type+"' and mf.folder_name=drmt.folder_name and mf.created_by=drmt.user_id"
				+ " and mf.created_by=sz.shared_by and su.user_id="+currentUser.getUserId()+" and drmt.my_template_id=sz.shared_item and drmt.user_id=sz.shared_by  and su.shared_zone_id= sz.shared_zone_id";
		logger.info("query  "+sql);
		List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sql);
		
		return list1;*/
	}catch (Exception e) {
		logger.error("Exception :: ",e);
	return null;
	}
	}
	
	
	public List<MyFolders> findUserOrgLevelFolders(Long orgID) throws Exception{
		
		
	try{
		
		String qry="FROM MyFolders  WHERE orgId="+orgID;
		return executeQuery(qry);
		
		/*commented by venkata 
		 * String sql= "select distinct mf.folder_id as ids from my_folders mf, digital_receipt_my_templates drmt, shared_zone sz,"
				+ " shared_to_users su where drmt.org_id="+currentUser.getUserOrganization().getUserOrgId()+" and mf.type='"+type+"' and mf.folder_name=drmt.folder_name and mf.created_by=drmt.user_id"
				+ " and mf.created_by=sz.shared_by and su.user_id="+currentUser.getUserId()+" and drmt.my_template_id=sz.shared_item and drmt.user_id=sz.shared_by  and su.shared_zone_id= sz.shared_zone_id";
		logger.info("query  "+sql);
		List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sql);
		
		return list1;*/
	}catch (Exception e) {
		logger.error("Exception :: ",e);
	return null;
	}
	}
	
	
	public List<Map<String, Object>> findPowerUserFolders(Users currentUser,String type) throws Exception{
		
		List<Object[]> list = null;
	try{
		
		/*//String qry1="select distinct mf.folderId from MyFolders mf, DigitalReceiptMyTemplate drmt, SharedZone sz, ";

			int i = getJdbcTemplate().queryForInt("select count(mf.folder_id) from my_folders mf, digital_receipt_my_templates drmt, shared_zone sz,"
					+ " shared_to_users su where mf.type='"+type+"' and mf.folder_name=drmt.folder_name and mf.created_by=drmt.user_id"
					+ " and mf.created_by=sz.shared_by and su.user_id="+currentUser.getUserId()+" and drmt.my_template_id=sz.shared_item and drmt.user_id=sz.shared_by");

			String myFolderIds= "";
			if(i==1) {
			String sql= "select distinct mf.folder_id from my_folders mf, digital_receipt_my_templates drmt, shared_zone sz,"
					+ " shared_to_users su where mf.type='Drag_Drop_DRTEMPLATES' and mf.folder_name=drmt.folder_name and mf.created_by=drmt.user_id"
					+ " and mf.created_by=sz.shared_by and su.user_id="+currentUser.getUserId()+" and drmt.my_template_id=sz.shared_item and drmt.user_id=sz.shared_by";
			
			//List<MyFolders> listMF= executeQuery(sql);
			
			if(listMF!=null && listMF.size()>0){
				for(MyFolders myFolderObj : listMF ){
					myFolderIds = myFolderObj.getFolderId()+",";
					break;
				}
				for(MyFolders myFolderObj : listMF ){
					myFolderIds = myFolderIds+","+myFolderObj.getFolderId()+",";
				}
				if(myFolderIds.length()>1) {
					myFolderIds= myFolderIds.substring(0,myFolderIds.length()-1);
				}
			}
			
				Long folderIds = (Long)getJdbcTemplate().queryForObject(sql, new Object[] { userId }, Long.class);
				logger.info(""+folderIds);
				return folderIds;
				
				//String sql1=""
			
			}
						String qry="FROM MyFolders  WHERE folderId in("+myFolderIds+")";
				return executeQuery(qry);
			
*/		
		
		String sql= "select distinct mf.folder_id as ids from my_folders mf, digital_receipt_my_templates drmt, shared_zone sz,"
				+ " shared_to_users su where drmt.org_id="+currentUser.getUserOrganization().getUserOrgId()+" and mf.type='"+type+"' and mf.folder_name=drmt.folder_name and mf.created_by=drmt.user_id"
				+ " and mf.created_by=sz.shared_by and su.user_id="+currentUser.getUserId()+" and drmt.my_template_id=sz.shared_item and drmt.user_id=sz.shared_by  and su.shared_zone_id= sz.shared_zone_id";
		//jdbcTemplate.queryForObject(sql,Long.class);
		/*
		list = jdbcTemplate.query(sql, new RowMapper(){
			
			@Override
			public Object mapRow(ResultSet rs, int arg1)
					throws SQLException {
				Object obj [] = new Object[1];
				obj[0] = rs.getString("ids");
				return obj;
			}
			
		});
		return list;*/
		logger.info("query  "+sql);
		List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sql);
		
		return list1;
	}catch (Exception e) {
		logger.error("Exception :: ",e);
	return null;
	}
	}
	
	
	/*public List<MyFolders> findByOrgIdAndTypes(Long orgId,String dDEtype,String lEType) throws Exception{
		String qry="FROM MyFolders  WHERE orgId="+orgId+" and (type='"+dDEtype+"' or type='"+lEType+"')";
		return executeQuery(qry);
	}*/
	
	public List<MyFolders> findByOrgIdAndTypes(Long orgId,String dDEtype) throws Exception{
		String qry="FROM MyFolders  WHERE orgId="+orgId+" and (type='"+dDEtype+"')";
		return executeQuery(qry);
	}
	
	
	public List<MyFolders> findByOrgIdFolderName(Long orgId,String type,String folderName) throws Exception{
		String qry="FROM MyFolders WHERE orgId="+orgId+" and type='"+type+"'and folderName='"+folderName+"'";
		return executeQuery(qry);
		
	}
	
	
	
	
	
/*	public List<MyFolders> findAllUserFolders(Account accountId) throws Exception{
		String qry="FROM MyFolders  WHERE  account="+accountId.getAccountId();
		return executeQuery(qry);
		
	}
	
	
	public MyFolders findFolderByName(String folderName , Account accountId , String type) throws Exception{
		MyFolders myFolders = null;
		String qry="FROM MyFolders  WHERE  folderName='"+folderName+"' AND account="+accountId.getAccountId()+" AND type='"+type+"'" ;
		List<MyFolders> folderList= (List<MyFolders>)getHibernateTemplate().find(qry);
		if(folderList.size() > 0 || !folderList.isEmpty()){
			myFolders= folderList.get(0);
		}
		return myFolders;
	}*/
	
/*	 public int updateFolderName(Account accountId, String newfolder, String oldfolder) throws Exception {
	    	
	    	try {
				String qry = "UPDATE MyFolders SET folderName ='"+newfolder+"' WHERE   account = "+accountId.getAccountId()+" " +
						" AND folderName ='"+oldfolder+"' ";
				
				return executeUpdate(qry);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				return 0;
			}
	    	
	    	
	    }
*/
	 
/*	 public void deleteByFolderName(Account accountId, Long  folderId) throws Exception {
			
			try {
				executeUpdate("DELETE FROM MyFolders WHERE account= "+accountId.getAccountId()+" AND folderId ="+folderId.longValue());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
			}
		}
*/
/*public List<MyFolders> findAllBy(String typeStr, String valuStr, Long userId ,String type) throws Exception{
	
	List<MyFolders> folderlist= null;
	
	String field = null;
	if(typeStr.equals(Constants.USER_PREVILAGE_FILTER_ATTRIBUTE_VALUE_ACCOUNT)) field = "account";
	else if(typeStr.equals(Constants.USER_PREVILAGE_FILTER_ATTRIBUTE_VALUE_OWNER)) field = "createdBy";	
	else if(typeStr.equals(Constants.USER_PREVILAGE_FILTER_ATTRIBUTE_VALUE_SHAREDBY)) field = "folderId";
	else if(typeStr.equals(Constants.USER_PREVILAGE_FILTER_ATTRIBUTE_VALUE_SHAREDTO)) field = "folderId";
	
	String qry= "FROM MyFolders WHERE  type='"+type+"' AND "+field+" IN("+valuStr+")";
	folderlist = executeQuery(qry);
	
	return folderlist;
	
	
}*/
}
