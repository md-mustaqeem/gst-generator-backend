package com.gst.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="invoice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String invoiceNo;

    private LocalDate invoiceDate;

    @DecimalMin("0")
    private Double subtotal;

    @DecimalMin("0")
    private Double discountAmount;

    @DecimalMin("0")
    private Double gstAmount;

    @DecimalMin("0")
    private Double grandTotal;

    @DecimalMin("0")
    private Double paidAmount = 0.0;

    @DecimalMin("0")
    private Double dueAmount = 0.0;

    @Column(columnDefinition = "TEXT")
    private String pdfUrl;

    private LocalDateTime lastPaymentDate;

    private LocalDateTime lastDueUpdateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="business_id")
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="customer_id")
    private Customer customer;

    @OneToMany(
            mappedBy = "invoice",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<InvoiceItem> items=new ArrayList<>();


}