package org.mq.optculture.controller.ocevent;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.EventsDaoForDML;
import org.mq.optculture.model.events.Events;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.image.AImage;
import org.zkoss.util.media.Media;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;


public class CreateOCEventController extends GenericForwardComposer {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Textbox eNameTbId, subtitleTb, a1NameTbId, a2NameTbId, stateTbId, zipTbId, storeTbId,cityTbId,bodyContentId,
	rightButtonLabelTb,rightButtonURLTb,leftButtonURLTb,leftButtonLabelTb;
	private EventsDaoForDML eventsDaoForDML;
	private Session session ;
	private Image img[];// Have to use global variable due to Editor
	private String usersParentDirectory = null;
	private String currentUserName;
	private String eventsImageDirectory = null;
	private Div imagesDivId;
	private String dirId;// Have to use global variable due to Editor
	private Users user;
	private int count = 0;
	private Events eventSession;
	private Radio eventRadioOneDayId,eventRadioMultipleDaysId;
	private Radiogroup eventDayTypeRadioGrId;
	private MyDatebox eventStartDateboxId,eventEndDateboxId;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		bodyContentId.setVisible(false);
		eventsDaoForDML = (EventsDaoForDML) SpringUtil.getBean(OCConstants.EVENTS_DAO_ForDML);
		session =Sessions.getCurrent();
		usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory");
		currentUserName = GetUser.getUserName();
		eventsImageDirectory = PropertyUtil.getPropertyValue("eventsImageDirectory");
		user = GetUser.getUserObj();
		
		eventSession = (Events) session.getAttribute(OCConstants.SESSION_EDIT_EVENT);
		 
		if(eventSession!=null) {
			try {
			setEventFields(eventSession);
			}
			catch(WrongValueException e) {
				/** This exception comes because we are validating using ZK. 
				 Note: null or empty values not allowed in DB.
				 As we have constrained the value from UI. There is no chance of Null or empty value coming from UI.
				 But if someone tries to update a null or empty value or change Data-type from backed then this exception will happen.**/
				MessageUtil.setMessage(e.getMessage(),"color:red;");

				logger.info("Wrong Value exception in setEventFields"+e);
			}
			catch(Exception e) {
				logger.info("setEventFields-->e===>"+e);
			}
		}
		session.removeAttribute(OCConstants.SESSION_EDIT_EVENT);
	}

	
	// Null pointer check not needed as constraints are involved.
	private void saveEvent(String description) {
		logger.info("Entered saveEvent");
		Events event = new Events();	
		event.setEventTitle(eNameTbId.getValue().toString());
		event.setEventStartDate(getServerDate(eventStartDateboxId));
		event.setEventEndDate(getServerDate(eventEndDateboxId));
		event.setEventCreateDate(Calendar.getInstance());
		event.setUserId(user.getUserId());
		event.setSubtitle(subtitleTb.getValue().toString());
		event.setDescription(description);
		event.setAddressLine1(a1NameTbId.getValue().toString());
		event.setAddressLine2(a2NameTbId.getValue().toString());
		event.setState(stateTbId.getValue().toString());
		event.setZipCode(Long.parseLong(zipTbId.getValue().toString()));
		event.setStore(storeTbId.getValue().toString());
		event.setDirId(dirId); 
		event.setEventStatus(OCConstants.EVENT_STATUS_ACTIVE);
		event.setCity(cityTbId.getValue().trim().toString());
		event.setIsOneDay(eventDayTypeRadioGrId.getSelectedItem().equals(eventRadioOneDayId) ? true : false);
		event.setRightLable(rightButtonLabelTb.getValue().toString());
		event.setLeftLable(leftButtonLabelTb.getValue().toString());
		event.setRightLableURL(rightButtonURLTb.getValue().toString());
		event.setLeftLableURL(leftButtonURLTb.getValue().toString());
		event.setEventStartDateAPI(eventStartDateboxId.getRawText());
		event.setEventEndDateAPI(eventEndDateboxId.getRawText());		
		
		eventsDaoForDML.saveOrUpdate(event);	
	}
	// Null pointer check not needed as constraints are involved.
	private void setEventFields(Events event) {
		eNameTbId.setValue(event.getEventTitle());
		subtitleTb.setValue(event.getSubtitle());
		eventStartDateboxId.setValue(event.getEventStartDate());
		eventEndDateboxId.setValue(event.getEventEndDate());
		a1NameTbId.setValue(event.getAddressLine1());
		a2NameTbId.setValue(event.getAddressLine2());
		stateTbId.setValue(event.getState());
		zipTbId.setValue(event.getZipCode()+Constants.STRING_NILL);
		storeTbId.setValue(event.getStore());
		leftButtonLabelTb.setValue(event.getLeftLable() == null ? Constants.STRING_NILL : event.getLeftLable());
		rightButtonLabelTb.setValue(event.getRightLable() == null ? Constants.STRING_NILL : event.getRightLable());
		leftButtonURLTb.setValue(event.getLeftLableURL() == null ? Constants.STRING_NILL : event.getLeftLableURL());
		rightButtonURLTb.setValue(event.getRightLableURL()== null ? Constants.STRING_NILL : event.getRightLableURL());
		bodyContentId.setValue(event.getDescription());//For editor body content
		dirId = event.getDirId()==null ? Constants.STRING_NILL : event.getDirId(); // Setting 2
		eventDayTypeRadioGrId.setSelectedItem(event.getIsOneDay() == true ? eventRadioOneDayId : eventRadioMultipleDaysId);
		cityTbId.setValue(event.getCity());
		loadImages(dirId);
	}	
	private void loadImages(String dirId) {
		// TODO Auto-generated method stub
		String path = usersParentDirectory + File.separator +  currentUserName + eventsImageDirectory+ File.separator +dirId+File.separator;
		File pathFiles = new File(path);
        Components.removeAllChildren(imagesDivId);
		File[] imgArray = pathFiles.listFiles();
		createImageDiv(imgArray, imagesDivId);
	}



	public void onClickSaveEventData$jsonData(ForwardEvent event) throws Exception {
		Object htmlEventDescription = JSONValue.parse(event.getOrigin().getData().toString());
		
		String htmlQuill = Constants.STRING_NILL;
		if(htmlEventDescription!=null) {
			JSONObject jsonObj = (JSONObject) htmlEventDescription;
			htmlQuill = (String) jsonObj.get("htmlQuill");
			String htmlQuillText = (String) jsonObj.get("htmlQuillText");
			if(htmlQuill.isEmpty() || htmlQuill.trim().isEmpty() || htmlQuill.trim().equals("<p><br></p>")) {
				MessageUtil.setMessage("Please provide Event Description.", "color:red", "TOP");
				return;
			}else if(htmlQuillText.length() >= 5000 && htmlQuillText.chars().allMatch(Character::isLetterOrDigit)) {
				MessageUtil.setMessage("Event Description must be less than 5000 characters", "color:red", "TOP");
				return;
			}
		}
		if(!validateFields()) return;
		
		if(!validateZipcode()) return;
		
		if (imagesDivId.getChildren() == null || (imagesDivId.getChildren() != null && imagesDivId.getChildren().isEmpty())) {
			
			logger.info("imagesDivId.getChildren()==>"+ imagesDivId.getChildren());
			MessageUtil.setMessage("Image upload required.", "color:red", "TOP");
			return;
		}
		logger.info("imagesDivId.getChildren()==>"+ imagesDivId.getChildren());
		if(img != null && img.length !=0) {
			upload(img);
		}

		org.zkoss.zul.Messagebox.Button confirm= Messagebox.show(" Do you want to save the event?", "Confirm",
				new Messagebox.Button[] {Messagebox.Button.NO, Messagebox.Button.YES },
				Messagebox.INFORMATION, null, null); 
		if(confirm==null || !confirm.equals(Messagebox.Button.YES)) return;
		try {
			if(eventSession !=null) {
				updateEvent(eventSession,htmlQuill);	
			}
			else {
				saveEvent(htmlQuill);	
			}
		}
		catch(WrongValueException e) {
			MessageUtil.setMessage(e.getMessage(),"color:red;");

			logger.info("Wrong Value exception in setEventFields"+e);
			return;
		}	
			
		Redirect.goTo(PageListEnum.MANAGE_EVENT);

	}

	private boolean validateFields() {
		if(eventStartDateboxId.getValue() ==null || eventEndDateboxId.getValue()== null) {
			MessageUtil.setMessage("Both start and end dates are required.","color:red", "TOP");
			return false;
		}
		if(rightButtonLabelTb!=null && rightButtonLabelTb.getValue().length() > 20) {
			MessageUtil.setMessage("Right lable value can not be greater than 20 .","color:red", "TOP");
			return false;
		}
		if(leftButtonLabelTb!=null && leftButtonLabelTb.getValue().length() > 20) {
			MessageUtil.setMessage("Left lable value can not be greater than 20 .","color:red", "TOP");
			return false;
		}
		return true;
	}


	public void onClick$cancelBtnId() {
		Redirect.goTo(PageListEnum.MANAGE_EVENT);
	}

	private void updateEvent(Events event, String description) {
		// TODO Auto-generated method stub
		event.setEventTitle(eNameTbId.getValue().toString());
		event.setEventStartDate(getServerDate(eventStartDateboxId));
		event.setEventEndDate(getServerDate(eventEndDateboxId));
		event.setUserId(user.getUserId());
		event.setSubtitle(subtitleTb.getValue().toString());
		event.setDescription(description);
		event.setAddressLine1(a1NameTbId.getValue().toString());
		event.setAddressLine2(a2NameTbId.getValue().toString());
		event.setState(stateTbId.getValue().toString());
		event.setZipCode(Long.parseLong(zipTbId.getValue().toString()));
		event.setStore(storeTbId.getValue().toString());
		event.setDirId(dirId); 
		event.setCity(cityTbId.getValue().trim().toString());
		event.setIsOneDay(eventDayTypeRadioGrId.getSelectedItem().equals(eventRadioOneDayId) ? true :false );
		event.setIsOneDay(eventDayTypeRadioGrId.getSelectedItem().equals(eventRadioOneDayId) ? true : false);
		event.setRightLable(rightButtonLabelTb.getValue() == null ? Constants.STRING_NILL : rightButtonLabelTb.getValue().toString());
		event.setLeftLable(leftButtonLabelTb.getValue() == null ? Constants.STRING_NILL : leftButtonLabelTb.getValue().toString() );
		event.setRightLableURL(rightButtonURLTb.getValue() == null ? Constants.STRING_NILL : rightButtonURLTb.getValue().toString() );
		event.setLeftLableURL(leftButtonURLTb.getValue() == null ? Constants.STRING_NILL : leftButtonURLTb.getValue().toString());
		logger.info("event.getEventStartDate().getTimeZone().getDisplayName()==>"+eventStartDateboxId.getRawText());
		event.setEventStartDateAPI(eventStartDateboxId.getRawText());
		event.setEventEndDateAPI(eventEndDateboxId.getRawText());

		eventsDaoForDML.saveOrUpdate(event);
	}



	public void onUpload$uploadBtn(UploadEvent uploadEvent) throws Exception {
		try {
			int counter =0;
			if(uploadEvent.getMedias() == null){
				MessageUtil.setMessage("You uploaded no files!","color:red", "TOP");
				return;
			}
			img = new Image[uploadEvent.getMedias().length];
			StringBuilder sb = new StringBuilder("You uploaded: \n");
			
			if(dirId == null || dirId.isEmpty())
			dirId = System.currentTimeMillis()+Constants.STRING_NILL;//Setting 1
		
			for(Media media:uploadEvent.getMedias()) {
		
				sb.append(media.getName());
              	sb.append(" (");
                sb.append(media.getContentType());
                sb.append(")\n");
				String type = media.getContentType().split("/")[0];
			    if(validateImageFormat(media)) {
					if (type.equals("image")) {
						if(media!=null && media.getByteData()!=null) {
					        double fileSize = Math.ceil((media.getByteData().length))/1024;
								if(fileSize>10240){
									MessageUtil.setMessage(media.getName()+" cannot be uploaded.\n Reason: Size should not exceed 10 MB." , "color:red", "TOP");
									return;
								}
								String path = usersParentDirectory + File.separator +  currentUserName + eventsImageDirectory+ File.separator +dirId+File.separator;
								String imgPath = path +"/" +media.getName().trim();
								File file1  = new File(imgPath);
								if(file1.exists()) {
									MessageUtil.setMessage("Image "+media.getName()+" already exists.","color:red","TOP");
									return;
								}
								
					     } 
							if (media instanceof AImage) {
								
								img[counter] = new Image();
								img[counter].setContent((AImage) media);
								img[counter].setWidth(285+"px");
								img[counter].setHeight(220+"px");
								img[counter].setStyle("margin:auto;padding:auto;");
								img[counter].setAttribute("imageName", media.getName());
							}
			}else {
		        MessageUtil.setMessage(media.getName() +" file not image", "color: red;");

				 return;
			}
	    }else {
	        MessageUtil.setMessage(media.getName() +" image format incorrect", "color: red;");

			 return;
		}
		counter++;	    
		}
			createImageDiv(img,imagesDivId);
		    MessageUtil.setMessage( sb.toString()+" uploaded successfully!", "color: blue;");

		}catch (Exception e) {
			logger.error("Image resolution :: "+e);
		}
	}
	
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
	public void upload(Image imgs[]) throws Exception {
		
		for(Image img : imgs) {
		
		if(img==null || img.getContent()==null || img.getAttribute("imageName")==null) {
			
	        logger.info("image could not be uploaded");
	        continue;
		}
		Media media = img.getContent();
		String imageName = (String)img.getAttribute("imageName");
		MessageUtil.clearMessage();
			try{
				String path = usersParentDirectory + "/" +  currentUserName + eventsImageDirectory+ File.separator +dirId+File.separator ;
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
	}
	
	public void createImageDiv(File[] imgList,Div imagesDivId) {
		try{
			String imgPath = null;
			String absPath = null;
			String imgName = null;
			Vbox imageOuterVbox = null;
			Vbox imgInnerVbox = null;
			Image img = null;
			Label imgNameLbl = null;
			Hbox imageOptionsHbox = null;
			
			Image previewTlbBtn = null;
			Image delImg = null;
			String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
			int width = Integer.parseInt(PropertyUtil.getPropertyValue("imageThumbWidth"));
			int height = Integer.parseInt(PropertyUtil.getPropertyValue("imageThumbHeight"));
			
			File[] spimgList = imgList;
			
			for(int i=0;i<spimgList.length;i++){
				if(spimgList[i].isFile()) {
					imgName = spimgList[i].getName();
					logger.debug("image Name : " + imgName);
					if(!Utility.validateUploadFilName(imgName) || imgName.contains("'")) {
						spimgList[i].isHidden();
						
				} else {
					imgPath = "/UserData/"+ currentUserName + eventsImageDirectory +dirId+File.separator+imgName;
					logger.debug("Image path is : " + imgPath);
					
					
					imageOuterVbox = new Vbox();
					
					imageOuterVbox.setWidgetListener("onMouseOver", "onImgMouseOver(this)");
					imageOuterVbox.setWidgetListener("onMouseOut", "onImgMouseOut(this)");
					imageOuterVbox.setStyle("margin:2px;padding-left:20px;padding-top:20px;padding-right:20px;padding-bottom:5px;float:left;");
					imageOuterVbox.setAlign("center");
					imageOuterVbox.setId("vbox_"+count);
					
					imgInnerVbox = new Vbox();
					imgInnerVbox.setWidth(width + "px");
					imgInnerVbox.setHeight(height + "px");
					imgInnerVbox.setAlign("center");
					imgInnerVbox.setStyle("margin-left:2px;margin-top:1px;margin-bottom:2px;");
					
					img = new Image(imgPath);
					img.setWidth(width+"px");
					img.setHeight(height+"px");
					img.setStyle("margin:auto;padding:auto;");
					img.setAlign("center");
					img.setTooltiptext(imgName);
					img.setParent(imgInnerVbox);			
					
					imgNameLbl = new Label(imgName);
					imgNameLbl.setMaxlength(10);
					imgNameLbl.setTooltiptext(imgName);
					imgNameLbl.setParent(imgInnerVbox);		
					imgInnerVbox.setParent(imageOuterVbox);
					
					imageOptionsHbox = new Hbox();
					imageOptionsHbox.setPack("center");
					imageOptionsHbox.setWidth(width + "px");
					imageOptionsHbox.setHeight("40px");
					
					
					Vbox imageDetailsVbox= new Vbox();
					Label label=new Label(width+"X"+height);
					label.setStyle("font-size: 5px:");
					Label tempLabel = new Label((imgList[i].length()/10240)+" kb");
					label.setParent(imageDetailsVbox);
					tempLabel.setParent(imageDetailsVbox);
					imageDetailsVbox.setParent(imageOptionsHbox);
					
					String src = appUrl+ "UserData/"+currentUserName+eventsImageDirectory +dirId+File.separator+imgName;
					logger.info("imageName=====>"+imgName);
					previewTlbBtn = new Image("/img/theme/preview_icon.png");
					previewTlbBtn.setStyle("cursor:pointer;cursor:hand;margin:15px 0px 0px 15px");
					previewTlbBtn.setTooltiptext("Preview");
					previewTlbBtn.setWidgetListener("onClick", "previewWin('"+src+"')");
					
				
					
					logger.info("=====>"+src);
					previewTlbBtn.setVisible(false);
					previewTlbBtn.setParent(imageOptionsHbox);
					previewTlbBtn.setId("zoom_"+count);
					
					
					
					delImg = new Image("/img/icons/delete.png");
					
					delImg.setStyle("cursor:pointer;cursor:hand;margin-top:15px");
					delImg.setTooltiptext("Delete");
					
					//Set Attribute of FolderName  
					String imageFolderpath = usersParentDirectory + File.separator +  currentUserName + eventsImageDirectory;
					delImg.setAttribute("ImageFolder", imageFolderpath);
					
					//Set Attribute of Actual image Path
					absPath = usersParentDirectory + File.separator +  currentUserName + eventsImageDirectory +dirId+File.separator+imgName;
					delImg.setAttribute("path", absPath);
					delImg.setAttribute("imageName", imgName);
					
					delImg.setVisible(false);
					delImg.addEventListener("onClick", this);
					 
					delImg.setParent(imageOptionsHbox);
					delImg.setId("del_"+count);
					
					count=count+1;
					
					imageOptionsHbox.setParent(imageOuterVbox);
					
					imageOuterVbox.setParent(imagesDivId);	//finally vbox is added to images div
					//logger.debug("Vbox height" +imageOuterVbox.getHeight());
					
					
					
					Session session = Sessions.getCurrent();
					session.setAttribute("oldVbox",imageOuterVbox);
					//logger.debug("All components are added to div");
				}
				}else {
					logger.debug("not a file " + imgList[i].getPath());
				}
			}// for imgList
			}catch (Exception e) {
				logger.error("** Exception in uploadZIPFile **",e);
			}
		
	}// createImageDiv()
	public void createImageDiv(Image[] imgList,Div imagesDivId) {
		try{
			String imgPath = null;
			String absPath = null;
			String imgName = null;
			Vbox imageOuterVbox = null;
			Vbox imgInnerVbox = null;
			Image img = null;
			Label imgNameLbl = null;
			Hbox imageOptionsHbox = null;
			
			Image previewTlbBtn = null;
			Image delImg = null;
			String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
			int width = Integer.parseInt(PropertyUtil.getPropertyValue("imageThumbWidth"));
			int height = Integer.parseInt(PropertyUtil.getPropertyValue("imageThumbHeight"));
			
			Image[] spimgList = imgList;
			
			for(int i=0;i<spimgList.length;i++){
				
				imgName = (String)spimgList[i].getAttribute("imageName");
					
				logger.debug("image Name : " + imgName);
			
					imgPath = "/UserData/"+currentUserName+eventsImageDirectory +dirId+File.separator+imgName;
					logger.debug("Image path is : " + imgPath);
					
					
					imageOuterVbox = new Vbox();
					
					imageOuterVbox.setWidgetListener("onMouseOver", "onImgMouseOver(this)");
					imageOuterVbox.setWidgetListener("onMouseOut", "onImgMouseOut(this)");
					imageOuterVbox.setStyle("margin:2px;padding-left:20px;padding-top:20px;padding-right:20px;padding-bottom:5px;float:left;");
					imageOuterVbox.setAlign("center");
					imageOuterVbox.setId("vbox_"+count);
					
					imgInnerVbox = new Vbox();
					imgInnerVbox.setWidth(width + "px");
					imgInnerVbox.setHeight(height + "px");
					imgInnerVbox.setAlign("center");
					imgInnerVbox.setStyle("margin-left:2px;margin-top:1px;margin-bottom:2px;");
					
					img =spimgList[i];
					img.setWidth(width+"px");
					img.setHeight(height+"px");
					img.setStyle("margin:auto;padding:auto;");
					img.setAlign("center");
					img.setTooltiptext(imgName);
					img.setParent(imgInnerVbox);			
					
					imgNameLbl = new Label(imgName);
					imgNameLbl.setMaxlength(10);
					imgNameLbl.setTooltiptext(imgName);
					imgNameLbl.setParent(imgInnerVbox);		
					imgInnerVbox.setParent(imageOuterVbox);
					
					imageOptionsHbox = new Hbox();
					imageOptionsHbox.setPack("center");
					imageOptionsHbox.setWidth(width + "px");
					imageOptionsHbox.setHeight("40px");
					
					
					Vbox imageDetailsVbox= new Vbox();
					Label label=new Label(width+"X"+height);
					label.setStyle("font-size: 5px:");
					label.setParent(imageDetailsVbox);
					imageDetailsVbox.setParent(imageOptionsHbox);
					
					String src = appUrl+ "UserData/"+currentUserName+eventsImageDirectory + dirId + File.separator+imgName;
					logger.info("imageName=====>"+imgName);
					previewTlbBtn = new Image("/img/theme/preview_icon.png");
					previewTlbBtn.setStyle("cursor:pointer;cursor:hand;margin:15px 0px 0px 15px");
					previewTlbBtn.setTooltiptext("Preview");
					previewTlbBtn.setWidgetListener("onClick", "previewWin('"+src+"')");
					
				
					
					logger.info("=====>"+src);
					previewTlbBtn.setVisible(false);
					previewTlbBtn.setParent(imageOptionsHbox);
					previewTlbBtn.setId("zoom_"+count);
					
					
					
					delImg = new Image("/img/icons/delete.png");
					
					delImg.setStyle("cursor:pointer;cursor:hand;margin-top:15px");
					delImg.setTooltiptext("Delete");
					
					//Set Attribute of FolderName  
					String imageFolderpath = usersParentDirectory + File.separator +  currentUserName + eventsImageDirectory;
					delImg.setAttribute("ImageFolder", imageFolderpath);
					
					//Set Attribute of Actual image Path
					absPath = usersParentDirectory + File.separator +  currentUserName + eventsImageDirectory+dirId+File.separator+imgName;
					delImg.setAttribute("path", absPath);
					delImg.setAttribute("imageName", imgName);
					
					delImg.setVisible(false);
					delImg.addEventListener("onClick", this);
					
					delImg.setParent(imageOptionsHbox);
					delImg.setId("del_"+count);
					
					count=count+1;
					
					imageOptionsHbox.setParent(imageOuterVbox);
					
					imageOuterVbox.setParent(imagesDivId);	//finally vbox is added to images div
					
					
					
					Session session = Sessions.getCurrent();
					session.setAttribute("oldVbox",imageOuterVbox);
					//logger.debug("All components are added to div");
				}
				
			// for imgList
			}catch (Exception e) {
				logger.error("** Exception in uploadZIPFile **",e);
			}
		
	}// createImageDiv()

	public Calendar getServerDate(MyDatebox toDateboxId) {
		Calendar serverToDateCal = toDateboxId.getServerValue();		
		return serverToDateCal;
	}
	public void onChange$eventStartDateboxId (){
		if(eventStartDateboxId.getValue() ==null || eventEndDateboxId.getValue()== null) return;
		Calendar  start = eventStartDateboxId.getClientValue();
		Calendar end = eventEndDateboxId.getClientValue();
		
		if(end.before(start)) {
			MessageUtil.setMessage("'Event start' date cannot be later than 'Event end' date.", "red");
			eventStartDateboxId.setText(Constants.STRING_NILL);
			return;
		}
	}
	
	private boolean validateZipcode(){
		String countryType = user.getCountryType();
		String pin = null;
		try {
		 pin = zipTbId.getValue().trim();
		}
		catch(WrongValueException e) {
			logger.info("Wrong Value exception in setEventFields"+e);
			MessageUtil.setMessage(e.getMessage(),"color:red;");

			return false;
		}
//		if(zipTbId == null || zipTbId.getValue().isEmpty()) {
//			MessageUtil.setMessage(" Zip code cannot be left empty.","color:red;");
//			return false ;
//		}
		  if(Utility.zipValidateMap.containsKey(user.getCountryType())){
			if(Utility.zipValidateMap.containsKey(countryType)){
		    if(pin.length() == 0) {
					MessageUtil.setMessage(" Zip code cannot be left empty.","color:red;");
					return false ;
		    }
			 boolean zipCode = Utility.validateZipCode(pin, countryType);
			 if(!zipCode){
				 MessageUtil.setMessage("Please enter valid zip code.","color:red;");
					return false;
			 }
			}else{
				if(pin != null && pin.length() > 0){
					try{
		      } catch (NumberFormatException e) {
						MessageUtil.setMessage("Please provide 5 / 6 digits Zip Code.","color:red;");
						return false;
		      }
				if(pin.length() > 6 || pin.length() < 5) {
					//	Messagebox.show("Please provide 5 / 6 digits Zip code / pin.");
						MessageUtil.setMessage("Please provide 5 / 6 digits Zip code / Pin.", "Color:red", "Top");
						return false;
					}
				}
			}
			
		  }
		  return true;
	}
	
	public void onChange$eventEndDateboxId(){
		Calendar  start = null,end = null;
		try {
		  start = eventStartDateboxId.getClientValue();
		  end = eventEndDateboxId.getClientValue();
		}catch (Exception e) {
		  end = eventEndDateboxId.getClientValue();
		}
		if(end.before(start)) {
			MessageUtil.setMessage("'From' date cannot be later than 'To' date.", "red");
			eventEndDateboxId.setText(Constants.STRING_NILL);
			return;
		}
	}
	@Override
	public void onEvent(Event event) throws Exception {
		try{
			super.onEvent(event);
			Object eventObj = event.getTarget();
			
			if(eventObj instanceof Image) {  // Toolbarbutton
				try{
					
					
					if(Messagebox.show("If you delete this image, any email already sent using this image will open with blank image block(s)."
							+ " Do you still want to continue?",
							"Confirm", Messagebox.YES | Messagebox.NO, Messagebox.INFORMATION) == Messagebox.YES) {
						
						Image tbImg = (Image)eventObj;
						String imageName = (String)tbImg.getAttribute("imageName");
						if(Messagebox.show("You have chosen to delete "+imageName+". Click on 'Cancel' if you wish to keep this image.", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)  == Messagebox.OK){
							
							logger.debug("performing delete image operation");
							
							String path = (String)tbImg.getAttribute("path");
							Vbox vbox = (Vbox)tbImg.getParent().getParent();
							File delFile = new File(path);
							delFile.delete();
							imagesDivId.removeChild(vbox);
							//vbox.setVisible(false);
							
							MessageUtil.setMessage("Image deleted successfully.","color:blue;","TOP");
						
							
						}
						 
					 } // if
					else return;
									
			   } catch (Exception e) {
				// TODO: handle exception
				   //logger.info("Problem while deleting the image");
				MessageUtil.setMessage("Problem encountered while deleting the image.","color:red;","TOP");
				logger.error("Exception ::", e);;
			  }
		    }//if Toolbarbutton	
			else if(eventObj instanceof Vbox) {  // Vbox
				
				Vbox vbox=(Vbox)eventObj;
				boolean visibleFlag=((Toolbarbutton)(((Hbox)vbox.getChildren().get(1)).getChildren().get(0))).isVisible();
				
				((Toolbarbutton)(((Hbox)vbox.getChildren().get(1)).getChildren().get(0))).setVisible(!visibleFlag);
				((Toolbarbutton)(((Hbox)vbox.getChildren().get(1)).getChildren().get(2))).setVisible(!visibleFlag);
				
				if(!visibleFlag) {
					vbox.setStyle("margin:1px;padding-left:20px;padding-top:20px;padding-right:20px;padding-bottom:5px;float:left;border:#E1E1E1 1px solid;");
				}
				else {
					vbox.setStyle("margin:2px;padding-left:20px;padding-top:20px;padding-right:20px;padding-bottom:5px;float:left;");
				}
			}//else if
		}catch (Exception e) {
			logger.error("problem occured while handling the event ", e);
			
		}
	
	}//onEvent
	
}
