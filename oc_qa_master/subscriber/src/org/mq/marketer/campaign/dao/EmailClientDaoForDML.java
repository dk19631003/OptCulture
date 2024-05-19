package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.EmailClient;

@SuppressWarnings({ "unchecked", "serial","unused"})
public class EmailClientDaoForDML extends AbstractSpringDaoForDML {

    public EmailClientDaoForDML() {}
	
/*    public EmailClient find(Long id) {
        return (EmailClient) super.find(EmailClient.class, id);
    }

    public EmailClient findById(Long id) {
    	EmailClient emailClient = null;
		try {
			List list = getHibernateTemplate().find(
					"from EmailClient where id = " + id );
			if (list.size() > 0) {
				emailClient = (EmailClient) list.get(0);
			}
		} catch (Exception e) {
			logger.error(" ** Exception : ", e ); 
		}
		return emailClient;
        //return (EmailClient) super.find(EmailClient.class, id);
    }
*/
    public void saveOrUpdate(EmailClient emailClient) {
        super.saveOrUpdate(emailClient);
    }

    public void delete(EmailClient emailClient) {
        super.delete(emailClient);
    }
/*
    public List<EmailClient> findAll() {
        return super.findAll(EmailClient.class);
    }*/
}

