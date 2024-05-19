package org.mq.loyality.utils;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.type.Type;

/**
* @author 
*/
public class MySQLBitwiseAndSQLFunction extends StandardSQLFunction implements SQLFunction {

	
   public MySQLBitwiseAndSQLFunction(String name) {
       super(name);
   }

   public MySQLBitwiseAndSQLFunction(String name, Type typeValue) {
       super(name, typeValue);
   }

   @Override
   public String render(List args, SessionFactoryImplementor arg1) {
       if (args.size() != 2){
           throw new IllegalArgumentException("the function must be passed 2 arguments");
       }
       StringBuffer buffer = new StringBuffer(" ( "+args.get(0).toString());
       buffer.append(" & ").append(args.get(1)+" ) ");
       return buffer.toString();
   }

}