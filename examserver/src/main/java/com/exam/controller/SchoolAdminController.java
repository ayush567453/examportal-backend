package com.exam.controller;

import com.exam.model.ClassRoom;
import com.exam.model.Tenant;
import com.exam.repo.ClassRoomRepository;
import com.exam.repo.StudentProfileRepository;
import com.exam.repo.TeacherRepository;
import com.exam.repo.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/school")
@CrossOrigin("*")
public class SchoolAdminController {

    @Autowired
    private StudentProfileRepository studentRepo;

    @Autowired
    private TeacherRepository teacherRepo;

    @Autowired
    private ClassRoomRepository classRoomRepo;

    @Autowired
    private TenantRepository tenantRepo;

    // Dashboard stats for a specific tenant
    @GetMapping("/dashboard/{tenantId}")
    public Map<String, Object> getDashboard(@PathVariable String tenantId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", studentRepo.countByTenantId(tenantId));
        stats.put("totalTeachers", teacherRepo.countByTenantId(tenantId));
        stats.put("totalClasses", classRoomRepo.countByTenantId(tenantId));
        stats.put("recentStudents", studentRepo.findByTenantId(tenantId).stream()
                .limit(5).collect(java.util.stream.Collectors.toList()));
        return stats;
    }

    // ── Class management ──────────────────────────────────────
    @GetMapping("/classes/{tenantId}")
    public List<ClassRoom> getClasses(@PathVariable String tenantId) {
        return classRoomRepo.findByTenantId(tenantId);
    }

    @PostMapping("/classes")
    public ResponseEntity<?> addClass(@RequestBody ClassRoom classRoom) {
        return ResponseEntity.ok(classRoomRepo.save(classRoom));
    }

    @DeleteMapping("/classes/{id}")
    public ResponseEntity<?> deleteClass(@PathVariable Long id) {
        classRoomRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Class deleted"));
    }

    // School info
    @GetMapping("/info/{tenantId}")
    public ResponseEntity<?> getSchoolInfo(@PathVariable String tenantId) {
        return tenantRepo.findById(tenantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
