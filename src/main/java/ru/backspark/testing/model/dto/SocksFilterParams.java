package ru.backspark.testing.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SocksFilterParams {
    private String color;
    private Integer cottonPercentMin;
    private Integer cottonPercentMax;
    private String orderBy;
    private Boolean ascending;
}
