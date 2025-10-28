package com.study.redisstudy.service;

import com.study.redisstudy.common.redis.RedisCommon;
import com.study.redisstudy.domain.stategy.model.ValueWithTTL;
import com.study.redisstudy.domain.string.model.StringModel;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class RedisStrategyService {

    private final RedisCommon redis;

    public StringModel simpleStrategy(String key) {
        StringModel model = redis.getData(key, StringModel.class);

        if (model == null) {
            // DB를 조회한 값이라고 가정
            StringModel fromDBData = new StringModel(key, "new db");

            redis.setData(key, fromDBData);

            return fromDBData;
        }

        return model;
    }

    public StringModel PERStrategy(String key) {
        ValueWithTTL<StringModel> valueWIthTTL = redis.getValueWithTTL(key, StringModel.class);

        if (valueWIthTTL != null) {
            AsyncPERStrategy(key, valueWIthTTL.getTtl());

            return valueWIthTTL.getValue();
        }

        StringModel fromDBData = new StringModel(key, "new db");

        redis.setData(key, fromDBData);

        return fromDBData;
    }

    @Async
    protected void AsyncPERStrategy(String key, Long remainTTL) {

        double probability = calculateProbability(remainTTL);

        Random random = new Random();

        if (random.nextDouble() <= probability) {
            StringModel fromDB = new StringModel(key, "db from");
            redis.setData(key, fromDB);
        }
    }

    // 확률 계산
    public double calculateProbability(Long remainTTL) {
        double base = 0.5;
        double decayRate = 0.1; // 감지율

        return base * Math.pow(Math.E, -decayRate * remainTTL);
    }

    public void lauScript(String key1, String key2, String newKey) {
        redis.sumTowKeyAndRenew(key1, key2, newKey);
    }

}
