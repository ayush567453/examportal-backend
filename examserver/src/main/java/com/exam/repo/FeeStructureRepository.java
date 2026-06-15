package com.exam.repo;

import com.exam.model.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {

    List<FeeStructure> findByTenantId(String tenantId);

    Optional<FeeStructure> findByTenantIdAndClassKey(String tenantId, String classKey);

    void deleteByTenantId(String tenantId);
}
