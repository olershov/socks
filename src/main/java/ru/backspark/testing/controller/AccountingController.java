package ru.backspark.testing.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

   /* @ApiOperation(value = "Получение общего количества с фильтром", notes = "Получение общего количества с фильтром")
    @GetMapping("")
    public ResponseEntity<?> outcome(@Valid @RequestBody SocksEntity socks) {

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "Изменение параметров", notes = "Изменение параметров")
    @PutMapping("/{id}")
    public ResponseEntity<?> outcome(@PathVariable("cluster_id") Long clusterId,
                                     @PathVariable("switch_id") Long switchId,
                                     @RequestBody ClusterSwitchHostDTOs switchHostDTOs) {

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "Изменение параметров", notes = "Изменение параметров")
    @PostMapping("/batch")
    public ResponseEntity<?> outcome(@PathVariable("cluster_id") Long clusterId,
                                     @PathVariable("switch_id") Long switchId,
                                     @RequestBody ClusterSwitchHostDTOs switchHostDTOs) {

        return new ResponseEntity<>(result, HttpStatus.OK);
    }*/
}
