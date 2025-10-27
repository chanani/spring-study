package com.study.redisstudy.domain.list.controller;

import com.study.redisstudy.domain.list.model.ListModel;
import com.study.redisstudy.domain.list.model.request.ListRequest;
import com.study.redisstudy.service.RedisList;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "list", description = "list api")
@RestController
@RequestMapping("/api/v1/list")
@RequiredArgsConstructor
public class ListController {

    private final RedisList redis;

    @PostMapping(value = "/list-add-left")
    public void setNewValueToListLeft(
            @RequestBody @Valid ListRequest req
    ) {
        redis.addToListLeft(req.baseRequest().key(), req.name());
    }

    @PostMapping(value = "/list-add-right")
    public void setNewValueToListRight(
            @RequestBody @Valid ListRequest req
    ) {
        redis.addToListRight(req.baseRequest().key(), req.name());
    }

    @GetMapping(value = "/all")
    public List<ListModel> getAll(
            @RequestParam String key
    ) {
        return redis.getAllList(key);
    }

}
