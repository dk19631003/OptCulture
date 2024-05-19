package org.mq.captiway.scheduler.dao;

import java.util.Collection;
import java.util.List;

import org.mq.captiway.scheduler.beans.AutoProgramComponents;
import org.mq.captiway.scheduler.beans.SMSCampaigns;

public class AutoProgramComponentsDao extends AbstractSpringDao {

	
	
	
	
	
/*	public void saveOrUpdate(AutoProgramComponents autoProgramComponents) {
        super.saveOrUpdate(autoProgramComponents);
    }

    public void delete(AutoProgramComponents autoProgramComponents) {
        super.delete(autoProgramComponents);
    }*/

    public List findAll() {
        return super.findAll(AutoProgramComponents.class);
    }
	
	
	
	
	public AutoProgramComponents getComponentByWinId(String winId, Long programId) {
		
		return (AutoProgramComponents)getHibernateTemplate().find("FROM AutoProgramComponents where autoProgram="+programId+" and componentWinId='"+winId+"'").get(0);
		
		
		
	}
	
	
	
/*	public void saveByCollection(Collection<AutoProgramComponents> autoProgComponentsList) {
		
		super.saveOrUpdateAll(autoProgComponentsList);
		
	}*/
	
	public List<AutoProgramComponents> getProgramComponents(Long programId, Long userId) {
		
		List<AutoProgramComponents> autoProgramComponentsList = null;
		String queryString ="FROM AutoProgramComponents where autoProgram="+programId;
		autoProgramComponentsList = getHibernateTemplate().find(queryString);
		
		return autoProgramComponentsList;
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
