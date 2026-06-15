package com.exam.repo;

import com.exam.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    List<StudentProfile> findByTenantId(String tenantId);
    Optional<StudentProfile> findByStudentCodeAndTenantId(String studentCode, String tenantId);
    long countByTenantId(String tenantId);
    boolean existsByUsernameAndTenantId(String username, String tenantId);
}
