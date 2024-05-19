package org.mq.marketer.campaign.controller.contacts;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.CustomTemplatesDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.Redirect;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class SubscriptionFormController extends GenericForwardComposer {
	
//	private Image previewImgId1;
	private Window PreviewWindId;
	private Html html;
	private Div previewDivId;
	private Textbox selectedFormId;
	private Listbox existingFormsLbId;
	private CustomTemplatesDao customTemplatesDao;
	private CustomTemplatesDaoForDML customTemplatesDaoForDML;
	private Button editExistingFormsBtId;
	private Button deleteExistingFormsBtId;
	private Users currentUsers;
	private Window subscriptionFormWinId;
	private MailingListDao mailingListDao;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private Set<Long> listIdsSet;
	
	 private Session session;
	
	//Set<Long> userIdsSet = GetUser.getUsersSet();//added for multi user acc
	
	public SubscriptionFormController() {
		
		session = Sessions.getCurrent();
		customTemplatesDao = (CustomTemplatesDao)SpringUtil.getBean("customTemplatesDao");
		customTemplatesDaoForDML = (CustomTemplatesDaoForDML)SpringUtil.getBean("customTemplatesDaoForDML");
		mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao"); 
		currentUsers = GetUser.getUserObj();
		 UserActivitiesDao userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
		 UserActivitiesDaoForDML userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		/* if(userActivitiesDao != null) {
	      	userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_CAMPAIGNS_LIST,currentUsers);
		 }*/
		 if(userActivitiesDaoForDML != null) {
		      	userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_CAMPAIGNS_LIST,GetUser.getLoginUserObj());
			 }
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		logger.debug("--- Just Entered After Compose ---");
		super.doAfterCompose(comp);
		previewDivId = (Div)PreviewWindId.getFellow("previewDivId");
		
		if(currentUsers == null || customTemplatesDao == null) {
			logger.error("user or customTemplatesDao is Null ");
			return;
		}
		
		listIdsSet = (Set<Long>)session.getAttribute(Constants.LISTIDS_SET);
		List<CustomTemplates> existingList = customTemplatesDao.getTemplatesByType(listIdsSet, "subscriptionFormHTML");
		Listitem li;
		if(existingList != null && existingList.size() > 0) {
			for (CustomTemplates customTemplates : existingList) {
					li = new Listitem(customTemplates.getTemplateName());
					li.setValue(customTemplates.getTemplateId());
					li.setParent(existingFormsLbId);
							
			}
		} 
		else {
			li = new Listitem("No forms exist");
			li.setParent(existingFormsLbId);
			editExistingFormsBtId.setDisabled(true);
			deleteExistingFormsBtId.setDisabled(true);
		}
		
		existingFormsLbId.setSelectedIndex(0);
	}

	public void onClick$editExistingFormsBtId() throws Exception {
		
		logger.debug("Selected Template Id :" + existingFormsLbId.getSelectedItem().getValue());
		// Set desktop session with formId to edit.
		Desktop desktop = Executions.getCurrent().getDesktop();
		desktop.removeAttribute("formHTML");
		desktop.setAttribute("editSubscriptionFormId", existingFormsLbId.getSelectedItem().getValue());
		//Redirect.goTo(PageListEnum.CONTACT_SUBSCRIPTION_FORM_SETTINGS);		
	}
	
	public void onClick$deleteExistingFormsBtId() {
		try {
			logger.info("Selected Template Id :" + existingFormsLbId.getSelectedItem().getValue());
			if (Messagebox.show("Are you sure you want to delete the selected form?", "Delete?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) ==  Messagebox.YES) {
				//customTemplatesDao.deleteFormById((Long)(existingFormsLbId.getSelectedItem().getValue()));
				customTemplatesDaoForDML.deleteFormById((Long)(existingFormsLbId.getSelectedItem().getValue()));
				existingFormsLbId.removeItemAt(existingFormsLbId.getSelectedIndex());
				MessageUtil.setMessage("Selected subscription form has been deleted successfully.", "green", "top");
			}
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}
	
	/*public void onClick$previewImgId1() {
		Html html = new Html();
		html.setContent(previewContentHTMLId1.getContent());
		Components.removeAllChildren(previewDivId);
		if(previewDivId == null)
			logger.info("Div is null");
		html.setParent(previewDivId);
		PreviewWindId.setPosition("center");
		PreviewWindId.setVisible(true);
	}*/
	
	public void onChange$selectedFormId() {
		try {
			logger.debug("--- Just Entered ---");
			String formIdStr = selectedFormId.getValue();
			MessageUtil.clearMessage();
			Components.removeAllChildren(previewDivId);
			html = new Html();
			String token[] = formIdStr.split(":");
			
			Html htmlComp = (Html)subscriptionFormWinId.getFellow(token[1]);
			
			if(token[0].contains("HTML")) {
				html.setContent(htmlComp.getContent());
				html.setParent(previewDivId);
				PreviewWindId.setPosition("center");
				PreviewWindId.setVisible(true);
				selectedFormId.setValue("default"); // this is to reset the data for onChange event to fire next time 
			} 
			else if(token[0].contains("select")) {
				if((mailingListDao.findByIds(listIdsSet)).size() < 1) {
					MessageUtil.setMessage("At least one mailing list is required.", "red", "top");
					return;
				} 
				Desktop desktop = Executions.getCurrent().getDesktop();
				desktop.removeAttribute("editSubscriptionForm");
				desktop.setAttribute("formHTML",htmlComp.getContent());
				//Redirect.goTo(PageListEnum.CONTACT_SUBSCRIPTION_FORM_SETTINGS);
			}
		} catch (Exception e) {
			logger.debug("** Exception : " + e);
		}
	}
	
	 
}
