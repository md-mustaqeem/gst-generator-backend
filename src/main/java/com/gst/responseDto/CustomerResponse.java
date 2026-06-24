package com.gst.responseDto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private String address;

    private Long totalInvoices;
    private Long totalProducts;

    private Double totalBusiness;
    private Double totalPaid;
    private Double totalDue;

    private LocalDate customerSince;

    // Purchase
    private LocalDate lastPurchaseDate;
    private String lastInvoiceNo;
    private Integer todayPurchaseCount;

    // Payment
    private LocalDate lastPaymentDate;
    private Double lastPaymentAmount;
    private Integer todayPaymentCount;

    // Due
    private LocalDate lastDueDate;
    private Integer todayDueCount;

    private String status;
}