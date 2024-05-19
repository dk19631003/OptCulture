package org.mq.captiway.scheduler.dao;

import java.util.Date;
import java.util.List;

import org.mq.captiway.scheduler.beans.EmailQueue;
import org.mq.captiway.scheduler.beans.ErrorLog;
import org.mq.captiway.scheduler.beans.Users;

@SuppressWarnings({ "unchecked", "serial","unused"})
public class ErrorLogDao extends AbstractSpringDao {

    public ErrorLogDao() {}
	
    public ErrorLog find(Long id) {
        return (ErrorLog) super.find(ErrorLog.class, id);
    }

   /* public void saveOrUpdate(ErrorLog errorLog) {
        super.saveOrUpdate(errorLog);
    }

    public void delete(ErrorLog errorLog) {
        super.delete(errorLog);
    }*/

    public List<ErrorLog> findAll() {
        return super.findAll(ErrorLog.class);
    }

    
}

