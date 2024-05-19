package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.mq.marketer.campaign.beans.ExportFileDetails;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class ExportFileDetailsDao extends AbstractSpringDao {
	
public ExportFileDetailsDao() {}
	

	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
    public ExportFileDetails find(Long id) {
        return (ExportFileDetails) super.find(ExportFileDetails.class, id);
    }
/*
    public void saveOrUpdate(ExportFileDetails ExportFileDetails) {
        super.saveOrUpdate(ExportFileDetails);
    }

    public void delete(ExportFileDetails ExportFileDetails) {
        super.delete(ExportFileDetails);
    }*/

    public List<ExportFileDetails> findAll() {
        return super.findAll(ExportFileDetails.class);
    }
    
    public List<ExportFileDetails> findAllByStatus(){
    	 List<ExportFileDetails> list = null;
    	list = getHibernateTemplate().find("FROM ExportFileDetails WHERE status ='"+Constants.EXPORT_FILE_COMPLETED+"'");
    	return list;
    	
    }
    
    public int findAllCompletedByUserId(Long userId){
   	
	   	int count = 0;
	 	 String qry = " FROM ExportFileDetails WHERE  userId="+userId+" AND  "
	 	 		+ "status ='"+Constants.EXPORT_FILE_COMPLETED+"'";
	 	List list = getHibernateTemplate().find(qry);
	 	if(list != null && list.size() > 0 ) count = list.size();
	 	
	 	return count;
   	
   }
/*    public int updateFileStatusByUserId(Long userId,String filePath, String fileType){
      	 int count = 0;
      	 filePath = StringEscapeUtils.escapeSql(filePath);
      	 String qry = " UPDATE ExportFileDetails set status ='"+Constants.EXPORT_FILE_DELETED+"' WHERE "
       			+ " userId="+userId+" AND fileType='"+fileType+"' AND filePath='"+filePath+"'";
      	count = executeUpdate(qry);
      	
      	return count;
      	
    } 
    */
    public List<ExportFileDetails> findAllByStatusAndUserId(String status, Long userId,String fileType){
    	 try {
			List<ExportFileDetails> list = null;
			 String qry = "FROM ExportFileDetails WHERE user_id="+userId+" "
					+ "  AND fileType ='"+fileType+"' AND status  in ("+status+")";
			 logger.info("qry is  :::"+qry);
			list = getHibernateTemplate().find(qry);
			return list;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
}
