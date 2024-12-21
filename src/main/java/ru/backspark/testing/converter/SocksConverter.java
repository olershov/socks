package ru.backspark.testing.converter;

import org.springframework.stereotype.Component;
import ru.backspark.testing.model.dto.SocksDto;
import ru.backspark.testing.model.entity.SocksEntity;

@Component
public class SocksConverter {

    public SocksEntity toEntity(SocksDto dto) {
        var entity = new SocksEntity();
        entity.setColor(dto.getColor());
        entity.setCount(dto.getCount());
        entity.setCottonPercentContent(dto.getCottonPercentContent());
        return entity;
    }

    public SocksDto toDto(SocksEntity entity) {
        return SocksDto.builder()
                .color(entity.getColor())
                .count(entity.getCount())
                .cottonPercentContent(entity.getCottonPercentContent())
                .build();
    }
}
