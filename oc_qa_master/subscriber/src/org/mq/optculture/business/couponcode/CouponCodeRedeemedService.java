package org.mq.optculture.business.couponcode;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.couponcodes.CouponCodeEnquObj;
import org.mq.optculture.model.couponcodes.CouponCodeRedeemedObj;

public interface CouponCodeRedeemedService extends BaseService{
	public BaseResponseObject processRedeemedRequest(CouponCodeRedeemedObj couponCodeRedeemedObj) throws BaseServiceException;
	public BaseResponseObject processRedeemedRequest(CouponCodeRedeemedObj couponCodeRedeemedObj,String itemInfo,String cardNumber,int redeemReward) throws BaseServiceException;
}
