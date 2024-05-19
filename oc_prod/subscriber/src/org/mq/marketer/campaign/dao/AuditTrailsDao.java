package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.AuditTrails;

@SuppressWarnings({ "unchecked", "serial","unused"})
public class AuditTrailsDao extends AbstractSpringDao {

    public AuditTrailsDao() {}
	
    public AuditTrails find(Long id) {
        return (AuditTrails) super.find(AuditTrails.class, id);
    }

    /*public void saveOrUpdate(AuditTrails auditTrails) {
        super.saveOrUpdate(auditTrails);
    }

    public void delete(AuditTrails auditTrails) {
        super.delete(auditTrails);
    }*/

    public List<AuditTrails> findAll() {
        return super.findAll(AuditTrails.class);
    }
}

