package org.mq.marketer.campaign.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.mq.marketer.campaign.general.Constants;




public class ConfigureLoggerLevels extends HttpServlet {
     
	 private static Logger logger ;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				
		
		  String	applicationStr = request.getParameter("Application");
		  String levelStr = request.getParameter("LoggerLevels"); 
									
		PrintWriter pw=response.getWriter();
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration  config = ctx.getConfiguration();
	   
	    
		if(applicationStr.equals(Constants.SUBSCRIBER_LOGGER)){
			
			LoggerConfig loggerConfig = config.getLoggerConfig(Constants.SUBSCRIBER_LOGGER);
			logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
			
			
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
		}else{
			response.sendRedirect("/Scheduler/ConfigureLoggerLevels?Application=Scheduler&LoggerLevels="+levelStr);
		}
		
		
		
		
	
	}
	
	
}
