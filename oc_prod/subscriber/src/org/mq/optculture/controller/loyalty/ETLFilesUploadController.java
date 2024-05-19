package org.mq.optculture.controller.loyalty;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.clapper.util.io.FileUtil;
import org.mq.marketer.campaign.beans.ETLFileUploadLogs;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.MessageHandler;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.ETLFileUploadLogDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.util.media.Media;
import org.zkoss.zhtml.Fileupload;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ETLFilesUploadController extends GenericForwardComposer<Component> {

	private Users currUser;
	
	public ETLFilesUploadController() {
		currUser = GetUser.getUserObj();
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Import XML/ZIP Files", "", style, true);
	}

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private Textbox selectedFileTbId;
	private Window viewFileLogsWinId;
	private Grid viewFileLogsWinId$viewLogsGridId;
	
	private Listbox defaultDatesId;
	private Div datesDivId;
	private MyDatebox fromDateboxId,toDateboxId;
	
	private  ETLFileUploadLogDao  etlFileUploadLogDao;
	
	private String strMaxFileSize=PropertyUtil.getPropertyValue("fbb_max_size");
	
	@Override
	public void doAfterCompose(Component comp) {

		try {
			super.doAfterCompose(comp);
		
			MessageUtil.clearMessage();
			etlFileUploadLogDao = (ETLFileUploadLogDao)SpringUtil.getBean("etlFileUploadLogDao");

			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Long orgOwnerId = usersDao.getOwnerofOrg(currUser.getUserOrganization().getUserOrgId());
			Users orgOwner = usersDao.findByUserId(orgOwnerId);
			if(orgOwner.getUserId() != currUser.getUserId()){
				currUser = orgOwner;//?for APP-2120
			}
			
			onClick$cancelBtnId();
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	}
	
	public void onUpload$uploadBtnId(UploadEvent uploadEvent){
		Media media=uploadEvent.getMedia();
		
		logger.info("event========="+uploadEvent);
		//Media media = Fileupload.get();
		
		try
		{
			int maxSize = Integer.parseInt(strMaxFileSize);
			int size=0;
			if(media.isBinary())
			{
				size = media.getStreamData().available(); //for zip file
			}
			else
			{
				size = media.getStringData().length();   //for xml file
			}
			logger.info("FBB File size ===" + size);
			if(size>maxSize)
			{
				MessageUtil.setMessage("Upload below 20MB file only", "color:red", "TOP");
				return;
			}
		}catch(Exception e) {
			logger.error("Exception ::" , e);
		}
		MessageUtil.clearMessage();
		if (media == null) {
			MessageUtil.setMessage("Please select a file.", "color:red", "TOP");
			return;
		}
		//String path = PropertyUtil.getPropertyValue("usersParentDirectory").trim() + "/" + sessionScope.get("userName")
		String path = PropertyUtil.getPropertyValue("usersParentDirectory").trim() + "/" + currUser.getUserName()
			+ "/List/" + ((Media) media).getName();
		String xmlFilesPath = PropertyUtil.getPropertyValue("ETLFileSource");
		String ext = FileUtil.getFileNameExtension(path);
		
		String fileNameNoExt = FileUtil.getFileNameNoExtension(new File(media.getName()).getName());
		String strFileName = fileNameNoExt  +"."+ext;
		//Path etlPath = Paths.get(xmlFilesPath,sessionScope.get("userName").toString(),"source","files", strFileName);
		Path etlPath = Paths.get(xmlFilesPath,currUser.getUserName(),"source","files", strFileName);

		
		logger.info("path===>" + path);
	    if (ext == null) {
			MessageUtil.setMessage("Upload .xml/zip file only.", "color:red", "TOP");
			return;
		}
		if (!(ext.equalsIgnoreCase("xml")||ext.equalsIgnoreCase("zip"))) {
			MessageUtil.setMessage("Upload .xml/zip file only.", "color:red", "TOP");
			return;
		}
		
		boolean isSuccess = copyDataFromMediaToFile(etlPath.toString(), media);
		selectedFileTbId.setValue(media.getName());
		strFileName = media.getName();
		logger.info("media.getName()===>" + media.getName());
		selectedFileTbId.setDisabled(true);
		media = null;
		if (isSuccess) {
			MessageUtil.setMessage("File uploaded successfully.", "color:blue", "TOP");
		   
		}
		else
		{
			String message = "XML/ZIP file upload failed," + strFileName
					+ "\n could not copied reason may be due to network problem or may be very large file";
					
					MessageUtil.setMessage(message, "color:red", "TOP");
		}
		
		
		
		
		
	}
	
	public void onClick$cancelBtnId(){
		selectedFileTbId.setValue("");
		selectedFileTbId.setDisabled(false);
		
	}

	/*public void onClick$uploadBtnId()
	{
		Media media = Fileupload.get();
		
	try
	{
	    int maxSize = Integer.parseInt(strMaxFileSize);
	     	int size=0;
	    if(media.isBinary())
	    {
	    	size = media.getStreamData().available(); //for zip file
	    }
	    else
	    {
		   size = media.getStringData().length();   //for xml file
	    }
	    logger.info("FBB File size ===" + size);
		if(size>maxSize)
		{
			MessageUtil.setMessage("Upload below 20MB file only", "color:red", "TOP");
			return;
		}
	}catch(Exception e) {logger.error("Exception ::" , e);}
		MessageUtil.clearMessage();
		if (media == null) {
			MessageUtil.setMessage("Please select a file.", "color:red", "TOP");
			return;
		}
		//String path = PropertyUtil.getPropertyValue("usersParentDirectory").trim() + "/" + sessionScope.get("userName")
		String path = PropertyUtil.getPropertyValue("usersParentDirectory").trim() + "/" + currUser.getUserName()
			+ "/List/" + ((Media) media).getName();
		String xmlFilesPath = PropertyUtil.getPropertyValue("ETLFileSource");
		String ext = FileUtil.getFileNameExtension(path);
		
		String fileNameNoExt = FileUtil.getFileNameNoExtension(new File(media.getName()).getName());
		String strFileName = fileNameNoExt  +"."+ext;
		//Path etlPath = Paths.get(xmlFilesPath,sessionScope.get("userName").toString(),"source","files", strFileName);
		Path etlPath = Paths.get(xmlFilesPath,currUser.getUserName(),"source","files", strFileName);

		
		logger.info("path===>" + path);
	    if (ext == null) {
			MessageUtil.setMessage("Upload .xml/zip file only.", "color:red", "TOP");
			return;
		}
		if (!(ext.equalsIgnoreCase("xml")||ext.equalsIgnoreCase("zip"))) {
			MessageUtil.setMessage("Upload .xml/zip file only.", "color:red", "TOP");
			return;
		}
		
		boolean isSuccess = copyDataFromMediaToFile(etlPath.toString(), media);
		selectedFileTbId.setValue(media.getName());
		strFileName = media.getName();
		logger.info("media.getName()===>" + media.getName());
		selectedFileTbId.setDisabled(true);
		media = null;
		if (isSuccess) {
			MessageUtil.setMessage("File uploaded successfully.", "color:blue", "TOP");
		   
		}
		else
		{
			String message = "XML/ZIP file upload failed," + strFileName
					+ "\n could not copied reason may be due to network problem or may be very large file";
					
					MessageUtil.setMessage(message, "color:red", "TOP");
		}
		
		}*/
	/*public boolean copyDataFromMediaToFile(Path path, Media media)
	{
		
		try
		{
		byte[] data;
		if (media.isBinary()){
			File file = new File(path.toString());
			FileOutputStream out = new FileOutputStream(file);
			BufferedInputStream in = new BufferedInputStream((FileInputStream) media.getStreamData());
			byte[] buf = new byte[1024];
			int count = 0;
			while ((count = in.read(buf)) >= 0) {
				out.write(buf, 0, count);
			}
			out.flush();
			in.close();
			out.close();
		    return true;
			
			BufferedInputStream in = new BufferedInputStream((FileInputStream) media.getStreamData());
			data = IOUtils.toByteArray(in);

			//data = IOUtils.toByteArray(media.getStreamData());
		}
		else
			data = media.getStringData().getBytes(Charset.forName("UTF-8"));
		
		Files.write(path, data);
		return true;
		}catch(Exception e)
		{
			logger.error("Exception=="+e);
			
		}
		
		String message = "XML/ZIP file upload failed," + media.getName()
				+ "\n could not copied reason may be due to network problem or may be very large file";

		MessageHandler messageHanlder = new MessageHandler(); // to save the error in messages table
		messageHanlder.sendMessage("XML File", "upload failed", message, "Inbox", false, "INFO", currUser);
		return false;
	}*/
	
	/*public boolean copyDataFromMediaToFile1(String path, Media m)
	{
		File file = new File(path);
	
		if(m.isBinary()) //for zip files
		{
			try
			{
				FileOutputStream out = new FileOutputStream(file);
				BufferedInputStream in = new BufferedInputStream((FileInputStream) m.getStreamData());
				byte[] buf = new byte[1024];
				int count = 0;
				while ((count = in.read(buf)) >= 0) {
					out.write(buf, 0, count);
				}
				out.flush();
				in.close();
				out.close();
			    return true;
			}catch(Exception e)
			{
				logger.info("Exception=="+e);
				
			}
			
		}
		else            //other than zip files
		{
			try
			{
			String data = m.getStringData();
			FileUtils.writeStringToFile(file, data);
			return true;
			}catch(Exception e)
			{
				logger.info("Exception=="+e);
				
			}
		}
		
		String message = "XML/ZIP file upload failed," + m.getName()
				+ "\n could not copied reason may be due to network problem or may be very large file";

		MessageHandler messageHanlder = new MessageHandler();  // to save the error in messages table
		messageHanlder.sendMessage("XML File", "upload failed", message, "Inbox", false, "INFO", currUser);
		return false;

	}
	*/
	public boolean copyDataFromMediaToFile(String path, Media m) {
		
		String ext = FileUtil.getFileNameExtension(path);
		File file = new File(path);
		BufferedReader br = null;
		BufferedWriter bw = null;
		if (!(ext.equalsIgnoreCase("xml")||ext.equalsIgnoreCase("zip"))) {
			MessageUtil.setMessage("Upload .xml/zip file only.", "color:red", "BOTTOM");
			return false;
		}
		try {
			br = new BufferedReader((InputStreamReader) m.getReaderData());
			bw = new BufferedWriter(new FileWriter(path));
			String line = "";
			while ((line = br.readLine()) != null) {
				bw.write(line);
				bw.newLine();
			}
			bw.flush();
			bw.close();
			br.close();
			return true;
		} catch (Exception e1) {
			try {
				//logger.info("Exception1=="+e1);
				String data = m.getStringData();
				FileUtils.writeStringToFile(file, data);
				return true;
			} catch (Exception e2) {
				
				//logger.info("Exception2=="+e2);
				try {
					FileOutputStream out = new FileOutputStream(file);
					BufferedInputStream in = new BufferedInputStream((FileInputStream) m.getStreamData());
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
					
					//logger.info("Exceptione=="+e);
			
				} catch (Exception e3) {
					
					//logger.info("Exception3=="+e3);
				
					try {
						byte[] data = m.getByteData();
						FileOutputStream fos = new FileOutputStream(file);
						fos.write(data);
						fos.flush();
						fos.close();
						return true;
					} catch (Exception e) {
						//logger.info("Exception=="+e);
					}
				}
				String message = "XML/ZIP file upload failed," + m.getName()
						+ "\n could not copied reason may be due to network problem or may be very large file";
				
				MessageHandler messageHanlder = new MessageHandler();
				messageHanlder.sendMessage("XML File", "upload failed", message,
						"Inbox", false, "INFO", currUser);
				return false; 
			} 

			
		}
	} 
	
	
	
	/**
	 * This method captures on select event of time duration listBox
	 */
	public void onSelect$defaultDatesId() {
		logger.debug(">>>>>>>>>>>>> onSelect$tdefaultDatesId");
		

		if(defaultDatesId.getSelectedItem().getLabel().equalsIgnoreCase("Custom Dates")) {
			datesDivId.setVisible(true);
			fromDateboxId.setText("");
			toDateboxId.setText("");
		}
		else {
			datesDivId.setVisible(false);
			
			
		}
		logger.debug("<<<<<<<<<<<<< completed onSelect$tdefaultDatesId ");
	}//onSelect$defaultdatesId
	
	private Calendar startDate,endDate;
	private boolean getDateValues(TimeZone clientTimeZone) {
		logger.debug(">>>>>>>>>>>>> entered in getDateValues");
		endDate = new MyCalendar(clientTimeZone);
		endDate.set(MyCalendar.HOUR_OF_DAY, 23);
		endDate.set(MyCalendar.MINUTE, 59);
		endDate.set(MyCalendar.SECOND, 59);

		startDate = new MyCalendar(clientTimeZone);
		startDate.set(MyCalendar.HOUR_OF_DAY, 00);
		startDate.set(MyCalendar.MINUTE, 00);
		startDate.set(MyCalendar.SECOND, 00);

		
		String strOption = defaultDatesId.getSelectedItem().getLabel().toLowerCase();
		logger.info("selected label :"+ strOption);
		
		switch(strOption)
		{
		case "last 30 days": 
			int monthsDiff = 30;
			startDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - monthsDiff);
			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - 1);
			break;
		case "today": 
			startDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE));
			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE));
			break;
		case "one week": 
			int days = 7;
			startDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - days);
			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE));
		break;
	
		
		case "custom dates": 
			startDate = getStartDate();
			endDate = getEndDate();
	        if(startDate==null || endDate==null) return false;
	        if(startDate.getTimeInMillis() > endDate.getTimeInMillis()){
	    	  MessageUtil.setMessage("From date should be less than end date.", "color:red", "TOP");
	          return false;
	         }
			break;
		}
		
	
		logger.info("Starting date "+startDate);
		logger.info("End date "+endDate);
		logger.debug("<<<<<<<<<<<<< completed getDateValues ");
		return true;
	}//getDateValues

	private Calendar getStartDate(){
		logger.debug(">>>>>>>>>>>>> entered in getStartDate");
		if(fromDateboxId.getValue() != null && !fromDateboxId.getValue().toString().isEmpty()) {
			Calendar serverFromDateCal = fromDateboxId.getServerValue();
			Calendar tempClientFromCal = fromDateboxId.getClientValue();
			serverFromDateCal.set(Calendar.HOUR_OF_DAY, 
					serverFromDateCal.get(Calendar.HOUR_OF_DAY)-tempClientFromCal.get(Calendar.HOUR_OF_DAY));
			serverFromDateCal.set(Calendar.MINUTE, 
					serverFromDateCal.get(Calendar.MINUTE)-tempClientFromCal.get(Calendar.MINUTE));
			serverFromDateCal.set(Calendar.SECOND, 0);

			logger.debug("<<<<<<<<<<<<< completed getStartDate ");
			return serverFromDateCal;
		}
		else {
			MessageUtil.setMessage("From date cannot be empty.", "color:red", "TOP");
			logger.debug("<<<<<<<<<<<<< completed getStartDate ");
			return null;
		}
	}//getStartDate

	/**
	 * This method gets the end date
	 * @return serverToDateCal
	 */
	private Calendar getEndDate() {
		logger.debug(">>>>>>>>>>>>> entered in getEndDate");
		if(toDateboxId.getValue() != null && !toDateboxId.getValue().toString().isEmpty()) {
			Calendar serverToDateCal = toDateboxId.getServerValue();
			Calendar tempClientToCal = toDateboxId.getClientValue();

			serverToDateCal.set(Calendar.HOUR_OF_DAY, 
					23+serverToDateCal.get(Calendar.HOUR_OF_DAY)-tempClientToCal.get(Calendar.HOUR_OF_DAY));
			serverToDateCal.set(Calendar.MINUTE, 
					59+serverToDateCal.get(Calendar.MINUTE)-tempClientToCal.get(Calendar.MINUTE));
			serverToDateCal.set(Calendar.SECOND, 59);

			logger.debug("<<<<<<<<<<<<< completed getEndDate ");
			return serverToDateCal;
		}
		else{
			MessageUtil.setMessage("To date cannot be empty.", "color:red", "TOP");
			logger.debug("<<<<<<<<<<<<< completed getEndDate ");
			return null;
		}
	}//getEndDate

	
	
	
	public void onClick$viewLogsAId() {
		try {
			TimeZone clientTimeZone = (TimeZone)sessionScope.get("clientTimeZone"); 
			boolean datesAvailable = getDateValues(clientTimeZone);
			if(!datesAvailable) return;
			List<ETLFileUploadLogs> list = etlFileUploadLogDao.findAllByIdWithDateRange(currUser.getUserId(), MyCalendar.calendarToString(startDate, null), MyCalendar.calendarToString(endDate, null));
			Components.removeAllChildren(viewFileLogsWinId$viewLogsGridId);
			 
			 viewFileLogsWinId.setVisible(true);
			 viewFileLogsWinId.doHighlighted();
			 viewFileLogsWinId.setPosition("center");
			 
			 Row row1 = new Row();
			 Rows rows1 = new Rows();
			 Label lbl1 = new Label("File Name");
			 lbl1.setStyle("font-weight: bold;");
			 lbl1.setParent(row1);
			 lbl1 = new Label("Time Of Upload");
			 lbl1.setStyle("font-weight: bold;");
			 lbl1.setParent(row1);
			 
			 lbl1 = new Label("No Of Records");
			 lbl1.setStyle("font-weight: bold;");
			 lbl1.setParent(row1);
			 
			 lbl1 = new Label("File Status");
			 lbl1.setStyle("font-weight: bold;");
			 lbl1.setParent(row1);
			 
			 lbl1 = new Label("Comments");
			 lbl1.setStyle("font-weight: bold;");
			 lbl1.setParent(row1);
			 
			 lbl1 = new Label("Download the xml file");
			 lbl1.setStyle("font-weight: bold;");
			 lbl1.setParent(row1);
			 
			 lbl1 = new Label("Download the Receipt Details(.xls)");
			 lbl1.setStyle("font-weight: bold;");
			 lbl1.setParent(row1);
			 
			 
			 row1.setParent(rows1);
			 rows1.setParent(viewFileLogsWinId$viewLogsGridId);
			 
			 if(list != null && list.size() > 0) {
				 logger.debug("Log size : "+ list.size());
				 
				 for (ETLFileUploadLogs fileUploadLogs : list) {
					 
					 Row row = new Row();
					 Label lbl = new Label(fileUploadLogs.getFileName());
				
					 lbl.setParent(row);
					 lbl = new Label(MyCalendar.calendarToString(fileUploadLogs.getUploadTime(),MyCalendar.FORMAT_DATE_FORMAT,clientTimeZone));
					 lbl.setParent(row);
					 
					 lbl = new Label(fileUploadLogs.getRecordCount()+"");
					 lbl.setParent(row);
					 
					 lbl = new Label(fileUploadLogs.getFileStatus());
					 lbl.setParent(row);
					 row.setParent(rows1);
					 
					 lbl = new Label(fileUploadLogs.getComments());
					 lbl.setParent(row);
					 row.setParent(rows1);
					 
					 Button btnDownload = new Button("Download");
					 /*btnDownload.addEventListener("onClick", new EventListener<Event>() {

						@Override
						public void onEvent(Event arg0)  {
							try
							{
							   Path filePath = Paths.get(fileUploadLogs.getFileDownload());
							   URL url = new URL(fileUploadLogs.getFileDownload());
							   String outFile = Paths.get(System.getProperty("user.home"), filePath.getFileName().toString()).toString();
							  try (FileOutputStream fileOutputStream = new FileOutputStream(outFile);
							       FileChannel fileChannel = fileOutputStream.getChannel();
							 	   InputStream istream = url.openStream();
							 	   ReadableByteChannel readableByteChannel = Channels.newChannel(istream);) 
							      {
								    fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
								    logger.info(outFile+" downloaded");
								    alert("File Saved successfully at "+outFile);
    							  }
							}catch(Exception e)
							{
								logger.error("Exception ::" , e);
							}
						}
					});*/
					
					 btnDownload.addEventListener("onClick", new EventListener<Event>() {

						@Override
						public void onEvent(Event arg0)  {
							try
							{
								if(fileUploadLogs.getProcessedFilePath()==null)
								{
									alert ("File Not Found..");
									return;
								}
						    Path filePath = Paths.get(fileUploadLogs.getProcessedFilePath());
						    if(!Files.exists(filePath))
						    {
						    	alert ("File Not Found");
						    	return;
						    }
							File file = new File(filePath.toString());
							Filedownload.save(file,null);
							}catch (Exception e)
							{
								e.printStackTrace();
							}
						}});
						 
					btnDownload.setParent(row);
					row.setParent(rows1);
					 
					 
					
					 Button btnXlsDownload = new Button("Download");
					 btnXlsDownload.addEventListener("onClick", new EventListener<Event>() {

							@Override
							public void onEvent(Event arg0)  {
								try
								{
									if(fileUploadLogs.getReceiptDetailsPath()==null)
									{
										alert ("File Not Found..");
										return;
									}
							    Path filePath = Paths.get(fileUploadLogs.getReceiptDetailsPath());
							    if(!Files.exists(filePath))
							    {
							    	alert ("File Not Found"); 
							    	return;
							    }
								File file = new File(filePath.toString());
								Filedownload.save(file,null);
								}catch (Exception e)
								{
									e.printStackTrace();
								}
							}});
							 
					    btnXlsDownload.setParent(row);
						row.setParent(rows1);
						 
						 
				}
				 
				 rows1.setParent(viewFileLogsWinId$viewLogsGridId);
			 } else {
				 
				 viewFileLogsWinId$viewLogsGridId.setEmptyMessage("No logs found.");
				 
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	} 
	
	

}
