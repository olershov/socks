package ru.backspark.testing.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SocksFilterParams {
    private String color;
    private Integer cottonPercentMin;
    private Integer cottonPercentMax;
    private String orderBy = "color";
    private Boolean ascending = true;
}
