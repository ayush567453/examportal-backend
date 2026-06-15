package com.exam.controller;

import com.exam.model.Tenant;
import com.exam.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/super-admin/tenants")
@CrossOrigin("*")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @Value("${tenant.logo.path:./uploads/logos}")
    private String logoStoragePath;

    @GetMapping
    public List<Tenant> getAllTenants() {
        return tenantService.getAllTenants();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTenant(@PathVariable String id) {
        return tenantService.getTenantById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createTenant(
            @RequestParam("schoolName") String schoolName,
            @RequestParam("tenantCode") String tenantCode,
            @RequestParam(value = "address", required = false, defaultValue = "") String address,
            @RequestParam(value = "contactNumber", required = false, defaultValue = "") String contactNumber,
            @RequestParam("email") String email,
            @RequestParam(value = "status", defaultValue = "ACTIVE") String status,
            @RequestParam("adminUsername") String adminUsername,
            @RequestParam(value = "adminEmail", required = false, defaultValue = "") String adminEmail,
            @RequestParam("adminPassword") String adminPassword,
            @RequestParam(value = "logo", required = false) MultipartFile logo) {

        try {
            Tenant tenant = new Tenant();
            tenant.setSchoolName(schoolName);
            tenant.setTenantCode(tenantCode.toUpperCase().replaceAll("\\s+", "_"));
            tenant.setAddress(address);
            tenant.setContactNumber(contactNumber);
            tenant.setEmail(email);
            tenant.setStatus(status);
            tenant.setAdminUsername(adminUsername);
            tenant.setAdminEmail(adminEmail);

            if (logo != null && !logo.isEmpty()) {
                Files.createDirectories(Paths.get(logoStoragePath));
                // Sanitize filename: replace spaces and special chars
                String original = logo.getOriginalFilename() != null ? logo.getOriginalFilename() : "logo.png";
                String safeOriginal = original.replaceAll("[^a-zA-Z0-9._-]", "_");
                String filename = tenantCode + "_" + System.currentTimeMillis() + "_" + safeOriginal;
                File dest = Paths.get(logoStoragePath, filename).toFile();
                logo.transferTo(dest);
                tenant.setLogoUrl(filename);
            }

            Tenant created = tenantService.createTenant(tenant, adminPassword);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTenant(@PathVariable String id, @RequestBody Tenant tenant) {
        try {
            return ResponseEntity.ok(tenantService.updateTenant(id, tenant));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable String id) {
        tenantService.deactivateTenant(id);
        return ResponseEntity.ok(Map.of("status", "INACTIVE"));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable String id) {
        tenantService.activateTenant(id);
        return ResponseEntity.ok(Map.of("status", "ACTIVE"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTenant(@PathVariable String id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.ok(Map.of("message", "Tenant deleted"));
    }
}
