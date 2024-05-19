package org.mq.captiway.scheduler;

import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.RetailProSalesCSV;
import org.mq.captiway.scheduler.beans.CustomerSalesUpdatedData;
import org.mq.captiway.scheduler.dao.AbstractSpringDao;
import org.mq.captiway.scheduler.dao.CustomerSalesUpdatedDataDao;
import org.mq.captiway.scheduler.dao.CustomerSalesUpdatedDataDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.utils.ServiceLocator;
import org.mq.captiway.scheduler.dao.SalesLiteralHashCodeGenerateDao;

public class UpdateCustomerSalesData {
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	
	
	public boolean updateOrInsertSalesAggData(RetailProSalesCSV rProSalesObj,long userId, String salesLiteralId){
		
		
		try {
			CustomerSalesUpdatedData custSalesDataObj = null ;
			
			CustomerSalesUpdatedDataDao customerSalesUpdatedDataDao = (CustomerSalesUpdatedDataDao)
					ServiceLocator.getInstance().getDAOByName("customerSalesUpdatedDataDao");
			CustomerSalesUpdatedDataDaoForDML customerSalesUpdatedDataDaoForDML = (CustomerSalesUpdatedDataDaoForDML)
					ServiceLocator.getInstance().getDAOForDMLByName("customerSalesUpdatedDataDaoForDML");
		
			
			
			try {
				custSalesDataObj = customerSalesUpdatedDataDao.findObjByCustAndUserId(rProSalesObj.getCustomerId(),rProSalesObj.getUserId());
			} catch (Exception e) {
				logger.error("Exception occured while getting the existed obj.. so returning..");
				return false;
			}
			
			custSalesDataObj = custSalesDataObj == null ? new CustomerSalesUpdatedData() : custSalesDataObj;
			Double curntBasketSize = rProSalesObj.getQuantity();
			if(curntBasketSize != null){
//				if(custSalesDataObj != null){
					curntBasketSize =   custSalesDataObj.getBasketSize() != null ? 
							curntBasketSize+custSalesDataObj.getBasketSize() :curntBasketSize;
							
//				}
			}
			else{
				curntBasketSize= custSalesDataObj.getBasketSize() != null ? custSalesDataObj.getBasketSize():0.0;
			}
			
			Double curSalesPrices = rProSalesObj.getSalesPrice();
			if(curSalesPrices == null) {
				curSalesPrices = 0.0;
			}
			if(rProSalesObj.getQuantity() != null){
				curSalesPrices = curSalesPrices*rProSalesObj.getQuantity();
			}
			if(rProSalesObj.getTax() != null && curSalesPrices > 0){
				curSalesPrices = curSalesPrices+rProSalesObj.getTax();
			}
			if(rProSalesObj.getDiscount() != null && curSalesPrices > 0){
				curSalesPrices = curSalesPrices - rProSalesObj.getDiscount();
			}

			Calendar calObj = rProSalesObj.getSalesDate();
			
			//set Total Visits
			/*if(calObj != null && custSalesDataObj.getLastPurchaseDate() != null){
				custSalesDataObj.setTotVisits(custSalesDataObj.getTotVisits() == null? 1:custSalesDataObj.getLastPurchaseDate().after(rProSalesObj.getSalesDate())?custSalesDataObj.getTotVisits()+1:custSalesDataObj.getTotVisits());
			}
			if(custSalesDataObj.getLastPurchaseDate() == null)
			{
				custSalesDataObj.setTotVisits(1);
			}*/
			//set Total Visits
			if(custSalesDataObj.getLastPurchaseDate() != null){
				custSalesDataObj.setTotVisits(custSalesDataObj.getTotVisits()+findVisitCount(custSalesDataObj.getLastPurchaseDate(), rProSalesObj.getSalesDate()));
				
				
			}else {
				custSalesDataObj.setTotVisits(1);
			}
			
			
			
			//setCustomer Id
			custSalesDataObj.setCustomerId(rProSalesObj.getCustomerId());
			
			//userId
			custSalesDataObj.setUserId(rProSalesObj.getUserId());
			
			//set first purchase date
			Calendar firstPurCal = custSalesDataObj.getFirstPurchaseDate();
			if(firstPurCal == null || (firstPurCal  != null && firstPurCal.after(calObj))){
				custSalesDataObj.setFirstPurchaseDate(calObj);
			}
			
			//set last Purchase Date
			Calendar lastPurCal = custSalesDataObj.getLastPurchaseDate();
			if(lastPurCal == null || (lastPurCal  != null && lastPurCal.before(calObj))){
				custSalesDataObj.setLastPurchaseDate(calObj);
			}
			
			//set toat Basket Size
			/*if(custSalesDataObj.getBasketSize() != null){
				curntBasketSize +=custSalesDataObj.getBasketSize();
			}*/
			custSalesDataObj.setBasketSize(curntBasketSize);
			
			
			//set tot Purchase amount
			if(custSalesDataObj.getTotPurchaseAmt() != null) {
				curSalesPrices +=custSalesDataObj.getTotPurchaseAmt();
			}
			custSalesDataObj.setTotPurchaseAmt(curSalesPrices);
					
			//set Total invoice
			int invoiceCount = custSalesDataObj.getTotInvoice() == null ? 0 : custSalesDataObj.getTotInvoice(); 
			SalesLiteralHashCodeGenerateDao salesLitaeralHashCodeDao=(SalesLiteralHashCodeGenerateDao) ServiceLocator.getInstance().getDAOByName("salesLitaeralHashCodeDao");
			Boolean existFlag  = salesLitaeralHashCodeDao.isCodeExist(userId, salesLiteralId);
			
			if(existFlag==false)
			{
				invoiceCount++;	
			}
			
			/*if(custSalesDataObj.getLatestDocSid() != null &&
					!(rProSalesObj.getDocSid().equals(custSalesDataObj.getLatestDocSid()))){
				invoiceCount++;	
			}*/
			//TODO check all possibilities (1) if DOC_SId not exist from the retailpro sales
			//TODO (2) if Sales Date not exists from rproSales Object due not matching with Date format
			
			logger.info("updating customersalesupdateddata");
			custSalesDataObj.setTotInvoice(invoiceCount);
			//custSalesDataObj.setLatestDocSid(rProSalesObj.getDocSid()!= null ? rProSalesObj.getDocSid() : custSalesDataObj.getLatestDocSid());
			
			
			
		
			
			//customerSalesUpdatedDataDao.saveOrUpdate(custSalesDataObj);
			customerSalesUpdatedDataDaoForDML.saveOrUpdate(custSalesDataObj);

			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	private int findVisitCount(Calendar dbDate, Calendar curSalesDate) {
		int visitCount = 0;
		
		//Set 0 to Hours and Min and Sec
		dbDate.set(Calendar.HOUR, 0);
		dbDate.set(Calendar.HOUR_OF_DAY, 0);
		dbDate.set(Calendar.MINUTE, 0);
		dbDate.set(Calendar.SECOND, 0);
		dbDate.set(Calendar.MILLISECOND, 0);
		
		curSalesDate.set(Calendar.HOUR, 0);
		curSalesDate.set(Calendar.HOUR_OF_DAY, 0);
		curSalesDate.set(Calendar.MINUTE, 0);
		curSalesDate.set(Calendar.SECOND, 0);
		curSalesDate.set(Calendar.MILLISECOND, 0);
		
		if(dbDate.before(curSalesDate)){
			visitCount++;
		}
		return visitCount;
		
	}
	
	

}
