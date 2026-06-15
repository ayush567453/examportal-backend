package com.exam.controller;

import com.exam.repo.StudentProfileRepository;
import com.exam.repo.TeacherRepository;
import com.exam.repo.TenantRepository;
import com.exam.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/super-admin")
@CrossOrigin("*")
public class SuperAdminController {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSchools", tenantRepository.count());
        stats.put("activeSchools", tenantRepository.findByStatus("ACTIVE").size());
        stats.put("totalStudents", studentProfileRepository.count());
        stats.put("totalTeachers", teacherRepository.count());
        stats.put("totalUsers", userRepository.count());
        stats.put("recentTenants", tenantRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5).collect(java.util.stream.Collectors.toList()));
        return stats;
    }
}
