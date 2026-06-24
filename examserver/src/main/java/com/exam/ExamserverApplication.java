package com.exam;

import com.exam.helper.UserFoundException;
import com.exam.model.Role;
import com.exam.model.User;
import com.exam.model.UserRole;
import com.exam.service.RoleService;
import com.exam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class ExamserverApplication implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;  // Add a RoleService to handle roles

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(ExamserverApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            System.out.println("Starting application initialization...");

            // Create role
            Role role = new Role();
            role.setRoleId(44L);
            role.setRoleName("ADMIN");
            // Save role to ensure it's persisted
            role = roleService.saveRole(role);

            // Create first user
            User user1 = new User();
            user1.setFirstName("Durgesh");
            user1.setLastName("Tiwari");
            user1.setUsername("durgesh8896");
            user1.setPassword(this.bCryptPasswordEncoder.encode("abc"));
            user1.setEmail("durgesh@gmail.com");
            user1.setProfile("default.png");

            UserRole userRole1 = new UserRole();
            userRole1.setRole(role);
            userRole1.setUser(user1);

            Set<UserRole> userRoleSet1 = new HashSet<>();
            userRoleSet1.add(userRole1);

            // Create second user
            User user2 = new User();
            user2.setFirstName("Ayush");
            user2.setLastName("Maddheshiya");
            user2.setUsername("Ayush123");
            user2.setPassword(this.bCryptPasswordEncoder.encode("abc"));
            user2.setEmail("ayush@gmail.com");
            user2.setProfile("default.png");

            UserRole userRole2 = new UserRole();
            userRole2.setRole(role);
            userRole2.setUser(user2);

            Set<UserRole> userRoleSet2 = new HashSet<>();
            userRoleSet2.add(userRole2);

            // Create users
            User createdUser1 = this.userService.createUser(user1, userRoleSet1);
            User createdUser2 = this.userService.createUser(user2, userRoleSet2);

            System.out.println("User 1 created: " + createdUser1.getUsername());
            System.out.println("User 2 created: " + createdUser2.getUsername());

       } catch (Exception e) {
    System.out.println("Initialization skipped: " + e.getMessage());
}
    }
}
