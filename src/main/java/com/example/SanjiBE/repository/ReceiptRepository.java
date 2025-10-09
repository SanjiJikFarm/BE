package com.example.SanjiBE.repository;

import com.example.SanjiBE.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    boolean existsByUserAndShopAndReceiptDateAndTotalPrice(
            com.example.SanjiBE.entity.User user,
            com.example.SanjiBE.entity.Shop shop,
            java.time.LocalDate receiptDate,
            int totalPrice
    );

    List<Receipt> findByUser(com.example.SanjiBE.entity.User user);

    @Query("SELECT DISTINCT r FROM Receipt r " +
            "JOIN FETCH r.purchases p " +
            "JOIN FETCH p.product prod " +
            "JOIN FETCH r.shop s " +
            "WHERE r.user.id = :userId " +
            "AND YEAR(r.receiptDate) = :year " +
            "AND MONTH(r.receiptDate) = :month")
    List<Receipt> findWithPurchasesByUserAndYearMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );
}
