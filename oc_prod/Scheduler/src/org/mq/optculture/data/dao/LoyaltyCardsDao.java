package org.mq.optculture.data.dao;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.LoyaltyCards;
import org.mq.captiway.scheduler.dao.AbstractSpringDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.utils.OCConstants;

public class LoyaltyCardsDao  extends AbstractSpringDao implements Serializable  {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	/*public void saveOrUpdate(LoyaltyCards loyaltyCards) {
        getHibernateTemplate().saveOrUpdate(loyaltyCards);
    }
*/
	public LoyaltyCards findByCardNoAndOrgId(String cardNo, long orgId) {
		String query = " FROM LoyaltyCards WHERE cardNumber = '"+cardNo+"' AND orgId = "+orgId;
		
		List<LoyaltyCards> list =  getHibernateTemplate().find(query);
		if(list!=null && list.size()>0) {
			return list.get(0);
		}
		else return null;
	}
	
	public LoyaltyCards findByCardNoAnduserId(String cardNo, long userId) {
		String query = " FROM LoyaltyCards WHERE cardNumber = '"+cardNo+"' AND userId = "+userId;
		
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
		
	}
	/*public void deleteByPrgmId(Long prgmId) {
		String queryStr = " DELETE FROM LoyaltyCards WHERE programId = "+prgmId.longValue();
		
		getHibernateTemplate().bulkUpdate(queryStr);
		
	}*/
	
	public LoyaltyCards findCardByProgram(String programIdStr, String cardSetIdStr, String cardNumber, Long userId){
		
		String queryStr = " FROM LoyaltyCards WHERE programId IN ("+programIdStr+") AND cardSetId IN ("+cardSetIdStr+")"
				+ " AND userId = "+userId+" AND cardNumber = '"+cardNumber+"'";
		
		List<LoyaltyCards> cardList = getHibernateTemplate().find(queryStr);
		if(cardList != null && cardList.size() > 0){
			return cardList.get(0);
		}
		else return null;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
