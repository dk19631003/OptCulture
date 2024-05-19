package org.mq.marketer.campaign.controller.gallery;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;

public class ImageLibraryController extends GenericForwardComposer {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Div imageDivId;
	private Listbox ImageNamesLBId,galLBId;
	private Textbox imgPathTbId;
	private Label heightLbId,widthLbId;
	
	String userName,usersParentDirectory;
	private Button insertTinyMCEBtnId,insertckEditorBtnId;
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		List data = new LinkedList();
		data.add(0, "--Gallery Name--");
		usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory");
		logger.debug ("usersParentDirectory: " + usersParentDirectory);
		userName = GetUser. getUserName();
		logger.debug ("user Name : " + userName);
		File saveDirectory = new File(usersParentDirectory + "/" +  userName + "/"+ "Gallery" );
		String[] children = saveDirectory.list(); 
		if(children == null){// Either dir does not exist or is not a directory 
		}else{ 
			for (int i = 0; i < children.length; i++) { 
				data.add(children[i]); //response.getWriter().println(children[i]);
				logger.debug("gallery name is: " + children[i]);
			} 
		}
		
		ListModel strset = new SimpleListModel(data);
		galLBId.setModel(strset);
		
		//onSelect$galLBId();
//		logger.info("=============="+galLBId.getSelectedItem().getLabel());
//		logger.info(">>>>>>>>>>>>>>>"+galLBId.getSelectedItem().getValue());
		
		
		if(galLBId.getItemCount() >0) {
			galLBId.setSelectedIndex(0);
			onSelect$galLBId();
		}
		
		
		String selectEditorType = null;
		selectEditorType = (String)Sessions.getCurrent().getAttribute("EditorType");
		 
		 if(selectEditorType == null) {
			 logger.debug("selectEditorType is :::"+selectEditorType); 
		 }
		 else  if(selectEditorType.equals("TinyMCEEditor")) { //getting this attribute from session , BlockEditor controller
			 insertckEditorBtnId.setVisible(false);
			 insertTinyMCEBtnId.setVisible(true);
		 }
		 
		 else if(selectEditorType .equals("ckEditor")) { //getting this attribute from session , plainEditor controller
			insertTinyMCEBtnId.setVisible(false);
			insertckEditorBtnId.setVisible(true);
			
		}
	
	} //doafterCompose
	
	
	public void onSelect$ImageNamesLBId() {
		
		try {
			logger.debug("----Just Enter here----");
			displayImage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	}
	
	private void displayImage() throws Exception{
		logger.debug("Just enter onSeletct method");
		Components.removeAllChildren(imageDivId);
		String imageName = (String)ImageNamesLBId.getSelectedItem().getValue();
		String galName = (String)galLBId.getSelectedItem().getValue();
		String imageDirectory =  "/UserData/" +  userName + "/"+ "Gallery/"+galName+"/"+imageName;
		Image image = new Image();
		image.setSrc(imageDirectory);
		org.zkoss.image.AImage imgFile = new org.zkoss.image.AImage(usersParentDirectory + "/" +  userName + "/"+ "Gallery/"+galName+"/"+imageName);
		logger.debug("----original image height---"+imgFile.getHeight());
		logger.debug("----original image width---"+imgFile.getWidth());
		image.setHeight("150px");
		image.setWidth("150px");
		image.setParent(imageDivId);
		if(galName.equals("My Images")){
			galName= galName.replace("My Images", "My%20Images");
		}
		imgPathTbId.setValue(PropertyUtil.getPropertyValue("ApplicationUrl") + "UserData/"+userName+"/"+"Gallery/"+galName+"/"+imageName);
		heightLbId.setValue(imgFile.getHeight()+"");
		widthLbId.setValue(imgFile.getWidth()+"");
	} // displayImage
	
	public void onSelect$galLBId() {
		
		try {
			logger.debug("Just Enter here");
			onSelect();
		} catch (Exception e) {
			logger.error("Exception error getting from the onSelect method ::",e);
		}
	}
	
	private void onSelect() throws Exception {
		String galName = (String)galLBId.getSelectedItem().getLabel();
		logger.debug("---onSelect()--galName--"+galName);
		if(galLBId .getSelectedIndex() == -1 || galName.equals("--Gallery Name--")
											|| galName.trim().equals(""))  {
			return;
		}
		
		Label imageName;
		List list = new ArrayList();
		Components.removeAllChildren(ImageNamesLBId);
		Components.removeAllChildren(imageDivId);
		ImageNamesLBId.clearSelection();
		imgPathTbId.setValue(" ");
		heightLbId.setValue("");
		widthLbId.setValue("");
		
		logger.debug("GalleryName >>>>>::"+galName);
		if(galName.equals("--Gallery Name--")){
			return;
		}
		
		File galDirectory =  new File(usersParentDirectory + "/" +  userName + "/"+ "Gallery/"+galName );
		String[] imageList = galDirectory.list();
		if(imageList != null){
			logger.debug("---onSelect()--No of images in the gallery--"+imageList.length);
			for(int i=0; i < imageList.length;i++){
				logger.debug("---image --"+imageList[i]);
				list.add(imageList[i]);
			}
		}
		ListModel listModel = new SimpleListModel(list);
		Listbox imageListbox = new Listbox();
		ImageNamesLBId.setModel(listModel);
		ImageNamesLBId.setVisible(true);
		
		
	} // onSelect
	
}
