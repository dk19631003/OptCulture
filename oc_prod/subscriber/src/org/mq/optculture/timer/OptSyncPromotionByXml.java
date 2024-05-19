package org.mq.optculture.timer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.mq.marketer.campaign.beans.CouponCodes;
import org.mq.marketer.campaign.beans.CouponDiscountGeneration;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CouponCodesDao;
import org.mq.marketer.campaign.dao.CouponDiscountGenerateDao;
import org.mq.marketer.campaign.dao.CouponDiscountGenerateDaoForDML;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.couponcodes.DiscountInformation;
import org.mq.optculture.model.couponcodes.OptSyncPromoDiscountInfo;
import org.mq.optculture.model.couponcodes.OrgPromos;
import org.mq.optculture.model.couponcodes.OrgPromotion;
import org.mq.optculture.model.couponcodes.PromoGeneration;
import org.mq.optculture.model.couponcodes.PromoItemCodeInfo;
//import org.mq.optculture.model.couponcodes.PromoCodes;
import org.mq.optculture.model.couponcodes.PromoParentDescription;
import org.mq.optculture.model.couponcodes.PromoParentRootObject;
//import org.mq.optculture.model.couponcodes.PromoStore;
import org.mq.optculture.model.couponcodes.PromoStoreNumber;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.mq.optculture.utils.XMLUtil;

public class OptSyncPromotionByXml extends TimerTask {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private final String owner = PropertyUtil.getPropertyValueFromDB("owner");
	private final String group = PropertyUtil.getPropertyValueFromDB("group");
	
	public void run(){
		
		try {
			
			logger.info("Started OptSyncPromotionByXml timer...");
			
			
			promoOutboxFilesGeneration();
		} catch (Exception e) {
			logger.error("Error : occured while generaing the Marshal and unmarshal the Objects ",e);
		}
	
	}
	
	
	
	
	
	
	/*public boolean inboxFileExist(String inboxPath) throws Exception {

		File file = new File(inboxPath);
		if(file.exists() && file.isDirectory()){
			if(file.list().length > 0){
				return true;
			}
			else return false;
		}
		return false;
	}*/
	
	
	/*private boolean validateXmlWithXsd(String xmlfile) {
		logger.info("Started schema validation with file : "+xmlfile);
		try {
			String enrollxsd = PropertyUtil.getPropertyValue("promoRedemtionschema");
			return XMLUtil.validateXMLwithSchema(xmlfile, enrollxsd);
		} catch (BaseServiceException e) {
			logger.error("Error occured validation of xml to xsd " , e);
			return false;
		}
		finally{
			logger.info("Completed schema validation with file : "+xmlfile);
		}
	}*/
	
	
	/*public boolean renameAndMove(String xmlFile){
		logger.info("Started renaming and moving a file : "+xmlFile);
		try{
			String donePath = PropertyUtil.getPropertyValue("done");
			String renameCmd = "mv "+xmlFile+" "+xmlFile+".txt";
			Runtime.getRuntime().exec(renameCmd);
			
			String moveCmd = "mv "+xmlFile+".txt"+" "+donePath;
			Runtime.getRuntime().exec(moveCmd);
		}catch(Exception e){
			logger.error("Exception in renameandmove enroll xml file" ,e);
			return false;
		}
		logger.info("Completed renaming and moving a file : "+xmlFile);
		return true;
	}*/
	
	
	private void writeFailedMessage(String reason, String xmlfileName, String extention){
		logger.info("Started writing failed message of file :"+xmlfileName);		
		String outboxPromoPath = PropertyUtil.getPropertyValue("outboxpromo");
		FileWriter writer;
		String fileName = xmlfileName.substring(0, xmlfileName.indexOf(extention))+".txt";
		try {
			
			File file = new File(outboxPromoPath+"/"+fileName);
			writer = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(writer);
			StringBuffer str = new StringBuffer();
			str.append("Status : Not Processed");
			str.append("\r\n");
			str.append("Reason: ");
			str.append(reason);
			bw.write(str.toString());
			
			bw.flush();
			bw.close();
		} catch (IOException e) {
			logger.error("Exception in while writing failed message.", e);
		}
		
		//change the user/group and  the permissions to the file
				try {
					String chOwnCmd = "chown "+owner+":"+group+" "+outboxPromoPath+"/"+fileName;
					logger.debug("chaging the file permissions =="+ chOwnCmd);
					Runtime.getRuntime().exec(chOwnCmd);
					
					String chmodCmd = "chmod 777 "+outboxPromoPath+"/"+fileName;
					Runtime.getRuntime().exec(chmodCmd);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception in while writing failed message.", e);
				}
		
		logger.info("Completed writing failed message of file :"+xmlfileName);
	}
	
	/*public boolean checkFileWithXmlExtension(String xmlfile) {
		logger.info("Started checking xml extension of file : "+xmlfile);
		boolean status = false;
		if(xmlfile != null && xmlfile.trim().endsWith(".xml")){
			status = true;
		}
		logger.info("Completed checking xml extension of file : "+xmlfile);
		return status;
	}*/

	
	
	private void promoOutboxFilesGeneration(){

		CouponsDao couponsDao = null;
		CouponCodesDao couponCodesDao = null;
		try {
			couponsDao = (CouponsDao)ServiceLocator.getInstance().getDAOByName("couponsDao");
			
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName("usersDao");
			couponCodesDao = (CouponCodesDao)ServiceLocator.getInstance().getDAOByName("couponCodesDao");
			
			List<UserOrganization> userOrgList = usersDao.findAllOrganizations();
			
			if(userOrgList == null) {
				logger.debug("No Organization exists :");
				return;
			}else if(userOrgList.size() == 0){
				logger.debug("No Organization exists :");
				return;
			}
			logger.debug("userOrgList size is  ::: "+userOrgList.size());
			
			List<Coupons> orgPromosList = null;
			
			for (UserOrganization eachOrg : userOrgList) {
				
				try {
		//			orgPromosList = couponsDao.findCouponsByOrgId(eachOrg.getUserOrgId());
					
					orgPromosList = couponsDao.findStaticCouponsByOrgId(eachOrg.getUserOrgId());
					
					if(orgPromosList == null || orgPromosList.size() == 0) {
						logger.debug("No promos exist for this Organization id :"+eachOrg.getUserOrgId());
						continue;
					}
					
					//Create Root Object  like ORG 
					OrgPromotion orgProm = new OrgPromotion();
					//set Orgname 
					orgProm.setOrgName(eachOrg.getOrganizationName()); 
					
					
					int orgCount = 0;
					int fileCount = 0;
					
					for (Coupons eachPromo : orgPromosList) {
						
						logger.debug(eachPromo.getOrgId()+ " :: Promo is  : :"+eachPromo.getCouponName()+"  promoId is ::"+eachPromo.getCouponId());
						
						if(eachPromo.getStatus().contains("Expired") || eachPromo.getStatus().contains("Paused")) {
							continue;
						}
						
						long promoCount = 0;
						if(!eachPromo.getCouponGeneratedType().equals("single")) { // if its multiple
							
							String stausStr = "'"+Constants.COUP_CODE_STATUS_ACTIVE+"','"+Constants.COUP_CODE_STATUS_INVENTORY+"'";
							promoCount = couponCodesDao.findCoupCodeCountByCoupAndStatus(eachPromo.getCouponId(),stausStr);
							
							logger.debug(eachPromo.getCouponName()+" ::eachPromo multple promo count is  :"+promoCount);
							if(promoCount == 0) {
								continue;
							}
							orgCount += promoCount;
							
						}else {
							orgCount++;
						}
						
						if(orgCount  > 10000){
							
							logger.debug("prepare Org Promos xml file if promo count reach more than 10000 and file number is ::"+fileCount);
							// Prepare Organizational Promos
							orgProm = prepareOrgPromoObject(orgProm, eachPromo,promoCount);
							
							//For every more than 10000 promcount , write Org Promos Obj as XML in out box if Promo count reach more than 10000 
							writeOrgPromoFileAsXml(orgProm,fileCount,eachOrg);
							
							fileCount++;
							orgCount = 0;
							
							orgProm = new OrgPromotion();
							orgProm.setOrgName(eachOrg.getOrganizationName());
							
						}else {
							logger.debug("first time Org Promo  xml file created if count less than 10000 and file number is ::"+fileCount);
							orgProm = prepareOrgPromoObject(orgProm, eachPromo,promoCount);
						}
						
						
						//Promo Parent Description xml file Generation
						try {
							writePromoDescriptionFileAsXML(eachPromo,eachOrg);
						} catch (Exception e) {
							logger.error("Exception occured while generating xml file of   Promos description of this Promo  "+eachPromo.getCouponId() , e);
						}
						
					} // for Coupons
					
					//write Org Promos Obj as XML in out box if stil exists any 
					if(orgCount >0) {
						
						writeOrgPromoFileAsXml(orgProm,fileCount,eachOrg);
					}
				} catch (Exception e) {
					logger.error("Exception in while generating the Organizational promos as xml file of this Org "+eachOrg.getOrganizationName() , e);
				}
			
			}
			
			
		} catch (Exception e) {
			logger.error("Exception Occured in XMLfile Generation   ", e);
		}
		
		
	}
	
	
	
	private OrgPromotion prepareOrgPromoObject(OrgPromotion orgPromo,Coupons coupObj, long promoCount) {
		
		List<OrgPromos> promoNamelist = null;
		promoNamelist = orgPromo.getPromoNameList();
		
		if(promoNamelist == null){
			promoNamelist = new ArrayList<OrgPromos>();
		}
		logger.debug("coupObj is  ::"+coupObj.getCouponName());
		
		CouponCodesDao couponCodesDao = null;
		try {
			couponCodesDao = (CouponCodesDao)ServiceLocator.getInstance().getDAOByName("couponCodesDao");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OrgPromos promoNameObj = new OrgPromos();
		promoNameObj.setName(coupObj.getCouponName());
		promoNameObj.setType(coupObj.getCouponGeneratedType());
		List promoCodeList = new ArrayList();
		
		
		if(coupObj.getCouponGeneratedType().equals("single")) {
			logger.debug("single :::");
//			PromoCodes promoCodes= new PromoCodes();
//			promoCodes.setPromoCode(coupObj.getCouponCode());
			promoCodeList.add(coupObj.getCouponCode());
			
			
		}else {
			logger.debug("multiple :::");
			
			List<CouponCodes> promoCodeList1 = null;
			for (int initalCount = 0; initalCount < promoCount; initalCount+= 1000) {
				
				promoCodeList1 	= couponCodesDao.findByCouponId(coupObj.getCouponId(), initalCount , 1000);
				logger.debug("promoCodeList1 size is  :: "+promoCodeList1.size());
				if(promoCodeList1 != null && promoCodeList1.size() > 0){
					
					for (CouponCodes eachCCode : promoCodeList1) {
						/*PromoCodes promoCodes= new PromoCodes();
						promoCodes.setPromoCode(eachCCode.getCouponCode());*/
						promoCodeList.add(eachCCode.getCouponCode());
					}
				}
			}
			
		}
		promoNameObj.setPromoCodeList(promoCodeList);
		promoNamelist.add(promoNameObj);
		
		orgPromo.setPromoNameList(promoNamelist);
		return orgPromo;
		
	} //prepareOrgPromoObject
	
	private void writeOrgPromoFileAsXml(OrgPromotion orgPromo,int count,UserOrganization orgObj){
		
		try {
			 String owner = PropertyUtil.getPropertyValueFromDB("owner");
			 String group = PropertyUtil.getPropertyValueFromDB("group");
			
			String outboxPromoPath = PropertyUtil.getPropertyValue("outboxpromo");
			String orgOutboxPromoPath = outboxPromoPath+File.separator+orgObj.getOrgExternalId().toLowerCase();
			logger.debug(" Org promos generating path is  ::::"+orgOutboxPromoPath);
			File orgFolder = new File(orgOutboxPromoPath);
			if(!orgFolder.isDirectory()){
				orgFolder.mkdir();
			}
			String fileNameStr = orgOutboxPromoPath+File.separator+orgObj.getOrgExternalId().toLowerCase()+"_promos_"+count+".xml";
			XMLUtil.marshalAndWriteToFileAsXML(fileNameStr, OrgPromotion.class,orgPromo);
			
			
			File createdXmlFile = new File(fileNameStr);
			if(!createdXmlFile.exists()) {
				logger.debug(" XML file not creted with the Jaxb marshal of this file "+fileNameStr);
			}else {
				
				//create the zip file 
				boolean zipFileCreationflag = XMLUtil.zipFileCreation(fileNameStr, fileNameStr+".zip");
				logger.info("zip file is creation is  "+zipFileCreationflag +" of  this file ... "+fileNameStr);
				
				try {
					String chOwnCmd = "chown "+owner+":"+group+" "+fileNameStr+".zip";
					logger.debug("chaging the file permissions =="+ chOwnCmd);
					Process p = Runtime.getRuntime().exec(new String[] {"bash", "-c",chOwnCmd});
	                p.waitFor();
					String chmodCmd = "chmod 777 "+fileNameStr+".zip";
					Process p1 = Runtime.getRuntime().exec(new String[] {"bash", "-c",chmodCmd});
	                p1.waitFor();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception in while writing failed message.", e);
				}
				
				//delete or move to done folder
				//move to done folder
				String donePath = PropertyUtil.getPropertyValue("donepromo");
				if(!donePath.endsWith(File.separator) ){
					donePath = donePath+File.separator;
				}
				createdXmlFile.renameTo(new File(donePath+createdXmlFile.getName()));
			}
			
		
		} catch (BaseServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//prepare Promo Description file as XML
	private void writePromoDescriptionFileAsXML(Coupons couponObj,UserOrganization orgObj) throws Exception{
		
		
		logger.debug("Promo Parent Name description xml file generation  ..... "+couponObj.getCouponName());
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		Long orgOwner = usersDao.getOwnerofOrg(orgObj.getUserOrgId());
		
		
		PromoParentDescription promoParentDescObj = new PromoParentDescription();
		
		
		//promoName
		promoParentDescObj.setPromoName(couponObj.getCouponName());
		//type
		promoParentDescObj.setType(couponObj.getCouponGeneratedType());
		//status
		promoParentDescObj.setStatus(couponObj.getStatus());
		//orgName
		promoParentDescObj.setOrgName(orgObj.getOrganizationName());
		//ValidFrom
		promoParentDescObj.setValidFrom(MyCalendar.calendarToString(couponObj.getCouponCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR));
		//validTo
		promoParentDescObj.setValidTo(MyCalendar.calendarToString(couponObj.getCouponExpiryDate(), MyCalendar.FORMAT_DATETIME_STYEAR));
		//loyaltypoints
		promoParentDescObj.setLoyaltypoints(couponObj.getLoyaltyPoints() == null ? "" :""+couponObj.getLoyaltyPoints());
		//storeNumFlag
		promoParentDescObj.setStoreNumFlag(couponObj.getAllStoreChk() == null ||  !couponObj.getAllStoreChk()? "FALSE" : "TRUE");
		
		//promoStoreNumber
		String storeNumStr = couponObj.getSelectedStores();
		List<String> storeNumList = new ArrayList<String>();
		if(storeNumStr != null && storeNumStr.trim().length() >0) {
			String[] storeArr = storeNumStr.trim().split(";=;");
			for (String eachStore : storeArr) {
				/*PromoStore promoStore = new PromoStore();
				promoStore.setStore(eachStore.trim());*/
				storeNumList.add(eachStore.trim());
			}
		}
		PromoStoreNumber promoStorenumber = new PromoStoreNumber();
		promoStorenumber.setStoreList(storeNumList);
		promoParentDescObj.setPromoStoreNumber(promoStorenumber);
		
		
		//discountCriteria
		String disCountType ="";
		String discountCriteriaStr = "";
		if(couponObj.getDiscountType().equals("Percentage") && couponObj.getDiscountCriteria().equals("Total Purchase Amount")) {
			discountCriteriaStr = "PERCENTAGE-MVP";
			disCountType = "P";
		}
		else if(couponObj.getDiscountType().equals("Percentage") && couponObj.getDiscountCriteria().equals("SKU")) {
			discountCriteriaStr = "PERCENTAGE-IC";
//			isItemCode = true;
			disCountType = "P";
		}
		else if(couponObj.getDiscountType().equals("Value") && couponObj.getDiscountCriteria().equals("Total Purchase Amount")) {
			discountCriteriaStr = "VALUE-MVP";
			disCountType = "V";
		}
		else {
			discountCriteriaStr = "VALUE-I";
//			isItemCode = true;
			disCountType = "V";
		}
		promoParentDescObj.setDiscountCriteria(discountCriteriaStr);
		
		
		
		//promoGenreation
		PromoGeneration promogenerationObj = new PromoGeneration();
		
		logger.info(">>> type is  ::"+couponObj.getCouponGeneratedType()+" For this promo ::"+couponObj.getCouponName());
		if(couponObj.getCouponGeneratedType().equals("single")){
			
			promogenerationObj.setRedeemCount(couponObj.getRedeemed() == null || couponObj.getRedeemed() <= 0  ? "" : ""+couponObj.getRedeemed()); //TODO actual data from here
			promogenerationObj.setRedemtionflag(couponObj.getRedemedAutoChk() == null || !couponObj.getRedemedAutoChk() ? "False" : "True");
			if(couponObj.getRedeemdCount() != null){
				promogenerationObj.setRedemtionLimit(""+couponObj.getRedeemdCount());
			}
			promogenerationObj.setPerSubcriptionFlag(couponObj.getSingPromoContUnlimitedRedmptChk() == null || !couponObj.getSingPromoContUnlimitedRedmptChk() ? "False" : "True");
			promogenerationObj.setPerSubscriptionLimit(couponObj.getSingPromoContRedmptLimit() == null  || couponObj.getSingPromoContRedmptLimit() <= 0 ? "" : ""+couponObj.getSingPromoContRedmptLimit());
		}
		promoParentDescObj.setPromoGenreation(promogenerationObj);
		
		
		//discountInformation
		promoParentDescObj.setDiscountInformation(getDiscountInforObject(couponObj, disCountType, orgOwner));
		
		
		PromoParentRootObject promoParentRootObj = new PromoParentRootObject();
		promoParentRootObj.setPromoParentDescObj(promoParentDescObj);
		
		try {
			String outboxPromoPath = PropertyUtil.getPropertyValue("outboxpromo");
			String orgOutboxPromoPath = outboxPromoPath+File.separator+orgObj.getOrgExternalId().toLowerCase();
			logger.debug("==1===:: "+orgOutboxPromoPath);;
			File orgFolder = new File(orgOutboxPromoPath);
			if(!orgFolder.isDirectory()){
				logger.debug("===2===");
				boolean flag = orgFolder.mkdir();
				logger.debug("directry created >>> "+flag);
				try {
					String chOwnCmd = "chown "+owner+":"+group+" "+orgOutboxPromoPath;
					logger.debug("chaging the file permissions =="+ chOwnCmd);
					Runtime.getRuntime().exec(chOwnCmd);
					
					String chmodCmd = "chmod 777 "+outboxPromoPath+"/"+orgOutboxPromoPath;
					Runtime.getRuntime().exec(chmodCmd);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception in while writing failed message.", e);
				}
				
			}
			
			logger.debug("===3===");
			String pathStr = orgOutboxPromoPath+File.separator+couponObj.getCouponName()+
					"_"+orgObj.getOrgExternalId().toLowerCase()+"_description.xml";
			
			logger.info("promo parent path is  "+pathStr);
			
			XMLUtil.marshalAndWriteToFileAsXML(pathStr, PromoParentRootObject.class,promoParentRootObj);
			
			File createdXmlFile = new File(pathStr);
			if(!createdXmlFile.exists()) {
				logger.debug(" XML file not creted with the Jaxb marshal of this file "+pathStr);
			}else {
				
				//create the zip file 
				boolean zipFileCreationflag = XMLUtil.zipFileCreation(pathStr, pathStr+".zip");
				logger.info("zip file is creation is  "+zipFileCreationflag +" of  this file ... "+pathStr);
				
				try {
					String chOwnCmd = "chown "+owner+":"+group+" "+pathStr+".zip";
					logger.debug("chaging the file permissions =="+ chOwnCmd);
					Process p = Runtime.getRuntime().exec(new String[] {"bash", "-c",chOwnCmd});
	                p.waitFor();
					String chmodCmd = "chmod 777 "+pathStr+".zip";
					Process p1 = Runtime.getRuntime().exec(new String[] {"bash", "-c",chmodCmd});
	                p1.waitFor();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception in while writing failed message.", e);
				}
				
				//delete or move to done folder
				//move to done folder
				String donePath = PropertyUtil.getPropertyValue("donepromo");
				if(!donePath.endsWith(File.separator) ){
					donePath = donePath+File.separator;
				}
				createdXmlFile.renameTo(new File(donePath+createdXmlFile.getName()));
			}
			
			//logger.info("zip creation is  :::: "+XMLUtil.movetoZip(pathStr));
		} catch (BaseServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if(couponObj.getCouponGeneratedType().equals("single")) {
			
			//create PromocustomerInfo Thread
			Object[] obj = {couponObj, orgObj};
			
			if(logger.isDebugEnabled()) logger.debug("Is uploadCSVFile thread running : " + obj);
			PromoCustomerInfoXmlFileGeneration promoCustInfoXMLFileGenerationTread  = new PromoCustomerInfoXmlFileGeneration();
			logger.debug(" Calling thread  "+promoCustInfoXMLFileGenerationTread);
			promoCustInfoXMLFileGenerationTread.uploadQueue.add(obj);
			Thread thread = new Thread(promoCustInfoXMLFileGenerationTread);
			thread.start();
		}
		
		
	} //writePromoDescriptionFileAsXML
	
	
	
	private DiscountInformation getDiscountInforObject(Coupons eachPromo, String disCountType, Long orgOwner){
		DiscountInformation discountinformation = new DiscountInformation();
		try {
			List<OptSyncPromoDiscountInfo> disCountInfoList = new ArrayList<OptSyncPromoDiscountInfo>();
			
			CouponDiscountGenerateDao coupDiscGenDao  = (CouponDiscountGenerateDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPON_DICOUNT_GENERATE_DAO);
			CouponDiscountGenerateDaoForDML couponDiscountGenerateDaoForDML  = (CouponDiscountGenerateDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.COUPON_DICOUNT_GENERATE_DAO_FOR_DML);
			boolean isSkuFlag = eachPromo.getDiscountCriteria().equals("SKU") ? true : false;
			if(isSkuFlag) {
				
				List<String> retList = coupDiscGenDao.findDistinctAttr(eachPromo.getCouponId());
				if(retList == null || retList.isEmpty()) return  discountinformation;
				
				long deleted = couponDiscountGenerateDaoForDML.deleteTempPromoDumpBy(eachPromo.getCouponId(), orgOwner);
				logger.debug("deleted =="+deleted);
				long totRecordsInserted = 0l;
				for (String CDGAttr : retList) {
					
					long inserted = couponDiscountGenerateDaoForDML.insertIntoTempPromoDumpBy(eachPromo.getCouponId(), 
							orgOwner, Utility.CDGAttrToSKUMap.get(CDGAttr), CDGAttr);
					
					logger.debug("inserted =="+inserted);
					
					totRecordsInserted += inserted;
				}//for
				
				logger.debug("totRecordsInserted===="+totRecordsInserted);
				
				if(totRecordsInserted == 0) return  discountinformation;
				
				List<Object[]> list = coupDiscGenDao.getDiscAndNumOfItems(orgOwner);
				OptSyncPromoDiscountInfo optSyncDisCountInfo = new OptSyncPromoDiscountInfo();
				PromoItemCodeInfo promoIteCodeInfo = new PromoItemCodeInfo();
				
				String minPurvalue = "";
				for (Object[] objects : list) {
					List<String> itemCodeInfoList = new ArrayList<String>();
					//List<String> itemsList = new ArrayList<String>();
					Double discount = (Double)objects[0];
					logger.debug("for discount===="+discount);	
					itemCodeInfoList = coupDiscGenDao.getItems(discount, orgOwner);
					/*for (String item : itemCodeInfoList) {
						
						itemsList.add(item);
					}*/
					logger.debug("itemCodeInfoList==="+itemCodeInfoList.size());
					optSyncDisCountInfo.setVALUE(""+discount);
					optSyncDisCountInfo.setVALUECODE(disCountType);
					promoIteCodeInfo.setItemCodesList(itemCodeInfoList);
					optSyncDisCountInfo.setPromoItemCodeInfo(promoIteCodeInfo);
					optSyncDisCountInfo.setMINPURCHASEVALUE(minPurvalue);
					
					disCountInfoList.add(optSyncDisCountInfo);
				}//for
				
			}else{
				List<CouponDiscountGeneration> couponDiscList = coupDiscGenDao.findByCoupon(eachPromo);
				
				Map<Double,List<CouponDiscountGeneration>> coupDiscGenMap = new HashMap<Double, List<CouponDiscountGeneration>>();
				
				if(couponDiscList != null && couponDiscList.size() >0) {
					for (CouponDiscountGeneration eachCoupDisGen : couponDiscList) {
						
						List<CouponDiscountGeneration> coupDiscGenList = null;
						if(coupDiscGenMap.containsKey(eachCoupDisGen.getDiscount())){
							coupDiscGenList = coupDiscGenMap.get(eachCoupDisGen.getDiscount());
						}
						if(coupDiscGenList == null) {
							coupDiscGenList = new ArrayList<CouponDiscountGeneration>();
						}
						
						coupDiscGenList.add(eachCoupDisGen);
						coupDiscGenMap.put(eachCoupDisGen.getDiscount(), coupDiscGenList);
						
					}
				}
				Set<Double> doubleCoupDisgenSet = coupDiscGenMap.keySet();
//				List<DiscountInfo> disCountInfoList = new ArrayList<DiscountInfo>();
				
				OptSyncPromoDiscountInfo optSyncDisCountInfo = null;
//				DiscountInfo disCountInfo = null;
				for (Double eachDouble : doubleCoupDisgenSet) {
					optSyncDisCountInfo = new OptSyncPromoDiscountInfo();
					List<CouponDiscountGeneration> coupDisGenList = coupDiscGenMap.get(eachDouble);
					
					
					PromoItemCodeInfo promoIteCodeInfo = new PromoItemCodeInfo();
					String minPurvalue = "";
					if(isSkuFlag) {
						List<String> itemCodeInfoList = new ArrayList<String>();
						for (CouponDiscountGeneration eachCDGenObj : coupDisGenList) {
							
							itemCodeInfoList.add(eachCDGenObj.getItemCategory());
						}
						promoIteCodeInfo.setItemCodesList(itemCodeInfoList);
						
					}else {
						minPurvalue = coupDisGenList.get(0) == null ? "" :""+coupDisGenList.get(0).getTotPurchaseAmount();
					}
					
					optSyncDisCountInfo.setVALUE(""+eachDouble);
					optSyncDisCountInfo.setVALUECODE(disCountType);
					optSyncDisCountInfo.setPromoItemCodeInfo(promoIteCodeInfo);
					optSyncDisCountInfo.setMINPURCHASEVALUE(minPurvalue);
					
					disCountInfoList.add(optSyncDisCountInfo);
				}
			}
			
			discountinformation.setPromoDiscountInfoList(disCountInfoList);;
			return discountinformation;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug("Exception:: ", e);
			return discountinformation;
		}
	} // getDiscountInforObject
	
	/*
	private void writeStatusFile(String filename) {
		writeFailedMessage("is Unzipped ", filename, ".zip");
		
	}
	
	
	*/
	
	
}
