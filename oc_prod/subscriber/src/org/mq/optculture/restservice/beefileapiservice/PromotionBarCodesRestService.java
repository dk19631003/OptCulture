package org.mq.optculture.restservice.beefileapiservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;



public class PromotionBarCodesRestService extends AbstractController{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private CouponsDao couponsDao;
	private final String defaultJsonPatten = "{\"container\":{\"style\":{\"background-color\":\"transparent\",\"background-repeat\":\"no-repeat\",\"background-image\":\"none\",\"background-position\":\"top left\"}},\"columns\":[{\"grid-columns\":12,\"style\":{\"background-color\":\"transparent\",\"border-top\":\"0px solid transparent\",\"padding-top\":\"5px\",\"border-right\":\"0px solid transparent\",\"border-left\":\"0px solid transparent\",\"padding-left\":\"0px\",\"padding-bottom\":\"5px\",\"border-bottom\":\"0px solid transparent\",\"padding-right\":\"0px\"},\"modules\":[{\"descriptor\":{\"computedStyle\":{\"hideContentOnMobile\":false},\"html\":{\"html\":\"<div class=\\\"our-class\\\"> I'm a new HTML block. <\\/div>\"},\"style\":{\"padding-top\":\"0px\",\"padding-left\":\"0px\",\"padding-bottom\":\"0px\",\"padding-right\":\"0px\"}},\"type\":\"mailup-bee-newsletter-modules-html\"}]}],\"type\":\"one-column-empty\",\"content\":{\"computedStyle\":{\"rowColStackOnMobile\":true},\"style\":{\"background-color\":\"transparent\",\"background-repeat\":\"no-repeat\",\"color\":\"#000000\",\"width\":\"500px\",\"background-image\":\"none\",\"background-position\":\"top left\"}}}";
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONArray jsonArray = null;
		try {
			String userOrgId = request.getParameter("userOrgId");
			String userName = request.getParameter("userName");
			logger.info("SavedRows API ");
			 if(userOrgId!=null && !userOrgId.isEmpty()) {
				 jsonArray = generatePromoImage(userOrgId,userName);
			 }
			
			
		} catch(Exception e) {
			logger.error("Exception ::" , e);
		} finally {
			try {
				response.setContentType("application/json");
				PrintWriter pw = response.getWriter();
				if(jsonArray!=null && !jsonArray.toString().isEmpty()) {
					pw.write(jsonArray.toString());
				}else {
					pw.write(Constants.STRING_NILL);
				}
				pw.flush();
				pw.close();
			} catch (Exception e) {
				logger.error("Exception ::" , e);
			}
		}
		
		return null;
	}
	
	public JSONArray generatePromoImage(String userOrgId, String userName) throws Exception {
		//StringBuffer bufferImg = new StringBuffer();
		JSONArray jsonArray = new JSONArray();
		this.couponsDao = (CouponsDao) ServiceLocator.getInstance().getDAOByName("couponsDao");
		if(userOrgId!=null && !userOrgId.isEmpty() && userName!=null && !userName.isEmpty()) {
		List<Coupons> couponList = couponsDao.findActiveAndRunningCouponsbyOrgId(Long.parseLong(userOrgId),null,Constants.STRING_NILL);
		if(couponList!=null && !couponList.isEmpty()) {
			String imagecouponBeeStr="";
			List<String> imageCouponsPhBeeList = new ArrayList<String>();
			for (Coupons coupon : couponList) {
				
				if(coupon.getEnableBarcode() && coupon.getBarcodeType() != null && coupon.getBarcodeWidth() != null
						&& coupon.getBarcodeHeight() != null){

					String couponIdStr = Constants.CC_TOKEN + coupon.getCouponId() +"_"+coupon.getCouponName()+ 	
							"_"+coupon.getBarcodeType()+"_"+coupon.getBarcodeWidth()
							+"_"+coupon.getBarcodeHeight();

					BitMatrix bitMatrix = null;
					
					String COUPON_CODE_URL = null;
					String ccPreviewUrl = null;
					
					String message = "Test:"+coupon.getCouponName();
					String barcodeType = coupon.getBarcodeType().trim();
					int width = coupon.getBarcodeWidth().intValue();
					int height = coupon.getBarcodeHeight().intValue();
					
					if(barcodeType.equals(Constants.COUP_BARCODE_QR)){
						
						bitMatrix = new QRCodeWriter().encode(message, BarcodeFormat.QR_CODE, width, height,null);
						String bcqrImg = userName+File.separator+
								"Preview"+File.separator+"QRCODE"+File.separator+couponIdStr+".png";
						
						COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcqrImg;
						ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcqrImg;
					}
					else if(barcodeType.equals(Constants.COUP_BARCODE_AZTEC)){
						
						bitMatrix = new AztecWriter().encode(message, BarcodeFormat.AZTEC, width, height);
						String bcazImg = userName+File.separator+
								"Preview"+File.separator+"AZTEC"+File.separator+couponIdStr+".png";
						
						COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcazImg;
						
						ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcazImg;
					}
					else if(barcodeType.equals(Constants.COUP_BARCODE_LINEAR)){
						
						bitMatrix = new Code128Writer().encode(message, BarcodeFormat.CODE_128, width, height,null);
						String bclnImg = userName+File.separator+
								"Preview"+File.separator+"LINEAR"+File.separator+couponIdStr+".png";
						
						COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bclnImg;
						
						ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bclnImg;
					}
					else if(barcodeType.equals(Constants.COUP_BARCODE_DATAMATRIX)){
						
						bitMatrix = new DataMatrixWriter().encode(message, BarcodeFormat.DATA_MATRIX, width, height,null);
						String bcdmImg = userName+File.separator+
								"Preview"+File.separator+"DATAMATRIX"+File.separator+couponIdStr+".png";
						
						COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcdmImg;
						
						ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcdmImg;
					}
					
					File myTemplateFile = new File(COUPON_CODE_URL);
					File parentDir = myTemplateFile.getParentFile();
					if(!parentDir.exists()) {

						parentDir.mkdir();
					}

					if(!myTemplateFile.exists()) {
						
						MatrixToImageWriter.writeToStream(bitMatrix, "png", new FileOutputStream(
								new File(COUPON_CODE_URL)));	
					}
					
					imagecouponBeeStr = coupon.getCouponName() + Constants.DELIMETER_DOUBLECOLON + 
							"<center><img id=\""+couponIdStr + 
							"\" src=\""+ccPreviewUrl+"\" width=\""+coupon.getBarcodeWidth() +
							"\" height=\""+coupon.getBarcodeHeight()+ "\" style=\"float:center;\"/></center>";
					imageCouponsPhBeeList.add(imagecouponBeeStr); 
				}
				
				
			}
			
			 
			for (String couponPlaceHolder : imageCouponsPhBeeList) {
				JSONObject preDefinedJson = new JSONObject(defaultJsonPatten);
				if(couponPlaceHolder.trim().startsWith("--") || couponPlaceHolder.toLowerCase().contains(("place holder"))) { //Ignore
					continue;
				}
				String[] imagePHArr= couponPlaceHolder.split(Constants.DELIMETER_DOUBLECOLON );
				String coupImgName = imagePHArr[0];
				String coupImgValue = imagePHArr[1];
				JSONObject metaDataName = new JSONObject();
				metaDataName.put("name", coupImgName);
				preDefinedJson.put("metadata", metaDataName);
				
				JSONArray column = preDefinedJson.getJSONArray("columns");
				for (int i = 0; i < column.length(); i++) {
					preDefinedJson.getJSONArray("columns").
					getJSONObject(i).getJSONArray("modules").
					getJSONObject(0).getJSONObject("descriptor")
					.getJSONObject("html")
					.put("html", coupImgValue);
				}
				jsonArray.put(preDefinedJson);
			} 
			
		}		
	}
		return jsonArray;
	}
	
	
}



