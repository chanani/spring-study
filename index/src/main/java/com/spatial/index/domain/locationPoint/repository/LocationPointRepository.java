package com.spatial.index.domain.locationPoint.repository;

import com.spatial.index.domain.locationPoint.entity.LocationPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocationPointRepository extends JpaRepository<LocationPoint, Long> {

    @Query(value = """
            SELECT lp.id            AS id,
                   lp.name          AS name,
                   ST_Y(lp.location) AS lat,
                   ST_X(lp.location) AS lng
            FROM location_point lp
            WHERE MBRContains(
                      ST_GeomFromText(
                          CONCAT('POLYGON((',
                              :swLng, ' ', :swLat, ',',
                              :neLng, ' ', :swLat, ',',
                              :neLng, ' ', :neLat, ',',
                              :swLng, ' ', :neLat, ',',
                              :swLng, ' ', :swLat, '))'),
                          4326, 'axis-order=long-lat'),
                      lp.location)
            ORDER BY lp.id
            LIMIT :limit
            """, nativeQuery = true)
    List<LocationPointProjection> findInBounds(@Param("swLat") double swLat,
                                               @Param("neLat") double neLat,
                                               @Param("swLng") double swLng,
                                               @Param("neLng") double neLng,
                                               @Param("limit") int limit);
}
