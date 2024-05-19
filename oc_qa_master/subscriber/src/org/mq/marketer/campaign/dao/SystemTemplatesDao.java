

package org.mq.marketer.campaign.dao;


import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SystemTemplates;
import org.mq.marketer.campaign.general.Constants;

@SuppressWarnings({ "unchecked"})
public class SystemTemplatesDao extends AbstractSpringDao {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
    public SystemTemplatesDao() {}


    public SystemTemplates find(Long id) {
        return (SystemTemplates) super.find(SystemTemplates.class, id);
    }

    /*public void saveOrUpdate(SystemTemplates systemTemplates) {
        super.saveOrUpdate(systemTemplates);
    }

    public void delete(SystemTemplates systemTemplates) {
        super.delete(systemTemplates);
    }*/

    public List findAll() {
        return super.findAll(SystemTemplates.class);
    }

    public SystemTemplates findByName(String name) {
        List list = getHibernateTemplate().find("from SystemTemplates where name = '" + name + "' ");
		SystemTemplates st = null;
        if (list.size() > 0) {
            st = (SystemTemplates)list.get(0);
        }
        return st; 
    }
    public List findByCategoryName(String category){
    	List list = getHibernateTemplate().find("from SystemTemplates where templateCategory.categoryName= '"+category +"'");
    	return list;
    }
    public List findDivisions(String name,String categoryName){
    	logger.debug("category name : " + categoryName);
    	try{
    	return getHibernateTemplate().find("select divisions from SystemTemplates where dirName= '"+name +"'  and templateCategory.dirName = '" + categoryName + "'");
    	}catch(Exception e){
    		logger.error(" ** Exception  : " +e+ " **");
    		return null;
    	}
    	
    	
    }
    public SystemTemplates findByNameAndCategory(String name,String categoryName){
    	List list = getHibernateTemplate().find("from SystemTemplates where dirName = '" + name + "' and templateCategory.dirName = '" + categoryName + "'");
		SystemTemplates st = null;
        if (list.size() > 0) {
            st = (SystemTemplates)list.get(0);
        }
        return st; 
    }
    
}
