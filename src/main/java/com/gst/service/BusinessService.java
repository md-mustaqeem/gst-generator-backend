package com.gst.service;

import com.gst.entity.Business;
import com.gst.responseDto.BusinessResponse;

public interface BusinessService {

    BusinessResponse saveBusiness(Business business, String email);

    BusinessResponse getBusiness(String email);

    BusinessResponse updateBusiness(Business business, String email);
}