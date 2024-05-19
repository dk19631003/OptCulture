package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.Vmta;
import org.mq.captiway.scheduler.utility.Constants;

@SuppressWarnings("unchecked")
public class VmtaDao extends AbstractSpringDao {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	public VmtaDao() {
	}

	public Vmta find(Long id) {
		return (Vmta) super.find(Vmta.class, id);
	}

	/*public void saveOrUpdate(Vmta vmta) {
		super.saveOrUpdate(vmta);
	}

	public void delete(Vmta vmta) {
		super.delete(vmta);
	}*/

	public List<Vmta> findAll() {
		return super.findAll(Vmta.class);
	}

	public List<Vmta> getAllVmtas() {
		return super.find("from Vmta order by id");
	}
	
	public List<Vmta> getOnlyVmtas() {
		return super.find("from Vmta where pooledVmtas is null  order by id");
	}
	
	public List<Vmta> getOnlyPooledVmtas() {
		return super.find("from Vmta where pooledVmtas is not null order by id");
	}

	public List<Vmta> getVmtasByPooledVmtas(String ids) {
		return super.find("from Vmta where id in ( " + ids + " ) ");
	}
	public Vmta getGenericVmta(String vmtaName, String accountName) {
		// in ses - vmtaName : AmazonSES accountName : TEST SES
		Vmta vmtaObj = null;
		List vmta = getHibernateTemplate().find("from Vmta where vmtaName = '"+vmtaName+"' AND accountName ='"+accountName+"'");
		
		if(vmta.size() > 0)
			vmtaObj = (Vmta) vmta.get(0);
		
		return vmtaObj;
	}
	/*
	 * could use this and get the direct vmta for all other then sendgrid, if the vmta is ses ( change the assigning of the name , pwd of ses
	 * to sendgrid.)
	 */
	public Vmta findByVmtaName(String name) {

		Vmta vmta = null;
		List vmlist = getHibernateTemplate().find("from Vmta where vmtaName ='" + name + "'");
		if (vmlist.size() > 0) {
			vmta = (Vmta) vmlist.get(0);
		}
		return vmta;
	} // findByVmtaName

	public Vmta getBypooledVmtas(String name) {

		if(logger.isDebugEnabled()) logger.debug("====>" + name);
		Vmta vm = new Vmta();
		List vmlist = getHibernateTemplate().find(
				"from Vmta where vmtaName ='" + name + "'");
		if(logger.isDebugEnabled()) logger.debug(vmlist.size());
		if (vmlist.size() > 0) {
			vm = (Vmta) vmlist.get(0);
		}
		/*
		 * List examList = getHibernateTemplate().find("from ExaminationsPojo
		 * where exam name = '"+ name + "' "); if(examList.size()>0){ exam =
		 * (ExaminationsPojo)examList.get(0); }
		 */
		return vm;

	}
}	

