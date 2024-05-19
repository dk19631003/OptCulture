package org.mq.marketer.campaign.controller.contacts;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.layout.EditorController;
import org.mq.marketer.campaign.dao.CouponsDaoForDML;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.image.AImage;
import org.zkoss.util.media.Media;
import org.zkoss.zhtml.Div;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;

@SuppressWarnings("rawtypes")
public class OffersNotificationWeb extends GenericForwardComposer{

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private String currentUserName;
	private Session sessionScope;
	private Image img,imgBarcode;
	private Label msgLb,promotionCodes,mobileHeaddingDisplayId,headderContentId;
	private Listbox mergeTagListboxId;
	private Checkbox  promotionBarCodeId,insertPromotionCouponLbId,highLighMobileCheckId;
	private Set<Long> listIdsSet;
	private MailingListDao mailingListDao;
	private Textbox headerId,redirectUrl;
	private CouponsDaoForDML couponsDaoForDML;
	private String usersParentDirectory = null;
	private String mobileOfferImageDirectory = null;
	private Textbox bodyContentId,bodytextId,placeHolderDropdownId;
	private Div couponBasicSettingsFirstId,promotionRulesSecondId;
	
	
	public OffersNotificationWeb() {
		sessionScope = Sessions.getCurrent();
		currentUserName = GetUser.getUserName();
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Offer Details","",style,true);
		mailingListDao = (MailingListDao) SpringUtil.getBean("mailingListDao");
		couponsDaoForDML = (CouponsDaoForDML)SpringUtil.getBean("couponsDaoForDML");
		usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory");
		mobileOfferImageDirectory = PropertyUtil.getPropertyValue("mobileOfferImageDirectory");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		bodyContentId.setVisible(false);
		bodytextId.setVisible(false);
		placeHolderDropdownId.setVisible(false);
		listIdsSet = (Set<Long>) sessionScope.getAttribute(Constants.LISTIDS_SET);
		Set<MailingList> set = new HashSet<MailingList>();
		if(listIdsSet != null && listIdsSet.size() > 0){
			set.addAll(mailingListDao.findByIds(listIdsSet));
			getPlaceHolderList(set);
		}
		loadDataOnEdit();
	}
	
	private void loadDataOnEdit() {
		Coupons currentCouponObject = (Coupons)sessionScope.getAttribute("COUPON_OBJECT_FOR_MOBILE_OFFERS");
		String ccPreviewUrl =  generateBarCodeImage(currentCouponObject);
		if(currentCouponObject!=null) {
			if(currentCouponObject.getBannerImage()!=null && !currentCouponObject.getBannerImage().isEmpty()) {
				try {
					img.setSrc("/UserData/"+currentUserName+mobileOfferImageDirectory+currentCouponObject.getBannerImage());
					img.setWidth(285+"px");
					img.setHeight(220+"px");
					img.setStyle("margin:auto;padding:auto;");
				}catch (Exception e) {
					logger.error("Image resolution :: "+e);
				}
			}
			if(currentCouponObject.getBannerUrlRedirect()!=null && !currentCouponObject.getBannerUrlRedirect().isEmpty()) {
				redirectUrl.setValue(currentCouponObject.getBannerUrlRedirect());
			}
			if(currentCouponObject.getOfferHeading()!=null && !currentCouponObject.getOfferHeading().isEmpty()) {
				headerId.setValue(currentCouponObject.getOfferHeading());
				headderContentId.setValue(currentCouponObject.getOfferHeading());
			}
			if(currentCouponObject.getCouponCode()!=null && !currentCouponObject.getCouponCode().isEmpty()) {
				promotionCodes.setValue(currentCouponObject.getCouponCode());
			}else {
				promotionCodes.setValue(currentCouponObject.getCouponName());
			}
			
			if(ccPreviewUrl!=null && currentCouponObject.getEnableBarcode()) {
				imgBarcode.setSrc(ccPreviewUrl);
			}
			highLighMobileCheckId.setChecked(currentCouponObject.isHighlightedOffer());
			bodyContentId.setValue(currentCouponObject.getOfferDescription());
			mobileHeaddingDisplayId.setValue("Offers details");
			mobileHeaddingDisplayId.setStyle("font-size: 16px;");
		}
	}

	private String generateBarCodeImage(Coupons coupon) {
		try {
		String ccPreviewUrl = null;
		if(coupon!=null && (coupon.getStatus().equals(Constants.COUP_STATUS_ACTIVE)||coupon.getStatus().equals(Constants.COUP_STATUS_RUNNING))) {
				
				if(coupon.getEnableBarcode() && coupon.getBarcodeType() != null && coupon.getBarcodeWidth() != null
						&& coupon.getBarcodeHeight() != null){

					
					String couponIdStr = Constants.CC_TOKEN + coupon.getCouponId() +"_"+coupon.getCouponName()+ 	
							"_"+coupon.getBarcodeType()+"_"+coupon.getBarcodeWidth()
							+"_"+coupon.getBarcodeHeight();

					//generate image dynamically and set
					//generate barcode image
					BitMatrix bitMatrix = null;
					
					String COUPON_CODE_URL = null;
					
					String message = "Test:"+coupon.getCouponName();
					String barcodeType = coupon.getBarcodeType().trim();
					int width = coupon.getBarcodeWidth().intValue();
					int height = coupon.getBarcodeHeight().intValue();
					
					if(barcodeType.equals(Constants.COUP_BARCODE_QR)){
						
						bitMatrix = new QRCodeWriter().encode(message, BarcodeFormat.QR_CODE, width, height,null);
						String bcqrImg = GetUser.getUserName()+File.separator+
								"Preview"+File.separator+"QRCODE"+File.separator+couponIdStr+".png";
						
						COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcqrImg;
						ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcqrImg;
					}
					else if(barcodeType.equals(Constants.COUP_BARCODE_AZTEC)){
						
						bitMatrix = new AztecWriter().encode(message, BarcodeFormat.AZTEC, width, height);
						String bcazImg = GetUser.getUserName()+File.separator+
								"Preview"+File.separator+"AZTEC"+File.separator+couponIdStr+".png";
						
						COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcazImg;
						ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcazImg;
					}
					else if(barcodeType.equals(Constants.COUP_BARCODE_LINEAR)){
						
						bitMatrix = new Code128Writer().encode(message, BarcodeFormat.CODE_128, width, height,null);
						String bclnImg = GetUser.getUserName()+File.separator+
								"Preview"+File.separator+"LINEAR"+File.separator+couponIdStr+".png";
						
						COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bclnImg;
						ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bclnImg;
					}
					else if(barcodeType.equals(Constants.COUP_BARCODE_DATAMATRIX)){
						
						bitMatrix = new DataMatrixWriter().encode(message, BarcodeFormat.DATA_MATRIX, width, height,null);
						String bcdmImg = GetUser.getUserName()+File.separator+
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
							MatrixToImageWriter.writeToStream(bitMatrix, "png", new FileOutputStream(new File(COUPON_CODE_URL)));
					}
				}
			}
		return ccPreviewUrl;
		}catch (IOException | WriterException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void onClick$uploadBtn() throws Exception {
		try {
		msgLb.setValue("");
		Media media = Fileupload.get();
		if(media == null){
			return;
		}
		String type = media.getContentType().split("/")[0];
	    if(validateImageFormat(media)) {
			if (type.equals("image")) {
				if(media!=null && media.getByteData()!=null) {
			        double fileSize = Math.ceil((media.getByteData().length))/1024;
						if(fileSize>10240){
							MessageUtil.setMessage(media.getName()+" cannot be uploaded.\n Reason: Size should not exceed 10 MB." , "color:red", "TOP");
							return;
						}
						String path = usersParentDirectory + File.separator +  currentUserName + mobileOfferImageDirectory;
						String imgPath = path +"/" +media.getName().trim();
						File file1  = new File(imgPath);
						if(file1.exists()) {
							MessageUtil.setMessage("Image "+media.getName()+" already exists.","color:red","TOP");
							return;
						}
						
			     } 
				//BufferedImage data = getImage(media.getByteData());
				//java.awt.Image picture = (java.awt.Image) data.getScaledInstance(285,220, java.awt.Image.SCALE_DEFAULT);
				//data = toBufferedImage(picture);
				//data.createGraphics();
		        //img.setContent(data);
				
					if (media instanceof AImage) {
						img.setContent((AImage) media);
						img.setWidth(285+"px");
						img.setHeight(220+"px");
						img.setStyle("margin:auto;padding:auto;");
						img.setAttribute("imageName", media.getName());
					}
		        mobileHeaddingDisplayId.setValue("Offers details");
		        mobileHeaddingDisplayId.setStyle("font-size: 16px;");
		        MessageUtil.setMessage(media.getName() +" uploaded successfully!", "color: blue;");
			}else {
				 return;
			}
	    }else {
			 return;
		}
		}catch (Exception e) {
			logger.error("Image resolution :: "+e);
		}
	}
	
	public static BufferedImage toBufferedImage(java.awt.Image img){
	    if (img instanceof BufferedImage)
	    {
	        return (BufferedImage) img;
	    }
	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();
	    // Return the buffered image
	    return bimage;
	}
	
	
	public BufferedImage getImage(byte[] data) throws Exception
	 {
	    BufferedImage bi = null;
	    ByteArrayInputStream bais = new ByteArrayInputStream(data);
	    bi = ImageIO.read(bais);	    
	    return bi;
	 }
	
	
	public void onSelect$mergeTagListboxId() {
		if(mergeTagListboxId.getSelectedIndex() == 0) {
			return;
		}
		String value = (String)mergeTagListboxId.getSelectedItem().getValue();
		Clients.evalJavaScript("insertAtCursor(document.getElementById('" + headerId.getUuid() + "'), '"+value+"')");
		Clients.evalJavaScript("displayIndivOnKeyup();");
	}
	
	
	public  List<String> getPlaceHolderList(Set<MailingList> mlistSet) {
		StringBuffer buffer = new StringBuffer();
		try {
			logger.debug("-- Just Entered --");
			MLCustomFieldsDao mlCustomFieldsDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
			logger.debug("Got Ml Set of size :" + mlistSet.size());
			
			POSMappingDao posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
			List<String> placeHoldersList = new ArrayList<String>(); 
			
            
			
			List<POSMapping> contactsGENList = posMappingDao.findOnlyByGenType(Constants.POS_MAPPING_TYPE_CONTACTS, GetUser.getUserId() );
			
			placeHoldersList.addAll(Constants.PLACEHOLDERS_LIST);
			Users user = GetUser.getUserObj();
			
			
			if(user.getloyaltyServicetype() != null && user.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_SB) )
			{
				placeHoldersList.removeIf(e -> e.contains("Loyalty Gift Balance"));
			}
			if(user.getloyaltyServicetype() != null && user.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_OC) )
			{
				placeHoldersList.removeIf(e -> e.contains("Loyalty Membership Pin"));
			}
			
			if(user.getloyaltyServicetype() != null && !user.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_SB) )
			{
				//logger.info("the current user is a oc user");
				placeHoldersList.addAll(Constants.OCPLACEHOLDERS_LIST);
			}

			
			Map<String , String> StoreDefaultPHValues = EditorController.getDefaultStorePhValue(placeHoldersList);
			
			//Changes to add mapped UDF fields as placeholders
			//1.get all the pos mapped UDFs from the user pos settings(table:pos_mappings)
			List<POSMapping> contactsUDFList = posMappingDao.findOnlyByType(Constants.POS_MAPPING_TYPE_CONTACTS, GetUser.getUserId() );
			
			
				
				if(contactsUDFList != null) {
					
					//2.prepare merge tag and add to placeHoldersList
					//format : display lable :: |^GEN_<UDF>^|
					for (POSMapping posMapping : contactsUDFList) {
						
						String udfStr;
						if(posMapping.getDefaultPhValue()==null || posMapping.getDefaultPhValue().trim().isEmpty()) {
						
						udfStr = Constants.UDF_TOKEN + posMapping.getDisplayLabel() +
										Constants.DELIMETER_DOUBLECOLON +
										Constants.CF_START_TAG + Constants.UDF_TOKEN +
										posMapping.getCustomFieldName()  + Constants.DELIMETER_SPACE + Constants.DELIMETER_SLASH + Constants.DELIMETER_SPACE + Constants.DEFUALT_TOKEN+Constants.CF_END_TAG ;
						
						
						}
						else {
							 udfStr = Constants.UDF_TOKEN + posMapping.getDisplayLabel() +
									Constants.DELIMETER_DOUBLECOLON +
									Constants.CF_START_TAG + Constants.UDF_TOKEN +
									posMapping.getCustomFieldName()+ Constants.DELIMETER_SPACE + Constants.DELIMETER_SLASH + Constants.DELIMETER_SPACE + Constants.DEFUALT_TOKEN + posMapping.getDefaultPhValue() + Constants.CF_END_TAG ;
					
						
						}
						placeHoldersList.add(udfStr);
					}//for
					
					
					
				}//if
			//END
			
			for (MailingList mailingList : mlistSet) {
				
				if(!mailingList.isCustField())  continue;
				
				List<MLCustomFields> mlcust = mlCustomFieldsDao.findAllByList(mailingList);
				String custField ;
				for (MLCustomFields customField : mlcust) {
					custField = Constants.CF_TOKEN + customField.getCustFieldName() 
								+ Constants.DELIMETER_DOUBLECOLON + Constants.CF_START_TAG + 
								Constants.CF_TOKEN + 
								customField.getCustFieldName().toLowerCase() + Constants.CF_END_TAG;
					
					if(placeHoldersList.contains(custField)) continue;
					placeHoldersList.add(custField);
				}
				
			} // for
			
			Listitem item = null;
			for (String placeHolder : placeHoldersList) {
				
				if(placeHolder.trim().startsWith("--") || placeHolder.toLowerCase().contains(("place holder"))) { //Ignore
					continue;
				}
				
				if(placeHolder.startsWith("Unsubscribe Link") || placeHolder.startsWith("Web-Page Version Link") ||
						placeHolder.startsWith("Share on Twitter") || placeHolder.startsWith("Share on Facebook") ||
						placeHolder.startsWith("Forward To Friend") || placeHolder.startsWith("Subscriber Preference Link") ){
					continue;
				}
				
				String[] phTokenArr =  placeHolder.split("::"); 
				String key = phTokenArr[0];
				StringBuffer value = new StringBuffer(phTokenArr[1]);
//				logger.debug("key ::"+key+" value ::"+value);
				if(StoreDefaultPHValues.containsKey(placeHolder)) {
					
					value.insert(value.lastIndexOf("^"), StoreDefaultPHValues.get(placeHolder));
//					logger.info(" store ::"+placeHolder + " ====== value == "+value );
					placeHolder = placeHolder.replace(phTokenArr[1], value.toString());
				}
				
				
				for (POSMapping posMapping : contactsGENList) {
					
					if(!key.equalsIgnoreCase(posMapping.getCustomFieldName()) || posMapping.getCustomFieldName().startsWith("UDF")  ) continue;
					
					if(posMapping.getDefaultPhValue() == null || posMapping.getDefaultPhValue().isEmpty() ) break;
					
					value.insert(value.lastIndexOf("^"),  posMapping.getDefaultPhValue() );
					logger.debug(" value ::"+value);
				}
				item =  new Listitem(key,value.toString());
				
				if(buffer.length() > 0) buffer.append(",");
				key=StringEscapeUtils.escapeJavaScript(key);
				buffer.append("'"+key+"'"+":"+"'"+value+"'"); 
				item.setParent(mergeTagListboxId);
			} // for
			placeHolderDropdownId.setValue("{"+buffer+"}");
			logger.debug("-- Exit --");
			return placeHoldersList;
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
	public void onClickSaveOfferData$jsonData(ForwardEvent event) throws Exception {
		try {
		Object htmlPromotionDescription = JSONValue.parse(event.getOrigin().getData().toString());
		Coupons currentCouponObject = (Coupons)sessionScope.getAttribute("COUPON_OBJECT_FOR_MOBILE_OFFERS");
		if(currentCouponObject!=null) {
			if(img.getSrc()!= null || img.getContent()!= null) {
				 logger.info("loaded image");
			 }
			if(redirectUrl!=null) {
				String website = redirectUrl.getValue().trim();
				StringBuilder sbWebsite = new StringBuilder(website);
				if((!website.contains("http://")) && (!website.contains("https://"))) {
					sbWebsite.insert(0,"http://");
				}
				if(website!= null && website.trim().length() > 0 && (!website.equals("https://") && !website.equals("http://")) && !validateUrl(sbWebsite.toString().trim())){
					MessageUtil.setMessage("Please provide valid Website URL.", "color:red", "TOP");
					return;
				}
				if(website.equals("https://") || website.equals("http://") || (sbWebsite.toString().equals("https://") || sbWebsite.toString().equals("http://")))
					currentCouponObject.setBannerUrlRedirect("");
				else
					currentCouponObject.setBannerUrlRedirect(sbWebsite.toString().trim());
			}
			if(htmlPromotionDescription!=null) {
				JSONObject jsonObj = (JSONObject) htmlPromotionDescription;
				String htmlQuill = (String) jsonObj.get("htmlQuill");
				String htmlQuillText = (String) jsonObj.get("htmlQuillText");
				String headerText = (String) jsonObj.get("headerText");
				
				if(headerText!=null && !headerText.trim().isEmpty() && !headerText.isEmpty()) {
					currentCouponObject.setOfferHeading(headerText);
				}else {
					MessageUtil.setMessage("Please provide Heading. ", "color:red", "TOP");
					return;
				}
				
				if(htmlQuill.isEmpty() || htmlQuill.trim().isEmpty() || htmlQuill.trim().equals("<p><br></p>")) {
					MessageUtil.setMessage("Please provide Offer Description.", "color:red", "TOP");
					return;
				}else if(htmlQuillText.length() >= 3000 && htmlQuillText.chars().allMatch(Character::isLetterOrDigit)) {
					MessageUtil.setMessage("Offer Description must be less than 3000 characters", "color:red", "TOP");
					return;
				}else if(htmlPromotionDescription!=null && !htmlQuill.isEmpty()) {
					currentCouponObject.setOfferDescription(htmlQuill);
				}
			}else {
				MessageUtil.setMessage("Please provide Offer Description.", "color:red", "TOP");
				return;
			}
			currentCouponObject.setEnableOffer(true);
			
			String newEditorfolderPathStr = usersParentDirectory+File.separator+ GetUser.getUserObj().getUserName()+File.separator+"Coupon"+File.separator;
			String dirNameOffer= "offerBanner";
			 File newEditorfolder = new File(newEditorfolderPathStr+dirNameOffer);
			 if(newEditorfolder.isDirectory()) {
				logger.info("folder Exists");
			 }else {
				 File newDir= new File(newEditorfolderPathStr+dirNameOffer);
				 newDir.mkdir(); 
			 }
			 
			 if(img!=null && img.getContent()!=null) {
						 String imageName = (String)img.getAttribute("imageName");
						 String path = usersParentDirectory + File.separator +  currentUserName + mobileOfferImageDirectory;
							File file = new File(path);
							if(!file.exists()) {
								file.mkdirs();
							}
							String imgPath = path +"/" +imageName;
							File file1  = new File(imgPath);
							if(file1.exists()) {
								MessageUtil.setMessage("Image "+imageName+" already exists.","color:red","TOP");
								return;
							}
					 
					 if(currentCouponObject!=null && currentCouponObject.getBannerImage()!=null && !currentCouponObject.getBannerImage().isEmpty()) {
							String deleteImagePath = usersParentDirectory + "/" +  currentUserName + mobileOfferImageDirectory + currentCouponObject.getBannerImage();
							File fileDeletePath = new File(deleteImagePath);
								if(fileDeletePath.exists()) {
									fileDeletePath.delete();
								}
						}
					 
					 if(imageName!=null) {
						 currentCouponObject.setBannerImage(imageName);
					 }else {
						 return;
					 }
			 	}
			currentCouponObject.setHighlightedOffer(highLighMobileCheckId.isChecked());
			couponsDaoForDML.saveOrUpdate(currentCouponObject);
			if(img!=null && img.getContent()!=null && img.getAttribute("imageName")!=null) {
				upload(img.getContent(),(String)img.getAttribute("imageName"));
			}
			MessageUtil.setMessage("Offer saved successfully.", "color:blue");
			sessionScope.removeAttribute("COUPON_OBJECT_FOR_MOBILE_OFFERS");
			Redirect.goTo(PageListEnum.ADMIN_VIEW_COUPONS);
		}
		}catch (Exception e) {
			logger.error("offer Notificaton saving :"+e);
		}
	}
	
	
	private boolean validateUrl(String website) {
		try { 
			(new java.net.URL(website)).openStream().close();
			return true;
		}catch (Exception e) {
			return false;
		}
	}

	/*private boolean isAnySpecialChar(Media media){
			if(media!=null && media.getName()!=null && !media.getName().isEmpty()) {
				String imagName = media.getName();
				logger.info("+++++++++++++++++"+imagName);
					try {
						if(!Utility.validateUploadFilName(imagName) || imagName.contains("'")) {
							return true;
						}else {
							return false;
						}
					} catch (Exception e) {
						logger.error("validate image" +e);
					}
			}
			return false;
	}*/
	
	
	private boolean validateImageFormat(Media media){
		try {
			if(media!=null && (media.getName()!=null && !media.getName().isEmpty())) {
			String imagName = media.getName();
			String ext =  FilenameUtils.getExtension(media.getName());
				if((!ext.equalsIgnoreCase("jpeg") && !ext.equalsIgnoreCase("jpg") && !ext.equalsIgnoreCase("png") && !ext.equalsIgnoreCase("gif"))){
					MessageUtil.setMessage("Upload of image " +imagName+ " failed, upload images of format .jpeg, .jpg, .png, .gif  only.", "color:red", "TOP");
					return false;
				}
				if(!Utility.validateUploadFilName(imagName) || imagName.contains("'")) {
					MessageUtil.setMessage("Image names should be alpha-numeric. Special characters allowed are A-Z, a-z, 0-9, &, +, -, =, @, _ and space, If any other characters are used, images will not be uploaded.", "color:red", "TOP");
					return false;
				}
			String fileNameWithoutExtn = imagName.substring(0, imagName.lastIndexOf('.'));
			logger.debug(imagName +" ImageName  contains speacial characters" +"fileNameWithOutExt ::"+fileNameWithoutExtn);
			}
		}catch (Exception e) {
			logger.error("validate image" +e);
		}
		return true;
	}


	public void upload(Media media, String imageName) throws Exception {
		MessageUtil.clearMessage();
			try{
				String path = usersParentDirectory + "/" +  currentUserName + mobileOfferImageDirectory;
				File file = new File(path);
				
				if(!file.exists()) {
					file.mkdirs();
				}
				byte[] fi = media.getByteData();
				BufferedInputStream in = new BufferedInputStream (new ByteArrayInputStream (fi)); 
				FileOutputStream out = new FileOutputStream (new File(file.getPath()+"/"+imageName));
				//Copy the contents of the file to the output stream
				byte[] buf = new byte[1024];
				int count = 0;
				while ((count = in.read(buf)) >= 0){
					out.write(buf, 0, count);
				}
				in.close();
				out.close();
				}catch (Exception e) {
					logger.error("Exception :: error occured while Uploading the Image");
					logger.error("Exception ::", e);
			}
	}

	public void onCheck$insertPromotionCouponLbId(){
		if(insertPromotionCouponLbId.isChecked()) {
			Coupons currentCouponObject = (Coupons)sessionScope.getAttribute("COUPON_OBJECT_FOR_MOBILE_OFFERS");
			promotionCodes.setValue(currentCouponObject.getCouponCode());
		}else {
			promotionCodes.setValue(Constants.STRING_NILL);
		}
	}
	
	public void onCheck$promotionBarCodeId() {
		if(promotionBarCodeId.isChecked()) {
			Coupons currentCouponObject = (Coupons)sessionScope.getAttribute("COUPON_OBJECT_FOR_MOBILE_OFFERS");
			String type= null;
			String couponIdStr = Constants.CC_TOKEN + currentCouponObject.getCouponId() +"_"+currentCouponObject.getCouponName()+ 	
					"_"+currentCouponObject.getBarcodeType()+"_"+currentCouponObject.getBarcodeWidth()
					+"_"+currentCouponObject.getBarcodeHeight();
			switch(currentCouponObject.getBarcodeType()) {
				case Constants.COUP_BARCODE_LINEAR :
					type = "LINEAR";
					break;
				case Constants.COUP_BARCODE_DATAMATRIX :
					type = "DATAMATRIX";
					break;
				case Constants.COUP_BARCODE_AZTEC :
					type = "AZTEC";
					break;
				case Constants.COUP_BARCODE_QR:
					type = "QRCODE";
					break;
			}
			String path =  File.separator + "UserData" + File.separator +  currentUserName + File.separator +"Preview"+ File.separator + type + File.separator +couponIdStr+".png";
			imgBarcode.setSrc(path);
		}else {
			imgBarcode.setSrc(Constants.STRING_NILL);
		}
		
	}
	
	
public void onClick$promotionRulesSecondId() {
		Coupons currentCouponObject = (Coupons)sessionScope.getAttribute("COUPON_OBJECT_FOR_MOBILE_OFFERS");
		if(currentCouponObject== null) {
			logger.info("tempCouponObj is null next button won't work.");
			return;
		}
		sessionScope.setAttribute("EDIT_COUPON_OBJECT", currentCouponObject);
		Redirect.goTo(PageListEnum.ADMIN_VIEW_CREATECOUPONSUCCEEDING);

	}

public void onClick$couponBasicSettingsFirstId() {
	Coupons currentCouponObject = (Coupons)sessionScope.getAttribute("COUPON_OBJECT_FOR_MOBILE_OFFERS");
	if(currentCouponObject== null) {
		logger.info("tempCouponObj is null next button won't work.");
		return;
	}
	sessionScope.setAttribute("EDIT_COUPON_OBJECT", currentCouponObject);
	Redirect.goTo(PageListEnum.ADMIN_VIEW_CREATECOUPONS);
 }
	
}
