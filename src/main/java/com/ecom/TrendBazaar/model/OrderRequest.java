package com.ecom.TrendBazaar.model;

import lombok.Data;

@Data
public class OrderRequest
{
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String paymentType;
}
