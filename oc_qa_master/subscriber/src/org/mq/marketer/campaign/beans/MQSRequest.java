package org.mq.marketer.campaign.beans;

import java.util.Calendar;

// Generated 30 Nov, 2009 6:24:52 PM by Hibernate Tools 3.2.0.CR1



/**
 * MQSRequest generated by hbm2java
 */
public class MQSRequest  implements java.io.Serializable {


     private Long id;
     private String refNumber;
     private String service;
     private String status;
     private Calendar date;
     private String reqestXML;
     private String responseXML;

    public MQSRequest() {
    }
	
    public MQSRequest(String refNumber, String service) {
        this.refNumber = refNumber;
        this.service = service;
    }
    public MQSRequest(String refNumber, String service, String status, Calendar date, String reqestXML, String responseXML) {
       this.refNumber = refNumber;
       this.service = service;
       this.status = status;
       this.date = date;
       this.reqestXML = reqestXML;
       this.responseXML = responseXML;
    }
   
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    public String getRefNumber() {
        return this.refNumber;
    }
    
    public void setRefNumber(String refNumber) {
        this.refNumber = refNumber;
    }
    public String getService() {
        return this.service;
    }
    
    public void setService(String service) {
        this.service = service;
    }
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    public Calendar getDate() {
        return this.date;
    }
    
    public void setDate(Calendar date) {
        this.date = date;
    }
    public String getReqestXML() {
        return this.reqestXML;
    }
    
    public void setReqestXML(String reqestXML) {
        this.reqestXML = reqestXML;
    }
    public String getResponseXML() {
        return this.responseXML;
    }
    
    public void setResponseXML(String responseXML) {
        this.responseXML = responseXML;
    }

}

