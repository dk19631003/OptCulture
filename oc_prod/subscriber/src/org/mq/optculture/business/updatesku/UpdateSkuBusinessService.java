package org.mq.optculture.business.updatesku;

import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.Users;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.updatesku.SkuInfo;
import org.mq.optculture.model.updatesku.UpdateSkuRequestObject;

public interface UpdateSkuBusinessService extends BaseService{
	BaseResponseObject processUpdateSkuRequest(UpdateSkuRequestObject updateSkuRequestObject,SkuInfo skuInfo,
			Users user,MailingList mailingList)throws BaseServiceException;
	BaseResponseObject processUpdateSkuRequest(UpdateSkuRequestObject updateSkuRequestObject)throws BaseServiceException;
}
