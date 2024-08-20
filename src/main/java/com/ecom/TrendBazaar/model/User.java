package com.ecom.TrendBazaar.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String mobileNumber;
    private String email;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String password;
    private String image;
    private String role;
    private String confirmPassword;
    private Boolean isEnable;
    private Boolean accountNonLocked;
    private int failedAttempts;
    private Date lockTime;
    private String resetToken;

}
