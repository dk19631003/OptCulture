package org.mq.marketer.campaign.general;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class LogUtil  {
	String userName = null;
	Logger logger = null;
	protected LogUtil(Logger logger, String userName) {
		this.logger = logger;
		this.userName = userName;
	}
	
	public void debug(Object message) {
		logger.debug(userName + " " + message);
	}

	public void error(Object message) {
		logger.error(userName + "** Exception" + message + " **");
	}

	public void fatal(Object message) {
		logger.fatal(userName + " " + message);
	}

	public void info(Object message) {
		logger.info(userName + " " + message);
	}

	public void warn(Object message) {
		logger.warn(userName + " " + message);
	}

}
