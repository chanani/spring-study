package com.study.redisstudy.common.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Schema(description = "base key request")
public record BaseRequest(
        @Schema(description = "redis key")
        @NotBlank
        @NotNull
        String key
) {
}
