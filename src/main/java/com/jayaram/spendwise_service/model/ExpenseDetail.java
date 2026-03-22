package com.jayaram.spendwise_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expense_detail", indexes = { @Index(name = "idx_expense_date", columnList = "expense_date"),
		@Index(name = "idx_expense_user", columnList = "user_id"),
		@Index(name = "idx_expense_category", columnList = "expense_category_id"),
		@Index(name = "idx_user_category", columnList = "user_id, expense_category_id") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseDetail extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "expense_name", nullable = false)
	private String expenseName;

	@Column(name = "expense_date", nullable = false)
	private LocalDate expenseDate;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal amount;

	private String description;

	@Column(name = "payment_method")
	private String paymentMethod;

	@Column(name = "expense_code", unique = true)
	private String expenseCode;

	@Column(name = "reference_number")
	private String referenceNumber;

	@Column(name = "receipt_url")
	private String receiptUrl;

	@Column(name = "currency")
	private String currency = "INR";

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "status")
	private String status = "ACTIVE";

	@Column(name = "is_deleted")
	private Boolean isDeleted = false;

	// Relationship
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "expense_category_id", nullable = false)
	private ExpenseCategory category;
}