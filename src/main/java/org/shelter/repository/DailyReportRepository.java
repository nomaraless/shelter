package org.shelter.repository;

import org.shelter.model.DailyReport;
import org.shelter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {
    List<DailyReport> findByProcessedFalse();
    Optional<DailyReport> findTopByUserOrderByCreatedAtDesc(User user);

}

