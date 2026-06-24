package com.gst.requestDto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequest {

    private String name;

    private String phone;

    private String email;

    private String address;
}