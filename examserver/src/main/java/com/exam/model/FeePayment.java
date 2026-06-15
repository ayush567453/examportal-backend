package com.exam.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_payments",
       uniqueConstraints = @UniqueConstraint(columnNames = {"tenantId", "studentId", "month", "year"}))
public class FeePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 36)
    private String tenantId;

    @Column(nullable = false)
    private Long studentId;

    private String studentName;
    private String rollNo;
    private String className;
    private String section;

    @Column(nullable = false, length = 10)
    private String month;       // e.g. "Apr", "May"

    @Column(nullable = false, length = 10)
    private String year;        // e.g. "2026"

    @Column(nullable = false)
    private String status = "pending";   // "paid" | "pending"

    private double amount = 0;
    private String note;

    private LocalDateTime paidAt;

    public FeePayment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String t) { this.tenantId = t; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long s) { this.studentId = s; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String s) { this.studentName = s; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String r) { this.rollNo = r; }

    public String getClassName() { return className; }
    public void setClassName(String c) { this.className = c; }

    public String getSection() { return section; }
    public void setSection(String s) { this.section = s; }

    public String getMonth() { return month; }
    public void setMonth(String m) { this.month = m; }

    public String getYear() { return year; }
    public void setYear(String y) { this.year = y; }

    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }

    public double getAmount() { return amount; }
    public void setAmount(double a) { this.amount = a; }

    public String getNote() { return note; }
    public void setNote(String n) { this.note = n; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime p) { this.paidAt = p; }
}
