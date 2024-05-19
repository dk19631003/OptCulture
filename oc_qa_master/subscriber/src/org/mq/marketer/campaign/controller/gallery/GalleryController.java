package org.mq.marketer.campaign.controller.gallery;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
// import javax.websocket.OnClose;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.util.media.Media;
import org.zkoss.zhtml.Fileupload;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Center;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

@SuppressWarnings({ "unchecked", "serial" })
public class GalleryController extends GenericForwardComposer {
	
	String userName = null;
	String usersParentDirectory = null;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	//Div allImagesDivId = null;
	private Div folderOpnDivId;
	Div folderOptDivId = null;
	Listbox galleryListLbId;
	Textbox searchBox;
	
	UserActivitiesDao userActivitiesDao = null;
	UserActivitiesDaoForDML userActivitiesDaoForDML = null;
    private Map<Media,Boolean> imageStatusMap;	
   
	private Textbox newGallerySubWinId$newFolderTbId;
	//private Center  selGalleryCenterId;
	private Div imagesDivId;
	private Button uploadImgBtnId;
	private String image="Name";//2651
	
	//private Set<Long> userIdsSet = GetUser.getUsersSet();
	
	
	private Window newGallerySubWinId;
	private Window multipleUploadWinId;
	private int numberOfUploadedImages;
	private int numberOfUploadedImagesFailed;
	private Label multipleUploadWinId$successMsgLblId;
	private Label searchId;
	private boolean isZipFile;
	public GalleryController() {
		
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Images","",style,true);
	
		
		//userName = GetUser.getUserName();
		userName=GetUser.getUserObj().getUserName();
		usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory");
		userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
		userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		
		 if(userActivitiesDaoForDML != null) {
		     userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_GALLERY_VIEW,GetUser.getLoginUserObj());
		 }
		 
		 //imageStatusMap = new HashMap<Media,Boolean>();
	}// GalleryController()

	private final String IMAGE_MEDIA = "MEDIA";
	private final String CURRNUM = "CURRNUM";
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		//uploadImgBtnId.setStyle("background-image: url(../images/button_green_idle.jpg) repeat-x scroll 0 0 transparent;	color:#ffffff;border:1px solid #2B660A; width:140px;" );
		
		/*if(galleryListLbId.getItemCount()==0){
			//allImagesDivId.setVisible(false);
			galleryListLbId.setVisible(false);
		}*/
		//set the galleryListbox if contains  image name
		//searchBox.setName("Enter image name");
		//searchBox.setFocus(false);
	//	searchBox.setValue("Enter Image Name");
		 //testing here :
		 String beekey = PropertyUtil.getPropertyValueFromDB("beeFileManagerKey");
		Clients.evalJavaScript("beekey='"+beekey+"';");
		
		getImages(imagesDivId,folderOpnDivId);
		
		
	}
	 
	public void onOK$searchBox(){
		logger.info("Search string  : "+searchBox.getValue());
		if(searchBox.getValue().equals("Enter Image Name")){
			searchBox.setValue("");
		}	
		getImages(imagesDivId,folderOpnDivId,searchBox.getValue());
	}
	
	public List<String> getGalleryList(){
		
		String galPath = null;
		File galDir = null;
		List<String> galList = new ArrayList<String>();
		
		//UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDa");
		
		//List<Users> userList = usersDao.findAllByIds(userIdsSet);
		List<Users> userList = new ArrayList<Users>();
		
		userList.add(GetUser.getUserObj());
		for (Users user : userList) {
			
			galPath = usersParentDirectory + "/" +user.getUserName()+ "/Gallery";
			galDir = new File(galPath);
			
			if(!galDir.exists()){
				logger.debug(user.getUserName() + " - Gallery dir does not exist, so creating ");
				galDir.mkdir();
			}
			
			
			String[] filneNames = galDir.list();
			for (String fileName : filneNames) {
				
				galList.add(fileName);
				
			}//for
			
		}//for
		
		return galList;
	}// getGalleryList()

	
	
	public void getImages(Div imagesDivId, Div folderOpnDivId) {
	
	try{
		logger.debug("--just entered--");
		MessageUtil.clearMessage();
		
		if(galleryListLbId.getItemCount()>0) {
			if(galleryListLbId.getSelectedIndex() == -1) {
				galleryListLbId.setSelectedIndex(0);
			}
		}else{
			return;
		}
	
		//((Listcell)(galleryListLbId.getSelectedItem().getChildren().get(0))).setImage("/img/icons/arrow-current.jpg");
		String galName = (String)galleryListLbId.getSelectedItem().getValue();
		String galPath = usersParentDirectory + "/" +userName + "/Gallery/" + galName;
		File galFile = new File(galPath);
		
		if(galFile.isDirectory()){

			Session session = Sessions.getCurrent();
			session.setAttribute("selGalleryName", galName);
			Components.removeAllChildren(imagesDivId);
			
			folderOpnDivId.setVisible(true);
			File[] imgList = galFile.listFiles();
			logger.info("-------->ImageList"+imgList);
			createImageDiv(imgList,galName,imagesDivId);
			
			setTotalImageSizeToSelectedlistItem(imgList.length);
			
			/*List chaildLists = galleryListLbId.getChildren();
			
			for (Object object : chaildLists) {
				
				if(!(object instanceof Listitem )) continue;
				Listitem tempLisitItem = (Listitem)object;
				String listitemName = tempLisitItem.getLabel();
				if(listitemName.contains("(")){
					listitemName = listitemName.substring(0, listitemName.indexOf("("));
				}
				
				tempLisitItem.setLabel(listitemName);
				
			}
			
			logger.debug("Number of images in the gallery '" + galName + "' : " + imgList.length);
			galleryListLbId.getSelectedItem().setLabel(galName+ " ("+imgList.length+ "images"+")");*/  
			//selGalleryCenterId.setTitle(galName+ " ("+imgList.length+ "images"+")");  
			//selGalleryCenterId.setStyle("glImageListStyle");
		}
	}catch (Exception e) {
		logger.error("Exception: Error occured while getting the Images ",e);
		
	}
}// getImages()

	public void getImages(Div imagesDivId, Div folderOpnDivId,String searchStr) {

		
		try{
			logger.debug("--just entered--");
			MessageUtil.clearMessage();
			
				if(galleryListLbId.getItemCount()>0) {
					if(galleryListLbId.getSelectedIndex() == -1) {
						galleryListLbId.setSelectedIndex(0);
					}
				}else{
					return;
				}
			int imgCount=0;
			int finalImgCount = 0;
			String userGallery = usersParentDirectory + "/" +userName + "/Gallery/";
			File usrGalFile = new File(userGallery);
			File[] galArray = usrGalFile.listFiles();
            Components.removeAllChildren(imagesDivId);
			int imgList1= 0; 
			for(int i=0;i<galArray.length;i++) {
			File galFile= galArray[i];
				File[] imgList = galFile.listFiles();

               if(galFile.isDirectory()) {
				
				if(searchStr==null ||  searchStr.length()==0)
				createImageDiv(imgList,galFile.getName(),imagesDivId);
				if(searchStr.length()==0 || searchStr== "Enter Image Name"){
					finalImgCount=1;
				}
				else 	
				imgCount = createImageDivBySearch(imgList,galFile.getName(),imagesDivId,searchStr);
				finalImgCount += imgCount;
				}imgList1=imgList1 + imgList.length;
				if(finalImgCount==0){
				searchId.setVisible(true);
				}
				else searchId.setVisible(false);
				
				try {
					logger.info(""+galleryListLbId.getSelectedItem().getValue());
					galleryListLbId.getSelectedItem().setSelected(false);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					logger.info("");
				}
				
				/*try {
					galleryListLbId.getSelectedItem().setSelected(false);
				} catch (Exception e) {
					logger.info("test1");
				}*/
			}
				/*List chaildLists = galleryListLbId.getChildren();
				
				for (Object object : chaildLists) {
					
					if(!(object instanceof Listitem )) continue;
					Listitem tempLisitItem = (Listitem)object;
					String listitemName = tempLisitItem.getLabel();
					if(listitemName.contains("(")){
						listitemName = listitemName.substring(0, listitemName.indexOf("("));
					}
					
					tempLisitItem.setLabel(listitemName);
				}*/
			
		}catch (Exception e) {
			logger.error("Exception: Error occured while getting the Images ",e);
			return;
		}
		
	}// getImages()
	

	
	
	public void clearImages(Div imagesDivId,Listbox galleryListLbId )throws Exception {
		
		
		logger.debug("---Just Entered---");
		String userGallery = null;
		
		try {
			/*if(Messagebox.show("Do you want to delete all images in this folder?", "Confirm Delete",
					 Messagebox.OK | Messagebox.CANCEL, Messagebox.INFORMATION) != Messagebox.OK) {
				 return;
			 } // if
*/			
			
			if(Messagebox.show("Clearing all images may cause email(s) already sent using these images to open with"
					+ " blank image block(s). Do you still want to continue?", "Confirm", 
					Messagebox.YES | Messagebox.NO, Messagebox.INFORMATION) == Messagebox.YES) {
				
				if(Messagebox.show("You have chosen to delete all the images. Click on 'Cancel' if you wish to keep the images."
						+ "", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)  == Messagebox.OK){
					
					String folderName = (String)galleryListLbId.getSelectedItem().getValue();
					userGallery = usersParentDirectory + "/" +userName + "/Gallery" +"/" +folderName;
					File usrGalFile = new File(userGallery);
					if(usrGalFile.exists() && usrGalFile.isDirectory()){
						
						 logger.debug("Removing images of corresponding foldername is : "+usrGalFile.getAbsolutePath());
						 String galleryFileNameArr[] = usrGalFile.list();
						 
						 File tempFile = null;
						 boolean flag = false;
						 
						 if(galleryFileNameArr.length > 0) {
							 try {
								for(int i=0; i<galleryFileNameArr.length; i++){
									tempFile = new File(userGallery +"/" +galleryFileNameArr[i]);
									if(tempFile.isFile()){
										flag = tempFile.delete();
									}
									
									if(flag){
										logger.debug("file deleted successfully :"+ galleryFileNameArr[i]);
									}
								 }
								MessageUtil.setMessage("All images are deleted successfully from the folder: "+folderName, "color:green", "TOP");
							} catch (Exception e) {
								logger.debug("Exception while deleting the image from the folder",e);
								MessageUtil.setMessage(" Exception: error occured while deleting images from the folder: "+folderName, "color:red", "TOP");
							}
						 }
					}
					//imagesDivId.setVisible(true);
					Components.removeAllChildren(imagesDivId);
					//imagesDivId.setVisible(false);   
					//selGalleryCenterId.setTitle(folderName +"(0 images)");
					setTotalImageSizeToSelectedlistItem(0);
					
					
				}
				 
			 } // if
			else return;
			
					
			
		} catch (Exception e) {
			logger.debug("***Exception:error  occured while clearing the Images",e);
		}
	}// clearImages()
	
	public void deleteGallery(Div imagesDivId,Div folderOpnDivId) {
		
		try{
			logger.debug("----just enterd here----");
			
			if(galleryListLbId.getSelectedIndex() == -1){
				
				MessageUtil.setMessage("Please select folder name.", "color:blue", "TOP");
				return;
			}
			String selectFolderName = (String)galleryListLbId.getSelectedItem().getLabel();
			
			selectFolderName = selectFolderName.substring(0, selectFolderName.indexOf("("));
						
			int cFirm = Messagebox.show("If you delete this folder, any email already sent using image(s) from this folder will open with blank image block(s). Do you still want to continue?", "Confirm", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION);
			
			if(cFirm == 16){
				
				int confirm = Messagebox.show("You have chosen to delete '" + selectFolderName + "' . Click on 'Cancel' if you wish to keep this folder.", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			
				if(confirm == 1) {
					
					String delDirPath = usersParentDirectory + "/" + userName + "/Gallery/" + selectFolderName.trim();
					//logger.info("Dir Path::"+delDirPath);
					
					//delete directory 
					FileUtils.deleteDirectory(new File(delDirPath));
					
					//File tempFile = new File(delDirPath);
					//boolean isdeletedFlag = tempFile.delete();
					
					/*logger.info("is deleteed dir is ::"+isdeletedFlag);
					if(isdeletedFlag == false) {
						logger.debug("folder can not be be deleted");
						return;
					}*/
					
					Listitem li  = galleryListLbId.getSelectedItem();
					galleryListLbId.removeChild(li);
					int count = galleryListLbId.getItemCount();
					
					if(count>0){
						
						galleryListLbId.setSelectedIndex(0);
					//	getImages(imagesDivId,folderOpnDivId,null);
						getImages(imagesDivId,folderOpnDivId);
						galleryListLbId.setVisible(true);
						//allImagesDivId.setVisible(true);
						folderOpnDivId.setVisible(true);
					} else {
						
						//galleryListLbId.setVisible(false);
						//allImagesDivId.setVisible(false);
						folderOpnDivId.setVisible(false);
						Components.removeAllChildren(imagesDivId);
					}
					
					if(userActivitiesDaoForDML != null) {
						
	            		userActivitiesDaoForDML.addToActivityList(ActivityEnum.GALLRY_DEL_FOLDER_p1folderName, GetUser.getLoginUserObj(), selectFolderName);
					}
					
					logger.debug(userName + " - " + selectFolderName + "Gallery deleted successfully");
					//selGalleryCenterId.setTitle("");
					MessageUtil.setMessage("Folder deleted successfully.", "color:blue", "TOP");
					
				 } // if 
				
				
			}
			
			
			/*int confirm = Messagebox.show("Do you want to  delete the folder : '" + selectFolderName + "' permanently?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			//boolean isdeletedFlag = false;
			*/
			} catch (Exception e) {
				
				logger.error(userName + " - ** Exception : while deleting the gallery " + e);
				
//				MessageUtil.setMessage("Folder deleted successfully.", "color:blue", "TOP");
			}
		
	}// deleteGallery()
	
	//private Map<String, Object> imageMap = null;
	
	private boolean changeImgNameflag = false;
	public void upload(Media media,Div imagesDivId) {
		
		try {
			MessageUtil.clearMessage();
			//logger.debug("----just entered Here---9");
			if(media == null){
				MessageUtil.setMessage("Select a file to upload.", "color:red", "TOP");
				return;
			}
			//imageMap = new HashMap<String, Object>();
			//imageMap.put("Media", media);
			//imageMap.put("Div", imagesDivId);
			
			String imagName = media.getName();
			
			logger.debug("upload Image Name is ::" +imagName);
			
			String ext =  FilenameUtils.getExtension(media.getName());
			
			
			if(ext.equals("zip")) {
				isZipFile = true;
				Session session = Sessions.getCurrent();
				String newGalName = (String)session.getAttribute("selGalleryName"); 
				
				if(newGalName == null){ return; }
				
				String res = uploadZIPFile(media,newGalName,imagesDivId);
				if(res != null) {
					if(res .equals("Uploading zip file failed")) MessageUtil.setMessage(res,"color:red;","TOP");
					
					else if((res .equals("zip file uploaded successfully")))MessageUtil.setMessage(res,"color:blue;","TOP");
					
					else MessageUtil.setMessage(res,"color:blue;","TOP");
				    return;
				}	
			}else if(ext.equalsIgnoreCase("jpeg") || ext.equalsIgnoreCase("jpg") || 
					 ext.equalsIgnoreCase("gif") || ext.equalsIgnoreCase("png")|| 
					 								ext.equalsIgnoreCase("bmp")) {
				
				/*if(!Utility.validateUploadFilName(imagName) || imagName.contains("'")) {
					
					Textbox oldImgNameTbox = (Textbox)newGallerySubWinId.getFellowIfAny("oldimgNameTxtboxId");

					String fileNameWithoutExtn = imagName.substring(0, imagName.lastIndexOf('.'));
					Label oldImgNameExtLbl =(Label)newGallerySubWinId.getFellowIfAny("oldExtLblId");
					Label newImgNameExtLbl =(Label)newGallerySubWinId.getFellowIfAny("newExtLblId");
					
					oldImgNameExtLbl.setValue("."+ext);
					newImgNameExtLbl.setValue("."+ext);
					logger.debug(imagName +" ImageName  contains speacial charecters" +"fileNameWithOutExt ::"+fileNameWithoutExtn);
					
					oldImgNameTbox.setValue(fileNameWithoutExtn);
					
					changeImgNameflag = true;
					
					
					newGallerySubWinId$changeImgNameTxtboxId.setValue("");
					newGallerySubWinId$folderErrorMsgLblId.setValue("");
					newGallerySubWinId$chngImgNameErrorMsgLblId.setValue("");
					
					newGallerySubWinId.setTitle("Change Image Name");
					newGallerySubWinId$createFolderDivId.setVisible(false);
					newGallerySubWinId$changeImgNameDivId.setVisible(true);
					newGallerySubWinId.setVisible(true);
					newGallerySubWinId.doHighlighted();
					
					
					
					//logger.info("returning.............................................");
				}*/
				double fileSize = Math.ceil((media.getByteData().length))/1024; //app-3657
				if(fileSize>10240){
					numberOfUploadedImagesFailed++;
					MessageUtil.setMessage(media.getName()+" cannot be uploaded.\n Reason: Size should not exceed 10 MB.","color:red", "TOP");
				    					
				}
				else 
					uploadImage(media);
				
			}
			else {
				MessageUtil.setMessage("Upload images of format .jpeg, .jpg, .gif, .png, .bmp, or zip files only.", "color:red", "TOP");
				return;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
	}// upload()
	
	public void uploadImage(Media media) throws Exception {
		
		//logger.info("inside uploadImage() status newGallerySubWinId.isVisible() =10 "+newGallerySubWinId.isVisible());
		MessageUtil.clearMessage();
		String imagName = media.getName();
		
		logger.info("upload Image Name is in uploadImage ::" +imagName+"::"+changeImgNameflag);
		
		if(changeImgNameflag) {
			newGallerySubWinId.setVisible(false);
			//logger.info("inside uploadImage() in spl case status newGallerySubWinId.isVisible() = "+newGallerySubWinId.isVisible());
			Textbox	changeImgNameTbox = (Textbox)newGallerySubWinId.getFellowIfAny("changeImgNameTxtboxId");
			
			/*if(changeImgNameTbox.getValue().trim().isEmpty()){*/
			if(changeImgNameTbox.getValue().trim().isEmpty() && newGallerySubWinId.isVisible()){
				//MessageUtil.setMessage("Upload of image failed.","color:red","TOP");
				MessageUtil.setMessage("Upload of image "+imagName+" failed.","color:red","TOP");//2.3.11 asana
				changeImgNameflag = false;
				return;
			}
			String ext =  FilenameUtils.getExtension(media.getName());
			imagName = changeImgNameTbox.getValue()+"."+ext;
			changeImgNameTbox.setValue("");
		}
		logger.debug("After changing the Image name is ::>>>>> ::"+imagName);
		changeImgNameflag = false;
		
		String ext =  FilenameUtils.getExtension(media.getName());
		
		//logger.info("ImageName:"+imagName);
		//Changes 2.5.2.13 Start
		
		//imagName = imagName.replaceAll(" +", "_"); Commented
		
		
		//Changes 2.5.2.13 End
		Session session = Sessions.getCurrent();
		String newGalName = (String)session.getAttribute("selGalleryName"); 
		if(newGalName == null) {
			return;
		}
		/*if(ext != null && ext.equalsIgnoreCase("jpeg") ||ext.equalsIgnoreCase("jpg") || 
						  ext.equalsIgnoreCase("gif") || ext.equalsIgnoreCase("png") ||
						  								 ext.equalsIgnoreCase("bmp"))  {*/
		if(ext != null && ext.equalsIgnoreCase("jpeg") ||ext.equalsIgnoreCase("jpg") || 
				  ext.equalsIgnoreCase("gif") || ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("bmp"))  {
			try{
				String path = usersParentDirectory + "/" +  userName + "/"+ "Gallery" + "/" + newGalName;
				File file = new File(path);
				
				if(!file.exists()) {
					file.mkdirs();
				}
				
				String imgPath = path +"/" +imagName;
				File file1  = new File(imgPath);
				//logger.debug("imageName is already exist >>> ::"+file.exists());
				if(file1.exists()) {
					//MessageUtil.setMessage("Image already exists.","color:blue","TOP");
					MessageUtil.setMessage("Image "+imagName+" already exists.","color:blue","TOP");//2.3.11 asana
					imageStatusMap.put(media, true);
					Integer currNum = (Integer)multipleUploadWinId.getAttribute(CURRNUM);
					multipleUploadWinId.setAttribute(CURRNUM, (currNum == null ? 1 : currNum+1));
					dispFinalWindow();
					return;
				}
				
				byte[] fi = media.getByteData();
				BufferedInputStream in = new BufferedInputStream (new ByteArrayInputStream (fi)); 
				FileOutputStream out = new FileOutputStream (new File(file.getPath()+"/"+imagName));
				//Copy the contents of the file to the output stream
				byte[] buf = new byte[1024];
				int count = 0;
				while ((count = in.read(buf)) >= 0){
					out.write(buf, 0, count);
				}
				in.close();
				out.close();
				/*
				 * The following block is for subscription plan based restrictions
				long img_memory = FileUtils.sizeOfDirectory(file);
				if(img_memory > available_memory){
					onError(file,"Image could not be uploaded as it is exceeding your memory limit");
					return;
				}
				*/
				File[] imgList = new File[1];
				File newFile = new File(path+"/"+imagName);
				logger.info("======>ImageName3"+imagName);
				Components.removeAllChildren(imagesDivId);

				if(file.exists()){
					//imgList[0] = newFile;
					imgList = file.listFiles();
					logger.debug("Image list size : " + imgList.length);
					logger.info("imagName4======>"+imgList);
					createImageDiv(imgList,newGalName,imagesDivId);
				}
				
				setTotalImageSizeToSelectedlistItem(imgList.length);
					//MessageUtil.setMessage("Image uploaded successfully.","color:blue;","TOP");
				logger.info(">>>>>>>>>>>>>>>>>>>>>>>>"+imagName+" uploaded successfully.");
				numberOfUploadedImages++;
				imageStatusMap.put(media, true);
				Integer currNum = (Integer)multipleUploadWinId.getAttribute(CURRNUM);
				multipleUploadWinId.setAttribute(CURRNUM, (currNum == null ? 1 : currNum+1));
				dispFinalWindow();
				if(userActivitiesDaoForDML != null) {
            		userActivitiesDaoForDML.addToActivityList(ActivityEnum.GALLRY_UPLOAD_IMG_p1galleryName, GetUser.getLoginUserObj(), newGalName);
				}
			}catch (Exception e) {
				logger.error("Exception :: error occured while Uploading the Image");
				logger.error("Exception ::", e);
			}
		}else 
			//MessageUtil.setMessage("Upload only images or zip files.","color:red","TOP");
			MessageUtil.setMessage("Upload images of format .jpeg, .jpg, .gif, .png, or zip files only.", "color:red", "TOP");
			
		//imageMap.clear();
		
	}
	
	private void dispFinalWindow() {
		
		Integer currNum = (Integer)multipleUploadWinId.getAttribute(CURRNUM);
		
		//logger.info("curr num===================="+currNum);
		if((currNum != null && currNum == imageStatusMap.size()) || isZipFile) {
			
			multipleUploadWinId$successMsgLblId.setValue(numberOfUploadedImages+" image(s) uploaded successfully, and "+numberOfUploadedImagesFailed+" image(s) failed to upload");
			multipleUploadWinId.setVisible(true);
			multipleUploadWinId.setPosition("center");
			multipleUploadWinId.doHighlighted();
			
		}
	}
	
	public void onClick$okBtnId$multipleUploadWinId(){
		//logger.info(">>>>>>>>>>>>>>>>>>>>>>inside ok button");
		multipleUploadWinId.setVisible(false);
	}
	public void onUpload$uploadMoreBtnId$multipleUploadWinId(UploadEvent uploadEvent){
		multipleUploadWinId.setVisible(false);
		onUpload$uploadImgBtnId(uploadEvent);
		//UploadEvent uploadevent = new U
		//onUpload$uploadImgBtnId();
	}
	
	private int count=0;
	public void createImageDiv(File[] imgList, String galName,Div imagesDivId) {
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
//			Toolbarbutton previewTlbBtn = null;
//			Toolbarbutton delBtn = null;
			String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
			int width = Integer.parseInt(PropertyUtil.getPropertyValue("imageThumbWidth"));
			int height = Integer.parseInt(PropertyUtil.getPropertyValue("imageThumbHeight"));
			
			//logger.debug("number of images are : " + imgList.length);
			File[] spimgList = imgList;
			
			for(int i=0;i<spimgList.length;i++){
				if(spimgList[i].isFile()) {
					//logger.debug(imgList[i]+" is a file -----------");
					imgName = spimgList[i].getName();
					logger.debug("image Name : " + imgName);
					if(!Utility.validateUploadFilName(imgName) || imgName.contains("'")) {
						spimgList[i].isHidden();
						
				} else {
					imgPath = "/UserData/"  +userName + "/Gallery/" + galName + "/" + imgName;
					logger.debug("Image path is : " + imgPath);
					
					
					imageOuterVbox = new Vbox();
					
					imageOuterVbox.setWidgetListener("onMouseOver", "onImgMouseOver(this)");
					imageOuterVbox.setWidgetListener("onMouseOut", "onImgMouseOut(this)");
					//imageOuterVbox.addEventListener("onMouseOver", this);
					//imageOuterVbox.addEventListener("onMouseOut", this);
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
					
					/*if(imgWidth>imgHeight)
						if(imgWidth > width)
							img.setWidth(width + "px");
						else
							img.setWidth(imgWidth + "px");
					else
						if(imgHeight > height)
							img.setHeight(height + "px");
						else
							img.setHeight(imgHeight + "px");*/
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
					//imageDetailsVbox.setAlign("center");
					Label label=new Label(width+"X"+height);
					label.setStyle("font-size: 5px:");
					Label tempLabel = new Label((imgList[i].length()/1024)+" kb"); //app-3657
					label.setParent(imageDetailsVbox);
					tempLabel.setParent(imageDetailsVbox);
					imageDetailsVbox.setParent(imageOptionsHbox);
					
					String src = appUrl + "UserData/" + userName + "/Gallery/" + galName + "/" + imgName;
					logger.info("imageName=====>"+imgName);
					previewTlbBtn = new Image("/img/theme/preview_icon.png");
					previewTlbBtn.setStyle("cursor:pointer;cursor:hand;margin:15px 0px 0px 15px");
//					previewTlbBtn = new Toolbarbutton("");
//					previewTlbBtn.setImage("/img/theme/preview_icon.png");
					previewTlbBtn.setTooltiptext("Preview");
					previewTlbBtn.setWidgetListener("onClick", "previewWin('"+src+"')");
					
				
					//previewTlbBtn.setWidgetAttribute("onClick", "previewWin('"+src+"')");
					
					logger.info("=====>"+src);
					previewTlbBtn.setVisible(false);
					previewTlbBtn.setParent(imageOptionsHbox);
					previewTlbBtn.setId("zoom_"+count);
					
					
					
					delImg = new Image("/img/icons/delete.png");
					
//					delBtn.setImage("/img/icons/delete.png");
					delImg.setStyle("cursor:pointer;cursor:hand;margin-top:15px");
					delImg.setTooltiptext("Delete");
					
					//Set Attribute of FolderName  
					String imageFolderpath = usersParentDirectory + "/" + userName + "/Gallery/" + galName;
					delImg.setAttribute("ImageFolder", imageFolderpath);
					
					//Set Attribute of Actual image Path
					absPath = usersParentDirectory + "/" + userName + "/Gallery/" + galName + "/" + imgName;
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
	
	public int createImageDivBySearch(File[] imgList, String galName,Div imagesDivId,String searchStr) {
		int imgCount = 0;
		int finalImgCount = 0;
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
//			Toolbarbutton previewTlbBtn = null;
//			Toolbarbutton delBtn = null;
			String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
			int width = Integer.parseInt(PropertyUtil.getPropertyValue("imageThumbWidth"));
			int height = Integer.parseInt(PropertyUtil.getPropertyValue("imageThumbHeight"));
			
			//logger.debug("number of images are : " + imgList.length);
			
			
			for(int i=0;i<imgList.length;i++){
				if(imgList[i].isFile()) {
					//logger.debug(imgList[i]+" is a file -----------");
					imgName = imgList[i].getName();
					logger.debug("image Name : " + imgName);
					if(!Utility.validateUploadFilName(imgName) || imgName.contains("'")) {
						imgList[i].isHidden();
					}
					
					else if(imgName.toLowerCase().contains(searchStr.toLowerCase())){
						imgCount=imgCount+1;
					logger.debug("image Name : " + imgName);
					imgPath = "/UserData/"  +userName + "/Gallery/" + galName + "/" + imgName;
					logger.debug("Image path is : " + imgPath);
					
					
					
					imageOuterVbox = new Vbox();
					
					imageOuterVbox.setWidgetListener("onMouseOver", "onImgMouseOver(this)");
					imageOuterVbox.setWidgetListener("onMouseOut", "onImgMouseOut(this)");
					//imageOuterVbox.addEventListener("onMouseOver", this);
					//imageOuterVbox.addEventListener("onMouseOut", this);
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
					
					/*if(imgWidth>imgHeight)
						if(imgWidth > width)
							img.setWidth(width + "px");
						else
							img.setWidth(imgWidth + "px");
					else
						if(imgHeight > height)
							img.setHeight(height + "px");
						else
							img.setHeight(imgHeight + "px");*/
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
					//imageDetailsVbox.setAlign("center");
					Label label=new Label(width+"X"+height);
					label.setStyle("font-size: 5px:");
					Label tempLabel = new Label((imgList[i].length()/1024)+" kb"); //app-3657
					label.setParent(imageDetailsVbox);
					tempLabel.setParent(imageDetailsVbox);
					imageDetailsVbox.setParent(imageOptionsHbox);
					
					String src = appUrl + "UserData/" + userName + "/Gallery/" + galName + "/" + imgName;
					logger.info("imgageName=====>"+imgName);
					previewTlbBtn = new Image("/img/theme/preview_icon.png");
					previewTlbBtn.setStyle("cursor:pointer;cursor:hand;margin:15px 0px 0px 15px");
//					previewTlbBtn = new Toolbarbutton("");
//					previewTlbBtn.setImage("/img/theme/preview_icon.png");
					previewTlbBtn.setTooltiptext("Preview");
					previewTlbBtn.setWidgetListener("onClick", "previewWin('"+src+"')");
					
				
					//previewTlbBtn.setWidgetAttribute("onClick", "previewWin('"+src+"')");
					
					logger.info("=====>"+src);
					previewTlbBtn.setVisible(false);
					previewTlbBtn.setParent(imageOptionsHbox);
					previewTlbBtn.setId("zoom_"+count);
					
					
					
					delImg = new Image("/img/icons/delete.png");
					
//					delBtn.setImage("/img/icons/delete.png");
					delImg.setStyle("cursor:pointer;cursor:hand;margin-top:15px");
					delImg.setTooltiptext("Delete");
					
					//Set Attribute of FolderName  
					String imageFolderpath = usersParentDirectory + "/" + userName + "/Gallery/" + galName;
					delImg.setAttribute("ImageFolder", imageFolderpath);
					
					//Set Attribute of Actual image Path
					absPath = usersParentDirectory + "/" + userName + "/Gallery/" + galName + "/" + imgName;
					delImg.setAttribute("path", absPath);
					
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
				else continue;
				}else {
					logger.debug("not a file " + imgList[i].getPath());
				}
			}// for imgList
			}catch (Exception e) {
				logger.error("** Exception in uploadZIPFile **",e);
				return 0;
			}
		return imgCount;
}// createImageDiv()

	public String uploadZIPFile(Object media,String galName,Div imagesDivId){
		try{
			logger.debug("just entered");
			Media m = (Media)media;
			String path = usersParentDirectory +"/" +  userName +"/" + "Gallery/" + galName;
			String srcpath = usersParentDirectory  + "/" +  userName +"/"+"Galleries.zip";
			
			File destFile = new File(path);
			File srcFile = new File(srcpath);
			
			BufferedInputStream in = new BufferedInputStream(m.getStreamData());
			FileOutputStream out = new FileOutputStream (srcFile);
			
//			logger.debug("Copying the contents of the file to the output stream");
			//Copy the contents of the file to the output stream
			byte[] buf = new byte[1024];
			int count = 0;
			while ((count = in.read(buf)) >= 0) {
				out.write(buf, 0, count);
			}
			in.close();
			out.close();
			logger.debug("Unzipping the file");
			
			//fetching the images from the zip file
			List fileList = unzipFile(srcFile,destFile);
			
			if(fileList.size() == 0 ) {
				//MessageUtil.setMessage("There are no images in the zip file or unable to upload the images.", "color:red", "TOP");
				MessageUtil.setMessage("There are no images in the zip file or unable to upload the images, or the zip file may contain invalid formats of files for images.", "color:red", "TOP");
				return null;
			}
			File[] imgList = new File[fileList.size()];
			for(int i=0;i<fileList.size();i++) {
				imgList[i] = (File)fileList.get(i);
			}
					
			logger.debug("Unzip completed");
			createImageDiv(imgList,galName,imagesDivId);
			numberOfUploadedImages = fileList.size();	
            try{
				srcFile.delete();
				logger.info("Deleted the ZIP file : " + srcFile);
			}catch(Exception e){
				logger.error("** Unable to delete the ZIP file : " + srcFile+" **");
			}
			logger.debug("Exiting");
			return "zip file uploaded successfully";
			
		}
		catch(Exception e){
				logger.error("** Exception in uploadZIPFile ",e);
				return "Uploading zip file failed";
		}
	}// upLoadZipFile()
	
	public List unzipFile (File source, File directory)	{	 
		try {	
			logger.debug(" just entered unzipFile ");
			CRC32 crc32 = new CRC32();			
			int n;
			byte[] buffer = new byte [1024];			
			directory.mkdirs();			
			ZipInputStream zipIn = new ZipInputStream (new FileInputStream (source));			
			ZipEntry zipEntry = null;		
			logger.debug("file list is being created ");
			List fileList = new ArrayList();
			String imagName = null;
			
			while ((zipEntry = zipIn.getNextEntry()) != null) {	
				//Changes start 2.5.2.13
				if(zipEntry.isDirectory())
					continue;
				//Changes end 2.5.2.13
				imagName = zipEntry.getName();
//				logger.debug("***********Image Path Name:"+imagName);
				String str[] = imagName.split("/");
				imagName = str[str.length-1];
//				logger.debug("***********Image Name **********:"+ imagName);
		        
				imagName = validateFileNameUnderZipFile(imagName) ;
				
				if(imagName == null) continue;
				double imageSize = Math.ceil(zipEntry.getSize())/1024; //app-3657
				File uncompressedFile = new File(directory.getAbsolutePath()+File.separator+imagName);	
				logger.info(">>>>>>>>>>>>>>>>---"+uncompressedFile.getName());
				
				if(!Utility.validateUploadFilName(uncompressedFile.getName()) || uncompressedFile.getName().contains("'")){
					
					logger.info(">>>>>>>>>>>>>>>>---2"+uncompressedFile.getName());
					numberOfUploadedImagesFailed++;
					logger.info(uncompressedFile.getName());
					//MessageUtil.setMessage("Upload of image "+uncompressedFile.getName()+" failed, upload images of format .jpeg, .jpg, .gif, .png, .bmp, or zip only.", "color:red", "TOP");
					MessageUtil.setMessage("Upload of image "+uncompressedFile.getName()+" failed,\n upload images of format \n .jpeg, .jpg, .gif, .png, .bmp, or zip only.", "color:red", "TOP");
					continue;
				}
				else if(imageSize>10240){
					numberOfUploadedImagesFailed++;
					MessageUtil.setMessage(uncompressedFile.getName()+" cannot be uploaded.\n Reason: Size should not exceed 10 MB." , "color:red", "TOP");
					continue;
				}else if(!(uncompressedFile.exists())) {
					
					if (!zipEntry.isDirectory()) {
						
						FileOutputStream fout = new FileOutputStream(uncompressedFile);					
						crc32.reset();					
						while ((n = zipIn.read(buffer)) > -1) {
							crc32.update(buffer, 0, n);						
							fout.write(buffer, 0, n);					
						}	
						fout.close();				
					}	
					if (crc32.getValue() == zipEntry.getCrc()) {	
						
						BufferedImage bf = ImageIO.read(uncompressedFile);
						if(bf!=null){
							fileList.add (uncompressedFile);	
						}
						else{
							logger.info("This-"+zipEntry.getName()+"- file is deleted from zipped image file-"+uncompressedFile.delete());
						}
					}	
				} else if(uncompressedFile.exists()){
					//existedImgname = 
					MessageUtil.setMessage("Image "+uncompressedFile.getName()+" already exists.","color:blue","TOP");
					logger.debug(imagName+" already exist");
				} 
			} // while
			logger.debug("---Extraction completed--");
			setTotalImageSizeToSelectedlistItem(fileList.size());
			zipIn.close();			
			return fileList;
		}
		catch (Exception e) {
			logger.error("** Exception :"+e.getMessage()+" **");
			return null;
		}	
	}// unZipFile()
	
		
	private String validateFileNameUnderZipFile(String imageName) {
		//jpg|png|gif|bmp|jpeg
		
		//String[] imgName = imageName.split("\\.");
		/*if(!(imageName.contains("jpg") || imageName.contains("jpg") || 
				imageName.contains("jpg") || imageName.contains("jpg"))) {
			return null;
		}*/
		imageName = imageName.replaceAll(" +", "_");
		imageName = imageName.replaceAll("'", "_");
		imageName = imageName.replaceAll(",", "_");
		return imageName;
	}
	
	public void getAllImages(Div imagesDivId , Div folderOpnDivId) {
	
		logger.debug("---Just Enterd Here---");
		MessageUtil.clearMessage();
		
		
		if(galleryListLbId.getSelectedItem()!=null) {
			
			galleryListLbId.getSelectedItem().setSelected(false); // set the folderName is selected false 
		}
		try {
			
			 if(Messagebox.show("It may take some time to load all the images.", "Information",
					 Messagebox.OK | Messagebox.CANCEL, Messagebox.INFORMATION) != Messagebox.OK) {
				 return;
			 } // if
			
			 folderOpnDivId.setVisible(false);
			
			String userGallery = usersParentDirectory + "/" +userName + "/Gallery";
			File usrGalFile = new File(userGallery);
			File[] galArray = usrGalFile.listFiles();
			logger.debug("no of images---->"+galArray.length);
			Components.removeAllChildren(imagesDivId);
			int imgList1= 0;  
			
			for(int i=0;i<galArray.length;i++) {
				
				File galFile = galArray[i];
				File[] imgList = galFile.listFiles();
				
				if(galFile.isDirectory()) {
					createImageDiv(imgList,galFile.getName(),imagesDivId);
				} // if
				imgList1=imgList1 + imgList.length;
				
			}//for galArray
			
			
			//selGalleryCenterId.setTitle(" ViewAllImages ("+imgList1 + "images" +")");
			Session session = Sessions.getCurrent();
			session.removeAttribute("selGalleryName");
			
		}catch (Exception e) {
			logger.error("problem while getting all images",e);
		}
		
	}// getAllImages()

	
	@Override
	public void onEvent(Event event) throws Exception {
		try{
			super.onEvent(event);
			Object eventObj = event.getTarget();
			
			if(eventObj instanceof Image) {  // Toolbarbutton
				try{
					/*if(Messagebox.show("Do you want to delete the image?", "Confirmation", 
						Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)!= Messagebox.OK) {
					return;
					}*/ 
									
					
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
							vbox.setVisible(false);
							
							MessageUtil.setMessage("Image deleted successfully.","color:blue;","TOP");
							
							if(userActivitiesDaoForDML != null) {
			            		userActivitiesDaoForDML.addToActivityList(ActivityEnum.GALLRY_DEL_IMG_p1imageName, GetUser.getLoginUserObj(), delFile.getName());
							}
							
							if(galleryListLbId.getSelectedItem() == null) return;
							
							String folderNamePath = (String)tbImg.getAttribute("ImageFolder");
							//FOR TITLE
							String galName = (String)galleryListLbId.getSelectedItem().getValue();
							//logger.info("::GALNAME >>::"+galName +"FOLDERPATH is >>::"+folderNamePath);
							File galFile = new File(folderNamePath);
							logger.debug("IS DIR >>::"+galFile.isDirectory());
							if(galFile.isDirectory()){
								
								File[] imgList = galFile.listFiles();
								//selGalleryCenterId.setTitle(galName+ " ("+imgList.length+ "images"+")");  
								//selGalleryCenterId.setStyle("glImageListStyle");
								setTotalImageSizeToSelectedlistItem(imgList.length);
							}
						
							
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
	
	private Textbox newGallerySubWinId$changeImgNameTxtboxId;
	private Label newGallerySubWinId$chngImgNameErrorMsgLblId;
	
	
	
	
	
	
	
	public void onClick$newGalsubWinokBtnId$newGallerySubWinId() {
		try {
			String changImgName = newGallerySubWinId$changeImgNameTxtboxId.getValue();
			newGallerySubWinId$chngImgNameErrorMsgLblId.setValue("");
			if(changImgName.trim().equals("")) {
				newGallerySubWinId$chngImgNameErrorMsgLblId.setValue("Provide image name without any special characters");
				return;
			}
			//Changes 2.5.2.13 Start
			else if(Utility.validateUploadFilNameWithoutExtension(changImgName) == false) {
				newGallerySubWinId$chngImgNameErrorMsgLblId.setValue("Image name contains special characters");
				return;
			}
			//Changes 2.5.2.13 End 
			Media media = (Media)newGallerySubWinId.getAttribute(IMAGE_MEDIA);
			uploadImage(media);
			newGallerySubWinId.setVisible(false);
			uploadMultipleImages();
			
			
		} catch (Exception e) {
			logger.error("problem occured from the uploadImage method ", e);
		}
	} // onClick$newGalsubWinokBtnId
	
	
	
	public void onSelect$galleryListLbId() {
		try {
			getImages(imagesDivId,folderOpnDivId);
			
		} catch (Exception e) {
			logger.error("problem occured from the getImages method ", e);
		}
	} //onSelect$galleryListLbId
	
	public void onClick$viewAllImageTbarBtnId() {
		try {
			getAllImages(imagesDivId,folderOpnDivId);
			
			//remove listSize from all the listitem(folderName),if any
			List chaildLists = galleryListLbId.getChildren();
			for (Object object : chaildLists) {
				
				if(!(object instanceof Listitem )) continue;
				Listitem tempLisitItem = (Listitem)object;
				String listitemName = tempLisitItem.getLabel();
				if(listitemName.contains("(")){
					listitemName = listitemName.substring(0, listitemName.indexOf("("));
				}
				tempLisitItem.setLabel(listitemName);
			}
		
		} catch (Exception e) {
			logger.error("Error occured from the getAllImages method ", e);
		}
	} // onClick$viewAllImageTbarBtnId
	
	/*public void onUpload$uploadImgFileId(UploadEvent event){
		try {
			logger.debug("onUpload method calling...");
			upload(event.getMedia(),imagesDivId);
		} catch (Exception e) {
			logger.error("Error occured from the upload  method ",e);
		}
	} // onUpload$uploadImgFileId
*/	
	
	public void onUpload$uploadImgBtnId(UploadEvent uploadEvent) {
		
		//logger.info("inside upload event--------------------------------->>>>>>>>>>>>>>>>>>>>>>>>>>>"); 
		//logger.info(uploadEvent.getName()+"  "+uploadEvent.getTarget()+" "+uploadEvent.getMedias()); 
		numberOfUploadedImages=0;
		numberOfUploadedImagesFailed=0;
		isZipFile = false;
		if(uploadEvent.getMedias() == null){
			 MessageUtil.setMessage("You did not select any images. Please select some.", "color:red");
			 return;
		 }
		
		try {
			if(galleryListLbId.getItemCount() == 0) {
				
				MessageUtil.setMessage("Create new folder.", "color:red","top");
				return;
			}
			
			else if(galleryListLbId.getSelectedIndex() == -1) {//"Provide the name for new folder", "color:red", "TOP"
				MessageUtil.setMessage("Choose the folder.", "color:red");
				return;
			}
			
			/*logger.debug("just click upload Image button");
			Media media = (Media)Fileupload.get();
//		if(media instanceof org.zkoss.image.Image) {
				
				logger.debug("========>"+media);
				if(media == null){
					MessageUtil.setMessage("Please select the image.", "color:red");
					return;
				}*/
			//newGallerySubWinId.setVisible(false);
			multipleUploadWinId.removeAttribute(CURRNUM);
			Media[] imageArray = uploadEvent.getMedias();
			
			imageStatusMap = new HashMap<Media, Boolean>();
			for(Media media:imageArray){
				
				logger.info("1--------------------------------->>>>>>>>>>>>>>>>>>>>>>>>>>>name ==="+media.getName());
				imageStatusMap.put(media, false);
			}
			
			uploadMultipleImages();	
			dispFinalWindow();
			
			
//		}
		} catch (Exception e) {
			logger.error("unable to upload the images"+e.getMessage());
		}
	}
	int i = 1;
	private void uploadMultipleImages() {
		//logger.info("uploadMultipleImages--------------------------------->>>>>>>>>>>>>>>>>>>>>>>>>>>1" +i++); 
		
		Integer currNum = (Integer)multipleUploadWinId.getAttribute(CURRNUM);
		//logger.info("uploadMultipleImages--------------------------------->>>>>>>>>>>>>>>>>>>>>>>>>>>2"+currNum); 
	
		
		
		 currNum = (Integer)multipleUploadWinId.getAttribute(CURRNUM);
		
		
		//logger.info("uploadMultipleImages--------------------------------->>>>>>>>>>>>>>>>>>>>>>>>>>>3"+((Integer)multipleUploadWinId.getAttribute(CURRNUM)).toString()); 
		for (Media eachMedia : imageStatusMap.keySet()) {
			//each meach media is considered conti
			
			logger.info("uploadMultipleImages--------------------------------->>>>>>>>>>>>>>>>>>>>>>>>>>>4"+(imageStatusMap.get(eachMedia)));
			
			if(imageStatusMap.get(eachMedia)) continue;
			
			
			logger.debug("===============================5"+eachMedia.getName());
			boolean isAnySpecChar = isAnySpecialChar(eachMedia);
			//logger.debug("isAnySpecialChar ===============================7"+isAnySpecChar);
			if(isAnySpecChar){
				try {
					double fileSize = Math.ceil((eachMedia.getByteData().length))/1024; //app-3657
					if(fileSize>10240){
						MessageUtil.setMessage(eachMedia.getName()+" cannot be uploaded.\n Reason: Size should not exceed 10 MB." , "color:red", "TOP");
					    continue;
					}
					displayChangeImageNameWindow(eachMedia);
					break;
				}catch (Exception e) {
					displayChangeImageNameWindow(eachMedia);
					break;
				}
			}
			else {
				//Changes 2.5.2.13 start
				changeImgNameflag =false;
				//Changes 2.5.2.13 end
				upload(eachMedia, imagesDivId);
			}
			//logger.info("curr num===================="+currNum);
			
		}
	}
	
	public void onClick$folderDeleteImgId() {
		try {
			deleteGallery(imagesDivId,folderOpnDivId);
		} catch (Exception e) {
			logger.error("Error occured from the deleteGallery  method ", e);
		}
	}
	
	public void onClick$clearAllImgsId() {
		try {
			clearImages(imagesDivId,galleryListLbId);
		} catch (Exception e) {
			logger.error("Error occured from the clearImages  method ", e);
		}
	}

	private Div newGallerySubWinId$createFolderDivId ,newGallerySubWinId$changeImgNameDivId;
	
	private Label newGallerySubWinId$folderErrorMsgLblId;
	//create New Folder
	public void onClick$createFoldTBarBtnId() {
		newGallerySubWinId$newFolderTbId.setValue("");
		newGallerySubWinId$folderErrorMsgLblId.setValue(""); //clear the errormsg from newGallerySubWinId  if any
		
		newGallerySubWinId.setTitle("Folder Creation");
		newGallerySubWinId$changeImgNameDivId.setVisible(false);
        newGallerySubWinId$createFolderDivId.setVisible(true);
		newGallerySubWinId.doHighlighted();
	} // onClick$createFoldTBarBtnId
	
	public void onClick$okBtnId$newGallerySubWinId() {
		MessageUtil.clearMessage();
		try {
			String folderName = newGallerySubWinId$newFolderTbId.getValue().trim();
			if(folderName.trim().isEmpty() || folderName == null) {
				newGallerySubWinId$folderErrorMsgLblId.setValue("Please provide the Folder Name");
				//MessageUtil.setMessage("Provide name for the new folder.", "color:red", "TOP");
				return;
			}
			else if(!Utility.validateName(folderName)){
				newGallerySubWinId$folderErrorMsgLblId.setValue("Provide a valid name without any special characters");
				//MessageUtil.setMessage("Provide a valid name without any special characters.", "color:red", "TOP");
				return;
			}
			
			//replace (_) instead of whitespace
			folderName = folderName.replaceAll(" +", "_");
			
			File newFile = new File(usersParentDirectory + "/" + userName + "/Gallery/" + folderName);
			
			if(!newFile.exists()){
				try{
//					logger.info("File existed and make di"+newFile.mkdir());
					newFile.mkdir();
					(new Listitem(folderName,folderName)).setParent(galleryListLbId);
					
					newGallerySubWinId.setVisible(false);
					folderOpnDivId.setVisible(true);
					
					MessageUtil.setMessage("New folder is created with the name : '" + folderName + "'", "color:blue", "TOP");
					
					if(userActivitiesDaoForDML != null) {
	            		userActivitiesDaoForDML.addToActivityList(ActivityEnum.GALLRY_CRE_FOLDER_p1folderName, GetUser.getLoginUserObj(),folderName);
					}
				}catch (Exception e) {
					newGallerySubWinId$folderErrorMsgLblId.setValue("Problem in creating the new folder");
					return;
//					MessageUtil.setMessage("Problem experienced while creating the new folder.", "color:red", "TOP");
				}
			}else{
				newGallerySubWinId$folderErrorMsgLblId.setValue("Folder name already exist");
				return;
//				MessageUtil.setMessage("Folder name already exists.", "color:red", "TOP");
			}
			
		} catch (Exception e) {
			logger.error("problem occured from the createNewFolder method ", e);
		}
	} // onClick$okBtnId$newGallerySubWinId
	
	private void setTotalImageSizeToSelectedlistItem(int size)  {
		List chaildLists = galleryListLbId.getChildren();
		
		for (Object object : chaildLists) {
			
			if(!(object instanceof Listitem )) continue;
			Listitem tempLisitItem = (Listitem)object;
			String listitemName = tempLisitItem.getLabel();
			if(listitemName.contains("(")){
				listitemName = listitemName.substring(0, listitemName.indexOf("("));
			}
			
			tempLisitItem.setLabel(listitemName);
			
		}
		String galName = (String)galleryListLbId.getSelectedItem().getLabel();
		logger.debug("Number of images in the gallery '" + galName + "' : " + size);
		galleryListLbId.getSelectedItem().setLabel(galName+ " ("+size+ "images"+")"); 
	}
	
	private boolean isAnySpecialChar(Media media){
		//logger.debug("isAnySpecialChar ===============================6");
		String imagName = media.getName();
		logger.info("+++++++++++++++++"+imagName);
		try {
			if(!Utility.validateUploadFilName(imagName) || imagName.contains("'")) {
				
					return true;
			}
			else return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
			
		}
			
		
	} //isAnySpecialChar()
	
	private void displayChangeImageNameWindow(Media media){
		
		
		
		
		String imagName = media.getName();
		Textbox oldImgNameTbox = (Textbox)newGallerySubWinId.getFellowIfAny("oldimgNameTxtboxId");

		
		String ext =  FilenameUtils.getExtension(media.getName());
		
		if((!ext.equalsIgnoreCase("jpeg") && !ext.equalsIgnoreCase("jpg") &&
				 !ext.equalsIgnoreCase("gif") && !ext.equalsIgnoreCase("png") &&
					!ext.equalsIgnoreCase("bmp") && !ext.equalsIgnoreCase("zip"))){
			//MessageUtil.setMessage("Upload images of format .jpeg, .jpg, .gif, .png, .bmp, or zip files only.", "color:red", "TOP");
			//MessageUtil.setMessage("Upload of image "+imagName+" failed, upload images of format .jpeg, .jpg, .gif, .png, .bmp, or zip only.", "color:red", "TOP");
			MessageUtil.setMessage("Upload of image "+imagName+" failed,\n upload images of format \n .jpeg, .jpg, .gif, .png, .bmp, or zip only.", "color:red", "TOP");
			numberOfUploadedImagesFailed++;
			imageStatusMap.put(media, true);
			Integer currNum = (Integer)multipleUploadWinId.getAttribute(CURRNUM);
			multipleUploadWinId.setAttribute(CURRNUM, (currNum == null ? 1 : currNum+1));
			dispFinalWindow();
			uploadMultipleImages();
			return;
		}
		String fileNameWithoutExtn = imagName.substring(0, imagName.lastIndexOf('.'));
		Label oldImgNameExtLbl =(Label)newGallerySubWinId.getFellowIfAny("oldExtLblId");
		Label newImgNameExtLbl =(Label)newGallerySubWinId.getFellowIfAny("newExtLblId");
		
		oldImgNameExtLbl.setValue("."+ext);
		newImgNameExtLbl.setValue("."+ext);
		logger.debug(imagName +" ImageName  contains speacial characters" +"fileNameWithOutExt ::"+fileNameWithoutExtn);
		
		oldImgNameTbox.setValue(fileNameWithoutExtn);
		
		changeImgNameflag = true;
		
		newGallerySubWinId$changeImgNameTxtboxId.setValue("");
		newGallerySubWinId$folderErrorMsgLblId.setValue("");
		newGallerySubWinId$chngImgNameErrorMsgLblId.setValue("");
		newGallerySubWinId.setTitle("Change Image Name");
		newGallerySubWinId$createFolderDivId.setVisible(false);
		newGallerySubWinId$changeImgNameDivId.setVisible(true);
		newGallerySubWinId.setVisible(true);
		newGallerySubWinId.setAttribute(IMAGE_MEDIA, media);
		newGallerySubWinId.doHighlighted();
		//logger.debug("isAnySpecialChar ===============================8");
	}//displayChangeImageNameWindow()
	
} // class
