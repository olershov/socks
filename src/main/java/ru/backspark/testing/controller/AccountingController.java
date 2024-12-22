package ru.backspark.testing.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.backspark.testing.model.dto.SocksDto;
import ru.backspark.testing.service.interfaces.SocksService;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/socks")
@Api(value = "Контроллер для учёта носков", tags = {"Accounting Controller"})
public class AccountingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountingController.class);
    private final SocksService socksService;

    @ApiOperation(value = "Регистрация прихода", notes = "Регистрация прихода")
    @PostMapping("/income")
    public ResponseEntity<SocksDto> income(@Valid @RequestBody SocksDto socksDto) {
        LOGGER.info("Запрос на регистрацию прихода: цвет=%s, процент содержания хлопка=%d, количество=%d".formatted(socksDto.getColor(), socksDto.getCottonPercentContent(), socksDto.getCount()));
        var result = socksService.calculateIncome(socksDto);
        LOGGER.info("Приход зарегистрирован, остаток: %d".formatted(result.getCount()));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "Регистрация отпуска", notes = "Регистрация отпуска")
    @PostMapping("/outcome")
    public ResponseEntity<SocksDto> outcome(@Valid @RequestBody SocksDto socksDto) {
        LOGGER.info("Запрос на регистрацию отпуска: цвет=%s, процент содержания хлопка=%d, количество=%d".formatted(socksDto.getColor(), socksDto.getCottonPercentContent(), socksDto.getCount()));
        var result = socksService.calculateOutcome(socksDto);
        LOGGER.info("Отпуск зарегистрирован, остаток: %d".formatted(result.getCount()));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /*@ApiOperation(value = "Получение общего количества с фильтром", notes = "Получение общего количества с фильтром")
    @GetMapping("")
    public ResponseEntity<?> getSocks() {
        LOGGER.info("Запрос на получение носков".formatted(socksId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }*/

    @ApiOperation(value = "Изменение параметров", notes = "Изменение параметров")
    @PutMapping("/{id}")
    public ResponseEntity<SocksDto> update(@PathVariable("id") Long socksId,
                                           @Valid @RequestBody SocksDto socksDto) {
        LOGGER.info("Запрос на изменение параметров носков с id=%d".formatted(socksId));
        var result = socksService.update(socksId, socksDto);
        LOGGER.info("Параметры обновлены: {}", result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }



    @ApiOperation(value = "Загрузка партий", notes = "Загрузка партий")
    @PostMapping("/batch")
    public ResponseEntity<String> uploadBatch(@RequestParam("file") MultipartFile file) {
        LOGGER.info("Запрос на добавление партии носков из файла %s".formatted(file.getOriginalFilename()));
        socksService.uploadBatch(file);
        LOGGER.info("Запрос на добавление партии носков из файла %s успешно выполнен".formatted(file.getOriginalFilename()));
        return new ResponseEntity<>("Партия успешно сохранена", HttpStatus.OK);
    }
}
