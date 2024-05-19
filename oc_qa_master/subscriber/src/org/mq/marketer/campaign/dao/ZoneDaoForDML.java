package org.mq.marketer.campaign.dao;

import java.io.Serializable;

import org.mq.marketer.campaign.beans.OrganizationZone;
import org.springframework.jdbc.core.JdbcTemplate;

public class ZoneDaoForDML  extends AbstractSpringDaoForDML implements Serializable {	
	private JdbcTemplate jdbcTemplate;	
public JdbcTemplate getJdbcTemplate() {		
	return jdbcTemplate;
}	
public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {		
	this.jdbcTemplate = jdbcTemplate;
}

public void saveOrUpdate(OrganizationZone zone){
    super.saveOrUpdate(zone);
}

public void deleteZone(OrganizationZone zone){
	super.delete(zone);
}
public void deleteZoneStoreBy(long zoneId){
	String query="DELETE FROM zone_store WHERE zone_id="+zoneId;
	jdbcTemplate.execute(query);
}
public void deleteStore(long storeId){
	String query="DELETE FROM zone_store WHERE store_id="+storeId;
	jdbcTemplate.execute(query);
}
}