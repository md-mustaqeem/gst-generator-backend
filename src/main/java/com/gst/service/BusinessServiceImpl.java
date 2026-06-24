package com.gst.service;

import com.gst.entity.Business;
import com.gst.entity.User;
import com.gst.repository.BusinessRepository;
import com.gst.repository.UserRepository;
import com.gst.responseDto.BusinessResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessServiceImpl implements BusinessService {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    @Override
    public BusinessResponse saveBusiness(Business business, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found"));

        business.setUser(user);

        Business saved = businessRepository.save(business);

        return mapToResponse(saved);
    }

    @Override
    public BusinessResponse getBusiness(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found"));

        Business business = businessRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Business Not Found"));

        return mapToResponse(business);
    }

    @Override
    public BusinessResponse updateBusiness(Business business, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found"));

        Business existing = businessRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Business Not Found"));

        existing.setName(business.getName());
        existing.setGst(business.getGst());
        existing.setPhone(business.getPhone());
        existing.setEmail(business.getEmail());
        existing.setAddress(business.getAddress());

        Business updated = businessRepository.save(existing);

        return mapToResponse(updated);
    }

    private BusinessResponse mapToResponse(Business business) {

        return BusinessResponse.builder()
                .id(business.getId())
                .name(business.getName())
                .gst(business.getGst())
                .phone(business.getPhone())
                .email(business.getEmail())
                .address(business.getAddress())
                .build();
    }
}