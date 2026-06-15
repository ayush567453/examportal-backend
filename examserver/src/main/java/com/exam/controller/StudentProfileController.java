package com.exam.controller;

import com.exam.model.StudentProfile;
import com.exam.model.User;
import com.exam.model.UserRole;
import com.exam.repo.RoleRepository;
import com.exam.repo.StudentProfileRepository;
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
@RequestMapping("/school/students")
@CrossOrigin("*")
public class StudentProfileController {

    @Autowired
    private StudentProfileRepository studentRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private UserRoleRepository userRoleRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/{tenantId}")
    public List<StudentProfile> getStudents(@PathVariable String tenantId) {
        return studentRepo.findByTenantId(tenantId);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getStudent(@PathVariable Long id) {
        return studentRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createStudent(@RequestBody Map<String, String> body) {
        try {
            String tenantId   = body.get("tenantId");
            String fullName   = body.get("fullName");
            String username   = body.get("username");
            String email      = body.get("email");
            String password   = body.get("password");
            String schoolName = body.get("schoolName");
            String className  = body.get("className");
            String section    = body.get("section");
            String phone      = body.get("phone");

            // Create login user for student
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password != null && !password.isEmpty() ? password : "student123"));
            user.setFirstName(fullName);
            user.setLastName("");
            user.setEnabled(true);
            user.setProfile("default.png");
            user.setTenantId(tenantId);
            User savedUser = userRepo.save(user);

            // Reuse existing STUDENT role (id=104) from DataInitializer
            com.exam.model.Role studentRole = roleRepo.findById(104L)
                    .orElseGet(() -> roleRepo.save(new com.exam.model.Role(104L, "STUDENT")));
            UserRole userRole = new UserRole();
            userRole.setUser(savedUser);
            userRole.setRole(studentRole);
            userRoleRepo.save(userRole);

            // Generate student code
            long count = studentRepo.countByTenantId(tenantId) + 1;
            String studentCode = "STU-" + String.format("%04d", count);

            StudentProfile profile = new StudentProfile();
            profile.setTenantId(tenantId);
            profile.setUserId(savedUser.getId());
            profile.setFullName(fullName);
            profile.setUsername(username);
            profile.setEmail(email);
            profile.setPhone(phone);
            profile.setSchoolName(schoolName);
            profile.setClassName(className);
            profile.setSection(section);
            profile.setStudentCode(studentCode);
            profile.setProfileImage("default.png");

            return ResponseEntity.ok(studentRepo.save(profile));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody StudentProfile updated) {
        return studentRepo.findById(id).map(s -> {
            s.setFullName(updated.getFullName());
            s.setClassName(updated.getClassName());
            s.setSection(updated.getSection());
            s.setPhone(updated.getPhone());
            s.setAddress(updated.getAddress());
            s.setGuardianName(updated.getGuardianName());
            s.setGuardianPhone(updated.getGuardianPhone());
            return ResponseEntity.ok(studentRepo.save(s));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        studentRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Student deleted"));
    }
}
