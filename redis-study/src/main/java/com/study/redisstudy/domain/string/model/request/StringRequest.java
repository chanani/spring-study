package com.study.redisstudy.domain.string.model.request;

import com.study.redisstudy.common.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "redis string collection request")
public record StringRequest(
        BaseRequest baseRequest,

        @Schema(description = "name")
        @NotBlank
        @NotNull
        String name
) {
}
