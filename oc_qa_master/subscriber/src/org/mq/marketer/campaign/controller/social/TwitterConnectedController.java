package org.mq.marketer.campaign.controller.social;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.twitter.api.Twitter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;

public class TwitterConnectedController extends GenericForwardComposer  {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		logger.info("TwitterConnectedController  Controller...");
		
	}
	
	
	public void onClick$twitterProfileBtnId() {
		logger.info("twitterProfileBtnId Button Clicked");
		
		UsersConnectionRepository newUcr = (UsersConnectionRepository)SpringUtil.getBean("usersConnectionRepository");
		logger.info("UCR="+newUcr);
		
		ConnectionRepository connectionRepository=newUcr.createConnectionRepository( "krishna");
		
		Connection<Twitter> connection = connectionRepository.findPrimaryConnection(Twitter.class);
		if (connection == null) {
			logger.info("Connection is null");
		}
		
		logger.info("Twitter="+connection.getApi().userOperations().getScreenName());
	
	}
}
