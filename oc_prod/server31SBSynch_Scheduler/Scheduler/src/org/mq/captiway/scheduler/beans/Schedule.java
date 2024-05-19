package org.mq.captiway.scheduler.beans;
// Generated 30 Jun, 2009 6:40:08 PM by Hibernate Tools 3.2.0.CR1


import java.util.Date;

/**
 * Schedule generated by hbm2java
 */
@SuppressWarnings("serial")
public class Schedule  implements java.io.Serializable {


     private Long scheduleId;
     private Campaigns campaign;
     private String frequencyType;
     private String frequency;
     private Date startDate;
     private Date endDate;
     private int day;
     private int date;
     private int startMonth;
     private int endMonth;
     private int startYear;
     private int endYear;
     private Date time;

    public Schedule() {
    }

	
    public Schedule(Campaigns campiagn) {
        this.campaign = campiagn;
    }
    public Schedule(Campaigns campiagn, String frequencyType, String frequency, Date startDate, Date endDate, int day, int date, int startMonth, int endMonth, int startYear, int endYear, Date time) {
       this.campaign = campiagn;
       this.frequencyType = frequencyType;
       this.frequency = frequency;
       this.startDate = startDate;
       this.endDate = endDate;
       this.day = day;
       this.date = date;
       this.startMonth = startMonth;
       this.endMonth = endMonth;
       this.startYear = startYear;
       this.endYear = endYear;
       this.time = time;
    }
   
    public static Schedule copySchedule(Schedule srcSchedule, Schedule destSchedule){
    	destSchedule.setDate(srcSchedule.getDate());
    	destSchedule.setDay(srcSchedule.getDay());
    	destSchedule.setEndDate(srcSchedule.getEndDate());
    	destSchedule.setEndMonth(srcSchedule.getEndMonth());
    	destSchedule.setEndYear(srcSchedule.getEndYear());
    	destSchedule.setFrequency(srcSchedule.getFrequency());
    	destSchedule.setFrequencyType(srcSchedule.getFrequencyType());
    	destSchedule.setStartDate(srcSchedule.getStartDate());
    	destSchedule.setStartMonth(srcSchedule.getStartMonth());
    	destSchedule.setStartYear(srcSchedule.getStartYear());
    	destSchedule.setTime(srcSchedule.getTime());
    	return destSchedule;
    }
    public Long getScheduleId() {
        return this.scheduleId;
    }
    
    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Campaigns getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaigns campaign) {
		this.campaign = campaign;
	}


	public String getFrequencyType() {
        return this.frequencyType;
    }
    
    public void setFrequencyType(String frequencyType) {
        this.frequencyType = frequencyType;
    }
    public String getFrequency() {
        return this.frequency;
    }
    
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
    public Date getStartDate() {
        return this.startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Date getEndDate() {
        return this.endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public int getDay() {
        return this.day;
    }
    
    public void setDay(int day) {
        this.day = day;
    }
    public int getDate() {
        return this.date;
    }
    
    public void setDate(int date) {
        this.date = date;
    }
    public int getStartMonth() {
        return this.startMonth;
    }
    
    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }
    public int getEndMonth() {
        return this.endMonth;
    }
    
    public void setEndMonth(int endMonth) {
        this.endMonth = endMonth;
    }
    public int getStartYear() {
        return this.startYear;
    }
    
    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }
    public int getEndYear() {
        return this.endYear;
    }
    
    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }
    public Date getTime() {
        return this.time;
    }
    
    public void setTime(Date time) {
        this.time = time;
    }




}


