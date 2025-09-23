package com.example.SanjiBE.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "product_num")
    private String productNum;

    @Column(name = "product_price", nullable = false)
    private int productPrice;

    @Column(name = "product_like")
    private int productLike;

    @Column(name = "product_image")
    private String productImage;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;
}

