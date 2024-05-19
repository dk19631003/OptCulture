package org.mq.marketer.campaign.controller.contacts;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.clapper.util.io.FileUtil;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.MessageHandler;
import org.mq.marketer.campaign.dao.MessagesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.general.Redirect;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

@SuppressWarnings( { "serial", "unchecked", "unused" })
public class UploadCSVFileController extends GenericForwardComposer {

	Session session = null;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private Label fieldListLbId,selectedListsLblId;
	private List fieldList = null;
	private List fieldIndexList = null;
	private Map cfMap = null;
	private Map genFieldMap = null;
	private boolean isNew = false;
	private String isNewML = null;
	private Set<MailingList> mailingLists = null;
	Media media = null;
	
	private Textbox selectedFileTbId;
	private Div fileUploadDivId,uploadResultDivId;
	
	public UploadCSVFileController() {
		session = Sessions.getCurrent();
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		MessageUtil.clearMessage();
		fieldList = (List) session.getAttribute("fieldList");
		
		
		fieldIndexList = (List) session.getAttribute("fieldIndexList");
		cfMap = (Map) session.getAttribute("cfMap");
		genFieldMap = (Map)session.getAttribute("genFieldMap");
		
		isNewML = (String) session.getAttribute("isNewML");
		if (isNewML != null)
			if (isNewML.equals("true"))
				isNew = true;
			else
				isNew = false;
		String selectedFields = "";
		Set keys = null;
		if (fieldList != null) {
			if(logger.isDebugEnabled()) logger.debug(fieldList.get(0));
			if(cfMap != null){
				if(logger.isDebugEnabled()) logger.debug(" No Of Custom Fields : " + cfMap.size());
				keys = cfMap.keySet();
			}
			for (int i = 0; i < fieldList.size(); i++) {
				if(logger.isDebugEnabled()) logger.debug("the " + i + " field is " + fieldList.get(i));
				if (!((String) fieldList.get(i)).contains("Custom Field"))
					selectedFields += (String) fieldList.get(i) + ", ";
				else{
					if (cfMap != null && keys != null) {
						for (Object obj : keys) {
							String key = (String) obj;
							if (key .equals((String) fieldList.get(i))) {
								String value = (String) cfMap.get(key);
								String[] temp = StringUtils.split(value,":");
								selectedFields += key + ": " + temp[1] + "("	+ temp[2] + ")" + ", ";
							}
						}
					}
				}
			}
			if ((selectedFields.charAt(selectedFields.length() - 2)) == ',')
				selectedFields = selectedFields.substring(0,
						(selectedFields.length() - 2));
		}
		if(logger.isDebugEnabled()) logger.debug(" selectedFields : "+selectedFields);
		fieldListLbId.setValue(selectedFields);
		mailingLists = (Set<MailingList>) session.getAttribute("uploadFile_Ml");
		String listNmaes = "";
		for(MailingList ml : mailingLists) {
			
			if(listNmaes.length()>0) listNmaes += ", ";
			
			listNmaes += ml.getListName();
			
			
		}
		selectedListsLblId.setValue(listNmaes);
		UserActivitiesDao userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
		UserActivitiesDaoForDML userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		/*if(userActivitiesDao != null) {
			userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CONTACT_UPLOAD_FILE,GetUser.getUserObj());
		}*/
		if(userActivitiesDaoForDML != null) {
			userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CONTACT_UPLOAD_FILE,GetUser.getLoginUserObj());
		}
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
     	PageUtil.setHeader("Add / Import Contacts","",style,true);
		
		
	}
	/*
	public void init(Label fieldListLbId){
		MessageUtil.clearMessage();
		this.fieldListLbId = fieldListLbId;
		fieldList = (List) session.getAttribute("fieldList");
		fieldIndexList = (List) session.getAttribute("fieldIndexList");
		cfMap = (Map) session.getAttribute("cfMap");
		isNewML = (String) session.getAttribute("isNewML");
		if (isNewML != null)
			if (isNewML.equals("true"))
				isNew = true;
			else
				isNew = false;
		String selectedFields = "";
		Set keys = null;
		if (fieldList != null) {
			if(logger.isDebugEnabled()) logger.debug(fieldList.get(0));
			if(cfMap != null){
				if(logger.isDebugEnabled()) logger.debug(" No Of Custom Fields : " + cfMap.size());
				keys = cfMap.keySet();
			}
			for (int i = 0; i < fieldList.size(); i++) {
				if(logger.isDebugEnabled()) logger.debug("the " + i + " field is " + fieldList.get(i));
				if (!((String) fieldList.get(i)).contains("Custom Field"))
					selectedFields += (String) fieldList.get(i) + ", ";
				else{
					if (cfMap != null && keys != null) {
						for (Object obj : keys) {
							String key = (String) obj;
							if (key == (String) fieldList.get(i)) {
								String value = (String) cfMap.get(key);
								String[] temp = StringUtils.split(value,":");
								selectedFields += key + ": " + temp[1] + "("	+ temp[2] + ")" + ", ";
							}
						}
					}
				}
			}
			if ((selectedFields.charAt(selectedFields.length() - 2)) == ',')
				selectedFields = selectedFields.substring(0,
						(selectedFields.length() - 2));
		}
		if(logger.isDebugEnabled()) logger.debug(" selectedFields : "+selectedFields);
		fieldListLbId.setValue(selectedFields);
		mailingLists = (Set) session.getAttribute("uploadFile_Ml");
		
		UserActivitiesDao userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
		if(userActivitiesDao != null) {
			userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CONTACT_UPLOAD_FILE,GetUser.getUserObj());
		}
	} //init
*/
	public void uploadCSVFile(Object media,Div fileUploadDivId,Div uploadResultDivId) {
		try {
			if(logger.isDebugEnabled()) logger.debug("-- just entered--");
			MessageUtil.clearMessage();
			Media m = (Media)media;
			String path = PropertyUtil.getPropertyValue("usersParentDirectory").trim() + "/" + GetUser.getUserName() + "/List/" + m.getName();
			boolean isSuccess = copyDataFromMediaToFile(path,m);
			if(logger.isDebugEnabled()) logger.debug("Is copy of the file successfull :"+isSuccess);
			if(!isSuccess){
				if(logger.isDebugEnabled()) logger.debug("Couldnot copy the file from Media");
				return;
			}
			if(logger.isDebugEnabled()) logger.debug("File copied from media is successfull :" + mailingLists.size());
			UploadCSVFile uploadCSVFile = (UploadCSVFile)SpringUtil.getBean("uploadCSVFile");
			
			Boolean isPurgeContacts = (Boolean)session.getAttribute("isPurgeContacts");
			isPurgeContacts = (isPurgeContacts==null) ? false : isPurgeContacts;
			PurgeList purgeList = (PurgeList)SpringUtil.getBean("purgeList");
			
			Object[] obj = {GetUser.getUserObj(), new Boolean(isNew), mailingLists, path, fieldIndexList, cfMap, purgeList, isPurgeContacts,genFieldMap};
			
			if(logger.isDebugEnabled()) logger.debug("Is uploadCSVFile thread running : " + uploadCSVFile.isRunning);
			
			synchronized(uploadCSVFile) {
				uploadCSVFile.uploadQueue.add(obj);
				if(!uploadCSVFile.isRunning){
					Thread thread = new Thread(uploadCSVFile);
					thread.start();
				}
			}
			
			session.removeAttribute("isPurgeContacts");
			session.removeAttribute("uploadFile_Ml");
			session.removeAttribute("isNewML");
			fileUploadDivId.setVisible(false);
			uploadResultDivId.setVisible(true);
			//MessageUtil.setMessage("File will be uploaded in a moment.", "color:blue", "TOP");
			//Redirect.goTo("RMHome");
		} catch (Exception e) {
			logger.error("** Exception : while creating the background process for uploading contact file "+e+" **");
		}

	}
	
	public void close(){
		Redirect.goTo(PageListEnum.RM_HOME);
	}
	
	public boolean copyDataFromMediaToFile(String path,Media m){
		MessagesDao messagesDao = (MessagesDao) SpringUtil.getBean("messagesDao");
		String ext = FileUtil.getFileNameExtension(path);
		File file = new File(path);
		BufferedReader br = null;
		BufferedWriter bw = null ;
		if(!ext.equalsIgnoreCase("csv")){
			MessageUtil.setMessage("Upload .csv file only.","color:red","BOTTOM");
			return false;
		}
		try{
			if(logger.isDebugEnabled()) logger.debug("reading data from media using getReaderData()");
			br = new BufferedReader((InputStreamReader)m.getReaderData());
			bw = new BufferedWriter(new FileWriter(path));
			String line = "";
			while((line=br.readLine())!=null){
				bw.write(line);
				bw.newLine();
			}
			bw.flush();
			bw.close();
			br.close();
			return true;
		}catch(Exception e1){
			logger.error("** Exception is " + e1.getMessage()+" :trying to read with Media.getStringData() **");
			try{
				if(logger.isDebugEnabled()) logger.debug("Reading file with Media.getStringData()");
				String data = m.getStringData(); 
				FileUtils.writeStringToFile(file, data);
				return true;
			}catch(Exception e2){
				logger.error("** Exception is " + e2 +" :trying to read as Streams **");
				try {
					FileOutputStream out = new FileOutputStream (file);
					BufferedInputStream in = new BufferedInputStream((FileInputStream)m.getStreamData());
					byte[] buf = new byte[1024];
					int count = 0;
					while ((count = in.read(buf)) >= 0) {
						out.write(buf, 0, count);
					}
					out.flush();
					in.close();
					out.close();
					return true;
				} catch (FileNotFoundException e) {
					logger.error("** Exception is : File not found **");
				} catch (Exception e3) {
					logger.error("** Exception is " + e3 +"  so trying to read as bytes **");
					try {
						byte[] data = m.getByteData();
						FileOutputStream fos = new FileOutputStream(file);
						fos.write(data);
						fos.flush();
						fos.close(); 
						return true;
					} catch (Exception e) {
						logger.error("** Exception is " + e +" **");
					}
				}
				String message = "CSV file upload failed,"+m.getName()+"\n could not copied reason may be due to network problem or may be very large file";
				Users user = GetUser.getUserObj();
				(new MessageHandler(messagesDao,user.getUserName())).sendMessage("Contact","uploaded failed",message,"Inbox",false,"INFO", user);
				return false;
			}
			
		}
	}


	public void onClick$closeBtnId() {
		try {
			close();
		} catch (Exception e) {
			logger.error("** Exception  : from the Close method",e);
		}
	} // onClick$closeBtnId
	
	public void onClick$backBtnId() {
		PageUtil.goToPreviousPage();
	} //onClick$backBtnId()
	
	public void onUpload$brouseUploadBtnId(UploadEvent event) {
		try {
			logger.info("Browse is called");
			media = event.getMedia();
			selectedFileTbId.setValue(media.getName());
//		media = m;
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
	}
	
	
	public void onClick$uploadBtnId() {
		try {
			upload();
		} catch (Exception e) {
			logger.error("*******Exception error getting from the upload method ::",e);
		}
	}
	
	private void upload()throws Exception {
		MessageUtil.clearMessage();
		if(media == null) {
			MessageUtil.setMessage("Please select a file.", "color:red", "TOP");
			return;
		}
		String path = PropertyUtil.getPropertyValue("usersParentDirectory").trim() + "/" + sessionScope.get("userName") + "/List/" +((Media)media).getName();
		String ext = FileUtil.getFileNameExtension(path);
		if(ext == null){
			MessageUtil.setMessage("Upload .csv file only.","color:red","TOP");
			return;
		}
		if(!ext.equalsIgnoreCase("csv")){
			MessageUtil.setMessage("Upload .csv file only.","color:red","TOP");
			return;
		}
		uploadCSVFile((Media)media,fileUploadDivId,uploadResultDivId);
		media = null;
	} // upload()
	
	
//	public void 
}
