/**
 * 
 */
package org.mq.optculture.business.magento;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.magento.MagentoPromoRequest;

/**
 * @author abheeshna.nalla
 * 
 *
 */
public interface MagentoBusinessService extends BaseService{
	
	public BaseResponseObject processPromoRequest(MagentoPromoRequest magentoPromoRequest) throws BaseServiceException;

}

