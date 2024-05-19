package org.mq.loyality.common.dao;


import java.util.List;

import org.hibernate.SessionFactory;
import org.mq.loyality.common.hbmbean.OCSMSGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class OCSMSGatewayDao {

	@Autowired
	 private SessionFactory sessionFactory;
	
	 @Transactional
	public OCSMSGateway findById(Long id) {
			
			String qry = "FROM OCSMSGateway WHERE id="+id;
			
			List<OCSMSGateway>  retObjectsList  = sessionFactory.getCurrentSession().createQuery(qry).list();
			
			if(retObjectsList != null && retObjectsList.size() > 0) {
				
				return retObjectsList.get(0);
			}else return null;
			
				
		}//findById
		// Added for admin screen
}
