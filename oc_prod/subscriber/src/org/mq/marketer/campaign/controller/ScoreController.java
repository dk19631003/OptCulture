package org.mq.marketer.campaign.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.UserScoreSettings;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.UserScoreSettingsDao;
import org.mq.marketer.campaign.dao.UserScoreSettingsDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;



public class ScoreController extends GenericForwardComposer {
	//********************** page Visited ****************************************
	private static final String PV_HBOXID = "pVisitHboxId_";
	private static final String PV_CHKID = "pVisitChkId_";
	private static final String PV_QUERYLSTID = "pVisitQuryLstBxId_";
	private static final String PV_QUERYTXTBXID = "pVisitQuryTxtBxId_";
	private static final String PV_QUERYMINSCRTXTBXID = "pVisitQuryMinScoreTxtBxId_";
	
	private int pvCount=1;
	
	//********************** Downloaded ****************************************
	private static final String DWL_HBOXID = "dwLHboxId_";
	private static final String DWL_TXTBXID = "dwLTxtBxId_";
	private static final String DWL_ALLPDFCHKID = "dwLAllPDFChkId_";
	private static final String DWL_ALLDOCCHKID = "dwLAllDOCChkId_";
	private static final String DWL_ALLPPTCHKID = "dwLAllPPTChkId_";
	private static final String DWL_MINSCRTXTBXID = "dwLMinScoreTxtBxId_";
	private int dlCount=1;
	
	//********************** Source Of Visit ****************************************
	
	private static final String SOV_HBOXID = "sOfVisitHboxId_";
	private static final String SOV_LSTID = "sOfVisitLstBxId_";
	private static final String SOV_TXTBXID = "sOfVisitTxtBxId_";
	private static final String SOV_MINSCRTXTBXID = "sOfVisitMinScoreTxtBxId_";
	private int sovCount=1;
	
	
	//********************** Email Opened ****************************************
	private static final String EOPN_HBOXID = "eOpnHboxId_";
	private static final String EOPN_LSTID = "eOpnLstBxId_";
	private static final String EOPN_MINSCRTXTBXID = "eOpnMinScoreTxtBxId_";
	private int eOpnCount=1;
	
	//********************** Email Clicked ****************************************
	private static final String ECLK_HBOXID = "eClKdHboxId_";
	private static final String ECLK_LSTID = "eClkLstBxId_";
	private static final String ECLK_MINSCRTXTBXID = "eClkdMinScoreTxtBxId_";
	private int eClkCount=1;
	
	//********************** Email Not Opend ****************************************
	private static final String ENOP_HBOXID = "eNOpnHboxId_";
	private static final String ENOP_LSTID = "eNOpnLstBxId_";
	private static final String ENOP_MINSCRTXTBXID = "eNOpnMinScoreTxtBxId_";
	private int eNOpndCount=1;
	
	//********************** Email Unsubscribe ****************************************
	private static final String EUSUB_HBOXID = "eUSubHboxId_";
	private static final String EUSUB_LSTID = "eUSubLstBxId_";
	private static final String EUSUB_MINSCRTXTBXID = "eUSubMinScoreTxtBxId_";
	private int eUSubCount=1;
	
	//********************** Form Submitted ****************************************
	private static final String FSUB_HBOXID = "formSubHboxId_";
	private static final String FSUB_LSTID = "formSubLstBxId_";
	private static final String FSUB_MINSCRTXTBXID = "formSubMinScoreTxtBxId_";
	private int fSubCount=1;
	
	//********************** Form Abondoned ****************************************
	private static final String FA_HBOXID = "fAbndHboxId_";
	private static final String FA_LSTID = "fAbndLstBxId_";
	private static final String FA_MINSCRTXTBXID = "fAbndMinScoreTxtBxId_";
	private int fAbndCount=1;
	
	//********************** Form Fill Ratio ****************************************
	private static final String FFR_HBOXID = "fFRHboxId_";
	private static final String FFR_NLSTID = "fFRNLstBxId_";
	private static final String FFR_CNDLSTID = "fFRCndLstBxId_";
	private static final String	FFR_INTBXID = "fFRIntBxId_";
	private static final String FFR_MINSCRTXTBXID = "fFRMinScoreTxtBxId_";
	private int fFRCount=1;
	
	
	private Textbox pVisitedMaxScoreTbId,downloadedMaxScoreTbId,sourceOfVisitMaxScorTbId,emailOpendMaxScorTbId,emailClickedMaxScorTbId,
					emailNotOpenTbId,emailUnsubscribTbId,formSubmitmaxScoreTbId,formAbndMaxScorTbId,formFRatioMaxScorTbId;
	
	private Textbox pVisitQuryMinScoreTxtBxId_0,dwLMinScoreTxtBxId_0,sOfVisitMinScoreTxtBxId_0,eOpnMinScoreTxtBxId_0,eClkdMinScoreTxtBxId_0,
					eNOpnMinScoreTxtBxId_0,eUSubMinScoreTxtBxId_0,formSubMinScoreTxtBxId_0,fAbndMinScoreTxtBxId_0,fFRMinScoreTxtBxId_0;

	private CampaignsDao campaignsDao;
	private UserScoreSettingsDao userScoreSettingsDao;
	private UserScoreSettingsDaoForDML userScoreSettingsDaoForDML;
	private Users user;
	private Div pageVisitedDivId,downloadedDivId,sourceOfVisitDivId,emailOpenedDivId,ecl_DivId;
	private Div emailNotOpenDivId,emailUnsubscribeDivId,formSubmitDivId,formAbondonedDivId,formFillRatioDivId;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);	
	private MyRenderer renderer = new MyRenderer();

	
	
	public ScoreController(){
		
//		sessionScope = Sessions.getCurrent();
		campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
		userScoreSettingsDao=(UserScoreSettingsDao)SpringUtil.getBean("userScoreSettingsDao");
		userScoreSettingsDaoForDML=(UserScoreSettingsDaoForDML)SpringUtil.getBean("userScoreSettingsDaoForDML");
		PageUtil.setHeader("Score Settings","","font-weight:bold;font-size:15px;color:#313031;align:left" ,true);
												
		
		user = GetUser.getUserObj();
	}
	
	
	
	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
			
			//**  for minScore*****
			pVisitQuryMinScoreTxtBxId_0.setWidgetListener("onFocus", "setLabel(this);");
			pVisitQuryMinScoreTxtBxId_0.setWidgetListener("onBlur", "setLabel(this);");
			
			dwLMinScoreTxtBxId_0.setWidgetListener("onFocus", "setLabel(this);");
			dwLMinScoreTxtBxId_0.setWidgetListener("onBlur", "setLabel(this);");
			
			sOfVisitMinScoreTxtBxId_0.setWidgetListener("onFocus", "setLabel(this);");
			sOfVisitMinScoreTxtBxId_0.setWidgetListener("onBlur", "setLabel(this);");
			
			eOpnMinScoreTxtBxId_0.setWidgetListener("onFocus", "setLabel(this);");
			eOpnMinScoreTxtBxId_0.setWidgetListener("onBlur", "setLabel(this);");
			
			eClkdMinScoreTxtBxId_0.setWidgetListener("onFocus", "setLabel(this);");
			eClkdMinScoreTxtBxId_0.setWidgetListener("onBlur", "setLabel(this);");
			
			eNOpnMinScoreTxtBxId_0.setWidgetListener("onFocus", "setLabel(this);");
			eNOpnMinScoreTxtBxId_0.setWidgetListener("onBlur", "setLabel(this);");
			
			eUSubMinScoreTxtBxId_0.setWidgetListener("onFocus", "setLabel(this);");
			eUSubMinScoreTxtBxId_0.setWidgetListener("onBlur", "setLabel(this);");
			
			formSubMinScoreTxtBxId_0.setWidgetListener("onFocus", "setLabel(this);");
			formSubMinScoreTxtBxId_0.setWidgetListener("onBlur", "setLabel(this);");
			
			fAbndMinScoreTxtBxId_0.setWidgetListener("onFocus", "setLabel(this);");
			fAbndMinScoreTxtBxId_0.setWidgetListener("onBlur", "setLabel(this);");
			
			fFRMinScoreTxtBxId_0.setWidgetListener("onFocus", "setLabel(this);");
			fFRMinScoreTxtBxId_0.setWidgetListener("onBlur", "setLabel(this);");
			
			//** for maxScore***
			pVisitedMaxScoreTbId.setWidgetListener("onFocus", "setMaxLabel(this);");
			pVisitedMaxScoreTbId.setWidgetListener("onBlur", "setMaxLabel(this);");
			
			downloadedMaxScoreTbId.setWidgetListener("onFocus", "setMaxLabel(this);");
			downloadedMaxScoreTbId.setWidgetListener("onBlur", "setMaxLabel(this);");
			
			sourceOfVisitMaxScorTbId.setWidgetListener("onFocus", "setMaxLabel(this);");
			sourceOfVisitMaxScorTbId.setWidgetListener("onBlur", "setMaxLabel(this);");
			
			emailOpendMaxScorTbId.setWidgetListener("onFocus", "setMaxLabel(this);");
			emailOpendMaxScorTbId.setWidgetListener("onBlur", "setMaxLabel(this);");
			
			emailClickedMaxScorTbId.setWidgetListener("onFocus", "setMaxLabel(this);");
			emailClickedMaxScorTbId.setWidgetListener("onBlur", "setMaxLabel(this);");
			
			emailNotOpenTbId.setWidgetListener("onFocus", "setMaxLabel(this);");
			emailNotOpenTbId.setWidgetListener("onBlur", "setMaxLabel(this);");
			
			emailUnsubscribTbId.setWidgetListener("onFocus", "setMaxLabel(this);");
			emailUnsubscribTbId.setWidgetListener("onBlur", "setMaxLabel(this);");
			
			formSubmitmaxScoreTbId.setWidgetListener("onFocus", "setMaxLabel(this);");
			formSubmitmaxScoreTbId.setWidgetListener("onBlur", "setMaxLabel(this);");
			
			formAbndMaxScorTbId.setWidgetListener("onFocus", "setMaxLabel(this);");
			formAbndMaxScorTbId.setWidgetListener("onBlur", "setMaxLabel(this);");
			
			formFRatioMaxScorTbId.setWidgetListener("onFocus", "setMaxLabel(this);");
			formFRatioMaxScorTbId.setWidgetListener("onBlur", "setMaxLabel(this);");
			

			setCurrentSettings();
			
			
		} catch (Exception e) {
			
		}
		
	}
	
	
	/**
	 * ByDefault setting  
	 */
	public void setCurrentSettings() {
		try {
			logger.debug("<<<<<<<<<< just enterd in setCurrentSettings() >>>>>>>>>>");
			List<UserScoreSettings> userScoresList = userScoreSettingsDao.getUserScoreSetting(user.getUserId());
			
			if(userScoresList == null && userScoresList.size() == 0) {
				return;
			}
			
			int childInd;
			Hbox tempHbox;
			Checkbox tempChkbox;
			Listbox tempListbox;
			Textbox tempTextbox;
			
	
			
			for (UserScoreSettings scoreObj : userScoresList) {
				
				try {
					logger.info("UserScoreSetting object is : "+ scoreObj.getGroupName()+":"+ scoreObj.getCondition());
					
					//********************** Page Visited ****************************************
					if(scoreObj.getGroupName().equalsIgnoreCase(Constants.SCORE_PAGE_VISIT)) {
						
						childInd = pageVisitedDivId.getChildren().size();
						childInd--;

						if(childInd==0) {
							tempHbox = (Hbox) pageVisitedDivId.getFellowIfAny(PV_HBOXID+childInd, true);
							
							if(tempHbox.getAttribute("scoreObject") != null) {
								onClick$pageVisitedImgId(); // Add new Page Visited Row
							}
							else if(scoreObj.getMaxScore()!=null) {
								pVisitedMaxScoreTbId.setText(""+scoreObj.getMaxScore());
							}
						} // if
						else {
							onClick$pageVisitedImgId(); // Add new Page Visited Row
						}
						
						childInd = pageVisitedDivId.getChildren().size();
						childInd--;
						
						//Set the object to the Hbox
						tempHbox = (Hbox) pageVisitedDivId.getFellowIfAny(PV_HBOXID+childInd, true);
						if(tempHbox!=null) tempHbox.setAttribute("scoreObject", scoreObj);
						
						// Set CheckBox data
						tempChkbox = (Checkbox) pageVisitedDivId.getFellowIfAny(PV_CHKID+childInd, true);
						if(tempChkbox!=null) tempChkbox.setChecked(scoreObj.getCondition().equalsIgnoreCase("All Visit"));
						
						// Set Listbox data
						tempListbox = (Listbox) pageVisitedDivId.getFellowIfAny(PV_QUERYLSTID+childInd, true);
						if(tempListbox!=null) {
							if(scoreObj.getCondition().equalsIgnoreCase("contains")) {
								tempListbox.setSelectedIndex(0);
							}
							else if(scoreObj.getCondition().equalsIgnoreCase("Reg Exp")) {
								tempListbox.setSelectedIndex(1);
							}
						}

						// Set Textbox data
						tempTextbox = (Textbox) pageVisitedDivId.getFellowIfAny(PV_QUERYTXTBXID+childInd, true);
						if(tempTextbox!=null) tempTextbox.setText(scoreObj.getDataOne());
						
						// Set score data
						tempTextbox = (Textbox) pageVisitedDivId.getFellowIfAny(PV_QUERYMINSCRTXTBXID+childInd, true);
						if(tempTextbox!=null) tempTextbox.setText(""+scoreObj.getScore());
											
						
						
					} // if ("Page Visited")
					
					//********************** Downloaded ****************************************
					
					
					else if(scoreObj.getGroupName().equalsIgnoreCase(Constants.SCORE_DOWNLOAD)){
						
					
						childInd = downloadedDivId.getChildren().size();
						childInd--;
						
						if(childInd==0) {
							tempHbox = (Hbox) downloadedDivId.getFellowIfAny(DWL_HBOXID+childInd, true);
							
							if(tempHbox.getAttribute("scoreObject") != null) {
								onClick$downloadImgId(); // Add new Page Visited Row
							}
							else if(scoreObj.getMaxScore()!=null) {
								downloadedMaxScoreTbId.setText(""+scoreObj.getMaxScore());
							}
						} // if
						else {
							onClick$downloadImgId(); // Add new Page Visited Row
						}
						
						
						childInd = downloadedDivId.getChildren().size();
						childInd--;
						
						//Set the object to the Hbox
						tempHbox = (Hbox) downloadedDivId.getFellowIfAny(DWL_HBOXID+childInd, true);
						if(tempHbox!=null) tempHbox.setAttribute("scoreObject", scoreObj);
						
						// Set Textbox data
						tempTextbox = (Textbox) downloadedDivId.getFellowIfAny(DWL_TXTBXID+childInd, true);
						String txtStr=scoreObj.getDataOne().trim();
						
						String txtStr1 = txtStr.substring(0, scoreObj.getDataOne().indexOf("|"));
						logger.info("actual text is>>>>>>"+txtStr1);
						if(tempTextbox!=null) tempTextbox.setText(txtStr1);
						
						// Set PDF CheckBox data 
						tempChkbox = (Checkbox) downloadedDivId.getFellowIfAny(DWL_ALLPDFCHKID+childInd, true);
						if(tempChkbox!=null) tempChkbox.setChecked(txtStr.contains("|All PDF"));
						
						// Set DOC CheckBox data
						tempChkbox = (Checkbox) downloadedDivId.getFellowIfAny(DWL_ALLDOCCHKID+childInd, true);
						if(tempChkbox!=null) tempChkbox.setChecked(txtStr.contains("|All DOC"));
						
						// Set PPT CheckBox data
						tempChkbox = (Checkbox) downloadedDivId.getFellowIfAny(DWL_ALLPPTCHKID+childInd, true);
						if(tempChkbox!=null) tempChkbox.setChecked(txtStr.contains("|All PPT"));					
						
						
						// Set score data
						tempTextbox = (Textbox) downloadedDivId.getFellowIfAny(DWL_MINSCRTXTBXID+childInd, true);
						if(tempTextbox!=null) tempTextbox.setText(""+scoreObj.getScore());
						
					} // if("Downloaded")
					
					
					//********************** Source Of Visit ****************************************
					else if(scoreObj.getGroupName().equalsIgnoreCase(Constants.SCORE_SOURCE_OF_VISIT)){
						
/*					if(isFirstSOVfilled) {
							onClick$sourceOfVisitImgId(); // Add new Source Of Visted Row
						}
						else { // First Top record
							
							isFirstSOVfilled=true;
							if(scoreObj.getMaxScore()!=null) {
								sourceOfVisitMaxScorTbId.setText(""+scoreObj.getMaxScore());
							}
						}
*/					
						childInd = sourceOfVisitDivId.getChildren().size();
						childInd--;
						
						
						if(childInd==0) {
							tempHbox = (Hbox) sourceOfVisitDivId.getFellowIfAny(SOV_HBOXID+childInd, true);
							
							if(tempHbox.getAttribute("scoreObject") != null) {
								onClick$sourceOfVisitImgId(); // Add new Page Visited Row
							}
							else if(scoreObj.getMaxScore()!=null) {
								sourceOfVisitMaxScorTbId.setText(""+scoreObj.getMaxScore());
							}
						} // if
						else {
							onClick$sourceOfVisitImgId(); // Add new Page Visited Row
						}

						childInd = sourceOfVisitDivId.getChildren().size();
						childInd--;
						
						//Set the object to the Hbox
						tempHbox = (Hbox) sourceOfVisitDivId.getFellowIfAny(SOV_HBOXID+childInd, true);
						if(tempHbox!=null) tempHbox.setAttribute("scoreObject", scoreObj);
						
						// Set Listbox data
						tempListbox = (Listbox) sourceOfVisitDivId.getFellowIfAny(SOV_LSTID+childInd, true);
						if(tempListbox!=null) {
							if(scoreObj.getCondition().equalsIgnoreCase("Contains")) {
								tempListbox.setSelectedIndex(0);
							}
							else if(scoreObj.getCondition().equalsIgnoreCase("Does not contains")) {
								tempListbox.setSelectedIndex(1);
							}
							else if(scoreObj.getCondition().equalsIgnoreCase("All these words")) {
								tempListbox.setSelectedIndex(2);
							}
							else if(scoreObj.getCondition().equalsIgnoreCase("Any of thes words")) {
								tempListbox.setSelectedIndex(3);
							}
						}
						
						// Set Textbox data
						tempTextbox = (Textbox) sourceOfVisitDivId.getFellowIfAny(SOV_TXTBXID+childInd, true);
						if(tempTextbox!=null) tempTextbox.setText(scoreObj.getDataOne());
					
						// Set score data
						tempTextbox = (Textbox) sourceOfVisitDivId.getFellowIfAny(SOV_MINSCRTXTBXID+childInd, true);
						if(tempTextbox!=null) tempTextbox.setText(""+scoreObj.getScore());
					}
					
					
					//********************** Email Opned ****************************************
					else if(scoreObj.getGroupName().equalsIgnoreCase(Constants.SCORE_EMAIL_OPEN)) {
						

						setEmailObject(emailOpenedDivId, EOPN_MINSCRTXTBXID, EOPN_HBOXID, EOPN_LSTID, emailOpendMaxScorTbId, scoreObj);
						

					}
					
					//********************** Email Clicked ****************************************
					else if(scoreObj.getGroupName().equalsIgnoreCase(Constants.SCORE_EMAIL_CLICK)) {
						
						setEmailObject(ecl_DivId, ECLK_MINSCRTXTBXID, ECLK_HBOXID, ECLK_LSTID, emailClickedMaxScorTbId, scoreObj);
					}
					
					//********************** Email Not Opened ****************************************
					else if(scoreObj.getGroupName().equalsIgnoreCase(Constants.SCORE_EMAIL_NOTOPEN)) {
						
						setEmailObject(emailNotOpenDivId, ENOP_MINSCRTXTBXID, ENOP_HBOXID, ENOP_LSTID, emailNotOpenTbId, scoreObj);
					}
					
					//********************** Email Unsubscribed  ****************************************
					else if(scoreObj.getGroupName().equalsIgnoreCase(Constants.SCORE_EMAIL_UNSUBSCRIBED)) {
						
						setEmailObject(emailUnsubscribeDivId, EUSUB_MINSCRTXTBXID, EUSUB_HBOXID, EUSUB_LSTID, emailUnsubscribTbId, scoreObj);
					}
					
					//********************** Form Submitted  ****************************************
					else if(scoreObj.getGroupName().equalsIgnoreCase(Constants.SCORE_FORM_SUBMIT)) {
						
						setFormObject(formSubmitDivId, FSUB_MINSCRTXTBXID, FSUB_HBOXID, FSUB_LSTID, formSubmitmaxScoreTbId, scoreObj);
					}
					
					//********************** Form Abondoned  ****************************************
					else if(scoreObj.getGroupName().equalsIgnoreCase(Constants.SCORE_FORM_ABND)) {
						setFormObject(formAbondonedDivId, FA_MINSCRTXTBXID, FA_HBOXID, FA_LSTID, formAbndMaxScorTbId, scoreObj);
					}
					
					//********************** Form Fill Ratio  ****************************************
					else if(scoreObj.getGroupName().equalsIgnoreCase(Constants.SCORE_FORM_F_RATIO)) {
						
						setFormObject(formFillRatioDivId, FFR_MINSCRTXTBXID, FFR_HBOXID, FFR_NLSTID, formFRatioMaxScorTbId, scoreObj);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::", e);
				}
				
				
			} // for
				
		} catch (Exception e) {
				logger.error("**exception : error occured while rendering the list",e);
		} 	
		
	} // setCurrentSettings
	

	/**
	 * Calling method for displaying the EmailScore
	 * @param emailDiv
	 * @param scoreStartingIdStr
	 * @param hboxStartingIdStr
	 * @param listboxStartingIdStr
	 * @param maxScoreId
	 * @param scoreObj
	 */
	private void setEmailObject(Div emailDiv, String scoreStartingIdStr, String hboxStartingIdStr, 
								String listboxStartingIdStr, Textbox maxScoreId, UserScoreSettings scoreObj) {
		try {
			
			int childInd;
			Hbox tempHbox=null;
			Checkbox tempChkbox;
			Listbox tempListbox;
			Textbox tempTextbox;
			
			childInd = emailDiv.getChildren().size();
			childInd--;
			
			if(childInd==0) {
				tempHbox = (Hbox) emailDiv.getFellowIfAny(hboxStartingIdStr+childInd, true);
			}
			
				if(childInd > 0 || tempHbox.getAttribute("scoreObject") != null) { // Create new row
					
					if(scoreObj.getGroupName().equalsIgnoreCase(Constants.SCORE_EMAIL_OPEN)) {
						onClick$emailOpenedImgId(); 
					}
					else if(scoreObj.getGroupName().equalsIgnoreCase(Constants.SCORE_EMAIL_CLICK)) {
						onClick$emailClickedImgId(); 
					} 
					else if(scoreObj.getGroupName().equalsIgnoreCase(Constants.SCORE_EMAIL_NOTOPEN)) {
						onClick$emailNotOpenImgId(); 
					} 
					else if(scoreObj.getGroupName().equalsIgnoreCase(Constants.SCORE_EMAIL_UNSUBSCRIBED)) {
						onClick$emailUnsubscribeImgId(); 
					} 

				}
				else if(scoreObj.getMaxScore()!=null) { // First Row
					maxScoreId.setText(""+scoreObj.getMaxScore());
				}
				


			childInd = emailDiv.getChildren().size();
			childInd--;
			
			//Set the object to the Hbox
			tempHbox = (Hbox) emailDiv.getFellowIfAny(hboxStartingIdStr + childInd, true);
			if(tempHbox!=null) tempHbox.setAttribute("scoreObject", scoreObj);
			
			// Set  CampaignListbox data
			tempListbox = (Listbox) emailDiv.getFellowIfAny(listboxStartingIdStr+childInd, true);
			
			if(childInd==0) {
				tempListbox.setModel(new ListModelList(getCampaigns()));
				tempListbox.setItemRenderer(renderer);
			}
			
			tempListbox.renderAll();
			
//			logger.info(scoreObj.getGroupName()+" :: >>>>>>>>>>>>>> CampaignListbox item :: "+tempListbox.getItemCount()); 
			if(tempListbox!=null) {
				
				List<Component> items = tempListbox.getChildren();//getItems();
				
				for (Component eachComp : items) {
					Listitem listitem=(Listitem)eachComp;
					if(listitem.getLabel().trim().equals(scoreObj.getDataOne())) {
						tempListbox.setSelectedItem(listitem);
						break;
					}
				} // for
			} // if
			
			// Set score data
			tempTextbox = (Textbox) emailDiv.getFellowIfAny(scoreStartingIdStr+childInd, true);
			if(tempTextbox!=null) tempTextbox.setText(""+scoreObj.getScore());
			
		
			
		} catch (Exception e) {
			logger.error("Exception :error while rendering the emailRecords ::",e);
		}
	}
	
	
	/**
	 * calling method for formSocre  settig
	 * @param formDiv
	 * @param scoreStartingIdStr
	 * @param hboxStartingIdStr
	 * @param formNamelistboxStartingIdStr
	 * @param maxScoreId
	 * @param scoreObj
	 */
	private void setFormObject(Div formDiv, String scoreStartingIdStr, String hboxStartingIdStr, 
			String formNamelistboxStartingIdStr, Textbox maxScoreId, UserScoreSettings scoreObj) {
		
		try {
			int childInd;
			Hbox tempHbox=null;
			Checkbox tempChkbox;
			Listbox tempListbox;
			Textbox tempTextbox;
			
			childInd = formDiv.getChildren().size();
			childInd--;
			
			if(childInd==0) {
				tempHbox = (Hbox) formDiv.getFellowIfAny(hboxStartingIdStr+childInd, true);
			}
			
				if(childInd > 0 || tempHbox.getAttribute("scoreObject") != null) { // Create new row
					
					if(scoreObj.getGroupName().equalsIgnoreCase(Constants.SCORE_FORM_SUBMIT)) {
						onClick$formSubscribeImgId(); 
					}
					 
					else if(scoreObj.getGroupName().equalsIgnoreCase(Constants.SCORE_FORM_ABND)) {
						onClick$formAbondonedImgId(); 
					} 
					else if(scoreObj.getGroupName().equalsIgnoreCase(Constants.SCORE_FORM_F_RATIO)) {
						onClick$formFillRatioImgId(); 
					} 

				}
				else if(scoreObj.getMaxScore()!=null) { // First Row
					maxScoreId.setText(""+scoreObj.getMaxScore());
				}
				
			//} // if


			childInd = formDiv.getChildren().size();
			childInd--;
			
			//Set the object to the Hbox
			tempHbox = (Hbox) formDiv.getFellowIfAny(hboxStartingIdStr + childInd, true);
			if(tempHbox!=null) tempHbox.setAttribute("scoreObject", scoreObj);
			
			// Set  FormListbox data
			tempListbox = (Listbox) formDiv.getFellowIfAny(formNamelistboxStartingIdStr+childInd, true);
			
/*		if(childInd==0) {
				tempListbox.setModel(new ListModelList(getForms()));
				tempListbox.setItemRenderer(renderer);
			}
			
*/
			tempListbox.renderAll();
			
			logger.info(scoreObj.getGroupName()+" :: >>>>>>>>>>>>>> CampaignListbox item :: "+tempListbox.getItemCount()); 
			if(tempListbox!=null) {
				
				List<Component> items = tempListbox.getChildren();//getItems();
				
				for (Component eachComp : items) {
					Listitem listitem=(Listitem)eachComp;
					if(listitem.getLabel().trim().equals(scoreObj.getDataOne())) {
						tempListbox.setSelectedItem(listitem);
						break;
					}
				} // for
			} // if
			
			
			// Set FormFill Ratio details
			if(scoreObj.getGroupName().equalsIgnoreCase(Constants.SCORE_FORM_F_RATIO)) {
				
				tempListbox = (Listbox) formDiv.getFellowIfAny(FFR_CNDLSTID+childInd, true);
				
				List<Component> items = tempListbox.getChildren();//getItems();
				
				for (Component eachComp : items) {
					Listitem listitem=(Listitem)eachComp;

					if(listitem.getLabel().trim().equals(scoreObj.getCondition())) {
						tempListbox.setSelectedItem(listitem);
						break;
					}
				} // for
				
				Intbox tempIntbox = (Intbox) formDiv.getFellowIfAny(FFR_INTBXID+childInd, true);
				if(tempIntbox!=null) tempIntbox.setText(""+scoreObj.getDataTwo());
			} // if 

			// Set score data
			tempTextbox = (Textbox) formDiv.getFellowIfAny(scoreStartingIdStr+childInd, true);
			if(tempTextbox!=null) tempTextbox.setText(""+scoreObj.getScore());
		} catch (Exception e) {
			logger.error("Exception :error while rendering the formRecords ::",e);
		}
		
		
	} // setFormObject
	
	
	
	List<Campaigns> campaignsList  = null;
	public List<Campaigns> getCampaigns() {
		
		try {
			if(campaignsList==null) {
				
				campaignsList = campaignsDao.findByUser(user.getUserId());
			}
			return campaignsList;
		} catch (Exception e) {
			logger.error("**Exception : error occured while getting the compaign from CampaignDao");
			return null;
		}
	}
	
	
	/**
	 * Adding a new row for pageVisited
	 */
	public void onClick$pageVisitedImgId() {
		try {
			Image img;
			Space space ;
			Textbox urlTb;
			Textbox scoreTb;
			Listbox lbox;
			Listitem li;
			Hbox leftHBox;
			Label lb;
			Hbox outerHBox= new Hbox();
			outerHBox.setId(PV_HBOXID+pvCount);
			outerHBox.setStyle("padding:2px;");
			
			leftHBox = new Hbox();
			leftHBox.setWidth("500px");
			leftHBox.setAlign("center");
			
			Checkbox chbox = new Checkbox();
			chbox.setId(PV_CHKID+pvCount);
			chbox.setLabel("All Visits");
			chbox.setParent(leftHBox);
			
			space = new Space();
//				space.setWidth("20px");
			space.setParent(leftHBox);
			
			lb =new Label("OR");
			lb.setStyle("font-weight:bold");
			lb.setParent(leftHBox);
			
			space = new Space();
			space.setParent(leftHBox);
			
			lbox = new Listbox();
			lbox.setId(PV_QUERYLSTID+pvCount);
			lbox.setMold("select");
			li = new Listitem("contains");
//				lbox.setSelectedIndex(0);
			li.setSelected(true);
			li.setParent(lbox);
			 
			li = new Listitem("Reg Exp");
			li.setParent(lbox);
			lbox.setParent(leftHBox);
			 
			space = new Space();
			space.setParent(leftHBox);
			 
			urlTb= new Textbox();
			urlTb.setId(PV_QUERYTXTBXID+pvCount);
			urlTb.setParent(leftHBox);
			leftHBox.setParent(outerHBox);
			 
			Hbox rightHBox=new Hbox();
			rightHBox.setWidth("200px");
			rightHBox.setPack("end");
			
			lb= new Label("*");
			lb.setStyle("color:red;padding-right:5px;");
			lb.setParent(rightHBox);
			
			scoreTb=new Textbox();
			scoreTb.setId(PV_QUERYMINSCRTXTBXID+pvCount);
			scoreTb.setValue("score");
			scoreTb.setWidgetListener("onFocus", "setLabel(this);");
			scoreTb.setWidgetListener("onBlur", "setLabel(this);");
			scoreTb.setName("minScore");
			scoreTb.setStyle("padding-right:5px;");
			
			
			scoreTb.setWidth("40px");
			scoreTb.setParent(rightHBox);
			
			img = new Image("/img/icons/delete.png");
			img.setStyle("cursor:pointer;cursor:hand;padding-left:6px;");
			img.addEventListener("onClick", new MyRenderer() );
			
			img.setParent(rightHBox);
			rightHBox.setParent(outerHBox);
			
			outerHBox.setParent(pageVisitedDivId);
			pvCount++;
			
		} catch (Exception e) {
			logger.error("** Exception : error occured clicking the pagevisited **",e);
		}
		
	}
	
	
	/**
	 * Adding new row for downloaded
	 */
	public void onClick$downloadImgId(){
		try {
			Image img;
			Checkbox chbox;
			Space space;
			Textbox tb;
			Hbox outerHBOx=new Hbox();
			outerHBOx.setId(DWL_HBOXID+dlCount);
			outerHBOx.setStyle("padding:2px;");
			Hbox leftHBox= new Hbox();
				
			leftHBox.setWidth("500px");
			 tb= new Textbox();
			 tb.setId(DWL_TXTBXID+dlCount);
			 tb.setParent(leftHBox);
			
			 space = new Space();
			 space.setParent(leftHBox);
			 
			Label lb =new Label("OR");
			lb.setStyle("font-weight:bold");
			lb.setParent(leftHBox);
			
			 space = new Space();
			 space.setParent(leftHBox);
			
			chbox = new Checkbox();
			chbox.setId(DWL_ALLPDFCHKID+dlCount);
			chbox.setLabel("All PDF");
			chbox.setParent(leftHBox);
			
			chbox = new Checkbox();
			chbox.setLabel("All DOC");
			chbox.setId(DWL_ALLDOCCHKID+dlCount);
			chbox.setParent(leftHBox);
			
			chbox = new Checkbox();
			chbox.setLabel("All PPT");
			chbox.setId(DWL_ALLPPTCHKID+dlCount);
			chbox.setParent(leftHBox);
			
			leftHBox.setParent(outerHBOx);
			
			Hbox rightHBoxox= new Hbox();
			rightHBoxox.setWidth("200px");
			rightHBoxox.setPack("end");
			
			lb= new Label("*");
			lb.setStyle("color:red;padding-right:5px;");
			lb.setParent(rightHBoxox);
			
			tb =new Textbox();
			tb.setValue("score");
			tb.setId(DWL_MINSCRTXTBXID+dlCount);
			tb.setWidgetListener("onFocus", "setLabel(this);");
			tb.setWidgetListener("onBlur", "setLabel(this);");
			tb.setName("minScore");
			tb.setWidth("40px");
			tb.setParent(rightHBoxox);
			
			img = new Image("/img/icons/delete.png");
			img.setStyle("cursor:pointer;cursor:hand;padding-left:6px;");
			img.addEventListener("onClick", new MyRenderer());
			img.setParent(rightHBoxox);
			
			rightHBoxox.setParent(outerHBOx);
			dlCount++;
			outerHBOx.setParent(downloadedDivId);
		} catch (Exception e) {
			logger.error("Exception : error occured while clicking downloadedImgId **",e);
		}
		
	}
	
	
	/**
	 * Adding new row for Source of Visted
	 */
	public void onClick$sourceOfVisitImgId(){
		try {
			Image img;
			Hbox leftHBox;
			Textbox tb;
			Listbox listBox;
			Listitem li;
			Label lb;
			Space space;
			Hbox outerHBox= new Hbox();
			
			outerHBox.setId(SOV_HBOXID+sovCount);
			outerHBox.setStyle("padding:2px;");
			leftHBox=new Hbox();
			leftHBox.setWidth("500px");
			
			listBox = new Listbox();
			listBox.setId(SOV_LSTID+sovCount);
			
			li = new Listitem("contains");
//			listBox.setSelectedIndex(0);
			li.setSelected(true);
			listBox.setMold("select");
			li.setParent(listBox);
			li = new Listitem("Does not contains");
			li.setParent(listBox);
			li = new Listitem("All these words");
			li.setParent(listBox);
			li = new Listitem("Any of these words");
			li.setParent(listBox);
			listBox.setParent(leftHBox);
			
			space = new Space();
			space.setParent(leftHBox);
			
			tb= new Textbox();
			tb.setId(SOV_TXTBXID+sovCount);
			tb.setParent(leftHBox);
			leftHBox.setParent(outerHBox);
			
			Hbox rightHBox= new Hbox();
			rightHBox.setWidth("200px");
			rightHBox.setPack("end");
			
			lb= new Label("*");
			lb.setStyle("color:red;padding-right:5px;");
			lb.setParent(rightHBox);
			
			tb =new Textbox();
			tb.setId(SOV_MINSCRTXTBXID+sovCount);
			tb.setName("minScore");
			tb.setValue("score");
			tb.setWidgetListener("onFocus", "setLabel(this);");
			tb.setWidgetListener("onBlur", "setLabel(this);");
			tb.setWidth("40px");
			tb.setParent(rightHBox);
			
			img = new Image("/img/icons/delete.png");
			img.setStyle("cursor:pointer;cursor:hand;padding-left:6px;");
			img.addEventListener("onClick", new MyRenderer());
			img.setParent(rightHBox);
			
			rightHBox.setParent(outerHBox);
			sovCount++;
			outerHBox.setParent(sourceOfVisitDivId);
		} catch (Exception e) {
			logger.error("Exception : error occured while clicking downloadedImgId **",e);
		}
		
	}
	
	
	/**
	 * Adding new row for EmailOpen
	 */
	public void onClick$emailOpenedImgId(){

		try {
			Hbox outerHBox = new Hbox();
			outerHBox.setStyle("padding:2px;");
			outerHBox.setId(EOPN_HBOXID+eOpnCount);
			
			Hbox leftHBox = new Hbox();
			leftHBox.setWidth("500px");
			
			Listbox listBox = new Listbox();
			
			listBox.setId(EOPN_LSTID+eOpnCount);
			
			listBox.setModel(new ListModelList(getCampaigns()));
			listBox.setSelectedIndex(0);
			listBox.setItemRenderer(renderer);
			listBox.setMold("select");
			listBox.setParent(leftHBox);
			
			
			leftHBox.setParent(outerHBox);
			
			Hbox rightHBox= new Hbox();
			rightHBox.setWidth("200px");
			rightHBox.setPack("end");
			
			Label lb= new Label("*");
			lb.setStyle("color:red;padding-right:5px;");
			lb.setParent(rightHBox);
			
			Textbox tb =new Textbox();
			tb.setId(EOPN_MINSCRTXTBXID+eOpnCount);
			tb.setName("minScore");
			tb.setValue("score");
			tb.setWidgetListener("onFocus", "setLabel(this);");
			tb.setWidgetListener("onBlur", "setLabel(this);");
			tb.setWidth("40px");
			tb.setParent(rightHBox);
			
			Image img = new Image("/img/icons/delete.png");
			img.setStyle("cursor:pointer;cursor:hand;padding-left:6px;");
			img.addEventListener("onClick", new MyRenderer());
			img.setParent(rightHBox);
			
			rightHBox.setParent(outerHBox);
			eOpnCount++;
			outerHBox.setParent(emailOpenedDivId);
		} catch (Exception e) {
			logger.error("** Exception : error occured while adding the emailOpen HBox **",e);
		}
		
	}
	
	
	/**
	 * Adding new row for Email Clicked
	 */
	public void onClick$emailClickedImgId(){
		try {
			Hbox outerHBox = new Hbox();
			outerHBox.setId(ECLK_HBOXID + eClkCount);
			
			outerHBox.setStyle("padding:2px;");
			Hbox leftHBox = new Hbox();
			leftHBox.setWidth("500px");
			
			Listbox listBox = new Listbox();
			listBox.setId(ECLK_LSTID + eClkCount);
			
			listBox.setModel(new ListModelList(getCampaigns()));
			listBox.setSelectedIndex(0);
			listBox.setItemRenderer(renderer);
			listBox.setMold("select");
			listBox.setParent(leftHBox);
			leftHBox.setParent(outerHBox);
			
			Hbox rightHBox= new Hbox();
			rightHBox.setWidth("200px");
			rightHBox.setPack("end");
			
			Label lb= new Label("*");
			lb.setStyle("color:red;padding-right:5px;");
			lb.setParent(rightHBox);
			
			Textbox tb =new Textbox();
			tb.setName("minScore");
			tb.setValue("score");
			tb.setWidgetListener("onFocus", "setLabel(this);");
			tb.setWidgetListener("onBlur", "setLabel(this);");
			tb.setId(ECLK_MINSCRTXTBXID+eClkCount);
			
			tb.setWidth("40px");
			tb.setParent(rightHBox);
			
			Image img = new Image("/img/icons/delete.png");
			img.setStyle("cursor:pointer;cursor:hand;padding-left:6px;");
			img.addEventListener("onClick", new MyRenderer());
			img.setParent(rightHBox);
			
			rightHBox.setParent(outerHBox);
			outerHBox.setParent(ecl_DivId);
			eClkCount++;
			
		} catch (Exception e) {
			logger.error("Exception : error occured while clicking emailClickedImgId **",e);
		}
		
	}
	
	
	
	/**
	 * Adding new row for Email Not Open
	 */
	public void onClick$emailNotOpenImgId(){
		try {
			Hbox outerHBox = new Hbox();
			outerHBox.setId(ENOP_HBOXID + eNOpndCount);
			
			outerHBox.setStyle("padding:2px;");
			Hbox leftHBox = new Hbox();
			leftHBox.setWidth("500px");
			
			Listbox listBox = new Listbox();
			listBox.setId(ENOP_LSTID + eNOpndCount);
			
			listBox.setModel(new ListModelList(getCampaigns()));
			listBox.setSelectedIndex(0);
			listBox.setItemRenderer(renderer);
			listBox.setMold("select");
			listBox.setParent(leftHBox);
			leftHBox.setParent(outerHBox);
			
			Hbox rightHBox= new Hbox();
			rightHBox.setWidth("200px");
			rightHBox.setPack("end");
			
			Label lb= new Label("*");
			lb.setStyle("color:red;padding-right:5px;");
			lb.setParent(rightHBox);
			
			Textbox tb =new Textbox();
			tb.setId(ENOP_MINSCRTXTBXID + eNOpndCount);
			
			tb.setValue("score");
			tb.setWidgetListener("onFocus", "setLabel(this);");
			tb.setWidgetListener("onBlur", "setLabel(this);");
			tb.setName("minScore");
			tb.setWidth("40px");
			tb.setParent(rightHBox);
			
			Image img = new Image("/img/icons/delete.png");
			img.setStyle("cursor:pointer;cursor:hand;padding-left:6px;");
			img.addEventListener("onClick", new MyRenderer());
			img.setParent(rightHBox);
			
			rightHBox.setParent(outerHBox);
			outerHBox.setParent(emailNotOpenDivId);
			eNOpndCount++;
			
		} catch (Exception e) {
			logger.error("Exception : error occured while clicking EmailNotOpnImgId **",e);
		}
		
	}
	
	
	/**
	 * Adding new row for Email Unsubscribe
	 */
	public void onClick$emailUnsubscribeImgId(){
		try {
			Hbox outerHBox = new Hbox();
			outerHBox.setId(EUSUB_HBOXID + eUSubCount);
			
			outerHBox.setStyle("padding:2px;");
			Hbox leftHBox = new Hbox();
			leftHBox.setWidth("500px");
			Listbox listBox = new Listbox();
			listBox.setId(EUSUB_LSTID + eUSubCount);
			
			listBox.setModel(new ListModelList(getCampaigns()));
			listBox.setSelectedIndex(0);
			listBox.setItemRenderer(renderer);
			listBox.setMold("select");
			listBox.setParent(leftHBox);
			leftHBox.setParent(outerHBox);
			
			Hbox rightHBox= new Hbox();
			rightHBox.setWidth("200px");
			rightHBox.setPack("end");
			
			Label lb= new Label("*");
			lb.setStyle("color:red;padding-right:5px;");
			lb.setParent(rightHBox);
			
			Textbox tb =new Textbox();
			tb.setId(EUSUB_MINSCRTXTBXID + eUSubCount);
			tb.setValue("score");
			tb.setWidgetListener("onFocus", "setLabel(this);");
			tb.setWidgetListener("onBlur", "setLabel(this);");
			tb.setName("minScore");
			tb.setWidth("40px");
			tb.setParent(rightHBox);
			
			Image img = new Image("/img/icons/delete.png");
			img.setStyle("cursor:pointer;cursor:hand;padding-left:6px;");
			img.addEventListener("onClick", new MyRenderer());
			img.setParent(rightHBox);
			
			rightHBox.setParent(outerHBox);
			outerHBox.setParent(emailUnsubscribeDivId);
			eUSubCount++;
			
		} catch (Exception e) {
			logger.error("Exception : error occured while clicking emailUnsubscribImgId **",e);
		}
		
	}
	
	
	
	/**
	 * Adding new row for Form Submition
	 */
	public void onClick$formSubscribeImgId(){
		try {
			Hbox outerHBox = new Hbox();
			
			outerHBox.setId(FSUB_HBOXID+fSubCount);
			outerHBox.setStyle("padding:2px;");
			Hbox leftHBox = new Hbox();
			leftHBox.setWidth("500px");
			
			Listbox listBox = new Listbox();
			listBox.setId(FSUB_LSTID+fSubCount);
			Listitem listitem = new Listitem("form1");
			listitem.setSelected(true);
//			listBox.setSelectedIndex(0);
			listitem.setParent(listBox);
			
			listBox.setMold("select");
			listBox.setParent(leftHBox);
			leftHBox.setParent(outerHBox);
			
			Hbox rightHBox= new Hbox();
			rightHBox.setWidth("200px");
			rightHBox.setPack("end");
			
			Label lb= new Label("*");
			lb.setStyle("color:red;padding-right:5px;");
			lb.setParent(rightHBox);
			
			Textbox tb =new Textbox();
			tb.setId(FSUB_MINSCRTXTBXID+fSubCount);
			tb.setValue("score");
			tb.setWidgetListener("onFocus", "setLabel(this);");
			tb.setWidgetListener("onBlur", "setLabel(this);");
			tb.setName("minScore");
			tb.setWidth("40px");
			tb.setParent(rightHBox);
			
			Image img = new Image("/img/icons/delete.png");
			img.setStyle("cursor:pointer;cursor:hand;padding-left:6px;");
			img.addEventListener("onClick", new MyRenderer());
			img.setParent(rightHBox);
			
			rightHBox.setParent(outerHBox);
			fSubCount++;
			outerHBox.setParent(formSubmitDivId);
		} catch (Exception e) {
			logger.error("Exception : error occured while clicking formSubmitImgId **",e);
		}
		
	}
	
	/**
	 * Adding new row for Form Abondoned
	 */
	
	public void onClick$formAbondonedImgId(){
		try {
			Hbox outerHBox = new Hbox();
			
			outerHBox.setId(FA_HBOXID+fAbndCount);
			outerHBox.setStyle("padding:2px;");
			
			Hbox leftHBox = new Hbox();
			leftHBox.setWidth("500px");
			
			Listbox listBox = new Listbox();
			listBox.setId(FA_LSTID+fAbndCount);
			Listitem listitem = new Listitem("form1");
			listitem.setSelected(true);
			listitem.setParent(listBox);
			
			listBox.setMold("select");
			listBox.setParent(leftHBox);
			leftHBox.setParent(outerHBox);
			
			Hbox rightHBox= new Hbox();
			rightHBox.setWidth("200px");
			rightHBox.setPack("end");
			
			Label lb= new Label("*");
			lb.setStyle("color:red;padding-right:5px;");
			lb.setParent(rightHBox);
			
			Textbox tb =new Textbox();
			tb.setId(FA_MINSCRTXTBXID+fAbndCount);
			tb.setValue("score");
			tb.setWidgetListener("onFocus", "setLabel(this);");
			tb.setWidgetListener("onBlur", "setLabel(this);");
			tb.setName("minScore");
			tb.setWidth("40px");
			tb.setParent(rightHBox);
			
			Image img = new Image("/img/icons/delete.png");
			img.setStyle("cursor:pointer;cursor:hand;padding-left:6px;");
			img.addEventListener("onClick", new MyRenderer());
			img.setParent(rightHBox);
			
			rightHBox.setParent(outerHBox);
			fAbndCount++;
			outerHBox.setParent(formAbondonedDivId);
		} catch (Exception e) {
			logger.error("Exception : error occured while clicking form AbndedImgId **",e);
		}
		
	}
	/**
	 * Adding new row for Form Fill Ratio
	 */
	
	public void onClick$formFillRatioImgId(){
		try {
			Listitem li;
			Hbox outerHBox = new Hbox();
			
			outerHBox.setId(FFR_HBOXID+fFRCount);
			outerHBox.setStyle("padding:2px;");
			
			Hbox leftHBox = new Hbox();
			leftHBox.setWidth("500px");
			leftHBox.setAlign("center");
			
			Listbox leftListBox = new Listbox();
			leftListBox.setId(FFR_NLSTID+fFRCount);
			leftListBox.setName("formnameListBox");
			Listitem listitem = new Listitem("form1");
			listitem.setSelected(true);
//			leftListBox.setSelectedIndex(0);
			listitem.setParent(leftListBox);
			
			listitem.setSelected(true);
			leftListBox.setMold("select");
			leftListBox.setParent(leftHBox);
			leftHBox.setParent(outerHBox);
			
			Listbox rightListBox = new Listbox();
			rightListBox.setId(FFR_CNDLSTID+fFRCount);
			rightListBox.setName("formCndListBox");
			li = new Listitem("is equal to");
//			rightListBox.setSelectedIndex(0);
			li.setSelected(true);
			li.setParent(rightListBox);
			li = new Listitem("is less than");
			li.setParent(rightListBox);
			li = new Listitem("is greater than");
			li.setParent(rightListBox);
			rightListBox.setMold("select");
			rightListBox.setParent(leftHBox);
			
			Intbox formFldRatioIntBox= new Intbox();
			formFldRatioIntBox.setId(FFR_INTBXID+fFRCount);
			formFldRatioIntBox.setWidth("40px");
			formFldRatioIntBox.setParent(leftHBox);
			
			Space space= new Space();
			space.setWidth("2px");
			space.setParent(leftHBox);
			leftHBox.setParent(outerHBox);
			
			Hbox rightHBox= new Hbox();
			rightHBox.setWidth("200px");
			rightHBox.setPack("end");
			
			Label lb= new Label("*");
			lb.setStyle("color:red;padding-right:5px;");
			lb.setParent(rightHBox);
			
			Textbox tb =new Textbox();
			tb.setId(FFR_MINSCRTXTBXID+fFRCount);
			tb.setName("minScore");
			tb.setValue("score");
			tb.setWidgetListener("onFocus", "setLabel(this);");
			tb.setWidgetListener("onBlur", "setLabel(this);");
			tb.setWidth("40px");
			tb.setParent(rightHBox);
			
			Image img = new Image("/img/icons/delete.png");
			img.setStyle("cursor:pointer;cursor:hand;padding-left:6px;");
			img.addEventListener("onClick", new MyRenderer());
			img.setParent(rightHBox);
			
			rightHBox.setParent(outerHBox);
			fFRCount++;
			outerHBox.setParent(formFillRatioDivId);
		} catch (Exception e) {
			logger.error("Exception : error occured while clicking formFillRatioImgId **",e);
		}
		
	}
	
	
	
	List<UserScoreSettings> invaludExistingUSSRecords = new ArrayList<UserScoreSettings>();
	
	
	public boolean setMinScore(Hbox hbox, UserScoreSettings tempUserScoreSettings) {
		try {
			List rightHbxChildList = (List)hbox.getChildren();
			
			for (Object object : rightHbxChildList) {
				
				if(object instanceof Textbox) {
					
					Textbox textbox=(Textbox)object;
					if(!textbox.getName().equalsIgnoreCase("minScore")) continue;
					
					
					try {
						logger.debug("MinScore Value is>>>>>>>"+textbox.getValue());
						int minScoreInt =Integer.parseInt(textbox.getValue().trim());
						tempUserScoreSettings.setScore(minScoreInt);
						return true;
					} catch (Exception e) {
						tempUserScoreSettings.setScore(null);
						logger.error("Exception : Invalid Min Score "+textbox.getValue());
						
						if(tempUserScoreSettings.getId()!=null) {
							invaludExistingUSSRecords.add(tempUserScoreSettings);
						}
						
						return false;
					}
					 
				} // if
			} // for 
			
		} catch (Exception e) {
			logger.error("** Exception :error occured from set minScore **" );
		}
		return false;
		
	} // scoreSetting
	
	
	public void onClick$saveBtnId(){
		
		try {
			logger.debug("Just enter>>>>>>>");
			
			if(Messagebox.show("Invalid data field records will be ignored. Do you want to continue saving?",
					"Update Score Settings", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) != Messagebox.OK) {
				return;
			}
			
//			int minScoreInt;
			Listbox lbox;
			Textbox textbox;
			Hbox leftHbox,rightHbox;
			List leftHbxChildList;
			
			//********************** Page Visited ****************************************
			List<Component> pageVstdDivChldList = pageVisitedDivId.getChildren();
			
			outer: for (Component eachComp : pageVstdDivChldList) {
				Hbox hbox =(Hbox)eachComp;
				try {
					if(!hbox.isVisible()) {
						continue;
					}
					
					UserScoreSettings userScoreSettings = (UserScoreSettings)hbox.getAttribute("scoreObject");
					
					logger.info("----userScoreSettings----:"+userScoreSettings);
					
					if(userScoreSettings==null) {
						userScoreSettings = new UserScoreSettings(user); 
						userScoreSettings.setGroupName(Constants.SCORE_PAGE_VISIT);
						userScoreSettings.setType("score Activity");
					}
					
					leftHbox=(Hbox)hbox.getChildren().get(0);
					leftHbxChildList = (List)leftHbox.getChildren();
					
					Checkbox chbox=(Checkbox)leftHbxChildList.get(0);
					if(chbox.isChecked()){
						userScoreSettings.setCondition("All Visit");
						userScoreSettings.setDataOne(null);
						
					} else {
						
						for (Object object : leftHbxChildList) {
							
							if(object instanceof Listbox) {
								lbox =(Listbox)object;
								logger.debug("page visited Selected Item"+lbox.getSelectedItem().getLabel());
								userScoreSettings.setCondition(lbox.getSelectedItem().getLabel());
							}
							else if(object instanceof Textbox) {
								 textbox = (Textbox)object;
								logger.debug("page visited url value is"+textbox.getValue());
								
								if(textbox.getValue().trim().length()==0) {
									
									if(userScoreSettings.getId()!=null) {
										invaludExistingUSSRecords.add(userScoreSettings);
									}
									
									continue outer;
								}
								userScoreSettings.setDataOne(textbox.getValue());
							} // else
							
						} // for 
						
					} // else
					
					rightHbox=(Hbox)hbox.getChildren().get(1);
					
					if(setMinScore(rightHbox, userScoreSettings)==false) {
						continue;
					}
					
					try {
						int maxScoreInt =Integer.parseInt(pVisitedMaxScoreTbId.getValue().trim());
						userScoreSettings.setMaxScore(maxScoreInt);
					} catch (Exception e) {
						userScoreSettings.setMaxScore(null);
						logger.error("Exception : Invalid Max Score "+pVisitedMaxScoreTbId.getValue());
					}
					
					userScoreSettingsDaoForDML.saveOrUpdate(userScoreSettings);
				} catch (Exception e) {
					logger.error("**Exception : error while saving the pagevisited records **",e);
				}
				
			} // outer for
			
			
			//********************** Downloaded ****************************************
			
				
			List<Component> downloadedDivChldList = downloadedDivId.getChildren();
			 for (Component eachComp  : downloadedDivChldList) {
				 Hbox hbox =(Hbox)eachComp;
				try {
					if(!hbox.isVisible()) {
							continue;
					}

					UserScoreSettings userScoreSettings = (UserScoreSettings)hbox.getAttribute("scoreObject");
					
					logger.info("----userScoreSettings----:"+userScoreSettings);
					
					if(userScoreSettings==null) {
						userScoreSettings = new UserScoreSettings(user); 
						userScoreSettings.setGroupName(Constants.SCORE_DOWNLOAD);
						userScoreSettings.setType("score Activity");
					}
					userScoreSettings.setCondition("equals");
					 
//					userScoreSettings.setType("score Activity");
//					userScoreSettings.setGroupName("Downloaded");
					leftHbox=(Hbox)hbox.getFirstChild();
					leftHbxChildList = (List)leftHbox.getChildren();
					
					String downloadedCndStr="";

					for (Object object : leftHbxChildList) {
					
						if(object instanceof Textbox) {
						 	textbox = (Textbox)object;
							logger.debug("Downloaded url value is"+textbox.getValue());
							downloadedCndStr = downloadedCndStr + textbox.getValue().trim()+"|";
						}
						else if(object instanceof Checkbox) {
					
							Checkbox chbox=(Checkbox)object;
							if(chbox.isChecked() && chbox.getLabel().equalsIgnoreCase("All PDF")) {
								downloadedCndStr = downloadedCndStr + chbox.getLabel()+"|";
							}
							else if(chbox.isChecked() && chbox.getLabel().equalsIgnoreCase("All DOC")) {
								downloadedCndStr=downloadedCndStr + chbox.getLabel()+"|";
							}
							else if(chbox.isChecked() && chbox.getLabel().equalsIgnoreCase("All PPT")) {
								downloadedCndStr=downloadedCndStr + chbox.getLabel()+"|";
							}
							
						} // if
						
					} //for
					
					if(downloadedCndStr.trim().length()==0 || downloadedCndStr.equalsIgnoreCase("|")) {
						if(userScoreSettings.getId()!=null) {
							invaludExistingUSSRecords.add(userScoreSettings);
						}
						
						continue;
					}
					
					userScoreSettings.setDataOne(downloadedCndStr);
									
					rightHbox=(Hbox)hbox.getChildren().get(1);
					
					if(setMinScore(rightHbox, userScoreSettings)==false) {
						continue;
					}
									
					
					try {
						int maxScoreInt =Integer.parseInt(downloadedMaxScoreTbId.getValue().trim());
						userScoreSettings.setMaxScore(maxScoreInt);
					} catch (Exception e) {
						userScoreSettings.setMaxScore(null);
						logger.error("Exception : Invalid Max Score "+downloadedMaxScoreTbId.getValue());
					}
					
					userScoreSettingsDaoForDML.saveOrUpdate(userScoreSettings);
				} catch (Exception e) {
					logger.error("**Exception : error while saving the Downloaded records **",e);
				}

			 }//outer for
				
			
			//********************** Sorce Of Visited ****************************************
			 
				List<Component> sourceOfVisitDivChldList = sourceOfVisitDivId.getChildren();
			
			outer:for (Component eachComp  : sourceOfVisitDivChldList) {
				Hbox hbox2 =(Hbox)eachComp;	
			
				try {
					
					if(!hbox2.isVisible()) {
						continue;
					}

					UserScoreSettings userScoreSettings = (UserScoreSettings)hbox2.getAttribute("scoreObject");

					logger.info("----userScoreSettings----:"+userScoreSettings);

					if(userScoreSettings==null) {
						userScoreSettings = new UserScoreSettings(user); 
						userScoreSettings.setGroupName(Constants.SCORE_SOURCE_OF_VISIT);
						userScoreSettings.setType("score Activity");
					}
					
//					userScoreSettings.setType("score Activity");
//					userScoreSettings.setGroupName("SourceOfVisit");
					leftHbox=(Hbox)hbox2.getFirstChild();
					leftHbxChildList = (List)leftHbox.getChildren();
					for (Object obj : leftHbxChildList) {
						if(obj instanceof Listbox){
							try {
								lbox=(Listbox)obj;
								logger.debug("source os visited Selected Item is"+lbox.getSelectedItem().getLabel());
								userScoreSettings.setCondition(lbox.getSelectedItem().getLabel());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								logger.error("Exception while getting from the listbox selected item",e);
							}
						}
						if(obj instanceof Textbox){
							 textbox=(Textbox)obj;
							logger.debug("source of visited getDataOne"+textbox.getValue());
							if(textbox.getValue().length()==0){
								if(userScoreSettings.getId()!=null) {
									invaludExistingUSSRecords.add(userScoreSettings);
								}
								continue outer;
							}
							
							userScoreSettings.setDataOne(textbox.getValue());
						}
						
					}//for
					
					rightHbox=(Hbox)hbox2.getChildren().get(1);
					
					if(setMinScore(rightHbox, userScoreSettings)==false) {
						continue;
					}
					
					try {
						int maxScoreInt =Integer.parseInt(sourceOfVisitMaxScorTbId.getValue().trim());
						userScoreSettings.setMaxScore(maxScoreInt);
					} catch (Exception e) {
						userScoreSettings.setMaxScore(null);
						logger.error("Exception : Invalid Max Score "+sourceOfVisitMaxScorTbId.getValue());
					}
					
					userScoreSettingsDaoForDML.saveOrUpdate(userScoreSettings);
				} catch (Exception e) {
					logger.error("**Exception : error while saving the SourceOfVisited records **",e);
				}
					
					
			} // outer for
			
			
			//********************** Email Open ****************************************
			
			List<Component> emailOpenedDivChldList = emailOpenedDivId.getChildren();
			
	outer:	for (Component eachComp : emailOpenedDivChldList) {
				Hbox hbox2 = (Hbox)eachComp;
				try {
					if(!hbox2.isVisible()) {
						continue;
					}

					UserScoreSettings userScoreSettings = (UserScoreSettings)hbox2.getAttribute("scoreObject");

					logger.info("----userScoreSettings----:"+userScoreSettings);

					if(userScoreSettings==null) {
						userScoreSettings = new UserScoreSettings(user); 
						userScoreSettings.setGroupName(Constants.SCORE_EMAIL_OPEN);
						userScoreSettings.setType("score Activity");
					}
					
					//UserScoreSettings userScoreSettings = new UserScoreSettings(user); 

					leftHbox=(Hbox)hbox2.getFirstChild();
					leftHbxChildList = (List)leftHbox.getChildren();
					
					for (Object obj : leftHbxChildList) {
						if(obj instanceof Listbox){
							lbox=(Listbox)obj;
							logger.debug("emailOpen Selected Item is"+lbox.getSelectedItem().getLabel());
							
							if(lbox.getSelectedItem().getLabel().equalsIgnoreCase("--Select--")){ 
								if(userScoreSettings.getId()!=null) {
									invaludExistingUSSRecords.add(userScoreSettings);
								}
								continue outer;
							}
							userScoreSettings.setDataOne(lbox.getSelectedItem().getLabel());
						}
						
						
					}
					rightHbox=(Hbox)hbox2.getChildren().get(1);
					
					if(setMinScore(rightHbox, userScoreSettings)==false) {
						continue;
					}
					
					
					try {
						int maxScoreInt =Integer.parseInt(emailOpendMaxScorTbId.getValue().trim());
						userScoreSettings.setMaxScore(maxScoreInt);
					} catch (Exception e) {
						userScoreSettings.setMaxScore(null);
						logger.error("Exception : Invalid Max Score "+emailOpendMaxScorTbId.getValue());
					}
					
					userScoreSettingsDaoForDML.saveOrUpdate(userScoreSettings);
				} catch (Exception e) {
					logger.error("**Exception : error while saving the Email Opend records **",e);
				}
				
			}
			
			
			//**********************Email Clicked****************************************
			
			List<Component> emailClickDivChldList = ecl_DivId.getChildren();
	outer:	for (Component eachComp : emailClickDivChldList) {
				Hbox hbox2 =(Hbox)eachComp;
				try {
					if(!hbox2.isVisible()) {
						continue;
					}

					UserScoreSettings userScoreSettings = (UserScoreSettings)hbox2.getAttribute("scoreObject");

					logger.info("----userScoreSettings >>>>>>>:"+userScoreSettings);

					if(userScoreSettings==null) {
						userScoreSettings = new UserScoreSettings(user); 
						userScoreSettings.setGroupName(Constants.SCORE_EMAIL_CLICK);
						userScoreSettings.setType("score Activity");
					}
					
					
					leftHbox=(Hbox)hbox2.getFirstChild();
					leftHbxChildList = (List)leftHbox.getChildren();
					
					for (Object obj : leftHbxChildList) {
						if(obj instanceof Listbox){
							 lbox=(Listbox)obj;
							logger.debug("emailClicked Selected Item is"+lbox.getSelectedItem().getLabel());
							if(lbox.getSelectedItem().getLabel().equalsIgnoreCase("--Select--")){
								if(userScoreSettings.getId()!=null) {
									invaludExistingUSSRecords.add(userScoreSettings);
								}
								continue outer;
							}
							userScoreSettings.setDataOne(lbox.getSelectedItem().getLabel());
						}
						
						
					} //for
					rightHbox=(Hbox)hbox2.getChildren().get(1);
					
					if(setMinScore(rightHbox, userScoreSettings)==false) {
						continue;
					}

					try {
						int maxScoreInt =Integer.parseInt(emailClickedMaxScorTbId.getValue().trim());
						userScoreSettings.setMaxScore(maxScoreInt);
					} catch (Exception e) {
						userScoreSettings.setMaxScore(null);
						logger.error("Exception : Invalid Max Score "+emailClickedMaxScorTbId.getValue());
					}
					
					userScoreSettingsDaoForDML.saveOrUpdate(userScoreSettings);
				} catch (Exception e) {
					logger.error("**Exception : error while saving the Email Clicked records **",e);
				}
				

				
			}//outer for
			
			
			
			//********************** Email Not Open ****************************************
			
			List<Component> emailNotOpenDivChldList = emailNotOpenDivId.getChildren();
	outer:	for (Component eachComp : emailNotOpenDivChldList) {
				Hbox hbox2 = (Hbox)eachComp;
				try {
					if(!hbox2.isVisible()) {
						continue;
					}

					UserScoreSettings userScoreSettings = (UserScoreSettings)hbox2.getAttribute("scoreObject");

					logger.info("----userScoreSettings----:"+userScoreSettings);

					if(userScoreSettings==null) {
						userScoreSettings = new UserScoreSettings(user); 
						userScoreSettings.setGroupName(Constants.SCORE_EMAIL_NOTOPEN);
						userScoreSettings.setType("score Activity");
					}
					
					
					leftHbox=(Hbox)hbox2.getFirstChild();
					leftHbxChildList = (List)leftHbox.getChildren();
					for (Object obj : leftHbxChildList) {
						if(obj instanceof Listbox){
							lbox=(Listbox)obj;
							logger.debug("emailNotOpen Selected Item is"+lbox.getSelectedItem().getLabel());
							if(lbox.getSelectedItem().getLabel().equalsIgnoreCase("--Select--")) {
								if(userScoreSettings.getId()!=null) {
									invaludExistingUSSRecords.add(userScoreSettings);
								}
								continue outer;
							}
							userScoreSettings.setDataOne(lbox.getSelectedItem().getLabel());
						}
					} //for
					rightHbox=(Hbox)hbox2.getChildren().get(1);
					
					if(setMinScore(rightHbox, userScoreSettings)==false) {
						continue;
					}

					
					try {
						int maxScoreInt =Integer.parseInt(emailNotOpenTbId.getValue().trim());
						userScoreSettings.setMaxScore(maxScoreInt);
					} catch (Exception e) {
						userScoreSettings.setMaxScore(null);
						logger.error("Exception : Invalid Max Score "+emailNotOpenTbId.getValue());
					}
					
					userScoreSettingsDaoForDML.saveOrUpdate(userScoreSettings);
				} catch (Exception e) {
					logger.error("**Exception : error while saving the Email Not Open records **",e);
				}

				
			} //outer for
			
			
			//********************** Email Unsubscribe ****************************************
			
			List<Component> emailUnsubscribeDivChldList = emailUnsubscribeDivId.getChildren();
	outer:	for (Component eachComp : emailUnsubscribeDivChldList) {
				Hbox hbox2 = (Hbox)eachComp;
				try {
					if(!hbox2.isVisible()) {
						continue;
					}

					UserScoreSettings userScoreSettings = (UserScoreSettings)hbox2.getAttribute("scoreObject");

					logger.info("----userScoreSettings----:"+userScoreSettings);

					if(userScoreSettings==null) {
						userScoreSettings = new UserScoreSettings(user); 
						userScoreSettings.setGroupName(Constants.SCORE_EMAIL_UNSUBSCRIBED);
						userScoreSettings.setType("score Activity");
					}
					
					leftHbox=(Hbox)hbox2.getFirstChild();
					 leftHbxChildList = (List)leftHbox.getChildren();
					for (Object obj : leftHbxChildList) {
						if(obj instanceof Listbox){
							lbox=(Listbox)obj;
							logger.debug("emailUnsubscribe Selected Item is"+lbox.getSelectedItem().getLabel());
							if(lbox.getSelectedItem().getLabel().equalsIgnoreCase("--Select--")) {
								if(userScoreSettings.getId()!=null) {
									invaludExistingUSSRecords.add(userScoreSettings);
								}
								continue outer;
							}
							userScoreSettings.setDataOne(lbox.getSelectedItem().getLabel());
						}
					}//for
					rightHbox=(Hbox)hbox2.getChildren().get(1);
					
					if(setMinScore(rightHbox, userScoreSettings)==false) {
						continue;
					}
					
					
					try {
						int maxScoreInt =Integer.parseInt(emailUnsubscribTbId.getValue().trim());
						userScoreSettings.setMaxScore(maxScoreInt);
					} catch (Exception e) {
						userScoreSettings.setMaxScore(null);
						logger.error("Exception : Invalid Max Score "+emailUnsubscribTbId.getValue());
					}
					
					userScoreSettingsDaoForDML.saveOrUpdate(userScoreSettings);
				} catch (Exception e) {
					logger.error("**Exception : error while saving the Email Unsubcrib records **",e);
				}


			} //outer for
			
			
			//**********************Form Submit ****************************************
			
			List<Component> formSubmitDivChldList = formSubmitDivId.getChildren();
			for (Component eachComp : formSubmitDivChldList) {
				Hbox hbox2 = (Hbox)eachComp;
				try {
					if(!hbox2.isVisible()) {
						continue;
					}

					UserScoreSettings userScoreSettings = (UserScoreSettings)hbox2.getAttribute("scoreObject");

					logger.info("----userScoreSettings----:"+userScoreSettings);

					if(userScoreSettings==null) {
						userScoreSettings = new UserScoreSettings(user); 
						userScoreSettings.setGroupName(Constants.SCORE_FORM_SUBMIT);
						userScoreSettings.setType("score Activity");
					}
					
					
					leftHbox=(Hbox)hbox2.getFirstChild();
					 leftHbxChildList = (List)leftHbox.getChildren();
					
					for (Object obj : leftHbxChildList) {
						if(obj instanceof Listbox){
							lbox=(Listbox)obj;
							logger.debug("formSubmit Selected Item is"+lbox.getSelectedItem().getLabel());
							userScoreSettings.setDataOne(lbox.getSelectedItem().getLabel());
						}
					}//for
					
					rightHbox=(Hbox)hbox2.getChildren().get(1);
					
					if(setMinScore(rightHbox, userScoreSettings)==false) {
						continue;
					}
					
					try {
						int maxScoreInt =Integer.parseInt(formSubmitmaxScoreTbId.getValue().trim());
						userScoreSettings.setMaxScore(maxScoreInt);
					} catch (Exception e) {
						userScoreSettings.setMaxScore(null);
						logger.error("Exception : Invalid Max Score "+formSubmitmaxScoreTbId.getValue());
					}
					
					userScoreSettingsDaoForDML.saveOrUpdate(userScoreSettings);
				} catch (Exception e) {
					logger.error("**Exception : error while saving the Form Submit records **",e);
				}

				
			} //outer for
			
			
			//********************** Form Abondoned ****************************************
			
			List<Component> formAbondonedDivChldList = formAbondonedDivId.getChildren();
			for (Component eachComp : formAbondonedDivChldList) {
				Hbox hbox2 = (Hbox)eachComp;
				try {
					if(!hbox2.isVisible()) {
						continue;
					}

					UserScoreSettings userScoreSettings = (UserScoreSettings)hbox2.getAttribute("scoreObject");

					logger.info("----userScoreSettings----:"+userScoreSettings);

					if(userScoreSettings==null) {
						userScoreSettings = new UserScoreSettings(user); 
						userScoreSettings.setGroupName(Constants.SCORE_FORM_ABND);
						userScoreSettings.setType("score Activity");
					}
					
					leftHbox=(Hbox)hbox2.getFirstChild();
					 leftHbxChildList = (List)leftHbox.getChildren();
					for (Object obj : leftHbxChildList) {
						if(obj instanceof Listbox){
							lbox=(Listbox)obj;
							userScoreSettings.setDataOne(lbox.getSelectedItem().getLabel());
						}
					} //for
					rightHbox=(Hbox)hbox2.getChildren().get(1);
					
					if(setMinScore(rightHbox, userScoreSettings)==false) {
						continue;
					}

					try {
						int maxScoreInt =Integer.parseInt(formAbndMaxScorTbId.getValue().trim());
						userScoreSettings.setMaxScore(maxScoreInt);
					} catch (Exception e) {
						userScoreSettings.setMaxScore(null);
						logger.error("Exception : Invalid Max Score "+formAbndMaxScorTbId.getValue());
					}
					
					userScoreSettingsDaoForDML.saveOrUpdate(userScoreSettings);
				} catch (Exception e) {
					logger.error("**Exception : error while saving the Form Abondoned records **",e);
				}
				
			} //outer for
			
			
			//********************** Form Fill Ratio  ****************************************
			
			List<Component> formFillRatioDivChldList = formFillRatioDivId.getChildren();
	outer :	for (Component eachComp : formFillRatioDivChldList) {
				try {
					Hbox hbox2 = (Hbox)eachComp;
					if(!hbox2.isVisible()) {
						continue;
					}

					UserScoreSettings userScoreSettings = (UserScoreSettings)hbox2.getAttribute("scoreObject");

					logger.info("----userScoreSettings----:"+userScoreSettings);

					if(userScoreSettings==null) {
						userScoreSettings = new UserScoreSettings(user); 
						userScoreSettings.setGroupName(Constants.SCORE_FORM_F_RATIO);
						userScoreSettings.setType("score Activity");
					}
					
					leftHbox=(Hbox)hbox2.getFirstChild();
					leftHbxChildList = (List)leftHbox.getChildren();
					
					for (Object obj : leftHbxChildList) {
						if(obj instanceof Listbox){
							lbox=(Listbox)obj;
							String str=lbox.getName();
							 if((lbox.getName().equalsIgnoreCase("formnameListBox"))){
							 		
							 		userScoreSettings.setDataOne(lbox.getSelectedItem().getLabel());
							 } if(lbox.getName().equalsIgnoreCase("formCndListBox")){
								 userScoreSettings.setCondition(lbox.getSelectedItem().getLabel());
							 	}
						}
						if(obj instanceof Intbox){
							Intbox ibox=(Intbox)obj;
							if(ibox.getValue() ==null){
								if(userScoreSettings.getId()!=null) {
									invaludExistingUSSRecords.add(userScoreSettings);
								}
								continue outer;
							}
							 userScoreSettings.setDataTwo(ibox.getValue().toString());
						}
						
					}//for
					
						
					rightHbox=(Hbox)hbox2.getChildren().get(1);
					
					if(setMinScore(rightHbox, userScoreSettings)==false) {
						continue;
					}
					
					
					try {
						int maxScoreInt =Integer.parseInt(formFRatioMaxScorTbId.getValue().trim());
						userScoreSettings.setMaxScore(maxScoreInt);
					} catch (Exception e) {
						userScoreSettings.setMaxScore(null);
						logger.error("Exception : Invalid Max Score "+formFRatioMaxScorTbId.getValue());
					}
					
					userScoreSettingsDaoForDML.saveOrUpdate(userScoreSettings);
					
				} catch (Exception e) {
					logger.error("**Exception : error while saving the Form Fill Ratio records **",e);
				}
			} // outer for
			
			logger.info("invaludExistingUSSRecords:"+invaludExistingUSSRecords.size() +" "+invaludExistingUSSRecords);
			if(invaludExistingUSSRecords.size()>0){
				userScoreSettingsDaoForDML.deleteByCollection(invaludExistingUSSRecords);
			}
			
			// Refresh the screen
			Utility.getXcontents().invalidate();
			
		} catch (Exception e) {
			logger.error("**Exception :error occured while saving the contacts",e);
		}
		
	}
	
	
	

	final class MyRenderer implements EventListener,ListitemRenderer {
		
		
		public MyRenderer() {
			super();
		}
		
		@Override
		public void render(Listitem item, Object obj,int arg2) throws Exception {
			// TODO Auto-generated method stub
			if(obj instanceof Campaigns) {
/*
				if(item.getIndex() == 0) {
					Listcell lc = new Listcell("-- Select --");
					lc.setParent(item);
					return;
				}*/
				
				Campaigns campaign = (Campaigns)obj;
				item.setValue(campaign);
				Listcell lc = new Listcell(campaign.getCampaignName());
				lc.setParent(item);
			}
		}
		@Override
		public void onEvent(Event event) throws Exception {
			try {

				
				Image img=(Image)event.getTarget();
				Hbox hbox=(Hbox)img.getParent().getParent();
				
				UserScoreSettings tempUSSObj = (UserScoreSettings)hbox.getAttribute("scoreObject");
				
				if(tempUSSObj==null) {
					hbox.setVisible(false);
					return;
				}
				else if(Messagebox.show("Are you sure you want to delete these settings?",
						"Delete Score Setting", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK) {
						
					userScoreSettingsDaoForDML.delete(tempUSSObj);
					hbox.setVisible(false);
					return;
				}
				
			} catch (Exception e) {
				logger.error("Exception while deleting the hbox",e);
			}
		}
	}
	
}
