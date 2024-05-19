package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.mq.captiway.scheduler.beans.EmailClient;

@SuppressWarnings({ "unchecked"})
public class EmailClientDaoForDML extends AbstractSpringDaoForDML {
    public EmailClientDaoForDML() {}
	
   /* public EmailClient find(Long id) {
        return (EmailClient) super.find(EmailClient.class, id);
    }*/

    public void saveOrUpdate(EmailClient emailClient) {
        super.saveOrUpdate(emailClient);
    }

    public void delete(EmailClient emailClient) {
        super.delete(emailClient);
    }

   /* public List<EmailClient> findAll() {
        return super.findAll(EmailClient.class);
    }*/
}

