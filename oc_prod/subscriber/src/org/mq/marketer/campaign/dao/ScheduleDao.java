package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.Schedule;

@SuppressWarnings({"unchecked","serial"})
public class ScheduleDao extends AbstractSpringDao {

    public ScheduleDao() {}
    
    public Schedule find(Long id) {
        return (Schedule) super.find(Schedule.class, id);
    }

   /* public void saveOrUpdate(Schedule schedule) {
        super.saveOrUpdate(schedule);
    }

    public void delete(Schedule schedule) {
        super.delete(schedule);
    }*/

	public List findAll() {
        return super.findAll(Schedule.class);
    }
	
	public Schedule getScheduleByCampaign(Campaigns campaign){
		Schedule schedule = null;
		List list = getHibernateTemplate().find("from Schedule where campaign = " + campaign.getCampaignId());
		if(list.size()>0){
			schedule = (Schedule)list.get(0);
		}
		return schedule;
	}
	
	/*public void deleteByCampaign(String campaignIds){
		getHibernateTemplate().bulkUpdate("delete from Schedule where campaign.campaignId in (" +campaignIds + ")");
	}*/

}
