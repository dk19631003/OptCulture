package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Vmta;
import org.mq.marketer.campaign.general.Constants;

@SuppressWarnings("unchecked")
public class VmtaDaoForDML extends AbstractSpringDaoForDML {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	public VmtaDaoForDML() {
	}

	/*public Vmta find(Long id) {
		return (Vmta) super.find(Vmta.class, id);
	}*/

	public void saveOrUpdate(Vmta vmta) {
		super.saveOrUpdate(vmta);
	}

	public void delete(Vmta vmta) {
		super.delete(vmta);
	}

	/*public List<Vmta> findAll() {
		
		return executeQuery("FROM Vmta");
		//return super.findAll(Vmta.class);
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

	public Vmta findByVmtaName(String name) {

		Vmta vmta = null;
		List vmlist = getHibernateTemplate().find("from Vmta where vmtaName ='" + name + "'");
		if (vmlist.size() > 0) {
			vmta = (Vmta) vmlist.get(0);
		}
		return vmta;
	} // findByVmtaName
	
	public Vmta findByVmtaName(String name) {

		Vmta vmta = null;
		List<Vmta> vmlist = executeQuery("from Vmta where vmtaName ='" + name + "'");
		if (vmlist !=null && vmlist.size() > 0) {
			vmta = (Vmta) vmlist.get(0);
			return vmta;
		}
		return vmta;
	} // findByVmtaName

	public Vmta getBypooledVmtas(String name) {

		logger.info("====>" + name);
		Vmta vm = new Vmta();
		List vmlist = getHibernateTemplate().find(
				"from Vmta where vmtaName ='" + name + "'");
		logger.info(vmlist.size());
		if (vmlist.size() > 0) {
			vm = (Vmta) vmlist.get(0);
		}
		
		 * List examList = getHibernateTemplate().find("from ExaminationsPojo
		 * where exam name = '"+ name + "' "); if(examList.size()>0){ exam =
		 * (ExaminationsPojo)examList.get(0); }
		 
		return vm;

	}
	
	
	*//**
	 * This method  find All Vmta By VmtaName
	 * @param vmtaName
	 * @return vmtaList
	 *//*
	public List<Vmta> findAllVmtaByVmtaName(String vmtaName) {

		List<Vmta> vmtaList =  null;
		String queryStr = "from Vmta where vmtaName ='"+vmtaName+"'";
		vmtaList = executeQuery(queryStr);
		if(vmtaList != null && vmtaList.size() > 0){
			return vmtaList;
		}
		return vmtaList;
		
	}
	
	public List<Vmta> findUniqueVmta(){
		List<Vmta> vmtaList =  null;
		String queryStr = "from Vmta group by vmtaName";
		vmtaList = executeQuery(queryStr);
		if(vmtaList != null && vmtaList.size() > 0){
			return vmtaList;
		}
		return vmtaList;
	}
	
	
	public List<Vmta> findVmtaListByAccountName(String vmtaAccountName){
		List<Vmta> vmtaList =  null;
		String queryStr = "from Vmta where accountName ='"+vmtaAccountName+"'";
		vmtaList = executeQuery(queryStr);
		if(vmtaList != null && vmtaList.size() > 0){
			return vmtaList;
		}
		return vmtaList;
		
	}*/
}	

