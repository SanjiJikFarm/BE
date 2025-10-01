package com.example.SanjiBE.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "purchase")
@Getter
@Setter
@NoArgsConstructor
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", nullable = false)
    private Receipt receipt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    private Purchase(Receipt receipt, Product product, int quantity) {
        this.receipt = receipt;
        this.product = product;
        this.quantity = quantity;
    }

    public static Purchase of(Receipt receipt, Product product, int quantity) {
        return new Purchase(receipt, product, quantity);
    }
}