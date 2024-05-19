package org.mq.optculture.sales.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Request")
public class RequestToOC {

	private String REQUESTURL;
	private String REQUESTJSON;
	private String REQUESTFORMAT;
	private String REQUESTSOURCE;
	private String REQUESTSTRING;

	@XmlElement(name = "REQUESTURL")
	public void setREQUESTURL(String REQUESTURL) {
		this.REQUESTURL = REQUESTURL;
	}

	public String getREQUESTURL() {
		return REQUESTURL;
	}

	public String getREQUESTJSON() {
		return REQUESTJSON;
	}

	@XmlElement(name = "REQUESTJSON")
	public void setREQUESTJSON(String REQUESTJSON) {
		this.REQUESTJSON = REQUESTJSON;
	}

	public String getREQUESTFORMAT() {
		return REQUESTFORMAT;
	}

	@XmlElement(name = "REQUESTFORMAT")
	public void setREQUESTFORMAT(String rEQUESTFORMAT) {
		REQUESTFORMAT = rEQUESTFORMAT;
	}

	@XmlElement(name = "REQUESTSTRING")
	public void setREQUESTSTRING(String rEQUESTSTRING) {
		REQUESTSTRING = rEQUESTSTRING;
	}

	public String getREQUESTSTRING() {
		return REQUESTSTRING;
	}

	public String getREQUESTSOURCE() {
		return REQUESTSOURCE;
	}

	@XmlElement(name = "REQUESTSOURCE")
	public void setREQUESTSOURCE(String rEQUESTSOURCE) {
		REQUESTSOURCE = rEQUESTSOURCE;
	}

}
