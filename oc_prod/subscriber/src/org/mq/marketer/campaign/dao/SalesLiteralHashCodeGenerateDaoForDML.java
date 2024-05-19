package org.mq.marketer.campaign.dao;

import java.util.Collection;
import java.util.List;

import org.mq.marketer.campaign.beans.SalesLiteralHashCode;
import org.springframework.jdbc.core.JdbcTemplate;

public class SalesLiteralHashCodeGenerateDaoForDML extends AbstractSpringDaoForDML {
	
	
private JdbcTemplate jdbcTemplate;
	
    public SalesLiteralHashCodeGenerateDaoForDML(){}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


    public void saveOrUpdate(SalesLiteralHashCode saleLiteralObj){
        super.saveOrUpdate(saleLiteralObj);
    }

    public void delete(SalesLiteralHashCode saleLiteralObj){
        super.delete(saleLiteralObj);
    }

   /* public void saveByCollection(Collection<SalesLiteralHashCode> salesLiteralColObj){
//    	super.saveOrUpdateAll(retailProSalesCollection);
    	getHibernateTemplate().saveOrUpdateAll(salesLiteralColObj);
    }*/
    
   /* public boolean existSalesLiteralObjByListId(long listId, String salesLiteralId) {
 	   
 	   List<SalesLiteralHashCode> tempList  = getHibernateTemplate().find("from SalesLiteralHashCode  where listId ="+listId +"  and salesLiteralHashCode = '" + salesLiteralId.trim()+"'");
 	   
 	   if(tempList != null && tempList.size() > 0) {
 		   return true;
 	   }
 	   return false;
    }*/
     


  /*public boolean isCodeExist(long userId, String salesLiteralId) {
 	   
 	   List<SalesLiteralHashCode> tempList  = 
 			   getHibernateTemplate().find("FROM SalesLiteralHashCode  WHERE listId ="+listId +
 					   "  AND salesLiteralHashCode = '" + salesLiteralId.trim()+"' AND currentFile = false");
 	   
 	  List<SalesLiteralHashCode> tempList  = 
 			  this.executeQuery("FROM SalesLiteralHashCode  WHERE userId ="+userId +
				   "  AND salesLiteralHashCode = '" + salesLiteralId.trim()+"' AND currentFile = false", 
				   0, 5);
 	   
 	   if(tempList != null && tempList.size() > 0) {
 		   return true;
 	   }
 	   return false;
    }*/

    public int  updateCurrentFileFlag(long userId) {
    	
    	String qry="UPDATE SalesLiteralHashCode SET currentFile = false WHERE userId ="+userId +" AND currentFile = true ";
    	
    	int updateCount = getHibernateTemplate().bulkUpdate(qry);
    	return updateCount;
    }
  	   


}
