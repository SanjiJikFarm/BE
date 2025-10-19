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
            s.id, s.shopName, s.shopImage, s.address,
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
            s.id, s.shopName, s.shopImage, s.address,
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
            s.id, s.shopName, s.shopImage, s.address,
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

    // 지도 초기 진입용(좌표 있는 매장만, id 오름차순)
    @Query("""
        SELECT new com.example.SanjiBE.dto.ShopMapResponse(
            s.id, s.shopName, s.shopImage, s.address,
            COALESCE(AVG(r.rating * 1.0), 0.0),
            COALESCE(COUNT(DISTINCT r.id), 0L),
            s.latitude, s.longitude
        )
        FROM Shop s
        LEFT JOIN Product p ON p.shop = s
        LEFT JOIN Review r ON r.product = p
        WHERE s.latitude IS NOT NULL AND s.longitude IS NOT NULL
        GROUP BY s.id, s.shopName, s.shopImage, s.address, s.latitude, s.longitude
        ORDER BY s.id ASC
        """)
    List<ShopMapResponse> findAllForMapWithStats();

    // 지도 초기: 평점순(동점 시 리뷰수, 다음 id)
    @Query("""
        SELECT new com.example.SanjiBE.dto.ShopMapResponse(
            s.id, s.shopName, s.shopImage, s.address,
            COALESCE(AVG(r.rating * 1.0), 0.0),
            COALESCE(COUNT(DISTINCT r.id), 0L),
            s.latitude, s.longitude
        )
        FROM Shop s
        LEFT JOIN Product p ON p.shop = s
        LEFT JOIN Review r ON r.product = p
        WHERE s.latitude IS NOT NULL AND s.longitude IS NOT NULL
        GROUP BY s.id, s.shopName, s.shopImage, s.address, s.latitude, s.longitude
        ORDER BY COALESCE(AVG(r.rating * 1.0), 0.0) DESC,
                 COALESCE(COUNT(DISTINCT r.id), 0L) DESC,
                 s.id ASC
        """)
    List<ShopMapResponse> findAllForMapOrderByRating();

    // 근처: 거리순(JPQL + FUNCTION 하버사인)
    @Query("""
        SELECT new com.example.SanjiBE.dto.ShopMapResponse(
            s.id, s.shopName, s.shopImage, s.address,
            COALESCE(AVG(r.rating * 1.0), 0.0),
            COALESCE(COUNT(DISTINCT r.id), 0L),
            s.latitude, s.longitude,
            (6371 *
              FUNCTION('acos',
                FUNCTION('cos', FUNCTION('radians', :lat)) *
                FUNCTION('cos', FUNCTION('radians', s.latitude)) *
                FUNCTION('cos', FUNCTION('radians', s.longitude) - FUNCTION('radians', :lng)) +
                FUNCTION('sin', FUNCTION('radians', :lat)) *
                FUNCTION('sin', FUNCTION('radians', s.latitude))
              )
            )
        )
        FROM Shop s
        LEFT JOIN Product p ON p.shop = s
        LEFT JOIN Review r ON r.product = p
        WHERE s.latitude IS NOT NULL AND s.longitude IS NOT NULL
          AND (
              :keyword IS NULL OR :keyword = '' OR
              LOWER(s.shopName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
              LOWER(s.address)  LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        GROUP BY s.id, s.shopName, s.shopImage, s.address, s.latitude, s.longitude
        HAVING (:radiusKm IS NULL OR :radiusKm <= 0 OR
                (6371 *
                  FUNCTION('acos',
                    FUNCTION('cos', FUNCTION('radians', :lat)) *
                    FUNCTION('cos', FUNCTION('radians', s.latitude)) *
                    FUNCTION('cos', FUNCTION('radians', s.longitude) - FUNCTION('radians', :lng)) +
                    FUNCTION('sin', FUNCTION('radians', :lat)) *
                    FUNCTION('sin', FUNCTION('radians', s.latitude))
                  )
                ) <= :radiusKm)
        ORDER BY
            (6371 *
              FUNCTION('acos',
                FUNCTION('cos', FUNCTION('radians', :lat)) *
                FUNCTION('cos', FUNCTION('radians', s.latitude)) *
                FUNCTION('cos', FUNCTION('radians', s.longitude) - FUNCTION('radians', :lng)) +
                FUNCTION('sin', FUNCTION('radians', :lat)) *
                FUNCTION('sin', FUNCTION('radians', s.latitude))
              )
            ) ASC
        """)
    List<ShopMapResponse> findNearbyForMapDistance(@Param("lat") double lat,
                                                   @Param("lng") double lng,
                                                   @Param("radiusKm") Double radiusKm,
                                                   @Param("keyword") String keyword);

    // 근처: 평점순(동점 시 리뷰수, 그다음 거리)
    @Query("""
        SELECT new com.example.SanjiBE.dto.ShopMapResponse(
            s.id, s.shopName, s.shopImage, s.address,
            COALESCE(AVG(r.rating * 1.0), 0.0),
            COALESCE(COUNT(DISTINCT r.id), 0L),
            s.latitude, s.longitude,
            (6371 *
              FUNCTION('acos',
                FUNCTION('cos', FUNCTION('radians', :lat)) *
                FUNCTION('cos', FUNCTION('radians', s.latitude)) *
                FUNCTION('cos', FUNCTION('radians', s.longitude) - FUNCTION('radians', :lng)) +
                FUNCTION('sin', FUNCTION('radians', :lat)) *
                FUNCTION('sin', FUNCTION('radians', s.latitude))
              )
            )
        )
        FROM Shop s
        LEFT JOIN Product p ON p.shop = s
        LEFT JOIN Review r ON r.product = p
        WHERE s.latitude IS NOT NULL AND s.longitude IS NOT NULL
          AND (
              :keyword IS NULL OR :keyword = '' OR
              LOWER(s.shopName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
              LOWER(s.address)  LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        GROUP BY s.id, s.shopName, s.shopImage, s.address, s.latitude, s.longitude
        HAVING (:radiusKm IS NULL OR :radiusKm <= 0 OR
                (6371 *
                  FUNCTION('acos',
                    FUNCTION('cos', FUNCTION('radians', :lat)) *
                    FUNCTION('cos', FUNCTION('radians', s.latitude)) *
                    FUNCTION('cos', FUNCTION('radians', s.longitude) - FUNCTION('radians', :lng)) +
                    FUNCTION('sin', FUNCTION('radians', :lat)) *
                    FUNCTION('sin', FUNCTION('radians', s.latitude))
                  )
                ) <= :radiusKm)
        ORDER BY
            COALESCE(AVG(r.rating * 1.0), 0.0) DESC,
            COALESCE(COUNT(DISTINCT r.id), 0L) DESC,
            (6371 *
              FUNCTION('acos',
                FUNCTION('cos', FUNCTION('radians', :lat)) *
                FUNCTION('cos', FUNCTION('radians', s.latitude)) *
                FUNCTION('cos', FUNCTION('radians', s.longitude) - FUNCTION('radians', :lng)) +
                FUNCTION('sin', FUNCTION('radians', :lat)) *
                FUNCTION('sin', FUNCTION('radians', s.latitude))
              )
            ) ASC
        """)
    List<ShopMapResponse> findNearbyForMapOrderByRating(@Param("lat") double lat,
                                                        @Param("lng") double lng,
                                                        @Param("radiusKm") Double radiusKm,
                                                        @Param("keyword") String keyword);
}
