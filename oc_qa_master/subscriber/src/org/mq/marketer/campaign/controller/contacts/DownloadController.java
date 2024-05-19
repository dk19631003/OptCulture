package org.mq.marketer.campaign.controller.contacts;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ExportFileDetails;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.ExportFileDetailsDao;
import org.mq.marketer.campaign.dao.ExportFileDetailsDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Vbox;

public class DownloadController extends GenericForwardComposer {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	Div fileImagesDivId;
	
	
	public DownloadController(){
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Downloads","",style,true);
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		defaultFileSettings();
		
	}

	private void defaultFileSettings() {
		
		
		ExportFileDetailsDao exportFileDetailsDao = null;
		
		
		try {
			exportFileDetailsDao = (ExportFileDetailsDao)ServiceLocator.getInstance().getDAOByName("exportFileDetailsDao");
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(exportFileDetailsDao == null) return;
		String statusStr = "'"+Constants.EXPORT_FILE_COMPLETED+"','"+Constants.EXPORT_FILE_PROCESSING+"'";
		List<ExportFileDetails> exportList = exportFileDetailsDao.findAllByStatusAndUserId(statusStr, GetUser.getLoginUserObj().getUserId(), Constants.EXPORT_FILE_TYPE_SEGMENT);
		if(exportList == null || exportList.size() == 0) {
			logger.debug("No segment files existed in DB  and returning");
			return;
		}
		for (ExportFileDetails eachExportFile : exportList) {
			
			try {
				
				createImageDiv(eachExportFile);
			} catch (Exception e) {
				logger.error("File not found  ... DB id is  "+eachExportFile.getExportFileId());
				e.printStackTrace();
			}
			
		}
		
		/*File[] exportFile = null;
		
		
		
		String userName = GetUser.getUserName();
		String usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory");
		String filePath = usersParentDirectory + "/" +userName + "/List/Export_Files/";
		logger.info("filePath is  >>> "+filePath);
		File exportFileDir = new File(filePath);
		
		if(!exportFileDir.isDirectory()){
			logger.info("User File Not Exist returning..");
			return;
		}
		
		File[] exportFile = exportFileDir.listFiles();
		createImageDiv(exportFile);*/
	}
	
	
	private int count=0;

	public void createImageDiv(ExportFileDetails eachExportFile) {
		try{
			File exportedFile = new File(eachExportFile.getFilePath());
			int width = Integer.parseInt(PropertyUtil.getPropertyValue("imageThumbWidth"));
			int height = Integer.parseInt(PropertyUtil.getPropertyValue("imageThumbHeight"));
			String fileName = null;
			Vbox imageOuterVbox = null;
			Vbox imgInnerVbox = null;
			Image img = null;
			Label imgNameLbl = null;
//			Hbox imageOptionsHbox = null;
			Image previewTlbBtn = null;
			
//			logger.debug("number of files are : " + fileList.length);
			String userName = GetUser.getUserName();
			String usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory");
			
			/*for(int i=0;i<fileList.length;i++){
				if(fileList[i].isFile()) {*/
					
//					File eachFile = tempFile;
					fileName = exportedFile.getName();
					
					imageOuterVbox = new Vbox();
					
					imageOuterVbox.setWidgetListener("onMouseOver", "onImgMouseOver(this)");
					imageOuterVbox.setWidgetListener("onMouseOut", "onImgMouseOut(this)");
					imageOuterVbox.setStyle("margin:2px;padding-left:20px;padding-top:20px;padding-right:20px;padding-bottom:5px;float:left;");
					imageOuterVbox.setAlign("center");
					imageOuterVbox.setId("vbox_"+count);
					
					imgInnerVbox = new Vbox();
					imgInnerVbox.setWidth(width + "px");
					imgInnerVbox.setHeight(height + "px");
					imgInnerVbox.setAlign("left");
					imgInnerVbox.setStyle("margin-left:2px;margin-top:1px;margin-bottom:2px;");
					
					img = new Image("img/icons/csv_icon.png");
					img.setWidth(width+"px");
					img.setHeight(height+"px");
					img.setStyle("margin:auto;padding:auto;");
					img.setAlign("center");
					img.setTooltiptext(fileName);
					img.setParent(imgInnerVbox);			
					
					//File Name
					Hbox tempHbox = new Hbox();
					tempHbox.setAlign("left");
					imgNameLbl = new Label("Name:");
					imgNameLbl.setStyle("display:block;font-weight: bold");
					imgNameLbl.setParent(tempHbox);	
					imgNameLbl = new Label(fileName);
					imgNameLbl.setStyle("white-space:pre;");
					imgNameLbl.setMaxlength(10);
					imgNameLbl.setTooltiptext(fileName);
					imgNameLbl.setParent(tempHbox);	
					tempHbox.setParent(imgInnerVbox);
					
					//Status
					tempHbox = new Hbox(); 
					tempHbox.setAlign("left");
					imgNameLbl = new Label("Status:");
					imgNameLbl.setStyle("display:block;font-weight: bold;");
					imgNameLbl.setParent(tempHbox);	
					String stausStr =  eachExportFile.getStatus().equals(Constants.EXPORT_FILE_COMPLETED) ? "Ready" : eachExportFile.getStatus();
					if(stausStr.equals(Constants.EXPORT_FILE_PROCESSING)) stausStr = "Processing"; 
					imgNameLbl = new Label(stausStr);
					imgNameLbl.setParent(tempHbox);	
					tempHbox.setParent(imgInnerVbox);
					imgInnerVbox.setParent(imageOuterVbox);
					
					//Size
					tempHbox = new Hbox(); 
					tempHbox.setAlign("left");
					imgNameLbl = new Label("Size:");
					imgNameLbl.setStyle("display:block;font-weight: bold");
					imgNameLbl.setParent(tempHbox);	
					double size = (exportedFile.length()/1024);
					String actualSize = "";
					if (size <= 0) {
						size = exportedFile.length();
						actualSize = size+" bytes";
					}else {
						actualSize = size+" kb";
					}
					imgNameLbl = new Label(actualSize);
					imgNameLbl.setStyle("white-space:pre;");
					imgNameLbl.setParent(tempHbox);	
					
					
					if(stausStr.equals("Ready")) {
						String src = usersParentDirectory + "/" + userName + "/List/Export_Files/"+ fileName;
						previewTlbBtn = new Image("/img/icons/download.png");
						previewTlbBtn.setStyle("cursor:pointer;cursor:hand;margin:0px 0px 0px 15px");
						previewTlbBtn.setTooltiptext("Download");
						previewTlbBtn.setVisible(false);
						previewTlbBtn.setAttribute("imageEventName","FILE_EXPORT");
						previewTlbBtn.setAttribute("FilePath", src);
						previewTlbBtn.addEventListener("onClick", this);
						previewTlbBtn.setParent(tempHbox);
						previewTlbBtn.setId("zoom_"+count);
						tempHbox.setParent(imgInnerVbox);
					}
					
					imgInnerVbox.setParent(imageOuterVbox);
					imageOuterVbox.setAttribute("DBObject", eachExportFile);
					
					imageOuterVbox.setParent(fileImagesDivId);
					count++;
					//logger.debug("All components are added to div");
//			}// for imgList
			}catch (Exception e) {
				logger.error("** Exception in uploadZIPFile **",e);
			}
		
	}// createImageDiv()
	
	
	public void onClick$clearAllFileDownloadTBId() {
		
		try {
			List imageVboxList = fileImagesDivId.getChildren();
			if(imageVboxList != null && imageVboxList.size() == 0) {
				MessageUtil.setMessage("There are no downloads available.", "color:red;");
				return;
			}
			
			int confirm =  Messagebox.show("Are you sure you want to delete all the exported files?"
					+ "Note: Files only in \"Ready\" status will be deleted.", "Prompt", 
					Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) ;
				if (confirm == 1) {
					
				ExportFileDetails eachExportFile = null;
				List<ExportFileDetails> exportFileDetailsList = new ArrayList<ExportFileDetails>();
				
				for (Object eachObj : imageVboxList) {
					
					if(! (eachObj instanceof Vbox)) continue;
					
					Vbox tempVbox = (Vbox)eachObj;
					eachExportFile = (ExportFileDetails)tempVbox.getAttribute("DBObject");
					
					if(eachExportFile.getStatus().equals(Constants.EXPORT_FILE_PROCESSING)) {
						continue;
					}
					
					eachExportFile.setStatus(Constants.EXPORT_FILE_DELETED);
					eachExportFile.setDeletedTime(Calendar.getInstance());
					exportFileDetailsList.add(eachExportFile);
				}
				
				try {
					/*ExportFileDetailsDao fileExportDetailsDao = 
							(ExportFileDetailsDao)ServiceLocator.getInstance().getDAOByName("exportFileDetailsDao");
					fileExportDetailsDao.saveByCollection(exportFileDetailsList);*/
					ExportFileDetailsDaoForDML fileExportDetailsDaoForDML = 
							(ExportFileDetailsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("exportFileDetailsDaoForDML");
					fileExportDetailsDaoForDML.saveByCollection(exportFileDetailsList);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ",e);
				}
				
				Components.removeAllChildren(fileImagesDivId);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return;
		}
		
	}// onClick$clearAllFileDownloadTBId
	//List/Export_Current_File
	
	private static final String userparentDir = PropertyUtil.getPropertyValue("usersParentDirectory");
	private boolean fileDownload(String filePath) {
		try {
			String userName=GetUser.getLoginUserObj().getUserName();
			userName = userName.substring(0, userName.lastIndexOf("__org__"));
			String dummyFileLocStr = userparentDir+File.separator+userName+"/List/Export_Current_File/";
			File downloadDir = new File(dummyFileLocStr);
			logger.info(" downloadDir file path :: "+downloadDir.getAbsolutePath());
			if(!downloadDir.exists()){
				downloadDir.mkdirs();
			}
			File[] listFile = downloadDir.listFiles();
			if(listFile.length > 0) {
				for (File eachFile : listFile) {
					eachFile.delete();
				}
			}
			
			File downLoadFile = new File(filePath);
			String fileName = downLoadFile.getName();
			File tempFile = new File(dummyFileLocStr+fileName);
			boolean flag= downLoadFile.renameTo(tempFile);
			logger.info(" before exporting file path :: "+downLoadFile.getAbsolutePath());
			
			logger.info("flag is  ::"+flag);
			
			if(!tempFile.canRead()){
				logger.info("unable to read the file .."+tempFile.getAbsolutePath());
				return false;
			}
			Filedownload.save(tempFile, "text/plain");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
			super.onEvent(event);
			Object eventObj = event.getTarget();
			
			if(eventObj instanceof Image) { 
				
				
				Image img = (Image)eventObj;
				logger.info(" get path is  ::"+img.getAttribute("FilePath"));
				String filePathStr = (String)img.getAttribute("FilePath");
				/*System.out.println("1 time div is "+img.getParent());
				System.out.println("1st=== object existed here :: "+img.getParent().getAttribute("DBObject"));
				System.out.println("2 time div is "+img.getParent().getParent());
				System.out.println("2nd === object existed here :: "+img.getParent().getParent().getAttribute("DBObject"));
				System.out.println("3rdnd === object existed here :: "+img.getParent().getParent().getParent().getAttribute("DBObject"));*/
				Vbox delVbox =(Vbox) img.getParent().getParent();
				
				
				ExportFileDetails eachExportFile = (ExportFileDetails)delVbox.getParent().getAttribute("DBObject");
				logger.info("after clicking the download icon the Object get from the VBOX is  :: "+eachExportFile);
				boolean fileDownloadedFlag = fileDownload(filePathStr);
				if(!fileDownloadedFlag) {
					logger.info("fileDownloadedFlag is not "+fileDownloadedFlag);
					return;
				}
				delVbox.setVisible(false);
				
				try {
					ExportFileDetailsDao fileExportDetailsDao = 
							(ExportFileDetailsDao)ServiceLocator.getInstance().getDAOByName("exportFileDetailsDao");
					ExportFileDetailsDaoForDML fileExportDetailsDaoForDML = 
							(ExportFileDetailsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("exportFileDetailsDaoForDML");
					eachExportFile.setStatus(Constants.EXPORT_FILE_DELETED);
					eachExportFile.setDeletedTime(Calendar.getInstance());
					//fileExportDetailsDao.saveOrUpdate(eachExportFile);
					fileExportDetailsDaoForDML.saveOrUpdate(eachExportFile);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				
//				boolean delFlag = downLoadFile.delete();
//				logger.info("delFlag is  :: "+delFlag);
//				Filedownload.save(file, "text/plain");
				/*File deletedFile = new File(filePathStr);
				boolean delFlag = deletedFile.delete();
				if(delFlag){
					
					Vbox delVbox =(Vbox) img.getParent().getParent();
					delVbox.setVisible(false);
				}else {
					MessageUtil.setMessage("After downloading we unable to delete the file due to some previlages issue", "green");
					return;
				}*/
			}
	}

}
