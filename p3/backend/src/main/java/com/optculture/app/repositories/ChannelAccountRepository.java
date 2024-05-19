package com.optculture.app.repositories;

import com.optculture.shared.entities.communication.ChannelAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelAccountRepository extends JpaRepository<ChannelAccount,Long> {

}
