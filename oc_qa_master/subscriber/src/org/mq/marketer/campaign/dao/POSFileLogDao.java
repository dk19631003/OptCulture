package org.mq.marketer.campaign.dao;

import java.util.Collection;
import java.util.List;


import org.mq.marketer.campaign.beans.POSFileLogs;
import org.springframework.jdbc.core.JdbcTemplate;

public class POSFileLogDao extends AbstractSpringDao {

	
	public POSFileLogDao() { }

	private JdbcTemplate jdbcTemplate;
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public POSFileLogs find(Long id) {
        return (POSFileLogs) super.find(POSFileLogs.class, id);
    }

    /*public void saveOrUpdate(POSFileLogs posFileLogs) {
        super.saveOrUpdate(posFileLogs);
    }

    public void delete(POSFileLogs posFileLogs) {
        super.delete(posFileLogs);
    }*/
    
    public List<POSFileLogs> findAllById(Long userId) {
    	return getHibernateTemplate().find("FROM POSFileLogs where userId="+ userId + " ORDER BY fetchedTime DESC");
    }
    
    public List<POSFileLogs> findAllByIdAndSize(Long userId, int startIndex ,int size) {
    	return getHibernateTemplate().find("FROM POSFileLogs where userId="+ userId + " ORDER BY fetchedTime DESC LIMIT "+ startIndex + "," + size);
    }

  /*  public void saveByCollections(Collection<POSFileLogs> posFileLogsCollection) {
    	// TODO Auto-generated method stub
    	super.saveByCollection(posFileLogsCollection);
    }
    */
    
     

	
	
}
