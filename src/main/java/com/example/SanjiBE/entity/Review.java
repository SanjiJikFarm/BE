package com.example.SanjiBE.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(name = "review_field", columnDefinition = "TEXT", nullable = false)
    private String reviewField;

    @Column(name = "review_rating", nullable = false)
    private int rating;

    @Column(name = "review_photo_url")
    private String reviewPhotoUrl;

    @Column(name = "review_like")
    private Integer reviewLike;

    // 상품 단위 리뷰
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
