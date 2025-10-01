package com.example.SanjiBE.repository;

import com.example.SanjiBE.entity.Receipt;
import com.example.SanjiBE.entity.Shop;
import com.example.SanjiBE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    boolean existsByUserAndShopAndReceiptDateAndTotalPrice(User user, Shop shop, LocalDate receiptDate, int totalPrice);

    List<Receipt> findByUser(User user);
}

