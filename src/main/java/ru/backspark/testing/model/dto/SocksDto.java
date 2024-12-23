package ru.backspark.testing.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Data
@Builder
@Getter
public class SocksDto {

    @NotBlank(message = "Поле цвет не может быть пустыми")
    @JsonProperty("color")
    private String color;

    @Positive(message = "Количество должно быть положительным числом")
    @JsonProperty("count")
    private Integer count;

    @PositiveOrZero(message = "Процент содержания не может быть отрицательным")
    @Max(value = 100,message = "Процент содержания не может быть больше 100")
    @JsonProperty("cotton_percent")
    private Integer cottonPercentContent;
}
