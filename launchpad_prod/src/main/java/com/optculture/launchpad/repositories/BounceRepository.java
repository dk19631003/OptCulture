package com.optculture.launchpad.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.optculture.shared.entities.communication.email.Bounces;

public interface BounceRepository extends JpaRepository<Bounces,Long> {

}
