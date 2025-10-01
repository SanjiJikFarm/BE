package com.example.SanjiBE.repository;

import com.example.SanjiBE.entity.Product;
import com.example.SanjiBE.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByShop_Id(Long shopId);

    Optional<Product> findByShopAndProductName(Shop shop, String name);
}
