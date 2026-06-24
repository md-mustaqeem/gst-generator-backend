package com.gst.responseDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItemResponse {

    private Long id;

    private String product;

    private Integer qty;

    private Double price;

    private Double discount;

    private Integer gst;

    private Double total;
}