package com.study.redisstudy.service;

import com.study.redisstudy.common.redis.RedisCommon;
import com.study.redisstudy.domain.string.model.StringModel;
import com.study.redisstudy.domain.string.model.request.MultiStringRequest;
import com.study.redisstudy.domain.string.model.request.StringRequest;
import com.study.redisstudy.domain.string.model.response.StringResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RedisString {

    private final RedisCommon redis;

    public void set(StringRequest req) {
        String key = req.baseRequest().key();
        StringModel newModel = new StringModel(key, req.name());

        redis.setData(key, newModel);
    }

    public StringResponse get(String key) {
        StringModel result = redis.getData(key, StringModel.class);
        List<StringModel> red = new ArrayList<StringModel>();

        if (result != null) {
            red.add(result);
        }

        return new StringResponse(red);
    }

    public void multiSet(MultiStringRequest req) {
        Map<String, Object> dataMap = new HashMap<String, Object>();

        for (int i = 0; i < req.names().length; i++) {
            String name = req.names()[i];
            String key = "key : " + (i + 1);
            StringModel newModel = new StringModel(key, name);
            dataMap.put(key, newModel);
        }

        redis.multiSetData(dataMap);
    }
}
