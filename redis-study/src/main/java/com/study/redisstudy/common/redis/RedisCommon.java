package com.study.redisstudy.common.redis;

import com.google.gson.Gson;
import com.study.redisstudy.domain.stategy.model.ValueWithTTL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisCommon {

    private final RedisTemplate<String, String> template;
    // 직렬화 관련
    private final Gson gson;

    @Value("${spring.data.redis.default-time}")
    private Duration defaultExpireTime;

    // redis에서 값을 가져오는 함수
    public <T> T getData(String key, Class<T> clazz) {
        String jsonValue = template.opsForValue().get(key);
        if (jsonValue == null) {
            return null;
        }
        return gson.fromJson(jsonValue, clazz);
    }

    // 데이터 저장
    public <T> void setData(String key, T value) {
        // 직렬화
        String jsonValue = gson.toJson(value);
        template.opsForValue().set(key, jsonValue);
        template.expire(key, defaultExpireTime);
    }

    // 다중 데이터 저장
    public <T> void multiSetData(Map<String, T> datas) {
        Map<String, String> jsonMap = new HashMap<>();

        for (Map.Entry<String, T> entry : datas.entrySet()) {
            jsonMap.put(entry.getKey(), gson.toJson(entry.getValue()));
        }

        template.opsForValue().multiSet(jsonMap);
    }

    // sortedSet 데이터 저장
    public <T> void addSortedSet(String key, T value, Float score) {
        String jsonValue = gson.toJson(value);
        template.opsForZSet().add(key, jsonValue, score);
    }

    // score 기반으로 데이터 조회
    public <T> Set<T> rangeByScore(String key, Float minScore, Float maxScore, Class<T> clazz) {
        Set<String> jsonValues = template.opsForZSet().rangeByScore(key, minScore, maxScore);
        Set<T> resultSet = new HashSet<>();

        if (jsonValues != null) {
            for (String jsonValue : jsonValues) {
                T v = gson.fromJson(jsonValue, clazz);
                resultSet.add(v);
            }
        }

        return resultSet;
    }

    // sortedSet으로 랭킹을 구하기 위한 함수
    public <T> List<T> getTopNFromSortedSet(String key, int n, Class<T> clazz) {
        Set<String> jsonValues = template.opsForZSet().range(key, 0, n - 1);
        List<T> resultSet = new ArrayList<>();
        if (jsonValues != null) {
            for (String jsonValue : jsonValues) {
                T v = gson.fromJson(jsonValue, clazz);
                resultSet.add(v);
            }
        }

        return resultSet;
    }

    // 리스트 타입 데이터 저장(left)
    public <T> void addListLeft(String key, T value) {
        String jsonValue = gson.toJson(value);
        template.opsForList().leftPush(key, jsonValue);
    }

    // 리스트 타입 데이터 저장(right)
    public <T> void addListRight(String key, T value) {
        String jsonValue = gson.toJson(value);
        template.opsForList().rightPush(key, jsonValue);
    }

    // 리스틑 조회
    public <T> List<T> getAllList(String key, Class<T> clazz) {
        // 모든 값 조회(-1)
        List<String> jsonValues = template.opsForList().range(key, 0, -1);
        List<T> resultSet = new ArrayList<>();

        if (jsonValues != null) {
            for (String jsonValue : jsonValues) {
                T value = gson.fromJson(jsonValue, clazz);
                resultSet.add(value);
            }
        }

        return resultSet;
    }

    // 리스트 삭제
    public <T> void deleteFromList(String key, T value) {
        String jsonValue = gson.toJson(value);
        template.opsForList().remove(key, 1, jsonValue);
    }

    // 해시 데이터 저장
    public <T> void pushHash(String key, String field, T value) {
        String jsonValue = gson.toJson(value);
        template.opsForHash().put(key, field, jsonValue);
    }

    // 해시 조회
    public <T> T getFromHash(String key, String field, Class<T> clazz) {
        Object result = template.opsForHash().get(key, field);

        if (result != null) {
            return gson.fromJson(result.toString(), clazz);
        }

        return null;
    }

    // 해시 삭제
    public void deleteFromHash(String key, String field) {
        template.opsForHash().delete(key, field);
    }

    public void setBit(String key, long offset, boolean value) {
        template.opsForValue().setBit(key, offset, value);
    }

    public boolean getBit(String key, long offset) {
        return template.opsForValue().getBit(key, offset);
    }

    public <T> ValueWithTTL<T> getValueWithTTL(String key, Class<T> clazz) {
        T value = null;
        Long ttl = null;
        try {
            List<Object> results = template.executePipelined((RedisCallback<Object>) connection -> {
                connection.openPipeline();

                StringRedisConnection conn = (StringRedisConnection) connection;

                conn.get(key);
                conn.pTtl(key);

                connection.closePipeline();

                return null;
            });

            value = (T) gson.fromJson((String) results.get(0), clazz);
            ttl = (Long) results.get(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ValueWithTTL<T>(value, ttl);
    }

    public Long sumTowKeyAndRenew(String script, String key1, String key2, String resultKey) {
        return template.execute((RedisCallback<Long>) connection -> {

            byte[] scriptBytes = script.getBytes();
            byte[] key1Bytes = key1.getBytes();
            byte[] key2Bytes = key2.getBytes();
            byte[] resultKeyBytes = resultKey.getBytes();

            return (Long) connection.execute("EVAL",
                    scriptBytes,
                    key1Bytes,
                    key2Bytes,
                    resultKeyBytes
            );
        });
    }


}
