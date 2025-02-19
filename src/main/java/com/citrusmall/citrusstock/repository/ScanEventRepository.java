package com.citrusmall.citrusstock.repository;

import com.citrusmall.citrusstock.model.ScanEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScanEventRepository extends JpaRepository<ScanEvent, Long> {
}