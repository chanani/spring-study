package com.study.redisstudy.domain.string.controller;

import com.study.redisstudy.domain.string.model.request.MultiStringRequest;
import com.study.redisstudy.domain.string.model.request.StringRequest;
import com.study.redisstudy.domain.string.model.response.StringResponse;
import com.study.redisstudy.service.RedisString;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "string", description = "string api")
@RestController
@RequestMapping("/api/v1/set")
@RequiredArgsConstructor
public class StringController {

    private final RedisString redis;

    @Operation(
            summary = "set string"
    )
    @PostMapping(value = "/set-string-collection")
    public void setString(
            @RequestBody @Valid StringRequest req
    ) {
        redis.set(req);
    }

    @Operation(
            summary = "get string"
    )
    @GetMapping(value = "/get-string-collection")
    public StringResponse getString(
            @RequestParam @Valid String key
    ) {
        return redis.get(key);
    }

    @Operation(
            summary = "multi set string"
    )
    @PostMapping(value = "multi-set-collection")
    public void multiString(
            @RequestBody @Valid MultiStringRequest req
    ) {
        redis.multiSet(req);
    }
}
