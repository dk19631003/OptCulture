package org.mq.captiway.scheduler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;



public class ConfigureLoggerLevels extends HttpServlet {
	
    
    private static Logger logger ;
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException {
		
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration config = ctx.getConfiguration();
		LoggerConfig loggerConfig = config.getLoggerConfig(org.mq.captiway.scheduler.utility.Constants.SCHEDULER_LOGGER);
		logger = LogManager.getLogger(org.mq.captiway.scheduler.utility.Constants.SCHEDULER_LOGGER);
		
		PrintWriter pw=response.getWriter();
		String levelStr = request.getParameter("LoggerLevels");
		
		if(levelStr.equals("debug")){
			loggerConfig.setLevel(org.apache.logging.log4j.Level.DEBUG);
		}
		else if(levelStr.equals("info")){
			loggerConfig.setLevel(org.apache.logging.log4j.Level.INFO);
		}
		else if(levelStr.equals("warn")){
			loggerConfig.setLevel(org.apache.logging.log4j.Level.WARN);
		}
		else if(levelStr.equals("error")){
			loggerConfig.setLevel(org.apache.logging.log4j.Level.ERROR);
		}
		else if(levelStr.equals("fatal")){
			loggerConfig.setLevel(org.apache.logging.log4j.Level.FATAL);
		}
		
		ctx.updateLoggers();			
		
		logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>TESTING LOG LEVELS : --------------------------Debug Message!");
	      logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>TESTING LOG LEVELS : --------------------------Info Message!");
	      logger.warn(">>>>>>>>>>>>>>>>>>>>>>>>>TESTING LOG LEVELS : --------------------------Warn Message!");
	      logger.error(">>>>>>>>>>>>>>>>>>>>>>>>>TESTING LOG LEVELS : --------------------------Error Message!");
	      logger.fatal(">>>>>>>>>>>>>>>>>>>>>>>>>TESTING LOG LEVELS : --------------------------Fatal Message!");
		
	      pw.println("<b>Logger level has been changed</b>");
	
	}

}
