package org.mq.marketer.campaign.controller.useradmin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.FAQ;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.dao.FAQDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.FAQDao;

public class FaqController extends GenericForwardComposer {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Textbox bodyContentId;
	private FAQDaoForDML faqDaoForDML;
	private Users user;
	private UserOrganization userOrg;
	private Listbox languageTypeLbId;
	
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		bodyContentId.setVisible(false);
		faqDaoForDML = (FAQDaoForDML)SpringUtil.getBean("FAQDaoForDML");
		user = GetUser.getUserObj();
		userOrg = user.getUserOrganization();
		onSelect$languageTypeLbId();
	}
	
	public void onSelect$languageTypeLbId() {
		if(languageTypeLbId.getSelectedItem().getValue().toString().equals("English")) {
			setEngData();
			Clients.evalJavaScript("bodyContent();");
		} else {
			setSpanData();
			Clients.evalJavaScript("bodyContent();");
		}
	}
	
	public void faqContentSave (String messageContent) throws Exception {
		FAQ faq = new FAQ();
		faq.setOrgId(userOrg.getUserOrgId());
		faq.setUserId(user.getUserId());
		faq.setLanguage(languageTypeLbId.getSelectedItem().getValue().toString());
		faq.setFaqContent(messageContent);
		faq.setCreatedDate(Calendar.getInstance());
		faqDaoForDML.saveOrUpdate(faq);
	}
	
	public void updateFAQ (FAQ faq, String messageContent) throws Exception {
		faq.setOrgId(userOrg.getUserOrgId());
		faq.setUserId(user.getUserId());
		faq.setLanguage(languageTypeLbId.getSelectedItem().getValue().toString());
		faq.setFaqContent(messageContent);
		faq.setCreatedDate(Calendar.getInstance());
		faqDaoForDML.saveOrUpdate(faq);
	}
	
	public void setEngData() {
		
		Long userId=user.getUserId();
		List<String> content = faqDaoForDML.findFaqDataByType("English",userId);
		if(content!=null && content.size()>0) {
			bodyContentId.setValue(content.get(0));
		} else {
			bodyContentId.setValue("");
		}
	}
	
	public void setSpanData() {
		Long userId=user.getUserId();
		List<String> content = faqDaoForDML.findFaqDataByType("Spanish",userId);
		if(content!=null && content.size()>0) {
			bodyContentId.setValue(content.get(0));
		} else {
			bodyContentId.setValue("");
		}
	}
	
	public void onClickSaveFaqData$jsonData(ForwardEvent event) throws Exception {
		
        Object htmlFaqDescription = JSONValue.parse(event.getOrigin().getData().toString());
		
		String htmlQuill = Constants.STRING_NILL;
		if(htmlFaqDescription!=null) {
			JSONObject jsonObj = (JSONObject) htmlFaqDescription;
			htmlQuill = (String) jsonObj.get("htmlQuill");
			String htmlQuillText = (String) jsonObj.get("htmlQuillText");
			if(htmlQuill.isEmpty() || htmlQuill.trim().isEmpty() || htmlQuill.trim().equals("<p><br></p>")) {
				MessageUtil.setMessage("Please provide Description.", "color:red", "TOP");
				return;
			}else if(htmlQuillText.length() >= 5000) {
				MessageUtil.setMessage("Description must be less than 5000 characters", "color:red", "TOP");
				return;
			}
		}
		
		org.zkoss.zul.Messagebox.Button confirm= Messagebox.show(" Do you want to save ?", "Confirm",
				new Messagebox.Button[] {Messagebox.Button.NO, Messagebox.Button.YES },
				Messagebox.INFORMATION, null, null);
		if(confirm==null || !confirm.equals(Messagebox.Button.YES)) return;
		else if (confirm.equals(Messagebox.Button.YES))
			Messagebox.show(" Saved Successfully"," ",null,Messagebox.INFORMATION,null,null); 
		
		List<FAQ> faq = faqDaoForDML.findByUserId(user.getUserId());
			FAQ eng = null, spanish = null;
			
			for(FAQ f:faq) {
				if(f.getLanguage()!=null && f.getLanguage().equalsIgnoreCase("English")) {
					eng = f;
				}
				if(f.getLanguage()!=null && f.getLanguage().equalsIgnoreCase("Spanish")) {
					spanish = f;
				}
			}
			if(languageTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("English")) {
				if(eng==null) {
					faqContentSave(htmlQuill);
				} else {
					updateFAQ(eng,htmlQuill);
				}
			}
			if(languageTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Spanish")) {
				if(spanish==null) {
					faqContentSave(htmlQuill);
				} else {
					updateFAQ(spanish,htmlQuill);
				}
			}
	}
	
}