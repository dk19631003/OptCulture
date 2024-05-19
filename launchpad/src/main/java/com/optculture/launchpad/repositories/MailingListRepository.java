package com.optculture.launchpad.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.optculture.shared.entities.contact.MailingList;


public interface MailingListRepository extends JpaRepository<MailingList, Long> {

}
