package ru.backspark.testing.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorDto {

    private String message;
    private List<String> errors;

    public ErrorDto(String message, String error) {
        this.message = message;
        this.errors = List.of(error);
    }

    public ErrorDto(String message, List<String> errors) {
        this.message = message;
        this.errors = errors;
    }
}
