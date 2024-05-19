package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.ErrorLog;

@SuppressWarnings({ "unchecked", "serial","unused"})
public class ErrorLogDaoForDML extends AbstractSpringDaoForDML {

    public ErrorLogDaoForDML() {}
	
   /* public ErrorLog find(Long id) {
        return (ErrorLog) super.find(ErrorLog.class, id);
    }*/

    public void saveOrUpdate(ErrorLog errorLog) {
        super.saveOrUpdate(errorLog);
    }

    public void delete(ErrorLog errorLog) {
        super.delete(errorLog);
    }

    /*public List<ErrorLog> findAll() {
        return super.findAll(ErrorLog.class);
    }*/
}

