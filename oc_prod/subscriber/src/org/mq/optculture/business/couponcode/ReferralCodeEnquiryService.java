package org.mq.optculture.business.couponcode;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.couponcodes.CouponCodeEnquObj;
import org.mq.optculture.model.couponcodes.ReferralCodeEnqRequest;
import org.mq.optculture.model.couponcodes.ReferralCodeEnqResponse;

public interface ReferralCodeEnquiryService extends BaseService {

	public ReferralCodeEnqResponse processReferralRequest(ReferralCodeEnqRequest referralCodeEnqRequest) throws BaseServiceException;
}
