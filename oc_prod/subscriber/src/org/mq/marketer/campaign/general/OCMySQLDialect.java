package org.mq.marketer.campaign.general;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.dialect.function.SQLFunctionTemplate;

/**
* @author 
*/
public class OCMySQLDialect extends org.hibernate.dialect.MySQL5InnoDBDialect{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
   public OCMySQLDialect() {
       super();
       
       try {
    	   logger.info("Registered bitwise_and");
    	   registerFunction("bitwise_and", new MySQLBitwiseAndSQLFunction("bitwise_and", Hibernate.INTEGER));
    	   registerFunction("bitwise_or", new MySQLBitwiseOrSQLFunction("bitwise_or", Hibernate.INTEGER));
    	   registerFunction( "regexp", new SQLFunctionTemplate(Hibernate.BOOLEAN, "?1 REGEXP ?2") );
    	   
	} catch (Exception e) {
		logger.error("Exception ::" , e);
	}
   }
   

}