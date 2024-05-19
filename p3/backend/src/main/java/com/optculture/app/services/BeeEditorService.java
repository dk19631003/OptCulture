package com.optculture.app.services;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.optculture.app.repositories.ApplicationPropertiesRepository;
import com.optculture.app.repositories.CommunicationTemplateRepository;
import com.optculture.app.repositories.CouponsRepository;
import com.optculture.app.repositories.MyTemplatesRepository;
import com.optculture.app.repositories.UserDesignedCustomRowsRepository;
import com.optculture.shared.entities.communication.CommunicationTemplate;
import com.optculture.shared.entities.communication.email.MyTemplates;
import com.optculture.shared.entities.communication.email.UserDesignedCustomRows;
import com.optculture.shared.entities.config.ApplicationProperties;
import com.optculture.shared.entities.org.User;
import com.optculture.shared.entities.promotion.Coupons;
import com.optculture.shared.util.Constants;

@Service
public class BeeEditorService{


private final Logger logger = LoggerFactory.getLogger(BeeEditorService.class);

@Autowired
UserDesignedCustomRowsRepository userDesignedCustomRowsRepository;

@Autowired
ApplicationPropertiesRepository applicationPropertiesRepository;

@Autowired
CommunicationTemplateRepository communicationTemplateRepository;

@Autowired
MyTemplatesRepository templateRepository;

@Autowired
CouponsRepository couponRepository;

@Value("${barcodesDirectory}")
String usersParentDirectory;

@Value("${ApplicationUrl}")
String appUrl;

//type : campaign/autoemail/ereceipt.
public JSONArray dynamicUrlforCustomRowsBeeEditor(String beeEditorType,User currentUser) {
	
	JSONArray array = null;
	try {
		array = new JSONArray();
		List<String> type = new ArrayList<>();
		logger.info("Entered in the dynamicUrls for CustomRows.."+currentUser.getUserId()+ " for type "+beeEditorType);
		JSONObject item = new JSONObject();
		if(beeEditorType.equals("MyTemplate")) {
			type.add("Email Campaign Footer");
			type.add("Auto-email Footer");
		}else if(beeEditorType.equals("autoEmail")) {
			type.add("Auto-email Footer");
		}else if(beeEditorType.equals("campaign")) {
			type.add("Email Campaign Footer");
		}
		
		logger.info("rows to fetch from DB as"+type);
		List<ApplicationProperties> defaultCustomRows  = applicationPropertiesRepository.findByKeyIn(type);
		List<String> userDesignedCustomRows  = userDesignedCustomRowsRepository.findByUserId(currentUser.getUserId());
		if(defaultCustomRows!=null && !defaultCustomRows.isEmpty()) {
			for (ApplicationProperties defaultRows : defaultCustomRows) {
				item.put("name", defaultRows.getKey());
				item.put("value", appUrl+"/api/custom-rows/savedRows?name="+defaultRows.getKey()+""); // 3.0  api
				logger.info(" row name : "+defaultRows.getKey()+ " url : "+"api/custom-rows/savedRows?name="+defaultRows.getKey());
				
				array.put(item);
				item = new JSONObject();
			}
		}
		if(userDesignedCustomRows!=null && !userDesignedCustomRows.isEmpty()) {
			for (String customRows : userDesignedCustomRows) {
				item.put("name", customRows);
				item.put("value", appUrl+"/api/custom-rows/userDesignedRows?name="+customRows+"&userId="+currentUser.getUserId()+""); // 3.0api 
	
				logger.info(" row name : "+customRows+ " url : "+appUrl+"/api/custom-rows/userDesignedRows?name="+customRows+"&userId="+currentUser.getUserId()+"");

				array.put(item);
				item = new JSONObject();
			}
		}
		

}catch(Exception e){
	logger.error("Exception e"+e);
	}
	logger.info("completed the dynamic url and sending array"+array);
	 return array;

}


public String saveBeeTemplateInCommunicationTemplate(String json, String html,User user,String name) {

	try {
		logger.info("This is the call for saving CommunicationTemplate rows");
		CommunicationTemplate myTemp = communicationTemplateRepository.findByUserIdAndTemplateNameAndChannelType(user.getUserId(),name,Constants.TYPE_EMAIL_CAMPAIGN);
		if(myTemp == null ) {
			myTemp = new CommunicationTemplate();
			myTemp.setTemplateName(name);
			myTemp.setUserId(user.getUserId());
			myTemp.setCreatedDate(LocalDateTime.now());
			myTemp.setTemplateType("beeEditor");
			myTemp.setChannelType(Constants.TYPE_EMAIL_CAMPAIGN);
			myTemp.setOrgId(user.getUserOrganization().getUserOrgId());
			}	

	myTemp.setModifiedDate(LocalDateTime.now());
	myTemp.setMsgContent(html);
	myTemp.setJsonContent(json);
	communicationTemplateRepository.save(myTemp);
	
	}catch(Exception e ) {
		return "Error while saving Template in DB";
	}
	return "Success";	
}
public String saveCustomRowForUser(String json,String html, User user) {
	try {
		JSONObject rowJson = new JSONObject(json);
		String rowName = "";
		logger.info("This is the call for saving custom rows");
		if(rowJson.has("metadata")) {
			rowName = rowJson.getString("metadata");
			}
		UserDesignedCustomRows row = new UserDesignedCustomRows();
		row.setRowHtmlData(html);
		row.setRowJsonData(rowJson.toString());
		if(!rowName.isEmpty())
			row.setTemplateName(rowName); // set row name name.
		else
			row.setTemplateName("Default");
		row.setRowCategory("My Custom Row");
		row.setUserId(user.getUserId());
		userDesignedCustomRowsRepository.save(row);
		logger.info("saving customrow...");
		
		}catch(Exception e ) {
			return "Error while saving Template in DB";
		}
	return "Success";	

}


public List<String> getBarCodesforUser(User user) {
	// TODO Auto-generated method stub
	
	String result = "";

	try {
		// add("CC_coupon Name::|^CC_123_couponName^|");
		
		logger.info("Enetered in the generate BarCodes ");
		List<String> couponsPhList = new ArrayList<String>();
		List<String> imageCouponsPhList = new ArrayList<String>();
		List<String> imageCouponsPhBeeList = new ArrayList<String>();

		List<Coupons> couponList = couponRepository.findActiveAndRunningCouponsListForBEE(user.getUserId());

		
		if(couponList!=null) {
			logger.info("couponList in the generate BarCodes ");

			String textcouponStr="";
			String imagecouponStr="";
			String imagecouponBeeStr="";
		

			for (Coupons coupon : couponList) {
				
					logger.info("Entered into Coupon block");

					textcouponStr = coupon.getCouponName() + Constants.DELIMETER_DOUBLECOLON + 
							Constants.CF_START_TAG + Constants.CC_TOKEN + coupon.getCouponId() +"_"+coupon.getCouponName()+Constants.CF_END_TAG;
				
					logger.info("textCouponStr in the generate BarCodes ");


				
				if(coupon.getEnableBarcode() && coupon.getBarcodeType() != null && coupon.getBarcodeWidth() != null
						&& coupon.getBarcodeHeight() != null){
					
					logger.info("enable barcode in the generate BarCodes ");

					
					String couponIdStr = Constants.CC_TOKEN + coupon.getCouponId() +"_"+coupon.getCouponName()+ 	
							"_"+coupon.getBarcodeType()+"_"+coupon.getBarcodeWidth()
							+"_"+coupon.getBarcodeHeight();

					//generate image dynamically and set
					//generate barcode image
					BitMatrix bitMatrix = null;
					
					String COUPON_CODE_URL = null;

					String ccPreviewUrl = null;
					
		           		 URI uri = null;

					
					String message = "Test:"+coupon.getCouponName();
					String barcodeType = coupon.getBarcodeType().trim();
					int width = coupon.getBarcodeWidth().intValue();
					int height = coupon.getBarcodeHeight().intValue();
					
					if(barcodeType.equals(Constants.COUP_BARCODE_QR)){
						
						bitMatrix = new QRCodeWriter().encode(message, BarcodeFormat.QR_CODE, width, height,null);
						String bcqrImg = user.getUserName()+File.separator+
								"Preview"+File.separator+"QRCODE"+File.separator+couponIdStr+".png";
						
						uri = new URI(bcqrImg);

						COUPON_CODE_URL = usersParentDirectory+"/UserData"+File.separator+uri.getPath();

						ccPreviewUrl = appUrl+"/UserData"+File.separator+bcqrImg;
						
						logger.info("barCode Type  in the generate BarCodes "+COUPON_CODE_URL+"\n"+ccPreviewUrl);

					}
					else if(barcodeType.equals(Constants.COUP_BARCODE_AZTEC)){
						
						bitMatrix = new AztecWriter().encode(message, BarcodeFormat.AZTEC, width, height);
						String bcazImg = user.getUserName()+File.separator+
								"Preview"+File.separator+"AZTEC"+File.separator+couponIdStr+".png";
						
						uri = new URI(bcazImg);

						
						COUPON_CODE_URL = usersParentDirectory+"/UserData"+File.separator+uri.getPath();
						
						ccPreviewUrl = appUrl+"/UserData"+File.separator+bcazImg;
						
						logger.info("aztec Type  in the generate BarCodes "+COUPON_CODE_URL+"\n"+ccPreviewUrl);

					}
					else if(barcodeType.equals(Constants.COUP_BARCODE_LINEAR)){
						
						bitMatrix = new Code128Writer().encode(message, BarcodeFormat.CODE_128, width, height,null);
						String bclnImg = user.getUserName()+File.separator+
								"Preview"+File.separator+"LINEAR"+File.separator+couponIdStr+".png";
						// have to have the bclnImg as the  url.
						uri = new URI(bclnImg);
						COUPON_CODE_URL = usersParentDirectory+"/UserData"+File.separator+uri.getPath();
						
						ccPreviewUrl = appUrl+"/UserData"+File.separator+bclnImg;
			             
			             
						
						logger.info("Linear Type  in the generate BarCodes "+COUPON_CODE_URL+"\n"+ccPreviewUrl);

					}
					else if(barcodeType.equals(Constants.COUP_BARCODE_DATAMATRIX)){
						
						bitMatrix = new DataMatrixWriter().encode(message, BarcodeFormat.DATA_MATRIX, width, height,null);
						String bcdmImg = user.getUserName()+File.separator+
								"Preview"+File.separator+"DATAMATRIX"+File.separator+couponIdStr+".png";
						
						uri = new URI(bcdmImg);
						
						COUPON_CODE_URL = usersParentDirectory+"/UserData"+File.separator+uri.getPath();
						
						
						ccPreviewUrl = appUrl+"/UserData"+File.separator+bcdmImg;
						
						logger.info("dailymatrix Type  in the generate BarCodes "+COUPON_CODE_URL+"\n"+ccPreviewUrl);

					}
					
					File directory = new File(COUPON_CODE_URL).getParentFile();
		             
		             if (!directory.exists()) {
		                 if (directory.mkdirs()) {
		                     System.out.println("Directories created successfully: " + directory.getAbsolutePath());
		                     
		                     MatrixToImageWriter.writeToStream(bitMatrix, "png", new FileOutputStream(
										new File(COUPON_CODE_URL)));
								
								logger.info("matrixToimage  in the generate BarCodes ");
		                     
		                 } else {
		                     logger.info("Failed to create directories: " + directory.getAbsolutePath());
		                     // Exit the program or handle the error as appropriate
		                     return new ArrayList<String>();
		                 }
		             }

					imagecouponStr = coupon.getCouponName() + Constants.DELIMETER_DOUBLECOLON + 
							"<img id="+couponIdStr +
							" src="+ccPreviewUrl+" width="+coupon.getBarcodeWidth() +
							" height="+coupon.getBarcodeHeight()+ " />";
					imageCouponsPhList.add(imagecouponStr);
					logger.info("imagecouponid Type  in the generate BarCodes "+imageCouponsPhBeeList);

					imagecouponBeeStr = coupon.getCouponName() + Constants.DELIMETER_DOUBLECOLON + 
							"<img id=\""+couponIdStr + 
							"\" src=\""+ccPreviewUrl+"\" width=\""+coupon.getBarcodeWidth() +
							"\" height=\""+coupon.getBarcodeHeight()+ "\" />";
					imageCouponsPhBeeList.add(imagecouponBeeStr); 
					
				}///subscriber/images/CC_test_150_150.png
				logger.info("barCode Type  in the generate BarCodes "+imageCouponsPhBeeList);

				couponsPhList.add(textcouponStr);	
				
			} // for
				
			//couponsPhList.add("CC_TEST::<img id=dummyid src=http://localhost:8080/subscriber/images/CC_test_150_150.png /> ");
			
		} // if
		
		//barCodes
		List<String> couponBeeList = new ArrayList<String>();
		StringBuffer bufferImg = new StringBuffer();
		for (String couponPlaceHolder : imageCouponsPhBeeList) {
			if(couponPlaceHolder.trim().startsWith("--") || couponPlaceHolder.toLowerCase().contains(("place holder"))) { //Ignore
				continue;
			}
			String[] imagePHArr= couponPlaceHolder.split(Constants.DELIMETER_DOUBLECOLON );
			String coupImgName = imagePHArr[0];
			String coupImgValue = imagePHArr[1];
			if(bufferImg.length() > 0) bufferImg.append(",");
			bufferImg.append("{name: '" +coupImgName+ "', value: '"+coupImgValue+"'}"); 
			couponBeeList.add("{name: '" +coupImgName+ "', value: '"+coupImgValue+"'}");
		} // for
		
		String barCodeImg=bufferImg.toString();
		
		logger.info("barcode final "+barCodeImg);
		
		return couponBeeList;


	} catch (Exception e) {
		logger.error("Exception ::" , e);;
		return null;
	}

}
	
}
