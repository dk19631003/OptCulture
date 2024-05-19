package org.mq.optculture.business.mobileapp;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.mobileapp.WebPortalBrandingRequest;
import org.mq.optculture.model.mobileapp.WebPortalBrandingResponse;


	public interface WebPortalBrandingService extends BaseService{
		public WebPortalBrandingResponse processBrandingRequest(WebPortalBrandingRequest brandingRequest)
				throws BaseServiceException;
	}
