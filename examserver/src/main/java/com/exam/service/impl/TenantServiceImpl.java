package com.exam.service.impl;

import com.exam.model.*;
import com.exam.repo.RoleRepository;
import com.exam.repo.TenantRepository;
import com.exam.repo.UserRepository;
import com.exam.repo.UserRoleRepository;
import com.exam.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Tenant createTenant(Tenant tenant, String adminPassword) throws Exception {
        if (tenantRepository.existsByTenantCode(tenant.getTenantCode())) {
            throw new Exception("School code already exists: " + tenant.getTenantCode());
        }
        if (tenantRepository.existsByEmail(tenant.getEmail())) {
            throw new Exception("A school with this email already exists: " + tenant.getEmail());
        }
        if (userRepository.findByUsername(tenant.getAdminUsername()) != null) {
            throw new Exception("Username already taken: " + tenant.getAdminUsername());
        }
        Tenant saved = tenantRepository.save(tenant);

        // Auto-create school admin user
        User admin = new User();
        admin.setUsername(tenant.getAdminUsername());
        admin.setEmail(tenant.getAdminEmail());
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setFirstName("Admin");
        admin.setLastName(tenant.getSchoolName());
        admin.setEnabled(true);
        admin.setProfile("default.png");
        admin.setTenantId(saved.getTenantId());

        User savedAdmin = userRepository.save(admin);

        // Reuse existing SCHOOL_ADMIN role created by DataInitializer (id=103)
        Role schoolAdminRole = roleRepository.findById(103L)
                .orElseGet(() -> roleRepository.save(new Role(103L, "SCHOOL_ADMIN")));

        UserRole userRole = new UserRole();
        userRole.setUser(savedAdmin);
        userRole.setRole(schoolAdminRole);
        userRoleRepository.save(userRole);

        return saved;
    }

    @Override
    public Tenant updateTenant(String tenantId, Tenant updated) {
        Tenant existing = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found: " + tenantId));
        existing.setSchoolName(updated.getSchoolName());
        existing.setAddress(updated.getAddress());
        existing.setContactNumber(updated.getContactNumber());
        existing.setEmail(updated.getEmail());
        existing.setStatus(updated.getStatus());
        if (updated.getLogoUrl() != null) existing.setLogoUrl(updated.getLogoUrl());
        return tenantRepository.save(existing);
    }

    @Override
    public void deleteTenant(String tenantId) {
        tenantRepository.deleteById(tenantId);
    }

    @Override
    public void deactivateTenant(String tenantId) {
        Tenant t = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        t.setStatus("INACTIVE");
        tenantRepository.save(t);
    }

    @Override
    public void activateTenant(String tenantId) {
        Tenant t = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        t.setStatus("ACTIVE");
        tenantRepository.save(t);
    }

    @Override
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    @Override
    public Optional<Tenant> getTenantById(String tenantId) {
        return tenantRepository.findById(tenantId);
    }

    @Override
    public long countActive() {
        return tenantRepository.findByStatus("ACTIVE").size();
    }

    @Override
    public long countAll() {
        return tenantRepository.count();
    }
}
