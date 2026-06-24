package com.gst.responseDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessResponse {

    private Long id;

    private String name;

    private String gst;

    private String phone;

    private String email;

    private String address;
}