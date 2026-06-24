package com.gst.responseDto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    // ================= Overall =================

    private Long totalInvoices;
    private Long totalCustomers;
    private Long totalProductsSold;

    private Double totalRevenue;
    private Double totalDiscount;
    private Double totalGST;
    private Double totalReceived;
    private Double totalDue;

    // ================= Today =================

    private Double todaySale;
    private Double todayReceived;
    private Double todayDue;

    private Long todayCustomers;
    private Long todayInvoices;

    // ================= Week =================

    private Double weekSale;
    private Double weekReceived;
    private Double weekDue;

    private Long weekCustomers;
    private Long weekInvoices;

    // ================= Month =================

    private Double monthSale;
    private Double monthReceived;
    private Double monthDue;

    private Long monthCustomers;
    private Long monthInvoices;

    // ================= Year =================

    private Double yearSale;
    private Double yearReceived;
    private Double yearDue;

    private Long yearCustomers;
    private Long yearInvoices;
}