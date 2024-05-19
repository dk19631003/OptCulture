package org.mq.captiway.scheduler.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.mq.captiway.scheduler.beans.Bounces;
import org.mq.captiway.scheduler.beans.EmailQueue;
import org.mq.captiway.scheduler.beans.ErrorLog;
import org.mq.captiway.scheduler.beans.Users;
import org.springframework.jdbc.core.JdbcTemplate;

@SuppressWarnings({ "unchecked", "serial","unused"})
public class BouncesDao extends AbstractSpringDao {

    public BouncesDao() {}
	
    private JdbcTemplate jdbcTemplate;
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public Bounces find(Long id) {
        return (Bounces) super.find(Bounces.class, id);
    }

   /* public void saveOrUpdate(Bounces bounce) {
        super.saveOrUpdate(bounce);
    }

    public void delete(Bounces bounce) {
        super.delete(bounce);
    }*/

    public List<Bounces> findAll() {
        return super.findAll(Bounces.class);
    }

   /* public void saveByCollection(Collection<Bounces> bounceCollection){
    	super.saveOrUpdateAll(bounceCollection);
    }*/

}

