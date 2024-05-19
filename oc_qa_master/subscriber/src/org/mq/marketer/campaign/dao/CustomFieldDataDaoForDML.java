package org.mq.marketer.campaign.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.CustomFieldData;


@SuppressWarnings("unchecked")
public class CustomFieldDataDaoForDML extends AbstractSpringDaoForDML{

    public CustomFieldDataDaoForDML(){}
    public SessionFactory sessionFactory;
    /*public CustomFieldData find(Long id){
        return (CustomFieldData) super.find(CustomFieldData.class, id);
    }*/

    public void saveOrUpdate(CustomFieldData customFieldData){
        super.saveOrUpdate(customFieldData);
    }

    public void delete(CustomFieldData customFieldData){
        super.delete(customFieldData);
    }

  /*  public List findAll(){
        return super.findAll(CustomFieldData.class);
    }*/
	
	/*public CustomFieldData getByContact(long contactId) {
		List list = getHibernateTemplate().find("from CustomFieldData where contact = " + contactId);
		if(list.size()>0)
			return (CustomFieldData)list.get(0);
		else
			return null;
	}
    
    public List getCustomFieldData(String cflist, Contacts contact){
		List list = getHibernateTemplate().find("select " + cflist + " from CustomFieldData where contact = " + contact.getContactId());
		return list;
	}*/
	
	public boolean updateCustomField(String cflist,Contacts contact) {
		int res = getHibernateTemplate().bulkUpdate("update CustomFieldData set " + cflist + " where contact.contactId = '" + contact.getContactId() + "'");
		if(res>0) {
			return true;
		} else
			return false;
	}
	

}
