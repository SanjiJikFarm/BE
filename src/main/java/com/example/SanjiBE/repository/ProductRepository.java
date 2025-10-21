package com.example.SanjiBE.repository;

import com.example.SanjiBE.entity.Product;
import com.example.SanjiBE.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 매장별 상품 목록
    List<Product> findByShop_Id(Long shopId);

    // 동일 매장 내 상품명 중복 검사 등에 사용
    Optional<Product> findByShopAndProductName(Shop shop, String name);

}
