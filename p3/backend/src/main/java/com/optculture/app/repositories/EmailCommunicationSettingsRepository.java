package com.optculture.app.repositories;

import com.optculture.shared.entities.communication.email.EmailCommunicationSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatusCode;

import java.util.Optional;

public interface EmailCommunicationSettingsRepository extends JpaRepository<EmailCommunicationSettings,Long> {
    Optional<EmailCommunicationSettings> findByUserId(Long userId);
}
