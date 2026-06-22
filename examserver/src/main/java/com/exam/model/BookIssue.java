package com.exam.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "library_book_issues")
public class BookIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private LibraryMember member;

    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    @Column(nullable = false)
    private String status = "ISSUED";

    private Double fine = 0.0;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public LibraryMember getMember() { return member; }
    public void setMember(LibraryMember member) { this.member = member; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getFine() { return fine; }
    public void setFine(Double fine) { this.fine = fine; }
}
