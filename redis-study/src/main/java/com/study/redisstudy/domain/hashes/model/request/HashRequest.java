package com.study.redisstudy.domain.hashes.model.request;

import com.study.redisstudy.common.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "redis hash collection request")
public record HashRequest(
        BaseRequest baseRequest,

        @Schema(description = "")
        @NotBlank
        @NotNull
        String filed,

        @Schema(description = "")
        @NotBlank
        @NotNull
        String name
) {
}
