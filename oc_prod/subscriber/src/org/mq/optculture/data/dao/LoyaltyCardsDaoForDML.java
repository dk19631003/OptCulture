package org.mq.optculture.data.dao;

import java.io.Serializable;
import java.util.List;

import org.mq.marketer.campaign.beans.LoyaltyCards;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.dao.AbstractSpringDaoForDML;
import org.mq.optculture.utils.OCConstants;

public class LoyaltyCardsDaoForDML  extends AbstractSpringDaoForDML implements Serializable  {
	
	public void save(LoyaltyCards loyaltyCards) {
        getHibernateTemplate().save(loyaltyCards);
    }
	
	public void saveOrUpdate(LoyaltyCards loyaltyCards) {
        getHibernateTemplate().saveOrUpdate(loyaltyCards);
    }/*

	public LoyaltyCards findByCardNoAndOrgId(String cardNo, long orgId) {
		String query = " FROM LoyaltyCards WHERE cardNumber = '"+cardNo+"' AND orgId = "+orgId;
		
		List<LoyaltyCards> list =  getHibernateTemplate().find(query);
		if(list!=null && list.size()>0) {
			return list.get(0);
		}
		else return null;
	}
	
	public LoyaltyCards findByCardNoAnduserId(String cardNo, long userId) {
		String query = " FROM LoyaltyCards WHERE userId = "+userId+" AND cardNumber = '"+cardNo+"'" ;
		
		List<LoyaltyCards> list =  getHibernateTemplate().find(query);
		if(list!=null && list.size()>0) {
			return list.get(0);
		}
		else return null;
	}
	
	public LoyaltyCards findByCardNoAndprgmId(String cardNo, long programId,long userId) {
		String query = " FROM LoyaltyCards WHERE cardNumber = '"+cardNo+"' AND userId = "+userId+ "AND programId = "+programId;
		
		List<LoyaltyCards> list =  getHibernateTemplate().find(query);
		if(list!=null && list.size()>0) {
			return list.get(0);
		}
		else return null;
	}
	
	public long getLoyaltyCardsCountByStatus(String cardIdStr, String status) {
		
		String query=null;

		if(status==null || status.trim().equalsIgnoreCase("All")) {
			 query = "SELECT  COUNT(cardId) FROM LoyaltyCards WHERE cardSetId IN("+cardIdStr+")";
		}else if(status != null && status.trim().equalsIgnoreCase("Registered")) {
			query = "SELECT  COUNT(cardId) FROM LoyaltyCards WHERE cardSetId IN("+cardIdStr+") AND registeredFlag = '"+OCConstants.FLAG_YES+"' ";
		}
		else{
			query = "SELECT  COUNT(cardId) FROM LoyaltyCards WHERE cardSetId IN("+cardIdStr+") AND status ='"+status.trim()+"' ";
		}

		List list = getHibernateTemplate().find(query);
		long count = 0; 
		
		if(list != null && list.size() > 0) {
			count = ((Long) list.get(0)).longValue();
		}
		return count;
		
	}
	
	public LoyaltyCards findAvailableCardFromProgram(Long programId, Long orgId, String type, Long setId){
		
		LoyaltyCards loyaltyCard = null;
		
		String queryStr = " FROM LoyaltyCards WHERE programId = "+programId+" AND orgId = "+orgId+
				" AND status = '"+type+"' AND cardSetId = "+setId;
		logger.info("query="+queryStr);
		List<LoyaltyCards> cardList = getHibernateTemplate().find(queryStr);
		
		if(cardList != null && cardList.size() > 0){
			return cardList.get(0);
		}
		else return null;
		
		
	}

	public LoyaltyCards findInventoryCard(Long programId, Long orgId, String type, String cardNumber){
		
		LoyaltyCards loyaltyCard = null;
		
		String queryStr = " FROM LoyaltyCards WHERE programId = "+programId+" AND orgId = "+orgId+
				" AND cardNumber ="+cardNumber+"AND status = '"+type;
		logger.info("query="+queryStr);
		List<LoyaltyCards> cardList = getHibernateTemplate().find(queryStr);
		
		if(cardList != null && cardList.size() > 0){
			return cardList.get(0);
		}
		else return null;
		
		
	}
	
	public LoyaltyCards findByCardNumber(String cardNo) {
		String query = " FROM LoyaltyCards WHERE cardNumber = '"+ cardNo +"'";
		
		List<LoyaltyCards> list =  executeQuery(query);
		if(list!=null && list.size()>0) {
			return list.get(0);
		}
		else return null;
	}
	
	public long getInventoryCardsCountByPrgmId(long prgmId, Long cardsetId) {
		
		String query=null;
		String subQry = "";
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
		}
		query = "SELECT  COUNT(cardId) FROM LoyaltyCards WHERE programId = " + prgmId + " " + subQry +
				"AND status = '"+OCConstants.LOYALTY_CARD_STATUS_INVENTORY+"' ";
		
		List list = getHibernateTemplate().find(query);
		long count = 0;
		
		if(list != null && list.size() > 0) {
			count = ((Long) list.get(0)).longValue();
		}
		return count;
	}
	
	public long getCardsCountByCardSetId(String cardsetIdStr, boolean isInvCount) {
		
		String query=null;
		String subQry = "";
		if(isInvCount) {
			subQry += " AND status = '"+OCConstants.LOYALTY_CARD_STATUS_INVENTORY+"' ";
		}
		
		query = "SELECT COUNT(cardId) FROM LoyaltyCards WHERE cardSetId IN(" + cardsetIdStr + ") " + subQry;
		
		List list = getHibernateTemplate().find(query);
		long count = 0;
		
		if(list != null && list.size() > 0) {
			count = ((Long) list.get(0)).longValue();
		}
		return count;
	}
	
	public long getInvCardsCountByCardSet(long cardsetid , boolean isInvCount){
		
		String query = null;
		String subQuery = "";
		if(isInvCount){
			subQuery += " AND status = '"+OCConstants.LOYALTY_CARD_STATUS_INVENTORY+"' ";
		}
		
		query = " SELECT COUNT(cardId) FROM LoyaltyCards WHERE cardSetId = "+cardsetid+" " + subQuery;
		
		List list = getHibernateTemplate().find(query);
		long count = 0;
		
		if(list != null && list.size() > 0) {
			count = ((Long) list.get(0)).longValue();
		}
		return count;
		
	}

	public LoyaltyCards getInventoryCard(Long orgId, String type){
		
		String queryStr = " FROM LoyaltyCards WHERE orgId = "+orgId+" AND status = '"+type+"' LIMIT 1";
		List<LoyaltyCards> cardList = getHibernateTemplate().find(queryStr);
		if(cardList != null && cardList.size() > 0){
			return cardList.get(0);
		}
		else return null;
		
	}
	
	public LoyaltyCards getInventoryCard(Long orgId, String cardNumber, String type){
		
		String queryStr = " FROM LoyaltyCards WHERE orgId = "+orgId+" AND cardNumber = '"+cardNumber+"' AND status = '"+type+"'";
		List<LoyaltyCards> cardList = getHibernateTemplate().find(queryStr);
		if(cardList != null && cardList.size() > 0){
			return cardList.get(0);
		}
		else return null;
		
	}
	
	public LoyaltyCards getInventoryCard(String programIdStr, String cardSetIdStr, String cardNumber, Long userId){
		
		String queryStr = null;
		List<LoyaltyCards> cardList = null;
		
		if(cardNumber == null){
			queryStr = " FROM LoyaltyCards WHERE userId = "+userId+" AND programId IN ("+programIdStr+")"
					+ " AND cardSetId IN ("+cardSetIdStr+") AND status = '"+OCConstants.LOYALTY_CARD_STATUS_INVENTORY+"'";
			cardList = executeQuery(queryStr, 0, 1);
		}
		else{
			queryStr = " FROM LoyaltyCards WHERE userId = "+userId+" AND programId IN ("+programIdStr+")"
					+ " AND cardSetId IN ("+cardSetIdStr+") AND cardNumber = '"+cardNumber+"'";
			cardList = executeQuery(queryStr);
		}
		
		logger.info("queryStr = "+queryStr);
		//List<LoyaltyCards> cardList = executeQuery(queryStr, 1, 1);
		
		//List<LoyaltyCards> cardList = getHibernateTemplate().find(queryStr);
		if(cardList != null && cardList.size() > 0){
			logger.info("cardList size is not empty");
			return cardList.get(0);
		}
		else{
			logger.info("cardList is null");
			return null;
		}
		
	}*/
	public void deleteByPrgmId(Long prgmId) {
		String queryStr = " DELETE FROM LoyaltyCards WHERE programId = "+prgmId.longValue();
		
		getHibernateTemplate().bulkUpdate(queryStr);
		
	}
	
	public void deleteBy(Long cardId) throws Exception{
		
		String queryStr = " DELETE FROM LoyaltyCards WHERE cardId = "+cardId.longValue();
		
		executeUpdate(queryStr);
	}
	/*public LoyaltyCards findCardByProgram(String programIdStr, String cardSetIdStr, String cardNumber, Long userId){
		
		String queryStr = " FROM LoyaltyCards WHERE programId IN ("+programIdStr+") AND cardSetId IN ("+cardSetIdStr+")"
				+ " AND userId = "+userId+" AND cardNumber = '"+cardNumber+"'";
		
		List<LoyaltyCards> cardList = getHibernateTemplate().find(queryStr);
		if(cardList != null && cardList.size() > 0){
			return cardList.get(0);
		}
		else return null;
		
	}
	
	public Long findNextPrimaryKey() {
		try{
			List list = getHibernateTemplate().find("SELECT MAX(cardId) FROM LoyaltyCards");
			if(list != null && list.size() > 0) return (Long)list.get(0);
			else return null;
		} catch (Exception e) {
			logger.error("Exception ", e);
			return null;
		}
		
	}

	public LoyaltyCards findByCardNoAndCardPin(String cardNumber, String cardPin) {
		String query = " FROM LoyaltyCards WHERE cardNumber = '"+cardNumber+"' AND cardPin = '"+cardPin+"'";

		List<LoyaltyCards> list =  getHibernateTemplate().find(query);
		if(list!=null && list.size()>0) {
			return list.get(0);
		}
		else return null;
	}*/
	
	public int updateDestCardStatus(String cardNumber, Long userId) throws Exception{
		
		
		String qry = "UPDATE LoyaltyCards SET status ='"+OCConstants.LOYALTY_CARD_STATUS_INVENTORY_TRANSFERED+"'"
				+ " WHERE userId="+userId+" AND cardNumber = '"+cardNumber+"'"
				+ " AND status ='"+OCConstants.LOYALTY_CARD_STATUS_INVENTORY+"' ";
		
		int count = executeUpdate(qry);
		return count;
		
	}
	
	
	public int revertDestCardStatus(String cardNumber, Long userId) throws Exception{
		
		
		String qry = "UPDATE LoyaltyCards SET status ='"+OCConstants.LOYALTY_CARD_STATUS_INVENTORY+"'"
				+ " WHERE userId="+userId+" AND cardNumber = '"+cardNumber+"'"
				+ " AND status ='"+OCConstants.LOYALTY_CARD_STATUS_INVENTORY_TRANSFERED+"' ";
		
		int count = executeUpdate(qry);
		return count;
		
	}
	
	
	
	
	
	
	
	
	
	
	
}
