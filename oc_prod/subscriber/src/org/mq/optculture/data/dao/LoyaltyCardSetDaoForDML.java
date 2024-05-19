package org.mq.optculture.data.dao;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.dao.AbstractSpringDaoForDML;
import org.mq.optculture.exception.LoyaltyProgramException;
import org.mq.optculture.utils.OCConstants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class LoyaltyCardSetDaoForDML  extends AbstractSpringDaoForDML implements Serializable {
	
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void saveOrUpdate(LoyaltyCardSet loyaltyCardSet) {
        getHibernateTemplate().saveOrUpdate(loyaltyCardSet);
    }

	/*public List<LoyaltyCardSet> findByProgramId(Long prgmId) {
		
		List<LoyaltyCardSet> cardSetList = getHibernateTemplate().find(" FROM LoyaltyCardSet WHERE programId = "+prgmId.longValue());
		if(cardSetList != null && cardSetList.size() > 0) return cardSetList;
		else return null;
	}
	
	public List<LoyaltyCardSet> findCardSetByProgramId(Long prgmId) {
		
		List<LoyaltyCardSet> cardSetList = getHibernateTemplate().find(" FROM LoyaltyCardSet WHERE programId = "+prgmId.longValue()+" AND generationType !='"+OCConstants.LOYALTY_CARDSET_TYPE_CUSTOM+"'");
		if(cardSetList != null && cardSetList.size() > 0) return cardSetList;
		else return null;
	}

	public LoyaltyCardSet findByCardSetId(long cardSetId) {
		
		List<LoyaltyCardSet> cardSetList = getHibernateTemplate().find(" FROM LoyaltyCardSet WHERE cardSetId = "+cardSetId);
		if(cardSetList != null && cardSetList.size() > 0) return cardSetList.get(0);
		else return null;
	}
	
	public List<Long> findInSequenceByProgramId(Long prgmId) {
		
		List<Long> cardSetList = getHibernateTemplate().find(" SELECT cardSetId FROM LoyaltyCardSet WHERE programId = "+prgmId.longValue()+" ORDER BY cardSetId ASC ");
		if(cardSetList != null && cardSetList.size() > 0) return cardSetList;
		else return null;
	}
	
	public List<LoyaltyCardSet> findActiveByProgramId(Long prgmId) {
		
		String queryStr = " FROM LoyaltyCardSet WHERE programId = "+prgmId.longValue()+" AND cardSetType ='"+
		OCConstants.LOYALTY_CARDSET_TYPE_VIRTUAL+"' AND status = '"+OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE+"' ORDER BY cardSetId ASC ";
		
		List<LoyaltyCardSet> cardSetList = getHibernateTemplate().find(queryStr);
		if(cardSetList != null && cardSetList.size() > 0) return cardSetList;
		else return null;
	}
	
public List<LoyaltyCardSet> findBy(Long prgmId, String status, String cardGenerationType) {
		
		String queryStr = " FROM LoyaltyCardSet WHERE programId = "+prgmId.longValue()+" AND cardGenerationType ='"+
				cardGenerationType+"' AND status = '"+status+"' ORDER BY cardSetId ASC ";
		
		List<LoyaltyCardSet> cardSetList = getHibernateTemplate().find(queryStr);
		if(cardSetList != null && cardSetList.size() > 0) return cardSetList;
		else return null;
	}
	
	public List<LoyaltyCardSet> findActiveSetByProgramId(Long prgmId) {
		
		String queryStr = " FROM LoyaltyCardSet WHERE programId = " + prgmId.longValue() + 
				" AND status = '" + OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE + "' ";
		
		List<LoyaltyCardSet> cardSetList = getHibernateTemplate().find(queryStr);
		if(cardSetList != null && cardSetList.size() > 0) return cardSetList;
		else return null;
	}
	
	public List<LoyaltyCardSet> findByProgramIdStr(String prgmIdStr, String status) {
		
		String queryStr = " FROM LoyaltyCardSet WHERE programId IN ("+prgmIdStr+") AND status = '"+status+"'";
		
		List<LoyaltyCardSet> cardSetList = getHibernateTemplate().find(queryStr);
		if(cardSetList != null && cardSetList.size() > 0) return cardSetList;
		else return null;
	}*/

	public void deleteByPrgmId(Long prgmId) {
		String queryStr = " DELETE FROM LoyaltyCardSet WHERE programId = "+prgmId.longValue();
		
		getHibernateTemplate().bulkUpdate(queryStr);

	}

	/*public List<LoyaltyCardSet> findByUserId(Long userId) {

		List<LoyaltyCardSet> cardSetList = getHibernateTemplate().find(" FROM LoyaltyCardSet WHERE createdBy = '"+userId.longValue()+"'");
		if(cardSetList != null && cardSetList.size() > 0) return cardSetList;
		else return null;
	}

	public List<LoyaltyCardSet> findCardSetByTierLevel(int tierLevel, Long prgmId) {
		List<LoyaltyCardSet> cardSetList = getHibernateTemplate().find(" FROM LoyaltyCardSet WHERE programId = "+prgmId.longValue()+" AND linkedTierLevel = "+tierLevel);
		if(cardSetList != null && cardSetList.size() > 0) return cardSetList;
		else return null;
	}

	public int findHighestTierLinkedToCardset(Long prgmId) {
		List<Integer> cardSetList = getHibernateTemplate(). find("SELECT linkedTierLevel FROM LoyaltyCardSet WHERE programId = "+prgmId.longValue()+" ORDER BY linkedTierLevel DESC ");
		if(cardSetList != null && cardSetList.size() > 0) return cardSetList.get(0).intValue();
		else return 0;
	}
	
	public Map<String, Object> fetchCardSetsByCardSetIdStr(String CardSetIdStr) throws LoyaltyProgramException{
		
		
		try{
			List<Object[]> list = new ArrayList<Object[]>();
			Map cardSetMap = new HashMap<String , Object>();
			String queryStr = "SELECT cardSetId, cardSetName FROM LoyaltyCardSet WHERE cardSetId IN ("+CardSetIdStr+")";
			list = getHibernateTemplate().find(queryStr);
			for(Object[] obj:list){
				cardSetMap.put(obj[0], obj[1]);
			}
			 return cardSetMap;
			
		}catch(Exception e){
			throw new LoyaltyProgramException("fetch tiers failed");
		}
	}
	
	public List<Object[]> fetchCardSetAndLinkedTierByPrgmId(Long prgmId) {
		
		List<Object[]> linktierList = null;
		//Map tierLinkMap = new HashMap<String , Object>();
		String queryStr = "SELECT card_set_id as cardSetId, lt.tier_id as tierId FROM loyalty_card_set lcs JOIN loyalty_program_tier lt ON lcs.program_id = lt.program_id AND concat('Tier ',lcs.linked_tier_level) = lt.tier_type WHERE linked_tier_level != 0 AND lcs.program_id = "+prgmId.longValue();
		linktierList = jdbcTemplate.query(queryStr, new RowMapper(){

			@Override
			public Object mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				Object obj[] = new Object[2];
				obj[0] = rs.getLong("cardSetId");
				obj[1] = rs.getLong("tierId");
				return obj;
			}
			
		});
		for(Object[] obj:list){
			tierLinkMap.put(obj[0], obj[1]);
		}
		if(linktierList!=null && linktierList.size()>0){
		 return linktierList;}
		else return null;
		
	}

public List<Object[]> fetchCardSetAndLinkedTierByPrgmId(Long prgmId, Long cardSetId,Long tierId) {
		
			List<Object[]> linktierList = null;
			//Map tierLinkMap = new HashMap<String , Object>();
			String queryStr = "SELECT card_set_id as cardSetId, lt.tier_id as tierId FROM loyalty_card_set lcs JOIN loyalty_program_tier lt ON lcs.program_id = lt.program_id AND concat('Tier ',lcs.linked_tier_level) = lt.tier_type WHERE linked_tier_level != 0 AND lcs.program_id = "+prgmId.longValue();
			if(cardSetId != null){
				queryStr += " AND card_set_id = "+cardSetId;
			}
			if(tierId != null){
				queryStr += " AND tier_id = "+tierId;
			}
			logger.debug("RRR"+queryStr);
			linktierList = jdbcTemplate.query(queryStr, new RowMapper(){

				@Override
				public Object mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					Object obj[] = new Object[2];
					obj[0] = rs.getLong("cardSetId");
					obj[1] = rs.getLong("tierId");
					return obj;
				}
				
			});
			for(Object[] obj:list){
				tierLinkMap.put(obj[0], obj[1]);
			}
			if(linktierList!=null && linktierList.size()>0){
			 return linktierList;}
			else return null;
			
		}*/
	public synchronized void updateCardSetQuantity(Long cardSetId, long qty) throws Exception{
	
	String qry = "UPDATE LoyaltyCardSet SET quantity=quantity+"+qty+" WHERE cardSetId="+cardSetId;
	
	executeUpdate(qry);
	
}/*
	public LoyaltyCardSet getCardSetsByCardGenerationType(String cardGenType, Long prgmId){
		List list = null;
		try{
			String qry = "FROM LoyaltyCardSet WHERE programId="+prgmId+" AND cardGenerationType='"+cardGenType+"'";
			list = getHibernateTemplate().find(qry);
		}catch(Exception e){
			logger.error("Exception :: ",e);
		}
		if(list != null && list.size() > 0) return (LoyaltyCardSet) list.get(0);
		return null;
	}*/
}
