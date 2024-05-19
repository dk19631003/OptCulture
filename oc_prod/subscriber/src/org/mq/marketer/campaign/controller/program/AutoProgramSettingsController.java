package org.mq.marketer.campaign.controller.program;

import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.AutoProgram;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.AutoProgramComponentsDao;
import org.mq.marketer.campaign.dao.AutoProgramComponentsDaoForDML;
import org.mq.marketer.campaign.dao.AutoProgramDao;
import org.mq.marketer.campaign.dao.AutoProgramDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.East;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

public class AutoProgramSettingsController extends GenericForwardComposer {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);	private final ListitemRenderer renderer = new MyRenderer();
	private Listbox userProgramsLbId,programTemplatesLbId;
	private Users user;
	private Tab programTemplatesTabId,userProgramsTabId;
	private TimeZone clientTimeZone;
	private Session session;
	private East descEastId,tempDescEastId;
	private Label programNameLblId,programDescLblId,tempProgramDescLblId,tempProgramNameLblId;
	private Include programDesignerIncId;
	private Div noTempDivId;
	private Borderlayout tempBorderLayoutId;
	//private Window programDesignerIncId;
	
	private AutoProgramDao autoProgramDao;
	private AutoProgramDaoForDML autoProgramDaoForDML;
	private AutoProgramComponentsDao autoProgramComponentsDao;
	private AutoProgramComponentsDaoForDML autoProgramComponentsDaoForDML;
	private UsersDao usersDao;
	
	
	public AutoProgramSettingsController() {
		autoProgramDao = (AutoProgramDao)SpringUtil.getBean("autoProgramDao");
		autoProgramDaoForDML = (AutoProgramDaoForDML)SpringUtil.getBean("autoProgramDaoForDML");
		autoProgramComponentsDao = (AutoProgramComponentsDao)SpringUtil.getBean("autoProgramComponentsDao");
		autoProgramComponentsDaoForDML = (AutoProgramComponentsDaoForDML)SpringUtil.getBean("autoProgramComponentsDaoForDML");
		usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		
		user = GetUser.getUserObj();
		session = Sessions.getCurrent();
		clientTimeZone = (TimeZone)session.getAttribute("clientTimeZone");
		
		String style = "font-weight:bold;font-size:15px;color:#313031;" +
						"font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("My Programs","",style,true);
		
		
	}
	
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		logger.debug("----just entered----");
		
		AutoProgram autoProgram = null;
		Users captiwayUser = usersDao.findByUsername("Captiway");
		
		if(captiwayUser == null) {
			
			logger.debug("no captiway user exist...");
			//noTempDivId.setVisible(true);
			//tempBorderLayoutId.setVisible(false);
			return;
			
		}
		
		
		Long userId = captiwayUser.getUserId();
		
		
		List<AutoProgram> listOfProgs = autoProgramDao.getUserPrograms(userId);
		
		if(listOfProgs == null || listOfProgs.size() <= 0 ) {
			logger.debug("no captiway user exist...");
			/*noTempDivId.setVisible(true);
			tempBorderLayoutId.setVisible(false);*/
			return;
		}
		
		programTemplatesLbId.setModel(new ListModelList(autoProgramDao.getUserPrograms(userId)));
		programTemplatesLbId.setItemRenderer(renderer);
		programTemplatesLbId.setSelectedIndex(0);
		
		tempDescEastId.setVisible(true);
		
		
		autoProgram = (AutoProgram)programTemplatesLbId.getListModel().getElementAt(0);
		tempProgramNameLblId.setValue(autoProgram.getProgramName());
		tempProgramDescLblId.setValue(autoProgram.getDescription());
		
		
		
		
		
		List<AutoProgram> userProgramList = autoProgramDao.getUserPrograms(user.getUserId());
		if(userProgramList.size() != 0) {//if user programs exists
			
			userProgramsTabId.setSelected(true);
			userProgramsLbId.setModel(new ListModelList(userProgramList));
			userProgramsLbId.setItemRenderer(renderer);
			userProgramsLbId.setSelectedIndex(0);
			
			descEastId.setVisible(true);
			
			autoProgram = (AutoProgram)userProgramsLbId.getListModel().getElementAt(0);
			programNameLblId.setValue(autoProgram.getProgramName());
			programDescLblId.setValue(autoProgram.getDescription());
			
			String status = autoProgram.getStatus();
			
			if(status.equalsIgnoreCase(Constants.AUTO_PROGRAM_STATUS_ACTIVE)) {
				progPublishBtnId.setLabel("Inactive");
			}else if(status.equalsIgnoreCase(Constants.AUTO_PROGRAM_STATUS_DRAFT)) {
				progPublishBtnId.setLabel("Active");
				
			}

		}else{
			//if user dont have his created  programs we have to show the captiway created program templates.
			
			programTemplatesTabId.setSelected(true);
		}
			
		
		
		logger.debug("----before exitting----");		
		
	}//doAfterCompose
	
	
	
	
	
	private Button progPublishBtnId,ProgViewAnalisysBtnId;
	
	
	/**
	 * This method used to show the online reports of the designed program.
	 * By default it shows the reports within the last 24 hours.
	 */
	
	public void onClick$ProgViewAnalisysBtnId() {
		logger.debug("---to view the analysis of the program----");
		AutoProgram autoProgram = (AutoProgram)userProgramsLbId.getSelectedItem().getValue();
		session.removeAttribute("viewProgramAnalysis");
		session.setAttribute("viewProgramAnalysis", autoProgram);//to show the selected program online reports.
		Redirect.goTo(PageListEnum.PROGRAM_PROGRAM_ANALYSIS);//this page takes the responsibility to show the selected program reports.
		
		
	}
	
	
	/*public void onClick$programTemplatesTabId() {
		
		Users user = usersDao.findByUsername("Captiway");
		
		if(user == null) {
			
			logger.debug("no captiway user exist...");
			noTempDivId.setVisible(true);
			tempBorderLayoutId.setVisible(false);
			return;
			
		}
		
		Long userId = user.getUserId();
		programTemplatesTabId.setSelected(true);
		List<AutoProgram> listOfProgs = autoProgramDao.getUserPrograms(userId);
		
		if(listOfProgs == null || listOfProgs.size() <= 0 ) {
			logger.debug("no captiway user exist...");
			noTempDivId.setVisible(true);
			tempBorderLayoutId.setVisible(false);
			return;
		}
		programTemplatesLbId.setModel(new ListModelList(listOfProgs));
		programTemplatesLbId.setItemRenderer(renderer);
		programTemplatesLbId.setSelectedIndex(0);
		
		tempDescEastId.setVisible(true);
		
		
		AutoProgram autoProgram = (AutoProgram)programTemplatesLbId.getListModel().getElementAt(0);
		tempProgramNameLblId.setValue(autoProgram.getProgramName());
		tempProgramDescLblId.setValue(autoProgram.getDescription());
		
	}*/
	
	/**
	 * This method called when the user selects the program that have been already created.  
	 */
	public void onSelect$userProgramsLbId() {
		
		descEastId.setVisible(true);
		AutoProgram autoProgram = (AutoProgram)userProgramsLbId.getSelectedItem().getValue();
		
		//to show the name and desc of the selected prog right side.
		programNameLblId.setValue(autoProgram.getProgramName());
		programDescLblId.setValue(autoProgram.getDescription());
		
		String status = autoProgram.getStatus();
		
		//modify the label of the publish button accordingly based on the selected program's status.
		if(status.equalsIgnoreCase(Constants.AUTO_PROGRAM_STATUS_ACTIVE)) {
			progPublishBtnId.setLabel("Inactive");
		}else if(status.equalsIgnoreCase(Constants.AUTO_PROGRAM_STATUS_DRAFT)) {
			progPublishBtnId.setLabel("Active");
			
		}
		
		
	}//onSelect$userProgramsLbId
	
	
	public void onSelect$programTemplatesLbId() {
		
		tempDescEastId.setVisible(true);
		AutoProgram autoProgram = (AutoProgram)programTemplatesLbId.getSelectedItem().getValue();
		
		//to show the name and desc of the selected prog right side.
		tempProgramNameLblId.setValue(autoProgram.getProgramName());
		tempProgramDescLblId.setValue(autoProgram.getDescription());
		
		/*String status = autoProgram.getStatus();
		
		//modify the label of the publish button accordingly based on the selected program's status.
		if(status.equalsIgnoreCase(Constants.AUTO_PROGRAM_STATUS_ACTIVE)) {
			progPublishBtnId.setLabel("Inactive");
		}else if(status.equalsIgnoreCase(Constants.AUTO_PROGRAM_STATUS_DRAFT)) {
			progPublishBtnId.setLabel("Active");
			
		}*/
		
		
		
	}//onSelect$programTemplatesLbId()
	
	/*public List<AutoProgram> getUserPrograms() {
		List<AutoProgram> userProgramList = autoProgramDao.getUserPrograms(user.getUserId());
		return userProgramList;
		
		
		
	}*/
	
	public void onClick$useProgTemplateBtnId() {
		
		
		
		logger.debug("---to use and edit the program---");
		
		AutoProgram autoProgram = (AutoProgram)programTemplatesLbId.getSelectedItem().getValue();
		
		//String status = autoProgramDao.getStatusById(autoProgram.getProgramId(), user.getUserId());
		/*if(status.equalsIgnoreCase("Active")) {
			
			//MessageUtil.setMessage("Program is under publish and it is running: can not be edit," +	"click 'stop' and continue for edit", "color:red", "top");
			//return;
			
		}*/
		
		session.setAttribute("editProgram", "use");
		session.setAttribute("autoProgram", autoProgram);
		Redirect.goTo(PageListEnum.GENERAL_PROGRAM_DESIGNER);
		
		
	}//onClick$useProgTemplateBtnId()
	
	
	
	
	/**
	 * This method called when user wants to change the status of the program.
	 */
	public void onClick$progPublishBtnId() {
		
		try {
			logger.debug("----to change the status of the program----");
			if( Messagebox.show("Are you sure you want to create the program? "+progPublishBtnId.getLabel()+"?","Confirm",
					 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK ){
				AutoProgram tempAutoProgram = (AutoProgram)userProgramsLbId.getSelectedItem().getValue();
				
				if(progPublishBtnId.getLabel().equalsIgnoreCase("Inactive")) {
					
					tempAutoProgram.setStatus(Constants.AUTO_PROGRAM_STATUS_DRAFT);
					
				}else if(progPublishBtnId.getLabel().equalsIgnoreCase("Active")) {
					
					tempAutoProgram.setStatus(Constants.AUTO_PROGRAM_STATUS_ACTIVE);
					
				}
				
				autoProgramDaoForDML.saveOrUpdate(tempAutoProgram);
				Messagebox.show("Program status modified successfully.");
				Utility.getXcontents().invalidate();
				//((ListModelList)userProgramsLbId.getModel()).retainAll(c)invalidate();
			}//if
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}
		
		
	}//onClick$progPublishBtnId
	
	
	
	private Button createProgramBtnId;
	Window progWindow=null;
	
	
	/**
	 * This method called when user wants to create a new program.
	 */
	public void onClick$createProgramBtnId() {
		logger.debug("---to create a new program---");
//		Redirect.goTo("general/programdesigner");
		//logger.info("---just entered--onclick-");
		/*if(progWindow==null)
			progWindow = (Window) Executions.createComponents("zul/general/programdesigner.zul", null, null);
		logger.info("---just entered-after-onclick-");*/
		session.removeAttribute("editProgram");
		session.removeAttribute("autoProgram");
		session.removeAttribute("viewProgramAnalysis");
		
		//Executions.createComponents("zul/general/programdesigner.zul", programDesignerIncId, null);
		//Redirect.goTo("general/programCreateIndex");
		Redirect.goTo(PageListEnum.GENERAL_PROGRAM_DESIGNER);
		/*programDesignerIncId.setSrc("zul/general/programdesigner.zul");
		programDesignerIncId.getFellowIfAny("programdesignerWinId").setVisible(true);*/
		
		//progWindow.setVisible(true);
		
	}
	
	private Window programDesignerOneWinId;
	
	
	/**
	 * This method called when user wants to edit the existing program.
	 */
	public void onClick$progEditBtnId() {
		
		try {
			//TODO need to get the program object and save it in the session redirect the page to programdesigner.zul
			//make a differentiation there b/w new and edit modes
			logger.debug("---to edit the program---");
			
			AutoProgram autoProgram = (AutoProgram)userProgramsLbId.getSelectedItem().getValue();
			
			String status = autoProgramDao.getStatusById(autoProgram.getProgramId(), user.getUserId());
			/*if(status.equalsIgnoreCase("Active")) {
				
				//MessageUtil.setMessage("program is under publish and it is running: can not be edit," +	"click 'stop' and continue for edit", "color:red", "top");
				//return;
				
			}*/
			
			session.setAttribute("editProgram", "edit");
			session.setAttribute("autoProgram", autoProgram);
			Redirect.goTo(PageListEnum.GENERAL_PROGRAM_DESIGNER);
			
			//programDesignerIncId = (Include)programDesignerOneWinId.getFellowIfAny("programDesignerIncId");
			/*programDesignerIncId.setSrc("zul/general/programdesigner.zul");
			programDesignerIncId.getFellowIfAny("programdesignerWinId").setVisible(true);*/
			//programDesignerOneWinId.setVisible(true);
			//programDesignerOneWinId.doModal();
			
			/*try {
				Thread.sleep(50000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::", e);
			}*/
			
			/*ProgramDesignerController tempObj  = ((ProgramDesignerController)session.getAttribute("programDesignerController"));
			logger.info("the temp object is====>"+tempObj);
			if(tempObj != null) tempObj.editProgram(autoProgram);*/
		} catch (Exception e) {
			logger.error("Exception ::" , e);;
		} 
		
		
		
	}//onClick$progEditBtnId()
	
	
	private Button progDeleteBtnId;
	
	
	/**
	 * This method called when user wants to delete the program.
	 */
	public void onClick$progDeleteBtnId () {
		
		
		try {
			logger.debug("----to delete the program----");
			if( Messagebox.show("Are you sure you want to delete the program?","Confirm",
					 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK ){
			
			
				AutoProgram autoProgram = (AutoProgram)userProgramsLbId.getSelectedItem().getValue();
				/*logger.info("the autoProgram to be deleted is====>"+autoProgram.toString()+" " +
									"the programId="+autoProgram.getProgramId()+" the status is===="+autoProgram.getStatus());*/
				String status = autoProgramDao.getStatusById(autoProgram.getProgramId(), user.getUserId());
				
				if(status.equalsIgnoreCase("Active")) {//need to know......
					
					MessageUtil.setMessage("Program is under publish and it is running. Cannot be deleted. " +
							"Click 'Stop' and continue to delete.", "color:red", "top");
					return;
					
				}
				userProgramsLbId.removeItemAt(userProgramsLbId.getSelectedIndex());
				autoProgramDaoForDML.delete(autoProgram);
				autoProgramComponentsDaoForDML.deleteByProgramId(autoProgram.getProgramId());
				
				
				MessageUtil.setMessage("Program deleted successfully.", "color:green", "top");
			}
		} catch (Exception e) {
			logger.error("Exception ::" , e);;
		}
	}
	
	
	private final class MyRenderer implements ListitemRenderer,EventListener {
		
		public MyRenderer() {
			super();
		}
		
		
		@Override
		public void render(Listitem item, Object obj, int arg2) throws Exception {
			
			try {
				Listcell lc = null;
				AutoProgram autoProgram = null;
				
				if(obj instanceof AutoProgram) {
					autoProgram = (AutoProgram)obj;
					item.setValue(autoProgram);
					
					
					lc = new Listcell();
					
					Image img = new Image("/img/icons/messages.gif");
					img.setParent(lc);
					lc.setParent(item);
					
					lc = new Listcell(autoProgram.getProgramName());
					lc.setParent(item);
					
					lc = new Listcell(autoProgram.getStatus());
					lc.setParent(item);
					
					lc = new Listcell(MyCalendar.calendarToString(autoProgram.getModifiedDate(), 
												MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
					lc.setParent(item);
					
					lc = new Listcell(autoProgram.getUser().getUserName());
					lc.setParent(item);
				}
			} catch (Exception e) {
				logger.error("Exception while rendering the autoPrograms objecys",e);
			}
			
			
			
		}//render
		
		@Override
		public void onEvent(Event arg0) throws Exception {
			logger.debug("---just entered---");
		}//onEvent
		
	}//MyRenderer
	
}
