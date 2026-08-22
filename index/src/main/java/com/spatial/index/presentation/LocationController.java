package com.spatial.index.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LocationController {

    @GetMapping(value = "/api/v1/locations")
    public ResponseEntity<String> getLocations() {

        return ResponseEntity.ok("Hello World");
    }

}
