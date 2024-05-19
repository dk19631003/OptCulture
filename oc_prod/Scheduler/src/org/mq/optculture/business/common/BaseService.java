/**
 * 
 */
package org.mq.optculture.business.common;

import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;

/**
 * @author manjunath.nunna
 *
 */
public interface BaseService {

	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject) throws BaseServiceException;

}
