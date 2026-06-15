package com.exam.model;

import javax.persistence.*;

@Entity
@Table(name = "fee_structures",
       uniqueConstraints = @UniqueConstraint(columnNames = {"tenantId", "classKey"}))
public class FeeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 36)
    private String tenantId;

    @Column(nullable = false)
    private String classKey;   // e.g. "Class 5", "KG-1"

    private String label;      // display name (same as classKey usually)

    private double monthly   = 0;
    private double admission = 0;
    private double exam      = 0;
    private double other     = 0;

    public FeeStructure() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getClassKey() { return classKey; }
    public void setClassKey(String classKey) { this.classKey = classKey; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public double getMonthly()   { return monthly; }
    public void   setMonthly(double monthly)   { this.monthly   = monthly; }

    public double getAdmission() { return admission; }
    public void   setAdmission(double admission) { this.admission = admission; }

    public double getExam()  { return exam; }
    public void   setExam(double exam)   { this.exam  = exam; }

    public double getOther() { return other; }
    public void   setOther(double other) { this.other = other; }
}
