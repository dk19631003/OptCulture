package org.mq.optculture.business.helper;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.SMSCampaignSentUrlShortCode;
import org.mq.captiway.scheduler.beans.UrlShortCodeMapping;
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
				bitMatrix = new QRCodeWriter().encode(couponcode, BarcodeFormat.QR_CODE, width, height,null);
			}
			else if(type.equals(Constants.COUP_BARCODE_AZTEC)){
				bitMatrix = new AztecWriter().encode(couponcode, BarcodeFormat.AZTEC, width, height);
			}
			else if(type.equals(Constants.COUP_BARCODE_LINEAR)){
				bitMatrix = new Code128Writer().encode(couponcode, BarcodeFormat.CODE_128, width, height,null);
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
			
		}else if(shortCode.startsWith(OCConstants.SHORTURL_CODE_PREFIX_BARCODE_TYPE_LINEAR) || 
				shortCode.startsWith(OCConstants.SHORTURL_CODE_PREFIX_BARCODE_TYPE_QR) ||
				shortCode.startsWith(OCConstants.SHORTURL_CODE_PREFIX_BARCODE_TYPE_DATAMATRIX) || 
				shortCode.startsWith(OCConstants.SHORTURL_CODE_PREFIX_BARCODE_TYPE_AZETEC)) {//sent short barcode url, just display the barcode 
			
			return getBarcode(shortCode);
			
		}
		
		return null;
	}
}

