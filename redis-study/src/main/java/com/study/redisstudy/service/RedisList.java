package com.study.redisstudy.service;

import com.study.redisstudy.common.redis.RedisCommon;
import com.study.redisstudy.domain.list.model.ListModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisList {

    private final RedisCommon redis;

    public void  addToListLeft(String key, String name){
        ListModel model = new ListModel(name);
        redis.addListLeft(key, model);
    }

    public void  addToListRight(String key, String name){
        ListModel model = new ListModel(name);
        redis.addListRight(key, model);
    }

    public List<ListModel> getAllList(String key){
        return redis.getAllList(key, ListModel.class);
    }


}
