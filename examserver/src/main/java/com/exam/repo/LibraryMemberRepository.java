package com.exam.repo;

import com.exam.model.LibraryMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibraryMemberRepository extends JpaRepository<LibraryMember, Long> {
    List<LibraryMember> findByNameContainingIgnoreCase(String name);
    List<LibraryMember> findByStatus(String status);
}
