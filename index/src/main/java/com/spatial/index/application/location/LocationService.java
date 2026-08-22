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
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    private static final int MAX_LIMIT = 500;

    private final LocationRepository locationRepository;

    public List<LocationsResponse> findInBounds(BoundsRequest request) {

        int limit = Math.min(request.limit(), MAX_LIMIT);
        Pageable pageable = PageRequest.of(0, limit);

        List<Location> locations = locationRepository.findInBounds(
                toDecimal(request.swLat()),
                toDecimal(request.neLat()),
                toDecimal(request.swLng()),
                toDecimal(request.neLng()),
                pageable
        );

        return locations.stream()
                .map(LocationsResponse::from)
                .toList();
    }

    private BigDecimal toDecimal(Double value) {
        return BigDecimal.valueOf(value).setScale(7, RoundingMode.HALF_UP);
    }
}
