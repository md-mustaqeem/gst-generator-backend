package com.gst.repository;

import com.gst.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByPhoneAndUserId(String phone, Long userId);

    long countByUserId(Long userId);

    // Get all customers of logged-in user
    List<Customer> findByUserIdOrderByIdDesc(Long userId);

    // Get customer by id and user
    Optional<Customer> findByIdAndUserId(Long id, Long userId);

}