package com.exam.controller;

import com.exam.model.Teacher;
import com.exam.model.User;
import com.exam.model.UserRole;
import com.exam.repo.RoleRepository;
import com.exam.repo.TeacherRepository;
import com.exam.repo.UserRepository;
import com.exam.repo.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/school/teachers")
@CrossOrigin("*")
public class TeacherMgmtController {

    @Autowired
    private TeacherRepository teacherRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private UserRoleRepository userRoleRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/{tenantId}")
    public List<Teacher> getTeachers(@PathVariable String tenantId) {
        return teacherRepo.findByTenantId(tenantId);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getTeacher(@PathVariable Long id) {
        return teacherRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createTeacher(@RequestBody Map<String, String> body) {
        try {
            String tenantId      = body.get("tenantId");
            String fullName      = body.get("fullName");
            String username      = body.get("username");
            String email         = body.get("email");
            String password      = body.get("password");
            String schoolName    = body.get("schoolName");
            String subject       = body.get("subject");
            String qualification = body.get("qualification");
            String phone         = body.get("phone");

            // Create login user for teacher
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password != null && !password.isEmpty() ? password : "teacher123"));
            user.setFirstName(fullName);
            user.setLastName("");
            user.setEnabled(true);
            user.setProfile("default.png");
            user.setTenantId(tenantId);
            User savedUser = userRepo.save(user);

            // Reuse existing TEACHER role (id=105) from DataInitializer
            com.exam.model.Role teacherRole = roleRepo.findById(105L)
                    .orElseGet(() -> roleRepo.save(new com.exam.model.Role(105L, "TEACHER")));
            UserRole userRole = new UserRole();
            userRole.setUser(savedUser);
            userRole.setRole(teacherRole);
            userRoleRepo.save(userRole);

            long count = teacherRepo.countByTenantId(tenantId) + 1;
            String teacherCode = "TCH-" + String.format("%04d", count);

            Teacher teacher = new Teacher();
            teacher.setTenantId(tenantId);
            teacher.setUserId(savedUser.getId());
            teacher.setFullName(fullName);
            teacher.setUsername(username);
            teacher.setEmail(email);
            teacher.setPhone(phone);
            teacher.setSchoolName(schoolName);
            teacher.setSubject(subject);
            teacher.setQualification(qualification);
            teacher.setTeacherCode(teacherCode);
            teacher.setProfileImage("default.png");

            return ResponseEntity.ok(teacherRepo.save(teacher));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTeacher(@PathVariable Long id, @RequestBody Teacher updated) {
        return teacherRepo.findById(id).map(t -> {
            t.setFullName(updated.getFullName());
            t.setSubject(updated.getSubject());
            t.setQualification(updated.getQualification());
            t.setPhone(updated.getPhone());
            t.setAddress(updated.getAddress());
            return ResponseEntity.ok(teacherRepo.save(t));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeacher(@PathVariable Long id) {
        teacherRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Teacher deleted"));
    }
}
