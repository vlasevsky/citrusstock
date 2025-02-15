package com.citrusmall.citrusstock.repository;

import com.citrusmall.citrusstock.model.Box;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoxRepository extends JpaRepository<Box, Long> {

    List<Box> findByProductBatch_Id(Long productBatchId);
}
