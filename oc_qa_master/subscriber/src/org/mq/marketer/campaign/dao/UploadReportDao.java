package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.UploadReport;

@SuppressWarnings({ "unchecked", "serial","unused"})
public class UploadReportDao extends AbstractSpringDao {

    public UploadReportDao() {}
	
    public UploadReport find(Long id) {
        return (UploadReport) super.find(UploadReport.class, id);
    }

   /* public void saveOrUpdate(UploadReport uploadReport) {
        super.saveOrUpdate(uploadReport);
    }

    public void delete(UploadReport uploadReport) {
        super.delete(uploadReport);
    }*/

    public List findAll() {
        return super.findAll(UploadReport.class);
    }
}

