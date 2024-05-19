package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.general.Constants;

@SuppressWarnings("unchecked")
public class MLCustomFieldsDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

    public MLCustomFieldsDaoForDML() {}

    /*public MLCustomFields find(Long id) {
        return (MLCustomFields) super.find(MLCustomFields.class, id);
    }*/

    public void saveOrUpdate(MLCustomFields mLCustomFields) {
        super.saveOrUpdate(mLCustomFields);
    }

    public void delete(MLCustomFields mLCustomFields) {
        super.delete(mLCustomFields);
    }

    /*public List findAll() {
        return super.findAll(MLCustomFields.class);
    }*/

   public List<MLCustomFields> findAllByList(MailingList mailingList) {
       return getHibernateTemplate().find("from MLCustomFields where mailingList = " + mailingList.getListId() + " order by fieldIndex ASC");
    }
    
    public void removeByList(MailingList mailingList){
    	List list = findAllByList(mailingList);
    	for(Object obj:list){
    		MLCustomFields mlCF = (MLCustomFields)obj;
    		delete(mlCF);
    	}
    }
    
    
    public void removeCustomFieldAndClearTheData(MLCustomFields mlCustomField) {
    	
    	// update customfield_data set cust_1=null where contact_id in (SELECT cid FROM contacts where list_id=523);
    	
    	String qryStr = "update CustomFieldData set cust"+mlCustomField.getFieldIndex()+" = null where contact in " +
		" (select contactId from Contacts where mailingList = " + mlCustomField.getMailingList().getListId() + " )";
    	
    	logger.info("====="+qryStr);
    	getHibernateTemplate().bulkUpdate(qryStr);
    	
		delete(mlCustomField);
    }
    
    /* public MLCustomFields findCFObjByName(MailingList mailingList,String name){
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
	
    public int findIndexByNameMl(MailingList mailingList,String name){
    	List l = getHibernateTemplate().find("select fieldIndex from MLCustomFields where mailingList = " + mailingList.getListId()  +" and custFieldName='"+ name +"' ");
    	try{
	    	if(l.size()>0){
	    		return ((Integer)l.get(0)).intValue();
	    	}
	    	else
	    		return -1;
    	}catch (Exception e) {
    		return -1;
		}
    }
    public String findDataType(MailingList mailingList,String name){
    	List l = getHibernateTemplate().find("select dataType from MLCustomFields where mailingList = " + mailingList.getListId() + " and custFieldName='"+ name +"' ");
    	if(l.size()>0)
    		return (String)l.get(0);
    	else
    		return null;
    	
    }
	
*/

}

