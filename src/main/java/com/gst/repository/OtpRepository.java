package com.gst.repository;

import com.gst.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpToken,Long> {

    Optional<OtpToken> findTopByEmailOrderByIdDesc(String email);

    Optional<OtpToken> findTopByMobileOrderByIdDesc(String mobile);

}