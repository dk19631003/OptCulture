package org.mq.marketer.campaign.controller.mqs;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PageUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Window;

public class MQSController extends GenericForwardComposer{
	Properties mqsProp = null;
	private Session session;
	private Iframe mqsIframeId;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	public MQSController() {
		mqsProp = (Properties)SpringUtil.getBean("mqsProperties");
		session = Sessions.getCurrent();
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		loadMQSPage(mqsIframeId);
	}
	
	public void loadMQSPage(String page,Iframe iframe){
		String token = mqsProp.getProperty(page + "TabId").trim();
		Users user = GetUser.getUserObj();
		String mqsKey = user.getUserName();
		String iframeUrl = mqsProp.getProperty("MQSIframeUrl") + "KEY=" + mqsKey + "&TOKEN=" + token;
		logger.debug("Iframe Url : " + iframeUrl);
		iframe.setSrc(iframeUrl);
	}
	
	public void loadMQSPage(Iframe iframe){
		try {
			if(session!=null && mqsProp!=null){
				String page = (String)session.getAttribute("mqsPageToLoad");
				String mqsPageHeader = (String)session.getAttribute("mqsPageHeader");
				if(page!=null && mqsPageHeader!=null){
					String token = mqsProp.getProperty(page + "TabId").trim();
					Users user = GetUser.getUserObj();
					String mqsKey = user.getUserName();
					String iframeUrl = mqsProp.getProperty("MQSIframeUrl") + "KEY=" + mqsKey + "&TOKEN=" + token;
					logger.debug("Iframe Url : " + iframeUrl);
					iframe.setStyle("height:620px;width:100%;");
					iframe.setSrc(iframeUrl);
					String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
					PageUtil.setHeader(mqsPageHeader,"",style,true);
				}
			}else{
				logger.error("**Exception : Session/mqs properties is null ");
			}
		}catch (Exception e) {
			logger.error("**Exception : Problem in loading the mqs page - " + e);
		}
	}
}
