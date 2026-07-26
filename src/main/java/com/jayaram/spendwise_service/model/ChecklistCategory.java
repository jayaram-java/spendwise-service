package com.jayaram.spendwise_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "checklist_category")
@Getter
@Setter
public class ChecklistCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_primary")
    private Boolean isPrimary = false;

    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
}