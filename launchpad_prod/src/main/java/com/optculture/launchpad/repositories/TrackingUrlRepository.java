package com.optculture.launchpad.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.optculture.shared.entities.communication.TrackingUrl;


public interface TrackingUrlRepository extends JpaRepository<TrackingUrl, Long> {

}
