package com.exam.model;

import javax.persistence.*;

@Entity
@Table(name = "class_rooms")
public class ClassRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 36)
    private String tenantId;

    private String className;   // e.g. "Class 10"
    private String section;     // e.g. "A"
    private String subject;
    private int capacity;
    private Long classTeacherId;

    public ClassRoom() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public Long getClassTeacherId() { return classTeacherId; }
    public void setClassTeacherId(Long classTeacherId) { this.classTeacherId = classTeacherId; }
}
