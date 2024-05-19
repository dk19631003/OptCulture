/**
 * 
 */
package org.mq.marketer.campaign.controller.program;

/**
 * @author krishna.arkala
 *
 */

public interface PDConstants {
	
	// ENEVT SETTINGS
	int CUST_ACTIVATED_PREV_SIZE = 0;
	int CUST_DEACTIVATED_PREV_SIZE = 0;
	int SCHEDULED_FILTER_PREV_SIZE = 0;
	int CUSTOM_EVENT_PREV_SIZE = 0;

	int ELAPSE_TIMER_PREV_SIZE = 3;
	int TARGET_TIMER_PREV_SIZE = 3;
	int END_PREV_SIZE = 4;

	int CUST_ACTIVATED_NEXT_SIZE = 1;
	int CUST_DEACTIVATED_NEXT_SIZE = 1;
	int SCHEDULED_FILTER_NEXT_SIZE = 1;
	int CUSTOM_EVENT_NEXT_SIZE = 1;

	int ELAPSE_TIMER_NEXT_SIZE = 1;
	int TARGET_TIMER_NEXT_SIZE = 1;
	int END_NEXT_SIZE = 0;

	//****  ACTIVITY SETTINGS  ************
	int SEND_EMAIL_PREV_SIZE=3;
	int SEND_SMS_PREV_SIZE=3;
	int SET_DATA_PREV_SIZE=3;

	int SEND_EMAIL_NEXT_SIZE=1;
	int SEND_SMS_NEXT_SIZE=1;
	int SET_DATA_NEXT_SIZE=1;
	
	
	
}
