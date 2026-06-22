package com.exam.controller;

import com.exam.model.Book;
import com.exam.model.BookIssue;
import com.exam.model.LibraryMember;
import com.exam.repo.BookIssueRepository;
import com.exam.repo.BookRepository;
import com.exam.repo.LibraryMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/library")
public class LibraryController {

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private LibraryMemberRepository memberRepo;

    @Autowired
    private BookIssueRepository issueRepo;

    // ─── Dashboard Stats ───────────────────────────────────────────────────────
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBooks", bookRepo.count());
        stats.put("totalMembers", memberRepo.count());
        stats.put("issuedBooks", issueRepo.countByStatus("ISSUED"));
        stats.put("overdueBooks", issueRepo.countByStatus("OVERDUE"));
        stats.put("availableBooks", bookRepo.countAvailable());
        return ResponseEntity.ok(stats);
    }

    // ─── Books ─────────────────────────────────────────────────────────────────
    @GetMapping("/books")
    public ResponseEntity<?> getAllBooks() {
        return ResponseEntity.ok(bookRepo.findAll());
    }

    @GetMapping("/books/search")
    public ResponseEntity<?> searchBooks(@RequestParam String q) {
        return ResponseEntity.ok(
            bookRepo.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(q, q)
        );
    }

    @PostMapping("/books")
    public ResponseEntity<?> addBook(@RequestBody Book book) {
        if (book.getAvailableCopies() == null) book.setAvailableCopies(book.getTotalCopies());
        return ResponseEntity.ok(bookRepo.save(book));
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody Book book) {
        book.setId(id);
        return ResponseEntity.ok(bookRepo.save(book));
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        bookRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Book deleted"));
    }

    // ─── Members ───────────────────────────────────────────────────────────────
    @GetMapping("/members")
    public ResponseEntity<?> getAllMembers() {
        return ResponseEntity.ok(memberRepo.findAll());
    }

    @PostMapping("/members")
    public ResponseEntity<?> addMember(@RequestBody LibraryMember member) {
        if (member.getMembershipDate() == null) member.setMembershipDate(LocalDate.now());
        if (member.getMembershipExpiry() == null)
            member.setMembershipExpiry(LocalDate.now().plusYears(1));
        return ResponseEntity.ok(memberRepo.save(member));
    }

    @PutMapping("/members/{id}")
    public ResponseEntity<?> updateMember(@PathVariable Long id, @RequestBody LibraryMember member) {
        member.setId(id);
        return ResponseEntity.ok(memberRepo.save(member));
    }

    @DeleteMapping("/members/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        memberRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Member deleted"));
    }

    // ─── Book Issues ───────────────────────────────────────────────────────────
    @GetMapping("/issues")
    public ResponseEntity<?> getAllIssues() {
        return ResponseEntity.ok(issueRepo.findAll());
    }

    @GetMapping("/issues/active")
    public ResponseEntity<?> getActiveIssues() {
        return ResponseEntity.ok(issueRepo.findByStatus("ISSUED"));
    }

    @PostMapping("/issues")
    public ResponseEntity<?> issueBook(@RequestBody Map<String, Long> body) {
        Long bookId = body.get("bookId");
        Long memberId = body.get("memberId");

        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        LibraryMember member = memberRepo.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (book.getAvailableCopies() <= 0)
            return ResponseEntity.badRequest().body(Map.of("error", "No copies available"));

        BookIssue issue = new BookIssue();
        issue.setBook(book);
        issue.setMember(member);
        issue.setIssueDate(LocalDate.now());
        issue.setDueDate(LocalDate.now().plusDays(14));
        issue.setStatus("ISSUED");

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepo.save(book);

        return ResponseEntity.ok(issueRepo.save(issue));
    }

    @PutMapping("/issues/{id}/return")
    public ResponseEntity<?> returnBook(@PathVariable Long id) {
        BookIssue issue = issueRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        issue.setReturnDate(LocalDate.now());
        issue.setStatus("RETURNED");

        // Fine: ₹2 per day after due date
        if (LocalDate.now().isAfter(issue.getDueDate())) {
            long daysLate = LocalDate.now().toEpochDay() - issue.getDueDate().toEpochDay();
            issue.setFine(daysLate * 2.0);
        }

        Book book = issue.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepo.save(book);

        return ResponseEntity.ok(issueRepo.save(issue));
    }

    @GetMapping("/issues/overdue")
    public ResponseEntity<?> getOverdueIssues() {
        List<BookIssue> issued = issueRepo.findByStatus("ISSUED");
        List<BookIssue> overdue = new ArrayList<>();
        for (BookIssue issue : issued) {
            if (issue.getDueDate() != null && LocalDate.now().isAfter(issue.getDueDate())) {
                issue.setStatus("OVERDUE");
                issueRepo.save(issue);
                overdue.add(issue);
            }
        }
        return ResponseEntity.ok(overdue);
    }
}
