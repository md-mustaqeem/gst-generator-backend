package com.gst.repository;

import com.gst.entity.Business;
import com.gst.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessRepository extends JpaRepository<Business, Long> {

    Optional<Business> findByUser(User user);

    Optional<Business> findByUserId(Long userId);

}