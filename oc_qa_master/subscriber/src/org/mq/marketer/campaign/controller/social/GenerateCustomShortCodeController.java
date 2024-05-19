package org.mq.marketer.campaign.controller.social;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.mq.marketer.campaign.beans.UrlShortCodeMapping;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.UrlShortCodeMappingDao;
import org.mq.marketer.campaign.dao.UrlShortCodeMappingDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.springframework.dao.DataIntegrityViolationException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;

public class GenerateCustomShortCodeController extends GenericForwardComposer {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public GenerateCustomShortCodeController() {
		
		urlShortCodeMappingDao = (UrlShortCodeMappingDao)SpringUtil.getBean("urlShortCodeMappingDao");
		urlShortCodeMappingDaoForDML = (UrlShortCodeMappingDaoForDML)SpringUtil.getBean("urlShortCodeMappingDaoForDML");
		user = GetUser.getUserObj();
		
	}

	private final RowRenderer rowRender  = new MyRenderer();
	private Grid custmShrtCodeGridId;
	private UrlShortCodeMappingDao urlShortCodeMappingDao;
	private UrlShortCodeMappingDaoForDML urlShortCodeMappingDaoForDML;
	private Users user;
	private Button savecodeBtnId;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		logger.info("-------just entered-----");
		custmShrtCodeGridId.setRowRenderer(rowRender);
		
		List retList = getUserShortCodes();
		
		if(retList == null ) return;
		
		custmShrtCodeGridId.setModel(new ListModelList(retList));
		
		
	}//doAfterCompose
	
	public RowRenderer getRowRenderer() {
		return rowRender;
	}
	
	public ListModel getCustomShortCodesModel() {
		return new ListModelList(getUserShortCodes());
	}
	
	
	List<StringBuffer> retShortCodesList ;
	
	private Textbox urlTxtBoxId,generatedUrlTxtBoxId;
	public void onClick$generateCodeBtnId () {
		
		if(retShortCodesList != null && retShortCodesList.size() > 0) {
			retShortCodesList.clear();
		}
		
		String enteredURL = urlTxtBoxId.getValue();
		
		if(enteredURL.equals("")||enteredURL.equalsIgnoreCase("http://")) {
			MessageUtil.setMessage("Please provide a valid URL.", "red", "top");
			
			return;
		}
		if(!enteredURL.startsWith("http://")) {
			 
			enteredURL = "http://"+enteredURL;
			urlTxtBoxId.setValue(enteredURL);
		}
		
		
		Row row = null;
		UrlShortCodeMapping urlShortCodeMapping = null;
		 
		Rows rows = custmShrtCodeGridId.getRows();
		
		
		if(rows != null) {
			List rowList = rows.getChildren();
			for (Object object : rowList) {
				
				row = (Row)object;
				urlShortCodeMapping = row.getValue();
				
				if(enteredURL.equals(urlShortCodeMapping.getUrlContent())) {
					
					generatedUrlTxtBoxId.setValue(getFullShortCode(urlShortCodeMapping.getShortCode()));
					savecodeBtnId.setDisabled(true);
					Messagebox.show("The specified URL is already short coded.", "URL Short Code", Messagebox.OK, Messagebox.INFORMATION);
					return;
					
				}//if
				
			}//for
		}
		
		String mappingUrl = user.getUserId()+"|"+enteredURL;
		
		
		//retShortCodesList = Utility.getSixDigitURLCode(mappingUrl);
		retShortCodesList = Utility.getEightDigitURLCode(mappingUrl);
		
		generatedUrlTxtBoxId.setValue(getFullShortCode(""+retShortCodesList.get(0)));
		
		savecodeBtnId.setDisabled(false);
		
	}
	
	private String getFullShortCode(String shortCode) {
		
		
		String insetedUrl = PropertyUtil.getPropertyValue("ApplicationShortUrl");
		
		String testSubDomain="";
		
		if(PropertyUtil.getPropertyValue("ApplicationUrl").contains("test.")) {
			testSubDomain="t/";
		}
		
		String shortCodeUrl = insetedUrl+  testSubDomain + shortCode;
		
		return shortCodeUrl;
		
	}
	
	public void onClick$savecodeBtnId() {
		
		
		
		UrlShortCodeMapping urlShortCodeMapping = null;
		if(retShortCodesList != null && retShortCodesList.size() > 0) {
			
			//check whether any returned  shordcode exists in DB
			
			for (StringBuffer shortCode : retShortCodesList) {
				
				urlShortCodeMapping = new UrlShortCodeMapping(shortCode+"", urlTxtBoxId.getValue(), Constants.URL_SHORT_CODE_TYPE_CUSTOM, user.getUserId());
				
				try {
					
					urlShortCodeMappingDaoForDML.saveOrUpdate(urlShortCodeMapping);
					
					//String ShortUrl = insetedUrl+"/"+shortCode;
					String ShortUrl = getFullShortCode(""+shortCode);
					
					if(!generatedUrlTxtBoxId.getValue().equalsIgnoreCase(ShortUrl)) {
						
						Messagebox.show("The specified URL is short coded successfully as: '"+ShortUrl+"'.You can use this short URL anywhere as required.", "URL Short Code", Messagebox.OK, Messagebox.INFORMATION);
						break;
					}
					Messagebox.show("The specified URL is short coded successfully.\n You can use this short URL anywhere as required.", "URL Short Code", Messagebox.OK, Messagebox.INFORMATION);
					break;
				}catch (DataIntegrityViolationException e) {
					// TODO: handle exception
					Messagebox.show("The specified URL is already short coded.", "URL Short Code", Messagebox.OK, Messagebox.INFORMATION);
					continue;
					
				}catch (ConstraintViolationException e) {
					// TODO: handle exception
					Messagebox.show("The specified URL is already short coded.", "URL Short Code", Messagebox.OK, Messagebox.INFORMATION);
					continue;
				}
				
				
			}//for
			
			Redirect.goTo(PageListEnum.SOCIAL_GENERATE_CUSTOM_SHORTCODE);
			
			/*Include xcontents = (Include)Sessions.getCurrent().getAttribute("xcontents");
			xcontents.setSrc("/zul/" + PageListEnum.EMPTY + ".zul");
			xcontents.setSrc("/zul/" + PageListEnum.SOCIAL_GENERATE_CUSTOM_SHORTCODE + ".zul");*/
		
		}//if
		
		
	}
	
	public List getUserShortCodes() {
		
		List<UrlShortCodeMapping> shortCodeList = urlShortCodeMappingDao.getAllUserCustomShortCodes(user.getUserId(), Constants.URL_SHORT_CODE_TYPE_CUSTOM);
		
		if(shortCodeList != null && shortCodeList.size() > 0) {
			//logger.info("myList size ::"+shortCodeList.size());
			
			return shortCodeList;		
			
		}else  {
			return null;
		}
		
	}
	
	
	private class MyRenderer implements RowRenderer {
		MyRenderer() {
			super();
			
		}
		@Override
		public void render(Row row, Object obj, int arg2) throws Exception {

			
			UrlShortCodeMapping urlShortCodeMapping = null;
			
			
			
			if(obj instanceof UrlShortCodeMapping) {
				urlShortCodeMapping = (UrlShortCodeMapping)obj;
				row.setValue(urlShortCodeMapping);
				
				new Label(urlShortCodeMapping.getUrlContent()).setParent(row);
				new Label(getFullShortCode(urlShortCodeMapping.getShortCode())).setParent(row);
				
				
				
			}
			
			
			
			
		}//render
	}
	
}
