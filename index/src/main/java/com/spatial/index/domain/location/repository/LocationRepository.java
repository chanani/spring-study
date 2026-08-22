package com.spatial.index.domain.location.repository;

import com.spatial.index.domain.location.entity.Location;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query("""
            SELECT l FROM Location l
            WHERE l.lat BETWEEN :swLat AND :neLat
              AND l.lng BETWEEN :swLng AND :neLng
            ORDER BY l.id
            """)
    List<Location> findInBounds(@Param("swLat") BigDecimal swLat,
                                @Param("neLat")BigDecimal neLat,
                                @Param("swLng")BigDecimal swLng,
                                @Param("neLng")BigDecimal neLng,
                                Pageable pageable);
}
