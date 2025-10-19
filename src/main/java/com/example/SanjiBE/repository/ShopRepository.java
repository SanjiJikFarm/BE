package com.example.SanjiBE.repository;


import com.example.SanjiBE.dto.ShopMapResponse;
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

    Optional<Shop> findByShopName(String shopName);

    // 초기 진입용: 좌표 있는 모든 매장 반환
    @Query("""
        SELECT new com.example.SanjiBE.dto.ShopMapResponse(
            s.id,
            s.shopName,
            s.shopImage,
            s.address,
            COALESCE(AVG(r.rating * 1.0), 0.0),
            COALESCE(COUNT(DISTINCT r.id), 0L),
            s.latitude,
            s.longitude
        )
        FROM Shop s
        LEFT JOIN Product p ON p.shop = s
        LEFT JOIN Review r ON r.product = p
        WHERE s.latitude IS NOT NULL AND s.longitude IS NOT NULL
        GROUP BY s.id, s.shopName, s.shopImage, s.address, s.latitude, s.longitude
        ORDER BY s.id ASC
        """)
    List<ShopMapResponse> findAllForMapWithStats();

    /**
     * 거리순: 위경도를 이용해 하버사인 거리(km) 계산.
     * MySQL Native Query 사용. 좌표 없는 매장은 제외.
     * radiusKm가 null 또는 <=0이면 반경 제한 없음.
     * keyword가 null/빈 문자열이면 필터 없음.
     */
    @Query(value = """
        SELECT
            s.shop_id        AS id,
            s.shop_name      AS shopName,
            s.shop_image     AS shopImage,
            s.address        AS address,
            COALESCE(AVG(r.rating * 1.0), 0.0) AS avgRating,
            COALESCE(COUNT(DISTINCT r.id), 0)  AS reviewCount,
            s.latitude       AS latitude,
            s.longitude      AS longitude,
            (6371 * ACOS(
                 COS(RADIANS(:lat)) * COS(RADIANS(s.latitude))
               * COS(RADIANS(s.longitude) - RADIANS(:lng))
               + SIN(RADIANS(:lat)) * SIN(RADIANS(s.latitude))
            )) AS distanceKm
        FROM shop s
        LEFT JOIN product p ON p.shop_id = s.shop_id
        LEFT JOIN review  r ON r.product_id = p.id
        WHERE s.latitude IS NOT NULL
          AND s.longitude IS NOT NULL
          AND (
              :keyword IS NULL OR :keyword = '' OR
              LOWER(s.shop_name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
              LOWER(s.address)   LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        GROUP BY s.shop_id, s.shop_name, s.shop_image, s.address, s.latitude, s.longitude
        HAVING (:radiusKm IS NULL OR :radiusKm <= 0 OR distanceKm <= :radiusKm)
        ORDER BY distanceKm ASC
        """, nativeQuery = true)
    List<Object[]> findNearbyForMapNative(@Param("lat") double lat,
                                          @Param("lng") double lng,
                                          @Param("radiusKm") Double radiusKm,
                                          @Param("keyword") String keyword);
}