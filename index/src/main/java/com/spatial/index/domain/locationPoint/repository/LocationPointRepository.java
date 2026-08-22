package com.spatial.index.domain.locationPoint.repository;

import com.spatial.index.domain.locationPoint.entity.LocationPoint;
import org.locationtech.jts.geom.Polygon;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocationPointRepository extends JpaRepository<LocationPoint, Long> {

    @Query(value = """
        SELECT lp.id             AS id,
               lp.name           AS name,
               ST_X(lp.location) AS lat,
               ST_Y(lp.location) AS lng
        FROM location_point lp
        WHERE MBRContains(ST_GeomFromText(:bounds, 4326), lp.location)
        """, nativeQuery = true)
    List<LocationPointProjection> findInBounds(@Param("bounds") String bounds,
                                               Pageable pageable);
}
