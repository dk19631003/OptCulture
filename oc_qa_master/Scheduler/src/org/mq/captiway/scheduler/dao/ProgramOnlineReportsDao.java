package org.mq.captiway.scheduler.dao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.AutoProgramComponents;
import org.mq.captiway.scheduler.beans.ProgramOnlineReports;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;


@SuppressWarnings({ "unchecked", "serial","unused"})
public class ProgramOnlineReportsDao extends AbstractSpringDao{

	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	
	 public ProgramOnlineReportsDao() {}
	 
	 
	 	private JdbcTemplate jdbcTemplate;
	 	
	 	
	 	
	 	
	    public JdbcTemplate getJdbcTemplate() {
			return jdbcTemplate;
		}

		public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}


		private SessionFactory sessionFactory;

	    public ProgramOnlineReports find(Long id) {
	        return (ProgramOnlineReports) super.find(ProgramOnlineReports.class, id);
	    }

	   /* public void saveOrUpdate(ProgramOnlineReports programOnlineReports) {
	        super.saveOrUpdate(programOnlineReports);
	    }

	    public void delete(ProgramOnlineReports programOnlineReports) {
	        super.delete(programOnlineReports);
	    }*/

	    public List findAll() {
	        return super.findAll(ProgramOnlineReports.class);
	    }
	    
	   /* public void saveByCollection(List<ProgramOnlineReports> progonlineReplist) {
	    	
	    	 super.saveOrUpdateAll(progonlineReplist);
	    }
	*/
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    
	    public long getCountToCalPercentage(AutoProgramComponents tempComp, Calendar consDate, String switchWinId) {
	    	String dateConsSubQry = null;
	    	
	    	if(consDate != null) {
	    		//means count should be taken for over all contacts and the consideration date will be specified by consDate
	    		Date countConsDate = consDate.getTime();
	    		 String CountConsFormattedDate = format.format(countConsDate);
	    		   
	    		
	    		
	    		
	    		dateConsSubQry = " AND activity_date >='"+CountConsFormattedDate+"'";
	    		
	    	}
	    	//*********this is absolutely wrong because need to consider the exact window id along with the pick**********
	    	String qry = "SELECT COUNT(contact_id) FROM program_online_reports WHERE program_id="+
	    					tempComp.getAutoProgram().getProgramId()+" AND component_id="+tempComp.getCompId()+dateConsSubQry+"" +
							" AND component_win_id='"+switchWinId+"'";
	    	
	    	
	    	long contactCount =  jdbcTemplate.queryForLong(qry);
	    	if(logger.isDebugEnabled()) logger.debug("getCountToCalPercentage(AutoProgramComponents tempComp, Calendar consDate, String switchWinId)========>"+contactCount);
	    	return contactCount;
	    	
	    }//getCountToCalPercentage
	    
	    public long getCountToCalPercentage(AutoProgramComponents tempComp, Calendar consDate) {
	    	String dateConsSubQry = null;
	    	
	    	if(consDate != null) {
	    		//means count should be taken for over all contacts and the consideration date will be specified by consDate
	    		Date countConsDate = consDate.getTime();
	    		 String CountConsFormattedDate = format.format(countConsDate);
	    		   
	    		
	    		
	    		
	    		dateConsSubQry = " AND activity_date >='"+CountConsFormattedDate+"'";
	    		
	    	}
	    	//*********this is absolutely wrong because need to consider the exact window id along with the pick**********
	    	String qry = "SELECT COUNT(contact_id) FROM program_online_reports WHERE program_id="+
	    					tempComp.getAutoProgram().getProgramId()+" AND component_id="+tempComp.getCompId()+dateConsSubQry;
							
	    	
	    	
	    	long contactCount =  jdbcTemplate.queryForLong(qry);
	    	if(logger.isDebugEnabled()) logger.debug("getCountToCalPercentage(AutoProgramComponents tempComp, Calendar consDate)==========?"+contactCount);
	    	return contactCount;
	    	
	    }//getCountToCalPercentage
	    
	    
	    
	    
	    
	    
}
