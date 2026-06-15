package com.exam.service;

import com.exam.model.Tenant;
import java.util.List;
import java.util.Optional;

public interface TenantService {
    Tenant createTenant(Tenant tenant, String adminPassword) throws Exception;
    Tenant updateTenant(String tenantId, Tenant tenant);
    void deleteTenant(String tenantId);
    void deactivateTenant(String tenantId);
    void activateTenant(String tenantId);
    List<Tenant> getAllTenants();
    Optional<Tenant> getTenantById(String tenantId);
    long countActive();
    long countAll();
}
