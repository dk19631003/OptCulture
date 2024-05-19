
package org.mq.captiway.scheduler.dao;

import org.mq.captiway.scheduler.beans.EmailContent;

public class EmailContentDaoForDML extends AbstractSpringDaoForDML {

    public EmailContentDaoForDML() {}
	
    /*public EmailContent find(Long contentId) {
        return (EmailContent) super.find(EmailContent.class, contentId);
    }*/

    public void saveOrUpdate(EmailContent emailContent) {
        super.saveOrUpdate(emailContent);
    }
    
   
}


