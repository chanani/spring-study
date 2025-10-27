package com.study.redisstudy.service;

import com.study.redisstudy.common.redis.RedisCommon;
import com.study.redisstudy.domain.hashes.model.HashModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisHash {

    private final RedisCommon redis;

    public void putInHash(String key, String filed, String name){
        HashModel model = new HashModel(name);
        redis.pushHash(key, filed, model);
    }

    public HashModel getFromHash(String key, String filed){
        return redis.getFromHash(key, filed, HashModel.class);
    }

}
