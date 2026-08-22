package com.spatial.index.application.locationPoint;

import com.spatial.index.application.dto.location.request.BoundsRequest;
import com.spatial.index.application.dto.location.response.LocationsResponse;
import com.spatial.index.domain.locationPoint.entity.LocationPoint;
import com.spatial.index.domain.locationPoint.repository.LocationPointProjection;
import com.spatial.index.domain.locationPoint.repository.LocationPointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationPointService {
    private static final int MAX_LIMIT = 2000;

    private final LocationPointRepository locationPointRepository;

    public List<LocationsResponse> findInBounds(BoundsRequest request) {

        long t0 = System.nanoTime();

        int limit = Math.min(request.limit(), MAX_LIMIT);

        List<LocationPointProjection> rows = locationPointRepository.findInBounds(
                request.swLat(), request.neLat(),
                request.swLng(), request.neLng(),
                limit
        );

        long tFetched = System.nanoTime();

        List<LocationsResponse> result = rows.stream()
                .map(r -> new LocationsResponse(
                        r.getId(),
                        r.getName(),
                        BigDecimal.valueOf(r.getLat()).setScale(7, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(r.getLng()).setScale(7, RoundingMode.HALF_UP)))
                .toList();

        long tMapped = System.nanoTime();

        log.info("[SPATIAL]  fetched={}건 {}ms / mapped={}건 {}ms / total {}ms",
                rows.size(), ms(t0, tFetched),
                result.size(), ms(tFetched, tMapped),
                ms(t0, tMapped));

        return result;
    }

    private String ms(long from, long to) {
        return String.format("%.2f", (to - from) / 1_000_000.0);
    }
}
