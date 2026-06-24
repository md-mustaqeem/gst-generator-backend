package com.gst.controller;

import com.gst.requestDto.InvoiceRequest;
import com.gst.responseDto.DashboardResponse;
import com.gst.responseDto.InvoiceResponse;
import com.gst.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@CrossOrigin("*")
public class InvoiceController {

    private final InvoiceService invoiceService;

    // Create Invoice
    @PostMapping("/saveInvoice")
    public ResponseEntity<InvoiceResponse> createInvoice(
            @Valid @RequestBody InvoiceRequest request) {

        return new ResponseEntity<>(
                invoiceService.createInvoice(request),
                HttpStatus.CREATED
        );
    }

    // Get All Invoices
    @GetMapping("/getAllInvoice")
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {

        return ResponseEntity.ok(
                invoiceService.getAllInvoices()
        );
    }

    // Get Invoice By Id
    @GetMapping("/getInvoice/{id}")
    public ResponseEntity<InvoiceResponse> getInvoiceById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                invoiceService.getInvoiceById(id)
        );
    }

    // Delete Invoice
    @DeleteMapping("/deleteInvoice/{id}")
    public ResponseEntity<String> deleteInvoice(
            @PathVariable Long id) {

        invoiceService.deleteInvoice(id);

        return ResponseEntity.ok("Invoice Deleted Successfully");
    }

    //recent invoice

    @GetMapping("/by-date")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByDate(
            @RequestParam LocalDate date) {

        return ResponseEntity.ok(
                invoiceService.getInvoicesByDate(date)
        );
    }


    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {

        return ResponseEntity.ok(
                invoiceService.getDashboard()
        );
    }


    @PutMapping("/updatePayment/{id}")
    public ResponseEntity<InvoiceResponse> updatePayment(
            @PathVariable Long id,
            @RequestBody InvoiceRequest request
    ) {

        return ResponseEntity.ok(
                invoiceService.updatePayment(id, request)
        );
    }


    @PutMapping("updateInvoice/{id}")
    public ResponseEntity<InvoiceResponse> updateInvoice(
            @PathVariable Long id,
            @RequestBody InvoiceRequest request) {

        return ResponseEntity.ok(invoiceService.updateInvoice(id, request));
    }
}