package org.mq.marketer.campaign.controller.layout;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.EmailLayout;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.EmailLayoutDao;
import org.mq.marketer.campaign.dao.EmailLayoutDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class ViewEmailController extends Window implements AfterCompose{

	private String userName = null;
	private EmailLayoutDao emailLayoutDao = null;
	private EmailLayoutDaoForDML emailLayoutDaoForDML = null;
	private ListModel listModel = null;
	private Hbox viewBtnHbId = null;
	
	Listbox emailListLbId = null;
	Radiogroup emailOpnRbId = null;
	Div emailDisDivId = null;
	@SuppressWarnings("unchecked")
	List emailList;
	Window emailDisWinId = null;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	Session session = Sessions.getCurrent();
	String firstEmail = "";
	public ViewEmailController(){
		 userName = GetUser.getUserName();
		 emailLayoutDao = (EmailLayoutDao)SpringUtil.getBean("emailLayoutDao");
		 emailLayoutDaoForDML = (EmailLayoutDaoForDML)SpringUtil.getBean("emailLayoutDaoForDML");
		 
	}
	public void afterCompose() {
		this.emailDisWinId = (Window)Utility.getComponentById("emailDisWinId");
		this.emailListLbId = (Listbox)Utility.getComponentById("emailListLbId");
		this.emailOpnRbId = (Radiogroup)Utility.getComponentById("emailOpnRbId");
		this.emailDisDivId = (Div)Utility.getComponentById("emailDisDivId");
		this.viewBtnHbId = (Hbox)Utility.getComponentById("viewBtnHbId");
		getEmails();
		displayEmail();
		//Clients.evalJavaScript("parent.window.scrollTo(0,0)");
	}

	public void getEmails(){
		logger.debug("--just entered--");
		emailList =  emailLayoutDao.findEmailNamesByStatus(userName, emailOpnRbId.getSelectedItem().getLabel());
		logger.debug(" No of emails :"+emailList.size());
		listModel = new SimpleListModel(emailList);
		emailListLbId.setModel(listModel);
		if(emailList.size() > 0){
			firstEmail = (String) emailList.get(0);
			emailListLbId.setSelectedItem(emailListLbId.getItemAtIndex(0));
			viewBtnHbId.setVisible(true);
			emailOpnRbId.setVisible(true);
			MessageUtil.clearMessage();
		}else{ 
			viewBtnHbId.setVisible(false);
			emailOpnRbId.setVisible(false);
			MessageUtil.setMessage("No emails to display.","color:red","TOP");
		}
	}
	
	public void editEmail(){
		try{
			String template = emailListLbId.getSelectedItem().getLabel();
			if (template == null){
				MessageUtil.setMessage ("Please choose a email","color:red","TOP");
				return;
			}
			EmailLayout emailLayout = emailLayoutDao.findByLayoutName(template,userName);
			session.setAttribute("isEdit", "true");
			session.setAttribute("emailLayout", emailLayout);
			if(emailLayout==null){
				return;
			}
			session.removeAttribute("editorType");
			String type = emailLayout.getEditorType();
			session.setAttribute("editorType", type);
			logger.debug("Editor type : " + type);
			if(type.equalsIgnoreCase("blockEditor"))
				Executions.sendRedirect("/email/Editor.zul");
			else if(type.equalsIgnoreCase("plainTextEditor"))
				Executions.sendRedirect("/email/plainEditor.zul");
			else
				Executions.sendRedirect("/email/uploadHTML.zul");
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	@SuppressWarnings("unchecked")
	public void deleteEmail(){
		try{
			int confirm = Messagebox.show("Are you sure you want to delete the selected email?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm == 1){
				String emailName = emailListLbId. getSelectedItem().getLabel();
				String usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory");
				logger.debug("usersParentDirectory: " + usersParentDirectory);
				logger.debug("user Name : " + userName);
				File emailDir = new File(usersParentDirectory + "/" +  userName + "/" + "Email" + "/" + emailName);
				logger.debug("DeleteEmail;  path is:  " + emailDir.getPath());
				if (emailDir.exists()){
					logger.debug("File/directory exists  ");
					try {
						EmailLayout emailLayout = emailLayoutDao.findByLayoutName(emailName, userName);
						logger.debug("got email layout object :"+ emailLayout.getLayoutName());
						Set campaigns = emailLayout.getCampaigns();
						if (campaigns.size() == 0){
							emailLayoutDaoForDML.delete(emailLayout);
							FileUtils.deleteDirectory(emailDir);
							emailListLbId.removeChild(emailListLbId.getSelectedItem()); 
							logger.info("The selected Email deleted successfully : " + emailName);
							if(emailListLbId.getItemCount() > 0){
								emailListLbId.setSelectedItem(emailListLbId.getItemAtIndex(0));
								displayEmail();
							}else{
								emailDisWinId.setVisible(false);
							}
							MessageUtil.setMessage("Email deleted successfully.","color:blue","TOP");
						}else {
							logger.debug("Email was not deleted as it is configured to a campaign: "+ emailName);
							MessageUtil.setMessage("Email is configured to a campaign and cannot be deleted.","color:red","TOP");
						}
					}catch (Exception e) {
						logger.error("** Problem while deleting email " + e.getMessage()+" ** ");
					}
				}else{
					logger.error("** File/directory DOES NOT exist ** ");
				}
			}
		}catch (Exception e) {
			logger.error("** Exception: " + e.getMessage()+" ** ");
		}
	}
	
	public void displayEmail(){
		try{
			logger.debug("--- just entered ---");
			Components.removeAllChildren(emailDisDivId);
			int emailCount = emailListLbId.getItemCount();
			logger.debug(" emailCount  :"+emailCount);
			if(emailCount == 0){
				emailDisWinId.setVisible(false);
				return ;
			}
			Listitem listItem = emailListLbId.getSelectedItem();
			logger.debug("listItem :"+listItem);
			String emailName;
			if(listItem.getValue() == null || listItem.getLabel().trim().length()==0){
				if(firstEmail.trim().length()== 0){
					firstEmail = emailListLbId.getItemAtIndex(0).getLabel();
				}
				emailName = firstEmail;
			}else
				emailName = listItem.getLabel();
			logger.debug("emailName :"+emailName);
			emailDisWinId.setVisible(true);
			EmailLayout emailLayout = emailLayoutDao.findByLayoutName(emailName,userName);
			Html htmlStuff = new Html();
			if(emailLayout.isPrepared())
				htmlStuff.setContent(emailLayout.getFinalHtmlText());
			else{
				/*
				PrepareFinalHTML prepareHTML = new PrepareFinalHTML(userName,emailLayout,emailLayoutDao);
				emailLayout = prepareHTML.prepare();
				htmlStuff.setContent(emailLayout.getHtmlText());
				*/
			}
			htmlStuff.setParent(emailDisDivId);
		}catch (Exception e) {
			logger.error("** Exception :"+e+" **");
		}
	}

	
}
