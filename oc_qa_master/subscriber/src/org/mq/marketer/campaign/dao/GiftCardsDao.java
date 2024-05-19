package org.mq.marketer.campaign.dao;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.jdbc.core.JdbcTemplate;

public class GiftCardsDao extends AbstractSpringDao implements Serializable{
	
	public GiftCardsDao() {}
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public Set<String> findCardsListByUserId(Long userId) {
		
		String query = "SELECT giftCardNumber FROM GiftCards WHERE userId="+userId+" AND giftCardStatus='Active' ";
		
		logger.info("all gift cards nubers query by user id "+query);
		List<Object> listOfGiftCards = getHibernateTemplate().find(query);
		logger.info("listOfGiftCards size "+listOfGiftCards.size());
		
		Set<String> cardsSet = new HashSet<>();
		if(listOfGiftCards!=null && listOfGiftCards.size()>0) {
			for (Object obj : listOfGiftCards) {
			    if (obj instanceof String) {
			    	cardsSet.add(obj.toString());
			    }
			}
			if(cardsSet!=null && cardsSet.size()>0)
				return cardsSet;
			else
				return null;
		}else {
			return null;
		}
	}

}
