package com.gst.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadPdf(MultipartFile file) {

        try {

            String originalName = file.getOriginalFilename();

            String fileName;

            if (originalName == null || originalName.isBlank()) {
                fileName = "invoice_" + System.currentTimeMillis();
            } else {
                fileName = originalName.replace(".pdf", "")
                        + "_" + System.currentTimeMillis();
            }

            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "raw",
                            "folder", "gst-invoice-generator/invoices",
                            "public_id", fileName,
                            "overwrite", false
                    )
            );

            return uploadResult.get("secure_url").toString();

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException("PDF Upload Failed : " + e.getMessage());

        }
    }
}