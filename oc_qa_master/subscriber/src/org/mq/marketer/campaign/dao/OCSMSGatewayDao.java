package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;


public class OCSMSGatewayDao extends AbstractSpringDao{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
    public OCSMSGatewayDao(){
    	
    }
    private JdbcTemplate jdbcTemplate;

   	public JdbcTemplate getJdbcTemplate() {
   		return jdbcTemplate;
   	}

   	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
   		this.jdbcTemplate = jdbcTemplate;
   	}

    /*public void saveOrUpdate(OCSMSGateway ocsmsGateway){
        super.saveOrUpdate(ocsmsGateway);
    }*/
	
    /**
     * 
     * @param id
     * @return
     */
	 public List<OCSMSGateway> findByIds(String id) {
		
		String qry = "FROM OCSMSGateway WHERE id IN("+id+")";
		
		List<OCSMSGateway>  retObjectsList  = executeQuery(qry);
		
		if(retObjectsList != null && retObjectsList.size() > 0) {
			
			return retObjectsList;
		}else return null;
		
		
	}//findById
	 
	 
	 public OCSMSGateway findById(Long id) {
			
		String qry = "FROM OCSMSGateway WHERE id="+id;
		
		List<OCSMSGateway>  retObjectsList  = executeQuery(qry);
		
		if(retObjectsList != null && retObjectsList.size() > 0) {
			
			return retObjectsList.get(0);
		}else return null;
		
			
	}//findById
	// Added for admin screen
	 
	 /**
	  * 
	  * @return
	  * @throws Exception
	  */
	 public int getTotalCountOfAllGateways () throws Exception {
	    	
	    	
    	String query = " SELECT COUNT(*) FROM OCSMSGateway " ;
    	return ((Long)((getHibernateTemplate().find(query)).get(0))).intValue();
	    }//getTotalCountOfAllGateways
	 /**
	  * 
	  * @param start
	  * @param end
	  * @return
	  * @throws Exception
	  */

	 public List<OCSMSGateway> findAllGateways(int start,int end) throws Exception {
		 List<OCSMSGateway> ocSmsGatewaysList=null;
		 
		 String qry =" FROM OCSMSGateway ORDER BY id desc";
		 ocSmsGatewaysList =executeQuery(qry, start, end);
		 
		 
		 return ocSmsGatewaysList;
		
	 }//findAllGateways
	 
	/**
	 * 	
	 * @param countryStr
	 * @return
	 * @throws Exception
	 */
	 
	 public List<String> findPromotionalGateways(String countryStr) throws Exception {
		 List<String> promotionalList=null;
		 
		 String qry =" SELECT gatewayName FROM OCSMSGateway WHERE countryName='"+countryStr+"' "
		 		+ "AND accountType  ='"+Constants.SMS_TYPE_PROMOTIONAL+"' GROUP BY gatewayName ";
		 promotionalList =executeQuery(qry);
		 
		 
		 return promotionalList;
		   
		
		 
	 }//findPromotionalGateways
	 
	 public List<OCSMSGateway> findOcSMSGatewaysByUserIds(String countryStr, String gatewayStr, String accountType) throws Exception {
		 List<OCSMSGateway> promotionalList=null;
		 
		 String qry ="  FROM OCSMSGateway WHERE countryName='"+countryStr+"' AND accountType  ='"+accountType+"' AND gatewayName='"+gatewayStr+"' "; 
		 promotionalList =executeQuery(qry);
		 
		 logger.debug(promotionalList.size());
		 return promotionalList;
		   
		
		 
	 }//findPromotionalGatewaysUserIds
	 
	 public List<OCSMSGateway> findOcSMSGatewaysByAccountName(String countryStr, String gatewayStr, String accountType,String accName) throws Exception {
		 List<OCSMSGateway> promotionalList=null;
		 
		 String qry ="  FROM OCSMSGateway WHERE countryName='"+countryStr+"' AND accountType  ='"+accountType+"' "
		 		+ "AND gatewayName='"+gatewayStr+"' AND userId='"+accName+"' "; 
		 promotionalList =executeQuery(qry);
		 
		 logger.debug(promotionalList.size());
		 return promotionalList;
		   
		
		 
	 }
	 
	 
	 /**
		 * 	
		 * @param countryStr
		 * @return
		 * @throws Exception
		 */
		 
		 public List<String> findGateways(String countryStr, String type) throws Exception {
			 List<String> promotionalList=null;
			 
			 String qry =" SELECT gatewayName FROM OCSMSGateway WHERE countryName='"+countryStr+"' "
			 		+ "AND accountType  ='"+type+"' GROUP BY gatewayName ";
			 promotionalList =executeQuery(qry);
			 
			 
			 return promotionalList;
			   
			
			 
		 }//findPromotionalGateways
		 
	 
	 /**
	 * @param countryStr
	 * @return
	 * @throws Exception
	 */
	public List<String> findTransactionalGateways(String countryStr) throws Exception {
		 List<String> transactionalList=null;
		 
		 String qry ="SELECT gatewayName FROM OCSMSGateway WHERE countryName='"+countryStr+"' AND accountType  ='"+Constants.SMS_TYPE_TRANSACTIONAL+"' GROUP BY gatewayName ";
		 transactionalList =executeQuery(qry);
		 
		 
		 return transactionalList;
		 
		
		 
	 }//findTransactionalGateways
	 /**
	  * 
	  * @param countryStr
	  * @return
	  * @throws Exception
	  */
	 public List<String> findOptinGateways(String countryStr) throws Exception {
		 List<String> optinList=null;
		 
		 String qry ="SELECT gatewayName  FROM OCSMSGateway WHERE countryName='"+countryStr+"' AND accountType  ='"+Constants.SMS_SENDING_TYPE_OPTIN+"' GROUP BY gatewayName ";
		 optinList =executeQuery(qry);
		 
		 
		 return optinList;
		 
		
		 
	 }//findOptinGateways
	 
	 /**
	  * 
	  * @param countryStr
	  * @param gatewayStr
	  * @return
	  * @throws Exception
	  */
	 public List<OCSMSGateway> findPromotionalGatewaysUserIds(String countryStr ,String gatewayStr) throws Exception {
		 List<OCSMSGateway> promotionalList=null;
		 
		 String qry ="  FROM OCSMSGateway WHERE countryName='"+countryStr+"' AND accountType  "
		 		+ "='"+Constants.SMS_TYPE_PROMOTIONAL+"' AND gatewayName='"+gatewayStr+"' "; 
		 promotionalList =executeQuery(qry);
		 
		 logger.debug(promotionalList.size());
		 return promotionalList;
		   
		
		 
	 }//findPromotionalGatewaysUserIds
	 
	 
	 
	 /**
	  * 
	  * @param countryStr
	  * @param gatewayStr
	  * @return
	  * @throws Exception
	  */
	public List<OCSMSGateway> findTransactionalGatewaysUserIds(String countryStr ,String gatewayStr) throws Exception {
		 List<OCSMSGateway> transactionalList=null;
		 
		 String qry =" FROM OCSMSGateway WHERE countryName='"+countryStr+"' AND accountType  ='"+Constants.SMS_TYPE_TRANSACTIONAL+"' AND gatewayName='"+gatewayStr+"' "; 
		 transactionalList =executeQuery(qry);
		 
		 
		 return transactionalList;
		 
		
		 
	 }//findTransactionalGatewaysUserIds
	 
	/**
	 * 
	 * @param countryStr
	 * @param gatewayStr
	 * @return
	 * @throws Exception
	 */
	 public List<OCSMSGateway> findOptinGatewaysUserIds(String countryStr,String gatewayStr) throws Exception {
		 List<OCSMSGateway> optinList=null;
		 
		 String qry =" FROM OCSMSGateway WHERE countryName='"+countryStr+"' AND accountType "
		 		+ "='"+Constants.SMS_SENDING_TYPE_OPTIN+"' AND gatewayName='"+gatewayStr+"' " ;
		 optinList =executeQuery(qry);
		 
		 
		 return optinList;
		 
		
		 
	 }//findOptinGatewaysUserIds
	 
	 public OCSMSGateway findById(String ocSMSGatewayIdsStr) {
			
			String qry = "FROM OCSMSGateway WHERE id IN ("+ocSMSGatewayIdsStr+") ";
			
			List<OCSMSGateway>  retObjectsList  = executeQuery(qry);
			
			if(retObjectsList != null && retObjectsList.size() > 0) {
				
				return retObjectsList.get(0);
			}else return null;
			
				
		}
	 
	 public OCSMSGateway findForSubscriber(String gatewayName, String accountType){
		 String qry = "FROM OCSMSGateway WHERE gatewayName = '"+gatewayName+"' "
		 		+ "AND accountType='"+accountType+"' AND server='"+Constants.SUBSCRIBER_LOGGER+"'";
			
			List<OCSMSGateway>  retObjectsList  = executeQuery(qry);
			
			if(retObjectsList != null && retObjectsList.size() > 0) {
				
				return retObjectsList.get(0);
			}else return null;
		 
	 }
	 
	 public List<OCSMSGateway>  find(){
		 String qry = "FROM OCSMSGateway WHERE mode='SMPP' AND pullReports=false AND  "
		 		+ "server='"+Constants.SUBSCRIBER_LOGGER+"' AND enableSessionAlive=1 ";
			
			List<OCSMSGateway>  retObjectsList  = executeQuery(qry);
			
			return retObjectsList;
		 
	 }
		
	
	 
	 public OCSMSGateway findTransactionalGatewayUserIds(String countryStr ,String gatewayStr) throws Exception {
		 List<OCSMSGateway> transactionalList=null;
		 
		 String qry =" FROM OCSMSGateway WHERE countryName='"+countryStr+"' AND accountType  ='"+Constants.SMS_TYPE_TRANSACTIONAL+"' AND gatewayName='"+gatewayStr+"' "; 
		 transactionalList =executeQuery(qry);
		  return transactionalList.get(0);
		 
		
		 
	 }//findTransactionalGatewaysUserIds
	
}
