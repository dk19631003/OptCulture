

package org.mq.marketer.campaign.dao;


import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.TemplateCategory;
import org.mq.marketer.campaign.general.Constants;

@SuppressWarnings({ "unchecked"})
public class TemplateCategoryDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
    
    public TemplateCategoryDaoForDML() {
    }


   /* public TemplateCategory find(Long id) {
        return (TemplateCategory) super.find(TemplateCategory.class, id);
    }*/

    public void saveOrUpdate(TemplateCategory templateCategory) {
        super.saveOrUpdate(templateCategory);
    }

    public void delete(TemplateCategory templateCategory) {
        super.delete(templateCategory);
    }

 /*   public List findAll() {
        return super.findAll(TemplateCategory.class);
    }
    public List findCategories(){
    	return getHibernateTemplate().find("from TemplateCategory where categoryName not in ('plainEditor','Others') order by cratedDate");
    }
    
    public TemplateCategory findByName(String name) {
        List list = getHibernateTemplate().find("from TemplateCategory where categoryName = '" + name + "' ");
        TemplateCategory ct = null;
        if (list.size() > 0) {
            ct = (TemplateCategory)list.get(0);
        }
        return ct; 
    }
    */
    
}
