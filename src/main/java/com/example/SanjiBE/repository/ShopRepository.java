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

    // 검색(리스트)
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

    // 전체 목록(리스트)
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

    // 단일 조회(리스트)
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

    // 지도 초기(좌표 있는 매장만)
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

    // 지도 초기: 평점순
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

    /*
     * 아래 3개 쿼리(거리 계산)는 Hibernate 6에서 FUNCTION(...)의 반환이 Object로 추론되어
     * 산술 연산 시 타입검증 오류가 발생하므로, 모든 FUNCTION 호출에 대해 CAST(... AS double)로
     * 숫자 타입을 명시한다. 또한 빼기 연산 피연산자도 double로 캐스팅한다.
     */

    // 근처: 거리순
    @Query("""
        SELECT new com.example.SanjiBE.dto.ShopMapResponse(
            s.id, s.shopName, s.shopImage, s.address,
            COALESCE(AVG(r.rating * 1.0), 0.0),
            COALESCE(COUNT(DISTINCT r.id), 0L),
            s.latitude, s.longitude,
            (6371.0 * CAST(
              FUNCTION('acos',
                CAST(FUNCTION('cos', CAST(FUNCTION('radians', :lat) AS double)) AS double) *
                CAST(FUNCTION('cos', CAST(FUNCTION('radians', s.latitude) AS double)) AS double) *
                CAST(FUNCTION('cos',
                      CAST(
                        (CAST(FUNCTION('radians', s.longitude) AS double) - CAST(FUNCTION('radians', :lng) AS double))
                      AS double)
                ) AS double) +
                CAST(FUNCTION('sin', CAST(FUNCTION('radians', :lat) AS double)) AS double) *
                CAST(FUNCTION('sin', CAST(FUNCTION('radians', s.latitude) AS double)) AS double)
              )
            AS double))
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
                (6371.0 * CAST(
                  FUNCTION('acos',
                    CAST(FUNCTION('cos', CAST(FUNCTION('radians', :lat) AS double)) AS double) *
                    CAST(FUNCTION('cos', CAST(FUNCTION('radians', s.latitude) AS double)) AS double) *
                    CAST(FUNCTION('cos',
                          CAST(
                            (CAST(FUNCTION('radians', s.longitude) AS double) - CAST(FUNCTION('radians', :lng) AS double))
                          AS double)
                    ) AS double) +
                    CAST(FUNCTION('sin', CAST(FUNCTION('radians', :lat) AS double)) AS double) *
                    CAST(FUNCTION('sin', CAST(FUNCTION('radians', s.latitude) AS double)) AS double)
                  )
                AS double)) <= :radiusKm)
        ORDER BY
            (6371.0 * CAST(
              FUNCTION('acos',
                CAST(FUNCTION('cos', CAST(FUNCTION('radians', :lat) AS double)) AS double) *
                CAST(FUNCTION('cos', CAST(FUNCTION('radians', s.latitude) AS double)) AS double) *
                CAST(FUNCTION('cos',
                      CAST(
                        (CAST(FUNCTION('radians', s.longitude) AS double) - CAST(FUNCTION('radians', :lng) AS double))
                      AS double)
                ) AS double) +
                CAST(FUNCTION('sin', CAST(FUNCTION('radians', :lat) AS double)) AS double) *
                CAST(FUNCTION('sin', CAST(FUNCTION('radians', s.latitude) AS double)) AS double)
              )
            AS double)) ASC
        """)
    List<ShopMapResponse> findNearbyForMapDistance(@Param("lat") double lat,
                                                   @Param("lng") double lng,
                                                   @Param("radiusKm") Double radiusKm,
                                                   @Param("keyword") String keyword);

    // 근처: 평점순 (동점 시 리뷰수, 다음 거리)
    @Query("""
        SELECT new com.example.SanjiBE.dto.ShopMapResponse(
            s.id, s.shopName, s.shopImage, s.address,
            COALESCE(AVG(r.rating * 1.0), 0.0),
            COALESCE(COUNT(DISTINCT r.id), 0L),
            s.latitude, s.longitude,
            (6371.0 * CAST(
              FUNCTION('acos',
                CAST(FUNCTION('cos', CAST(FUNCTION('radians', :lat) AS double)) AS double) *
                CAST(FUNCTION('cos', CAST(FUNCTION('radians', s.latitude) AS double)) AS double) *
                CAST(FUNCTION('cos',
                      CAST(
                        (CAST(FUNCTION('radians', s.longitude) AS double) - CAST(FUNCTION('radians', :lng) AS double))
                      AS double)
                ) AS double) +
                CAST(FUNCTION('sin', CAST(FUNCTION('radians', :lat) AS double)) AS double) *
                CAST(FUNCTION('sin', CAST(FUNCTION('radians', s.latitude) AS double)) AS double)
              )
            AS double))
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
                (6371.0 * CAST(
                  FUNCTION('acos',
                    CAST(FUNCTION('cos', CAST(FUNCTION('radians', :lat) AS double)) AS double) *
                    CAST(FUNCTION('cos', CAST(FUNCTION('radians', s.latitude) AS double)) AS double) *
                    CAST(FUNCTION('cos',
                          CAST(
                            (CAST(FUNCTION('radians', s.longitude) AS double) - CAST(FUNCTION('radians', :lng) AS double))
                          AS double)
                    ) AS double) +
                    CAST(FUNCTION('sin', CAST(FUNCTION('radians', :lat) AS double)) AS double) *
                    CAST(FUNCTION('sin', CAST(FUNCTION('radians', s.latitude) AS double)) AS double)
                  )
                AS double)) <= :radiusKm)
        ORDER BY
            COALESCE(AVG(r.rating * 1.0), 0.0) DESC,
            COALESCE(COUNT(DISTINCT r.id), 0L) DESC,
            (6371.0 * CAST(
              FUNCTION('acos',
                CAST(FUNCTION('cos', CAST(FUNCTION('radians', :lat) AS double)) AS double) *
                CAST(FUNCTION('cos', CAST(FUNCTION('radians', s.latitude) AS double)) AS double) *
                CAST(FUNCTION('cos',
                      CAST(
                        (CAST(FUNCTION('radians', s.longitude) AS double) - CAST(FUNCTION('radians', :lng) AS double))
                      AS double)
                ) AS double) +
                CAST(FUNCTION('sin', CAST(FUNCTION('radians', :lat) AS double)) AS double) *
                CAST(FUNCTION('sin', CAST(FUNCTION('radians', s.latitude) AS double)) AS double)
              )
            AS double)) ASC
        """)
    List<ShopMapResponse> findNearbyForMapOrderByRating(@Param("lat") double lat,
                                                        @Param("lng") double lng,
                                                        @Param("radiusKm") Double radiusKm,
                                                        @Param("keyword") String keyword);

    // 지도용 검색 + 평점순
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
          AND (
              COALESCE(:keyword, '') = '' OR
              LOWER(s.shopName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
              LOWER(s.address)  LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        GROUP BY s.id, s.shopName, s.shopImage, s.address, s.latitude, s.longitude
        ORDER BY COALESCE(AVG(r.rating * 1.0), 0.0) DESC,
                 COALESCE(COUNT(DISTINCT r.id), 0L) DESC,
                 s.id ASC
        """)
    List<ShopMapResponse> searchForMapOrderByRating(@Param("keyword") String keyword);

    // 지도용 검색 + 거리순
    @Query("""
        SELECT new com.example.SanjiBE.dto.ShopMapResponse(
            s.id, s.shopName, s.shopImage, s.address,
            COALESCE(AVG(r.rating * 1.0), 0.0),
            COALESCE(COUNT(DISTINCT r.id), 0L),
            s.latitude, s.longitude,
            (6371.0 * CAST(
              FUNCTION('acos',
                CAST(FUNCTION('cos', CAST(FUNCTION('radians', :lat) AS double)) AS double) *
                CAST(FUNCTION('cos', CAST(FUNCTION('radians', s.latitude) AS double)) AS double) *
                CAST(FUNCTION('cos',
                      CAST(
                        (CAST(FUNCTION('radians', s.longitude) AS double) - CAST(FUNCTION('radians', :lng) AS double))
                      AS double)
                ) AS double) +
                CAST(FUNCTION('sin', CAST(FUNCTION('radians', :lat) AS double)) AS double) *
                CAST(FUNCTION('sin', CAST(FUNCTION('radians', s.latitude) AS double)) AS double)
              )
            AS double))
        )
        FROM Shop s
        LEFT JOIN Product p ON p.shop = s
        LEFT JOIN Review r ON r.product = p
        WHERE s.latitude IS NOT NULL AND s.longitude IS NOT NULL
          AND (
              COALESCE(:keyword, '') = '' OR
              LOWER(s.shopName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
              LOWER(s.address)  LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        GROUP BY s.id, s.shopName, s.shopImage, s.address, s.latitude, s.longitude
        HAVING (:radiusKm IS NULL OR :radiusKm <= 0 OR
                (6371.0 * CAST(
                  FUNCTION('acos',
                    CAST(FUNCTION('cos', CAST(FUNCTION('radians', :lat) AS double)) AS double) *
                    CAST(FUNCTION('cos', CAST(FUNCTION('radians', s.latitude) AS double)) AS double) *
                    CAST(FUNCTION('cos',
                          CAST(
                            (CAST(FUNCTION('radians', s.longitude) AS double) - CAST(FUNCTION('radians', :lng) AS double))
                          AS double)
                    ) AS double) +
                    CAST(FUNCTION('sin', CAST(FUNCTION('radians', :lat) AS double)) AS double) *
                    CAST(FUNCTION('sin', CAST(FUNCTION('radians', s.latitude) AS double)) AS double)
                  )
                AS double)) <= :radiusKm)
        ORDER BY
            (6371.0 * CAST(
              FUNCTION('acos',
                CAST(FUNCTION('cos', CAST(FUNCTION('radians', :lat) AS double)) AS double) *
                CAST(FUNCTION('cos', CAST(FUNCTION('radians', s.latitude) AS double)) AS double) *
                CAST(FUNCTION('cos',
                      CAST(
                        (CAST(FUNCTION('radians', s.longitude) AS double) - CAST(FUNCTION('radians', :lng) AS double))
                      AS double)
                ) AS double) +
                CAST(FUNCTION('sin', CAST(FUNCTION('radians', :lat) AS double)) AS double) *
                CAST(FUNCTION('sin', CAST(FUNCTION('radians', s.latitude) AS double)) AS double)
              )
            AS double)) ASC
        """)
    List<ShopMapResponse> searchForMapOrderByDistance(@Param("keyword") String keyword,
                                                      @Param("lat") double lat,
                                                      @Param("lng") double lng,
                                                      @Param("radiusKm") Double radiusKm);
}
