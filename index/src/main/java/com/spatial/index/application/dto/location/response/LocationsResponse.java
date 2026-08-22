package com.spatial.index.application.dto.location.response;

import com.spatial.index.domain.location.entity.Location;

public record LocationsResponse(
        Long id,
        String name
) {

    public static LocationsResponse from(Location location) {
        return new LocationsResponse(location.getId(), location.getName());
    }
}
