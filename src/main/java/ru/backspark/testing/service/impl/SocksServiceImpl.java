package ru.backspark.testing.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.backspark.testing.converter.SocksConverter;
import ru.backspark.testing.exception.InvalidDataException;
import ru.backspark.testing.exception.ObjectNotFoundException;
import ru.backspark.testing.model.dto.SocksDto;
import ru.backspark.testing.model.entity.SocksEntity;
import ru.backspark.testing.repository.SocksRepository;
import ru.backspark.testing.service.interfaces.SocksService;

@Service
@RequiredArgsConstructor
public class SocksServiceImpl implements SocksService {

    private final SocksRepository socksRepository;
    private final SocksConverter socksConverter;

    @Transactional
    @Override
    public SocksDto calculateIncome(SocksDto dto) {
        SocksEntity forSave = socksConverter.toEntity(dto);
        var socksFromDbOpt = socksRepository.findByColorIgnoreCaseAndCottonPercentContent(dto.getColor(), dto.getCottonPercentContent());
        if (socksFromDbOpt.isPresent()) {
            forSave = socksFromDbOpt.get();
            forSave.setCount(calculateCount(forSave.getCount(), dto.getCount()));
        }
        return socksConverter.toDto(socksRepository.save(forSave));
    }

    @Transactional
    @Override
    public SocksDto calculateOutcome(SocksDto dto) {
        var socksFromDbOpt = socksRepository.findByColorIgnoreCaseAndCottonPercentContent(dto.getColor(), dto.getCottonPercentContent());
        if (socksFromDbOpt.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Socks with color=%s and cotton_percent=%d not found", dto.getColor(), dto.getCottonPercentContent()));
        }
        var socksFromDb = socksFromDbOpt.get();
        var remains = calculateCount(socksFromDbOpt.get().getCount(), -dto.getCount());
        if (remains < 0) {
            throw new InvalidDataException("The count of products sold exceeds the remaining stock");
        }
        socksFromDb.setCount(remains);
        return socksConverter.toDto(socksRepository.save(socksFromDb));
    }

    private int calculateCount(int before, int income) {
        return before + income;
    }
}
