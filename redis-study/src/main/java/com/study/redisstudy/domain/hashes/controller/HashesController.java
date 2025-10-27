package com.study.redisstudy.domain.hashes.controller;

import com.study.redisstudy.domain.hashes.model.HashModel;
import com.study.redisstudy.domain.hashes.model.request.HashRequest;
import com.study.redisstudy.service.RedisHash;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "hash", description = "hash api")
@RestController
@RequestMapping("/api/v1/hash")
@RequiredArgsConstructor
public class HashesController {

    private final RedisHash redis;

    @PostMapping(value = "/put-hashes")
    public void putHashes(
        @RequestBody @Valid HashRequest req
    ) {
        redis.putInHash(req.baseRequest().key(), req.filed(), req.name());
    }

    @GetMapping(value = "/get-hash-value")
    public HashModel getHashes(
            @RequestParam @Valid String key,
            @RequestParam @Valid String filed
    ) {
        return redis.getFromHash(key, filed);
    }


}
