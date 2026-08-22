package com.spatial.index.presentation;

import com.spatial.index.application.dto.location.request.BoundsRequest;
import com.spatial.index.application.dto.location.response.LocationsResponse;
import com.spatial.index.application.location.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping(value = "/api/v1/locations")
    public ResponseEntity<List<LocationsResponse>> getLocations(@ModelAttribute @Valid BoundsRequest request) {
        List<LocationsResponse> locations = locationService.findInBounds(request);
        return ResponseEntity.ok(locations);
    }

}
