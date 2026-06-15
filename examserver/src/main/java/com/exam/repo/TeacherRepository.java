package com.exam.repo;

import com.exam.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> findByTenantId(String tenantId);
    long countByTenantId(String tenantId);
}
