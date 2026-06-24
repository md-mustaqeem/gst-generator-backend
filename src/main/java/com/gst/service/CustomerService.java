package com.gst.service;

import com.gst.responseDto.CustomerResponse;

import java.util.List;

public interface CustomerService {

    List<CustomerResponse> getAllCustomers();

    CustomerResponse getCustomerById(Long id);

}