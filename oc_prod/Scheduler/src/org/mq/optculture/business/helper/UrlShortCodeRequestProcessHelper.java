package org.mq.optculture.business.helper;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.AutoSmsQueue;
import org.mq.captiway.scheduler.beans.AutosmsSenturlShortcode;
import org.mq.captiway.scheduler.beans.DRSMSChannelSent;
import org.mq.captiway.scheduler.beans.DRSMSSent;
import org.mq.captiway.scheduler.beans.SMSCampaignSentUrlShortCode;
import org.mq.captiway.scheduler.beans.UrlShortCodeMapping;
import org.mq.captiway.scheduler.dao.AutoSmsQueueDao;
import org.mq.captiway.scheduler.dao.AutoSmsQueueDaoForDML;
import org.mq.captiway.scheduler.dao.AutoSmsUrlDaoForDML;
import org.mq.captiway.scheduler.dao.AutosmsSenturlShortcodeDao;
import org.mq.captiway.scheduler.dao.DRSMSChannelSentDao;
import org.mq.captiway.scheduler.dao.DRSMSSentDao;
import org.mq.captiway.scheduler.dao.DRSMSSentDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignReportDao;
import org.mq.captiway.scheduler.dao.SMSCampaignSentUrlShortCodeDao;
import org.mq.captiway.scheduler.dao.UrlShortCodeMappingDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;

public class UrlShortCodeRequestProcessHelper {

	
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	public SMSCampaignSentUrlShortCode getSMSCampSentUrlObj(String shortCode) throws BaseServiceException{
		
		ServiceLocator locator = ServiceLocator.getInstance();
		SMSCampaignSentUrlShortCodeDao smsCampaignSentUrlShortCodeDao = null;
		try {
			smsCampaignSentUrlShortCodeDao = (SMSCampaignSentUrlShortCodeDao)locator.getDAOByName(OCConstants.SMSCAMPAIGNSENTURLSHORTCODE_DAO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("No Dao found with the given ID ::"+OCConstants.SMSCAMPAIGNSENTURLSHORTCODE_DAO);
		}
		
		SMSCampaignSentUrlShortCode smsCampaignSentUrlShortCode = smsCampaignSentUrlShortCodeDao.findByShortCode(shortCode);
		
		return smsCampaignSentUrlShortCode;
	}
	
	public String getActualUrl(String shortCode) throws BaseServiceException{
		
		String mappedActualUrlStr = null;
		
		UrlShortCodeMappingDao urlShortCodeMappingDao = null;
		try {
			urlShortCodeMappingDao = (UrlShortCodeMappingDao)ServiceLocator.getInstance().getDAOByName(OCConstants.URLSHORTCODEMAPPING_DAO);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("No Dao found with the given ID ::"+OCConstants.URLSHORTCODEMAPPING_DAO);
		}
		UrlShortCodeMapping urlShortCodeMapping = (UrlShortCodeMapping)urlShortCodeMappingDao.findlongUrlByShortCode(shortCode);
		
		if(urlShortCodeMapping == null) {
			
			if(logger.isDebugEnabled()) logger.debug("getting null value from the DB ::");
			
			return null;
			
		}
		
		mappedActualUrlStr = urlShortCodeMapping.getUrlContent();
		if(mappedActualUrlStr == null) {
			if(logger.isDebugEnabled()) logger.debug("getting null value from the DB ::");
			
			return null;
		}
		 
		if(!(mappedActualUrlStr.toLowerCase().startsWith("http://")) && !(mappedActualUrlStr.toLowerCase().startsWith("https://"))){
			 mappedActualUrlStr = "http://"+mappedActualUrlStr;
		 }
		logger.debug(mappedActualUrlStr);
		return mappedActualUrlStr;
		
	}
	
	public BitMatrix getBarcode(String barcodeStr) throws BaseServiceException{

		try {
			
			BitMatrix bitMatrix = null;
			
			
			//String bcTokensArr[] = barcodeStr.split("_");
			
			String type = null;
			String couponcode = null;
			
			String barcodeType = barcodeStr.substring(0, 1);
			couponcode = barcodeStr.substring(1);
			if(barcodeType.equals("L")){
				type = "LN";
			}
			else if(barcodeType.equals("Q")){
				type = "QR";
			}
			else if(barcodeType.equals("D")){
				type = "DM";
			}
			else if(barcodeType.equals("A")){
				type = "AZ";
			}
			
			//String couponcode = bcTokensArr[1].trim();
			//String type = bcTokensArr[0].trim();
			//String widthStr = bcTokensArr[3].trim();
			//String heightStr = bcTokensArr[4].trim();
			int width = 0;
			int height = 0;
			if(!Constants.barcodeTypes.contains(type)){
				return null;
			}
			if(type.equals("LN")){
				width = 120;
				height = 30;
			}
			else if(Constants.barcodeSquareTypes.contains(type)){
				width = 120;
				height = 120;
			}
			
			/*int width = Integer.parseInt(widthStr);
			int height = Integer.parseInt(heightStr);
			*/
			
			logger.info("barcode code = "+couponcode);
			logger.info("barcode width = "+width);
			logger.info("barcode height = "+height);
		
			if(type.equals(Constants.COUP_BARCODE_QR)){
				bitMatrix = new QRCodeWriter().encode(couponcode, BarcodeFormat.QR_CODE, 640, 360,null);
			}
			else if(type.equals(Constants.COUP_BARCODE_AZTEC)){
				bitMatrix = new AztecWriter().encode(couponcode, BarcodeFormat.AZTEC, width, height);
			}
			else if(type.equals(Constants.COUP_BARCODE_LINEAR)){
				bitMatrix = new Code128Writer().encode(couponcode, BarcodeFormat.CODE_128, 640, 360,null);
			}
			else if(type.equals(Constants.COUP_BARCODE_DATAMATRIX)){
				bitMatrix = new DataMatrixWriter().encode(couponcode, BarcodeFormat.DATA_MATRIX, width, height,null);
			}
			
			if(bitMatrix == null){
				return null;
			}
			
			return bitMatrix;
		} catch (Exception e) {
			logger.error("Exception : Problem while generating SMS barcode \n",	e);
			
			throw new BaseServiceException("Exception : Problem while generating SMS barcode \n");
		}

	
		
	}
	
	public void updateSMSClicks(SMSCampaignSentUrlShortCode smsCampaignSentUrlShortCode) throws BaseServiceException{
		
		UpdateSMSClicks updateSMSclicksThread = new UpdateSMSClicks(smsCampaignSentUrlShortCode);
		updateSMSclicksThread.start();
		
	}
	
	public Object checkAndPerformAction(String shortCode) throws BaseServiceException{
		
		if(shortCode.startsWith(OCConstants.SHORTURL_CODE_PREFIX_U)) {//sent short url, just redirect to the actual big url
			
			return getActualUrl(shortCode);
			
		}else if(shortCode.startsWith(OCConstants.SHORTURL_CODE_PREFIX_DR)){
			
			String mappedActualUrlStr = null;
			
			DRSMSSentDao drSMSSentDao = null;
			DRSMSChannelSentDao drSMSChannelSentDao = null;
			DRSMSChannelSent drSMSChannelSent = null;
			try {
				drSMSSentDao = (DRSMSSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DR_SMS_SENT_DAO);
				drSMSChannelSentDao = (DRSMSChannelSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DR_SMS_CHANNEL_SENT_DAO);
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new BaseServiceException("No Dao found with the given ID ::"+OCConstants.DR_SMS_SENT_DAO);
			}
			DRSMSSent drSMSSent = (DRSMSSent)drSMSSentDao.findlongUrlByShortCode(shortCode);
			if(drSMSSent==null) drSMSChannelSent = (DRSMSChannelSent)drSMSChannelSentDao.findlongUrlByShortCode(shortCode) ; //OPS-391
			if(drSMSSent == null && drSMSChannelSent == null) {
				
				if(logger.isDebugEnabled()) logger.debug("getting null value from the DB ::");
				
				return null;
				
			}
			
			if(drSMSSent!=null) mappedActualUrlStr = drSMSSent.getOriginalUrl();
			else if(drSMSChannelSent!=null) mappedActualUrlStr = drSMSChannelSent.getOriginalUrl();
			if(mappedActualUrlStr == null) {
				if(logger.isDebugEnabled()) logger.debug("getting null value from the DB ::");
				
				return null;
			}
			 
			if(!(mappedActualUrlStr.toLowerCase().startsWith("http://")) && !(mappedActualUrlStr.toLowerCase().startsWith("https://"))){
				 mappedActualUrlStr = "http://"+mappedActualUrlStr;
			 }
			logger.debug(mappedActualUrlStr);
			return mappedActualUrlStr;
			
		}else if(shortCode.startsWith(OCConstants.SHORTURL_CODE_PREFIX_BARCODE_TYPE_LINEAR) || 
				shortCode.startsWith(OCConstants.SHORTURL_CODE_PREFIX_BARCODE_TYPE_QR) ||
				shortCode.startsWith(OCConstants.SHORTURL_CODE_PREFIX_BARCODE_TYPE_DATAMATRIX) || 
				shortCode.startsWith(OCConstants.SHORTURL_CODE_PREFIX_BARCODE_TYPE_AZETEC)) {//sent short barcode url, just display the barcode 
			
			return getBarcode(shortCode);
			
		}
		
		//return null;
		
		return getActualUrl(shortCode);
	}

	public AutosmsSenturlShortcode getAutoSMSSentUrlObj(String shortCode) throws BaseServiceException {
		ServiceLocator locator = ServiceLocator.getInstance();
		AutosmsSenturlShortcodeDao autosmsSenturlShortcodeDao = null;
		try {
			autosmsSenturlShortcodeDao = (AutosmsSenturlShortcodeDao)locator.getDAOByName(OCConstants.AUTOSMS_SENTURLSHORTCODE_DAO);
		} catch (Exception e) {
			throw new BaseServiceException("No Dao found with the given ID ::"+OCConstants.AUTOSMS_SENTURLSHORTCODE_DAO);
		}
		
		AutosmsSenturlShortcode autosmsSenturlShortcode = autosmsSenturlShortcodeDao.findByShortCode(shortCode);
		
		return autosmsSenturlShortcode;
	}
	public DRSMSSent getDRSMSSentObj(String shortCode) throws BaseServiceException {
		ServiceLocator locator = ServiceLocator.getInstance();
		DRSMSSentDao drSMSSentDao = null;
		try {
			drSMSSentDao = (DRSMSSentDao)locator.getDAOByName(OCConstants.DR_SMS_SENT_DAO);
		} catch (Exception e) {
			throw new BaseServiceException("No Dao found with the given ID ::"+OCConstants.DR_SMS_SENT_DAO);
		}
		
		DRSMSSent drSMSSent = drSMSSentDao.findlongUrlByShortCode(shortCode);
		
		return drSMSSent;
	}
	public DRSMSChannelSent getDRSMSChannelSentObj(String shortCode) throws BaseServiceException {
		ServiceLocator locator = ServiceLocator.getInstance();
		DRSMSChannelSentDao drSMSChannelSentDao = null;
		try {
			drSMSChannelSentDao = (DRSMSChannelSentDao)locator.getDAOByName(OCConstants.DR_SMS_CHANNEL_SENT_DAO);
		} catch (Exception e) {
			throw new BaseServiceException("No Dao found with the given ID ::"+OCConstants.DR_SMS_CHANNEL_SENT_DAO);
		}
		
		DRSMSChannelSent drSMSChannelSent = drSMSChannelSentDao.findlongUrlByShortCode(shortCode);
		
		return drSMSChannelSent;
	}

	public void updateAutoSMSClicks(AutosmsSenturlShortcode autosmsSenturlShortcode) {
		try {
			logger.info("Update autoSms count in sms queue");
			AutoSmsQueueDaoForDML  autoSmsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
			if(autoSmsQueueDaoForDML!=null) {
				autoSmsQueueDaoForDML.updateclicksBasedOnSmsQueueId(autosmsSenturlShortcode.getAutoSmsQueueSentId());
			}
			logger.info("Updated autoSms count in sms queue");
		} catch (Exception e) {
			logger.error("updateAutoSMSClicks ::" +e);
		}
	}
	public void updateDRSMSClicks(DRSMSSent drSMSSent) {
		try {
			logger.info("Update drsms count in sms queue");
			DRSMSSentDaoForDML  drSMSSentDaoForDML = (DRSMSSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.DR_SMS_SENT_DAO_ForDML);
			if(drSMSSentDaoForDML!=null) {
				drSMSSentDaoForDML.updateclicks(drSMSSent.getId());
			}
			logger.info("Updated autoSms count in sms queue");
		} catch (Exception e) {
			logger.error("updateAutoSMSClicks ::" +e);
		}
	}
}

