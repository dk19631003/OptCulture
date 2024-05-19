package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.beans.SupportTicket;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;

public class SupportTicketDaoForDML extends AbstractSpringDaoForDML{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	 private static String allStr = "--All--";
	
	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	/*public SupportTicket find(Long id) {
		return (SupportTicket) super.find(SupportTicket.class, id);
	}*/

	public void saveOrUpdate(SupportTicket supportTicket) throws Exception {
		super.saveOrUpdate(supportTicket);
	}

	public void delete(SupportTicket supportTicket) {
		super.delete(supportTicket);
	}
/*	  public List<SupportTicket> findBynumberOfTickets(int startFrom, int count) throws Exception {
	    	
	    	List<SupportTicket> numOfTicketsList=null;
	    	try {
				
				String qry=" FROM SupportTicket ORDER BY ticketId desc";
				numOfTicketsList = executeQuery(qry, startFrom, count);

	    	} catch (DataAccessResourceFailureException e) {
				// TODO Auto-generated catch block
				logger.error("Exception " ,e);
			} catch (HibernateException e) {
				// TODO Auto-generated catch block
				logger.error("Exception " ,e);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				logger.error("Exception " ,e);
			} catch( Exception e) {
				logger.error("Exception " ,e);
			}
			return numOfTicketsList;
	    }//findBynumberOfReports()
	
	  
	     * this method fetches the desired number of records for desired user (dashboard)
	     
	    
	    public List<SupportTicket> findByUserAndOrg(String orgName,int startFrom,int count,String userName) throws Exception {
	    	
	    	List<SupportTicket> numOfticketsList=null;
	    	
	    		String qry= "";
	    		
	    	
	        	String appendquery="";
	        	
	        	
	        	if(!userName.equals(allStr)){
	        		
	        		appendquery = "AND userName = '"+userName+"' ";
	        	}
	        	String endQry=" ORDER BY ticketId desc";
	        	qry="FROM SupportTicket where userOrgName= '"+orgName+"' " +appendquery+endQry ;
				logger.info(">>>>>>>>>>>>>>>>>>>>>>>>query is ::::"+qry);
				numOfticketsList=executeQuery(qry,startFrom,count);
			    logger.info("scheduled based on selection "+numOfticketsList.size());
				
			
		    
			return numOfticketsList;
	    	
	    	
	    }//findByUserName()
	    
	    public int getTotalCountOfAllTickets () throws Exception {
	    	
	    	
	    	String query = " SELECT COUNT(*) FROM SupportTicket ";
	    	return ((Long)((getHibernateTemplate().find(query)).get(0))).intValue();
	    }

	    public int getTotalCountOfTicketsByOrgIdAndUserId (String orgName,String userName ) throws Exception {
	    	
	    	String appendQuery = "";
	    	
	    	if(userName != null){
	    		appendQuery = " AND userName = '"+userName+"' ";
	    	}
	    	
	    	String query = "SELECT COUNT(*) FROM SupportTicket WHERE userOrgName = '"+orgName+"' " + appendQuery  ;
	    	return ((Long)((getHibernateTemplate().find(query)).get(0))).intValue();
	    }
	    
	    public List<SupportTicket> getTicketsByOrgIdAndUserId(String orgName,String userName) throws Exception{
	    	
	    	String appendQuery = "";
	    	
	    	if(userName != null){
	    		appendQuery = " AND userName = '"+userName+"' ";
	    	}
	    	
	    	String query = "FROM SupportTicket WHERE userOrgName = '"+orgName+"' " +appendQuery ;
	    	List<SupportTicket> list =(List<SupportTicket>) getHibernateTemplate().find(query);//(List<Users>) getHibernateTemplate().find(query);
			return list;
	    }*/
	    
}
