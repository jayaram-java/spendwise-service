package com.jayaram.spendwise_service.model;


import java.time.LocalDateTime;

import com.jayaram.spendwise_service.util.ChecklistStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "checklist")
@Getter
@Setter 
public class Checklist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_category_id", nullable = false)
    private ChecklistCategory checklistCategory;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // ✅ Enum instead of FK
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ChecklistStatus status;

    @Column(name = "reference_link", length = 500)
    private String referenceLink;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "is_deleted")
	private Boolean isDeleted = false;
}