package org.mq.captiway.scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.MLCustomFields;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.dao.MLCustomFieldsDao;
import org.mq.captiway.scheduler.dao.AbstractSpringDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@SuppressWarnings("unchecked")
public class MLCustomFieldsDao extends AbstractSpringDao{
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
    
	
	private MLCustomFieldsDaoForDML mLCustomFieldsDaoForDML;
	
	public MLCustomFieldsDaoForDML getmLCustomFieldsDaoForDML() {
		return mLCustomFieldsDaoForDML;
	}

	public void setmLCustomFieldsDaoForDML(
			MLCustomFieldsDaoForDML mLCustomFieldsDaoForDML) {
		this.mLCustomFieldsDaoForDML = mLCustomFieldsDaoForDML;
	}

	private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public MLCustomFieldsDao() {}

    public MLCustomFields find(Long id) {
        return (MLCustomFields) super.find(MLCustomFields.class, id);
    }

   /* public void saveOrUpdate(MLCustomFields mLCustomFields) {
        super.saveOrUpdate(mLCustomFields);
    }

    public void delete(MLCustomFields mLCustomFields) {
        super.delete(mLCustomFields);
    }*/

    public List findAll() {
        return super.findAll(MLCustomFields.class);
    }

    public List<MLCustomFields> findAllByList(MailingList mailingList) {
       return getHibernateTemplate().find("from MLCustomFields where mailingList = " + mailingList.getListId() + " order by fieldIndex ASC");
    }
    
    public void removeByList(MailingList mailingList){
    	List list = findAllByList(mailingList);
    	for(Object obj:list){
    		MLCustomFields mlCF = (MLCustomFields)obj;
    		mLCustomFieldsDaoForDML.delete(mlCF);
    	}
    }
    /**
     * @param mailingList
     * @return
     * added for eventTrigger
     */
    public int hasCustomFields(MailingList mailingList) {
        return jdbcTemplate.queryForInt(" SELECT count(*) from ml_customFileds where list_id = " + mailingList.getListId() + " ");
     }
    
   /* public void removeCustomFieldAndClearTheData(MLCustomFields mlCustomField) {
    	
    	// update customfield_data set cust_1=null where contact_id in (SELECT cid FROM contacts where list_id=523);
    	
    	String qryStr = "update CustomFieldData set cust"+mlCustomField.getFieldIndex()+" = null where contact in " +
		" (select contactId from Contacts where mailingList = " + mlCustomField.getMailingList().getListId() + " )";
    	
    	if(logger.isDebugEnabled()) logger.debug("====="+qryStr);
    	getHibernateTemplate().bulkUpdate(qryStr);
    	
		delete(mlCustomField);
    }*/
    
    public MLCustomFields findCFObjByName(MailingList mailingList,String name){
    	List l = getHibernateTemplate().find("from MLCustomFields where mailingList = " + mailingList.getListId()  +" and custFieldName='"+ name +"' ");
    	if(l.size()>0)
    		return (MLCustomFields)l.get(0);
    	else
    		return null;
    }
   
    
    
    public String findCFByNameMl(MailingList mailingList,String name){
    	List l = getHibernateTemplate().find("select selectedField from MLCustomFields where mailingList = " + mailingList.getListId()  +" and custFieldName='"+ name +"' ");
    	if(l.size()>0)
    		return (String)l.get(0);
    	else
    		return null;
    }
	
    /**
     * 
     * @param mailingList
     * @param name
     * @return
     * 
     * this method is added for EventTrigger to get column index 
     * of a custom field based on its name 
     */
    public int findIndexByNameMl(MailingList mailingList,String customFieldName){ // added for EventTrigger
    	
    	String queryStr = "select fieldIndex from MLCustomFields where mailingList = " + mailingList.getListId()+"" +
    			" and custFieldName LIKE '"+ customFieldName +"' ";
    	
    	if(logger.isDebugEnabled()) logger.debug("cust field query is "+queryStr);
    	List l = getHibernateTemplate().find(queryStr);
    	try{
	    	if(l.size()>0){
	    		return ((Integer)l.get(0)).intValue();
	    	}
	    	else
	    		return -1;
    	}catch (Exception e) {
    		return -1;
		}
    } // findIndexByNameMl
    
    /**
     * Added for EventTrigger
     * 
     * @param srcMl
     * @param destMl
     * 
     * ml1 is source
     * ml2 is destination ML obj reference
     * gets all the common customfiled name and indices for given mailings lists 
     */

    public List<Object[]> findCommonCustFieldsForMls(String eventTriggerMlStr,MailingList copyToMlList){

    	try {
    		if(logger.isInfoEnabled()) logger.info("inside CFDao");
    		List<Object[]> customFieldsList;
    		String queryStr = " SELECT DISTINCT ml1,ml2.mailingList,ml2.fieldIndex FROM MLCustomFields ml1, MLCustomFields ml2 " +
    		" WHERE ml1.mailingList IN ("+eventTriggerMlStr+") " +
    		" AND ml2.mailingList = "+copyToMlList.getListId()+" " +
    		" AND ml1.mailingList != ml2.mailingList " +
    		" AND ml1.dataType LIKE ml2.dataType " +
    		" AND ml1.custFieldName LIKE ml2.custFieldName ";

    		if(logger.isInfoEnabled()) logger.info("query is "+queryStr);
    		customFieldsList = getHibernateTemplate().find(queryStr);

    		if(logger.isDebugEnabled()) logger.debug("Size of list  returning = "+customFieldsList.size());

    		if(logger.isInfoEnabled()) logger.info(" customFieldsList "+customFieldsList);

    		return customFieldsList;
    	}
    	catch(Exception e){

    		if(logger.isErrorEnabled()) logger.error("**Exception ",e);
    		return null;
    	}
    } // findCommonCustFieldsForMls
    
    public String findDataType(MailingList mailingList,String name){
    	List l = getHibernateTemplate().find("select dataType from MLCustomFields where mailingList = " + mailingList.getListId() + " and custFieldName='"+ name +"' ");
    	if(l.size()>0)
    		return (String)l.get(0);
    	else
    		return null;
    	
    }
	
}
