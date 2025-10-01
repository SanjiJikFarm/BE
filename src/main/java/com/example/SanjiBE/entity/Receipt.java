package com.example.SanjiBE.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity

public class Receipt {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_id")
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Getter @Setter
    @Column(name = "receipt_date", nullable = false)
    private LocalDate receiptDate;

    @Getter @Setter
    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    // 영수증 - 구매
    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Purchase> purchaseList;

    private Receipt(User user, Shop shop, LocalDate receiptDate, int totalPrice) {
        this.user = user;
        this.shop = shop;
        this.receiptDate = receiptDate;
        this.totalPrice = totalPrice;
    }

    public Receipt() {

    }

    public static Receipt create(User user, Shop shop, LocalDate date, int total) {
        return new Receipt(user, shop, date, total);
    }

}
