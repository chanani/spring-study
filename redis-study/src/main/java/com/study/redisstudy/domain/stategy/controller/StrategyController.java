package com.study.redisstudy.domain.stategy.controller;

import com.study.redisstudy.common.redis.RedisCommon;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "strategy", description = "strategy api")
@RestController
@RequestMapping("/api/v1/strategy")
@RequiredArgsConstructor
public class StrategyController {

    private final RedisCommon redis;
}
