package org.mq.loyality.common.service;


import java.util.Set;

import org.mq.loyality.utils.City;
import org.mq.loyality.utils.State;
import org.mq.loyality.utils.Zip;


public interface GeoService {

	public Set<State> findAllStates();

	public Set<City> findCitiesForState(String state);

	public Set<Zip> findzipForCIty(String city);
	

}
