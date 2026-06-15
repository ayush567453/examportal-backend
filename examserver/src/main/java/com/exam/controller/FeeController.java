package com.exam.controller;

import com.exam.model.FeePayment;
import com.exam.model.FeeStructure;
import com.exam.repo.FeePaymentRepository;
import com.exam.repo.FeeStructureRepository;
import com.exam.repo.StudentProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/school/fees")
@CrossOrigin("*")
public class FeeController {

    @Autowired
    private FeeStructureRepository structureRepo;

    @Autowired
    private FeePaymentRepository paymentRepo;

    @Autowired
    private StudentProfileRepository studentRepo;

    // ═══════════════════════════════════════════════════════════════
    //  FEE STRUCTURE
    // ═══════════════════════════════════════════════════════════════

    /** GET all fee structures for a school */
    @GetMapping("/structure/{tenantId}")
    public List<FeeStructure> getStructure(@PathVariable String tenantId) {
        return structureRepo.findByTenantId(tenantId);
    }

    /**
     * POST - Save/Replace all fee structures for a school.
     * Accepts the full array; upserts each entry by classKey.
     */
    @PostMapping("/structure/{tenantId}")
    public ResponseEntity<?> saveStructure(
            @PathVariable String tenantId,
            @RequestBody List<FeeStructure> incoming) {

        List<FeeStructure> saved = new ArrayList<>();
        for (FeeStructure fs : incoming) {
            fs.setTenantId(tenantId);
            if (fs.getLabel() == null || fs.getLabel().isBlank()) {
                fs.setLabel(fs.getClassKey());
            }

            // Upsert: update existing row or create new
            Optional<FeeStructure> existing =
                structureRepo.findByTenantIdAndClassKey(tenantId, fs.getClassKey());
            if (existing.isPresent()) {
                FeeStructure e = existing.get();
                e.setMonthly(fs.getMonthly());
                e.setAdmission(fs.getAdmission());
                e.setExam(fs.getExam());
                e.setOther(fs.getOther());
                e.setLabel(fs.getLabel());
                saved.add(structureRepo.save(e));
            } else {
                saved.add(structureRepo.save(fs));
            }
        }
        return ResponseEntity.ok(saved);
    }

    // ═══════════════════════════════════════════════════════════════
    //  FEE PAYMENTS
    // ═══════════════════════════════════════════════════════════════

    /** GET all payment records for a school */
    @GetMapping("/payments/{tenantId}")
    public List<FeePayment> getPayments(@PathVariable String tenantId) {
        return paymentRepo.findByTenantId(tenantId);
    }

    /** GET payments for a single student */
    @GetMapping("/payments/{tenantId}/student/{studentId}")
    public List<FeePayment> getStudentPayments(
            @PathVariable String tenantId,
            @PathVariable Long studentId) {
        return paymentRepo.findByTenantIdAndStudentId(tenantId, studentId);
    }

    /**
     * POST /school/fees/payments/mark
     * Mark a specific student's month as paid or pending.
     * Upserts by (tenantId, studentId, month, year).
     */
    @PostMapping("/payments/mark")
    public ResponseEntity<?> markPayment(@RequestBody FeePayment req) {
        if (req.getYear() == null || req.getYear().isBlank()) {
            req.setYear(String.valueOf(LocalDateTime.now().getYear()));
        }

        Optional<FeePayment> existing =
            paymentRepo.findByTenantIdAndStudentIdAndMonthAndYear(
                req.getTenantId(), req.getStudentId(), req.getMonth(), req.getYear());

        FeePayment record = existing.orElse(new FeePayment());
        record.setTenantId(req.getTenantId());
        record.setStudentId(req.getStudentId());
        record.setStudentName(req.getStudentName());
        record.setRollNo(req.getRollNo());
        record.setClassName(req.getClassName());
        record.setSection(req.getSection());
        record.setMonth(req.getMonth());
        record.setYear(req.getYear());
        record.setStatus(req.getStatus());
        record.setAmount(req.getAmount());
        record.setNote(req.getNote());

        if ("paid".equals(req.getStatus())) {
            record.setPaidAt(LocalDateTime.now());
        } else {
            record.setPaidAt(null);
        }

        return ResponseEntity.ok(paymentRepo.save(record));
    }

    /**
     * POST /school/fees/payments/mark-bulk
     * Mark multiple payments at once (e.g. mark all months for one student).
     */
    @PostMapping("/payments/mark-bulk")
    public ResponseEntity<?> markBulk(@RequestBody List<FeePayment> requests) {
        List<FeePayment> saved = new ArrayList<>();
        for (FeePayment req : requests) {
            ResponseEntity<?> resp = markPayment(req);
            saved.add((FeePayment) resp.getBody());
        }
        return ResponseEntity.ok(saved);
    }

    // ═══════════════════════════════════════════════════════════════
    //  SUMMARY DASHBOARD
    // ═══════════════════════════════════════════════════════════════

    /**
     * GET /school/fees/summary/{tenantId}
     * Returns aggregated summary: total collected, pending,
     * month-wise breakdown, and class-wise breakdown.
     */
    @GetMapping("/summary/{tenantId}")
    public Map<String, Object> getSummary(@PathVariable String tenantId) {

        List<FeePayment> all = paymentRepo.findByTenantId(tenantId);
        List<FeeStructure> structures = structureRepo.findByTenantId(tenantId);

        // Build fee-per-class map
        Map<String, Double> feePerClass = new HashMap<>();
        for (FeeStructure fs : structures) {
            feePerClass.put(fs.getClassKey(), fs.getMonthly());
        }

        double totalCollected = 0;
        double totalPending   = 0;

        for (FeePayment p : all) {
            if ("paid".equals(p.getStatus())) totalCollected += p.getAmount();
            else                               totalPending   += p.getAmount();
        }

        double totalDue = totalCollected + totalPending;
        int    rate     = totalDue > 0 ? (int) Math.round((totalCollected / totalDue) * 100) : 0;

        // Month-wise breakdown
        String[] months = {"Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec","Jan","Feb","Mar"};
        List<Map<String, Object>> monthWise = new ArrayList<>();
        for (String m : months) {
            List<FeePayment> forMonth = all.stream()
                .filter(p -> m.equals(p.getMonth())).collect(Collectors.toList());

            double collected = forMonth.stream()
                .filter(p -> "paid".equals(p.getStatus()))
                .mapToDouble(FeePayment::getAmount).sum();
            double pending = forMonth.stream()
                .filter(p -> "pending".equals(p.getStatus()))
                .mapToDouble(FeePayment::getAmount).sum();
            long pendingStudents = forMonth.stream()
                .filter(p -> "pending".equals(p.getStatus())).count();

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("month", m);
            row.put("collected", collected);
            row.put("pending", pending);
            row.put("pendingStudents", pendingStudents);
            monthWise.add(row);
        }

        // Class-wise breakdown
        Map<String, double[]> classMap = new LinkedHashMap<>();
        for (FeePayment p : all) {
            classMap.computeIfAbsent(p.getClassName(), k -> new double[]{0, 0});
            if ("paid".equals(p.getStatus())) classMap.get(p.getClassName())[0] += p.getAmount();
            else                               classMap.get(p.getClassName())[1] += p.getAmount();
        }
        List<Map<String, Object>> classWise = new ArrayList<>();
        classMap.forEach((cls, arr) -> {
            double c = arr[0], pe = arr[1], tot = c + pe;
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("className", cls);
            row.put("collected", c);
            row.put("pending", pe);
            row.put("total", tot);
            row.put("rate", tot > 0 ? (int) Math.round((c / tot) * 100) : 0);
            classWise.add(row);
        });

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalCollected", totalCollected);
        result.put("totalPending", totalPending);
        result.put("totalDue", totalDue);
        result.put("collectionRate", rate);
        result.put("totalPayments", all.size());
        result.put("monthWise", monthWise);
        result.put("classWise", classWise);
        return result;
    }
}
