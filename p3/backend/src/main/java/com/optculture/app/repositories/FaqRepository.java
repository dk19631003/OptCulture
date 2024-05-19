package com.optculture.app.repositories;

import com.optculture.shared.entities.config.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaqRepository extends JpaRepository<Faq,Long> {
    Faq findOneByUserId(Long userId);
}
