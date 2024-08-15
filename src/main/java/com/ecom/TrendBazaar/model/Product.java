package com.ecom.TrendBazaar.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.autoconfigure.web.WebProperties;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Product
{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    @Column(length = 500)
    private String title;
    //@Column(length = 5000)
    private String description;
    private String category;
    private Double price;
    private int stock;
    private String image;
}
