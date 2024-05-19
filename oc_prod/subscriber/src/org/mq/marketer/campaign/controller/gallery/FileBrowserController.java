package org.mq.marketer.campaign.controller.gallery;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.clapper.util.io.FileUtil;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.FileUploader;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

@SuppressWarnings("serial")
public class FileBrowserController extends GenericForwardComposer {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	Tree explorerTree;
	Grid fileGridId;
	Div filesDivId;
	String userName;
	
	private String usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory");
	private String galleryPathStr;
	private String applicationUrl = PropertyUtil.getPropertyValueFromDB("ApplicationUrl");
	private String urlDir;
	
	List<String> extList = null;
	DateFormat format;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		userName = GetUser. getUserName();
		if(logger.isDebugEnabled()) logger.debug ("user Name : " + userName);
		if(userName == null) {
			return;
		}
		galleryPathStr = usersParentDirectory + File.separator + userName;
		urlDir = applicationUrl + "UserData" + File.separator + userName;
		fileGridId.setRowRenderer(new FileRenderer());
		fillTree();
		
		filesDivId.setVisible(true);
		File file = new File(galleryPathStr + File.separator + "Files");
		List<File> fileList = getFilesOnly(file);
		if(fileList.size() >0)
			fileGridId.setVisible(true);
		else
			fileGridId.setVisible(false);

		fileGridId.setModel(new SimpleListModel(fileList));
		extList = new ArrayList<String>();
		extList.add("pdf"); extList.add("PDF"); 
		extList.add("gif"); extList.add("jpg"); extList.add("png"); extList.add("jpeg");
		extList.add("GIF"); extList.add("JPG"); extList.add("PNG"); extList.add("JPEG");
		
		format = new SimpleDateFormat("dd/MM/yyyy HH:mm aaa");
	}
	
	/**
	 * Fills the Folders tree with the directories and its sub-directories
	 */
	private void fillTree() {
		
		if(logger.isDebugEnabled()) logger.debug("-- Just Entered --");
		
		String userFolders = PropertyUtil.getPropertyValueFromDB("userFolders");
		String[] folders = userFolders.split(",");
		Treechildren tc = new Treechildren();
		File file;
		String folderType;
		int i =0;
		for (String folder : folders) {
			if(i==0) {
				folderType = "Files";
			}else {
				folderType = "Gallery";
			}
			i++;
			file = new File(galleryPathStr + File.separator + folder);
			Treeitem item = new Treeitem();
			item.setAttribute("FolderType", folderType);
			item.setOpen(false);
			Treerow row = new Treerow();
			row.setParent(item);
			File fs[] = file.listFiles();
			if(logger.isDebugEnabled()) logger.debug("File Path: " + file.getAbsolutePath()
					+ " Size : " + fs.length);
			Treecell cell1 = new Treecell(file.getName() + " (" + fs.length
					+ ")", "/img/icons/folder.png");
			cell1.addEventListener("onClick", this);
			cell1.setParent(row);
			item.setValue(file);
			if (fs.length > 0) {
				creteChild(item, file, folderType);
			}
			item.setParent(tc);
		}
		tc.setParent(explorerTree);
		if(logger.isDebugEnabled()) logger.debug("-- Exit --");
	}

	/**
	 * Creates sub directories in the tree if it has 
	 * @param treeItem - Parent item to which sub directories needs to be created
	 * @param file - Parent item directory 
	 * @param folderType - specifies type of the folder (Gallery, Files)
	 */
	private void creteChild(Treeitem treeItem, File file, String folderType) {
		if(logger.isDebugEnabled()) logger.debug("-- Just Entered --");
		
		File[] files = file.listFiles();
		if (files.length == 0) {
			return;
		}
		Treechildren tc = new Treechildren();

		for (File f : files) {
			if (!f.isDirectory())
				continue;
			Treeitem item = new Treeitem();
			item.setAttribute("FolderType", folderType);
			item.setOpen(false);
			Treerow row = new Treerow();
			row.setParent(item);
			File fs[] = f.listFiles();
			if(logger.isDebugEnabled()) logger.debug("Sub File Path" + f.getAbsolutePath()
					+ " Size : " + fs.length);
			
			Treecell cell1 = new Treecell(f.getName() + " (" + fs.length + ")",
					"/img/icons/folder.png");
			cell1.setParent(row);
			cell1.addEventListener("onClick", this);
			item.setValue(f);
			if (fs.length > 0) {
				creteChild(item, f, folderType);
			}
			item.setParent(tc);
		}
		if (tc.getItemCount() > 0) {
			tc.setParent(treeItem);
		}
		if(logger.isDebugEnabled()) logger.debug("-- Exit --");
	}

	public void onEvent(Event event) {
		
		if (event.getTarget() instanceof Treecell) {
			Treeitem ti = (Treeitem)event.getTarget().getParent().getParent();
			try {
				filesDivId.setVisible(true);
				File file = (File) ti.getValue();
				if(logger.isDebugEnabled()) logger.debug("got file : " + file.getAbsolutePath());
				List<File> fileList = getFilesOnly(file);
				if(fileList.size() >0)
					fileGridId.setVisible(true);
				else
					fileGridId.setVisible(false);

				fileGridId.setModel(new SimpleListModel(fileList));
			} catch (Exception e) {
				logger.error("Exception : Problem while fetching the files" ,e);
			}
		}
	}
	
	/**
	 * Returns the list of files in the specified directory
	 * @param file - Directory file
	 * @return - List&lt;File&gt; of Files
	 */
	public List<File> getFilesOnly(File file) {
		List<File> fileList = new ArrayList<File>();
		
		File[] files = file.listFiles();
		for (File f : files) {
			if(f.isFile())
				fileList.add(f);
		}
		return fileList;
	}
	
	/**
	 * RowRenderer is used to render the tree items with the onClick event
	 */
	class FileRenderer implements RowRenderer, EventListener{
		private DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm aaa");
		public void render(Row row, java.lang.Object data, int arg2) {
			if(data instanceof File) {
				File file = (File) data;
				if (!file.isDirectory()) {
					Label lbl;
					lbl = new Label(file.getName());
					lbl.setMaxlength(30);
					lbl.setTooltiptext(file.getName());
					lbl.setSclass("imgbr_label");
					lbl.setParent(row);
					
					long size = file.length() / 1024;
					new Label(size+" KB").setParent(row);
					new Label(format.format(file.lastModified())).setParent(row);
					Button btn = new Button("Create Link");
					btn.addEventListener("onClick", this);
					btn.setParent(row);
					row.setValue(file);
				}
			}
		}
		
		@Override
		public void onEvent(Event event) throws Exception {
			
			if(event.getTarget() instanceof Button) {
				
				try {
					Button btn = (Button)event.getTarget();
					Row row = (Row)btn.getParent();
					File file = (File)row.getValue();
					String filePath = file.getAbsolutePath();
					String arr[] = filePath.split(GetUser.getUserName());
					String url = file.getPath();
					
					if(arr.length > 1)
						url = urlDir + arr[1];
					
					if(logger.isDebugEnabled()) logger.debug("URL : " + url);
					Clients.evalJavaScript("insertLink('" + url +  "')");
				} catch (Exception e) {
					logger.error("Exception : Problem while inserting the link " , e);
				}
			}
		}
	}
	Media media =null;
	
	public void onUpload$uploadBtnId(UploadEvent event) {
		media = event.getMedia();
		logger.debug("Browse is called");
		selectedFileTbId.setValue(media.getName());
		//media = m;
	}
	
	public void onClick$imgUploadBtnId() {
		try {
			upload();
		} catch (Exception e) {
			logger.error("Exception :: error occured while upload method",e);
		}
	}
	
	private Hbox fileUploadHboxId;
	private Textbox selectedFileTbId;
	
	
	private void upload() throws Exception{
		
		if(media == null) {
			Messagebox.show("Please select a file.", "Error", Messagebox.OK , Messagebox.ERROR);
			return;
		}
		Treeitem ti = (Treeitem)explorerTree.getSelectedItem();
		if(ti == null) {
			MessageUtil.setMessage("Please select the folder to upload.","color:red");
			return;
		}
		File file = (File)ti.getValue();
		String path = file.getAbsolutePath() + "/" +((Media)media).getName();
		logger.debug("File Path : " + path);
		String ext = FileUtil.getFileNameExtension(path);
		logger.debug("Extention : " + ext);
		if(ext == null){
			Messagebox.show("Upload valid file.", "Error", Messagebox.OK , Messagebox.ERROR);
			return;
		}
		if(!extList.contains(ext)){
			Messagebox.show("Upload image or PDF files only.", "Error", Messagebox.OK , Messagebox.ERROR);
			return;
		}
		try{
			String folderType = (String)ti.getAttribute("FolderType");
			if(logger.isDebugEnabled()) logger.debug("Folder Type : "+ folderType);
			if(folderType == null) {
				return;
			}
			if(folderType.equalsIgnoreCase("Gallery")) {
				if(ext.equalsIgnoreCase("pdf")) {
					Messagebox.show("Upload image file only in the folder.", "Error", Messagebox.OK , Messagebox.ERROR);
					return;
				}
			}
			
			byte res = FileUploader.upload(media,path,false);
			
			if (res == -10) {
				if(Messagebox.show("File name already exists. Do you want to replace the file?",
						"File Upload", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)
						== Messagebox.CANCEL){
					return;
				}
				res = FileUploader.upload(media,path,true);
			}
			if(res < 0) {
				Messagebox.show("File upload failed. Please retry.",
						"Error", Messagebox.OK , Messagebox.ERROR);
				return;
			}
			
			Messagebox.show("File uploaded successfully. Refresh the page to see the file.", "Info", Messagebox.OK , Messagebox.INFORMATION);						
			fileUploadHboxId.setVisible(false);
			selectedFileTbId.setValue("");
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		media = null;
	}
	
}
