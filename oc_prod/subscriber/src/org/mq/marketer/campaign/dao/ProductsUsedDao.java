package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ProductsUsed;
import org.mq.marketer.campaign.general.Constants;

public class ProductsUsedDao extends AbstractSpringDao  {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public ProductsUsedDao(){}
		
		public ProductsUsed find(Long id){
			return (ProductsUsed)super.find(ProductsUsed.class, id);
		}
		
		/*public void saveOrUpdate(ProductsUsed product){
			super.saveOrUpdate(product);
		}
		
		public void delete(ProductsUsed product){
			super.delete(product);
		}*/

		public ProductsUsed findByUserId(Long userId) {
			String qry = "FROM ProductsUsed WHERE userId =" + userId.longValue();
			
			List<ProductsUsed> list =  executeQuery(qry);
			logger.debug("list ::"+list.size());
			if(list!=null && list.size()>0) {
				return list.get(0);
			}
			else return null;
		}
		
}
