package org.mq.optculture.timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.CampaignScheduleDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.stereotype.Service;

@Service
public class CheckEmailSmsCreditThread{
    private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

    private CampaignScheduleDaoForDML campaignScheduleDaoForDML;
    
      /**
     @param user 
     @param empty
     @return void 
     @Description  method to check Email credit score it positive or not */
    public void checkEmailSmsCredit(Users user){
        try {
            if (user != null) {
               checkEmailSmsCreditAndUpdateStatus(user);
            }
        }catch (Exception e) {
            logger.error("Exception ",e);
        }
     }
     

    private void checkEmailSmsCreditAndUpdateStatus(Users user){
        try {
            campaignScheduleDaoForDML = (CampaignScheduleDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("campaignScheduleDaoForDML");
                 campaignScheduleDaoForDML.updateAllFuturePasuedSchedulesToActive(user);
                    logger.info("updated campaigns scheduler status to active");
        } catch (Exception e) {
            logger.error("Exception ",e);
        }
    }
}
