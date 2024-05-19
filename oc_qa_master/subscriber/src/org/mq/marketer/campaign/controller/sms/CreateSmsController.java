package org.mq.marketer.campaign.controller.sms;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.UserSMSSenderId;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.UserSMSSenderIdDao;
import org.mq.marketer.campaign.general.Constants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;

public class CreateSmsController extends GenericForwardComposer{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private Long userId;
	private UserSMSSenderIdDao userSMSSenderIdDao;
	
	public CreateSmsController() {
		
		userId = GetUser.getUserId();
		this.userSMSSenderIdDao = (UserSMSSenderIdDao)SpringUtil.getBean("userSMSSenderIdDao");
		
		
		
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
	}
	
	public List<UserSMSSenderId> getsenderIds() {
		List<UserSMSSenderId> senderIds = null;
		try{
			logger.info("the logged user id is====>"+userId);
			senderIds = userSMSSenderIdDao.findByUserId(this.userId);
			
		}catch (Exception e) {

		logger.error("Exception ::" , e);
		}
		return senderIds;
		
		
		
	}
	
	
	
	
	

}
