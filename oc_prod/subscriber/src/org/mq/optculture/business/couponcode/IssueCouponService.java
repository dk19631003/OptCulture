package org.mq.optculture.business.couponcode;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.couponcodes.IssueCouponRequest;
import org.mq.optculture.model.couponcodes.IssueCouponResponse;


public interface IssueCouponService extends BaseService {
	public IssueCouponResponse processIssueCoupon(IssueCouponRequest issueCouponRequest)  throws BaseServiceException;
}
