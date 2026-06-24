package com.gst.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {

    private Long id;

    private String invoiceNo;

    private LocalDate invoiceDate;

    private LocalDateTime lastPaymentDate;

    private LocalDateTime lastDueUpdateDate;

    private String pdfUrl;

    // Business
    // Business
    private Long businessId;
    private String businessName;
    private String businessGst;

    private String businessAddress;
    private String businessPhone;
    private String businessEmail;



    // Customer
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String customerAddress;

    // Items
    private List<InvoiceItemResponse> items;

    // Summary
    private Double subtotal;
    private Double discountAmount;
    private Double gstAmount;
    private Double grandTotal;

    // Payment
    private Double paidAmount;
    private Double dueAmount;
}