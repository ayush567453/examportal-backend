package com.exam.repo;

import com.exam.model.FeePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FeePaymentRepository extends JpaRepository<FeePayment, Long> {

    List<FeePayment> findByTenantId(String tenantId);

    List<FeePayment> findByTenantIdAndStudentId(String tenantId, Long studentId);

    List<FeePayment> findByTenantIdAndMonth(String tenantId, String month);

    List<FeePayment> findByTenantIdAndStatus(String tenantId, String status);

    Optional<FeePayment> findByTenantIdAndStudentIdAndMonthAndYear(
            String tenantId, Long studentId, String month, String year);

    @Query("SELECT SUM(p.amount) FROM FeePayment p WHERE p.tenantId = :tenantId AND p.status = 'paid'")
    Double sumCollectedByTenant(@Param("tenantId") String tenantId);

    @Query("SELECT SUM(p.amount) FROM FeePayment p WHERE p.tenantId = :tenantId AND p.status = 'pending'")
    Double sumPendingByTenant(@Param("tenantId") String tenantId);

    long countByTenantIdAndStatus(String tenantId, String status);
}
