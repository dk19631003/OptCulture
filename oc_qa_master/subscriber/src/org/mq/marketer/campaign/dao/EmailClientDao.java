package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.EmailClient;

@SuppressWarnings({ "unchecked", "serial","unused"})
public class EmailClientDao extends AbstractSpringDao {

    public EmailClientDao() {}
	
    public EmailClient find(Long id) {
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

/*    public void saveOrUpdate(EmailClient emailClient) {
        super.saveOrUpdate(emailClient);
    }

    public void delete(EmailClient emailClient) {
        super.delete(emailClient);
    }*/

    public List<EmailClient> findAll() {
        return super.findAll(EmailClient.class);
    }
    //App-3644
    public List<Long> getIdsByUserClient(String userClient, String emailClient) {
    	
    	String qry="select distinct ec.emailClientId from EmailClient ec "+ 
    			" WHERE  ec.emailClient='"+emailClient+"' AND  ec.userAgent IN("+userClient+")";
    	logger.info("getIdsByUserClient query::"+qry);
    	return getHibernateTemplate().find(qry);
    }
}

