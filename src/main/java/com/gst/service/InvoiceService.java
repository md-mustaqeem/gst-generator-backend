package com.gst.service;


import com.gst.requestDto.InvoiceRequest;
import com.gst.responseDto.DashboardResponse;
import com.gst.responseDto.InvoiceResponse;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceService {

    InvoiceResponse createInvoice(InvoiceRequest request);

    List<InvoiceResponse> getAllInvoices();

    InvoiceResponse getInvoiceById(Long id);

    void deleteInvoice(Long id);

    List<InvoiceResponse> getInvoicesByDate(LocalDate date);

    DashboardResponse getDashboard();


    InvoiceResponse updatePayment(Long id, InvoiceRequest request);

    InvoiceResponse updateInvoice(Long id, InvoiceRequest request);


    void updatePdfUrl(Long invoiceId, String pdfUrl);
}
