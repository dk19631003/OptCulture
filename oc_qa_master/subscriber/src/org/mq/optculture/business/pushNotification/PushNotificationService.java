/**
 * 
 */
package org.mq.optculture.business.pushNotification;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.PushNotification.PushNotificationRequest;
import org.mq.optculture.model.magento.MagentoPromoRequest;

/**
 * @author abheeshna.nalla
 * 
 *
 */
public interface PushNotificationService extends BaseService{
	
	public BaseResponseObject processPushNotificationRequest(PushNotificationRequest pushNotificationRequest) throws BaseServiceException;

}

