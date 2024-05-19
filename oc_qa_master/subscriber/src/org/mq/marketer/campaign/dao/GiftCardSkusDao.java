package org.mq.marketer.campaign.dao;

import java.io.Serializable;
import java.util.List;

import org.mq.marketer.campaign.beans.GiftCardSkus;
import org.springframework.jdbc.core.JdbcTemplate;

public class GiftCardSkusDao extends AbstractSpringDao implements Serializable{
	
	public GiftCardSkusDao() {}
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public List<GiftCardSkus> findListByUserId(Long userId) {
		
		String query = "FROM GiftCardSkus WHERE userId ="+userId+" ";
		logger.info("skus list all progrsms query "+query);
		
		List<GiftCardSkus> skusList = getHibernateTemplate().find(query);
		if(skusList!=null && skusList.size()>0) {
			return skusList;
		}else {
			return null;
		}
	}
	
	public GiftCardSkus getSkuByUserIdAndSkuCode(Long userId, String skuCode) {
		
		String query = "FROM GiftCardSkus WHERE userId="+userId+" AND skuCode='"+skuCode+"' ";
		List<GiftCardSkus> giftCardSkuList = getHibernateTemplate().find(query);
		
		if(giftCardSkuList!=null && giftCardSkuList.size()>0) {
			return giftCardSkuList.get(0);
		}else {
			return null;
		}
	}

}
