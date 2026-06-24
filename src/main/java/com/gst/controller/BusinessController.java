package com.gst.controller;

import com.gst.entity.Business;
import com.gst.responseDto.BusinessResponse;
import com.gst.service.BusinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    // Save Business
    @PostMapping("/saveBusiness")
    public ResponseEntity<BusinessResponse> saveBusiness(
            @RequestBody Business business,
            Authentication authentication) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(businessService.saveBusiness(
                        business,
                        authentication.getName()
                ));
    }

    @GetMapping("/getBusiness")
    public ResponseEntity<BusinessResponse> getBusiness(
            Authentication authentication) {

        return ResponseEntity.ok(
                businessService.getBusiness(authentication.getName())
        );
    }

    @PutMapping("/updateBusiness")
    public ResponseEntity<BusinessResponse> updateBusiness(
            @RequestBody Business business,
            Authentication authentication) {

        return ResponseEntity.ok(
                businessService.updateBusiness(
                        business,
                        authentication.getName()
                )
        );
    }
}