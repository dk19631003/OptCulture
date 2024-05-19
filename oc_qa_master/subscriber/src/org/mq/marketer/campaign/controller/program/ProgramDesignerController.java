package org.mq.marketer.campaign.controller.program;

import java.awt.geom.Arc2D;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.AutoProgram;
import org.mq.marketer.campaign.beans.AutoProgramComponents;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.SMSCampaigns;
import org.mq.marketer.campaign.beans.SwitchCondition;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.AutoProgramComponentsDao;
import org.mq.marketer.campaign.dao.AutoProgramComponentsDaoForDML;
import org.mq.marketer.campaign.dao.AutoProgramDao;
import org.mq.marketer.campaign.dao.AutoProgramDaoForDML;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.SMSCampaignsDao;
import org.mq.marketer.campaign.dao.SwitchConditionDao;
import org.mq.marketer.campaign.dao.SwitchConditionDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.ProgramUtility;
import org.mq.marketer.campaign.general.QueryGenerator;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.zkforge.canvas.Canvas;
import org.zkforge.canvas.Drawable;
import org.zkforge.canvas.Path;
import org.zkforge.canvas.Rectangle;
import org.zkforge.canvas.Shape;
import org.zkoss.json.JSONValue;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * @author Krishna
 *
 */
public class ProgramDesignerController extends GenericForwardComposer {
	
	private static final long serialVersionUID = 1L;
	
	private static final String LINE_SELECTED_COLOR="#FF0000";
	private static final String LINE_DRAW_COLOR="#116CAE";
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private static double alpha;
	
	//for programSettings
	private Listbox programMlListLbId;
	private Users currentUser;
	//private Set<Long> userIdsSet = GetUser.getUsersSet();
	
	private MailingListDao mailingListDao;
	private UsersDao usersDao;
	
	Map argumentsMap = new HashMap();
	
	
	private Canvas cvs2;
	private Canvas centerDiv;
	
	//private Label modeLblId;
	
	
	private Label shapeDataLb;
	private Label selectedShapeDataLb;
	
	private ListModelList shapeListModel;
	
	private List<Shape> _shapes;
	private List<Shape> _selectedShape;
	
	private List<String> _shapeNames;
	private AutoProgramComponentsDao autoProgramComponentsDao;
	private AutoProgramComponentsDaoForDML autoProgramComponentsDaoForDML;
	private SwitchConditionDao switchConditionDao;
	private SwitchConditionDaoForDML switchConditionDaoForDML;
	private CampaignsDao campaignsDao;
	private SMSCampaignsDao smsCampaignsDao;
	private Session session;
	
	public ProgramDesignerController() {
		session = Sessions.getCurrent();
		currentUser = GetUser.getUserObj();
		mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		autoProgramComponentsDao = (AutoProgramComponentsDao)SpringUtil.getBean("autoProgramComponentsDao");
		autoProgramComponentsDaoForDML = (AutoProgramComponentsDaoForDML)SpringUtil.getBean("autoProgramComponentsDaoForDML");
		autoProgramDao = (AutoProgramDao)SpringUtil.getBean("autoProgramDao");
		autoProgramDaoForDML = (AutoProgramDaoForDML)SpringUtil.getBean("autoProgramDaoForDML");
		switchConditionDao = (SwitchConditionDao)SpringUtil.getBean("switchConditionDao");
		switchConditionDaoForDML = (SwitchConditionDaoForDML)SpringUtil.getBean("switchConditionDaoForDML");
		campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
		smsCampaignsDao = (SMSCampaignsDao)SpringUtil.getBean("smsCampaignsDao");
		usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		//session.setAttribute("programDesignerController", this);
	
	}
	
	
	public void onDrop$programdesignerWinId(Event event) {
		
		logger.debug("---ControllerSide -- Drop Event ="+event);

		DropEvent dropEvent = ((DropEvent) ((ForwardEvent) event).getOrigin());
		Component dragged = dropEvent.getDragged();
		
		logger.debug("----------Dragged target ="+dragged);
		
		if (dragged instanceof Image) {
			// new object
			Image img = (Image) dragged;
			globalzindex++;
			
			Window obj = createNewComp(img);
			obj.setStyle("position:absolute;padding:1px");
			obj.setTop(dropEvent.getY() + "px");
			obj.setLeft(dropEvent.getX() + "px");
			obj.setZindex(globalzindex);
			if(obj.getId().contains("_CUST_ACTIVATED")) {//if if if is a customer activated component
				
				Long isEnabled = 0l;
				obj.setAttribute("supportData", isEnabled);
				
				
			}
		}
		else if (dragged instanceof Window) {
		
			Window obj = (Window) dragged;
			globalzindex++;
			
			obj.setTop(dropEvent.getY() + "px");
			obj.setLeft(dropEvent.getX() + "px");
			obj.setZindex(globalzindex);
			Clients.evalJavaScript("showPrevNode();");
			
			
			// Window tempWin = (Window) dragged;
			
			if( ((Label)obj.getFellow("nextCompLblId")).getValue().trim().length()>0 ||
				((Label)obj.getFellow("prevCompLblId")).getValue().trim().length()>0 ) {
				redrawAllLines();
			} // if
		}
		
		
	} // onDrop$programdesignerWinId
	
	
	/**
	 * This method gets the ProgramEnum object based on the component's image we passed.<BR>
	 * 
	 * Called in createNewComp(-).
	 * 
	 * @param imgName is nothing but the component's image name.
	 * @return ProgramEnum object.
	 */
	private ProgramEnum getProgramEnumByImageName(String imgName) {
		
		ProgramEnum tempEnumArr[] = ProgramEnum.values();
		
		for (ProgramEnum tempEmum : tempEnumArr) {
			if(tempEmum.getDraw_image().trim().startsWith(imgName.trim()+".")) {
				return tempEmum;
			}
		}
		
		return null;
		
	} // getProgramEnumByImageName

	/**
	 * This method will be called when we want to drag the components to other position on the canvas.
	 */
	public void onClick$arrowTbBtnId() {
		
		setDrawMode(0);
		
	}//onClick$arrowTbBtnId()
	
	/**
	 * This method will be called when we want to connect(draw the lines B/W the components)one component to other on the canvas.
	 */
	public void onClick$connectorTbBtnId() {
		
		setDrawMode(1);
		
	}//onClick$connectorTbBtnId()
	
	/**
	 * This method will be called when we select(click on the canvas) on the canvas.<BR>
	 * It will free the earlier selected components from selection.
	 * 
	 * called 
	 * 
	 * @param canvas
	 */
	public void freeSelection(org.zkforge.canvas.Canvas canvas) {
		List children = canvas.getChildren();
		
		for (int i = 0; i < children.size(); i++) {
			Window obj = (Window) children.get(i);
			obj.setBorder("none");
			obj.setStyle("position:absolute;padding:1px");
		}//for
		
	}//freeSelection
	
	
	int globalzindex = 10;//it will be used to set id to the component drawn on canvas.
	
	/**
	 * This method will create a new component window on canvas.
	 * 
	 * called in onDrop$programdesignerWinId() method
	 * 
	 * @param img this is the image which is going to be set as the background of the component.
	 * @return  Window object (the created component's window).
	 */
	private Window createNewComp(Image img) {
		
		// new object

		String imgFilePath = img.getSrc();
		
		String imgFileName = imgFilePath.substring(imgFilePath.lastIndexOf('/')+1, imgFilePath.lastIndexOf('.'));
		
		imgFileName = imgFileName.replace("tb_", "draw_");
		
		ProgramEnum tempEnum = getProgramEnumByImageName(imgFileName);
		
		
		String bgImagePath= imgFilePath.replace("/program/tb_","/program/draw_");
		Window obj=null;
		Div tempDiv=null;
		String enumName=null;
		
		if(bgImagePath.contains("/program/draw_event_")) {
			
			obj = (Window) Executions.createComponents("zul/general/eventcomponent.zul", centerDiv, argumentsMap);
			
			tempDiv = (Div)obj.getFellow("imageDiv");
			Label tempLabel = (Label) obj.getFellow("titleLblId");
			
			tempLabel.setValue(tempEnum.getFooter());
			
		}
		else if(bgImagePath.contains("/program/draw_activities_")) {
			obj = (Window) Executions.createComponents("zul/general/activitycomponent.zul", centerDiv, argumentsMap);
			tempDiv = (Div)obj.getFellow("imageDiv");
			Label titleLabel = (Label) obj.getFellow("titleLblId");
			Label messageLabel = (Label) obj.getFellow("activityMessageLblId");
			Label footerLabel = (Label) obj.getFellow("activityFooterLblId");

			titleLabel.setValue(tempEnum.getTitle());
			messageLabel.setValue(tempEnum.getMessage());
			footerLabel.setValue(tempEnum.getFooter());
			footerLabel.setVisible(true);
			
		}
		else if(bgImagePath.contains("/program/draw_switches_")) {
			
			obj = (Window) Executions.createComponents("zul/general/switchcomponent.zul", centerDiv, argumentsMap);
			tempDiv = (Div)obj.getFellow("imageDiv");
			Label tempLabel = (Label) obj.getFellow("titleLblId");
			tempLabel.setValue(tempEnum.getFooter());	
		} 

		
		obj.setId(tempEnum.name()+"-"+globalzindex+"w");
		
		tempDiv.setStyle("background:url(img/program/"+tempEnum.getDraw_image()+");background-repeat:no-repeat; ");
		
		setComponentTitle(obj);
		return obj;
		
	} //createNewComp
	
	/**
	 * This method acts as a toggle among the drag and connect options.<BR>
	 * which drags and connects the components on the canvas respectively.
	 * 
	 * called in onClick$arrowTbBtnId(), onClick$connectorTbBtnId() methods.
	 *   
	 * @param mode 
	 */
	public void setDrawMode(int mode) {
		
		List children = centerDiv.getChildren();
		if(mode==0) {
			
			for (int i = 0; i < children.size(); i++) {
				((Window) children.get(i)).setDraggable("canvas2");
			}
		}
		else {
			for (int i = 0; i < children.size(); i++) {
				((Window) children.get(i)).setDraggable("false");
			}
		}
		
		for (int i = 0; i < centerDiv.size(); i++) {
		
		} // for i

	} // set Mode
	
	/**
	 * This method will be called when we click on the canvas.<BR>
	 * 
	 * It will free up all the selections we made on the canvas(with the components).
	 * 
	 * (When we click on a particular components,that will be highlighted with the rectangle surrounds it ).
	 * 
	 */
	public void onClick$centerDiv() {
		
		List children = centerDiv.getChildren();
		for (int i = 0; i < children.size(); i++) {
			Window obj = (Window) children.get(i);
			obj.setBorder("none");
			obj.setStyle("position:absolute;padding:1px");
		}//for
		
	}//onClick$centerDiv()
	
	
	/**
	 * not calling anywhere, yet to be remove.
	 * @param winList
	 */
	private void resetSwitchAttibutes(List<Window> winList) {
		for (Window win : winList) {
			if(win.getId().startsWith("SWITCH_")) {
				win.removeAttribute(ProgramUtility.SWITCH_LINES_USED);
			}
		}
	} // resetSwitchAttibutes
	
	
	/**
	 * This method will be called to draw/adjust the lines B/W the components on the canvas in the following scenarioes.
	 * 1.When we delete a particular component.
	 * 2.When we drop a new component on the canvas.
	 * 3.while editing the program.(after all the components of the program are fetched from DB are drawn).
	 * 4.while editting the program template.(After all the components of the program template are fetched from DB are drawn).
	 * 
	 * called in deleteWindowComponentOrLines(),editProgramTemplate(),editProgram(),onDrop$programdesignerWinId().
	 * 
	 */
	public void redrawAllLines() {
		
		try {
			List<Component> children = centerDiv.getChildren();
			
			
			//centerDiv.clear();//need to decide y it is wrking only when i commented this statement
			lineWindows.clear();
			
			// resetSwitchAttibutes(children); // TODO Need to remove after retaining the original attributes 
			
			Window lineFromWin;
			Window lineToWin;
			
			Label fromNextLbl=null;
			
			
			Shape tempShape=null;
			
			for (int i=0; i < children.size(); i++) {
				logger.debug("----for next----");
				lineFromWin = (Window)children.get(i);
				fromNextLbl = (Label)lineFromWin.getFellow("nextCompLblId");
				if(fromNextLbl.getValue()==null || fromNextLbl.getValue().trim().length()==0) continue;
				
				Label toPrevLbl=null;
				
				String winIdArr[] = fromNextLbl.getValue().split(",");
				String winId;
				
				for (String actualWinId : winIdArr) {
					
					logger.debug("----for previous----");
					lineToWin = getWindowFromCenterDiv(actualWinId);// (Window)centerDiv.getFellowIfAny(winId);
					if(lineToWin==null) continue;
					
					toPrevLbl = (Label)lineToWin.getFellow("prevCompLblId");
					String toPrevStr = toPrevLbl.getValue();
					
					int ind = toPrevStr.indexOf(lineFromWin.getId());
					String actPrevWinId = toPrevStr.substring(ind,toPrevStr.indexOf(",", ind));
					
					tempShape = ProgramUtility.getLineForWindws(lineFromWin, lineToWin);
					if(tempShape != null) {
						
						centerDiv.add(tempShape);
						lineWindows.put(tempShape, actPrevWinId+":"+actualWinId);
						
					}
				} // for each winId
				
			} // for i
		} catch (ComponentNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	}//redrawAllLines()
	
	/**
	 * we can delete it,nowhere it is calling. 
	 * @param tempShape
	 * @param lineFromWin
	 * @param lineToWin
	 */
	private void storeLineInMap(Shape tempShape, Window lineFromWin, Window lineToWin) {
		try {
			logger.debug("In STORELINE---"+lineFromWin.getId()+"  ,   "+lineToWin.getId());
			
			if(!lineToWin.getId().startsWith("SWITCH_")) {
				
				lineWindows.put(tempShape, lineFromWin.getId()+":"+lineToWin.getId());
				return;
			}
			
			// If lineToWin is an SWITCH Type.
			
			Label fromNextLbl = (Label)lineFromWin.getFellow("nextCompLblId");
			if(fromNextLbl.getValue()==null || 
					fromNextLbl.getValue().trim().length()==0) { 
				return;
			}
			
			String winIdArr[] = fromNextLbl.getValue().split(",");
			
			for (String actualWinId : winIdArr) {
				if(actualWinId.indexOf(lineToWin.getId())!=-1) {
					lineWindows.put(tempShape, lineFromWin.getId()+":"+actualWinId);
					return;
				}
				
			} // for each winId
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} //storeLineInMap
	
	/**
	 * 
	 * @param event
	 */
	public void onSelect$centerDiv(Event event) {
		
		logger.debug("In onSelect$centerDiv..............." +centerDiv.getChildren().size());
		
		if(centerDiv.getChildren().size() < 2) return;
		
		double[] data = ProgramUtility.getDataValues(event);
	
		List<Component> children = centerDiv.getChildren();
		
		Window lineFromWin=null;
		Window lineToWin=null;
		
		int winPoints[];
		
		for (Component winComp : children) {
			Window win = (Window)winComp;
			winPoints = ProgramUtility.getWindowPoints(win);
			if(winPoints==null) continue;
			
			if(data[0] >= winPoints[0] && data[0] <=winPoints[2] &&
					data[1] >= winPoints[1] && data[1] <=winPoints[3]) {
				logger.debug("X="+win.getLeft()+" Y="+win.getTop()+"  Id="+win.getId()+"------ From win");
				lineFromWin=win;
			} // if


			if(data[2] >= winPoints[0] && data[2] <=winPoints[2] &&
					data[3] >= winPoints[1] && data[3] <=winPoints[3]) {
				logger.debug("X="+win.getLeft()+" Y="+win.getTop()+"  Id="+win.getId()+"------ To win");
				lineToWin=win;
			} // if

		} // for win

		
		if(lineFromWin==null && lineToWin==null) selectLines(data);
		
		if(lineFromWin==null || lineToWin==null || (lineToWin==lineFromWin)) return;

		// Validate if the line can be draws between two windows, And connect its ids
		
		List<String> retList = ProgramUtility.validateAndAddWindowIds(lineFromWin, lineToWin);
		if(retList!=null) {
			
			
			Shape tempShape = ProgramUtility.getLineForWindws(lineFromWin, lineToWin);
			
			if(tempShape != null) {
				centerDiv.add(tempShape);
				//lineWindows.put(tempShape, lineFromWin.getId()+":"+lineToWin.getId());
				
				lineWindows.put(tempShape, retList.get(0)+":"+retList.get(1));
				
			}//if
		}//if
		
	} // onSelect$centerDiv
	
	/**
	 * 
	 */
	public void selectLines(double[] data) {
		
		double startX = Math.min(data[0], data[2]);
		double startY = Math.min(data[1], data[3]);
		double sizeX = Math.abs(data[0] - data[2]);
		double sizeY = Math.abs(data[1] - data[3]);
		
		List<Drawable> tempShapes = new ArrayList<Drawable>();

		for(int i=centerDiv.size()-1; i >= 0; i--) {
			Drawable d = centerDiv.get(i);
			
			if(d instanceof Shape) {
				if( ((Shape)d).intersects(startX, startY, sizeX+1, sizeY+1)) { 
					d.setStrokeStyle(LINE_SELECTED_COLOR);
					tempShapes.add(d);
				} // if
			} // if
		} // for i

		if(tempShapes.size() == 0) {
			
			for(int i=centerDiv.size()-1; i >= 0; i--) {
				Drawable d = centerDiv.get(i);
				
				if(d instanceof Shape &&  d.getStrokeStyle().equals(LINE_SELECTED_COLOR)) {
					d.setStrokeStyle(LINE_DRAW_COLOR);
					tempShapes.add(d);
				} // if
			} // for i
		} // if
		
		if(tempShapes.size() > 0) {
			
			centerDiv.removeAll(tempShapes);
			centerDiv.addAll(tempShapes);
		}//if

	} // selectLines
	
	
	/**
	 * This method will be called when we right click on a particular component to set the properties for it.
	 */
	public void handleMenuitemPropertiesClick(Event event) {
		
		Window tempWin=null;
		
		if(event.getTarget() instanceof Window) {
			tempWin = (Window)event.getTarget();
		}
		else if(event.getTarget().getParent() instanceof Window) {
			tempWin = (Window)event.getTarget().getParent();
		}
		
		//TODO need to show properties window
		
		if(tempWin == null) {
			logger.debug("Unable to find the Target window...");
			return;
		}
		
		showPropertiesWindow(tempWin);
		
	} // onClick$menuitemPropertiesId
	
	
	/**
	 * This method will be called when we right click on a particular component to to remove it.
	 */
	public void handleMenuitemRemoveClick(Event event) {
		
		Window tempWin = null;
		
		if(event.getTarget() instanceof Window) {
			tempWin = (Window)event.getTarget();
		}
		else if(event.getTarget().getParent() instanceof Window) {
			tempWin = (Window)event.getTarget().getParent();
		}
		
		if(tempWin == null) {
			logger.debug("Unable to find the Target window...");
			return;
		}
		
		deleteWindowComponent(tempWin);
		
	} // menuEventHandler
	
	Textbox propertiesWinId$titleTbId,propertiesWinId$messageTbId;
	Button propertiesWinId$propertyIgnoreBtn;
	
	/**
	 * This method saves the properties for each component prepares its support data and sets<BR>
	 * the attributes(supportData,autoProgramComponents etc...) for various components.
	 *  
	 * called when the user clicks on 'save' button of the opened<BR> 
	 * properties window of a particular component. 
	 */
	public void onClick$propertySaveBtnId$propertiesWinId() {
		
		
			try {
				
				String winId = componentWin.getId();
				Label tempLbl = (Label)componentWin.getFellowIfAny("titleLblId");
				
				if(winId.startsWith("ACTIVITY_")) { //if component's category is 'ACTIVITY'.
					
					tempLbl.setValue(propertiesWinId$titleTbId.getText());
					
					tempLbl = (Label)componentWin.getFellowIfAny("activityMessageLblId");
					tempLbl.setValue(propertiesWinId$messageTbId.getText());
					
					tempLbl = (Label)componentWin.getFellow("activityFooterLblId");
					if(winId.contains("_SEND_EMAIL")) {//if it is a Email component 
						
						Campaigns campaign = ((AutoProgramEmailCampaignActivityController)session.getAttribute("MY_COMPOSER")).getCampaignObject();
						if(campaign == null) {
							return;
							
						}
						componentWin.setAttribute("supportData", campaign);
						tempLbl.setValue(campaign.getCampaignName());
					}
					else if(winId.contains("_SEND_SMS")) {//if it is a SMS component
						SMSCampaigns smsCampaign = ((AutoProgramSMSCampaignActivityController)session.
															getAttribute("MY_COMPOSER")).getSmsCampaignObject();
						if(smsCampaign == null) {
							return;
						}
						
						componentWin.setAttribute("supportData", smsCampaign);
						tempLbl.setValue(smsCampaign.getSmsCampaignName());
						
					}//else if
					
				}//if 
				
				else if(winId.startsWith("EVENT_")) {//if component's category is 'EVENT'.

					if(winId.contains("_ELAPSE_TIMER")) {//if it is an Elapse Timer component
						
						Long minOffset = ((AutoProgramElapseTimerEventController)session.getAttribute("MY_COMPOSER")).getLongObject();
						componentWin.setAttribute("supportData", minOffset);
						
					}else if(winId.contains("_TARGET_TIMER")) {//if it is target timer component
						
						Calendar targetTime = ((AutoProgramTargetTimerEventController)session.getAttribute("MY_COMPOSER")).getTargetDateObject();
						componentWin.setAttribute("supportData", targetTime);
						
					}
					else if(winId.contains("_CUST_ACTIVATED")) {//if if if is a customer activated component
						
						Long isEnabled = ((AutoProgramCustActivatedEventController)session.getAttribute("MY_COMPOSER")).isEnabled();
						componentWin.setAttribute("supportData", isEnabled);
						
						
					}
					tempLbl.setValue(propertiesWinId$titleTbId.getText());
					
					
					
				}//else if
				else if(winId.startsWith("SWITCH_")) {//if component's category is 'SWITCH'.
					
					if(winId.contains("_DATA")) {//if it is a switch-data component
						
						
						
						String strToformQuery = ((AutoProgramSwitchDataController)session.getAttribute("MY_COMPOSER")).
													validateAndGetStrToFormQry((Label)componentWin.getFellowIfAny("messageLine1Id"), 
													(Label)componentWin.getFellowIfAny("messageLine2Id"));
						
						logger.debug("str to form query is=====>"+strToformQuery);
						
						if(strToformQuery == null) {
							
							try {
								MessageUtil.setMessage("Please select a valid segmentation rule and ensure both modes (true / false) and the GoTo titles are not same.", "color:red");
								
							} catch (Exception e) {
								
								logger.error("Exception ::" , e);
							}
							return;
						}//if
						
						//modified = false;
						componentWin.setAttribute("filter", strToformQuery.split(Constants.DELIMETER_DOUBLECOLON)[1]);
						componentWin.setAttribute("supportData", strToformQuery);
						componentWin.setAttribute("switchCampMap", DFSForSwitch(componentWin));
						
					}//if
					else if(winId.contains("_ALLOCATION")) {
						
					String tempStr = ((AutoProgramSwitchAllocationController)session.getAttribute("MY_COMPOSER")).
										validateAndGetTempStr((Label)componentWin.getFellowIfAny("messageLine1Id"), 
										(Label)componentWin.getFellowIfAny("messageLine2Id"));
										
						if(tempStr == null) {
							
							MessageUtil.setMessage("Please set the allocation properly.", "color:red");
							return;
						}
						
						componentWin.setAttribute("supportData", tempStr);							
						
						
					}
					tempLbl.setValue(propertiesWinId$titleTbId.getText());
					
				}// else if 
				propertiesWinId.setVisible(false);
			} catch (WrongValueException e) {
				logger.error("Exception ::" , e);
			} catch (ComponentNotFoundException e) {
				logger.error("Exception ::" , e);
			}
		
	}//onClick$propertySaveBtnId$propertiesWinId()
	
	public List<MailingList> getConfiguredMailingLists() {
		
		
		Set mlSet = null;
		List<MailingList> mlList = new ArrayList<MailingList>();
		
			
			mlSet = programMlListLbId.getSelectedItems();
			
			
			if(mlSet != null && mlSet.size() > 0) {	
			
				Listitem item = null;
				MailingList mailingList = null;
				String mlIdStr = "";
				
				
				for (Object object : mlSet) {
					item = (Listitem)object;
					
					mailingList = (MailingList)item.getValue();
					
					if (mlIdStr.length()>0) mlIdStr += ",";
					mlIdStr += mailingList.getListId();
					
					mlList.add(mailingList);
				}//for
		
			}//if
		
		return mlList;
		
		
	}//getConfiguredMailingLists()
	
	
	
	
	
	Window componentWin;
	Include  propertiesWinId$centerIncId;
	
	/**
	 * this method allows to set the properties for each component(window)
	 * depending up on the component type it opens the window consisting the specific properties to be set.
	 * @param win
	 */
	
	public void showPropertiesWindow(Window win) {
		try {
			
			String selectedItem = "";
			componentWin = win;
			String winId = componentWin.getId();
			String winTitle = ((Label)win.getFellowIfAny("titleLblId")).getValue();
			propertiesWinId$titleTbId.setText(winTitle);
			
			if(winId.startsWith("ACTIVITY_")) { //if its category is ACTIVITY
				
				propertiesWinId$messageTbId.setDisabled(false);
				
				
				propertiesWinId$messageTbId.setText(((Label)win.getFellowIfAny("activityMessageLblId")).getValue());

				if(winId.contains("_SEND_EMAIL")) { //if it is a send email component
					
					propertiesWinId$centerIncId.setSrc("zul/program/AutoProgramEmailCampaignActivity.zul");
					
					selectedItem = ((Label)win.getFellowIfAny("activityFooterLblId")).getValue();
					propertiesWinId.setVisible(((AutoProgramEmailCampaignActivityController)session.
							getAttribute("MY_COMPOSER")).setSelectionOfListItem(selectedItem));
					
				}
				else if(winId.contains("_SEND_SMS")) { // if it is a send sms component
					
					propertiesWinId$centerIncId.setSrc("zul/program/AutoProgramSMSCampaignActivity.zul");
					
					selectedItem = ((Label)win.getFellowIfAny("activityFooterLblId")).getValue();
					
					propertiesWinId.setVisible(((AutoProgramSMSCampaignActivityController)session.
							getAttribute("MY_COMPOSER")).setSelectionOfListItem(selectedItem));
					
				}
				else if(winId.contains("_SET_DATA")) {//if it is a set data component
					
					propertiesWinId$centerIncId.setSrc("zul/Empty.zul");
					propertiesWinId.setVisible(true);
				}
				
			}// if
			else if(winId.startsWith("EVENT_")) { // if its category is 'EVENT'
				
				propertiesWinId$messageTbId.setText("");
				propertiesWinId$messageTbId.setDisabled(true);
				 propertiesWinId$titleTbId = (Textbox)propertiesWinId.getFellowIfAny("titleTbId");
				 
				 propertiesWinId$titleTbId.setText(winTitle);
					
					if(winId.contains("_ELAPSE_TIMER")) {
						propertiesWinId$centerIncId.setSrc("zul/program/AutoProgramElapseTimerEvent.zul");
						
						((AutoProgramElapseTimerEventController)session.getAttribute("MY_COMPOSER")).
							setSelectionOfListItem((Long)win.getAttribute("supportData"));
					}
					else if(winId.contains("_TARGET_TIMER")) {
						
						propertiesWinId$centerIncId.setSrc("zul/program/AutoProgramTargetTimerEvent.zul");
						((AutoProgramTargetTimerEventController)session.getAttribute("MY_COMPOSER")).
							setTargetTime((Calendar)win.getAttribute("supportData"));
						
					}
					else if( winId.contains("CUST_ACTIVATED")) {
						
						propertiesWinId$centerIncId.setSrc("zul/program/AutoProgramCustActivatedEvent.zul");
						((AutoProgramCustActivatedEventController)session.getAttribute("MY_COMPOSER")).
						setEnable((Long)win.getAttribute("supportData"));
						
						
					}
					else if(winId.contains("_END") || winId.contains("_CUSTOM_EVENT") || 
							winId.contains("_SCHEDULED_FILTER") || winId.contains("CUST_DEACTIVATED")) {
						
						propertiesWinId$centerIncId.setSrc("zul/Empty.zul");
						
					}
					else {
						propertiesWinId$centerIncId.setSrc("zul/Empty.zul");
					}
				propertiesWinId.setVisible(true);
			}// else if
			
			else if(win.getId().startsWith("SWITCH_")) {//if its category is 'SWITCH'
				
				propertiesWinId$messageTbId.setText("");
				propertiesWinId$messageTbId.setDisabled(true);
				
				propertiesWinId$titleTbId = (Textbox)propertiesWinId.getFellowIfAny("titleTbId");
				 
				propertiesWinId$titleTbId.setText(winTitle);
					
				if(winId.contains("_DATA")) {//if it is a switch-data component
					
					try {
						//ensure that for all other email sending components properties have been set
						String switchErrMsg = validateForSwitch();
						if(switchErrMsg.length() > 0){
							
							MessageUtil.setMessage(switchErrMsg, "color:red;", "top");
							return;
						}
						
						List<MailingList> mlList = getConfiguredMailingLists();
						
						String nextId = ((Label)win.getFellowIfAny("nextCompLblId")).getValue();
						String prevId = ((Label)win.getFellowIfAny("prevCompLblId")).getValue();
						Label msgLbl1 = (Label)win.getFellowIfAny("messageLine1Id");
						Label msgLbl2 = (Label)win.getFellowIfAny("messageLine2Id");
						
						String conditionrules = (String)win.getAttribute("supportData");//the condition rules (if any) applied for this switch.
						
						if(nextId.trim().length() == 0 || prevId.trim().length() == 0) {
							
							Messagebox.show("Make sure that this switch has input/output lines."); 
							return;
						}
						else if(nextId.length() > 0 && prevId.length() > 0) {
							
							//need to traverse backward to get all the campaigns(configured for all ACTIVITY_SEND_EMAIL type components)
							//Map<String,String> progCampMap = DFSForSwitch(win);
							
							//logger.info("the campaigns map in my program is====>"+progCampMap);
							
							Executions.getCurrent().removeAttribute("progCampMap");//doubt here all the time it works perfectly or not
							Executions.getCurrent().removeAttribute("programMailingLists");
							Executions.getCurrent().setAttribute("progCampMap", DFSForSwitch(win));
							Executions.getCurrent().setAttribute("programMailingLists", mlList);
							propertiesWinId$centerIncId.setSrc(null);//is it required...?
							propertiesWinId$centerIncId.setSrc("zul/program/AutoProgramSwitchData.zul");
							
							//get the next components related data.
							
							String[] nextIds = nextId.split(",");
							Label windowtitle;//it holds the next component's title which is required for a switch's goto properties.
							List<Label> windowTitleList = new ArrayList<Label>();
							Window actualWin = null;
							//windowTitleList.clear();
							logger.debug("nextIds of "+winId+" are====>"+nextId);
							
							for (int i = 0; i < nextIds.length; i++) {
								
								actualWin = getWindowFromCenterDiv(nextIds[i]);//returns the actual window component from the centerDiv(canvas)
								
									windowtitle = ((Label)actualWin.getFellowIfAny("titleLblId"));
								
								
								windowtitle.setAttribute("mode", nextIds[i]);//to know the component to which it is giving output
								
								windowTitleList.add(windowtitle);
							}
							
							AutoProgramSwitchDataController tempSwitchDataController = 
										((AutoProgramSwitchDataController)session.getAttribute("MY_COMPOSER"));
							
							if(windowTitleList.size() == 1) { //means single out put line is there
								
								tempSwitchDataController.getNextComponents(windowTitleList,false);
								tempSwitchDataController.setProperties(conditionrules, msgLbl1, msgLbl2, centerDiv, false);
								
							}
							else { // means max(two) number of output lines are there
								
								tempSwitchDataController.getNextComponents(windowTitleList,true);//sets the next components titles
								
								tempSwitchDataController.setProperties(conditionrules, msgLbl1, msgLbl2, centerDiv, true);// to show the configured properties
							}//else
							
						}//if
					}
					catch (Exception e) {
						logger.error("Exception ::" , e);
					}
					
				}//if
				else if(winId.contains("_ALLOCATION")) {
					
					try {
						String nextId = ((Label)win.getFellowIfAny("nextCompLblId")).getValue();
						//String prevId = ((Label)win.getFellowIfAny("prevCompLblId")).getValue();
						Label msgLbl1 = (Label)win.getFellowIfAny("messageLine1Id");
						Label msgLbl2 = (Label)win.getFellowIfAny("messageLine2Id");
						String tempStr = (String)win.getAttribute("supportData");
						if(nextId.trim().length() == 0 ) {
							
							Messagebox.show("Make sure that this switch has output lines."); 
							return;
							
						}
						else if(nextId.trim().length() > 0) {
							
							propertiesWinId$centerIncId.setSrc("zul/program/AutoProgramSwitchAllocation.zul");
							String[] nextIds = nextId.split(",");
							Label windowtitle;//it holds the next component's title which is required for a switch's goto properties.
							List<Label> windowTitleList = new ArrayList<Label>();
							Window actualWin = null;
							//windowTitleList.clear();
							logger.debug("nextIds of "+winId+" are====>"+nextId);
							
							for (int i = 0; i < nextIds.length; i++) {
								
								actualWin = getWindowFromCenterDiv(nextIds[i]);//returns the actual window component from the centerDiv(canvas)
								
									windowtitle = ((Label)actualWin.getFellowIfAny("titleLblId"));
								
								
								windowtitle.setAttribute("mode", nextIds[i]);//to know the component to which it is giving output
								
								windowTitleList.add(windowtitle);
							}
							
							AutoProgramSwitchAllocationController tempSwitchAllocationController = 
								((AutoProgramSwitchAllocationController)session.getAttribute("MY_COMPOSER"));
					
							if(windowTitleList.size() == 1) { //means single out put line is there
								logger.info("yes i have 1 out put line...........");
								tempSwitchAllocationController.getNextComponents(windowTitleList,false);
								tempSwitchAllocationController.setProperties(tempStr, msgLbl1, msgLbl2, centerDiv, false);
								
							}
							else { // means max(two) number of output lines are there
								logger.info("yes i have 2 out put line...........");
								tempSwitchAllocationController.getNextComponents(windowTitleList,true);//sets the next components titles
								tempSwitchAllocationController.setProperties(tempStr, msgLbl1, msgLbl2, centerDiv, true);
							}//else
					
							
							
							
							
						}//else if
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::" , e);
					}
				}
				propertiesWinId.setVisible(true);
			}//else if
		} catch (WrongValueException e) {
			logger.error("Exception ::" , e);
		}
		
		
	}//showPropertiesWindow(-)
	
	private Set<String> visitedCompSet = new HashSet<String>();
	
	/**
	 * This method used to be traverse along the input path of particular switch and <BR>
	 * prepares the email activity components related data in the form of a map(key----->configured campaign name::value----->the email component window id).
	 * @param switchComp the window object representing the switch component.
	 * @return a Map object consists ( key--->Email campaign name , value---> component's window id)<BR>
	 * 
	 * NOTE:if more than one component configured with the same Email campaign,then that<BR>
	 *  	component's window id will be appended to the existing 'value'.
	 * 
	 */
	
	public Map<String,String> DFSForSwitch(Window switchComp) {
		
		Map<String,String> progCampsMap = new HashMap<String, String>();
		
		Stack<Window> s = new Stack<Window>();
		
		visitedCompSet.clear();
		visitedCompSet.add(switchComp.getId());
		boolean reachedRootNode = false;
		s.push(switchComp);
		
		while(!s.empty()) {
			
			

			
			Window tempCompWin = (Window)s.peek();
				
			reachedRootNode = (tempCompWin==switchComp);
			
			Window childCompWin = null;
			
			String tempPrevId =  getUnvisitedChildComp(tempCompWin);
			if(tempPrevId != null) childCompWin = getWindowFromCenterDiv(tempPrevId.substring(0,tempPrevId.length()-1));
			
			
			if(childCompWin != null) {
				visitedCompSet.add(tempPrevId);
				
				//check for reach rootnode,datagiving component,target timer

				//Need to consider dataGiving component type
				
				if(isEmailActivityComponent(childCompWin)==true ) {
					
					progCampsMap = getEmailActivityCampaign(childCompWin,progCampsMap);
					
					
					
				}
					s.push(childCompWin);
					
				
			}
			else {
				s.pop();
			}
			
		}//while
		return progCampsMap;
	}//DFSForSwitch
	
	/**
	 * It helps to run a DFS algorithm to a particular switch.and prepares the map.
	 * called in DFSForSwitch().
	 * @param childCompWindow
	 * @param progCampsMap
	 * @return
	 */
	public Map<String,String> getEmailActivityCampaign(Window childCompWindow, Map<String,String> progCampsMap) {
		
		String compWinId = childCompWindow.getId();
		String progCampMapCompWinId = "";
		String progCampName = ((Label)childCompWindow.getFellowIfAny("activityFooterLblId")).getValue();//((Campaigns)childCompWindow.getAttribute("supportData")).getCampaignName();
		if(progCampsMap.containsKey(progCampName)) {
			progCampMapCompWinId = progCampsMap.get(progCampName);
			if(!progCampMapCompWinId.contains(compWinId)) {
				compWinId += ","+progCampMapCompWinId;
			}
			
			
			
		}
		progCampsMap.put(progCampName, compWinId);
		return progCampsMap;
		
	}//getEmailActivityCampaign
	
	/**
	 * It helps to find whether the passed component is an SEND_EMAILtype or not.
	 * called in DFSForSwitch() method.
	 * @param compWin
	 * @return
	 */
	private boolean isEmailActivityComponent(Window compWin) {
		
		
			String compId = compWin.getId();
			
			boolean foundEmail = compId.contains(ProgramEnum.ACTIVITY_SEND_EMAIL.name());

			return foundEmail;
			
		
		
	}//isEmailActivityComponent
	
	
	/**
	 * This method gives the unvisited child component id(previous Id) of a particuler component.
	 * called in DFSForSwitch().
	 * @param tempComp
	 * @return
	 */
	private String getUnvisitedChildComp( Window tempComp) {
		
		try {
			
			String prevIds = ((Label)tempComp.getFellowIfAny("prevCompLblId")).getValue();
			if(prevIds==null || prevIds.trim().length()==0) return null;
			
			String prevCompIds[] = prevIds.split(",");
			
			for (String prevId : prevCompIds) {
				if(prevId.trim().length() !=0 ) { // if after comma of last id we may have empty string
					//prevId = prevId.substring(0, prevId.length()-1);
					if(!visitedCompSet.contains(prevId)) {
						return prevId;
					}
				}//if
			} // for
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		return null;
		
	} // getUnvisitedChildComp
	
	/**
	 * This method Ensures that properties have been set for all other email sending components.
	 */
	
	public String validateForSwitch() {
		
		List<Component> componentWinList = centerDiv.getChildren();
		String winId = null;
		StringBuffer errSb = new StringBuffer();
		for (Component compWindow : componentWinList) {
			
			winId = compWindow.getId();
			
			if( winId.contains("SEND_EMAIL") ){
				
				if(compWindow.getAttribute("supportData") == null) {
					
					errSb.append("Please set the properties for all other email sending " +
									"components before switch.");
					return errSb.toString();
				}//if
				
			}//if
			
		}//for
	
		Set<Listitem> mlSet = programMlListLbId.getSelectedItems();
		if(mlSet == null || mlSet.size() <= 0) {
			errSb.append("Please select atleast one Mailing List before configuring Properties of switch.");
			return errSb.toString();
			//Messagebox.show("Please select atleast one list for the program.");
				
				
		}
		
		return "";
		
	}
	
	
	private Button saveProgramBtnId;
	private Button saveAsTemplateBtnId;
	
	/**
	 * This method allows us to save a particular program as a template(in the name of 'Captiway' user).<BR>
	 * 
	 * called when the 'save As Template' button has clicked.<BR>
	 * 
	 * Only captiway adminstrator can create program templates.<BR>
	 * This method does the following things.<BR>
	 * 
	 * 1.validate the program design.<BR>
	 * 2.creates/update the top-level(AutoProgram) object.<BR>
	 * 3.creates/update the bottom level(AutoProgramComponents)objects.<BR>
	 * 
	 * NOTE:1.need not to consider the properties that we set for any component in the program.<BR>
	 * 		2.this button will appears only to the Captiway Adminstrator. 
	 * 
	 */
	public void onClick$saveAsTemplateBtnId() {
		
		try {
			//TODO need to do save the same program with some other name for that user
			logger.debug("---just entered1---"+isAdmin);
			
			if(isAdmin) { //if the logged in user is an adminstrator
				//TODO need to save the created program in the name of 'Captiway' user
				logger.debug("---just entered2---"+isAdmin);
				
				if(!validate(true)) {
					
					
					logger.debug("program is not validated returning......");
					return;// by this time we need to validate design for input and out put paths,
					//for component's associated data,reassignment of the properties of switch(if anything has been modified)
					
				}//if
				
				//create/update the top level(AutoProgram) object.
				if(!saveProgramObj(true)) return ;
				
				//create/update the bottom level(AutoProgramComponents) objects.
				saveProgramTemplate();
				
				if(ListOfComponentsToBeDltd.size()>0) autoProgramComponentsDaoForDML.deleteByCollection(ListOfComponentsToBeDltd);
				Messagebox.show("Program template saved successfully.");
				Redirect.goTo(PageListEnum.PROGRAM_AUTO_PROGRAM_SETTINGS);
				
				
			}//if
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
		
		
	}//onClick$saveAsProgBtnId()
	
	
	/**
	 * This method helps to create/update the top-level(AutoProgram) object when we click 'save As...'button.<BR>
	 * 
	 * called in onClick$adminSaveAsTempBtnId$saveAsForAdminWinId(),onClick$adminSaveProgBtnId$saveAsForAdminWinId() methods.
	 *  
	 * @param programAsTemplate specifies whether the program should save as template or not.
	 * @return
	 */
	public boolean saveAsProgramObj(boolean programAsTemplate) {
		
		
		try {
			
			Set mlSet = null;
			Set<MailingList> mlList = new HashSet<MailingList>();
			if(!programAsTemplate) {
				
				mlSet = programMlListLbId.getSelectedItems();
				
				
					if(mlSet == null || mlSet.size() <= 0) {
						
						if(!programAsTemplate) {
							Messagebox.show("Please select atleast one list for the program.");
							
							return false;
						}
						
					}//if
				
				
				
				Listitem item = null;
				MailingList mailingList = null;
				String mlIdStr = "";
				
				
				for (Object object : mlSet) {
					item = (Listitem)object;
					
					mailingList = (MailingList)item.getValue();
					
					if (mlIdStr.length()>0) mlIdStr += ",";
					mlIdStr += mailingList.getListId();
					
					mlList.add(mailingList);
				}//for
			}//if
			
			
			Users tempUser = null;
			
			if(programAsTemplate){
				tempUser = usersDao.findByUsername("Captiway");
			}else{
				tempUser = GetUser.getUserObj();
			}
				
			if(copyOfProgWinId$copyProgNameTbId.getValue().trim().equals("")){
				Messagebox.show("Please provide a program name.");
				return false;
			}	
			
			autoProgram = (AutoProgram)programSettingsDivId.getAttribute("autoProgram");//the high level object associates with program settings window
			
			logger.debug("got Top level object---"+autoProgram);
			
			if(autoProgram == null){// this is the new program(all the basic settings yet to be set)
				
				//logger.info("got autoProgram obj as====>"+autoProgram);
				
				//creates the top level object
				
				if(autoProgramDao.isProgramNameExists(copyOfProgWinId$copyProgNameTbId.getValue(), tempUser.getUserId())) {
					
					Messagebox.show("Program name already exists. Please provide another name.");
					return false;
					
				}
				
				autoProgram = new AutoProgram(copyOfProgWinId$copyProgNameTbId.getValue(),Calendar.getInstance(), Calendar.getInstance(),tempUser );
					
				
				autoProgram.setDescription(programDescTbId.getValue());
				//set the selected mailingLists
				autoProgram.setMailingLists(mlList);
				autoProgram.setStatus("Draft");
				
				
			}
			else { // if program  is already exists (except the program name we given the provision to edit the remaining fields)
					
				if(autoProgramDao.isProgramNameExists(copyOfProgWinId$copyProgNameTbId.getValue(), tempUser.getUserId())) {
					
					Messagebox.show("Program name already exists. Please provide another name.");
					return false;
					
				}
				autoProgram = new AutoProgram(copyOfProgWinId$copyProgNameTbId.getValue(),Calendar.getInstance(), Calendar.getInstance(), tempUser);
				
				autoProgram.setStatus("Draft");
					
				autoProgram.setModifiedDate(Calendar.getInstance());
				autoProgram.setDescription(programDescTbId.getValue());
				autoProgram.setMailingLists(mlList);
			}
			
			//logger.info("mlList is=====>"+mlSet);
			programSettingsDivId.setAttribute("autoProgram", autoProgram);
			autoProgramDaoForDML.saveOrUpdate(autoProgram);
			// the top level object will be saved
			return true;
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return false;
			
		}

	}//saveProgramObj()
	
	
	/**
	 * This method helps to create the top level (AutoProgram ) after all the required validations.<BR>
	 * 
	 * 
	 * called in onClick$saveProgramBtnId(),onClick$saveAsTemplateBtnId(),
	 * 
	 * @param programAsTemplate is a boolean variable which deside among normal program and the program template.<BR>
	 * 
	 * @return true: if the program object created successfully.<BR>
	 * 		   false:if any validation fails.
	 */
	public boolean saveProgramObj(boolean programAsTemplate) {
		
		
		try {
			
			Set mlSet = null;
			Set<MailingList> mlList = new HashSet<MailingList>();
			if(!programAsTemplate) {
				
				mlSet = programMlListLbId.getSelectedItems();
				
				
					if(mlSet == null || mlSet.size() <= 0) {
						
						if(!programAsTemplate) {
							Messagebox.show("Please select atleast one list for the program.");
							
							return false;
						}
						
					}//if
				
				Listitem item = null;
				MailingList mailingList = null;
				String mlIdStr = "";
				
				
				for (Object object : mlSet) {
					item = (Listitem)object;
					
					mailingList = (MailingList)item.getValue();
					
					if (mlIdStr.length()>0) mlIdStr += ",";
					mlIdStr += mailingList.getListId();
					
					mlList.add(mailingList);
				}//for
			}//if
			
			
			Users tempUser = null;
			
			if(programAsTemplate){
				tempUser = usersDao.findByUsername("Captiway");
			}else{
				tempUser = GetUser.getUserObj();
			}
				
			if(programNameTbId.getValue().trim().equals("")){
				Messagebox.show("Please provide a program name.");
				return false;
			}	
			
			autoProgram = (AutoProgram)programSettingsDivId.getAttribute("autoProgram");//the high level object associates with program settings window
			
			logger.info("got Top level object---"+autoProgram);
			
			if(autoProgram == null){// this is the new program(all the basic settings yet to be set)
				
				//logger.info("got autoProgram obj as====>"+autoProgram);
				
				//creates the top level object
				
				if(autoProgramDao.isProgramNameExists(programNameTbId.getValue(), tempUser.getUserId())) {
					
					Messagebox.show("Program name already exists. Please provide another name.");
					return false;
					
				}
				
				autoProgram = new AutoProgram(programNameTbId.getValue(),Calendar.getInstance(), Calendar.getInstance(),tempUser );
				
				autoProgram.setDescription(programDescTbId.getValue());
				
				//set the selected mailingLists
				autoProgram.setMailingLists(mlList);
				autoProgram.setStatus("Draft");
				
				
			}
			else { // if program  is already exists (except the program name we given the provision to edit the remaining fields)
				
				if(programAsTemplate){
					autoProgram.setProgramName(programNameTbId.getValue());
					autoProgram.setUser(tempUser);
				}else{
					String  tempStr = (String)session.getAttribute("editProgram");
					if(tempStr != null && tempStr.equalsIgnoreCase("use")) {
						
						if(autoProgramDao.isProgramNameExists(programNameTbId.getValue(), tempUser.getUserId())) {
							
							Messagebox.show("Program name already exists. Please provide another name.");
							return false;
							
						}
						autoProgram = new AutoProgram(programNameTbId.getValue(),Calendar.getInstance(), Calendar.getInstance(), tempUser);
						
						
						autoProgram.setStatus("Draft");
						//TODO need to create new AutoProgramComponents too for this new program
					}//if
					
					
				}//else
				
				autoProgram.setModifiedDate(Calendar.getInstance());
				autoProgram.setDescription(programDescTbId.getValue());
				autoProgram.setMailingLists(mlList);
			}
			
			//logger.info("mlList is=====>"+mlSet);
			programSettingsDivId.setAttribute("autoProgram", autoProgram);
			autoProgramDaoForDML.saveOrUpdate(autoProgram);// the top level object will be saved
			return true;
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return false;
			
		}

	}//saveProgramObj()
	
	
	/**
	 * This method calls when we click on 'save program' button which save the program into DB.
	 * 
	 * 
	 */
	
	public void onClick$saveProgramBtnId() {
		try {
			logger.debug("----just entered to save the program----");
			//TODO need to save the program
			
			if(!validate(false)) {
				
				logger.debug("program is not validated returning......");
				return;// by this time we need to validate design for input and out put paths,
				//for component's associated data,reassignment of the properties of switch(if anything has been modified)
				
			}//if
			
			if(!saveProgramObj(false)) return;
			
			logger.debug("The program is validated..continuing further.....");
			
			String tempStr = (String)session.getAttribute("editProgram");
			
			if(tempStr != null && tempStr.equalsIgnoreCase("use")) {
				// need to associate each component of canvas to this newly created program.
				AutoProgramComponents tempComp = null;
				List<Component> templateCompWin = centerDiv.getChildren();
				
				for (Component windowComp : templateCompWin) {
					Window window = (Window)windowComp;
					
					tempComp = (AutoProgramComponents)window.getAttribute("autoProgramComponents");
					
					if(tempComp != null) {
						
						tempComp.setCompId(null);
						tempComp.setAutoProgram(autoProgram);
						
					}//if
					
					
				}//for
				
				
			}//if
			
			saveProgram();
			
			//if there are any components asked for deletion
			if(ListOfComponentsToBeDltd.size()>0) autoProgramComponentsDaoForDML.deleteByCollection(ListOfComponentsToBeDltd);
			
			if( !autoProgram.getStatus().equals("Active")) {
				
				if(	Messagebox.show("Program saved successfully. Click OK button to Publish it now.", "Confirm",
						 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK ) {
					
					// TODO need to change the status.
					autoProgram.setStatus("Active");
					autoProgramDaoForDML.saveOrUpdate(autoProgram);
					
				}//if
				Redirect.goTo(PageListEnum.PROGRAM_AUTO_PROGRAM_SETTINGS);
			}//if 
			else {
				Messagebox.show("Program saved successfully."); 
				Redirect.goTo(PageListEnum.PROGRAM_AUTO_PROGRAM_SETTINGS);
			}//else
			
			programNameTbId.setDisabled(true);
			switchCampSet.clear();//this is the set holds the deleted campaigns which are required for a particular switch
			
			
		}
		catch (Exception e) {
			logger.error("Exception ::" , e);
		
		}
	}//onClick$saveProgramBtnId()
	
	
	/**
	 * This method helps to save the program template along with its bottom level component(AutoProgramComponents)objects.<BR>
	 *  
	 * NOTE:This method lets not to consider the configured properties (if any) for the components of a program template.
	 */
	public void saveProgramTemplate() {
		
		try {
			
			AutoProgramComponents autoProgramComponents = null;
			String id = "";
			String title = "";
			String message = "";
			String footer = "";
			String previousId = "";
			String nextId = "";
			String coordinates = "";
			String type = "";
			Textbox tempTitleTb = null;
			Label tempLbl = null;
			
			Messagebox.show("If any component has been configured with specific properties, those will be ignored");
			
			List<Component> components = centerDiv.getChildren();// to make all the components of this program to be saved in to DB
			
			List<AutoProgramComponents> programComponentsList = new ArrayList<AutoProgramComponents>();// hold all the AutoProgramComponent objects
			
			for (Component eachComponentWindow : components) {// for each child of canvas(centerDIv)
				Window componentWindow = (Window)eachComponentWindow;
				/*****************Set the common properties**************************************************/
				id = componentWindow.getId();
				
				previousId = ((Label)componentWindow.getFellowIfAny("prevCompLblId")).getValue();
				nextId = ((Label)componentWindow.getFellowIfAny("nextCompLblId")).getValue();
				coordinates = componentWindow.getLeft()+","+componentWindow.getTop();
				
				//logger.info("previousId===="+previousId+"      "+"nextId===="+nextId);
				
				autoProgramComponents = (AutoProgramComponents)componentWindow.getAttribute("autoProgramComponents");
				
				if(autoProgramComponents == null) {//if new 
					
					autoProgramComponents = new AutoProgramComponents(id.substring(0, id.indexOf("-")),
									coordinates, previousId,nextId, autoProgram, id);
				}
				else {// if exists
					
					autoProgramComponents.setPreviousId(previousId);
					autoProgramComponents.setNextId(nextId);
					autoProgramComponents.setCoordinates(coordinates);
					
				}
				
				/******************Set the specific properties**************************************************************/
				
				if(id.startsWith("ACTIVITY_")) {//if it is an activity type component
					
					//basic & general settings of a component
					title = ((Label)componentWindow.getFellowIfAny("titleLblId")).getValue();
					message = ((Label)componentWindow.getFellowIfAny("activityMessageLblId")).getValue();
					//footer = ((Label)componentWindow.getFellowIfAny("activityFooterLblId")).getValue();
					if(id.contains("SEND_EMAIL")) {
						footer = "Select Campaign";
					}else if(id.contains("SEND_SMS")) {
						footer = "Select SMS";
					}
					
					autoProgramComponents.setTitle(title);
					autoProgramComponents.setMessage(message);
					autoProgramComponents.setFooter(footer);
					
					
				}else if(id.startsWith("EVENT_")) { // if it is an event type component
					
					//basic & general settings of a component
					title = ((Label)componentWindow.getFellowIfAny("titleLblId")).getValue();
					autoProgramComponents.setTitle(title);
					
				}else if(id.startsWith("SWITCH_")) {// if it is a switch type component
					
					title = ((Label)componentWindow.getFellowIfAny("titleLblId")).getValue();
					autoProgramComponents.setTitle(title);
					
				}// else if
				componentWindow.setAttribute("autoProgramComponents", autoProgramComponents);// need when object is going to save in DB
				
				programComponentsList.add(autoProgramComponents);
			}//for each comp
			
			//autoProgramComponentsDao.saveByCollection(programComponentsList);
			autoProgramComponentsDaoForDML.saveByCollection(programComponentsList);
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
		
		
	}//saveProgramTemplate()
	
	/**
	 * This method helps to copy the existing program with other name<BR>(this copied program may have other modifications too).<BR><BR>
	 * @param copiedProg is a AutoProgram object created as a copy of existing(selected) program
	 */
	public void saveCopiedProg(AutoProgram copiedProg) {
		

		try {
			//TODO need to write only saving of program and program components.
			
			AutoProgramComponents autoProgramComponents = null;
			String id = "";
			String title = "";
			String message = "";
			String footer = "";
			String previousId = "";
			String nextId = "";
			String coordinates = "";
			String type = "";
			Textbox tempTitleTb = null;
			Label tempLbl = null;
			
			
			Set<MailingList> mlSet = copiedProg.getMailingLists();
			
			String mlIdStr = "";
			for (MailingList mailingList : mlSet) {
				//item = (Listitem)object;
				
				//mailingList = (MailingList)item.getValue();
				
				if (mlIdStr.length()>0) mlIdStr += ",";
				mlIdStr += mailingList.getListId();
				
				//mlList.add(mailingList);
			}
			
			
			
			List<Window> switchCompWinList = new ArrayList<Window>();//used to process the switch components further
			
			switchCompWinList.clear();
			
			
			List<Component> components = centerDiv.getChildren();// to make all the components of this program to be saved in to DB
			
			List<AutoProgramComponents> programComponentsList = new ArrayList<AutoProgramComponents>();// hold all the AutoProgramComponent objects
			
			for (Component eachComponentWindow : components) {// for each child of canvas(centerDIv)
				Window componentWindow = (Window)eachComponentWindow;
				/*****************Set the common properties**************************************************/
				id = componentWindow.getId();
				previousId = ((Label)componentWindow.getFellowIfAny("prevCompLblId")).getValue();
				nextId = ((Label)componentWindow.getFellowIfAny("nextCompLblId")).getValue();
				coordinates = componentWindow.getLeft()+","+componentWindow.getTop();
				
				//logger.info("previousId===="+previousId+"      "+"nextId===="+nextId);
				
				autoProgramComponents = (AutoProgramComponents)componentWindow.getAttribute("autoProgramComponents");
				
				if(autoProgramComponents == null) {//if new 
					autoProgramComponents = new AutoProgramComponents(id.substring(0, id.indexOf("-")),
									coordinates, previousId,nextId, copiedProg, id);
				}
				else {// if exists
					autoProgramComponents.setCompId(null);
					autoProgramComponents.setAutoProgram(copiedProg);
					autoProgramComponents.setPreviousId(previousId);
					autoProgramComponents.setNextId(nextId);
					autoProgramComponents.setCoordinates(coordinates);
					
				}
				
				/******************Set the specific properties**************************************************************/
				
				if(id.startsWith("ACTIVITY_")) {//if it is an activity type component
					
					//basic & general settings of a component
					title = ((Label)componentWindow.getFellowIfAny("titleLblId")).getValue();
					message = ((Label)componentWindow.getFellowIfAny("activityMessageLblId")).getValue();
					footer = ((Label)componentWindow.getFellowIfAny("activityFooterLblId")).getValue();
					
					autoProgramComponents.setTitle(title);
					autoProgramComponents.setMessage(message);
					autoProgramComponents.setFooter(footer);
					
					//specific settings of a component
					if(id.contains("_SEND_EMAIL")) {
						
						autoProgramComponents.setSupportId(((Campaigns)componentWindow.getAttribute("supportData")).getCampaignId());
						
					}else if(id.contains("_SEND_SMS")) {
						
						autoProgramComponents.setSupportId(((SMSCampaigns)componentWindow.getAttribute("supportData")).getSmsCampaignId());
						
					}
					
				}else if(id.startsWith("EVENT_")) { // if it is an event type component
					
					//basic & general settings of a component
					title = ((Label)componentWindow.getFellowIfAny("titleLblId")).getValue();
					autoProgramComponents.setTitle(title);
					
					//specific settings of a component
					if(id.contains("_ELAPSE_TIMER")) {
						
						autoProgramComponents.setSupportId((Long)componentWindow.getAttribute("supportData"));
						
					} else if(id.contains("_TARGET_TIMER")) {
						
						autoProgramComponents.setSupportId(new Long(((Calendar)componentWindow.getAttribute("supportData")).getTimeInMillis()));
						
					}else if(id.contains("_CUST_ACTIVATED")) {
						
						autoProgramComponents.setSupportId((Long)componentWindow.getAttribute("supportData"));
						
					}
					
				}else if(id.startsWith("SWITCH_")) {// if it is a switch type component
					
					title = ((Label)componentWindow.getFellowIfAny("titleLblId")).getValue();
					autoProgramComponents.setTitle(title);
					
						//segmentQueryStr = QueryGenerator.generateListSegmentQuery( segmentRuleStr, true);
						
						switchCompWinList.add(componentWindow);//this list will be processed further
						
						
				
					
				}// else if
				componentWindow.setAttribute("autoProgramComponents", autoProgramComponents);// need when object is going to save in DB
				
				programComponentsList.add(autoProgramComponents);
			}//for each comp
			
			//autoProgramComponentsDao.saveByCollection(programComponentsList);
			autoProgramComponentsDaoForDML.saveByCollection(programComponentsList);
			
			//*********process the list of switch components inorder to make conditions********
			
			Long winId = null;
			SwitchCondition switchCondition = null;
			String tempStr = "";
			String supportData = "";
			String query = "";
			String tempArr[] = null;
			Label msgLabel1 = null;
			Label msgLabel2 = null;
			String dataSubQry = null;
			
			String openCampWinIds = "";
			String clickCampWinIds = "";
			
			String filter = null;
			String rulesArr[] = null;
			String[] activityRuleArr = null;
			/**
			 * for each output line create a switch condition object,before creating delete the existing conditions(if any) for 
			 * this component
			 */
			
			
			for(Window switchCompWindow :switchCompWinList) {
				
				
				query = "";
				supportData = (String)switchCompWindow.getAttribute("supportData");// support data is nothing but the condition string
				
				
				logger.info("==============winId============="+switchCompWindow.getId());
				winId = autoProgramComponentsDao.getIdByWinId(switchCompWindow.getId(), copiedProg.getProgramId()); 
				logger.info("==============winId============="+winId);
				//switchConditionDao.executeUpdate("DELETE FROM SwitchCondition where componentId="+winId+" and programId= "+copiedProg.getProgramId());
				switchConditionDaoForDML.executeUpdate("DELETE FROM SwitchCondition where componentId="+winId+" and programId= "+copiedProg.getProgramId());
				
				
				
				
				
				if(switchCompWindow.getId().startsWith("SWITCH_DATA")) {
				//logger.info("the supportData is=====>"+supportData);
				rulesArr = supportData.replace(supportData.substring(supportData.indexOf(Constants.DELIMETER_DOUBLECOLON)), "").split(Constants.ADDR_COL_DELIMETER);
				
				filter = (String)switchCompWindow.getAttribute("filter");
				
				if(filter.equals("2") || filter.equals("3") ) {
					
					activityRuleArr = rulesArr[1].split("\\|\\|");//Any:||ACTIVITY_SEND_EMAIL-12w|Opens||ACTIVITY_SEND_EMAIL-13w|Clicks::3
					String ruleTokenArr[] = null;
					
					for (int i=1; i<activityRuleArr.length ;i++) {
						
						logger.debug("activityRuleArr====>"+activityRuleArr[i]);
						ruleTokenArr = activityRuleArr[i].split("\\|");
						logger.debug("ruleTokenArr====>"+ruleTokenArr[1]);
						if(ruleTokenArr[1].equalsIgnoreCase("Opens")) {
							
							if(openCampWinIds.length() > 0) openCampWinIds += ",";
							openCampWinIds += ruleTokenArr[0];
							
						}else if(ruleTokenArr[1].equalsIgnoreCase("Clicks")) {
							
							if(clickCampWinIds.length() > 0) clickCampWinIds += ",";
							clickCampWinIds += ruleTokenArr[0];
							
						}// else if
						
						
					}//for
					
					
				}//if
				
				logger.debug("openCampWinIds=====>"+openCampWinIds+" clickCampWinIds=====>"+clickCampWinIds );
				
				//supportData = supportData.split(Constants.ADDR_COL_DELIMETER)[0];
				 
				query = QueryGenerator.generateSwitchConditionQuery(rulesArr[0].replace("<mlIdsToBeReplaced>", mlIdStr), true);// generate the actual executable query
				
				
				}
				
				
				//for each output line examine the condition make an entry in switch_condition based on the mode 
				//as of now only two are allowed 
				//this is for the first output line 
				
				msgLabel1 = (Label)switchCompWindow.getFellowIfAny("messageLine1Id");
				
				tempStr = (String)msgLabel1.getAttribute("moveTo");//this is attribute holds mode||next component('true||ACTIVITY_SEND_EMAIL-11w0')
				
				tempArr = tempStr.split("\\|\\|");//need to modify this value when the next component asked to be deleted
				
				switchCondition = new SwitchCondition(winId, query, supportData, tempArr[1], msgLabel1.getValue(), 
								switchCompWindow.getId(),copiedProg.getProgramId(), tempArr[0], openCampWinIds, clickCampWinIds);

				//switchConditionDao.saveOrUpdate(switchCondition);
				switchConditionDaoForDML.saveOrUpdate(switchCondition);
				
				if( ( (Label)switchCompWindow.getFellowIfAny("nextCompLblId") ).getValue().split(",").length > 1) {
					//this is for the second output line
					msgLabel2 = (Label)switchCompWindow.getFellowIfAny("messageLine2Id");
					
					tempStr = (String)msgLabel2.getAttribute("moveTo");
					
					tempArr = tempStr.split("\\|\\|");
					switchCondition = new SwitchCondition(winId, query, supportData, tempArr[1],msgLabel2.getValue(),
										switchCompWindow.getId(), copiedProg.getProgramId(),tempArr[0], openCampWinIds, clickCampWinIds);

					//switchConditionDao.saveOrUpdate(switchCondition);
					switchConditionDaoForDML.saveOrUpdate(switchCondition);

				}// if
				
			} //for
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
		
		
	}//saveCopiedProg()
	
	
	
	//added to reuse the same code for programtemplate creation
	public void saveProgram() {
		try {
			//TODO need to write only saving of program and program components.
			
			AutoProgramComponents autoProgramComponents = null;
			String id = "";
			String title = "";
			String message = "";
			String footer = "";
			String previousId = "";
			String nextId = "";
			String coordinates = "";
			String type = "";
			Textbox tempTitleTb = null;
			Label tempLbl = null;
			
			
			Set<MailingList> mlSet = autoProgram.getMailingLists();
			
			String mlIdStr = "";
			for (MailingList mailingList : mlSet) {
				//item = (Listitem)object;
				
				//mailingList = (MailingList)item.getValue();
				
				if (mlIdStr.length()>0) mlIdStr += ",";
				mlIdStr += mailingList.getListId();
				
				//mlList.add(mailingList);
			}
			
			
			
			List<Window> switchCompWinList = new ArrayList<Window>();//used to process the switch components further
			
			switchCompWinList.clear();
			
			
			List<Component> components = centerDiv.getChildren();// to make all the components of this program to be saved in to DB
			
			List<AutoProgramComponents> programComponentsList = new ArrayList<AutoProgramComponents>();// hold all the AutoProgramComponent objects
			
			for (Component eachComponentWindow : components) {// for each child of canvas(centerDIv)
				Window componentWindow = (Window)eachComponentWindow;
				
				/*****************Set the common properties**************************************************/
				id = componentWindow.getId();
				previousId = ((Label)componentWindow.getFellowIfAny("prevCompLblId")).getValue();
				nextId = ((Label)componentWindow.getFellowIfAny("nextCompLblId")).getValue();
				coordinates = componentWindow.getLeft()+","+componentWindow.getTop();
				
				//logger.info("previousId===="+previousId+"      "+"nextId===="+nextId);
				
				autoProgramComponents = (AutoProgramComponents)componentWindow.getAttribute("autoProgramComponents");
				
				if(autoProgramComponents == null) {//if new 
					autoProgramComponents = new AutoProgramComponents(id.substring(0, id.indexOf("-")),
									coordinates, previousId,nextId, autoProgram, id);
				}
				else {// if exists
					autoProgramComponents.setPreviousId(previousId);
					autoProgramComponents.setNextId(nextId);
					autoProgramComponents.setCoordinates(coordinates);
					
				}
				
				/******************Set the specific properties**************************************************************/
				
				if(id.startsWith("ACTIVITY_")) {//if it is an activity type component
					
					//basic & general settings of a component
					title = ((Label)componentWindow.getFellowIfAny("titleLblId")).getValue();
					message = ((Label)componentWindow.getFellowIfAny("activityMessageLblId")).getValue();
					footer = ((Label)componentWindow.getFellowIfAny("activityFooterLblId")).getValue();
					
					autoProgramComponents.setTitle(title);
					autoProgramComponents.setMessage(message);
					autoProgramComponents.setFooter(footer);
					
					//specific settings of a component
					if(id.contains("_SEND_EMAIL")) {
						autoProgramComponents.setSupportId(((Campaigns)componentWindow.getAttribute("supportData")).getCampaignId());
					}else if(id.contains("_SEND_SMS")) {
						autoProgramComponents.setSupportId(((SMSCampaigns)componentWindow.getAttribute("supportData")).getSmsCampaignId());
					}
					
				}else if(id.startsWith("EVENT_")) { // if it is an event type component
					
					//basic & general settings of a component
					title = ((Label)componentWindow.getFellowIfAny("titleLblId")).getValue();
					autoProgramComponents.setTitle(title);
					
					//specific settings of a component
					if(id.contains("_ELAPSE_TIMER")) {
						autoProgramComponents.setSupportId((Long)componentWindow.getAttribute("supportData"));
					} else if(id.contains("_TARGET_TIMER")) {
						autoProgramComponents.setSupportId(new Long(((Calendar)componentWindow.getAttribute("supportData")).getTimeInMillis()));
						
					}else if(id.contains("_CUST_ACTIVATED")) {
						autoProgramComponents.setSupportId((Long)componentWindow.getAttribute("supportData"));
						
					}
					
				}else if(id.startsWith("SWITCH_")) {// if it is a switch type component
					
					title = ((Label)componentWindow.getFellowIfAny("titleLblId")).getValue();
					autoProgramComponents.setTitle(title);
					
						//segmentQueryStr = QueryGenerator.generateListSegmentQuery( segmentRuleStr, true);
						
						switchCompWinList.add(componentWindow);//this list will be processed further
						
						
					
					
				}// else if
				componentWindow.setAttribute("autoProgramComponents", autoProgramComponents);// need when object is going to save in DB
				
				programComponentsList.add(autoProgramComponents);
			}//for each comp
			
			//autoProgramComponentsDao.saveByCollection(programComponentsList);
			autoProgramComponentsDaoForDML.saveByCollection(programComponentsList);
			
			//*********process the list of switch components inorder to make conditions********
			
			Long winId = null;
			SwitchCondition switchCondition = null;
			String tempStr = "";
			String supportData = "";
			String query = "";
			String tempArr[] = null;
			Label msgLabel1 = null;
			Label msgLabel2 = null;
			String dataSubQry = null;
			
			String openCampWinIds = "";
			String clickCampWinIds = "";
			
			String filter = null;
			String rulesArr[] = null;
			String[] activityRuleArr = null;
			/**
			 * for each output line create a switch condition object,before creating delete the existing conditions(if any) for 
			 * this component
			 */
			
			
			for(Window switchCompWindow :switchCompWinList) {
				query = "";
				supportData = (String)switchCompWindow.getAttribute("supportData");// support data is nothing but the condition string
				
				
				winId = autoProgramComponentsDao.getIdByWinId(switchCompWindow.getId(), autoProgram.getProgramId()); 
				//switchConditionDao.executeUpdate("DELETE FROM SwitchCondition where componentId="+winId+" and programId= "+autoProgram.getProgramId());
				switchConditionDaoForDML.executeUpdate("DELETE FROM SwitchCondition where componentId="+winId+" and programId= "+autoProgram.getProgramId());
				
				//logger.info("the supportData is=====>"+supportData);
				
				if(switchCompWindow.getId().startsWith("SWITCH_DATA")) {
					rulesArr = supportData.replace(supportData.substring(supportData.indexOf(Constants.DELIMETER_DOUBLECOLON)), "").split(Constants.ADDR_COL_DELIMETER);
					
					filter = (String)switchCompWindow.getAttribute("filter");
					
					if(filter.equals("2") || filter.equals("3") ) {
						
						activityRuleArr = rulesArr[1].split("\\|\\|");//Any:||ACTIVITY_SEND_EMAIL-12w|Opens||ACTIVITY_SEND_EMAIL-13w|Clicks::3
						String ruleTokenArr[] = null;
						
						for (int i=1; i<activityRuleArr.length ;i++) {
							
							logger.debug("activityRuleArr====>"+activityRuleArr[i]);
							ruleTokenArr = activityRuleArr[i].split("\\|");
							logger.debug("ruleTokenArr====>"+ruleTokenArr[1]);
							if(ruleTokenArr[1].equalsIgnoreCase("Opens")) {
								
								if(openCampWinIds.length() > 0) openCampWinIds += ",";
								openCampWinIds += ruleTokenArr[0];
								
							}else if(ruleTokenArr[1].equalsIgnoreCase("Clicks")) {
								
								if(clickCampWinIds.length() > 0) clickCampWinIds += ",";
								clickCampWinIds += ruleTokenArr[0];
								
							}// else if
							
							
						}//for
						
						
					}//if
					
					logger.debug("openCampWinIds=====>"+openCampWinIds+" clickCampWinIds=====>"+clickCampWinIds );
					
					//supportData = supportData.split(Constants.ADDR_COL_DELIMETER)[0];
					 
					query = QueryGenerator.generateSwitchConditionQuery(rulesArr[0].replace("<mlIdsToBeReplaced>", mlIdStr), true);// generate the actual executable query
				}//if switchdata
					
					//for each output line examine the condition make an entry in switch_condition based on the mode 
					//as of now only two are allowed 
					//this is for the first output line 
					
					msgLabel1 = (Label)switchCompWindow.getFellowIfAny("messageLine1Id");
					
					tempStr = (String)msgLabel1.getAttribute("moveTo");//this is attribute holds mode||next component('true||ACTIVITY_SEND_EMAIL-11w0')
					
					tempArr = tempStr.split("\\|\\|");//need to modify this value when the next component asked to be deleted
					
					switchCondition = new SwitchCondition(winId, query, supportData, tempArr[1], msgLabel1.getValue(), 
									switchCompWindow.getId(),autoProgram.getProgramId(), tempArr[0], openCampWinIds, clickCampWinIds);
					//logger.info("data to be inserted is=====>"+winId+"         "+query +"            "+supportData +"     "+tempStr.split("\\|\\|")[1] +"   "+((Label)switchCompWindow.getFellowIfAny("messageLine1Id")).getValue());
	
					//switchConditionDao.saveOrUpdate(switchCondition);
					switchConditionDaoForDML.saveOrUpdate(switchCondition);
					
					if( ( (Label)switchCompWindow.getFellowIfAny("nextCompLblId") ).getValue().split(",").length > 1) {
						//this is for the second output line
						msgLabel2 = (Label)switchCompWindow.getFellowIfAny("messageLine2Id");
						
						tempStr = (String)msgLabel2.getAttribute("moveTo");
						
						tempArr = tempStr.split("\\|\\|");
						switchCondition = new SwitchCondition(winId, query, supportData, tempArr[1],msgLabel2.getValue(),
											switchCompWindow.getId(), autoProgram.getProgramId(),tempArr[0], openCampWinIds, clickCampWinIds);
	
						//switchConditionDao.saveOrUpdate(switchCondition);
						switchConditionDaoForDML.saveOrUpdate(switchCondition);
						
					}// if
				
			} //for
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
		
		
	}//saveProgram()
	
	
	private boolean isSaved = false;//
	private Window programdesignerWinId;
	
	/**
	 * This method cancels program design operation.
	 */
	public void onClick$cancelProgramBtnId() {
		try {
			
			if(!isSaved) {
			if( Messagebox.show("Closing the window will retain saved settings only. Are you sure you want to continue?","Confirm",
					 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK ){
				
				programdesignerWinId.setVisible(false);
				Components.removeAllChildren(centerDiv);
				centerDiv.clear();
				lineWindows.clear();
				//programNameTbId.setConstraint("");
				programNameTbId.setDisabled(false);
				programNameTbId.setValue("");
				programDescTbId.setValue("");
				programMlListLbId.setSelectedIndex(-1);
				Redirect.goTo(PageListEnum.PROGRAM_AUTO_PROGRAM_SETTINGS);
			}//if
			}else{
				Redirect.goTo(PageListEnum.PROGRAM_AUTO_PROGRAM_SETTINGS);
				
			}
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		
		
	}//onClick$cancelProgramBtnId
	
	/**
	 * Remove all lines of the Window component
	 * @param tempWin
	 */
	public void removeAllLines(Window tempWin) {
		deleteWindowComponentOrLines(tempWin, false);
	}

	
	/**
	 * 
	 * @param actualWinId
	 * @return
	 */
	private Window getWindowFromCenterDiv(String actualWinId) {
		
		//logger.info("actualWinId="+actualWinId);
		if(actualWinId==null || actualWinId.trim().length()==0) return null;
		
		String winId = (actualWinId.endsWith("w")) ? actualWinId : actualWinId.substring(0, actualWinId.length()-1);
		//logger.info("winId="+winId);

		return (Window)centerDiv.getFellowIfAny(winId);
	}
	
	/**
	 * Delete the Window component
	 * @param tempWin
	 */
	List<AutoProgramComponents> ListOfComponentsToBeDltd = new ArrayList<AutoProgramComponents>();
	
	
	/**
	 * this method help to delete the componentwhich is asked for deletion.
	 * @param tempWin
	 */
	public void deleteWindowComponent(Window tempWin) {
		try {
			
			AutoProgramComponents tempComponentToBeDltd = (AutoProgramComponents)tempWin.getAttribute("autoProgramComponents");
			if(tempComponentToBeDltd != null) {
				if( Messagebox.show("Are you sure you want to delete the component?","Deleting Component",
						 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK ){
					deleteWindowComponentOrLines(tempWin, true);
		
				}
				if(tempComponentToBeDltd.getCompId() != null) ListOfComponentsToBeDltd.add(tempComponentToBeDltd);
				
			}else{
				deleteWindowComponentOrLines(tempWin, true);
			}
		}catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	}
	
	//boolean modified = false;
	Set<String> switchCampSet = new HashSet<String>();
	//String activityCompWinIds = "";
	
	/**
	 * Deletes the WindowComponent or All Lines
	 * @param tempWin
	 * @param removeWindowFlag
	 */
	private void deleteWindowComponentOrLines(Window tempWin, boolean removeWindowFlag) {
		
		//visitedCompSet.remove(tempWin.getId());
		String id = tempWin.getId();
		if(id.contains("SEND_EMAIL")) {
			switchCampSet.add(id);
			//String campname = ((Label)tempWin.getFellowIfAny("activityFooterLblId")).getValue();
			
		}
		
		Label fromNextLbl = (Label)tempWin.getFellow("nextCompLblId");
		Label fromPrevLbl = (Label)tempWin.getFellow("prevCompLblId");
		
		String fromNextStr = fromNextLbl.getValue();
		String fromPrevStr = fromPrevLbl.getValue();
		
		String tempWinId = tempWin.getId().trim();
		/**
		 * NextLabel=EVENT_END-14w0,
			PrevLabel=ACTIVITY_SEND_EMAIL-12w0,
			Win ID TO REMOVE =ACTIVITY_SEND_SMS-13w
		 */
		//logger.info("NextLabel="+fromNextStr);
		//logger.info("PrevLabel="+fromPrevStr);
		//logger.info("Win ID TO REMOVE ="+tempWinId);
		
		if(fromNextStr.trim().length()==0 && fromPrevStr.trim().length()==0) {
			
			centerDiv.removeChild(tempWin);
			return;
		}
		else {
			String fromNextWinIdsArr[] = fromNextStr.split(",");
			String fromPrevWinIdsArr[] = fromPrevStr.split(",");

			Window fromWinId;
			Window toWinId;
			Label tempLbl;
			
			if(fromNextStr.trim().length() > 0 ) {  // Remove Next Components lines
				
				for (String winIdStr : fromNextWinIdsArr) {
					toWinId = getWindowFromCenterDiv(winIdStr); //(Window)centerDiv.getFellowIfAny(winIdStr);
					tempLbl = (Label)toWinId.getFellow("prevCompLblId");
					
					String toPrevStr = tempLbl.getValue();
					int ind = toPrevStr.indexOf(tempWinId);
					String actwinId = toPrevStr.substring(ind,toPrevStr.indexOf(",", ind));
					
					//logger.info("------&&&&&&&&&&&------To remove: "+actwinId);
					
					tempLbl.setValue(tempLbl.getValue().replace(actwinId+",", ""));
					
					resetLinesUsedAttribute(toWinId);
				} // for
			} // if
			
			if(fromPrevStr.trim().length() > 0 ) { // Remove Previous Components lines

				for (String winIdStr : fromPrevWinIdsArr) {
					
					fromWinId = getWindowFromCenterDiv(winIdStr); //(Window)centerDiv.getFellowIfAny(winIdStr);
					tempLbl = (Label)fromWinId.getFellow("nextCompLblId");
					
					
					String toPrevStr = tempLbl.getValue();
					int ind = toPrevStr.indexOf(tempWinId);
					String actwinId = toPrevStr.substring(ind,toPrevStr.indexOf(",", ind));
					
					//logger.info("------&&&&&&&&&&&------To remove: "+actwinId);
					tempLbl.setValue(tempLbl.getValue().replace(actwinId+",", ""));
					
					resetLinesUsedAttribute(fromWinId);
				} // for
			} // if
			
			
			
			if(removeWindowFlag) { // Remove the Window
				
				centerDiv.removeChild(tempWin);
			}
			else { // Remove only lines
				
				fromNextLbl.setValue("");
				fromPrevLbl.setValue("");
			}
			
			if(fromNextStr.trim().length() > 0 && fromPrevStr.trim().length() > 0) { // Try to draw line from PREV to NEXT window.
				
				for (String formWinIdStr : fromPrevWinIdsArr) {
					fromWinId = getWindowFromCenterDiv(formWinIdStr); //(Window)centerDiv.getFellowIfAny(formWinIdStr);
					
					for (String toWinIdStr : fromNextWinIdsArr) {
						toWinId = getWindowFromCenterDiv(toWinIdStr); //(Window)centerDiv.getFellowIfAny(toWinIdStr);
						
							List<String> retList = ProgramUtility.validateAndAddWindowIds(fromWinId, toWinId);
							if(retList!=null) {
							
							Shape tempShape = ProgramUtility.getLineForWindws(fromWinId, toWinId);
							
							if(tempShape != null) {
								centerDiv.add(tempShape);
								//lineWindows.put(tempShape, fromWinId.getId()+":"+toWinId.getId());
								lineWindows.put(tempShape, retList.get(0)+":"+retList.get(1));

								// storeLineInMap(tempShape, fromWinId, toWinId);
							} // if
						} // if
					} // for 
				} // for
			} // if
			
			
		} // else
		
		redrawAllLines();
		
	} // deleteWindowComponentOrLines
	
	/**
	 * This method does an important operation as it checks and validate the entire design of the program.
	 * the following are the validation steps need to be considered.
	 * 1.Ensure that no Event type component(except ELLAPSETIMER, TARGETTIMER) should repeat more than once.
	 * 2.validate each component's input/output paths.
	 * 3.Ensure each component(and its paths) reaching proper end point or not. 
	 * 4.Ensure properties have been set for all the components.
	 * 5.check is the switch components data has been modified and need to reassign the properties 
	 * @return
	 */
	
	public boolean validate(boolean programAsTemplate) {
	
		try {
			logger.debug("----just entered to validate the design------");
			boolean isValid = true;
			
			/*****************STEP:1 validate the event components so that those should appear only once in a program*******************************************************************************/
			
			Vector<String> eventComponentWinId = new Vector<String>();
			List<Window> emailActivityWin = new ArrayList<Window>();
			
			
			/*****STEP2: validate the component's input and output paths*********************************/
			List<Component> componentWinList = centerDiv.getChildren();
			List<Window> switchCompWinList = new ArrayList<Window>();
			int inputCount = 0;
			int outputCount = 0;
			
			//String[] preViousIds = null;
			int numberOfNextIds = 0;
			int numberOfPrevIds = 0;
			String errcompLbls = "";
			String prevId = "";
			String nextId = "";
			String winId = "";
			for (Component eachCompWindow : componentWinList) {
				Window compWindow = (Window)eachCompWindow;
				winId = compWindow.getId();
				numberOfNextIds = 0;
				numberOfPrevIds = 0;
				prevId = ((Label)compWindow.getFellowIfAny("prevCompLblId")).getValue();
				
				
				if(prevId.trim().length() > 0) 	numberOfPrevIds = ProgramUtility.countOccurrences(prevId, ',');
				
				
				nextId = ((Label)compWindow.getFellowIfAny("nextCompLblId")).getValue();
				if(nextId.trim().length() >0) numberOfNextIds = ProgramUtility.countOccurrences(nextId, ',');
				
				//logger.info("the prevId====>("+prevId+") the next id is====>("+nextId+") and the counts are===>"+numberOfPrevIds+" and "+numberOfNextIds);
				/*logger.info("the prevId is===="+((Label)compWindow.getFellowIfAny("prevCompLblId")).getValue());
				logger.info("the winId is==="+compWindow.getId()+" preV===="+preViousIds.length+" nextIds===="+nextIds.length);*/
				//errcompLbls = ((Label)compWindow.getFellowIfAny("errorCompLblId")).getValue();
				
				if( winId.startsWith("SWITCH_") ) {
					
					if(winId.contains("_DATA")) {
						switchCompWinList.add(compWindow);
					}
					
					inputCount = ProgramEnum.SWITCH_DATA.getPrev_size();
					outputCount = ProgramEnum.SWITCH_DATA.getNext_size();
					
				} // if
				
				else if( winId.startsWith("ACTIVITY_") ) {
					inputCount = ProgramEnum.ACTIVITY_SEND_EMAIL.getPrev_size();
					outputCount = ProgramEnum.ACTIVITY_SEND_EMAIL.getNext_size();
					
					if(winId.contains("_SEND_EMAIL")) {
						
						emailActivityWin.add(compWindow);
						
						
					}//if
					
				} // else if
				
				else if(winId.startsWith("EVENT_")) {
					
					//logger.info(" the event component is===>"+winId.substring(0, winId.indexOf("-", 0)));
					
					if( winId.startsWith("EVENT_CUST_") || 
							winId.startsWith("EVENT_SCHEDULED_") || 
							winId.startsWith("EVENT_CUSTOM_")) {
						
						String eventCompType = winId.substring(0, winId.indexOf("-", 0));
						
						if(!eventComponentWinId.contains(eventCompType)) {
							
							eventComponentWinId.add(eventCompType);
						}else {
							MessageUtil.setMessage("Please design properly. No "+eventCompType+" " +
									"should appear more than once.", "color:red;", "top");
							isValid = false;
							return isValid;
						}
						
						
						inputCount = ProgramEnum.EVENT_CUST_ACTIVATED.getPrev_size();
						outputCount = ProgramEnum.EVENT_CUST_ACTIVATED.getNext_size();
					
					}// if
					else if( winId.contains("_TIMER") ) {
						
						inputCount = ProgramEnum.EVENT_ELAPSE_TIMER.getPrev_size();
						outputCount = ProgramEnum.EVENT_ELAPSE_TIMER.getNext_size();
						
					} // else if
					else if(winId.contains("EVENT_END")){
						
						inputCount = ProgramEnum.EVENT_END.getPrev_size();
						outputCount = ProgramEnum.EVENT_END.getNext_size();
						
					}//else
					
				}// else
				if( numberOfPrevIds < getMinLines(inputCount) || numberOfNextIds < getMinLines(outputCount) ) {
					//logger.info("entered for===="+winId);
					if(errcompLbls.length() > 0 ) errcompLbls += ",";
					errcompLbls += winId;
					
				}//if
				
			}//for
			//logger.info("the error label length is===>"+errcompLbls.length()+" the error label value is===>"+errcompLbls);
			if(errcompLbls.trim().length()>0) {
				
				logger.debug("the error components are===="+errcompLbls);
				Messagebox.show("Some of the components are not designed properly: wrong number of input / output lines");
				isValid = false;
				return isValid;
				
			}
			
			/*************STEP:3 validate is every path reaching end or not(is any cyclic dependency found)***********************/
			
			for(Component eachComp : componentWinList) {
				Window compWin = (Window)eachComp;
				if(!compWin.getId().contains("EVENT_END")) {
					
					if(!endCompFound(compWin)) {
						
						Messagebox.show("Found cyclic dependency in the design : Some of the components "+compWin.getId()+" " +
								"are not reaching proper endpoint");
						isValid = false;
						return isValid;
						
					}//if
					
				}//if
				
				
			}//for
			
			/**** STEP4:validate the design with respect to data associated with each individual component ******************/
			if(!programAsTemplate) {
				for (Component compWindow : componentWinList) {
					
					winId = compWindow.getId();
					
					if(winId.contains("TIMER") || winId.contains("SEND") || 
						winId.contains("SWITCH_") || winId.contains("CUST_ACTIVATED")) {
						
						if(compWindow.getAttribute("supportData") == null) {
							Messagebox.show("Properties have not been set for some of the components: ");
							isValid = false;
							return isValid;
						}//if
						
					}//if
					
				}//for
			}
			
			/****** STEP5:validate the switch data(if it need to reassign the properties with respect to the deleted components(if any)) *****/
			if(!programAsTemplate) {
				String messageline = "";
				String nextWinId = "";
				String[] nextIds = null;
				//String[] prevIds = null;
				String strToFormQry = null;
				String campStr;
				
				Map<String,String> switchCampMap = null;
				Collection<String> campList = null;
				boolean modified = false;
				String filter = "";
				
				for(Window switchWindow : switchCompWinList) {//this is need to be simplified(this code runs for deleted email activity
					//components,and for modifications in configured campaigns (which are appearing in input path of this switch))
					filter = (String)switchWindow.getAttribute("filter");
					campStr = "";
					if(filter.equals("2") || filter.equals("3")) { //if activity filter is applied 
						
						switchCampMap = (Map<String,String>)switchWindow.getAttribute("switchCampMap");
						if(switchCampMap.size()==0) modified = true;
						
						//logger.info("this switch map is====>"+switchCampMap);
						campList = (Collection<String>)switchCampMap.values();
						for (String camp : campList) {
							if(campStr.length()>0) campStr += ",";
							campStr += camp;
						}//for
						//logger.info("the total camp str is====>"+campStr);
						
						for(String campWin : switchCampSet) {
							//logger.info("the deleted camp is=====>"+campWin);
							
							if(campStr.contains(campWin)) {
								modified = true;
								break;
							}//if
							
						}//for
						
						for(Window CampWin : emailActivityWin) {
							
							
							if(campStr.contains(CampWin.getId())) {
								
								if(!switchCampMap.containsKey(((Label)CampWin.getFellowIfAny("activityFooterLblId")).getValue())) {
									modified = true;
									break;
								}//if
								
								
							}//if
							
							
						}//for
					}//if activity filter is applied
					
					messageline = (String)((Label)switchWindow.getFellowIfAny("messageLine1Id")).getAttribute("moveTo");
					messageline += (String)((Label)switchWindow.getFellowIfAny("messageLine2Id")).getAttribute("moveTo");
					
					nextWinId = ((Label)switchWindow.getFellowIfAny("nextCompLblId")).getValue();
					//prevId = ((Label)switchWindow.getFellowIfAny("prevCompLblId")).getValue();
					
					
					
					
					nextIds = nextWinId.split(",");
					for (String nxtId : nextIds) {
						
						if(!messageline.contains(nxtId) || modified ) {
							//switchWindow.removeAttribute("supportData");//this is not required here
							isValid = false;
							
							Messagebox.show("Please reassign the properties for the marked switch");
							
							return isValid;
							
						}//if
					}//for
					
				}//for
			}//if 
			
			return isValid;

		} catch (Exception e) {
			logger.error("Exception ::" , e);
			
			return false;
			
		}
		
		
	}//validate()
	
	
	/**
	 * This method used to validate each component is that reaching proper end point or not.
	 * called in validate() method for each component of centerDiv.
	 * @param compWin
	 * @return
	 */
	public boolean endCompFound(Window compWin) {
		
		boolean found = false;
		//String nextIds = ((Label)compWin.getFellowIfAny("nextCompLblId")).getValue();
		visitedCompSet.clear();
		
		visitedCompSet.add(compWin.getId());
		Stack<Window> s = new Stack<Window>();
		s.push(compWin); 
		
		
		while(!s.empty()) {
			
			Window tempCompWin = s.peek();
			Window nextCompWin = null;
			
			String tempnextId =  getUnvisitedNextComp(tempCompWin);
			if(tempnextId != null) nextCompWin = getWindowFromCenterDiv(tempnextId.substring(0,tempnextId.length()-1));
			
			if(nextCompWin != null) {
				visitedCompSet.add(tempnextId);
				if(tempnextId.contains("EVENT_END")) {
					found = true;
					break;
				}else {
					s.push(nextCompWin);
				}
			}
			else {
				s.pop();
			}
			
		}
		
		return found;
	}//endCompFound
	
	
	/**
	 * This method helps for DFS algorithm as it returns the unvisited next component.
	 * called in endCompFound().
	 * @param tempComp
	 * @return
	 */
	private String getUnvisitedNextComp( Window tempComp) {
		
		try {
			
			String nextIds = ((Label)tempComp.getFellowIfAny("nextCompLblId")).getValue();
			if(nextIds == null || nextIds.trim().length()==0) return null;
			
			String nextCompIds[] = nextIds.split(",");
			
			for (String nextId : nextCompIds) {
				if(nextId.trim().length() != 0 ) { // if after comma of last id we may have empty string
					//prevId = prevId.substring(0, prevId.length()-1);
					if(!visitedCompSet.contains(nextId)) {
						return nextId;
					}
				}//if
			} // for
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		return null;
		
	} // getUnvisitedChildComp
	
	
	
	
	
	public int getMinLines(int inputCount) {
		int min;
		
		if(inputCount>0) min = 1;
		else min = 0;
		
		
		return min;
		
		
		
	}
	
	
	public int getMinOutputLines(int outputCount) {
		
		int min;
		
		if(outputCount > 0) min = 1;
		else min = 0;
		
		return min;
		
		
	}
	
	
	private void resetLinesUsedAttribute(Window win) {
		
		try {
			String currWinId = win.getId();
			logger.debug("ResetLinesUsed Attr for :"+currWinId);
			if(!currWinId.startsWith("SWITCH_")) return;
			
			Label nextLbl = (Label)win.getFellow("nextCompLblId");
			Label prevLbl = (Label)win.getFellow("prevCompLblId");
			
			logger.debug("NextLabel="+nextLbl.getValue());
			logger.debug("PrevLabel="+prevLbl.getValue());

			String nextWinIdsArr[] = nextLbl.getValue().split(",");
			String prevWinIdsArr[] = prevLbl.getValue().split(",");

			Window fromWinId;
			Window toWinId;
			Label tempLbl;
			
			String finalAttr="";
			
			for (String winIdStr : nextWinIdsArr) {
				if(winIdStr.trim().length()==0) continue;
				
				toWinId = getWindowFromCenterDiv(winIdStr);
				tempLbl = (Label)toWinId.getFellow("prevCompLblId");
				
				String idsStr = tempLbl.getValue();
				
				int ind = idsStr.indexOf(currWinId);
				if(ind != 1) {
					finalAttr += idsStr.substring(ind+currWinId.length(), idsStr.indexOf("," , ind))+",";
				} // if
			} // for
			

			for (String winIdStr : prevWinIdsArr) {
				if(winIdStr.trim().length()==0) continue;
				toWinId = getWindowFromCenterDiv(winIdStr);
				tempLbl = (Label)toWinId.getFellow("nextCompLblId");
				
				String idsStr = tempLbl.getValue();
				
				int ind = idsStr.indexOf(currWinId);
				if(ind != 1) {
					finalAttr += idsStr.substring(ind+currWinId.length(), idsStr.indexOf("," , ind))+",";
				} // if
			} // for
			
			win.setAttribute(ProgramUtility.SWITCH_LINES_USED, finalAttr);
		} catch (ComponentNotFoundException e) {
			logger.error("Exception ::" , e);
		}
		
		
	} // resetLinesUsedAttribute
	
	
	
	Button deleteLinesBtnId;
	
	public void onClick$deleteLinesBtnId() {
		
		List<Drawable> tempShapes = new ArrayList<Drawable>();
		
		Label fromNextLbl;
		Label toPrevLbl;

		Window lineFromWin;
		Window lineToWin;
		
		String fromToWinIdStr;
		String fromWinId;
		String toWinId;
		
		for(int i=centerDiv.size()-1; i >= 0; i--) {
			Drawable d = centerDiv.get(i);
			
			if(d instanceof Shape &&  d.getStrokeStyle().equals(LINE_SELECTED_COLOR)) {
				fromToWinIdStr = lineWindows.get(d);
				
				logger.debug("fromToWinIdStr="+fromToWinIdStr);
				
				fromWinId = fromToWinIdStr.substring(0,fromToWinIdStr.indexOf(":"));
				toWinId = fromToWinIdStr.substring(fromToWinIdStr.indexOf(":")+1);
				
				lineFromWin = getWindowFromCenterDiv(fromWinId);// (Window)centerDiv.getFellowIfAny(fromWinId);
				lineToWin = getWindowFromCenterDiv(toWinId);// (Window)centerDiv.getFellowIfAny(toWinId);

				fromNextLbl = (Label)lineFromWin.getFellow("nextCompLblId");
				toPrevLbl = (Label)lineToWin.getFellow("prevCompLblId");
				
				fromNextLbl.setValue(fromNextLbl.getValue().replace(toWinId+",", ""));
				toPrevLbl.setValue(toPrevLbl.getValue().replace(fromWinId+",", ""));
				
				
				resetLinesUsedAttribute(lineFromWin);
				resetLinesUsedAttribute(lineToWin);

				lineWindows.remove(d);
				
				tempShapes.add(d);
			} // if
		} // for i
		
		centerDiv.removeAll(tempShapes);

	}
	
	Map<Drawable, String> lineWindows = new HashMap<Drawable, String>();
	
	private Borderlayout borderLayoutId;
	private Div centerPanelDiv;
	private boolean isAdmin;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		try {
			int tempWidth  =  Integer.parseInt(Sessions.getCurrent().getAttribute("screenWidth").toString().trim());
			int tempHeight =  Integer.parseInt(Sessions.getCurrent().getAttribute("screenHeight").toString().trim());
			
			Window tempWin = (Window)comp;

			tempWidth = tempWidth-20;
			tempHeight = tempHeight-370;
			
			int currWidth = getLengthFromStr(borderLayoutId.getWidth());
			int currHeight = getLengthFromStr(borderLayoutId.getHeight());
			
			if(tempWidth < currWidth) tempWidth = currWidth;
			if(tempHeight < currHeight) tempHeight = currHeight;

			
			borderLayoutId.setWidth(tempWidth+"px");
			borderLayoutId.setHeight(tempHeight+"px");

			centerPanelDiv.setWidth(tempWidth+"px");
			centerPanelDiv.setHeight((tempHeight-30)+"px");
			
			
			String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
	     	PageUtil.setHeader("Program Designer","",style,true);
			//PageUtil.setHeader("Captiway Program Designer", "", "", true);
			
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		
		isAdmin = (Boolean)session.getAttribute("isAdmin");
		logger.info("is admin====>"+isAdmin);
		if(!isAdmin) {
			
			saveAsTemplateBtnId.setVisible(false);
			
		}
		
		constructShapes();
		// send all shapes to client side
		shapeDataLb.setValue(JSONValue.toJSONString(_shapes));
		shapeListModel = new ListModelList();
		
		
		argumentsMap.put("programController", this);
		
		ListOfComponentsToBeDltd.clear();
		
	} // 
	
	private Label menuNavPageTbId,tempLblId;
	public void onChange$menuNavPageTbId() {
		
		logger.debug("----just entered in dummy event-----");
		
	}
	
	private Window copyOfProgWinId,saveAsForAdminWinId;
	private Textbox copyOfProgWinId$copyProgNameTbId;
	private Button copyOfProgWinId$CopyProgBtnId;
	public void onClick$saveCopyOfProgBtnId() {
		try {
			//not only copy of existing program need to be created with another name.
			//Messagebox.show("Are you sure do you want to save the Program?", titleCode, buttons, icon);
			if( Messagebox.show("Are you sure you want to save the program?","Save as New Program",
					 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK ) {
					
				
				
				if(autoProgram == null) {
					
					copyOfProgWinId$copyProgNameTbId.setValue(programNameTbId.getValue());
					copyOfProgWinId$CopyProgBtnId.setLabel("Save");
					/*logger.debug("got program object as null,returning...");
					return;*/
				}
				else {
					
					
					copyOfProgWinId$copyProgNameTbId.setValue("Copy Of "+autoProgram.getProgramName());
					copyOfProgWinId$CopyProgBtnId.setLabel("Copy");
				}
				
				copyOfProgWinId.setVisible(true);
				copyOfProgWinId.doModal();
				
			}//if
			
			
		} catch (SuspendNotAllowedException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
		
		
		
		
		
	}//onClick$saveCopyOfProgBtnId()
	
	public void onClick$adminSaveAsTempBtnId$saveAsForAdminWinId() {
		
		try {
			if(!validate(true)) {
				
				
				
				logger.debug("program is not validated returning......");
				return;// by this time we need to validate design for input and out put paths,
				//for component's associated data,reassignment of the properties of switch(if anything has been modified)
				
			}//if
			
			//create/update the top level(AutoProgram) object.
			if(!saveAsProgramObj(true)) return ;
			
			saveAsForAdminWinId.setVisible(false);
			copyOfProgWinId.setVisible(false);
			//create/update the bottom level(AutoProgramComponents) objects.
			saveProgramTemplate();
			
			if(ListOfComponentsToBeDltd.size()>0) //autoProgramComponentsDao.deleteByCollection(ListOfComponentsToBeDltd);
			autoProgramComponentsDaoForDML.deleteByCollection(ListOfComponentsToBeDltd);
			Messagebox.show("Program template is saved successfully");
			Redirect.goTo(PageListEnum.PROGRAM_AUTO_PROGRAM_SETTINGS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}

		
	}//onClick$adminSaveAsTempBtnId$saveAsForAdminWinId()
	
	public void onClick$adminSaveProgBtnId$saveAsForAdminWinId() {


		
		try {
			if(!validate(false)) {
				
				
				logger.debug("program is not validated returning......");
				return;// by this time we need to validate design for input and out put paths,
				//for component's associated data,reassignment of the properties of switch(if anything has been modified)
				
			}//if
			
			//create/update the top level(AutoProgram) object.
			if(!saveAsProgramObj(false)){
				saveAsForAdminWinId.setVisible(false);
				copyOfProgWinId.setVisible(false);
				
				
				return ;
				
				
				
			}
				
			
			
			//create/update the bottom level(AutoProgramComponents) objects.
			saveProgram();
			
			if(ListOfComponentsToBeDltd.size()>0) autoProgramComponentsDaoForDML.deleteByCollection(ListOfComponentsToBeDltd);
			
			if( !autoProgram.getStatus().equals("Active")) {
				
				if(	Messagebox.show("Program saved successfully. Click OK button to Publish it now.", "Confirm",
						 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK ) {
					
					// TODO need to change the status.
					autoProgram.setStatus("Active");
					autoProgramDaoForDML.saveOrUpdate(autoProgram);
					//isSaved = true;
					
				}
				Redirect.goTo(PageListEnum.PROGRAM_AUTO_PROGRAM_SETTINGS);
			}
			else {
				Messagebox.show("Program saved successfully."); 
				Redirect.goTo(PageListEnum.PROGRAM_AUTO_PROGRAM_SETTINGS);
				//isSaved = true;
			}
			//Messagebox.show("Program is saved successfully");
			Redirect.goTo(PageListEnum.PROGRAM_AUTO_PROGRAM_SETTINGS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}

		
	
		
		
		
	}//onClick$adminSaveProgBtnId$saveAsForAdminWinId()
	
	
	public void onClick$CopyProgBtnId$copyOfProgWinId() {
		
		
		try {
			
			logger.debug("----just entered----");
			String edit = (String)session.getAttribute("editProgram");
			if(isAdmin){
				logger.debug("edit is=====>"+edit);
				if(edit == null || (edit != null && edit.equalsIgnoreCase("use"))){
					
					saveAsForAdminWinId.setVisible(true);
					saveAsForAdminWinId.doModal();
					
					
				}/*else if(edit != null && edit.equalsIgnoreCase("Use")) {
					
					
					
					
				}*/
				
			}//if
			
			if(!isAdmin && edit == null) {
				String copyNameStr = copyOfProgWinId$copyProgNameTbId.getValue();
				if(!validate(false)){
					
					logger.debug("Program is not validated returning.....");
					return;
					
				}
				Set mLstSet = programMlListLbId.getSelectedItems();
				
				if(mLstSet == null || mLstSet.size() == 0) {
					
					Messagebox.show("Please select atleast one mailing list for the program ");
					return;
					
				}
				
				
				
				
				Set<MailingList> mlSet = new HashSet<MailingList>();
				//Set mlItemSet = programMlListLbId.getSelectedItems();
				
				Listitem item = null;
				for (Object object : mLstSet) {
					
					item = (Listitem)object;
					mlSet.add((MailingList)item.getValue());
					
					
					
				}//for
				
				if(copyNameStr == null || copyNameStr.trim().length() == 0) {
					
					Messagebox.show("Please provide the name for the program to be copied");
					return;
					
				}//if
				
				if(autoProgramDao.isProgramNameExists(copyNameStr, GetUser.getUserObj().getUserId())) {
					
					Messagebox.show("Plaese provide another name for the Program : Program name already exist");
					return;
					
				}
				autoProgram = new AutoProgram(copyOfProgWinId$copyProgNameTbId.getValue(),Calendar.getInstance(), Calendar.getInstance(),currentUser);
				autoProgram.setDescription(programDescTbId.getValue());
				autoProgram.setMailingLists(mlSet);
				autoProgram.setStatus("Draft");
				autoProgramDaoForDML.saveOrUpdate(autoProgram);
				saveCopiedProg(autoProgram);
				
				
			}
			//logger.debug("just entered to create a copy...");
			if(edit != null && edit.equalsIgnoreCase("edit")) {
				
					
					
					if(autoProgram == null) {
						//need to created new program or template based on the 
						
						
						logger.debug("got program object as null,returning");
						return;
						
					}//if
					
				
					String copyNameStr = copyOfProgWinId$copyProgNameTbId.getValue();
					
					if(copyNameStr == null || copyNameStr.trim().length() == 0) {
						
						Messagebox.show("Please provide the name for the program to be copied");
						return;
						
					}//if
					
					if(autoProgramDao.isProgramNameExists(copyNameStr, GetUser.getUserObj().getUserId())) {
						
						Messagebox.show("Plaese provide another name for the Program : Program name already exist");
						return;
						
					}
					
					
					if(!validate(false)){
						
						logger.debug("Program is not validated returning.....");
						return;
						
					}
					Set mLstSet = programMlListLbId.getSelectedItems();
					
					if(mLstSet == null || mLstSet.size() == 0) {
						
						Messagebox.show("Please select atleast one mailing list for the program ");
						return;
						
					}
					
					
					
					
					Set<MailingList> mlSet = new HashSet<MailingList>();
					//Set mlItemSet = programMlListLbId.getSelectedItems();
					
					Listitem item = null;
					for (Object object : mLstSet) {
						
						item = (Listitem)object;
						mlSet.add((MailingList)item.getValue());
						
						
						
					}//for
					
					logger.debug("make a copy of the program.....");
					
					try {
						
						String name = autoProgram.getProgramName();
						AutoProgram tempCopyProg = autoProgram;	
						
						tempCopyProg.setProgramId(null);
						tempCopyProg.setProgramName(copyNameStr);
						tempCopyProg.setMailingLists(mlSet);
						
						//autoProgramDao.saveOrUpdate(tempCopyProg);
						autoProgramDaoForDML.saveOrUpdate(tempCopyProg);
						
						saveCopiedProg(tempCopyProg);
						
						Messagebox.show("Copy of '"+name+"' is created with the name '"+tempCopyProg.getProgramName()+"' successfully.");
						Redirect.goTo(PageListEnum.PROGRAM_AUTO_PROGRAM_SETTINGS);
						
						
					} catch (Exception e) {

						logger.error("**Exception while creating the copy of the program ",e);
					}
					
						
			}
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
		
		
	}//onClick$CopyProgBtnId$copyOfProgWinId()
	
	
	
	
	public Bandbox mailingListBandBoxId;
	
	private Button applyMlSettingsBtnId;
	
	private Button saveCopyOfProgBtnId;
	
	public void onSelect$programMlListLbId() {
		Listitem item = null;
		MailingList mailingList = null;
		String mlNameStr = "";
		Set mlSet = programMlListLbId.getSelectedItems();
		for (Object object : mlSet) {
			item = (Listitem)object;
			
			mailingList = (MailingList)item.getValue();
			if(mlNameStr.length() > 0 ) mlNameStr += ",";
			mlNameStr += mailingList.getListName();
			
		}//for
		mailingListBandBoxId.setValue(mlNameStr);
		mailingListBandBoxId.setTooltiptext(mlNameStr);
		
	}
	
	
	public void onClick$applyMlSettingsBtnId() {
		mailingListBandBoxId.close();		
		
		
		
	}
	
	/**
	 * This method called from the client side as an event handling method.
	 * 
	 */
	
	public void onChange$tempLblId() {
		String edit = (String)session.getAttribute("editProgram");
		if(edit != null ){
			
			AutoProgram tempAutoProgram = (AutoProgram)session.getAttribute("autoProgram");

			if(edit.equalsIgnoreCase("edit")) {
				
				programNameTbId.setDisabled(true);
				saveAsTemplateBtnId.setVisible(false);
				editProgram(tempAutoProgram);
			}
			else if(edit.equalsIgnoreCase("use")) {
				
				programNameTbId.setDisabled(false);
				
				editProgramTemplate(tempAutoProgram);
				
			}
		
		}//if
		else{
			//saveCopyOfProgBtnId.setDisabled(true);
			programNameTbId.setValue(setDefaultName());
			lastModifiedDateLbl.setValue(MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_DATETIME_STDATE));
		}//else
		
	}
	
	/**
	 * This method sets the default name for the newly added program.
	 * @return
	 */
	public String setDefaultName() {
		
		try {
			Session sessionScope = Sessions.getCurrent();
			TimeZone clientTimeZone = (TimeZone)sessionScope.getAttribute("clientTimeZone");
			//logger.info();
			Calendar tempCal= Calendar.getInstance();
//			tempCal=triggerCustomEvent.getEventDate();
			
			tempCal.setTimeZone(clientTimeZone);
			
//			  Format formatter = new SimpleDateFormat(" dd MMM yyyy hh_mm_ss ");
			  Format formatter = new SimpleDateFormat(" dd MMM, hh:mm a");
			 String timeStamp = "Program of"+MyCalendar.calendarToString(tempCal, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone);
//			 String timeStamp = user.getUserName()+today;
			 return timeStamp;
			
		} catch (Exception e) {
			logger.error("** Exception : error occured while setting campaign Name . ",e);
			return null;
		}
		
	}//setDefaultName()
	
	
	public void editProgramTemplate(AutoProgram programTemplate) {

		

		
		/**
		 * STEP1: prepare the view as per the earlier settings of the program.
		 * STEP2: save the latest settings(if any) upon the user confimation.
		 */
			Calendar cal = Calendar.getInstance();
			autoProgram = programTemplate;
		
			if(autoProgram == null) {
				logger.debug("found autoProgram as null,returning.....");
				return;
				
			}
			//autoProgram.setModifiedDate(cal);
			
			programSettingsDivId.setAttribute("autoProgram", autoProgram);
			//logger.info("programNameTbId "+programNameTbId);
			programNameTbId.setValue(autoProgram.getProgramName());
			//programNameTbId.setDisabled(true);//not to allow to modify the name of the program.
			
			programDescTbId.setValue(autoProgram.getDescription());
			
			/*Vector<String> tempMlNameVector = new Vector<String>();
			
			Set<MailingList> tempProgMlList = autoProgram.getMailingLists();
			
			if(tempProgMlList != null && tempProgMlList.size() > 0) {
			
				programMlListLbId.setAttribute("mlLists", tempProgMlList);
				//lastModifiedDateLbl.setValue(MyCalendar.calendarToString(autoProgram.getModifiedDate(), MyCalendar.FORMAT_DATETIME_STDATE));
				
				String mlName = null;
				for (MailingList mailingList : tempProgMlList) { // to add each list name in this temp vector so that we can make a selection of those already configured mlLists for this program
					mlName = mailingList.getListName();
					
					if(!tempMlNameVector.contains(mlName)) tempMlNameVector.add(mlName);
					
				} // for
				
				for(int i=0; i<programMlListLbId.getItemCount(); i++) { // to make a selection of mlLists for this program
					
					mlName = programMlListLbId.getItemAtIndex(i).getLabel();
					if(tempMlNameVector.contains(mlName))programMlListLbId.setSelectedIndex(i);
					
					
				}// for
			
			}//if
*/			
			List<AutoProgramComponents> componentsList = autoProgramComponentsDao.getProgramComponents(autoProgram.getProgramId());
			logger.debug("got====>"+componentsList.size()+"  number of objects for this program "+autoProgram.getProgramId());
			
			String coordArr[] = null;
			Window component = null;
			Div tempDiv = null;
			String bgImagePath = "";
			Label titleLabel = null;
			Long supportId = null;
			List<Window> switchCompWin = new ArrayList<Window>();
			
			/****for each component set the earlier configured properties.*******************/
			
			for (AutoProgramComponents tempComponents : componentsList) {
				
				String winId = tempComponents.getComponentWinId();
				
				updateGlobalZIndex(winId);
				
				supportId = tempComponents.getSupportId();
				
				if(winId.startsWith("ACTIVITY_")) {// if it an activity
					//logger.info("----just entered and it is an activity-----");
					coordArr = tempComponents.getCoordinates().split(",");
					
					//logger.info("the top:"+coordArr[1]+" the left is:"+coordArr[0]);
					
					component = (Window) Executions.createComponents("zul/general/activitycomponent.zul", centerDiv, argumentsMap);
					//component.setId(winId);
					component.setStyle("position:absolute;padding:1px");
					tempDiv = (Div)component.getFellowIfAny("imageDiv");
					//((Image)component.getFellowIfAny("image")).setSrc(bgImagePath);
					component.setTop(coordArr[1]);
					component.setLeft(coordArr[0]);
					
					//tempDiv = (Div)component.getFellow("imageDiv");
					
					titleLabel = (Label) component.getFellow("titleLblId");
					Label messageLabel = (Label) component.getFellow("activityMessageLblId");
					Label footerLabel = (Label) component.getFellow("activityFooterLblId");
					((Label)component.getFellowIfAny("prevCompLblId")).setValue(tempComponents.getPreviousId());
					((Label)component.getFellowIfAny("nextCompLblId")).setValue(tempComponents.getNextId());
					//logger.info("============"+Integer.parseInt(winId.substring(winId.indexOf("-")+1, winId.length()-1)));
					//component.setZindex(Integer.parseInt(winId.substring(winId.indexOf("-"), winId.length()-1)));
					//logger.info("tempComponents.getTitle()"+tempComponents.getTitle());
					titleLabel.setValue(tempComponents.getTitle());
					messageLabel.setValue(tempComponents.getMessage());
					footerLabel.setValue(tempComponents.getFooter());
					component.setId(winId);
					//obj.setZindex(globalzindex);
					//centerDiv.add(component);
					//component.setParent(centerDiv);
					if(winId.contains("SEND_EMAIL")) {
						
						/*Campaigns tempCampaign = campaignsDao.find(supportId);
						component.setAttribute("supportData", tempCampaign);*/
						
						bgImagePath = "/img/program/draw_activities_send_email_campaign.PNG";
						
					}// if
					else if(winId.contains("SEND_SMS")) {
						
						/*SMSCampaigns tempSmsCampaign = smsCampaignsDao.findByCampaignId(supportId);
						component.setAttribute("supportData", tempSmsCampaign);*/
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
					
					logger.debug("the top:"+coordArr[1]+" the left is:"+coordArr[0]);
					
					component = (Window) Executions.createComponents("zul/general/eventcomponent.zul", centerDiv, argumentsMap);
					//component.setId(winId);
					component.setStyle("position:absolute;padding:1px");
					tempDiv = (Div)component.getFellowIfAny("imageDiv");
					//((Image)component.getFellowIfAny("image")).setSrc(bgImagePath);
					component.setTop(coordArr[1]);
					component.setLeft(coordArr[0]);
					
					//tempDiv = (Div)component.getFellow("imageDiv");
					titleLabel = (Label) component.getFellow("titleLblId");
					/*Label messageLabel = (Label) component.getFellow("activityMessageLblId");
							Label footerLabel = (Label) component.getFellow("activityFooterLblId");
					 */((Label)component.getFellowIfAny("prevCompLblId")).setValue(tempComponents.getPreviousId());
					 ((Label)component.getFellowIfAny("nextCompLblId")).setValue(tempComponents.getNextId());
					 //logger.info("============"+Integer.parseInt(winId.substring(winId.indexOf("-")+1, winId.length()-1)));
					 //component.setZindex(Integer.parseInt(winId.substring(winId.indexOf("-"), winId.length()-1)));
					 logger.info("tempComponents.getTitle()"+tempComponents.getTitle());
					 titleLabel.setValue(tempComponents.getTitle());
					 /*messageLabel.setValue(tempComponents.getMessage());
							footerLabel.setValue(tempComponents.getFooter());*/
					 component.setId(winId);
					 //obj.setZindex(globalzindex);
					 //centerDiv.add(component);
					 //component.setParent(centerDiv);
					if(winId.contains("CUST_ACTIVATED")) {
						
						bgImagePath = "/img/program/draw_event_cust_activated.PNG";
						//component.setAttribute("supportData", tempComponents.getSupportId());
						
					}// if
					else if(winId.contains("_END")) {
						
						bgImagePath = "/img/program/draw_event_end.PNG";
					}
					else if(winId.contains("_TARGET_TIMER")) {
						
						/*Calendar targetTime = Calendar.getInstance();
						targetTime.setTimeInMillis(supportId);
						component.setAttribute("supportData", targetTime);*/
						bgImagePath = "/img/program/draw_event_target_timer.PNG";
						
						
					}
					else if(winId.contains("_ELAPSE_TIMER")) {
					
						/*long minOffset = supportId.longValue();
						if(minOffset >= 1440){
							minOffset = minOffset/1440;
						}
						else {
							minOffset = minOffset/60;
						}*/
						//component.setAttribute("supportData", supportId);
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
					
					//logger.info("the top:"+coordArr[1]+" the left is:"+coordArr[0]);
					
					component = (Window) Executions.createComponents("zul/general/switchcomponent.zul", centerDiv, argumentsMap);
					//component.setId(winId);
					component.setStyle("position:absolute;padding:1px");
					tempDiv = (Div)component.getFellowIfAny("imageDiv");
					//((Image)component.getFellowIfAny("image")).setSrc(bgImagePath);
					component.setTop(coordArr[1]);
					component.setLeft(coordArr[0]);
					
					titleLabel = (Label) component.getFellow("titleLblId");
					titleLabel.setValue(tempComponents.getTitle());
					
					((Label)component.getFellowIfAny("prevCompLblId")).setValue(tempComponents.getPreviousId());
					 ((Label)component.getFellowIfAny("nextCompLblId")).setValue(tempComponents.getNextId());
					 
					 component.setId(winId);
					 
					 if(winId.contains("_DATA")) {
						 
						 //progCampsMap = DFSForSwitch(component);
						 bgImagePath = "/img/program/draw_switches_data.PNG";
					
					 
					
					 
						/* String strToformQry = "";
						 Label msgLbl1 = null;
						 Label msgLbl2 = null;
						 List<SwitchCondition> tempSwitchCondLst = switchConditionDao.findByComponentId(tempComponents.
								 									getCompId(), autoProgram.getProgramId());
						
						 if(tempSwitchCondLst.size() > 1){
							 
							 for (SwitchCondition switchCondition : tempSwitchCondLst) {
								 
								 msgLbl1 = (Label)component.getFellowIfAny("messageLine1Id");
								 msgLbl2 = (Label)component.getFellowIfAny("messageLine2Id");
								 
								 //here it is the mistake it only works 
								 if(switchCondition.getModeFlag().equalsIgnoreCase("false") ) {//false mode(it condition is empty means it is for false mode)
									 
									 strToformQry = switchCondition.getCondition();
									 
									 msgLbl1.setValue(switchCondition.getLineMessage());
									 msgLbl1.setAttribute("moveTo", switchCondition.getModeFlag()+"||"+switchCondition.getModeAttribute());
									 
								 }else { //true mode
									 
									 strToformQry = switchCondition.getCondition();
									 msgLbl2.setValue(switchCondition.getLineMessage());
									 msgLbl2.setAttribute("moveTo", switchCondition.getModeFlag()+"||"+switchCondition.getModeAttribute());
									 
									 
								 }//else 
								 
							 }//for
							 
						 }else{
							 
							 SwitchCondition switchCondition = tempSwitchCondLst.get(0);
							 
							 msgLbl1 = (Label)component.getFellowIfAny("messageLine1Id");
							 strToformQry = switchCondition.getCondition();
							 
							 msgLbl1.setValue(switchCondition.getLineMessage());
							 msgLbl1.setAttribute("moveTo", switchCondition.getModeFlag()+"||"+switchCondition.getModeAttribute());
							 
							 
						 }
						 
						 
						 
						//we will be having two switch condition objects for each Switch component
						//examine and prepare view as those should appear
						 switchCompWin.add(component);
						 component.setAttribute("supportData", strToformQry);//it is just a condition query
						 component.setAttribute("filter", strToformQry.split(Constants.DELIMETER_DOUBLECOLON)[1] );
						 //logger.info(" DFSForSwitch(component)======>"+ DFSForSwitch(component));
						 //component.setAttribute("switchCampMap", DFSForSwitch(component));
						*/
					 }
					 else if(winId.contains("_ALLOCATION")) {
						 
						 bgImagePath = "/img/program/draw_switches_allocation.PNG";
					 }
				 
					 tempDiv.setStyle("background:url("+bgImagePath.substring(1)+");background-repeat:no-repeat; "); 
					 component.setAttribute("autoProgramComponents", tempComponents);
					 
				}// else if
				
				
				//createNewComp(img);
				
				
			}//for
			lastModifiedDateLbl.setValue(MyCalendar.calendarToString(autoProgram.getModifiedDate(), MyCalendar.FORMAT_DATETIME_STDATE));
			logger.debug("the num of children in main component centerdiv is=====>"+centerDiv.getChildren().size());
			redrawAllLines();
			
			/*for (Window window : switchCompWin) {
				window.removeAttribute("switchCampMap");
				window.setAttribute("switchCampMap", DFSForSwitch(window));
				//logger.info("in edit program this "+window.getId()+" campaign map is====>"+(Map)window.getAttribute("switchCampMap"));
				
			}*/
			
			/*for(Window window : switchCompWin) { // written for debug...
				
				logger.info("in edit program this "+window.getId()+" campaign map is====>"+(Map)window.getAttribute("switchCampMap"));
			}*/
			//redrawAllLines();
	
		
		
		
		
	}//editProgramTemplate()
	
	
	
	
	/**
	 * This method allows to edit the program which has been designed earlier.
	 * @param autoProgram
	 */
	public void editProgram(AutoProgram tempAutoProgram) {
		try {
			
			/**
			 * STEP1: prepare the view as per the earlier settings of the program.
			 * STEP2: save the latest settings(if any) upon the user confimation.
			 */
				Calendar cal = Calendar.getInstance();
				autoProgram = tempAutoProgram;
			
				if(autoProgram == null) {
					logger.debug("found autoProgram as null,returning.....");
					return;
					
				}
				//autoProgram.setModifiedDate(cal);
				
				programSettingsDivId.setAttribute("autoProgram", autoProgram);
				//logger.info("programNameTbId "+programNameTbId);
				programNameTbId.setValue(autoProgram.getProgramName());
				//programNameTbId.setDisabled(true);//not to allow to modify the name of the program.
				
				programDescTbId.setValue(autoProgram.getDescription());
				
				Vector<String> tempMlNameVector = new Vector<String>();
				
				Set<MailingList> tempProgMlList = autoProgram.getMailingLists();
				String mlNameStr = "";
				
				if(tempProgMlList != null && tempProgMlList.size() > 0) {
				
					programMlListLbId.setAttribute("mlLists", tempProgMlList);
					//lastModifiedDateLbl.setValue(MyCalendar.calendarToString(autoProgram.getModifiedDate(), MyCalendar.FORMAT_DATETIME_STDATE));
					
					String mlName = null;
					for (MailingList mailingList : tempProgMlList) { // to add each list name in this temp vector so that we can make a selection of those already configured mlLists for this program
						mlName = mailingList.getListName();
						
						if(!tempMlNameVector.contains(mlName)) tempMlNameVector.add(mlName);
						if(mlNameStr.length() > 0) mlNameStr += ",";
						mlNameStr += mlName;
						
					} // for
					
					for(int i=0; i<programMlListLbId.getItemCount(); i++) { // to make a selection of mlLists for this program
						
						mlName = programMlListLbId.getItemAtIndex(i).getLabel();
						if(tempMlNameVector.contains(mlName))programMlListLbId.addItemToSelection(programMlListLbId.getItemAtIndex(i));
						
						
					}// for
				mailingListBandBoxId.setValue(mlNameStr);
				mailingListBandBoxId.setTooltiptext(mlNameStr);
				}//if
				
				List<AutoProgramComponents> componentsList = autoProgramComponentsDao.getProgramComponents(autoProgram.getProgramId());
				logger.debug("got====>"+componentsList.size()+"  number of objects for this program "+autoProgram.getProgramId());
				
				String coordArr[] = null;
				Window component = null;
				Div tempDiv = null;
				String bgImagePath = "";
				Label titleLabel = null;
				Long supportId = null;
				List<Window> switchCompWin = new ArrayList<Window>();
				
				/****for each component set the earlier configured properties.*******************/
				
				for (AutoProgramComponents tempComponents : componentsList) {
					
					String winId = tempComponents.getComponentWinId();
					
					updateGlobalZIndex(winId);
					
					supportId = tempComponents.getSupportId();
					
					if(winId.startsWith("ACTIVITY_")) {// if it an activity
						//logger.info("----just entered and it is an activity-----");
						coordArr = tempComponents.getCoordinates().split(",");
						
						//logger.info("the top:"+coordArr[1]+" the left is:"+coordArr[0]);
						
						component = (Window) Executions.createComponents("zul/general/activitycomponent.zul", centerDiv, argumentsMap);
						//component.setId(winId);
						component.setStyle("position:absolute;padding:1px");
						tempDiv = (Div)component.getFellowIfAny("imageDiv");
						//((Image)component.getFellowIfAny("image")).setSrc(bgImagePath);
						component.setTop(coordArr[1]);
						component.setLeft(coordArr[0]);
						
						//tempDiv = (Div)component.getFellow("imageDiv");
						
						titleLabel = (Label) component.getFellow("titleLblId");
						Label messageLabel = (Label) component.getFellow("activityMessageLblId");
						Label footerLabel = (Label) component.getFellow("activityFooterLblId");
						((Label)component.getFellowIfAny("prevCompLblId")).setValue(tempComponents.getPreviousId());
						((Label)component.getFellowIfAny("nextCompLblId")).setValue(tempComponents.getNextId());
						//logger.info("============"+Integer.parseInt(winId.substring(winId.indexOf("-")+1, winId.length()-1)));
						//component.setZindex(Integer.parseInt(winId.substring(winId.indexOf("-"), winId.length()-1)));
						//logger.info("tempComponents.getTitle()"+tempComponents.getTitle());
						titleLabel.setValue(tempComponents.getTitle());
						messageLabel.setValue(tempComponents.getMessage());
						footerLabel.setValue(tempComponents.getFooter());
						component.setId(winId);
						//obj.setZindex(globalzindex);
						//centerDiv.add(component);
						//component.setParent(centerDiv);
						if(winId.contains("SEND_EMAIL")) {
							
							Campaigns tempCampaign = campaignsDao.find(supportId);
							component.setAttribute("supportData", tempCampaign);
							
							bgImagePath = "/img/program/draw_activities_send_email_campaign.PNG";
							
						}// if
						else if(winId.contains("SEND_SMS")) {
							
							SMSCampaigns tempSmsCampaign = smsCampaignsDao.findByCampaignId(supportId);
							component.setAttribute("supportData", tempSmsCampaign);
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
						
						logger.debug("the top:"+coordArr[1]+" the left is:"+coordArr[0]);
						
						component = (Window) Executions.createComponents("zul/general/eventcomponent.zul", centerDiv, argumentsMap);
						//component.setId(winId);
						component.setStyle("position:absolute;padding:1px");
						tempDiv = (Div)component.getFellowIfAny("imageDiv");
						//((Image)component.getFellowIfAny("image")).setSrc(bgImagePath);
						component.setTop(coordArr[1]);
						component.setLeft(coordArr[0]);
						
						//tempDiv = (Div)component.getFellow("imageDiv");
						titleLabel = (Label) component.getFellow("titleLblId");
						/*Label messageLabel = (Label) component.getFellow("activityMessageLblId");
								Label footerLabel = (Label) component.getFellow("activityFooterLblId");
						 */((Label)component.getFellowIfAny("prevCompLblId")).setValue(tempComponents.getPreviousId());
						 ((Label)component.getFellowIfAny("nextCompLblId")).setValue(tempComponents.getNextId());
						 //logger.info("============"+Integer.parseInt(winId.substring(winId.indexOf("-")+1, winId.length()-1)));
						 //component.setZindex(Integer.parseInt(winId.substring(winId.indexOf("-"), winId.length()-1)));
						 logger.info("tempComponents.getTitle()"+tempComponents.getTitle());
						 titleLabel.setValue(tempComponents.getTitle());
						 /*messageLabel.setValue(tempComponents.getMessage());
								footerLabel.setValue(tempComponents.getFooter());*/
						 component.setId(winId);
						 //obj.setZindex(globalzindex);
						 //centerDiv.add(component);
						 //component.setParent(centerDiv);
						if(winId.contains("CUST_ACTIVATED")) {
							
							bgImagePath = "/img/program/draw_event_cust_activated.PNG";
							component.setAttribute("supportData", tempComponents.getSupportId());
							
						}// if
						else if(winId.contains("_END")) {
							
							bgImagePath = "/img/program/draw_event_end.PNG";
						}
						else if(winId.contains("_TARGET_TIMER")) {
							
							Calendar targetTime = Calendar.getInstance();
							targetTime.setTimeInMillis(supportId);
							component.setAttribute("supportData", targetTime);
							bgImagePath = "/img/program/draw_event_target_timer.PNG";
							
							
						}
						else if(winId.contains("_ELAPSE_TIMER")) {
						
							/*long minOffset = supportId.longValue();
							if(minOffset >= 1440){
								minOffset = minOffset/1440;
							}
							else {
								minOffset = minOffset/60;
							}*/
							component.setAttribute("supportData", supportId);
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
						
						//logger.info("the top:"+coordArr[1]+" the left is:"+coordArr[0]);
						
						component = (Window) Executions.createComponents("zul/general/switchcomponent.zul", centerDiv, argumentsMap);
						//component.setId(winId);
						component.setStyle("position:absolute;padding:1px");
						tempDiv = (Div)component.getFellowIfAny("imageDiv");
						//((Image)component.getFellowIfAny("image")).setSrc(bgImagePath);
						component.setTop(coordArr[1]);
						component.setLeft(coordArr[0]);
						
						titleLabel = (Label) component.getFellow("titleLblId");
						titleLabel.setValue(tempComponents.getTitle());
						
						((Label)component.getFellowIfAny("prevCompLblId")).setValue(tempComponents.getPreviousId());
						 ((Label)component.getFellowIfAny("nextCompLblId")).setValue(tempComponents.getNextId());
						 
						 component.setId(winId);
						 
						
							 
							 //progCampsMap = DFSForSwitch(component);
							
						
						 
						
						 
							 String strToformQry = null;
							 Label msgLbl1 = null;
							 Label msgLbl2 = null;
							 List<SwitchCondition> tempSwitchCondLst = switchConditionDao.findByComponentId(tempComponents.
									 									getCompId(), autoProgram.getProgramId());
							
							 if(tempSwitchCondLst.size() > 1 && tempSwitchCondLst.size() == 2) {
								 
									 strToformQry = "";
									 msgLbl1 = (Label)component.getFellowIfAny("messageLine1Id");
									 msgLbl2 = (Label)component.getFellowIfAny("messageLine2Id");
									 
									 //here it is the mistake it only works 
									 //need to handle here for switch allocation
										 
									 SwitchCondition switchCondition = tempSwitchCondLst.get(0);
										 strToformQry = switchCondition.getCondition();
										 
										 msgLbl1.setValue(switchCondition.getLineMessage());
										 msgLbl1.setAttribute("moveTo", switchCondition.getModeFlag()+"||"+switchCondition.getModeAttribute());
										 
									 
										 switchCondition = tempSwitchCondLst.get(1);
										 strToformQry = switchCondition.getCondition();
										 msgLbl2.setValue(switchCondition.getLineMessage());
										 msgLbl2.setAttribute("moveTo", switchCondition.getModeFlag()+"||"+switchCondition.getModeAttribute());
								 
							 } else if(tempSwitchCondLst.size() == 1 ){
								 
								 SwitchCondition switchCondition = tempSwitchCondLst.get(0);
								 
								 msgLbl1 = (Label)component.getFellowIfAny("messageLine1Id");
								 strToformQry = switchCondition.getCondition();
								 
								 msgLbl1.setValue(switchCondition.getLineMessage());
								 msgLbl1.setAttribute("moveTo", switchCondition.getModeFlag()+"||"+switchCondition.getModeAttribute());
								 
								 
							 }
							 
							 
							 logger.info("str to form querdy is=======>"+winId+"---------"+strToformQry);
							//we will be having two switch condition objects for each Switch component
							//examine and prepare view as those should appear
							 component.setAttribute("supportData", strToformQry);//it is just a condition query
							 if(winId.contains("_DATA")) {
								 switchCompWin.add(component);
								 
								 bgImagePath = "/img/program/draw_switches_data.PNG";
								 component.setAttribute("filter", strToformQry.split(Constants.DELIMETER_DOUBLECOLON)[1] );
							 //logger.info(" DFSForSwitch(component)======>"+ DFSForSwitch(component));
							 //component.setAttribute("switchCampMap", DFSForSwitch(component));
							 }
						 
						 else if(winId.contains("_ALLOCATION")) {
							 
							 bgImagePath = "/img/program/draw_switches_allocation.PNG";
						 }
					 
						 tempDiv.setStyle("background:url("+bgImagePath.substring(1)+");background-repeat:no-repeat; "); 
						 component.setAttribute("autoProgramComponents", tempComponents);
						 
					}// else if
					
					
					//createNewComp(img);
					
					
				}//for
				lastModifiedDateLbl.setValue(MyCalendar.calendarToString(autoProgram.getModifiedDate(), MyCalendar.FORMAT_DATETIME_STDATE));
				logger.debug("the num of children in main component centerdiv is=====>"+centerDiv.getChildren().size());
				redrawAllLines();
				
				for (Window window : switchCompWin) {
					window.removeAttribute("switchCampMap");
					window.setAttribute("switchCampMap", DFSForSwitch(window));
					//logger.info("in edit program this "+window.getId()+" campaign map is====>"+(Map)window.getAttribute("switchCampMap"));
					
				}
				
				/*for(Window window : switchCompWin) { // written for debug...
					
					logger.info("in edit program this "+window.getId()+" campaign map is====>"+(Map)window.getAttribute("switchCampMap"));
				}*/
				//redrawAllLines();
		} catch (NumberFormatException e) {
			logger.error("Exception ::" , e);
		}
		
		
		
	}//editProgram
	
	private Label lastModifiedDateLbl;
	
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
		
		//logger.info("the updated global z index is====>"+globalzindex);
		
	}
	
	
	private int getLengthFromStr(String widthOrHeightWithPx) {
		
		try {
			return Integer.parseInt(widthOrHeightWithPx.substring(0, widthOrHeightWithPx.length()-2));
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		return -1;
	} // getLengthFromStr
	
	//private Window programSettingsWinId;
	private Div programSettingsDivId;
	
	//yet to be deleted
	public List<MailingList> getMailingLists() {
		//logger.info("just entered in getMailingLists()------");
		try {
			//List<MailingList> mlLists = mailingListDao.findAllByUser(userIdsSet);
			Set<Long> listIdsSet = (Set<Long>)session.getAttribute(Constants.LISTIDS_SET);
			List<MailingList> mlLists = mailingListDao.findByIds(listIdsSet);
			return mlLists;
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}//getMailingLists
	
	
	
	private Textbox programNameTbId,programDescTbId;
	private Label nameStatusLblId;
	private AutoProgram autoProgram;
	private AutoProgramDao autoProgramDao;
	private AutoProgramDaoForDML autoProgramDaoForDML;
	
	/*
	 * public void checkEmailName(Textbox cNameTbId, Label nameStatusLblId) {
		String emailName = Utility.condense(cNameTbId.getValue().trim());
		
		if(emailName.trim().equals("")){
			nameStatusLblId.setValue("");
			return;
		}else if(!Utility.validateName(emailName)){
			nameStatusLblId.setStyle("color:red");
			nameStatusLblId.setValue("Special characters not allowed");
			return;
		}
		MessageUtil.clearMessage();
		CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
		nameExist =  campaignsDao.checkName(emailName,userId);
		if(nameExist){
			nameStatusLblId.setStyle("color:red");
			nameStatusLblId.setValue("Already exits");
		}else{
			nameStatusLblId.setStyle("color:#023849");
			nameStatusLblId.setValue("Available");
//			cNameTbId.setValue(emailName);
		}
	}
	 */
	
	/**
	 * this method executes when user wants to set(or change) the program properties
	 */
	/*public void onClick$programSettingsAnchId() {
		try {
			
			String mlListName = "";
			Vector<String> mlListNamesVector = new Vector<String>();// holds the configured ml list names
			//programNameTbId.setValue(programName.getValue());
			//programDescTbId.setValue(programDescription.getValue());
			if(programMlListLbId.getAttribute("mlLists") != null) {
				Set<MailingList> mlListSet = (Set)programMlListLbId.getAttribute("mlLists");
				
				for(MailingList mailingList : mlListSet){
					
					mlListName = mailingList.getListName();
					mlListNamesVector.add(mlListName);
				}// for 
				
				for(int i=0; i<programMlListLbId.getItemCount(); i++){ // check and set the selection
					
					if(mlListNamesVector.contains(programMlListLbId.getItemAtIndex(i).getLabel())){
						programMlListLbId.setSelectedIndex(i);
						
					}
				} // for
				
			} // if
			else {
				Messagebox.show("Please select atleast one mailing list");
			}
			programSettingsDivId.setVisible(true);
			
		} catch (WrongValueException e) {
			logger.error("Exception ::" , e);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		
		
	}//onClick$saveProgramBtnId
*/	
	private boolean nameExist;
	private Label errorMsgLblId;;
	
	public void onBlur$programNameTbId$programSettingsWinId() {
		
		String programName = Utility.condense(programNameTbId.getValue().trim());
		if(programName.trim().equals("")){
			 nameStatusLblId.setValue("");
			return;
		}
		else if(!Utility.validateName(programName)){
			nameStatusLblId.setStyle("color:red");
			nameStatusLblId.setValue("Special characters not allowed");
			return;
		}
		nameExist = autoProgramDao.isProgramNameExists(programName, GetUser.getUserId());
		if(nameExist) {
			nameStatusLblId.setStyle("color:red");
			nameStatusLblId.setValue("Already exits");
			return;
		}
		else{
			nameStatusLblId.setStyle("color:#023849");
			nameStatusLblId.setValue("Available");
//			cNameTbId.setValue(emailName);
		
		}
		
	}// onBlur$programNameTbId$programSettingsWinId()
	
	//yet to be deleted
	/**
	 * this method executes when program's basic settings asked to be save
	 */
	public void saveProgramSettings() {
		try {
			//logger.info("----just entered in onClick$saveProgramSettingsBtnId$programSettingsWinId----");
			//when user clicks on applychanges...........
			AutoProgram autoProgram = null;
			Set<MailingList> mlList = null;
			Listitem listItem = null;
			MailingList mailingList = null;
			String listNameStr = "";
			String listIdStr = "";
			//onClick$programSettingsAnchId();
			if(programNameTbId.isValid()) {
				
				//logger.info("yes program name text box is valid......");
					///programName.setValue(programNameTbId.getValue());
				
				
					
					//programDescription.setValue(programDescTbId.getValue());
				
				
					Set mlLists = programMlListLbId.getSelectedItems();
					
					
					
					mlList = new HashSet<MailingList>();
					for(Object obj : mlLists) {
						listItem = (Listitem)obj;
						mailingList = (MailingList)listItem.getValue();
						if(listIdStr.length() != 0){
							listIdStr += ",";
						}
						listIdStr += mailingList.getListId();
						
						if(listNameStr.length() != 0) {
							listNameStr += ",";
						}
						listNameStr += mailingList.getListName();
						mlList.add(mailingList);
						
					}
					//programMlListNames.setValue(listNameStr);
					programMlListLbId.setAttribute("mlLists", mlList);
					//programMlListNames.setTooltiptext(listNameStr);
				
				
				/*if(programSettingsWinId.getAttribute("autoProgram")!= null){
					autoProgram = (AutoProgram)programSettingsWinId.getAttribute("autoProgram");
					
					autoProgram.setDescription(programDescription.getValue());
					autoProgram.setMailingLists(mlList);
					autoProgramDao.saveOrUpdate(autoProgram);
					programSettingsWinId.removeAttribute("autoProgram");
					programSettingsWinId.setAttribute("autoProgram",autoProgram);
				}*/
					
					/*Clients.evalJavaScript("jq(this.$f('programSettingsWinId')).slideToggle(1000);");
					programSettingsDivId.setVisible(false);*/
			}//if
			/*
			 * 
				try{
					logger.debug("-----just entered----");
					if(smsCampaignsDao == null){
						smsCampaignsDao = (SMSCampaignsDao)SpringUtil.getBean("smsCampaignsDao");
					}
					
					if(dispMlListsLBoxId.getItemCount() == 0){ // if user have no mailing lists
						MessageUtil.setMessage("Please Create the Mailing List.", "color:red;", "top");
						return false;
					}
					if(dispMlListsLBoxId.getSelectedIndex() == -1){ // if no mailing list is selected 
						logger.debug("selected item is======>"+dispMlListsLBoxId.getSelectedIndex());
						MessageUtil.setMessage("select atleast one Mailing List", "color:red;", "top");
						return false;
					}
					
					Set mlLists =  dispMlListsLBoxId.getSelectedItems();
					Set<MailingList> mlList = new HashSet<MailingList>();
					Listitem listItem = null;
					MailingList mailingList = null;
					String listIdStr = "";
					
					for( Object obj : mlLists){
						listItem = (Listitem)obj;
						mailingList = (MailingList)listItem.getValue();
						if(listIdStr.length() != 0){
							listIdStr += ",";
						}
						listIdStr += mailingList.getListId();
						mlList.add(mailingList);
						
					}//for
					logger.debug("list ids are=====>"+listIdStr);
					smsCampaigns.setMailingLists(mlList);
					
					
					else {
						MessageUtil.setMessage("The selected list have no contacts:List should not be empty.", "color:red", "top");
						return false;
					}
					return true;
				}catch ( Exception e) {
					logger.error("** Exception while configuring mailinglist(s)",e);
					return false;
				}//catch
				
			
			 */
			
			
			/*if((!programNameTbId.getValue().trim().equals("")) && (programMlListLbId.getSelectedIndex() != -1)){
				
				autoProgram = new AutoProgram(programNameTbId.getValue(), Calendar.getInstance(), Calendar.getInstance(),
						 GetUser.getUserObj() );
				
				if(programSettingsWinId.getAttribute("autoProgramObj") == null){ 
					autoProgramDao.saveOrUpdate(autoProgram);
				}
			}*/
			

		} catch (WrongValueException e) {
			logger.error("Exception ::" , e);
		}
		
		
	}//onclick$saveProgramSettings;

	
	/*private final class MyRenderer implements ListitemRenderer,EventListener {
		
		
		public MyRenderer() {
			super();
		}
		
		@Override
		public void render(Listitem item, Object obj) throws Exception {

			Listcell lc = null;
			MailingList mailingList = null;
			if(obj instanceof MailingList) {
				
				mailingList = (MailingList)obj;
				item.setValue(mailingList);
				
				lc = new Listcell(mailingList.getListName());
				lc.setParent(item);
				
			}
			
			
			
			
		}
		
		@Override
		public void onEvent(Event arg0) throws Exception {
			
		}
		
		
		
	}*/
	/*private static void setDrawingState(Drawable drawable, String storkeColor, double alphaVal) {
		// get drawing type
		int doStroke = (strokeTypeBox.getSelectedIndex() > 0) ? 1 : 0;
		int doFill = (fillTypeBox.getSelectedIndex() > 0) ? 2 : 0;
		int doStroke = 1;
		int doFill = 0;
		String drawingType = "";
		
		switch(doStroke + doFill){
			case 0:
				drawingType = Drawable.DrawingType.NONE;
				break;
			case 1:
				drawingType = Drawable.DrawingType.STROKE;
				break;
			case 2:
				drawingType = Drawable.DrawingType.FILL;
				break;
			case 3:
			default:
				drawingType = Drawable.DrawingType.BOTH;
				break;
		}
		
//		String storkeColor = strokeColorBox.getValue();
//		String fillColor = fillColorBox.getValue(); 
		
		//String storkeColor = LINE_DRAW_COLOR;
		String fillColor = "#000000"; 
		
		//double alpha = alphaSlider.getCurpos() / 100.0;
		//bug #3006313: getCurpos() does not work
		
		drawable.setDrawingType(drawingType);
		drawable.setStrokeStyle(storkeColor);
		drawable.setFillStyle(fillColor);
		drawable.setAlpha(alphaVal);
		
	}
	*/

	
	/*	
	private double[] getDataValues(Event event){
		Event evt = ((ForwardEvent) event).getOrigin();
		Object[] data = (Object[]) evt.getData();
		double[] result = new double[data.length];
		for(int i=0; i<result.length; i++) {
			logger.info("---"+data[i]);
			
			if(data[i] instanceof Double) {
				result[i] = (Double) data[i];
			} else {
				result[i] = (Integer) data[i];
			}
		}
		return result;
	}
	
*/	
	
	
	
	private void constructShapes(){
		_shapes = new ArrayList<Shape>();
		_shapeNames = new ArrayList<String>();
		
		_shapeNames.add("Rectangle");
		_shapes.add(new Rectangle(0,0,1000,1000));
		
		_shapeNames.add("Line");
		_shapes.add(new Path().moveTo(0,0).lineTo(1000,1000).closePath());
		
		
		_shapeNames.add("Triangle");
		_shapes.add(new Path().moveTo(0,0).lineTo(0,1000).lineTo(1000,500)
				.lineTo(0,0).closePath());
		
		_shapeNames.add("Circle");
		_shapes.add(new Path(new Arc2D.Double(0, 0, 1000, 1000, 0, 360, 
				Arc2D.CHORD)));
		
		_shapeNames.add("Hexagon");
		_shapes.add(ProgramUtility.ngon(500,6));
		
		_shapeNames.add("Condition");
		_shapes.add(new Path().moveTo(0,0).lineTo(0,1000).lineTo(1000,1000).lineTo(1000,0).lineTo(0,0)
				.moveTo(0,300).lineTo(1000,300).moveTo(0,700).lineTo(1000,700)
				.closePath());
		
		_shapeNames.add("DHexagon");
		_shapes.add(ProgramUtility.ngon(500,4));
		
		_shapeNames.add("Star");
		_shapes.add(ProgramUtility.nstar(500,5,43.5));
		
		_shapeNames.add("Heart");
		_shapes.add(ProgramUtility.heart(1000));
		
	}
	

	/*
	
	private Path ngon(double r, int n){
		Path p = new Path().moveTo(r, 0);
		for(int i=1; i<n+1; i++){
			double arg = Math.PI * (1.5 + (2.0*i)/n);
			p.lineTo(r + r * Math.cos(arg), r + r * Math.sin(arg));
		}
		p.closePath();
		return p;
	}
	
	private Path nstar(double r, int n, double theta){
		Path p = new Path().moveTo(r, 0);
		double r2 = r * Math.sin(Math.PI*theta/360) / Math.sin(Math.PI*(theta/360 + 2.0/n));
		
		for(int i=1; i<n+1; i++){
			double arg1 = Math.PI * (1.5 + (2.0*i)/n);
			double arg2 = arg1 - Math.PI/n;
			p.lineTo(r + r2 * Math.cos(arg2), r + r2 * Math.sin(arg2));
			p.lineTo(r + r * Math.cos(arg1), r + r * Math.sin(arg1));
		}
		
		p.closePath();
		return p;
	}
	
	private Path heart(double r){
		double ctrp1X = 0.1 * r;
		double ctrp1Y = -0.2 * r;
		double ctrp2X = -0.04 * r;
		double ctrp2Y = 0.2 * r;
		double ctrp3X = 0.08 * r;
		double ctrp3Y = -0.4 * r;
		double ctrp4X = 0;
		double ctrp4Y = -0.34 * r;
		
		double midY = 0.4 * r;
		double midYC = 0.34 * r;
		
		double p1X = 0.5 * r;
		double p1Y = r;
		double p2X = 0.96 * r;
		double p2Y = midY;
		double p3X = 0.5 * r;
		double p3Y = midYC;
		double p4X = 0.04 * r;
		double p4Y = midY;
		
		Path p = new Path().moveTo(p1X, p1Y);
		p.curveTo(p1X + ctrp1X, p1Y + ctrp1Y,
				  p2X + ctrp2X, p2Y + ctrp2Y, p2X, p2Y);
		p.curveTo(p2X + ctrp3X, p2Y + ctrp3Y,
				  p3X + ctrp4X, p3Y + ctrp4Y, p3X, p3Y);
		p.curveTo(p3X - ctrp4X, p3Y + ctrp4Y,
				  p4X - ctrp3X, p4Y + ctrp3Y, p4X, p4Y);
		p.curveTo(p4X - ctrp2X, p4Y + ctrp2Y,
				  p1X - ctrp1X, p1Y + ctrp1Y, p1X, p1Y);
		
		return p;
	}
*/
	
	
	Window propertiesWinId;
	Div topDivId;
	//Div centerDivId;
	Div bottomDivId;
	
	
	public void setComponentTitle(Window windowComp) {
		
		//List<Window> windCompList = centerDiv.getChildren();
		String winId = windowComp.getId();
		Label tempLabel = (Label)windowComp.getFellowIfAny("titleLblId");
		tempLabel.setValue(tempLabel.getValue()+winId.substring(winId.indexOf("-"), winId.length()-1));
		
		
		
	}
	
	
	
} // Class


