package com.learning.clearing.domain.repository;

import com.learning.clearing.domain.model.OutboxDeadLetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxDeadLetterRepository extends JpaRepository<OutboxDeadLetter, Long> {
}
