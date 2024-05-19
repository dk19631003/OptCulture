package org.mq.marketer.campaign.controller.program;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.AutoProgram;
import org.mq.marketer.campaign.beans.AutoProgramComponents;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.SMSCampaigns;
import org.mq.marketer.campaign.beans.SwitchCondition;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.AutoProgramComponentsDao;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.ComponentsAndContactsDao;
import org.mq.marketer.campaign.dao.ProgramOnlineReportsDao;
import org.mq.marketer.campaign.dao.SMSCampaignsDao;
import org.mq.marketer.campaign.dao.SwitchConditionDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.ProgramUtility;
import org.zkforge.canvas.Canvas;
import org.zkforge.canvas.Drawable;
import org.zkforge.canvas.Shape;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Window;

public class ProgramAnalysisController extends GenericForwardComposer {
	
	private Session session;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	
	private AutoProgramComponentsDao autoProgramComponentsDao;
	private CampaignsDao campaignsDao;
	private SMSCampaignsDao smsCampaignsDao;
	private SwitchConditionDao switchConditionDao;
	private ProgramOnlineReportsDao programOnlineReportsDao;
	private ComponentsAndContactsDao componentsAndContactsDao;
	private Desktop desktopScope = null;
	
	private Div centerPanelDiv;
	//private Borderlayout borderLayoutId;
	private Canvas centerDiv;
	private AutoProgram autoProgram; 
	
	
	private MyDatebox fromDateboxId;
	private MyDatebox toDateboxId;
	
	Map argumentsMap = new HashMap();
	
	public ProgramAnalysisController() {
		session = Sessions.getCurrent();
		autoProgramComponentsDao = (AutoProgramComponentsDao)SpringUtil.getBean("autoProgramComponentsDao");
		campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
		smsCampaignsDao = (SMSCampaignsDao)SpringUtil.getBean("smsCampaignsDao");
		switchConditionDao = (SwitchConditionDao)SpringUtil.getBean("switchConditionDao");
		programOnlineReportsDao = (ProgramOnlineReportsDao)SpringUtil.getBean("programOnlineReportsDao");
		componentsAndContactsDao = (ComponentsAndContactsDao)SpringUtil.getBean("componentsAndContactsDao");
		desktopScope = Executions.getCurrent().getDesktop();
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		//these width settings are required otherwise canvas width may not be set properly.
		int tempWidth  =  Integer.parseInt(Sessions.getCurrent().getAttribute("screenWidth").toString().trim());
		int tempHeight =  Integer.parseInt(Sessions.getCurrent().getAttribute("screenHeight").toString().trim());
		
		Window tempWin = (Window)comp;
		logger.info("Desktop :: "+tempWidth+"x"+tempHeight);

		logger.info("Window :: "+tempWin.getWidth()+"x"+tempWin.getHeight() + " Left:"+tempWin.getLeft() +" , Top: "+tempWin.getTop());

		tempWidth = tempWidth-20;
		tempHeight = tempHeight-370;
		
		/*int currWidth = getLengthFromStr(borderLayoutId.getWidth());
		int currHeight = getLengthFromStr(borderLayoutId.getHeight());
		
		if(tempWidth < currWidth) tempWidth = currWidth;
		if(tempHeight < currHeight) tempHeight = currHeight;

		
		borderLayoutId.setWidth(tempWidth+"px");
		borderLayoutId.setHeight(tempHeight+"px");*/

		centerPanelDiv.setWidth(tempWidth+"px");
		centerPanelDiv.setHeight((tempHeight-30)+"px");
		
		
		//AutoProgram autoProgram = (AutoProgram)session.getAttribute("viewProgramAnalisys");
		/*if(autoProgram != null) {
			logger.info("got the program object===>"+autoProgram);
			
			viewProgramAnalisys(autoProgram);
			
			
			
		}*/
		/*logger.info("");
		fromDateboxId.setValue((Calendar)desktopScope.getAttribute("fromDate"));
		toDateboxId.setValue((Calendar)desktopScope.getAttribute("toDate"));	
		*/
		
		
		//logger.info("-------no isBack enabled------");
		Calendar cal = MyCalendar.getNewCalendar();
		toDateboxId.setValue(cal);
		logger.debug("ToDate (server) :"+cal);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-1);
		logger.debug("FromDate (server) :"+cal);
		fromDateboxId.setValue(cal);
		
		
	}
	
	private Div popupWindow$popDivId;
	
	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);
		if(event.getTarget() instanceof A) {
			
			A tempLink = (A)event.getTarget();
			String type = (String)tempLink.getAttribute("type");
			
			if(type.equalsIgnoreCase("passedThrough")) {
				
				
				List<Contacts> tempList = (List<Contacts>)tempLink.getAttribute("contacts");
				
				if(tempList == null || tempList.size() <= 0) {
					
					MessageUtil.setMessage("No contacts within selected date criteria.", "color:red;");
					return;
					
				}//if
				
				doPopUp(tempList);
				//tempDiv.setTooltiptext("my list size is====>"+tempList.size());
			} else if(type.equalsIgnoreCase("current")) {
				
				List<Contacts> tempList = (List<Contacts>)tempLink.getAttribute("contacts");
				
				if(tempList == null || tempList.size() <= 0) {
					
					MessageUtil.setMessage("No contacts waiting within selected date criteria.", "color:red;");
					return;
					
				}//if
				
				
				doPopUp(tempList);
				
			}
			popupWindow.setVisible(true);
			popupWindow.setPosition("center");
			popupWindow.doHighlighted();
			
			
			
		}//if
		
		
	}//onEvent
	
	/**
	 * This method returns the width and height as an integer value from passed string.  
	 * @param widthOrHeightWithPx
	 * @return
	 */
	private int getLengthFromStr(String widthOrHeightWithPx) {
		
		try {
			return Integer.parseInt(widthOrHeightWithPx.substring(0, widthOrHeightWithPx.length()-2));
		} catch (Exception e) {
			logger.error("Exception ::" , e);;
		}
		return -1;
	} // getLengthFromStr
	
	
	/**
	 * This method called from client side as and it handles the onChange event on tempLbl(As a known problem 
	 * doAfterCompose is unable to create the program components on canvas while rendering the page itself).
	 */
	public void onChange$tempLblId() {
		
		logger.info("----just entered to edit the program----");
		//logger.info("----just entered in dummy lable event-----");
		autoProgram = (AutoProgram)session.getAttribute("viewProgramAnalysis");
		if(autoProgram != null) {
			logger.info("got the program object===>"+autoProgram);
			
			viewProgramAnalysis(autoProgram);
			
		}//if
		
	}// onChange$tempLblId
	
	private Label progMlListLblId,programNameLblId,programDescLblId;
	
	/**
	 * This method done all the required operations to show the program reports. 
	 * @param autoProgram
	 */
	public void viewProgramAnalysis(AutoProgram autoProgram) {
		

		try {
			
			/**
			 * STEP1: prepare the view as per the earlier settings of the program.
			 * STEP2: save the latest settings(if any) upon the user confimation.
			 */
				Calendar cal = Calendar.getInstance();
				//autoProgram = tempAutoProgram;
			
				if(autoProgram == null) {
					logger.debug("found autoProgram as null,returning.....");
					return;
					
				}//if
				
				/*****these are require after designed th ecomplete screen********/
				//autoProgram.setModifiedDate(cal);
				
				//programSettingsDivId.setAttribute("autoProgram", autoProgram);
				//logger.info("programNameTbId "+programNameTbId);
				programNameLblId.setValue(autoProgram.getProgramName());
				//programNameTbId.setDisabled(true);//not to allow to modify the name of the program.
				
				programDescLblId.setValue(autoProgram.getDescription());
				
				//Vector<String> tempMlNameVector = new Vector<String>();
				Set<MailingList> tempProgMlList = autoProgram.getMailingLists();
				
				//programMlListLbId.setAttribute("mlLists", tempProgMlList);
				//lastModifiedDateLbl.setValue(MyCalendar.calendarToString(autoProgram.getModifiedDate(), MyCalendar.FORMAT_DATETIME_STDATE));
				
				String mlName = "";
				for (MailingList mailingList : tempProgMlList) { // to add each list name in this temp vector so that we can make a selection of those already configured mlLists for this program
					
					if(mlName.length() > 0) mlName += ",";
					mlName += mailingList.getListName();
					
				} // for
				progMlListLblId.setValue(mlName);
				progMlListLblId.setTooltiptext(mlName);
				
				/*for(int i=0; i<programMlListLbId.getItemCount(); i++) { // to make a selection of mlLists for this program
					
					mlName = programMlListLbId.getItemAtIndex(i).getLabel();
					if(tempMlNameVector.contains(mlName))programMlListLbId.setSelectedIndex(i);
					
					
				}// for
*/				
				
				List<AutoProgramComponents> componentsList = autoProgramComponentsDao.getProgramComponents(autoProgram.getProgramId());
				//logger.debug("got====>"+componentsList.size()+"  number of objects for this program "+autoProgram.getProgramId());
				
				String coordArr[] = null;
				Window component = null;
				Div tempDiv = null;
				String bgImagePath = "";
				Label titleLabel = null;
				
				/****for each program component create the component window and try to fetch its online reports.*******************/
				for (AutoProgramComponents tempComponents : componentsList) {
					
					String winId = tempComponents.getComponentWinId();
					
					argumentsMap.put("deSelect", "deSelect");//this makes the window wdn't be selected.
					updateGlobalZIndex(winId);
					
					
					if(winId.startsWith("ACTIVITY_")) {// if it an activity
						coordArr = tempComponents.getCoordinates().split(",");
						
						component = (Window) Executions.createComponents("zul/general/activitycomponent.zul", centerDiv, argumentsMap);
						component.setStyle("position:absolute;padding:1px");
						tempDiv = (Div)component.getFellowIfAny("imageDiv");
						component.setTop(coordArr[1]);
						component.setLeft(coordArr[0]);
						
						
						titleLabel = (Label) component.getFellow("titleLblId");
						Label messageLabel = (Label) component.getFellow("activityMessageLblId");
						Label footerLabel = (Label) component.getFellow("activityFooterLblId");
						((Label)component.getFellowIfAny("prevCompLblId")).setValue(tempComponents.getPreviousId());
						((Label)component.getFellowIfAny("nextCompLblId")).setValue(tempComponents.getNextId());
						
						titleLabel.setValue(tempComponents.getTitle());
						messageLabel.setValue(tempComponents.getMessage());
						footerLabel.setValue(tempComponents.getFooter());
						component.setId(winId);
						
						component.setContext("");//this makes window wdn't have the options properties,delete on its rightclick.
						component.setDraggable("false");//this makes window wdn't be draggable.
						
						if(winId.contains("SEND_EMAIL")) {
							
							
							bgImagePath = "/img/program/draw_activities_send_email_campaign.PNG";
							
						}// if
						else if(winId.contains("SEND_SMS")) {
							
							bgImagePath = "/img/program/draw_activities_send_sms_message.PNG";
							
						}//else if
						else if(winId.contains("SET_DATA")) {
							
							bgImagePath = "/img/program/draw_activities_set_data.PNG";
						}
						
						
						tempDiv.setStyle("background:url("+bgImagePath.substring(1)+");background-repeat:no-repeat; ");
						component.setAttribute("autoProgramComponents", tempComponents);
						
					}// outer if
					else if(winId.startsWith("EVENT_")) {
						
						
						coordArr = tempComponents.getCoordinates().split(",");
						
						//logger.info("the top:"+coordArr[1]+" the left is:"+coordArr[0]);
						
						component = (Window) Executions.createComponents("zul/general/eventcomponent.zul", centerDiv, argumentsMap);
						component.setStyle("position:absolute;padding:1px");
						tempDiv = (Div)component.getFellowIfAny("imageDiv");
						component.setTop(coordArr[1]);
						component.setLeft(coordArr[0]);
						
						titleLabel = (Label) component.getFellow("titleLblId");
						((Label)component.getFellowIfAny("prevCompLblId")).setValue(tempComponents.getPreviousId());
						((Label)component.getFellowIfAny("nextCompLblId")).setValue(tempComponents.getNextId());
						
						 logger.info("tempComponents.getTitle()"+tempComponents.getTitle());
						 titleLabel.setValue(tempComponents.getTitle());
						
						 component.setId(winId);
						 component.setContext("");
						 component.setDraggable("false");
						 
						if(winId.contains("CUST_ACTIVATED")) {
							
							bgImagePath = "/img/program/draw_event_cust_activated.PNG";
							
						}// if
						else if(winId.contains("_END")) {
							
							bgImagePath = "/img/program/draw_event_end.PNG";
						}
						else if(winId.contains("_TARGET_TIMER")) {
							
							bgImagePath = "/img/program/draw_event_target_timer.PNG";
							
							
						}
						else if(winId.contains("_ELAPSE_TIMER")) {
						
							bgImagePath = "/img/program/draw_event_elapse_timer.PNG";
						}
						else if(winId.contains("CUST_DEACTIVATED")) {
							
							bgImagePath = "/img/program/draw_event_cust_deactivated.PNG";
							
						}
						else if(winId.contains("_CUSTOM_EVENT")) {
							
							bgImagePath = "/img/program/draw_event_custom_event.PNG";
							
						}
						else if(winId.contains("_SCHEDULED_FILTER")) {
							
							bgImagePath = "/img/program/draw_event_scheduled_filter.PNG";
							
						}
						tempDiv.setStyle("background:url("+bgImagePath.substring(1)+");background-repeat:no-repeat; ");
						component.setAttribute("autoProgramComponents", tempComponents);
						
					}//else if
					
					else if(winId.contains("SWITCH_")) {
						
						
						coordArr = tempComponents.getCoordinates().split(",");
						
						component = (Window) Executions.createComponents("zul/general/switchcomponent.zul", centerDiv, argumentsMap);
						component.setStyle("position:absolute;padding:1px");
						tempDiv = (Div)component.getFellowIfAny("imageDiv");
						component.setTop(coordArr[1]);
						component.setLeft(coordArr[0]);
						
						titleLabel = (Label) component.getFellow("titleLblId");
						titleLabel.setValue(tempComponents.getTitle());
						
						((Label)component.getFellowIfAny("prevCompLblId")).setValue(tempComponents.getPreviousId());
						 ((Label)component.getFellowIfAny("nextCompLblId")).setValue(tempComponents.getNextId());
						 
						 component.setId(winId);
						 component.setContext("");
						 component.setDraggable("false");
						 
						 if(winId.contains("_DATA")) {
							 
							 bgImagePath = "/img/program/draw_switches_data.PNG";
						
						 
							 Label msgLbl1 = null;
							 Label msgLbl2 = null;
							 
							 //if message lines(line text) are given for this switch,this data will be there in its switchCondition..
							 //try to get message line related text from its switchCondition objects.
							 List<SwitchCondition> tempSwitchCondLst = switchConditionDao.findByComponentId(tempComponents.
									 									getCompId(), autoProgram.getProgramId());
							
							 if(tempSwitchCondLst.size() > 1) {//if switch have two output paths.
								 
								 for (SwitchCondition switchCondition : tempSwitchCondLst) {
									 
									 msgLbl1 = (Label)component.getFellowIfAny("messageLine1Id");
									 msgLbl2 = (Label)component.getFellowIfAny("messageLine2Id");
									 
									 
									 if(switchCondition.getModeFlag().equalsIgnoreCase("false") ) {//false mode
										 
										 
										 msgLbl1.setValue(switchCondition.getLineMessage());
										 
									 }else { //true mode
										 
										 msgLbl2.setValue(switchCondition.getLineMessage());
										 
										 
									 }//else 
									 
								 }//for
								 
							 }else {//if switch have only one output path.
								 
								 SwitchCondition switchCondition = tempSwitchCondLst.get(0);
								 
								 msgLbl1 = (Label)component.getFellowIfAny("messageLine1Id");
								 
								 msgLbl1.setValue(switchCondition.getLineMessage());
								 
								 
							 }//else
							 
							 
							
							
						 }
						 else if(winId.contains("_ALLOCATION")) {
							 
							 bgImagePath = "/img/program/draw_switches_allocation.PNG";
						 }
					 
						 tempDiv.setStyle("background:url("+bgImagePath.substring(1)+");background-repeat:no-repeat; "); 
						 component.setAttribute("autoProgramComponents", tempComponents);
						 
					}// else if
					
					if(!showOnlineReports(component))//it makes the window component will be having its own online reports.
						return;
					
				}//for
				//logger.info("the num of children in main component centerdiv is=====>"+centerDiv.getChildren().size());
				redrawAllLines();//to draw the lines(to connect)b/w all the window components. 
				
		} catch (NumberFormatException e) {
			logger.error("Exception ::" , e);;
		}
		
		
		
	}//viewProgramAnalysis
	
	
	
	/**
	 * This method returns the Window object corresponding to the windowId that is passed.
	 * @param actualWinId
	 * @return
	 */
	private Window getWindowFromCenterDiv(String actualWinId) {
		
		//logger.info("actualWinId="+actualWinId);
		if(actualWinId==null || actualWinId.trim().length()==0) return null;
		
		String winId = (actualWinId.endsWith("w")) ? actualWinId : actualWinId.substring(0, actualWinId.length()-1);
		//logger.info("winId="+winId);

		return (Window)centerDiv.getFellowIfAny(winId);
	}//getWindowFromCenterDiv
	
	
	
	Map<Drawable, String> lineWindows = new HashMap<Drawable, String>();
	
	
	/**
	 * This method draws lines (connect) b/w all the components.
	 * called in viewProgramAnalysis() method.
	 */
	public void redrawAllLines() {
		
		try {
			List<Component> children = centerDiv.getChildren();
			
			logger.debug("the total number of components ====>"+children.size());
			
			centerDiv.clear();
			lineWindows.clear();
			
			// resetSwitchAttibutes(children); // TODO Need to remove after retaining the original attributes 
			
			Window lineFromWin;
			Window lineToWin;
			
			Label fromNextLbl=null;
			
			
			Shape tempShape=null;
			
			for (int i=0; i < children.size(); i++) {
				//logger.info("----for next----");
				lineFromWin = (Window)children.get(i);
				fromNextLbl = (Label)lineFromWin.getFellow("nextCompLblId");
				if(fromNextLbl.getValue()==null || fromNextLbl.getValue().trim().length()==0) continue;
				
				Label toPrevLbl=null;
				
				String winIdArr[] = fromNextLbl.getValue().split(",");
				String winId;
				
				for (String actualWinId : winIdArr) {
					
/*				logger.info("actualWinId="+actualWinId);
					winId = (actualWinId.endsWith("w")) ? actualWinId : actualWinId.substring(0, actualWinId.length()-1);
					logger.info("winId="+actualWinId);
*/				//logger.info("----for previous----");
					lineToWin = getWindowFromCenterDiv(actualWinId);// (Window)centerDiv.getFellowIfAny(winId);
					if(lineToWin==null) continue;
					
					toPrevLbl = (Label)lineToWin.getFellow("prevCompLblId");
					String toPrevStr = toPrevLbl.getValue();
					
					int ind = toPrevStr.indexOf(lineFromWin.getId());
					//logger.info(" this temporary ind is====>"+ind);
					String actPrevWinId = toPrevStr.substring(ind,toPrevStr.indexOf(",", ind));
					
					tempShape = ProgramUtility.getLineForWindws(lineFromWin, lineToWin);
					//logger.info("tempShape is====>"+tempShape);
					if(tempShape != null) {
						
						//logger.info("-----before adding shape to centerDiv----"+centerDiv.size());
						centerDiv.add(tempShape);
						//logger.info("After adding shape to centerDiv----"+centerDiv.size());
						//lineWindows.put(tempShape, lineFromWin.getId()+":"+lineToWin.getId());
						lineWindows.put(tempShape, actPrevWinId+":"+actualWinId);

						//lineWindows.put(tempShape, ProgramUtility.getAvailableId(lineFromWin)+":"+ProgramUtility.getAvailableId(lineToWin));
						
//					storeLineInMap(tempShape, lineFromWin, lineToWin);
						//logger.info("#### LinesMap="+lineWindows);
					}//if
				} // for each winId
				
			} // for i
		} catch (ComponentNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}
		
	}//redrawAllLines
	
	
	
	
	
	
	private int globalzindex = 10;
	
	/**
	 * This method used to set the id for each component.
	 * called in editProgram().this helps to assign the id for newly added components w.r.t the existing components.
	 * @param winId
	 */
	public void updateGlobalZIndex(String winId) {
		
		String gZindex = winId.substring(winId.indexOf("-")+1, winId.length()-1);
		
		int gIndex = Integer.parseInt(gZindex);
		if(globalzindex < gIndex) {
			globalzindex = gIndex+1;
		}else{
			globalzindex += 1;
		}
		
		logger.info("the updated global z index is====>"+globalzindex);
		
	}//updateGlobalZIndex
	
	private Radiogroup fetchrepRgId,fetchOptionRgId;
	private Listbox withInLastLbId,hoursDaysLbId;
	
	
	/**
	 * This method called when user clicks the fetch button to get the online reports of
	 * the program 
	 */
	public void onClick$fetchReportsBtnId() {
		
		logger.debug("----just entered to fetch the online reports-----");
		List<Component> compWinList = centerDiv.getChildren();
		for (Component compWin : compWinList) {
			
			if(!showOnlineReports((Window)compWin))
				return;
			
			
		}//for
		
	}//onClick$fetchReportsBtnId
	
	
	/**
	 * This method helps to get the  required  offset value of hours/days 
	 * called in showOnlineReports(-) method.
	 * @return
	 */
	public Long getLongObject() {
		
		long numberVal = 0l;
		long mins = 0l;
		
		numberVal = Long.parseLong((String)withInLastLbId.getSelectedItem().getValue());
		mins = Long.parseLong((String)hoursDaysLbId.getSelectedItem().getValue());
		Long numOfDays = new Long(numberVal * mins);
		return numOfDays;
		
	}//getLongObject()
	
	//unused method,yet to be delete.
	
	/*public void showOnlineReports(Long offset) {
		
		logger.info("----just entered to show online reports----"+offset);
		List<Window> componentsList = centerDiv.getChildren();
		
		
		String winId = null;
		long current=0;
		long passThrough = 0;
		Long programId = autoProgram.getProgramId();
		
		
		
		for (Window component : componentsList) {
			winId = component.getId();
			
			
			
		}//for
		
		
		
	}//showOnlineReports
*/	
	
	private Window popupWindow;
	
	/**
	 * This method shows the online reports W.r.t each individual component.
	 * called in viewProgramAnalysis(-) and onClick$fetchReportsBtnId() methods. 
	 */
	public boolean showOnlineReports(Window compWin) {
		
		try {
			//TODO need to fetch the online reports for this component.and show them in the view(grid).
			List<Contacts> passeContactsList = null;
			List<Contacts> currentContactsList = null ;
			((Div)compWin.getFellowIfAny("analysisDivId")).setVisible(true);
			
			
			
			long passedThrough = 0;
			long current = 0;
			Radio radio  = fetchrepRgId.getSelectedItem();
			Radio fetchOptionRadio = fetchOptionRgId.getSelectedItem();
			Long offSet = null;
			
			AutoProgramComponents currComp = (AutoProgramComponents)compWin.getAttribute("autoProgramComponents");
			
			String componentWinId = currComp.getComponentWinId();
			String inputCompIdStr = "";
			if(componentWinId.contains(ProgramEnum.EVENT_ELAPSE_TIMER.name())) {
				List<String> waitTimerInputCompList = findTimerInputComps(currComp);
				if(waitTimerInputCompList != null && waitTimerInputCompList.size() > 0) {
					
					for (String compWinId : waitTimerInputCompList) {
					
						if(inputCompIdStr.length() > 0) inputCompIdStr += ",";
						inputCompIdStr += "'"+compWinId+"'";
						
						
					}//for
					
					
				}//if
			
			}//if
			
			else if(componentWinId.contains(ProgramEnum.EVENT_TARGET_TIMER.name())) {
				List<String> waitTimerInputCompList = findTimerInputComps(currComp);
				if(waitTimerInputCompList != null && waitTimerInputCompList.size() > 0) {
					
					for (String compWinId : waitTimerInputCompList) {
					
						if(inputCompIdStr.length() > 0) inputCompIdStr += ",";
						inputCompIdStr += "'"+compWinId+"'";
						
						
					}//for
					
					
				}//if
			
			}//if
			
			
			String countFetchOption = null;
			String contactFetchoption = null;
			
			if(fetchOptionRadio.getId().equalsIgnoreCase("totalCountRBtnId")) {
				
				countFetchOption = "COUNT(prog_rep_id)";
				contactFetchoption = "";
				
			}else if(fetchOptionRadio.getId().equalsIgnoreCase("uniqueCountRBtnId")) {
				
				countFetchOption = "COUNT(distinct comp_contacts_id, component_id)";
				contactFetchoption = "DISTINCT";
			}
			
			
			if(radio.getId().equalsIgnoreCase("specificDayOrHourRBtnId")) {//if within days/hours option
				offSet = getLongObject();//calculates the offset value based on the selections made on withInLastLbId,hoursDaysLbId
				//showOnlineReports(getLongObject());
				//Long offset = getLongObject();
				if(componentWinId.contains(ProgramEnum.EVENT_TARGET_TIMER.name()) ){
					if( isElapsed(currComp.getSupportId())) {
				
					
						passedThrough = programOnlineReportsDao.getPassedThroughCount(currComp,offSet,null,null,null,countFetchOption);
					}else {
						passedThrough = 0;
					}
				}else if(!componentWinId.contains(ProgramEnum.EVENT_TARGET_TIMER.name()) ){
					
					passedThrough = programOnlineReportsDao.getPassedThroughCount(currComp,offSet,null,null,null,countFetchOption);
				}
				passeContactsList = programOnlineReportsDao.getPassedThroughContactsList(currComp, offSet, null, null, null, contactFetchoption);
				//logger.info("=================>yes got my list<=========== "+passeContactsList);
				
					
				if(componentWinId.contains(ProgramEnum.EVENT_ELAPSE_TIMER.name())) {
					
					current = programOnlineReportsDao.getCurrentCount(currComp,offSet,inputCompIdStr,null,null,null,countFetchOption);
					currentContactsList = programOnlineReportsDao.getCurrentContactsList(currComp,offSet,inputCompIdStr,null,null,null,contactFetchoption);
					//logger.info("=================>yes got my list<=========== "+currentContactsList);
					logger.info("-------got the current count for elapsed timer as=====>"+current);
					
				}//if
				else if(componentWinId.contains(ProgramEnum.EVENT_TARGET_TIMER.name()) && !isElapsed(currComp.getSupportId()) ) {
					
					current = programOnlineReportsDao.getCurrentCountForTargetTimer(currComp,offSet,inputCompIdStr,null,null,null,countFetchOption);
					currentContactsList = programOnlineReportsDao.getCurrentContactsListForTargetTimer(currComp,offSet,inputCompIdStr,null,null,null,contactFetchoption);
					
				}
					
			} //if
			
			else if( radio.getId().equalsIgnoreCase("createdDateRBtnId") ) {//if from created date option
				
				Calendar createdDate = currComp.getAutoProgram().getCreatedDate();
				if(componentWinId.contains(ProgramEnum.EVENT_TARGET_TIMER.name()) ){
					if( isElapsed(currComp.getSupportId())) {
				
						passedThrough = programOnlineReportsDao.getPassedThroughCount(currComp,null,createdDate,null,null,countFetchOption);
					}else {
						passedThrough = 0;
					}
				}else if(!componentWinId.contains(ProgramEnum.EVENT_TARGET_TIMER.name()) ) {
					
					passedThrough = programOnlineReportsDao.getPassedThroughCount(currComp,null,createdDate,null,null,countFetchOption);
				}
				
				passeContactsList = programOnlineReportsDao.getPassedThroughContactsList(currComp, null, createdDate, null, null, contactFetchoption);
				if(componentWinId.contains(ProgramEnum.EVENT_ELAPSE_TIMER.name())) {
					
					current = programOnlineReportsDao.getCurrentCount(currComp,null,inputCompIdStr,createdDate,null,null,countFetchOption); 
					currentContactsList = programOnlineReportsDao.getCurrentContactsList(currComp, null, inputCompIdStr, createdDate, null, null,contactFetchoption);
					logger.info("-------got the current count for elapsed timer as=====>"+current);
				}
				else if(componentWinId.contains(ProgramEnum.EVENT_TARGET_TIMER.name()) && !isElapsed(currComp.getSupportId()) ) {
					
					current = programOnlineReportsDao.getCurrentCountForTargetTimer(currComp,null,inputCompIdStr,createdDate,null,null,countFetchOption); 
					currentContactsList = programOnlineReportsDao.getCurrentContactsListForTargetTimer(currComp, null, inputCompIdStr, createdDate, null, null,contactFetchoption);
					logger.info("-------got the current count for elapsed timer as=====>"+current);
					
				}
					
				
			}//else if
			
			else if( radio.getId().equalsIgnoreCase("betweenDateRBtnId") ) {//if between dates option
				
				Calendar fromDate = fromDateboxId.getServerValue();
				Calendar toDate = toDateboxId.getServerValue();
				
				Calendar serverFromDateCal = fromDateboxId.getServerValue();
				Calendar serverToDateCal = toDateboxId.getServerValue();
				
				Calendar tempClientFromCal = fromDateboxId.getClientValue();
				Calendar tempClientToCal = toDateboxId.getClientValue();
				  
				serverFromDateCal.set(Calendar.HOUR_OF_DAY, 
						serverFromDateCal.get(Calendar.HOUR_OF_DAY)-tempClientFromCal.get(Calendar.HOUR_OF_DAY));
				serverFromDateCal.set(Calendar.MINUTE, 
						serverFromDateCal.get(Calendar.MINUTE)-tempClientFromCal.get(Calendar.MINUTE));
				serverFromDateCal.set(Calendar.SECOND, 0);
			
				serverToDateCal.set(Calendar.HOUR_OF_DAY, 
						23+serverToDateCal.get(Calendar.HOUR_OF_DAY)-tempClientToCal.get(Calendar.HOUR_OF_DAY));
				serverToDateCal.set(Calendar.MINUTE, 
						59+serverToDateCal.get(Calendar.MINUTE)-tempClientToCal.get(Calendar.MINUTE));
				serverToDateCal.set(Calendar.SECOND, 59);
			
				if(serverToDateCal.compareTo(serverFromDateCal) < 0){
					MessageUtil.setMessage("'To' date must be later than 'From' date.", "color:red",	"TOP");
					return false;
				}
				 
				
				
				
				/*if(fromDate == null || toDate == null) {
					Messagebox.show("Please provide From Date/To Date properly.");
					return;
					
				}
				*/
				
				if(componentWinId.contains(ProgramEnum.EVENT_TARGET_TIMER.name()) ){
					if( isElapsed(currComp.getSupportId())) {
				
						passedThrough = programOnlineReportsDao.getPassedThroughCount(currComp, null, null, serverFromDateCal, serverToDateCal, countFetchOption);
					}else {
						
						passedThrough = 0;
					}
				}else if(!componentWinId.contains(ProgramEnum.EVENT_TARGET_TIMER.name()) ) {
					
					passedThrough = programOnlineReportsDao.getPassedThroughCount(currComp, null, null, serverFromDateCal, serverToDateCal, countFetchOption);
					
				}
				passeContactsList = programOnlineReportsDao.getPassedThroughContactsList(currComp, null, null, serverFromDateCal, serverToDateCal, contactFetchoption);
				
				if(componentWinId.contains(ProgramEnum.EVENT_ELAPSE_TIMER.name())) {
					
					current = programOnlineReportsDao.getCurrentCount(currComp, null, inputCompIdStr,
								null, serverFromDateCal, serverToDateCal,countFetchOption); 
					
					currentContactsList = programOnlineReportsDao.getCurrentContactsList(currComp, null, inputCompIdStr,
											null, serverFromDateCal, serverToDateCal, contactFetchoption);
					logger.info("-------got the current count for elapsed timer as=====>"+current);
				}
				else if(componentWinId.contains(ProgramEnum.EVENT_TARGET_TIMER.name()) && !isElapsed(currComp.getSupportId()) ) {
					
					current = programOnlineReportsDao.getCurrentCountForTargetTimer(currComp, null, inputCompIdStr,
							null, serverFromDateCal, serverToDateCal,countFetchOption); 
				
					currentContactsList = programOnlineReportsDao.getCurrentContactsListForTargetTimer(currComp, null, inputCompIdStr,
											null, serverFromDateCal, serverToDateCal, contactFetchoption);
					logger.info("-------got the current count for elapsed timer as=====>"+current);
					
					
				}
					
				
				
			}//else if
			
			
			logger.info("-------got the passed through count as=====>"+passedThrough);
			A currLbl = (A)compWin.getFellowIfAny("currentLblId");
			currLbl.setAttribute("type", "current");
			currLbl.setAttribute("contacts", currentContactsList);
			currLbl.addEventListener("onClick",this);
			
			A passedLbl = (A)compWin.getFellowIfAny("passedLblId");
			passedLbl.setAttribute("type", "passedThrough");
			passedLbl.setAttribute("contacts", passeContactsList);
			passedLbl.addEventListener("onClick", this);
			
			if(compWin.getId().contains(ProgramEnum.EVENT_CUST_ACTIVATED.name()) || 
					compWin.getId().contains(ProgramEnum.EVENT_END.name())) {
				
				currLbl.setLabel("");
				passedLbl.setLabel(""+passedThrough);

			}//if
			/*else if(compWin.getId().contains(ProgramEnum.EVENT_TARGET_TIMER.name())){
				
				if(isElapsed(currComp.getSupportId())){
					
					currLbl.setLabel("Reached");
					passedLbl.setLabel("");
				}
				
			
			}*/else {
				currLbl.setLabel(""+current);
				passedLbl.setLabel(""+passedThrough);
			}
			
			return true;
		} catch (Exception e) {
			logger.error("Exception while fetching the online reports of"+compWin.getId(),e);
			return false;
		}
		
		
		
	}//showOnlineReports
	private ListitemRenderer renderer =  new MyRenderer();
	private void doPopUp(List<Contacts> contactsList ){
		if(logger.isDebugEnabled())
			logger.debug("--just entered --");
		
		Div div = (Div)popupWindow.getFellow("popDivId");
		Listbox Lb = (Listbox)popupWindow.getFellowIfAny("contactsListLbId");
		Lb.setModel(new ListModelList(contactsList));
		Lb.setItemRenderer(renderer);
		
		
		
	}
	
	
	
	
	
	
	/**
	 * This method helps to find the current count for ElapseTimer component(As  Elapse Timer component only have the current count).
	 * gives the list of its previous(the components which gives the input data for Elapse Timer)components.
	 * 
	 * called in showOnlineReports(-).
	 * 
	 * @param elapseTimerComp
	 * @return
	 */
	public List<String> findTimerInputComps(AutoProgramComponents TimerComp) {
		
		String prevIds = TimerComp.getPreviousId();
		String[] prev = null;
		List<String> compWinIdList = new ArrayList<String>();
		if(prevIds.contains(",")) {
			
			prev = prevIds.split(",");
			for (String prevId : prev) {
				if(prevId.length() > 0) {
					
					if(!prevId.startsWith("SWITCH_") ){
						
						compWinIdList.add(prevId.substring(0, prevId.length()-1));
						
						
					}else if(prevId.startsWith("SWITCH_") ) {
						compWinIdList.add(prevId);
						
					}
					
					
					
					/*compIdList.add(autoProgramComponentsDao.getIdByWinId(prevId.substring(0, prevId.length()-1), 
							elapseTimerComp.getAutoProgram().getProgramId()));*/
				}//if
				
			}//for
			
			
		}//if
 		
		return compWinIdList;
		
	}//findElapsTimerInputComps

	/**
	 * This method helps to know whether the Target Timer is reached or not.
	 * 
	 * called in 
	 * 
	 * @param sec
	 * @return
	 */
	public boolean isElapsed(Long sec) {
		boolean elapsed = (sec <= Calendar.getInstance().getTimeInMillis() );
		logger.info("-----just entered in isElapsed(-)-----"+elapsed);
		return elapsed;
		
		
		
	}
	
	private class MyRenderer implements ListitemRenderer{
		
		public MyRenderer() {
			
			super();
		}
		
		@Override
		public void render(Listitem li, Object obj, int arg2) throws Exception {
			Contacts contact = null;
			Listcell lc = null;
			
			if(obj instanceof Contacts) {
				contact = (Contacts)obj;
				
				lc = new Listcell(contact.getFirstName());
				lc.setParent(li);
				
				lc= new Listcell(contact.getEmailId());
				lc.setParent(li);
				
				lc = new Listcell();
				if(contact.getMobilePhone() != null) {
					lc.setLabel(contact.getMobilePhone());
					
				}
				/*if(contact.getPhone() != null) {
					lc.setLabel(""+contact.getPhone());
					
				}*/else {
					
					lc.setLabel("--");
				}
				
				lc.setParent(li);
				
				lc = new Listcell(contact.getActivityDate());
				lc.setParent(li);
				
			}
			
			
			
		}
	}
	
	
}
