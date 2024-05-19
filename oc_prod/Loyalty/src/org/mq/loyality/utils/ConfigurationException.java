package org.mq.loyality.utils;


public class ConfigurationException extends RuntimeException {

	public ConfigurationException(String msg) {
		super("config: " + msg);
	}

	public ConfigurationException() {
		super("Unable to read configuration");
	}
}