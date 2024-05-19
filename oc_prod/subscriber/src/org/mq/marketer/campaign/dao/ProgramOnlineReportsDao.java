package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.AutoProgramComponents;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ProgramOnlineReports;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class ProgramOnlineReportsDao extends AbstractSpringDao{
	
	
	
	 public ProgramOnlineReportsDao() {}
	    private SessionFactory sessionFactory;
	    
	    private JdbcTemplate jdbcTemplate;

	    public JdbcTemplate getJdbcTemplate() {
			return jdbcTemplate;
		}

		public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}

		public ProgramOnlineReports find(Long id) {
	        return (ProgramOnlineReports) super.find(ProgramOnlineReports.class, id);
	    }

	    /*public void saveOrUpdate(ProgramOnlineReports programOnlineReports) {
	        super.saveOrUpdate(programOnlineReports);
	    }

	    public void delete(ProgramOnlineReports programOnlineReports) {
	        super.delete(programOnlineReports);
	    }*/

	    public List findAll() {
	        return super.findAll(ProgramOnlineReports.class);
	    }
	    
	   /* public void saveByCollection(List<ProgramOnlineReports> progonlineReplist) {
	    	
	    	 super.saveByCollection(progonlineReplist);
	    }
	*/
	    
	    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    
	    public List<Contacts> getPassedThroughContactsList(AutoProgramComponents currComp,Long offSet,
	    		Calendar createdDate, Calendar fromDate, Calendar toDate, String fetchOption) {
	    	
	    	List<Contacts> passedThroughContactsList = null;
	    	String timeDiff = null;
	    	
	    	if(offSet != null) {
				if(offSet > 1440) {
					
					offSet = offSet/1440;
					timeDiff = " AND DATEDIFF(now(),activity_date) <= "+offSet;
					
					
				}//if
				else if(offSet >= 0 && offSet <= 1440) {
					offSet = offSet/60;
					timeDiff = " AND now() > activity_date AND HOUR( TIMEDIFF(now(),activity_date) ) <= "+offSet;
					
				}//else if
			
			}//if
			
			if(createdDate != null) {
				
				Date activityDate = createdDate.getTime();
				String formatDate = format.format(activityDate);
				
				
				
				timeDiff = " AND activity_date BETWEEN '"+formatDate+"' AND now() ";
				
				
				
			}//if
			
			
			
			if(fromDate != null && toDate != null) {
				
				
				Date frmDate = fromDate.getTime();
				String frmFormattedDate = format.format(frmDate);
				
				Date targetDate = toDate.getTime();
				String toFormattedDate = format.format(targetDate);
				
				timeDiff = " AND activity_date BETWEEN '"+frmFormattedDate+"' AND '"+toFormattedDate+"' ";
				
				
			}
	    	/*
	    	 * select comp_contacts_id from program_online_reports where program_id=15 And component_win_id='ACTIVITY_SEND_SMS-23w'
	    	 */
			
			
			
			String qry  = " SELECT "+fetchOption+" c.email_id,c.first_name,c.mobile_phone,pos.activity_date FROM contacts c, program_online_reports pos where " +
						  "c.cid = pos.contact_id AND pos.program_id ="+currComp.getAutoProgram().getProgramId()+
						  " AND pos.component_id="+currComp.getCompId()+timeDiff; 
			
			passedThroughContactsList = jdbcTemplate.query(qry, new RowMapper() {
		
		        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		    		
		            Contacts contact = new Contacts();
		            contact.setEmailId(rs.getString("email_id"));
		            contact.setFirstName(rs.getString("first_name"));
//		            contact.setPhone(rs.getLong("phone"));
		            contact.setMobilePhone(rs.getString("mobile_phone"));
		            contact.setActivityDate(rs.getString("activity_date"));
		            return contact;
		        }
		    });
			
	    	/*String subQuery = "SELECT DISTINCT comp_contacts_id FROM program_online_reports where program_id="+currComp.getAutoProgram().getProgramId()+
    						  " AND component_id="+ currComp.getCompId()+timeDiff;
	    	
	    	String ccid = getRequiredIdsStr(subQuery);
	    	
	    	
	    	if(ccid.length()>0) {
	    		
	    		subQuery = "SELECT contact_id FROM components_contacts where cc_id in("+ccid+")";
	    		
	    		ccid = getRequiredIdsStr(subQuery);
	    		if(ccid.length() > 0) {
	    			
	    			passedThroughContactsList = getHibernateTemplate().find("FROM Contacts WHERE cid in("+ccid+")");
	    			
	    		}
	    		
	    		
	    	}//if
	    	if(passedThroughContactsList != null && passedThroughContactsList.size() > 0) {
	    		
	    		
	    		
	    		
	    		
	    		
	    		
	    		
	    		
	    	}//if
*/	    	return passedThroughContactsList;
	    	
	    	
	    	
	    }
	    
	    
	    
	    
	    
	    public String getRequiredIdsStr(String subQuery) {
			
			String qry = subQuery;
			String requiredIds = "";
			List<Long> contactIdList = jdbcTemplate.queryForList(subQuery, Long.class);
			
			
			
			if(contactIdList.isEmpty()) {
				
				logger.debug("no contacts found for this query"+subQuery);
				return "";
			}
			
			for(Long id : contactIdList) {
				
				if(requiredIds.length() > 0 ) requiredIds += ",";
				requiredIds += id;
			}
			return requiredIds;
		}
	    
	    
	    
	    
	    public long getPassedThroughCount(AutoProgramComponents currComp,Long offSet,
	    		Calendar createdDate, Calendar fromDate, Calendar toDate, String fetchOption) {
	    	try {
				long passedThrough = 0;
				
				logger.info("----just entered----"+offSet+"   "+createdDate);
				
				String timeDiff = null;
				
				if(offSet != null) {
					if(offSet > 1440) {
						
						offSet = offSet/1440;
						timeDiff = " AND DATEDIFF(now(),activity_date) <= "+offSet;
						
						
					}//if
					else if(offSet >= 0 && offSet <= 1440) {
						offSet = offSet/60;
						timeDiff = " AND now() > activity_date AND HOUR( TIMEDIFF(now(),activity_date) ) <= "+offSet;
						
					}//else if
				
				}//if
				
				if(createdDate != null) {
					
					Date activityDate = createdDate.getTime();
					String formatDate = format.format(activityDate);
					
					
					
					timeDiff = " AND activity_date BETWEEN '"+formatDate+"' AND now() ";
					
					
					
				}//if
				
				
				
				if(fromDate != null && toDate != null) {
					
					
					Date frmDate = fromDate.getTime();
					String frmFormattedDate = format.format(frmDate);
					
					Date targetDate = toDate.getTime();
					String toFormattedDate = format.format(targetDate);
					
					timeDiff = " AND activity_date BETWEEN '"+frmFormattedDate+"' AND '"+toFormattedDate+"' ";
					
					
				}
				String qry = " SELECT "+fetchOption+" FROM program_online_reports " +
							 " WHERE program_id="+currComp.getAutoProgram().getProgramId() +" AND component_id="+currComp.getCompId()+timeDiff;
	    		
				
				/*String hql =  " SELECT COUNT(p.progRepId) FROM ProgramOnlineReports p" +
							  " WHERE p.programId="+currComp.getAutoProgram().getProgramId() +" AND p.componentId="+currComp.getCompId()+" AND " +
					  		  " (to_date('"+formatDate+"','YYYY/MM/DD HH24:MI:SS')-to_date(p.activity_date,'YYYY/MM/DD HH24:MI:SS') <="+offSet;
	    		*/	
	    			logger.info("SQL query is=====>"+qry);
	    			
	    			return getJdbcTemplate().queryForLong(qry);
				//select trunc (to_date('2005/04/18 11:05:00','YYYY/MM/DD HH24:MI:SS')-to_date('2005/04/15 10:15:32','YYYY/MM/DD HH24:MI:SS')) as days from dual;
				
			} catch (Exception e) {
				return 0;
				// TODO: handle exception
			}
	    	
	    }
	    
	    public List<Contacts>  getCurrentContactsListForTargetTimer(AutoProgramComponents currComp, Long offSet, String inputCompIdStr,
	    		Calendar createdDate, Calendar fromDate, Calendar toDate, String fetchOption) {
	    
	    	List<Contacts> currentContactList = null;
	    	String timeDiff = null;
			
			if(offSet != null) {
			
				if(offSet > 1440) {
					
					offSet = offSet/1440;
					timeDiff = " AND DATEDIFF(now(),activity_date) <= "+offSet;
					
					
				}else if(offSet >= 0 && offSet <= 1440) {
					offSet = offSet/60;
					timeDiff = " AND now() > activity_date AND HOUR( TIMEDIFF(now(),activity_date) ) <= "+offSet;
					
				}
			
			}
			
			if(createdDate != null) {
				Date activityDate = createdDate.getTime();
				String formatDate = format.format(activityDate);
				
				
				
				timeDiff = " AND activity_date BETWEEN '"+formatDate+"' AND now() ";
				
				
			}
			
			
			if(fromDate != null && toDate != null) {
				
				
				Date frmDate = fromDate.getTime();
				String frmFormattedDate = format.format(frmDate);
				
				Date targetDate = toDate.getTime();
				String toFormattedDate = format.format(targetDate);
				
				timeDiff = " AND activity_date BETWEEN '"+frmFormattedDate+"' AND '"+toFormattedDate+"' ";
				
				
			}
	    	
			String qry1 = "SELECT "+fetchOption+" contact_id FROM program_online_reports WHERE program_id="
							+currComp.getAutoProgram().getProgramId()+" and component_win_id IN("+inputCompIdStr+")"+timeDiff;
	
	    	
	    	
			String ccid = getRequiredIdsStr(qry1);
			
			if(ccid.length() > 0) {
				String qry = "SELECT "+fetchOption+" c.email_id,c.first_name,c.mobile_phone,now() From contacts c where c.cid in("+ccid+")";
				currentContactList = jdbcTemplate.query(qry, new RowMapper() {
					
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			    		
			            Contacts contact = new Contacts();
			            contact.setEmailId(rs.getString("email_id"));
			            contact.setFirstName(rs.getString("first_name"));
//			            contact.setPhone(rs.getLong("phone"));
			            contact.setMobilePhone(rs.getString("mobile_phone"));
			            contact.setActivityDate(rs.getString("now()"));
			            return contact;
			        }
			    });
				
					
					
				
				
				
				
			}//if
		    		
		    		
		    	
		    	
	    	return currentContactList;
	    
	    }
	    
	    
	    
	    public List<Contacts> getCurrentContactsList(AutoProgramComponents currComp, Long offSet, String inputCompIdStr,
	    		Calendar createdDate, Calendar fromDate, Calendar toDate, String fetchOption) {
	    	
	    	List<Contacts> currentContactList = null;
	    	String timeDiff = null;
			
			if(offSet != null) {
			
				if(offSet > 1440) {
					
					offSet = offSet/1440;
					timeDiff = " AND DATEDIFF(now(),activity_date) <= "+offSet;
					
					
				}else if(offSet >= 0 && offSet <= 1440) {
					offSet = offSet/60;
					timeDiff = " AND now() > activity_date AND HOUR( TIMEDIFF(now(),activity_date) ) <= "+offSet;
					
				}
			
			}
			
			if(createdDate != null) {
				Date activityDate = createdDate.getTime();
				String formatDate = format.format(activityDate);
				
				
				
				timeDiff = " AND activity_date BETWEEN '"+formatDate+"' AND now() ";
				
				
			}
			
			
			if(fromDate != null && toDate != null) {
				
				
				Date frmDate = fromDate.getTime();
				String frmFormattedDate = format.format(frmDate);
				
				Date targetDate = toDate.getTime();
				String toFormattedDate = format.format(targetDate);
				
				timeDiff = " AND activity_date BETWEEN '"+frmFormattedDate+"' AND '"+toFormattedDate+"' ";
				
				
			}
	    		String retCcid = null;
	    		String qry1 = "";
				
				
			
	    		/*String qry2 = "SELECT DISTINCT comp_contacts_id FROM program_online_reports WHERE program_id="+currComp.getAutoProgram().getProgramId()+
    						  " AND component_id="+currComp.getCompId()+timeDiff;
	    		
	    		
	    		String currCcid = getRequiredIdsStr(qry2);
	    			
	    			if(currCcid.length() > 0) {
	    				
	    				
	    				qry1 = "SELECT DISTINCT comp_contacts_id FROM program_online_reports WHERE program_id="
	    						+currComp.getAutoProgram().getProgramId()+" and component_win_id IN("+inputCompIdStr+")"+timeDiff+" AND comp_contacts_id NOT IN("+currCcid+")";
	    				
	    				
	    				
	    			}else {
	    				
	    				qry1 = "SELECT DISTINCT comp_contacts_id FROM program_online_reports WHERE program_id="
	    						+currComp.getAutoProgram().getProgramId()+" and component_win_id IN("+inputCompIdStr+")"+timeDiff;
	    				
	    				
	    				
	    			}
	    			
	    			String ccid = getRequiredIdsStr(qry1);
	    			
	    			if(ccid.length() > 0) {
	    				
	    				String cid = getRequiredIdsStr("SELECT contact_id FROM components_contacts WHERE cc_id in("+ccid+")");
	    				
	    				if(cid.length() > 0 ) {
	    					
	    					currentContactList = getHibernateTemplate().find("FROM Contacts Where cid in("+cid+")");
	    				}//if
	    				
	    				
	    				
	    			}//if
*/	    			
	    		
	    		
	    		String qry2 = "SELECT "+fetchOption+" contact_id FROM program_online_reports WHERE program_id="+currComp.getAutoProgram().getProgramId()+
				  				" AND component_id="+currComp.getCompId()+timeDiff;
	
	
	String currCcid = getRequiredIdsStr(qry2);
		
		if(currCcid.length() > 0) {
			
			
			qry1 = "SELECT "+fetchOption+" contact_id FROM program_online_reports WHERE program_id="
					+currComp.getAutoProgram().getProgramId()+" and component_win_id IN("+inputCompIdStr+")"+timeDiff+" AND contact_id NOT IN("+currCcid+")";
			
			
			
		}else {
			
			qry1 = "SELECT "+fetchOption+" contact_id FROM program_online_reports WHERE program_id="
					+currComp.getAutoProgram().getProgramId()+" and component_win_id IN("+inputCompIdStr+")"+timeDiff;
			
			
			
		}
		
		String ccid = getRequiredIdsStr(qry1);
		
		if(ccid.length() > 0) {
			String qry = "SELECT "+fetchOption+" c.email_id,c.first_name,c.mobile_phone,now() From contacts c where c.cid in("+ccid+")";
			currentContactList = jdbcTemplate.query(qry, new RowMapper() {
				
		        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		    		
		            Contacts contact = new Contacts();
		            contact.setEmailId(rs.getString("email_id"));
		            contact.setFirstName(rs.getString("first_name"));
//		            contact.setPhone(rs.getLong("phone"));
		            contact.setMobilePhone(rs.getString("mobile_phone"));
		            contact.setActivityDate(rs.getString("now()"));
		            return contact;
		        }
		    });
			
				
				
			
			
			
			
		}//if
	    		
	    		
	    	
	    	
	    	return currentContactList;
	    	
	    	
	    }
	    
	   /* public String removePassedCcids(String ccid, String currCcid) {
	    	
	    	String[] tempArr = null;
	    	if(currCcid.contains(",")) {
	    		
	    		
	    		tempArr = currCcid.split(",");
	    		for (String tempCcid : tempArr) {
					
	    			if(ccid.contains(tempCcid)) {
	    				
	    				ccid = ccid.replace(tempCcid+",", "");
	    				
	    			}
	    			
				}//for
	    		
	    		
	    	}
	    	
	    	else{
	    		
	    		if(ccid.contains(currCcid)){
	    			
	    			ccid = ccid.replace(currCcid, "");
	    		}
	    		
	    		
	    	}
	    	
	    	return ccid;
	    	
	    	
	    	
	    	
	    	
	    }*/
	    public long getCurrentCountForTargetTimer(AutoProgramComponents currComp, Long offSet, String inputCompIdStr,
	    		Calendar createdDate, Calendar fromDate, Calendar toDate, String fetchOption) {
	    	
	    	long current = 0;
	    	
	    	logger.info(">>>>>>>>>>>>>>>>>>> "+inputCompIdStr);
			
			String timeDiff = null;
			
			if(offSet != null) {
			
				if(offSet > 1440) {
					
					offSet = offSet/1440;
					timeDiff = " AND DATEDIFF(now(),activity_date) <= "+offSet;
					
					
				}else if(offSet >= 0 && offSet <= 1440) {
					offSet = offSet/60;
					timeDiff = " AND now() > activity_date AND HOUR( TIMEDIFF(now(),activity_date) ) <= "+offSet;
					
				}
			
			}
			
			if(createdDate != null) {
				Date activityDate = createdDate.getTime();
				String formatDate = format.format(activityDate);
				
				
				
				timeDiff = " AND activity_date BETWEEN '"+formatDate+"' AND now() ";
				
				
			}
			
			
			if(fromDate != null && toDate != null) {
				
				
				Date frmDate = fromDate.getTime();
				String frmFormattedDate = format.format(frmDate);
				
				Date targetDate = toDate.getTime();
				String toFormattedDate = format.format(targetDate);
				
				timeDiff = " AND activity_date BETWEEN '"+frmFormattedDate+"' AND '"+toFormattedDate+"' ";
				
				
			}
	    	
			String qry1 = " SELECT " +fetchOption+" FROM program_online_reports " +
			  " WHERE program_id="+currComp.getAutoProgram().getProgramId() +" AND component_win_id IN("+inputCompIdStr+")"+timeDiff;

			long inputCurrent = getJdbcTemplate().queryForLong(qry1);
	    	
	    	return inputCurrent;
	    	
	    }
	    
	    
	    public long getCurrentCount(AutoProgramComponents currComp, Long offSet, String inputCompIdStr,
	    		Calendar createdDate, Calendar fromDate, Calendar toDate, String fetchOption) {
	    	
	    	long current = 0;
	    	
	    	logger.info("88888888888888"+inputCompIdStr);
			
			String timeDiff = null;
			
			if(offSet != null) {
			
				if(offSet > 1440) {
					
					offSet = offSet/1440;
					timeDiff = " AND DATEDIFF(now(),activity_date) <= "+offSet;
					
					
				}else if(offSet >= 0 && offSet <= 1440) {
					offSet = offSet/60;
					timeDiff = " AND now() > activity_date AND HOUR( TIMEDIFF(now(),activity_date) ) <= "+offSet;
					
				}
			
			}
			
			if(createdDate != null) {
				Date activityDate = createdDate.getTime();
				String formatDate = format.format(activityDate);
				
				
				
				timeDiff = " AND activity_date BETWEEN '"+formatDate+"' AND now() ";
				
				
			}
			
			
			if(fromDate != null && toDate != null) {
				
				
				Date frmDate = fromDate.getTime();
				String frmFormattedDate = format.format(frmDate);
				
				Date targetDate = toDate.getTime();
				String toFormattedDate = format.format(targetDate);
				
				timeDiff = " AND activity_date BETWEEN '"+frmFormattedDate+"' AND '"+toFormattedDate+"' ";
				
				
			}
	    	
	    	String qry1 = " SELECT " +fetchOption+" FROM program_online_reports " +
		 				  " WHERE program_id="+currComp.getAutoProgram().getProgramId() +" AND component_win_id IN("+inputCompIdStr+")"+timeDiff;
	    	
	    	String qry2 = "";
	    	long inputCurrent = getJdbcTemplate().queryForLong(qry1);
	    	if(inputCurrent > 0) {
	    		
	    	qry2 = " SELECT "+fetchOption+" FROM program_online_reports " +
		 			" WHERE program_id="+currComp.getAutoProgram().getProgramId() +" AND component_id="+currComp.getCompId()+timeDiff;
    		
	    		long currCompCurrent = getJdbcTemplate().queryForLong(qry2);
	    		
	    		current = (inputCurrent-currCompCurrent);
	    		
	    		
	    	}
	    	
	    	logger.info("qry1 and qry2 are=====>" + qry1+"    "+qry2);
	    	return current;
	    	
	    }

}
