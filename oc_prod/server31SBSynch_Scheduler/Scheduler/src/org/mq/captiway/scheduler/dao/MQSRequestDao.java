package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.mq.captiway.scheduler.beans.MQSRequest;

@SuppressWarnings({ "unchecked", "serial","unused"})
public class MQSRequestDao extends AbstractSpringDao {

    public MQSRequestDao() {}
	
    public MQSRequest find(Long id) {
        return (MQSRequest) super.find(MQSRequest.class, id);
    }

   /* public void saveOrUpdate(MQSRequest mQSRequest) {
        super.saveOrUpdate(mQSRequest);
    }

    public void delete(MQSRequest mQSRequest) {
        super.delete(mQSRequest);
    }*/

    public List<MQSRequest> findAll() {
        return super.findAll(MQSRequest.class);
    }
    
    public MQSRequest getByRefNumber(String refNum){
    	List reqs = getHibernateTemplate().find("from MQSRequest where refNumber = " + refNum);
    	MQSRequest mqsReq = null;
    	if(reqs.size()>0){
    		mqsReq = (MQSRequest)reqs.get(0);
    	}
    	return mqsReq;
    }
}

