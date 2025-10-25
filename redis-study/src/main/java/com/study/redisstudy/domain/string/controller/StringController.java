package com.study.redisstudy.domain.string.controller;

import com.study.redisstudy.domain.string.model.request.MultiStringRequest;
import com.study.redisstudy.domain.string.model.request.StringRequest;
import com.study.redisstudy.domain.string.model.response.StringResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "string", description = "string api")
@RestController
@RequestMapping("/api/v1/set")
@RequiredArgsConstructor
public class StringController {

    public void setString(
            @RequestBody @Valid StringRequest req
    ){

    }

    public StringResponse getString(
            @RequestParam @Valid String key
    ){

    }

    public void multiString(
            @RequestBody @Valid MultiStringRequest req
    ){

    }
}
