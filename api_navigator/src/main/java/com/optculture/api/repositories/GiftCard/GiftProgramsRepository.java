package com.optculture.api.repositories.GiftCard;

import com.optculture.shared.entities.GiftCard.GiftPrograms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiftProgramsRepository extends JpaRepository<GiftPrograms,Long> {
    GiftPrograms findByGiftProgramId(Long programID);
}
