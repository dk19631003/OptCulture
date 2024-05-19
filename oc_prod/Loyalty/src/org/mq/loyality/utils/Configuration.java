package org.mq.loyality.utils;



import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Configuration extends Properties {

	private static final long serialVersionUID = 2237092819511268259L;
	static String rootpath = System.getProperty("CONFDIR");
	static final String CONFIG_FILE = System.getProperty("CONFFILE",
			"common.properties");

	static Configuration conf = null;
	static Object dummy = new Object();

//	private ConfManager manager = null;
	protected File confFile = null;
	private List listners = new LinkedList();
	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);
	public static Configuration getInstance() throws ConfigurationException {
		if (conf == null)
			synchronized (dummy) {
				if (conf == null)
					conf = new Configuration();
			}
		return conf;
	}

	private File getConfigurationFile() {
		if (rootpath == null) {
			URL fileURL = this.getClass().getClassLoader().getResource(
					CONFIG_FILE);
			this.confFile = new File(fileURL.getFile());
		} else {
			if (!rootpath.endsWith("/")) {
				rootpath += "/";
			}

			this.confFile = new File(rootpath + CONFIG_FILE);
		}
		return this.confFile;
	}

	private Configuration() throws ConfigurationException {
		InputStream in = null;

		try {
			//this.confFile = getConfigurationFile();
			in=this.getClass().getClassLoader().getResourceAsStream("application.properties");
			
			//in = new FileInputStream(this.confFile);
			load(in);
			in.close();
		/*} catch (Exception e) {
			e.printStackTrace();
			logger.info("Configuration file: ******************");
			throw new ConfigurationException("Cannot open config file: "
					+ e.getMessage());
		}
			try{*/

		//	this.manager = new ConfManager();
		//	this.manager.setDaemon(true);
		//	this.manager.start();

		} catch (Exception e) {
			logger.info(" Exception :: ",e);
			throw new ConfigurationException("Cannot open config file: "
					+ e.getMessage());
		}
	}

	public synchronized void addConfigurationListener(
			ConfigurationListener listner) {
		if (listner != null) {
			this.listners.add(listner);
		}
	}

	public synchronized void removeConfigurationListener(
			ConfigurationListener listner) {
		if (listner != null) {
			this.listners.remove(listner);
		}
	}

	protected synchronized void notifyListners() {
		Object[] list = this.listners.toArray();
		ConfigurationEvent cfgEvt = new ConfigurationEvent(this);
		ConfigurationListener currListner;
		for (int i = 0; i < list.length; i++) {
			currListner = (ConfigurationListener) list[i];
			currListner.configurationChanged(cfgEvt);
		}
	}

	public synchronized void close() {
		workable = false;
	}

	public static synchronized void shutdown() {
		workable = false;
	}

	static private boolean workable = true;

	protected long lastModified;
	protected long REFRESH_INTERVAL = 30000;

	/*private class ConfManager extends Thread {
		FileInputStream fileIn = null;

		public void run() {
			while (workable) {
				try {
					long timeOfModification = Configuration.this.confFile
							.lastModified();
					// disk "+ timeOfModification);
					if (Configuration.this.lastModified != timeOfModification) {

						// /*********************CHK this ( manish )
						synchronized (this) {
							Configuration.this.lastModified = timeOfModification;
							clear();
							load(this.fileIn = new FileInputStream(
									Configuration.this.confFile));
							notifyListners();
						}
					}
				} catch (Exception ex) {
				} finally {
					try {
						this.fileIn.close();
					} catch (IOException iex) {
					}
				}

				try {
					sleep(Configuration.this.REFRESH_INTERVAL);
				} catch (InterruptedException iex) {
				}
			}
		}
	}*/

	public static String getConfigurationRoot() {
		if (!rootpath.endsWith("/")) {
			rootpath += "/";
		}
		return rootpath;
	}

	public String toString() {
		String s = "Configuration:\n";
		s += "\tConfigFile=" + CONFIG_FILE;
		s += "\tProduct=" + getProperty("System.ProductName");
		return s;
	}

	public static void main(String args[]) throws Exception {
		Configuration c = Configuration.getInstance();
		
		Enumeration keys = c.keys();
	
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = c.getProperty(key);
			
		}
	}

	protected void finalize() throws Throwable {
		workable = false;
		super.finalize();
	}

}
