package com.example.SanjiBE.repository;

import com.example.SanjiBE.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    // 메서드명은 유지, 구현은 JPQL로 지정
    @Query("""
        select (count(p) > 0)
        from Purchase p
        join p.receipt r
        join r.user u
        where u.id = :userId
          and p.product.id = :productId
    """)
    boolean existsByOrder_User_IdAndProduct_Id(@Param("userId") Long userId,
                                               @Param("productId") Long productId);
}
