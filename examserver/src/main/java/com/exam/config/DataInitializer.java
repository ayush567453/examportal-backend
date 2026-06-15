package com.exam.config;

import com.exam.model.Role;
import com.exam.model.User;
import com.exam.model.UserRole;
import com.exam.repo.RoleRepository;
import com.exam.repo.UserRepository;
import com.exam.repo.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createRoleIfNotExists(100L, "SUPER_ADMIN");
        createRoleIfNotExists(101L, "ADMIN");
        createRoleIfNotExists(102L, "NORMAL");
        createRoleIfNotExists(103L, "SCHOOL_ADMIN");
        createRoleIfNotExists(104L, "STUDENT");
        createRoleIfNotExists(105L, "TEACHER");

        // Create SUPER_ADMIN user if not exists
        if (userRepository.findByUsername("superadmin") == null) {
            User superAdmin = new User();
            superAdmin.setUsername("superadmin");
            superAdmin.setPassword(bCryptPasswordEncoder.encode("superadmin123"));
            superAdmin.setFirstName("Super");
            superAdmin.setLastName("Admin");
            superAdmin.setEmail("superadmin@examportal.com");
            superAdmin.setEnabled(true);
            superAdmin.setTenantId(null);

            User savedUser = userRepository.save(superAdmin);

            Role superAdminRole = roleRepository.findById(100L).orElseThrow();
            UserRole userRole = new UserRole();
            userRole.setUser(savedUser);
            userRole.setRole(superAdminRole);
            userRoleRepository.save(userRole);

            System.out.println("✅ SUPER_ADMIN created → username: superadmin | password: superadmin123");
        } else {
            System.out.println("ℹ️  SUPER_ADMIN already exists.");
        }
    }

    private void createRoleIfNotExists(Long id, String name) {
        if (!roleRepository.existsById(id)) {
            roleRepository.save(new Role(id, name));
        }
    }
}
