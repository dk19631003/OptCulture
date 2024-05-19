package org.mq.optculture.business.campaign;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.campaign.CampaignReportRequest;
import org.mq.optculture.model.campaign.CampaignReportResponse;

public interface CampaignReportBusinessService extends BaseService {
	CampaignReportResponse processOpenUpdate(CampaignReportRequest campaignReportRequest) throws BaseServiceException;
	CampaignReportResponse processClickUpdate(CampaignReportRequest campaignReportRequest) throws BaseServiceException;
	CampaignReportResponse processWeblink(CampaignReportRequest campaignReportRequest) throws BaseServiceException;
	CampaignReportResponse processResubscribe(CampaignReportRequest campaignReportRequest) throws BaseServiceException;
	CampaignReportResponse processUnsubscribeRequest(CampaignReportRequest campaignReportRequest) throws BaseServiceException;
	CampaignReportResponse processUnsubscribeUpdate(CampaignReportRequest campaignReportRequest) throws BaseServiceException;
	CampaignReportResponse processFarwardUpdate(CampaignReportRequest campaignReportRequest) throws BaseServiceException;
	CampaignReportResponse processUpdateSubscriptionlink(CampaignReportRequest campaignReportRequest) throws BaseServiceException;
	CampaignReportResponse processShareOnTwitter(CampaignReportRequest campaignReportRequest) throws BaseServiceException;
	CampaignReportResponse processShareOnFacebook(CampaignReportRequest campaignReportRequest) throws BaseServiceException;
	CampaignReportResponse processShareLink(CampaignReportRequest campaignReportRequest) throws BaseServiceException;
	CampaignReportResponse processCouponBarCode(CampaignReportRequest campaignReportRequest) throws BaseServiceException;
	CampaignReportResponse processEmailTopdf(CampaignReportRequest campaignReportRequest) throws BaseServiceException;
	CampaignReportResponse processNotificationClickUpdate(CampaignReportRequest campaignReportRequest) throws BaseServiceException;
}

