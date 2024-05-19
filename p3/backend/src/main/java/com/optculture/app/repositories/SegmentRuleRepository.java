package  com.optculture.app.repositories;
import com.optculture.app.dto.segment.SegmentRuleDTO;
import com.optculture.shared.entities.contact.SegmentRule;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SegmentRuleRepository extends JpaRepository<SegmentRule, Long> {

    List<SegmentRule> findByUserIdOrderByModifiedDateDesc(Long userId);
    @Query("SELECT new com.optculture.app.dto.segment.SegmentRuleDTO(s.segRuleId AS segRuleId, s.segRuleName AS segRuleName, s.description AS description,'--' AS type,s.totSize AS totSize,s.totEmailSize AS totEmailSize,s.totMobileSize AS totMobileSize,s.modifiedDate AS modifiedDate) FROM SegmentRule s WHERE s.segRuleId IN :segmentIdList AND s.userId = :userId")
    List<SegmentRuleDTO> findBySegmentRuleIds(List<Long> segmentIdList, Long userId);
    @Query("SELECT new com.optculture.app.dto.segment.SegmentRuleDTO(s.segRuleId AS segRuleId, s.segRuleName AS segRuleName, s.description AS description,'--' AS type,s.totSize AS totSize,s.totEmailSize AS totEmailSize,s.totMobileSize AS totMobileSize,s.modifiedDate AS modifiedDate) FROM SegmentRule s  WHERE  (s.userId = :#{#example.userId}) AND (:#{#example.segRuleName} IS NULL OR s.segRuleName LIKE %:#{#example.segRuleName}%) ")
    Page<SegmentRuleDTO> findByExample(SegmentRule example, PageRequest pg);
    
    SegmentRule findOneBySegRuleIdAndUserId(Long segRuleId, Long userId);

    List<SegmentRule> findByUserIdAndSegRuleIdIn(Long userId, List<Long> segmentIds);

}
