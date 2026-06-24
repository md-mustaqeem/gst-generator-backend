package com.gst.controller;

import com.gst.service.CloudinaryService;
import com.gst.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/pdf")
@RequiredArgsConstructor
public class PdfController {

    private final CloudinaryService cloudinaryService;
    private final InvoiceService invoiceService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam("invoiceId") Long invoiceId) {

        try {

            // Upload PDF to Cloudinary
            String url = cloudinaryService.uploadPdf(file);

            // Save URL in Invoice table
            invoiceService.updatePdfUrl(invoiceId, url);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "url", url
            ));

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", e.getMessage()
                    )
            );
        }
    }
}