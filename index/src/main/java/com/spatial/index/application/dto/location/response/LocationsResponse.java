package com.spatial.index.application.dto.location.response;

import com.spatial.index.domain.location.entity.Location;

import java.math.BigDecimal;

public record LocationsResponse(
        Long id,
        String name,
        BigDecimal lat,
        BigDecimal lng
) {

    public static LocationsResponse from(Location location) {
        return new LocationsResponse(
                location.getId(),
                location.getName(),
                location.getLat(),
                location.getLng()
        );
    }
}
