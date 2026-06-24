package com.gst.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceRequest {

    private CustomerRequest customer;

    private List<InvoiceItemRequest> products;

    private Double subtotal;

    private Double discountAmount;

    private Double gstAmount;

    private Double grandTotal;

    private Double paidAmount;
    private Double dueAmount;
}