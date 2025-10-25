package com.study.redisstudy.domain.sortedSet.controller;

import com.study.redisstudy.domain.sortedSet.model.SortedSet;
import com.study.redisstudy.domain.sortedSet.model.request.SortedSetRequest;
import com.study.redisstudy.service.RedisSortedSet;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Tag(name = "sorted set", description = "sorted set api")
@RestController
@RequestMapping("/api/v1/sorted-set")
@RequiredArgsConstructor
public class SortedSetController {

    private final RedisSortedSet redis;

    @PostMapping(value = "/sorted-set-collection")
    public void setSortedSet(
            @RequestBody @Valid SortedSetRequest req
    ) {
        redis.setSortedSet(req);
    }

    @GetMapping(value = "/get-sorted-set-collection")
    public Set<SortedSet> getSortedSet(
            @RequestParam @Valid String key,
            @RequestParam @Valid Float min,
            @RequestParam @Valid Float max
    ) {
        return redis.getSetDataByRange(key, min, max);
    }

    @GetMapping(value = "/get-sorted-set-by-top")
    public List<SortedSet> getTopN(
            @RequestParam @Valid String key,
            @RequestParam @Valid Integer n
    ) {
        return redis.getTopN(key, n);
    }




}
