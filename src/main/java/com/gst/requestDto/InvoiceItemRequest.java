package com.gst.requestDto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceItemRequest {

    private String product;

    private Integer qty;

    private Double price;

    private Double discount;

    private Integer gst;
}