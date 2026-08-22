package com.spatial.index.application.location;

import com.spatial.index.application.dto.location.request.BoundsRequest;
import com.spatial.index.application.dto.location.response.LocationsResponse;
import com.spatial.index.domain.location.entity.Location;
import com.spatial.index.domain.location.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    private static final int MAX_LIMIT = 2000;
    private static final double EARTH_RADIUS_M = 6_371_008.8;

    private final LocationRepository locationRepository;

    public List<LocationsResponse> findInBounds(BoundsRequest request) {

        long t0 = System.nanoTime();

        int limit = Math.min(request.limit(), MAX_LIMIT);
        Pageable pageable = PageRequest.of(0, limit);

        List<Location> locations = locationRepository.findInBounds(
                toDecimal(request.swLat()),
                toDecimal(request.neLat()),
                toDecimal(request.swLng()),
                toDecimal(request.neLng()),
                pageable
        );
        long tFetched = System.nanoTime();

        List<LocationsResponse> result = locations.stream()
                .map(LocationsResponse::from)
                .toList();

        long tMapped = System.nanoTime();

        log.info("[BBOX]     fetched={}건 {}ms / mapped={}건 {}ms / total {}ms",
                locations.size(), ms(t0, tFetched),
                result.size(), ms(tFetched, tMapped),
                ms(t0, tMapped));

        return result;
    }

    private BigDecimal toDecimal(Double value) {
        return BigDecimal.valueOf(value).setScale(7, RoundingMode.HALF_UP);
    }

    public List<LocationsResponse> findAllFullScan(BoundsRequest request) {

        long t0 = System.nanoTime();

        List<Location> all = locationRepository.findAllForFullScan();

        long tFetched = System.nanoTime();

        double centerLat = (request.swLat() + request.neLat()) / 2;
        double centerLng = (request.swLng() + request.neLng()) / 2;
        double radiusM = haversineMeters(
                request.swLat(), request.swLng(),
                request.neLat(), request.neLng()) / 2;

        List<LocationsResponse> result = new ArrayList<>();
        for (Location l : all) {
            double lat = l.getLat().doubleValue();
            double lng = l.getLng().doubleValue();

            if (haversineMeters(centerLat, centerLng, lat, lng) <= radiusM) {
                result.add(LocationsResponse.from(l));
            }
            if (result.size() >= request.limit()) {
                break;
            }
        }

        long tFiltered = System.nanoTime();

        log.info("[FULLSCAN] fetched={}건 {}ms / matched={}건 {}ms / total {}ms",
                all.size(), ms(t0, tFetched),
                result.size(), ms(tFetched, tFiltered),
                ms(t0, tFiltered));

        return result;
    }

    private double haversineMeters(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return EARTH_RADIUS_M * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private String ms(long from, long to) {
        return String.format("%.2f", (to - from) / 1_000_000.0);
    }

}
