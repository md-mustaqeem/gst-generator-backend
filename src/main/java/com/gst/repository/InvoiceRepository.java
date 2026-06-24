package com.gst.repository;

import com.gst.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByUserIdOrderByIdDesc(Long userId);

    List<Invoice> findByUserIdAndInvoiceDateOrderByIdDesc(
            Long userId,
            LocalDate invoiceDate
    );

    List<Invoice> findByUserId(Long userId);

    List<Invoice> findByCustomerIdOrderByInvoiceDateDesc(Long customerId);

}