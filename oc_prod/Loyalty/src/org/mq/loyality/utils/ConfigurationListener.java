package org.mq.loyality.utils;


import java.util.EventListener;

public interface ConfigurationListener  extends EventListener {

	public void configurationChanged(ConfigurationEvent cfgEvt);

}
