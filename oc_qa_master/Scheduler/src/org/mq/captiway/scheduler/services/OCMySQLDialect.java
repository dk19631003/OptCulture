package org.mq.captiway.scheduler.services;

import org.hibernate.Hibernate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.utility.Constants;
/**
* @author 
*/
public class OCMySQLDialect extends org.hibernate.dialect.MySQL5InnoDBDialect{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
   public OCMySQLDialect() {
       super();
       
       try {
    	   //if(logger.isDebugEnabled()) logger.debug("Registered bitwise_and");
    	   registerFunction("bitwise_and", new MySQLBitwiseAndSQLFunction("bitwise_and", Hibernate.INTEGER));
    	   registerFunction("bitwise_or", new MySQLBitwiseOrSQLFunction("bitwise_or", Hibernate.INTEGER));
    	   
	} catch (Exception e) {
		logger.error("Exception ::::" , e);
	}
   }
   

}