package com.exam.controller;

import com.exam.repo.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/public")
@CrossOrigin("*")
public class PublicController {

    @Autowired
    private TenantRepository tenantRepository;

    // Public endpoint — any logged-in user can fetch their school's info
    @GetMapping("/school/{tenantId}")
    public ResponseEntity<?> getSchoolInfo(@PathVariable String tenantId) {
        return tenantRepository.findById(tenantId).map(t -> {
            Map<String, String> info = new HashMap<>();
            info.put("schoolName", t.getSchoolName());
            info.put("logoUrl", t.getLogoUrl());
            info.put("tenantId", t.getTenantId());
            info.put("email", t.getEmail());
            return ResponseEntity.ok((Object) info);
        }).orElse(ResponseEntity.notFound().build());
    }
}
