package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.mq.captiway.scheduler.beans.Campaigns;
import org.mq.captiway.scheduler.beans.Schedule;

@SuppressWarnings({"unchecked","serial"})
public class ScheduleDaoForDML extends AbstractSpringDaoForDML {

    public ScheduleDaoForDML() {}
    
   /* public Schedule find(Long id) {
        return (Schedule) super.find(Schedule.class, id);
    }*/

    public void saveOrUpdate(Schedule schedule) {
        super.saveOrUpdate(schedule);
    }

    public void delete(Schedule schedule) {
        super.delete(schedule);
    }

	/*public List findAll() {
        return super.findAll(Schedule.class);
    }*/
	
	/*public Schedule getScheduleByCampaign(Campaigns campaign){
		Schedule schedule = null;
		List list = getHibernateTemplate().find("from Schedule where campaign = " + campaign.getCampaignId());
		if(list.size()>0){
			schedule = (Schedule)list.get(0);
		}
		return schedule;
	}*/

}

