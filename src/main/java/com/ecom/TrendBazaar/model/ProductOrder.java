package com.ecom.TrendBazaar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductOrder
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String orderId;
    private Date orderDate;
    @ManyToOne
    private Product product;
    private Double price;
    private int quantity;
    @ManyToOne
    private User user;
    private String status;
    private String paymentType;
    @OneToOne(cascade = CascadeType.ALL)
    private BillingAddress billingAddress;
}
