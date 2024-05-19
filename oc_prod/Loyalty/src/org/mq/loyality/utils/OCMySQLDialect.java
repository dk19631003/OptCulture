package org.mq.loyality.utils;

import java.sql.Types;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.TextType;


/**
* @author 
*/
public class OCMySQLDialect extends org.hibernate.dialect.MySQL5InnoDBDialect{

	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);
   public OCMySQLDialect() {
       super();
       
       try {
    	   logger.info("Registering bitwise >>>>>>. ");
    	   registerFunction("bitwise_and", new MySQLBitwiseAndSQLFunction("bitwise_and", Hibernate.INTEGER));
    	   registerFunction("bitwise_or", new MySQLBitwiseOrSQLFunction("bitwise_or", Hibernate.INTEGER));
    	   registerFunction( "regexp", new SQLFunctionTemplate(Hibernate.BOOLEAN, "?1 REGEXP ?2") );
    	   logger.info("Registered bitwise <<<<<<<< ");
	} catch (Exception e) {
		logger.info(" Exception :: ",e);
	}
   }
   

}