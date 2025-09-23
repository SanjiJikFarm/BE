package com.example.SanjiBE.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "shop")
@Getter
@Setter
@NoArgsConstructor
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long id;

    @Column(name = "shop_name", nullable = false, length = 100)
    private String shopName;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "owner_name", length = 100)
    private String ownerName;

    @Column(name = "business_num", length = 100)
    private String businessNum;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "shop_image")
    private String shopImage;

    // 리뷰와의 관계
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;
}
