package org.mq.captiway.scheduler;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.UrlShortCodeMapping;
import org.mq.captiway.scheduler.dao.UrlShortCodeMappingDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;

public class UrlShortningDispServlet extends AbstractController {
	
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private static String errorPage ="<html><head><title>Invalid Code</title></head><body><center>INVALID SHORT CODE</center></body></html>";
	
	public UrlShortningDispServlet() {
		
	}
	
	private UrlShortCodeMappingDao urlShortCodeMappingDao;
	public UrlShortCodeMappingDao getUrlShortCodeMappingDao() {
		return urlShortCodeMappingDao;
	}
	public void setUrlShortCodeMappingDao(UrlShortCodeMappingDao UrlShortCodeMappingDao) {
		this.urlShortCodeMappingDao = UrlShortCodeMappingDao;
	}
//	private String mappedActualUrlStr ;
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	
		try {
			//request the parameter from the  url
			String shortCodeStr = (String)request.getParameter("code");
			
			
			if(logger.isDebugEnabled()) logger.debug("Short code of requested Url is ::"+shortCodeStr);
			response.setContentType("text/html");
			//PrintWriter out = response.getWriter();
			
			if(shortCodeStr == null || (shortCodeStr !=null && shortCodeStr.trim().isEmpty())) {
				if(logger.isDebugEnabled()) logger.debug("requested shortCodeId is null");
				PrintWriter out = response.getWriter();
				out.println(errorPage);
				return null;
			}
			
			if(shortCodeStr.startsWith("L") || shortCodeStr.startsWith("Q") ||
					shortCodeStr.startsWith("D") || shortCodeStr.startsWith("A")){
				
				//String bcTokensArr[] = shortCodeStr.split("_");
				couponBarCode(request, response);
				return null;
			}
			
			
			
			logger.info("Url shortcode: "+shortCodeStr);
			
			PrintWriter out = response.getWriter();
			
			if(!shortCodeStr.startsWith("U")){
				logger.info("Invalid url shortner code....." +shortCodeStr);
				out.println(errorPage);
				return null;
				
			}
			
			String mappedActualUrlStr = null;
			UrlShortCodeMapping urlShortCodeMapping = (UrlShortCodeMapping)urlShortCodeMappingDao.findlongUrlByShortCode(shortCodeStr);
			
			if(urlShortCodeMapping == null) {
				if(logger.isDebugEnabled()) logger.debug("getting null value from the DB ::");
				
				out.println(errorPage);
				return null;
				
			}
			
			mappedActualUrlStr = urlShortCodeMapping.getUrlContent();
			if(mappedActualUrlStr == null) {
				if(logger.isDebugEnabled()) logger.debug("getting null value from the DB ::");
				
				out.println(errorPage);
				return null;
			}
			 
			else  if(!(mappedActualUrlStr.contains("http://"))){
				 mappedActualUrlStr = "http://"+mappedActualUrlStr;
			 }
			//String redirectUrl = response.encodeRedirectUrl(mappedActualUrlStr);
			 //response.sendRedirect(redirectUrl);
//			 Executions.sendRedirect(mappedActualUrlStr);
			
//			sendReirectUsingShortCode(shortCodeStr);
//			 logger.debug("mappedActualUrlStr ::"+mappedActualUrlStr);
			 
			 //remove cache from the browser
			 /*response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
			 response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
			 response.setDateHeader("Expires", 0); // Proxies.
*/
			 
			/*//Forces caches to obtain a new copy of the page from the origin server
			 response.setHeader("Cache-Control","no-cache");
			 //Directs caches not to store the page under any circumstance 
			 response.setHeader("Cache-Control","no-store");
			 //Causes the proxy cache to see the page as "stale" 
			 response.setDateHeader("Expires", 0); 
			 //HTTP 1.0 backward compatibility
			 response.setHeader("Pragma","no-cache"); 
*/
			 response.sendRedirect(mappedActualUrlStr); //redirecting the page like (http://www.google.com)
			
			
			return null;
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
			return null;
			
		}
	}
	
	/*public void sendReirectUsingShortCode(String shortCode) {
		
	}*/
	
	public void couponBarCode(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (logger.isDebugEnabled())
			logger.debug("-- Just Entered couponBarCode--");

		try {
			response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
			response.setHeader("Pragma", "no-cache"); // HTTP 1.0
			response.setDateHeader("Expires", 0); // prevents caching at the
													// proxy server
			response.setContentType("image/png");
			
			BitMatrix bitMatrix = null;
			
			String barcodeStr = (String)request.getParameter("code");
			
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
				return;
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
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			
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
					return;
				}
				MatrixToImageWriter.writeToStream(bitMatrix, "png", baos);
				ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			
				BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
			
			try {
				byte b[] = new byte[8];
				int count;
				while((count=bais.read(b)) != -1) {
					out.write(b,0,count);
				}
			
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception in SMS barcode generation", e);
			}finally {
				out.flush();
				out.close();
				bais.close();
			}


		} catch (Exception e) {
			logger.error("Exception : Problem while generating SMS barcode \n",	e);
			return;
		}

	}
	
	
	
}
