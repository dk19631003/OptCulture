package org.mq.marketer.campaign.controller.program;



public enum ProgramEnum {
	 
	//****  Event Type Settings  ************
	EVENT_CUST_ACTIVATED  (1, 1, "draw_event_cust_activated.PNG", "", "", "Customer Activated", 0, 1),
	EVENT_CUST_DEACTIVATED(2, 1, "draw_event_cust_deactivated.PNG", "", "", "Customer Deactivated", 0, 1),
	EVENT_SCHEDULED_FILTER(3, 1, "draw_event_scheduled_filter.PNG", "", "", "Scheduled Filter", 0, 1),
	EVENT_CUSTOM_EVENT	  (4, 1, "draw_event_custom_event.PNG", "", "", "Custom Event", 0, 1),

	EVENT_ELAPSE_TIMER	  (5, 1, "draw_event_elapse_timer.PNG", "", "", "Elapse Timer", 3, 1),
	EVENT_TARGET_TIMER	  (6, 1, "draw_event_target_timer.PNG", "", "", "Target Timer", 3, 1),
	EVENT_END			  (7, 1, "draw_event_end.PNG", "", "", "End", 4, 0),
	
	
	//****  Activity Type Settings  ************
	ACTIVITY_SEND_EMAIL   (11, 2, "draw_activities_send_email_campaign.PNG", "Send Email", "Send Email Campaign", "Select Campaign", 3, 1),
	ACTIVITY_SEND_SMS	  (12, 2, "draw_activities_send_sms_message.PNG", "Send SMS", "Send SMS Message", "", 3, 1),
	ACTIVITY_SET_DATA	  (13, 2, "draw_activities_set_data.PNG", "Set Data", "Set data", "", 3, 1),

	
	//****  Switch Type Settings  ************
	SWITCH_DATA			  (21, 3, "draw_switches_data.PNG", "", "", "Switch Data", 3, 2),
	SWITCH_ALLOCATION	  (22, 3, "draw_switches_allocation.PNG", "", "", "Switch Allocation", 3, 2);
	
	
	private int code;
	private int type;
	private String draw_image;
	private String title;
	private String message;
	private String footer;
	
	private int prev_size;
	private int next_size;
	
	private int width;
	private int height;
	
		
	private ProgramEnum() {
	}


	/**
	 * @param code
	 * @param type
	 * @param draw_image
	 * @param title
	 * @param message
	 * @param footer
	 * @param prev_size
	 * @param next_size
	 */
	private ProgramEnum(int code, int type, String draw_image, String title,
			String message, String footer, int prev_size, int next_size) {
		this.code = code;
		this.type = type;
		this.draw_image = draw_image;
		this.title = title;
		this.message = message;
		this.footer = footer;
		this.prev_size = prev_size;
		this.next_size = next_size;
		    
		if(type==1) {
			width=46+4;
			height=46+4;
			
		}
		else if(type==2) {
			width=128+4;
			height=80+4;
		}
		else if(type==3) {
			width=72+4;
			height=72+4;
		}
	}


	public int getCode() {
		return code;
	}


	public int getType() {
		return type;
	}


	public String getDraw_image() {
		return draw_image;
	}


	public String getTitle() {
		return title;
	}


	public String getMessage() {
		return message;
	}


	public String getFooter() {
		return footer;
	}


	public int getPrev_size() {
		return prev_size;
	}


	public int getNext_size() {
		return next_size;
	}


	public int getWidth() {
		return width;
	}


	public int getHeight() {
		return height;
	}
	
} 


