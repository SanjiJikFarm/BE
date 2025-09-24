package com.example.SanjiBE.repository;


import com.example.SanjiBE.dto.ShopResponse;
import com.example.SanjiBE.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    List<Shop> findByShopNameContainingIgnoreCaseOrAddressContainingIgnoreCase(String name, String address);

    // 검색
    @Query("""
        SELECT new com.example.SanjiBE.dto.ShopResponse(
            s.id,
            s.shopName,
            s.shopImage,
            s.address,
            COALESCE(AVG(r.rating * 1.0), 0.0),
            COALESCE(COUNT(DISTINCT r.id), 0L)
        )
        FROM Shop s
        LEFT JOIN Product p ON p.shop = s
        LEFT JOIN Review r ON r.product = p
        WHERE (COALESCE(:keyword, '') = ''
            OR LOWER(s.shopName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(s.address)  LIKE LOWER(CONCAT('%', :keyword, '%')))
        GROUP BY s.id, s.shopName, s.shopImage, s.address
        ORDER BY s.id ASC
        """)
    List<ShopResponse> searchWithStats(@Param("keyword") String keyword);

    // 전체 목록
    @Query("""
        SELECT new com.example.SanjiBE.dto.ShopResponse(
            s.id,
            s.shopName,
            s.shopImage,
            s.address,
            COALESCE(AVG(r.rating * 1.0), 0.0),
            COALESCE(COUNT(DISTINCT r.id), 0L)
        )
        FROM Shop s
        LEFT JOIN Product p ON p.shop = s
        LEFT JOIN Review r ON r.product = p
        GROUP BY s.id, s.shopName, s.shopImage, s.address
        ORDER BY s.id ASC
        """)
    List<ShopResponse> findAllWithStats();

    // 단일 조회
    @Query("""
        SELECT new com.example.SanjiBE.dto.ShopResponse(
            s.id,
            s.shopName,
            s.shopImage,
            s.address,
            COALESCE(AVG(r.rating * 1.0), 0.0),
            COALESCE(COUNT(DISTINCT r.id), 0L)
        )
        FROM Shop s
        LEFT JOIN Product p ON p.shop = s
        LEFT JOIN Review r ON r.product = p
        WHERE s.id = :shopId
        GROUP BY s.id, s.shopName, s.shopImage, s.address
        """)
    Optional<ShopResponse> findOneWithStats(@Param("shopId") Long shopId);
}