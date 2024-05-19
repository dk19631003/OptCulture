/**
 * 
 */
package org.mq.optculture.model;

import java.io.Serializable;

/**
 * @author manjunath.nunna
 *
 */
public class BaseRequestObject implements Serializable{
	
	
	private static final long serialVersionUID = -3551817185009000029L;
	private String action;
	private String jsonValue;
	private String msgContent;

	private String timeFormat;
	

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return the jsonValue
	 */
	public String getJsonValue() {
		return jsonValue;
	}

	/**
	 * @param jsonValue the jsonValue to set
	 */
	public void setJsonValue(String jsonValue) {
		this.jsonValue = jsonValue;
	}

	/**
	 * 
	 * @return the msgContent
	 */
	public String getMsgContent() {
		return msgContent;
	}

	/**
	 * 
	 * @param msgContent the msgContent to set
	 */
	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}
	
	public String getTimeFormat() {
		return timeFormat;
	}
	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}
}
