package com.learning.clearing.domain.repository;

import com.learning.clearing.domain.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByEntityIdOrderByTimestampDesc(String entityId);
}
