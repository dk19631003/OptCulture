package org.mq.optculture.business.couponcode;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.couponcodes.CouponCodeEnquObj;

public interface CouponCodeEnquiryService extends BaseService {

	public BaseResponseObject processCouponRequest(CouponCodeEnquObj couponCodeEnquObj) throws BaseServiceException;
}
