/**
 * 
 */
package org.mq.optculture.model;

import java.io.Serializable;

/**
 * @author manjunath.nunna
 *
 */
public class BaseResponseObject implements Serializable{


	private static final long serialVersionUID = 3174428992805704811L;

	private String action;
	private String responseCode;
	private String responseDesc;
	private Object responseObject;
	private String jsonValue;
	private String responseStr;
	

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
	 * @return the responseCode
	 */
	public String getResponseCode() {
		return responseCode;
	}

	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	/**
	 * @return the responseDesc
	 */
	public String getResponseDesc() {
		return responseDesc;
	}

	/**
	 * @param responseDesc the responseDesc to set
	 */
	public void setResponseDesc(String responseDesc) {
		this.responseDesc = responseDesc;
	}

	/**
	 * @return the responseObject
	 */
	public Object getResponseObject() {
		return responseObject;
	}

	/**
	 * @param responseObject the responseObject to set
	 */
	public void setResponseObject(Object responseObject) {
		this.responseObject = responseObject;
	}

	public String getJsonValue() {
		return jsonValue;
	}

	public void setJsonValue(String jsonValue) {
		this.jsonValue = jsonValue;
	}

	/**
	 * 
	 * @return the response string
	 */
	public String getResponseStr() {
		return responseStr;
	}

	/**
	 * 
	 * @param responseStr the response string to set
	 */
	public void setResponseStr(String responseStr) {
		this.responseStr = responseStr;
	}
}
