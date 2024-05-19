package org.mq.optculture.model;

import java.io.File;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.DR.prism.PrismBasedDRRequest;
import org.mq.optculture.utils.XMLUtil;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

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
	
	
	public static void main(String[] args) {/*
		try {
			String inboxDRPath = "/home/proumya/2561651219502976144_20180630091411783218.xml";
			File xmlFile = new File(inboxDRPath);
			
			
			
			Object object =  XMLUtil.unMarshal(xmlFile.toString(), RequestToOC.class);
			RequestToOC digiList = (RequestToOC)object;
						
			String requestJson = digiList.getREQUESTJSON();
			requestJson = "{\"drDocument\": "+requestJson+"}";
			System.out.println(requestJson);
			Gson gson = new Gson();
			PrismBasedDRRequest  jsonRequest = gson.fromJson(requestJson, PrismBasedDRRequest.class);
			System.out.println(jsonRequest);
			String JsonStr = gson.toJson(jsonRequest);
			System.out.println(JsonStr);
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BaseServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	*/}

}
