package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.mq.captiway.scheduler.beans.OCSMSGateway;
import org.mq.captiway.scheduler.utility.Constants;

public class OCSMSGatewayDao extends AbstractSpringDao{

	public OCSMSGateway findById(Long id) {
		
		String qry = "FROM OCSMSGateway WHERE id="+id.longValue();
		
		List<OCSMSGateway>  retObjectsList  = executeQuery(qry);
		
		if(retObjectsList != null && retObjectsList.size() > 0) {
			
			return retObjectsList.get(0);
		}else return null;
		
		
	}
	
	public List<OCSMSGateway> findBy(String mode) throws Exception{
		
		String qry = "FROM OCSMSGateway WHERE mode='"+Constants.SMS_SENDING_MODE_SMPP+"' and enableSessionAlive=1 "
				+ "AND pullReports=false and server is null";
		
		List<OCSMSGateway>  retObjectsList  = executeQuery(qry);
		
		return retObjectsList;
	}
	
}
