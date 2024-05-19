package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mq.marketer.campaign.beans.Bounces;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@SuppressWarnings({ "unchecked", "unused"})
public class BounceDao extends AbstractSpringDao {

    public BounceDao() {}

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public Bounces find(Long id) {
        return (Bounces) super.find(Bounces.class, id);
    }

    /*public void saveOrUpdate(Bounces bounces) {
        super.saveOrUpdate(bounces);
    }

    public void delete(Bounces bounces) {
        super.delete(bounces);
    }*/

    public List<Bounces> findAll() {
        return super.findAll(Bounces.class);
    }
    
    public int getBounceSizeByType(Long crId,String category){
		return ((Long)getHibernateTemplate().find("select count(sentId) from Bounces where category like '"+category+"%' and crId = " + crId).get(0)).intValue();
	}
    
    public List<Object[]> getBouncesByCategory(Long crId,String category,int firstResult,int maxResults) {
    	try {
			  //String query = "select b.sentId,cs.emailId from Bounces b, CampaignSent cs where b.sentId = cs.sentId and b.category='"+category+"' and cs.campaignReport = "+crId+")";
			  String query = "select b.sentId,cs.emailId,b.message from Bounces b, CampaignSent cs WHERE  cs.campaignReport = "+crId+
			  		" AND cs.status='Bounced' AND b.sentId = cs.sentId and b.category like '"+ category+"%' ";
			  
			  List bList = executeQuery(query, firstResult, maxResults);
			  return bList;
		  }catch (Exception e) {
			  logger.error("** Error : " + e.getMessage() + " **");
			  return null;
		  } 
    }
    
    /**
     * modified for new report screens
     * @param userId
     * @return
     */
    public List getCategoryPercentageByUser(Long userId) {
    	try {
    		List<Object[]> catList = new ArrayList<Object[]>();
    		String queryStr = "SELECT category, ROUND( (COUNT(b.bounce_id) /cr.sent) * 100 , 2)," +
    				" cr.cr_id FROM campaign_report cr , bounces b " +
    				"WHERE cr.cr_id = b.cr_id " +
    				"AND cr.user_id = " + userId + " GROUP BY cr.cr_id, b.category";
    		logger.debug("Query :" + queryStr);
    		catList = jdbcTemplate.query(queryStr,	new RowMapper(){
    			Object[] obj;
				@Override
				public Object mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					obj = new Object[3];
					obj[0] = rs.getString(1);
					obj[1] = rs.getFloat(2);
					obj[2] = rs.getLong(3);
					
					return obj;
				}
    			
    		});
    		return catList;
    		/*String queryStr = "SELECT b.category, ROUND( (COUNT(b.bounceId) /cr.sent) * 100 , 2)," +
    		" cr.crId FROM CampaignReport cr , Bounces b " +
    		"WHERE cr.crId = b.crId " +
    		"AND cr.user.userId = " + userId + " GROUP BY cr.crId, b.category";
    		//return executeQuery(queryStr);
    		*/
    	} catch (Exception e) {
    		logger.error("** Error : " + e.getMessage() + " **");
    		return null;
		}
    }
    
    public List<Object[]> getBounceCategories(String crIds) {
    	if(crIds==null || crIds.toString().trim().length()==0)
{
	return null;
}
    	String query = "SELECT category, COUNT(b.bounce_id)," +
						" cr.cr_id FROM campaign_report cr , bounces b " +
						"WHERE cr.cr_id = b.cr_id " +
						"AND cr.cr_id IN(" + crIds + ") GROUP BY cr.cr_id, b.category";
		    logger.debug("qry ===>"+query);	
    	List<Object[]> catList = jdbcTemplate.query(query, new RowMapper(){
    	          Object[] obj;
    	       @Override
    	       public Object mapRow(ResultSet rs, int rowNum)
    	         throws SQLException {
    	        obj = new Object[3];
    	        obj[0] = rs.getString(1);
    	        obj[1] = rs.getLong(2);
    	        obj[2] = rs.getLong(3);
    	        
    	        return obj;
    	       }
    	          
    	         });
if(catList==null|| catList.toString().trim().length()==0)
	 		{
	 			logger.info("crids :"+catList);
	 		return null;
	 		}
    	         return catList;
    	
    }
    
  public List getCategoryPercentageByUser(String userId, String campaignName) {
		try {
			List<Object[]> catList = new ArrayList<Object[]>();
			String queryStr = null;

			if (userId == null) {

				queryStr = "SELECT category, COUNT(b.bounce_id)," + " cr.cr_id FROM campaign_report cr , bounces b "
						+ "WHERE cr.cr_id = b.cr_id " + " GROUP BY cr.cr_id, b.category";

			}

			else if (userId != null && campaignName == null) {
				queryStr = "SELECT category, COUNT(b.bounce_id)," + " cr.cr_id FROM campaign_report cr , bounces b "
						+ "WHERE cr.cr_id = b.cr_id " + "AND cr.user_id IN(" + userId
						+ ") GROUP BY cr.cr_id, b.category";
			} else if (userId != null && campaignName != null) {

				queryStr = "SELECT category, COUNT(b.bounce_id)," + " cr.cr_id FROM campaign_report cr , bounces b "
						+ "WHERE cr.cr_id = b.cr_id " + "AND cr.user_id IN(" + userId + ") AND cr.campaign_name='"
						+ campaignName + "' GROUP BY cr.cr_id, b.category";

			}
			logger.info("Query :" + queryStr);
			catList = jdbcTemplate.query(queryStr, new RowMapper() {
				Object[] obj;

				@Override
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					obj = new Object[3];
					obj[0] = rs.getString(1);
					obj[1] = rs.getLong(2);
					obj[2] = rs.getLong(3);

					return obj;
				}

			});
			return catList;
			/*
			 * String queryStr =
			 * "SELECT b.category, ROUND( (COUNT(b.bounceId) /cr.sent) * 100 , 2),"
			 * + " cr.crId FROM CampaignReport cr , Bounces b " +
			 * "WHERE cr.crId = b.crId " + "AND cr.user.userId = " + userId +
			 * " GROUP BY cr.crId, b.category"; //return executeQuery(queryStr);
			 */
		} catch (Exception e) {
			logger.error("** Error : " + e.getMessage() + " **");
			return null;
		}
	}
    
    
    public List getCategoryPercentageByCrId(Long userId, Long campRepId) {
    	try {
    		List<Object[]> catList = new ArrayList<Object[]>();
    		String queryStr = "SELECT category, COUNT(b.bounce_id)" +
    				" FROM campaign_report cr , bounces b " +
    				"WHERE cr.cr_id=b.cr_id AND cr.cr_id ="+campRepId+
    				" AND cr.user_id = " + userId + " GROUP BY cr.cr_id, b.category";
    		logger.info("Query :" + queryStr);
    		catList = jdbcTemplate.query(queryStr,	new RowMapper(){
    			Object[] obj;
				@Override
				public Object mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					obj = new Object[2];
					obj[0] = rs.getString(1);
					obj[1] = rs.getLong(2);
					
					
					return obj;
				}
    			
    		});
    		return catList;
    		/*String queryStr = "SELECT b.category, ROUND( (COUNT(b.bounceId) /cr.sent) * 100 , 2)," +
    		" cr.crId FROM CampaignReport cr , Bounces b " +
    		"WHERE cr.crId = b.crId " +
    		"AND cr.user.userId = " + userId + " GROUP BY cr.crId, b.category";
    		//return executeQuery(queryStr);
    		*/
    	} catch (Exception e) {
    		logger.error("** Error : " + e.getMessage() + " **");
    		return null;
		}
    }
   /* public List<Object[]> getSentCampaignDetail(Long userId, String str){
		
    	try {
			return getHibernateTemplate().find("select sum(sent),sum(unsubscribes)," +
					"sum(bounces),sum(opens),sum(clicks),sum(spams) from CampaignReport where " +
					"user = " + userId + " and status in('sent')");
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}
    }*/
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
    
    public List<Object[]> getConsolidatedBounceCatByUser(String userId) {
    	try {
			List<Object[]> catList = new ArrayList<Object[]>();
			String queryStr = "SELECT category, COUNT(b.bounce_id)" +
							" FROM bounces b,campaign_report cr " +" WHERE b.cr_id in(SELECT cr.cr_id FROM campaign_report cr where cr.user_id IN("+userId +") )" +
							" and cr.cr_id=b.cr_id AND cr.status in('sent') GROUP BY cr.cr_id, b.category";
			logger.info("Query :" + queryStr);
    		catList = jdbcTemplate.query(queryStr,	new RowMapper(){
    			Object[] obj;
				@Override
				public Object mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					obj = new Object[2];
					obj[0] = rs.getString(1);
					obj[1] = rs.getLong(2);
					
					return obj;
				}
    			
    		});
			return catList;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    	
    	
    }//getConsolidatedBounceCatByUser(-)
    

    public List<Object[]> getCampConsolidatedBounceCat(Long userId, String campaignName) {
    	try {
			List<Object[]> catList = new ArrayList<Object[]>();
			String queryStr = "SELECT category, COUNT(b.bounce_id)" +
							" FROM bounces b,campaign_report cr " +" WHERE b.cr_id in(SELECT c.cr_id FROM campaign_report c where c.user_id="+userId +" AND c.campaign_name='"+campaignName+"' )" +
							" and cr.user_id="+userId+" AND b.cr_id=cr.cr_id and cr.campaign_name='"+campaignName+"' AND cr.status in('sent') GROUP BY cr.cr_id, b.category";
			logger.info("Query :" + queryStr);
    		catList = jdbcTemplate.query(queryStr,	new RowMapper(){
    			Object[] obj;
				@Override
				public Object mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					obj = new Object[2];
					obj[0] = rs.getString(1);
					obj[1] = rs.getLong(2);
					
					return obj;
				}
    			
    		});
			return catList;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    	
    	
    }//getConsolidatedBounceCatByUser(-)
    
    
    
    
    
    
    
    
    
    public List<Object[]> getConslBounceCatFor3Months(String userId) {
    	
    	try {
			List<Object[]> catList = new ArrayList<Object[]>();
			
			
			Date fromDate = new Date();
			fromDate.setMonth(fromDate.getMonth() - 3);
			
			String queryStr = "SELECT category, COUNT(b.bounce_id)" +
					" FROM campaign_report cr , bounces b " +
					"WHERE b.cr_id in(SELECT cr_id FROM campaign_report cr where user_id IN("+userId+") ) " +
					" and cr.sent_date > '" + format.format(fromDate) + "' and cr.cr_id=b.cr_id AND cr.status in('sent') GROUP BY cr.cr_id, b.category";
			logger.info("Query :" + queryStr);
    		catList = jdbcTemplate.query(queryStr,	new RowMapper(){
    			Object[] obj;
				@Override
				public Object mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					obj = new Object[2];
					obj[0] = rs.getString(1);
					obj[1] = rs.getLong(2);
					
					return obj;
				}
    			
    		});
			return catList;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    	
    	
    	
    	
    }//getConslBounceCatFor3Months(-)
    
    
    
}

