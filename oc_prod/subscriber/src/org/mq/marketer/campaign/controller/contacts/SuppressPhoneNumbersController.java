package org.mq.marketer.campaign.controller.contacts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.clapper.util.io.FileUtil;
import org.json.JSONObject;
import org.mq.marketer.campaign.beans.SMSSuppressedContacts;
import org.mq.marketer.campaign.beans.SuppressedContacts;
import org.mq.marketer.campaign.beans.Unsubscribes;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.contacts.SuppressContactsController.MyEventListener;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDao;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;
import org.zkoss.zhtml.Table;

public class SuppressPhoneNumbersController extends GenericForwardComposer {
	
	Grid suppContGridId;
	A viewSuppContsBtId;
	Button closeBtnId;
	Textbox suppLstSearchTbId, selectedFileTbId;
	Button delAllBtnId;
	Button delBtnId,exportBtnId;
	private Div suppressResDivId, suppressDivId,manualAdditionWinId$suppressResDivId,displayBottomMsgDivId,displayMeaningOfDeleteImageDivId;
	Listbox supptypeLbId, pageSizeLbId,exportFilterLbId;
	Paging suppContsPgId;
	//Bandbox actionsBandBoxId;
	private Window manuallyWinId,manualAdditionWinId;
	private Columns suppContColsId;
	private Rows suppContRowsId;
	private Bandbox actionsBandBoxId;
	
	private Radiogroup manualAdditionWinId$manualAdditionChoice;
	private Radio manualAdditionWinId$rdoBtn1;
	private Radio manualAdditionWinId$rdoBtn2;
	private Div manualAdditionWinId$bulkAdditionDivId1;
	private Div manualAdditionWinId$bulkAdditionDivId2;
	private Div manualAdditionWinId$suppContDivId;
	
	private Div manualAdditionWinId$singlePhoneDivId;
	private Div manualAdditionWinId$bulkPhoneIdDivId;
	private Div manualAdditionWinId$successMsgDivId;	
	private Textbox manualAdditionWinId$singleUserPhoneNumberTbId;
	private Textbox manualAdditionWinId$selectedFileTbId;
	private Button manualAdditionWinId$addSinglePhoneIdBtnId;
	private Button manualAdditionWinId$uploadBtnId;
	private Button manualAdditionWinId$uploadId;
	private Label manualAdditionWinId$emptyTextBoxLblId,displayBottomMsgLblId,manualAdditionWinId$errorMsgLblId;
	private Label manualAdditionWinId$successMsgLblId;
	private final String MANUALLY_ADDED = "Manually Added";
	private final String NO_REASON_FOR_MANUALLY_ADDED = "--";
	private boolean headerCheckBoxPreviouslyChkd;
	
	int totContacts;
	Media gMedia = null;
	
	SMSSuppressedContactsDao smsSuppressedContactsDao;
	SMSSuppressedContactsDaoForDML smsSuppressedContactsDaoForDML;
		
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		smsSuppressedContactsDao = (SMSSuppressedContactsDao)SpringUtil.getBean("smsSuppressedContactsDao");
		smsSuppressedContactsDaoForDML = (SMSSuppressedContactsDaoForDML)SpringUtil.getBean("smsSuppressedContactsDaoForDML");
		
		List<String> supuppTypes= smsSuppressedContactsDao.findAllTypeByUserId(GetUser.getUserId());
		Listitem li;
		for(String type : supuppTypes) {
				if(Constants.SMS_SUPP_TYPE_OPTED_OUT.equalsIgnoreCase(type) || Constants.SMS_SUPP_TYPE_USERADDED.equalsIgnoreCase(type)) continue;
				li = new Listitem();
				li.setValue(type);
				li.setLabel(type);
				if(type.equalsIgnoreCase(Constants.SMS_SUPP_TYPE_USERADDED)) {
					
					li.setValue(type);
					li.setLabel("CSV Uploaded");
				}
				li.setParent(supptypeLbId);
		}
		
		li=new Listitem();
		li.setValue(Constants.SMS_SUPP_TYPE_USERADDED);
		li.setLabel(MANUALLY_ADDED);
		li.setParent(supptypeLbId);
		
		if(supptypeLbId.getItemCount() > 0)supptypeLbId.setSelectedIndex(0);
		
        logger.info("inside doAfter()------------------------------------------------supptypeLbId.getSelectedIndex().getLabel()="+supptypeLbId.getSelectedItem().getLabel());		
		suppContsPgId.addEventListener("onPaging", new MyEventListener());
		onSelect$supptypeLbId();
		
		displayBottomMsgDivId.setVisible(false);
		displayMeaningOfDeleteImageDivId.setVisible(true);
		
	}//doAfterCompose()
	
	public int getCount() {
		int retCount = 0;
		
			String searchStr = "";
			if(!suppLstSearchTbId.getValue().equals("") && !suppLstSearchTbId.getValue().equalsIgnoreCase("Search Phone Number")) {
				searchStr = suppLstSearchTbId.getValue();
				
			}
			Long userId = GetUser.getUserId();
			String type = (String)supptypeLbId.getSelectedItem().getValue();
			retCount =  smsSuppressedContactsDao.getTotCountByUserId(userId, type, searchStr);
		
		return retCount;
		
	}//getCount()
	
	public List<SMSSuppressedContacts> getSMSSuppContactsList() {
		String type = supptypeLbId.getSelectedItem().getValue(); 
		List<SMSSuppressedContacts> smsSuppContactsList= null;
		logger.info("type====="+type);
		//if(supptypeLbId.getSelectedIndex()!=0) {
				String searchStr = "";
				if(!suppLstSearchTbId.getValue().equals("") && !suppLstSearchTbId.getValue().equalsIgnoreCase("Search Phone Number")) {
					
					searchStr = suppLstSearchTbId.getValue();
					
				}
				Long userId = GetUser.getUserId();
				
				smsSuppContactsList =  smsSuppressedContactsDao.findAllByUsrId(userId, type, searchStr, 
															suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize());
		//}
	
		return smsSuppContactsList;
	}//getSuppContactsList()
	
	public void redrawSMSSuppList(List<SMSSuppressedContacts> tempList, int firstResult, int size) {
		try {/*
			logger.info("--just entered--");
			Rows rows = resetGridRows();
			Row rowInner;
			for (SMSSuppressedContacts smsSuppressedContacts : tempList) {
				logger.info("----just entered for each row----");
				rowInner = new Row();
				rowInner.setAttribute("contactId", smsSuppressedContacts.getId());
				(new Checkbox()).setParent(rowInner);
				(new Label(smsSuppressedContacts.getMobile())).setParent(rowInner);
				(new Label(smsSuppressedContacts.getType())).setParent(rowInner);
				rowInner.setParent(rows);
			}*/
			
			
			
			
			logger.info("--just entered--");
			logger.info("------------------------------------redrawSMSSuppList-------------------------------");
			Components.removeAllChildren(suppContRowsId);
			//resetGridCols();
			
			
			TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone"); 
			String type = supptypeLbId.getSelectedItem().getValue().toString();
			logger.info("selected : "+type);
			Row rowInner = null;
			
			/*MyCalendar.calendarToString(mailingList.getLastModifiedDate(),
					MyCalendar.FORMAT_DATETIME_STDATE, tz)
					
					MyCalendar.calendarToString(lastPurDate,MyCalendar.FORMAT_YEARTODATE)
					
					lastRefreshedlId.setValue(MyCalendar.calendarToString(contactLoyalty.getLastFechedDate(), MyCalendar.FORMAT_SCHEDULE));*/
			for (SMSSuppressedContacts aSMSSuppressedContact : tempList) {
				//logger.info("----just entered for each row----");
				rowInner = new Row();
				rowInner.setAttribute("contactId", aSMSSuppressedContact.getId());
				//(new Checkbox()).setParent(rowInner);
				
				//if(type.equalsIgnoreCase(Constants.SMS_SUPP_TYPE_USERADDED)) (new Checkbox()).setParent(rowInner);// adding chkbx to  option 'manually added' 
				
				if(type.equalsIgnoreCase(Constants.SMS_SUPP_TYPE_USERADDED)) { 
					Checkbox chkbox = new Checkbox();
					chkbox.addEventListener("onCheck", new MyEventListener());
					chkbox.setParent(rowInner);
				}
				
				(new Label(aSMSSuppressedContact.getMobile())).setParent(rowInner);
				
				if(type.equalsIgnoreCase("all")){
					if(aSMSSuppressedContact.getType().equalsIgnoreCase(Constants.SMS_SUPP_TYPE_USERADDED)){
						
						(new Label(MANUALLY_ADDED)).setParent(rowInner);//adding suppression type to option 'all'.
					}
					else{
						(new Label(aSMSSuppressedContact.getType())).setParent(rowInner);
					}
				}
				
				
				
				(new Label(MyCalendar.calendarToString(aSMSSuppressedContact.getSuppressedtime(),
						MyCalendar.FORMAT_DATETIME_STDATE,tz))).setParent(rowInner);
				
				if(!type.equalsIgnoreCase(Constants.SMS_SUPP_TYPE_USERADDED)) (new Label(aSMSSuppressedContact.getReason())).setParent(rowInner);
				
				if (type.equalsIgnoreCase("all") || type.equalsIgnoreCase(Constants.SMS_SUPP_TYPE_USERADDED)) {
					Image img = new Image("/img/icons/delete_icon.png");
					img.setStyle("margin-right:5px;cursor:pointer;");
					img.setTooltiptext("Delete");
					img.addEventListener("onClick", new MyEventListener());
					img.setParent(rowInner);
				}
				
				rowInner.setParent(suppContRowsId);
			}
		  } catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception");
		}
		  
	}//redrawSuppList()
	
	private Rows resetGridRows() {
		Rows rows = suppContGridId.getRows();
		
		if(rows != null) {
			suppContGridId.removeChild(rows);
		} 
		rows = new Rows();
		rows.setParent(suppContGridId);
		return rows;
	}//resetGridRows()
	
	
	
	
	public void onSelect$pageSizeLbId() {
		try {
			int size = getCount();
			suppContsPgId.setTotalSize(size);
			int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
			suppContsPgId.setPageSize(pageSize);
			suppContsPgId.setActivePage(0);
			
			List<SMSSuppressedContacts> suppContTypeList = null;
			suppContTypeList = getSMSSuppContactsList();
			redrawSMSSuppList(suppContTypeList,suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize());
			
		} catch (Exception e) {
			logger.error("Exception");
		} 
	}//onSelect$pageSizeLbId() 
	
	public void onSelect$supptypeLbId() {
		
		try {
			if(logger.isDebugEnabled()) logger.debug("-- Just entered --");
			MessageUtil.clearMessage();
			
			//suppLstSearchTbId.setValue("Search Phone Number");
			List<SMSSuppressedContacts> suppContTypeList = null;
			
			String suppTypeStr = (String)supptypeLbId.getSelectedItem().getValue();
			
			if(supptypeLbId.getSelectedIndex() != 0) { //i.e option is not set to 'All'
				logger.info("onSelect$supptypeLbId()===================================================== other than option 'all'");
				    resetGridCols();
				    suppLstSearchTbId.setValue("Search Phone Number");
					int size = getCount();
					int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
					
					//List<SMSSuppressedContacts> suppContTypeList = null;
					suppContTypeList = getSMSSuppContactsList();
					redrawSMSSuppList(suppContTypeList,suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize());
		
					suppContsPgId.setTotalSize(size);
					suppContsPgId.setPageSize(pageSize);
					suppContsPgId.setActivePage(0);
					
					if(suppTypeStr.equalsIgnoreCase(Constants.SMS_SUPP_TYPE_USERADDED)){
						actionsBandBoxId.setVisible(true);
						actionsBandBoxId.setDisabled(true);
						/*if(size>0){
							actionsBandBoxId.setDisabled(false);
						}
						else{
							actionsBandBoxId.setDisabled(true);
						}*/
					}
					else{
						actionsBandBoxId.setVisible(false);
					}
					
					displayBottomMsgLblId.setValue("To remove a phone number in '"+suppTypeStr+"' category, please contact support team at support@optculture.com.");
					displayBottomMsgDivId.setVisible(true);
					displayMeaningOfDeleteImageDivId.setVisible(false);
					
					if(suppTypeStr.equalsIgnoreCase(Constants.SMS_SUPP_TYPE_USERADDED)){
						displayBottomMsgDivId.setVisible(false);
						displayMeaningOfDeleteImageDivId.setVisible(true);
					}
			}
			else { 
				
				/*Rows rows = suppContGridId.getRows();
				
				if(rows != null) {
					suppContGridId.removeChild(rows);
				} */
				actionsBandBoxId.setVisible(false);
				logger.info("onSelect$supptypeLbId()=====================================================option 'all'");
				resetGridCols();
				suppLstSearchTbId.setValue("Search Phone Number");
				int size = getCount();
				int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
				suppContsPgId.setTotalSize(size);
				suppContsPgId.setPageSize(pageSize);
				suppContsPgId.setActivePage(0);
				
				
				suppContTypeList = getSMSSuppContactsList();
				redrawSMSSuppList(suppContTypeList,suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize());
				
				displayBottomMsgDivId.setVisible(false);
				displayMeaningOfDeleteImageDivId.setVisible(true);
				
			}
			
		} catch (Exception e) {
			logger.error("** Exception while displaying selected entries :",e);
		}
	}
	/*****************************************************************************************************/
	public void onUpload$uploadBtnId$manualAdditionWinId(UploadEvent event) {
		
		browse(event.getMedia());
	}
	
	public void browse(Media media) {
		logger.info("Browse is called");
		manualAdditionWinId$selectedFileTbId.setValue(media.getName());
		manualAdditionWinId$selectedFileTbId.setDisabled(true);
		gMedia = media;
	}

	public void upload(){
		
		MessageUtil.clearMessage();
		Media media = gMedia; 
		
		if(media == null) {
			//MessageUtil.setMessage("Please select a file.", "color:red", "TOP");
			manualAdditionWinId$errorMsgLblId.setValue("Please select a file.");
			return;
		}else{
			manualAdditionWinId$errorMsgLblId.setValue("");
		}
		
		logger.info("File name : " + media.getName());
		String path = PropertyUtil.getPropertyValue("usersParentDirectory").trim() + "/" + sessionScope.get("userName") + "/List/" +((Media)media).getName();
		String ext = FileUtil.getFileNameExtension(path);
		if(ext == null) {
			//MessageUtil.setMessage("Upload .csv file only.","color:red","TOP");
			manualAdditionWinId$errorMsgLblId.setValue("Only .csv file format is allowed.");
			return;
		}
		if(!ext.equalsIgnoreCase("csv")) {
			//MessageUtil.setMessage("Upload .csv file only.","color:red","TOP");
			manualAdditionWinId$errorMsgLblId.setValue("Only .csv file format is allowed.");
			return;
		}
		uploadSuppressCSVFile(media,suppressDivId,manualAdditionWinId$suppressResDivId);
		gMedia = null;
		//actionsBandBoxId.setDisabled(false);
	}
	
	public void uploadSuppressCSVFile(Media media,Div suppressDivId,Div manualAdditionWinId$suppressResDivId) {
		try {
			
			if(logger.isDebugEnabled()) logger.debug("-- Just entered--");
			
			MessageUtil.clearMessage();
			String successMsg = "";
			Media m = (Media)media;
			String path = PropertyUtil.getPropertyValue("usersParentDirectory").trim() + "/" + sessionScope.get("userName") + "/List/" +((Media)media).getName();
			UploadCSVFileController uploadCSVFileController = new UploadCSVFileController();
			boolean isSuccess = uploadCSVFileController.copyDataFromMediaToFile(path,m);
			
			if(logger.isDebugEnabled()) logger.debug("Is copy of the file successfull :"+isSuccess);
			
			if(!isSuccess){
				if(logger.isDebugEnabled()) logger.debug("Could not copy the file from Media");
				return;
			}
			
			if(logger.isDebugEnabled()) logger.debug("File copied from media is successfull.");
			
			UploadCSVFile uploadCSVFile = (UploadCSVFile)SpringUtil.getBean("uploadCSVFile");
			uploadCSVFile.setSmsSuppressedContactsDao(smsSuppressedContactsDao);
			
			if(logger.isDebugEnabled()) logger.debug("uploadCSVFile object : " +  uploadCSVFile);
			
			Object[] obj = {GetUser.getUserObj(),path,m.getName(),"SMS"};
			
			if(logger.isDebugEnabled()) logger.debug("Is uploadCSVFile thread running : " + uploadCSVFile.isRunning);
			
			synchronized(uploadCSVFile) {
				uploadCSVFile.uploadQueue.add(obj);
				if(!uploadCSVFile.isRunning){
					Thread thread = new Thread(uploadCSVFile);
					thread.start();
				}
			}
			
			if(logger.isDebugEnabled()) logger.debug("Thread Started . Exiting ...");
			
			//suppressDivId.setVisible(false);
			//manualAdditionWinId$selectedFileTbId.setValue("");
			//manualAdditionWinId$suppressResDivId.setVisible(true);
			//suppContViewDivId.setVisible(false);
			
			
			manualAdditionWinId$manualAdditionChoice.setSelectedItem(null);
			manualAdditionWinId$singlePhoneDivId.setVisible(false);
			manualAdditionWinId$bulkPhoneIdDivId.setVisible(false);
			
			successMsg = "Phone numbers will be uploaded in a moment. You can view them in the manually added section.";
			
	        manualAdditionWinId$successMsgLblId.setValue(successMsg);
	        manualAdditionWinId$successMsgDivId.setVisible(true);
			
	     } catch(Exception e) {
	    	 logger.error("** Exception :",(Throwable)e);
	     }
	}
	
	public void onClick$uploadId$manualAdditionWinId() {
		
		upload();
		
	}
	/*****************************************************************************************************/

	public void onClick$delBtnId() {
		try {
			MessageUtil.clearMessage();
						
			Rows rows = suppContGridId.getRows();
			if(rows.getChildren().size() == 0) {
				
				MessageUtil.setMessage("There is no contact to delete.","color:blue");
				return;
				
				
			}
			//int size = rows.getChildren().size();
			Iterator<Component> iterator = rows.getChildren().iterator();
			Row row;
			boolean isCheckedFlag = false;
			
			if (Messagebox.show("Are you sure you want to delete selected phone number(s) from suppressed list?", "Confirm Delete", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) ==  Messagebox.YES) {
				while(iterator.hasNext()) {
					try {
						row = (Row)iterator.next();
						if(!(row.getFirstChild() instanceof Checkbox)) continue;
						
						if(!row.isVisible()) {
							logger.debug("---inside not visible block---");
							continue;
						}
						
						if(((Checkbox)row.getFirstChild()).isChecked() == true) {
							isCheckedFlag = true;
							smsSuppressedContactsDaoForDML.deleteById(Long.parseLong(row.getAttribute("contactId").toString()));
							row.setVisible(false);
							
						} // if
					} catch (Exception e) {
						logger.error("** Exception while looping selected entries :"+e);
					} //catch
				} //while
				
				if(!(rows.getChildren().iterator().hasNext())) {
					//actionsBandBoxId.setDisabled(true);
					
					delAllBtnId.setVisible(false);
					delBtnId.setVisible(false);
					//actionsBandBoxId.setDisabled(true);
				}

				if(!isCheckedFlag) {
					MessageUtil.setMessage("No phone number selected for deletion. Please " +
							"select the phone number to be deleted.", "color:red", "top");
					return;
				}
				
				int size = getCount();
				int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
				suppContsPgId.setTotalSize(size);
				suppContsPgId.setPageSize(pageSize);
				
				/*if(size>0){
					actionsBandBoxId.setDisabled(false);
				}
				else{
					actionsBandBoxId.setDisabled(true);
				}*/
				
				actionsBandBoxId.setDisabled(true);
				
				List<SMSSuppressedContacts> smsSuppList = getSMSSuppContactsList();
				redrawSMSSuppList(smsSuppList, suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize() );
				MessageUtil.setMessage("Selected phone number removed from suppression list successfully.", "green", "top");
			} //if(Messagebox)
		} catch (Exception e) {
			logger.error("** Exception while deleting selected entries :"+e);
		}
	}
	
	/*public void onChanging$suppLstSearchTbId(InputEvent event) {
		try {
			logger.info("onChanging$suppLstSearchTbId()==================================");
			String searchStr = event.getValue();
			String type = supptypeLbId.getSelectedItem().getValue(); 
			logger.debug("Search string is :"+searchStr);
			Long userId = GetUser.getUserId();
			
			int size =  smsSuppressedContactsDao.getTotCountByUserId(userId, type, searchStr);
			int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
			suppContsPgId.setTotalSize(size);
			suppContsPgId.setPageSize(pageSize);
			suppContsPgId.setActivePage(0);
			
			
			List<SMSSuppressedContacts> srchList =  smsSuppressedContactsDao.findAllByUsrId(userId, type, searchStr, 
														suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize());
			 
			redrawSMSSuppList(srchList,suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize());
			suppLstSearchTbId.setText(searchStr);
			
			
		} catch (Exception e) {
			logger.error("Exception");
			
		}
	}// onOK$suppLstSearchTbId()
*/	
	public void onClick$delAllBtnId() {
		try {
			MessageUtil.clearMessage();
			Rows rows = suppContGridId.getRows();
			if(rows.getChildren().size() == 0) {
				
				MessageUtil.setMessage("There is no contact to be deleted.","color:blue");
				return;
				
			}
			
			if (Messagebox.show("Are you sure you want to delete all the contacts from suppressed list?", "Confirm Delete", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) ==  Messagebox.YES) {
				//smsSuppressedContactsDao.deleteAllByUserId(GetUser.getUserId(),Constants.SMS_SUPP_TYPE_USERADDED);
				smsSuppressedContactsDaoForDML.deleteAllByUserId(GetUser.getUserId(),Constants.SMS_SUPP_TYPE_USERADDED);
				Components.removeAllChildren(rows);
				//actionsBandBoxId.setDisabled(true);
				suppContsPgId.setTotalSize(0);
				suppContsPgId.setActivePage(0);
				actionsBandBoxId.setDisabled(true);
				((Checkbox)suppContColsId.getFirstChild().getFirstChild()).setChecked(false);
			}
		} catch (Exception e) {
			logger.error("Exception");
		}
	}// onClick$delAllBtnId
	
	public void onClick$exportBtnId(){
		logger.debug("-- just entered --");
		int count = getCount();
		int size = 1000;
		String searchStr = "";
		/*if(supptypeLbId.getSelectedIndex() == 0) {
			MessageUtil.setMessage("Please select Suppression Type.", "color:red", "TOP");
			return;
		}*/
		
		if(!suppLstSearchTbId.getValue().equals("") && !suppLstSearchTbId.getValue().equalsIgnoreCase("Search Phone Number")) {
			
			searchStr = suppLstSearchTbId.getValue();
			
		}
		String type = (String)supptypeLbId.getSelectedItem().getValue();
		String fileType = exportFilterLbId.getSelectedItem().getLabel();
		String category = (String)supptypeLbId.getSelectedItem().getLabel();
		String userName = GetUser.getUserName();
		String usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
		String exportDir = usersParentDirectory + "/" + userName + "/SuppressedPhoneNumbers/" ;
		File downloadDir = new File(exportDir);
		if(downloadDir.exists()){
			try {
				FileUtils.deleteDirectory(downloadDir);
				logger.debug(downloadDir.getName() + " is deleted");
			} catch (Exception e) {
				logger.error("Exception");
				logger.warn(downloadDir.getName() + " is not deleted");
			}
		}
		if(!downloadDir.exists()){
			downloadDir.mkdirs();
		}
		
		String filePath = "";
		StringBuffer sb = null;

		logger.debug("Writing to the file : " + filePath);

		TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone"); 
		if(fileType.contains("csv")){
			try {
								
				filePath = exportDir +  "SuppressedPhoneNumbers_" + category + "_" + System.currentTimeMillis() + ".csv";
				
				logger.debug("Download File path : " + filePath);
				File file = new File(filePath);
				BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
				if(type.equalsIgnoreCase(Constants.SMS_SUPP_TYPE_USERADDED)){
					bw.write("\"Phone Number\",\"Timestamp\" \r\n");
				}else if(type.equalsIgnoreCase("all")){
					
					bw.write("\"Phone Number\",\"Suppression Type\",\"Timestamp\",\"Reason\" \r\n");
				}else{
					bw.write("\"Phone Number\",\"Timestamp\",\"Reason\" \r\n");
				}
				
				for(int i=0; i < count; i+=size) {
					sb = new StringBuffer();
					List<SMSSuppressedContacts> smsSuppList = smsSuppressedContactsDao.findAllByUsrId(GetUser.getUserId(), type, searchStr, i, size);
					if(smsSuppList.size() > 0) {
						for (SMSSuppressedContacts smsSuppCont : smsSuppList) {
							sb.append("\"");sb.append(smsSuppCont.getMobile());
							
								if(type.equalsIgnoreCase("all")){
									
									if(smsSuppCont.getType().equalsIgnoreCase(Constants.SMS_SUPP_TYPE_USERADDED)){
										sb.append("\","); sb.append("\"");sb.append(MANUALLY_ADDED);sb.append("\",");
										sb.append("\"");
										sb.append(MyCalendar.calendarToString(smsSuppCont.getSuppressedtime(),
												MyCalendar.FORMAT_DATETIME_STDATE,tz));
										sb.append("\",");
										sb.append("\"");
										sb.append(NO_REASON_FOR_MANUALLY_ADDED); sb.append("\"\r\n");
									}
									else {
										sb.append("\","); sb.append("\"");
										sb.append(smsSuppCont.getType());
										sb.append("\","); sb.append("\"");
										sb.append(MyCalendar.calendarToString(smsSuppCont.getSuppressedtime(),
												MyCalendar.FORMAT_DATETIME_STDATE,tz));
										sb.append("\",");sb.append("\"");
										/*sb.append(smsSuppCont.getType());sb.append("\"\r\n");*/
										sb.append((smsSuppCont.getReason() == null) ? "--": smsSuppCont.getReason());sb.append("\"\r\n");
									}
									
								}
								else if(type.equalsIgnoreCase(Constants.SMS_SUPP_TYPE_USERADDED)){
									sb.append("\","); sb.append("\"");
									sb.append(MyCalendar.calendarToString(smsSuppCont.getSuppressedtime(),
											MyCalendar.FORMAT_DATETIME_STDATE,tz));
									sb.append("\"\r\n");
								}
								else{
									sb.append("\","); sb.append("\"");
									sb.append(MyCalendar.calendarToString(smsSuppCont.getSuppressedtime(),
											MyCalendar.FORMAT_DATETIME_STDATE,tz));
									/*sb.append("\","); sb.append("\"");sb.append(smsSuppCont.getType()); sb.append("\"\r\n");*/
									sb.append("\","); sb.append("\"");sb.append((smsSuppCont.getReason() == null) ? "--": smsSuppCont.getReason()); sb.append("\"\r\n");
								}
						}
					}
					bw.write(sb.toString());
					smsSuppList = null;
					//System.gc();
				}

				bw.flush();
				bw.close();
				Filedownload.save(file, "text/plain");
			} catch (IOException e) {
				
			}
			logger.debug("-- exit --");
		}
	}//onClick$exportBtnId
	
	
	
	public class MyEventListener implements EventListener{
		
		
		public MyEventListener() {}
		
		@Override
		public void onEvent(Event event) throws Exception {

			Object target =  event.getTarget();
			
			if(target instanceof Paging) {
				
				Paging openPaging = (Paging)target;
				int desiredPage = openPaging.getActivePage();
				PagingEvent pagingEvent = (PagingEvent) event;
				int pSize = pagingEvent.getPageable().getPageSize();
				int ofs = desiredPage * pSize;
				
				String searchStr = "";
				if(!suppLstSearchTbId.getValue().equals("") && !suppLstSearchTbId.getValue().equalsIgnoreCase("Search Phone Number")) {
					
					searchStr = suppLstSearchTbId.getValue();
				}
				
				logger.info("starting indx==================="+ofs+"=================size==="+pSize);
				List<SMSSuppressedContacts> smsSuppList = smsSuppressedContactsDao.findAllByUsrId(GetUser.getUserId(), (String)supptypeLbId.getSelectedItem().getValue(), searchStr, ofs, pSize);
				redrawSMSSuppList(smsSuppList,ofs, pSize);
			}
			else if(target instanceof Checkbox){
				logger.info("Chkbox event happened for manually added section--------------------------------------------------------");
				Rows rows = suppContGridId.getRows();
				if(rows.getChildren().size() == 0) {
					//MessageUtil.setMessage("There is no contact to delete.","color:blue");
					return;
				}
				
				boolean displayActionsBandBox = false;
				boolean headerChkBoxChkd = ((Checkbox)suppContColsId.getFirstChild().getFirstChild()).isChecked();
				
				
				/*Iterator<Component> iterator = rows.getChildren().iterator();
				Row row;
				while(iterator.hasNext()){
					row = (Row)iterator.next();
					if (((Checkbox)suppContColsId.getFirstChild().getFirstChild()).isChecked()) {
						((Checkbox) row.getFirstChild()).setChecked(true);
						actionsBandBoxId.setDisabled(false);
					}
					else {
						((Checkbox) row.getFirstChild()).setChecked(false);
						actionsBandBoxId.setDisabled(true);
					}
				}*/
				
				
				Iterator<Component> iterator = rows.getChildren().iterator();
				Row row;
				while(iterator.hasNext()){
					row = (Row)iterator.next();
					
					
					if (headerChkBoxChkd) {
						((Checkbox) row.getFirstChild()).setChecked(true);
						displayActionsBandBox = true;
					}
					else {
						if(((Checkbox) row.getFirstChild()).isChecked()){
							((Checkbox) row.getFirstChild()).setChecked(true);
							displayActionsBandBox = true;
							
							if(headerCheckBoxPreviouslyChkd) {
								((Checkbox) row.getFirstChild()).setChecked(false);
								displayActionsBandBox = false;
							}
							
						}
						else{
							((Checkbox) row.getFirstChild()).setChecked(false);
						}
					}
					
					
				}//while loop ends
				
				if(headerChkBoxChkd){
					headerCheckBoxPreviouslyChkd = true;
				}else{
					headerCheckBoxPreviouslyChkd = false;
				}
				
				
				if(displayActionsBandBox){
					actionsBandBoxId.setDisabled(false);
				}else{
					actionsBandBoxId.setDisabled(true);
				}
				
			}
			else if(target instanceof Textbox){
				if (event.getName().equalsIgnoreCase("onOK")) {
					try {
						//String searchStr = suppLstSearchTbId.getValue();
						//logger.debug("Search string is :"+searchStr);
						logger.info("Text box onOk event.......................");
						int size = 0;
						size = getCount();
						
						int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
						suppContsPgId.setTotalSize(size);
						suppContsPgId.setPageSize(pageSize);
						suppContsPgId.setActivePage(0);
						
						List<SMSSuppressedContacts> suppContTypeList = getSMSSuppContactsList();
						redrawSMSSuppList(suppContTypeList,suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize());
						
					} catch (Exception e) {
						logger.error("Exception");
						
					}
				}
				else if(event.getName().equalsIgnoreCase("onFocus")){
					logger.info("Text box onFocus event.......................");
					suppLstSearchTbId.setValue("");
				}
				/*else if(event.getName().equalsIgnoreCase("onBlur")){
					suppLstSearchTbId.setValue("Search Phone Number");
					logger.info("Text box onBlur event.......................");
					try {
						//String searchStr = suppLstSearchTbId.getValue();
						//logger.debug("Search string is :"+searchStr);
						int size = 0;
						size = getCount();
						
						int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
						suppContsPgId.setTotalSize(size);
						suppContsPgId.setPageSize(pageSize);
						suppContsPgId.setActivePage(0);
						
						List<SMSSuppressedContacts> suppContTypeList = getSMSSuppContactsList();
						redrawSMSSuppList(suppContTypeList,suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize());
						
					} catch (Exception e) {
						logger.error("Exception");
						
					}
				 }*/
				/*else if(event.getName().equalsIgnoreCase("onChange")){
					logger.info("Text box onChange event.......................");
					try {
						String searchStr = suppLstSearchTbId.getValue();
						logger.debug("Search string is :"+searchStr);
						int size = 0;
						size = getCount();
						logger.info("size with search str = "+size);
						int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
						suppContsPgId.setTotalSize(size);
						suppContsPgId.setPageSize(pageSize);
						suppContsPgId.setActivePage(0);
						
						//suppLstSearchTbId.setValue(searchStr);
						List<SMSSuppressedContacts> suppContTypeList = getSMSSuppContactsList();
						redrawSMSSuppList(suppContTypeList,suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize());
						
					} catch (Exception e) {
						logger.error("Exception");
						
					}
				 }*/
			}
			
			else if(target instanceof Image) {
				
				
				logger.info("delete symbol 'x' is clicked......................................inside onEvent() method................... ");
				if(!supptypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(Constants.SMS_SUPP_TYPE_USERADDED)&& 
						!supptypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("all")) {
					return;
				}
				else {
					
						Image img = (Image)event.getTarget();
						Row selRow = (Row) img.getParent();
						List<Component> listOfComponents = selRow.getChildren();
						Label lbl = (Label)listOfComponents.get(1);
						
						if(supptypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("all")){
							logger.info("delete symbol 'x' is clicked......................................inside onEvent() method for suppType = 'All'................... ");
							List<Component> componentsList = selRow.getChildren();
							Label lblOfSuppressionType = (Label)componentsList.get(1);
							logger.info("lblOfSuppressionType.getValue()============================="+lblOfSuppressionType.getValue());
							
							
							if(lblOfSuppressionType.getValue().equalsIgnoreCase(MANUALLY_ADDED)){
								if (!(Messagebox.show("Are you sure you want to delete selected phone number from suppressed list?", "Confirm Delete", 
										Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) ==  Messagebox.YES)){
									return;
								}
							}
							else {
								MessageUtil.setMessage("To remove a phone number in \n'"+lblOfSuppressionType.getValue()+"'  category" +
										",\n please contact support team at support@optculture.com.","color:blue");
								return;
							}
						}
						else if(supptypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(Constants.SMS_SUPP_TYPE_USERADDED)){
							if (!(Messagebox.show("Are you sure you want to delete selected phone number from suppressed list?", "Confirm Delete", 
									Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) ==  Messagebox.YES)){
								return;
							}
						}
						//Rows rows = suppContGridId.getRows();
						
							try {
											
									smsSuppressedContactsDaoForDML.deleteById(Long.parseLong(selRow.getAttribute("contactId").toString()));
									selRow.setVisible(false);
													
								}catch(Exception e) {
											logger.error("Exception while deleting phone number.");
								}
									
								
	
							int size = getCount();
							int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
							suppContsPgId.setTotalSize(size);
							suppContsPgId.setPageSize(pageSize);
							
							if(size>0 && supptypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(Constants.SMS_SUPP_TYPE_USERADDED)){
								actionsBandBoxId.setDisabled(false);
							}
							else{
								actionsBandBoxId.setVisible(false);
								actionsBandBoxId.setDisabled(true);
							}
							
							List<SMSSuppressedContacts> smsSuppList = getSMSSuppContactsList();
							redrawSMSSuppList(smsSuppList, suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize() );
							MessageUtil.setMessage("Selected phone number removed from suppression list successfully.", "green", "top");
						
						
						
						
						
					} //else 
			
			
			
			

		}	
	}
	}
	
	
	private void resetGridCols(){
		Components.removeAllChildren(suppContColsId);
		String type = supptypeLbId.getSelectedItem().getValue(); 
		
		logger.info("inside resetGridCols()==================================");
		logger.info("type:"+type);
		
		
		/*if(type.equalsIgnoreCase(Constants.SMS_SUPP_TYPE_USERADDED)) {
			//suppContGridId.setWidth("70%");
			suppContGridId.setWidth("45%");
		}else{
			suppContGridId.setWidth("100%");
		}*/
		if (type.equalsIgnoreCase(Constants.SMS_SUPP_TYPE_USERADDED)) {

			Column chkboxCol = new Column();
			Checkbox chkbox = new Checkbox();
			chkbox.addEventListener("onCheck", new MyEventListener());
			chkbox.setParent(chkboxCol);
			//chkboxCol.setWidth("4%");
			chkboxCol.setWidth("3.5%");
			chkboxCol.setParent(suppContColsId);
		}
			Column phone = new Column();
			phone.setWidth("30%");
			if(type.equalsIgnoreCase(Constants.SMS_SUPP_TYPE_USERADDED)){
				phone.setWidth("95%");
				//phone.setWidth("66%");
			}
			Label phoneLabel =  new Label("Phone Number");
			phoneLabel.setStyle("margin-right:10px;");
			phoneLabel.setParent(phone);
			//email.setWidth("50%");
			if(suppLstSearchTbId == null) {
				logger.info("suppLstSearchTbId is null so creating new one...................................");
				suppLstSearchTbId = new Textbox("Search Phone Number");
				/*if(type.equalsIgnoreCase("all")){
					
					suppLstSearchTbId.setWidth("150px");
				}
				else{
					suppLstSearchTbId.setWidth("250px");
				}*/
				suppLstSearchTbId.setWidth("190px");
				suppLstSearchTbId.addEventListener("onOK", new MyEventListener());
				suppLstSearchTbId.addEventListener("onFocus", new MyEventListener());
				/*suppLstSearchTbId.addEventListener("onBlur", new MyEventListener());
				suppLstSearchTbId.addEventListener("onChange", new MyEventListener());*/
				
			}
			suppLstSearchTbId.setParent(phone);
			phone.setParent(suppContColsId);
			
			
			if(type.equalsIgnoreCase("all")){
				Column suppressionType = new Column("Suppression Type");
				suppressionType.setWidth("15%");
				suppressionType.setParent(suppContColsId);
				
			}
			
			if(type.equalsIgnoreCase(Constants.SMS_SUPP_TYPE_USERADDED)){
				Column timestamp = new Column("Timestamp");
				timestamp.setWidth("30%");
				timestamp.setParent(suppContColsId);
				
			} else {
				Column timestamp = new Column("Timestamp");
				timestamp.setWidth("15%");
				timestamp.setParent(suppContColsId);
				
			}
			
			
			
			
			
			if (!type.equalsIgnoreCase(Constants.SMS_SUPP_TYPE_USERADDED)) {
				Column reason = new Column("Reason");
				/*if(!type.equalsIgnoreCase("all")){
					reason.setWidth("75%");
				}else{
					
					reason.setWidth("55%");
				}*/
				reason.setWidth("55%");
				reason.setParent(suppContColsId);
			}
			if (type.equalsIgnoreCase("all") || type.equalsIgnoreCase(Constants.SMS_SUPP_TYPE_USERADDED)) {
				Column action = new Column("Action");
				/*if( type.equalsIgnoreCase(Constants.SMS_SUPP_TYPE_USERADDED)) {
					action.setWidth("11.11%");
				}else{
					action.setWidth("5%");
				}*/
				action.setWidth("5.2%");
				if(type.equalsIgnoreCase(Constants.SMS_SUPP_TYPE_USERADDED)) {
					action.setWidth("6%");
				}
				action.setParent(suppContColsId);
			}
		
	}
	
	public void onClick$addManuallyAnchId(){
		manualAdditionWinId$singleUserPhoneNumberTbId.setValue("Enter Phone Number");
		
		manualAdditionWinId$singlePhoneDivId.setVisible(true);
		manualAdditionWinId$bulkPhoneIdDivId.setVisible(false);
        manualAdditionWinId$successMsgDivId.setVisible(false);	
        manualAdditionWinId$manualAdditionChoice.setSelectedItem(manualAdditionWinId$rdoBtn1);
		manualAdditionWinId$singleUserPhoneNumberTbId.setValue("Enter Phone Number");
		manualAdditionWinId$errorMsgLblId.setValue("");
		manualAdditionWinId$selectedFileTbId.setValue("");
		manualAdditionWinId.setPosition("center");
		manualAdditionWinId.setVisible(true);
		manualAdditionWinId.doHighlighted();
		
		
	}
	
	public void onCheck$rdoBtn1$manualAdditionWinId(){
		manualAdditionWinId$singleUserPhoneNumberTbId.setValue("Enter Phone Number");
		manualAdditionWinId$errorMsgLblId.setValue("");
		manualAdditionWinId$singlePhoneDivId.setVisible(true);
		manualAdditionWinId$bulkPhoneIdDivId.setVisible(false);
		manualAdditionWinId$successMsgDivId.setVisible(false);
	}
    public void onCheck$rdoBtn2$manualAdditionWinId(){
    	//logger.info("radio event- occured-----2-------------------------------------------");
    	manualAdditionWinId$singleUserPhoneNumberTbId.setValue("Enter Phone Number");
    	manualAdditionWinId$selectedFileTbId.setValue("");
    	manualAdditionWinId$errorMsgLblId.setValue("");
    	manualAdditionWinId$singlePhoneDivId.setVisible(false);
		manualAdditionWinId$bulkPhoneIdDivId.setVisible(true);
		manualAdditionWinId$successMsgDivId.setVisible(false);	
    	
	}	
    
    public void onFocus$singleUserPhoneNumberTbId$manualAdditionWinId(){
    	manualAdditionWinId$singleUserPhoneNumberTbId.setValue("");
	}
    
    
    /*public void onClick$addSinglePhoneIdBtnId$manualAdditionWinId(){
    	String phone = manualAdditionWinId$singleUserPhoneNumberTbId.getValue();
        if(phone.trim().isEmpty() || phone.equalsIgnoreCase("Enter Phone Number")){
        	//manualAdditionWinId$errorMsgLblId.setValue("Textbox is empty.");
        	manualAdditionWinId$errorMsgLblId.setValue("Please enter a phone number.");
        	manualAdditionWinId$singleUserPhoneNumberTbId.setFocus(true);
        	return;
        }else if(!Utility.validateUserPhoneNum(phone.trim())){
        	//manualAdditionWinId$errorMsgLblId.setValue("Invalid phone number.");
        	manualAdditionWinId$errorMsgLblId.setValue("Please verify phone number you have entered.");
        	manualAdditionWinId$singleUserPhoneNumberTbId.setFocus(true);
        	return;
        }
        
        try {
        	SMSSuppressedContacts smsSuppressedContacts;   
        	smsSuppressedContacts = new SMSSuppressedContacts(GetUser.getUserObj(), phone, Constants.SMS_SUPP_TYPE_USERADDED);
        	smsSuppressedContacts.setSuppressedtime(Calendar.getInstance());
        	smsSuppressedContactsDao.saveOrUpdate(smsSuppressedContacts);
					
		} catch (Exception e) {
			manualAdditionWinId$errorMsgLblId.setStyle("color:red");
			manualAdditionWinId$errorMsgLblId.setValue("Unable to suppress the entered phone number.");
			manualAdditionWinId$singleUserPhoneNumberTbId.setFocus(true);
			return;
		}
        manualAdditionWinId$errorMsgLblId.setValue("");
        manualAdditionWinId$manualAdditionChoice.setSelectedItem(null);
        manualAdditionWinId$singlePhoneDivId.setVisible(false);
		manualAdditionWinId$bulkPhoneIdDivId.setVisible(false);
		//manualAdditionWinId$successMsgLblId.setValue("Phone number added successfully! You can view in the manually added section.");
		logger.info("here---------------");
		manualAdditionWinId$successMsgLblId.setValue("Phone number has been added successfully. You can view it in the manually added section.");
        manualAdditionWinId$successMsgDivId.setVisible(true);	
    }*/
    
    public void onClick$addSinglePhoneIdBtnId$manualAdditionWinId(){
    	String phone = manualAdditionWinId$singleUserPhoneNumberTbId.getValue();
    	//logger.debug(""+((Utility.phoneParse(phone.trim(),GetUser.getUserObj().getUserOrganization()))));
    	//logger.debug("!(Utility.phoneParse(phone.trim(),GetUser.getUserObj().getUserOrganization())==null)"+(!(Utility.phoneParse(phone.trim(),GetUser.getUserObj().getUserOrganization())==null)));
        if(phone.trim().isEmpty() || phone.equalsIgnoreCase("Enter Phone Number")){
        	//manualAdditionWinId$errorMsgLblId.setValue("Textbox is empty.");
        	manualAdditionWinId$errorMsgLblId.setValue("Please enter a phone number.");
        	manualAdditionWinId$singleUserPhoneNumberTbId.setFocus(true);
        	return;
        }else if((Utility.phoneParse(phone.trim(),GetUser.getUserObj().getUserOrganization())==null)){
        	//!Utility.validateUserPhoneNum(phone.trim())
        	//manualAdditionWinId$errorMsgLblId.setValue("Invalid phone number.");
        	manualAdditionWinId$errorMsgLblId.setValue("Please verify phone number you have entered.");
        	manualAdditionWinId$singleUserPhoneNumberTbId.setFocus(true);
        	return;
        }
        Users user = GetUser.getUserObj(); 
        //phone = phone.trim();
        phone = Utility.phoneParse(phone.trim(),user.getUserOrganization());
        String countryCarrier  = GetUser.getUserObj().getCountryCarrier()+Constants.STRING_NILL;
        if(phone.startsWith(countryCarrier) && 
        		phone.length() > GetUser.getUserObj().getUserOrganization().getMinNumberOfDigits()) {
			
        	//phone = phone.substring(countryCarrier.length() - 1);/// app
        	phone = phone.substring(countryCarrier.length());
			
			
		}
        
        List<SMSSuppressedContacts> retList =  smsSuppressedContactsDao.searchContactsByMultipleMobiles(GetUser.getUserObj().getUserId(), phone);
        List<SMSSuppressedContacts> finalList = new ArrayList<SMSSuppressedContacts>();
        
        try{
        	ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML) ServiceLocator.getInstance()
    				.getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
            	
        	if(retList != null && retList.size() > 0 ){
            	for(SMSSuppressedContacts aSmsSuppressedContact : retList){
            		
            		String suppMobile = aSmsSuppressedContact.getMobile();
					if(suppMobile.startsWith(countryCarrier)) {
						
						//suppMobile = suppMobile.substring(countryCarrier.length() - 1);
						suppMobile = suppMobile.substring(countryCarrier.length());
						
					}
					String phoneCompare= countryCarrier+phone;
					if(! phoneCompare.equals(aSmsSuppressedContact.getMobile())){
						aSmsSuppressedContact.setMobile(countryCarrier+suppMobile);
	            		finalList.add(aSmsSuppressedContact);	
					}else {
						manualAdditionWinId$errorMsgLblId.setStyle("color:red");
						manualAdditionWinId$errorMsgLblId.setValue("The given Phone number is already suppressed.");
						manualAdditionWinId$singleUserPhoneNumberTbId.setFocus(true);
						return;
					}
            	}
            }else{
            	SMSSuppressedContacts smsSuppressedContacts;   
            	smsSuppressedContacts = new SMSSuppressedContacts(GetUser.getUserObj(), countryCarrier+phone, Constants.SMS_SUPP_TYPE_USERADDED);
            	smsSuppressedContacts.setSuppressedtime(Calendar.getInstance());
            	finalList.add(smsSuppressedContacts);
            }
        	//APP-2441
        	contactsDaoForDML.updatemobileStatus (phone,Constants.CONT_STATUS_SUPPRESSED, user);
        	
        }catch(Exception e){
        	manualAdditionWinId$errorMsgLblId.setStyle("color:red");
			manualAdditionWinId$errorMsgLblId.setValue("Unable to suppress the entered phone number.");
			manualAdditionWinId$singleUserPhoneNumberTbId.setFocus(true);
			return;
        	
        }
        
        
        smsSuppressedContactsDaoForDML.saveByCollection(finalList);
        manualAdditionWinId$errorMsgLblId.setValue("");
        manualAdditionWinId$manualAdditionChoice.setSelectedItem(null);
        manualAdditionWinId$singlePhoneDivId.setVisible(false);
		manualAdditionWinId$bulkPhoneIdDivId.setVisible(false);
		logger.info("here---------------");
		manualAdditionWinId$successMsgLblId.setValue("Phone number has been added successfully. You can view it in the manually added section.");
        manualAdditionWinId$successMsgDivId.setVisible(true);	
    }
    public void onClick$cancelBtnId$manualAdditionWinId(){
    	manualAdditionWinId.setVisible(false);
    }
    
    
}
