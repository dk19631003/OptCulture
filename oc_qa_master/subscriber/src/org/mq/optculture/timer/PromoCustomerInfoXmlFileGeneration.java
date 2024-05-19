package org.mq.optculture.timer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.CouponCodesDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.couponcodes.CustomerPromo;
import org.mq.optculture.model.couponcodes.CustomerPromos;
import org.mq.optculture.model.couponcodes.PromoCustomerInfo;
import org.mq.optculture.model.couponcodes.PromoCustomerInformation;
import org.mq.optculture.utils.ServiceLocator;
import org.mq.optculture.utils.XMLUtil;


public class PromoCustomerInfoXmlFileGeneration implements Runnable{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public Queue<Object[]> uploadQueue = new LinkedList();
	Object[] pollObj;
	
	public void run(){
		while(pollQueue()) {
			preparePromoCustomerInfofile();
		}
		
		pollObj = null;
		//System.gc();
	} // run
	
	
	private void preparePromoCustomerInfofile(){
		try {
			
			logger.debug("PromoCustomerInfoXmlFileGeneration thread running now ::");
			if(pollObj == null) return;
			
			Coupons coupObj = (Coupons)pollObj[0];
			UserOrganization orgObj = (UserOrganization)pollObj[1];
//			String orgName = pollObj[1].toString();
			
			CouponCodesDao couponCodesDao = (CouponCodesDao) ServiceLocator.getInstance().getDAOByName("couponCodesDao");
			ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName("contactsDao");
			
			long totalCount = 0;
			logger.debug("Coup Objec name is  ::"+coupObj.getCouponName());
			
			totalCount = couponCodesDao.getCouponCodeCountByCond(coupObj.getCouponId());
			logger.debug("totalCountis  ::"+totalCount);
			if(totalCount == 0l) {
				logger.info("No ccodes found for this promo Id :"+coupObj.getCouponId());
				return;
			}
			int endIdx= 1000;
			
			int fileCount = 0;
			for (int initailCount = 0; initailCount < totalCount; initailCount+=endIdx) {
				
				
				
				List<Object[]> countByContObjArr = couponCodesDao.getCouponCodeByCond(coupObj.getCouponId(), initailCount, endIdx);
				
				if(countByContObjArr == null || countByContObjArr.size() == 0){
					logger.debug("No CountBy ContObjArr is null ::::");
					return;
				}
				String contactIds ="";
				
				Map<Long, Integer> conatIdCountMap = new HashMap<Long, Integer>() ;
				for (Object[] eachObj : countByContObjArr) {
					if(eachObj == null || eachObj[0] == null ) continue;
					
					contactIds += contactIds.trim().length() == 0 ? eachObj[0]+"" :","+eachObj[0];
					
					conatIdCountMap.put((Long)eachObj[0], (Integer)eachObj[1]);
					
				}//for each for Contacts Id
				
				logger.debug("contactIds are  ::"+contactIds);
				logger.debug("conatIdCountMap is  ::"+conatIdCountMap);
				
				List<Contacts> contactsObjList = contactsDao.findByQuery("FROM Contacts WHERE  contactId in ("+contactIds+")");
				
				Map<Long,Contacts> conatctMap = new HashMap<Long, Contacts>();
				if(contactsObjList != null && contactsObjList.size() > 0) {
					for (Contacts eachContObj : contactsObjList) {
						conatctMap.put(eachContObj.getContactId(), eachContObj);
					}
				}
				
				Set<Long> contKeySet = conatIdCountMap.keySet();
				logger.debug("contKeySet size is  :: ::"+contKeySet.size());
				List<PromoCustomerInfo> promoCustList = new ArrayList<PromoCustomerInfo>();
				
				for (Long eachConId : contKeySet) {
										
					Contacts contObj = conatctMap.get(eachConId);
					
					if(contObj == null) {
						logger.debug("No Contact is Found :::::");
						continue;
					}
					PromoCustomerInfo promoCustomerInfo = new PromoCustomerInfo();
					promoCustomerInfo.setCustomerId(contObj.getExternalId() == null ||  contObj.getExternalId().trim().isEmpty() ? "" : contObj.getExternalId());
					promoCustomerInfo.setEmailId(contObj.getEmailId() == null ? "" : contObj.getEmailId());
					promoCustomerInfo.setMobilePhone(contObj.getMobilePhone() == null ? "" : contObj.getMobilePhone());
					promoCustomerInfo.setStatus("REDEEMED");
					promoCustomerInfo.setRedeemCount(""+conatIdCountMap.get(eachConId));
					promoCustList.add(promoCustomerInfo);
				
				}
				
				logger.debug("promoCustList size is  ::"+promoCustList.size());
				
				CustomerPromo custmerPromoObj = new CustomerPromo();
				custmerPromoObj.setOrgId(orgObj.getOrganizationName());
				custmerPromoObj.setPromoName(coupObj.getCouponName());
				custmerPromoObj.setPromoCode(coupObj.getCouponCode());
				
				PromoCustomerInformation promoCustomerInformationObj = new  PromoCustomerInformation();
				
				promoCustomerInformationObj.setPromoCustomList(promoCustList);
				
				custmerPromoObj.setPromoCustomerInformation(promoCustomerInformationObj);
				//setPromoCustomList(promoCustList);
				
				CustomerPromos promosPojoObj = new CustomerPromos();
				promosPojoObj.setCustmerPromo(custmerPromoObj);
				WritePromoCustolerInfoFileAsXML(coupObj.getCouponName(),orgObj,promosPojoObj,fileCount);
				fileCount++;
			}
			
			
		} catch (Exception e) {
			logger.error("Error :",e);
		}
		
		
	}// preparePromoCustomerInfofile
	
	private void WritePromoCustolerInfoFileAsXML(String couponName, UserOrganization orgObj ,CustomerPromos promosPojoObj,int fileCount) {
		
		try {
			String outboxPromoPath = PropertyUtil.getPropertyValue("outboxpromo");
			String orgOutboxPromoPath = outboxPromoPath+File.separator+orgObj.getOrgExternalId().toLowerCase();
			logger.info(couponName+" is Promo Name and Path orgOutboxPromoPath is ::"+orgOutboxPromoPath);
			File orgFolder = new File(orgOutboxPromoPath);
			if(!orgFolder.isDirectory()){
				orgFolder.mkdir();
			}
			String pathStr = orgOutboxPromoPath+File.separator+couponName+"_"+orgObj.getOrgExternalId().toLowerCase()+"_CustomerInfo_"+fileCount+".xml";
			XMLUtil.marshalAndWriteToFileAsXML(pathStr, CustomerPromos.class,promosPojoObj);
			
			

			File createdXmlFile = new File(pathStr);
			if(!createdXmlFile.exists()) {
				logger.debug(" XML file not creted with the Jaxb marshal of this file "+pathStr);
			}else {
				
				//create the zip file 
				boolean zipFileCreationflag = XMLUtil.zipFileCreation(pathStr, pathStr+".zip");
				logger.info("zip file is creation is  "+zipFileCreationflag +" of  this file ... "+pathStr);
				
				//delete or move to done folder
				//move to done folder
				String donePath = PropertyUtil.getPropertyValue("donepromo");
				if(!donePath.endsWith(File.separator) ){
					donePath = donePath+File.separator;
				}
				createdXmlFile.renameTo(new File(donePath+createdXmlFile.getName()));
			}
			
			
			
			
		} catch (BaseServiceException e) {
			logger.error("Error :",e);
		}
	}
	
	boolean pollQueue(){
		pollObj = uploadQueue.poll();
		if(pollObj!=null) {
			return true;
		} 
		else {
			return false;
		}
	} // pollQueue
}
