package com.optculture.app.repositories;

import com.optculture.shared.entities.communication.UrlShortCodeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlShortCodeMappingRepository extends JpaRepository<UrlShortCodeMapping,Long> {

    Optional<UrlShortCodeMapping> findByUrlContentAndUserId(String enteredURL, Long userId);
}
