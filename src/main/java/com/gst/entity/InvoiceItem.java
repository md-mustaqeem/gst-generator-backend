package com.gst.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="invoice_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String product;

    @Min(0)
    private Integer qty;

    @DecimalMin("0")
    private Double price;

    @Min(0)
    @Max(100)
    private Double discount;

    @Min(0)
    @Max(28)
    private Integer gst;

    @DecimalMin("0")
    private Double total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="invoice_id")
    private Invoice invoice;
}