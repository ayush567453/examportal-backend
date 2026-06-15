package com.exam.repo;

import com.exam.model.ClassRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClassRoomRepository extends JpaRepository<ClassRoom, Long> {
    List<ClassRoom> findByTenantId(String tenantId);
    long countByTenantId(String tenantId);
}
