package com.study.redisstudy.service;

import com.study.redisstudy.common.redis.RedisCommon;
import com.study.redisstudy.domain.sortedSet.model.SortedSet;
import com.study.redisstudy.domain.sortedSet.model.request.SortedSetRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisSortedSet {

    private final RedisCommon redis;

    public void setSortedSet(SortedSetRequest req){
        SortedSet model = new SortedSet(req.name(), req.score());
        redis.addSortedSet(req.baseRequest().key(), model, req.score());
    }

    public Set<SortedSet> getSetDataByRange(String key, Float min, Float max){
        return redis.rangeByScore(key, min, max, SortedSet.class);
    }

    public Set<SortedSet> getTopN(String key, int n){
        return redis.getTopNFromSortedSet(key, n, SortedSet.class);
    }
}
