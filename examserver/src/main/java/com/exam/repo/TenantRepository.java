package com.exam.repo;

import com.exam.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, String> {
    Optional<Tenant> findByTenantCode(String tenantCode);
    List<Tenant> findByStatus(String status);
    boolean existsByTenantCode(String tenantCode);
    boolean existsByEmail(String email);
}
