package com.spatial.index.application.dto.location.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BoundsRequest(

        @NotNull @DecimalMin("-90") @DecimalMax("90")
        Double swLat,

        @NotNull @DecimalMin("-180") @DecimalMax("180")
        Double swLng,

        @NotNull @DecimalMin("-90") @DecimalMax("90")
        Double neLat,

        @NotNull @DecimalMin("-180") @DecimalMax("180")
        Double neLng,

        Integer zoom,

        @Min(1) @Max(2000)
        Integer limit
) {
}
