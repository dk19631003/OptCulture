package org.mq.optculture.restservice.digitalReceipt;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;

public interface DRToLtyExtractionService extends BaseService{
	
	public BaseResponseObject processRequest() throws BaseServiceException;
	public String processRequest(String drJsonId) throws BaseServiceException;
	
	public String processRequest(String drJsonId, boolean mustExtract) throws BaseServiceException;
}
