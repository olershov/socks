package ru.backspark.testing.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.backspark.testing.converter.SocksConverter;
import ru.backspark.testing.exception.InvalidDataException;
import ru.backspark.testing.exception.ObjectNotFoundException;
import ru.backspark.testing.model.dto.SocksDto;
import ru.backspark.testing.model.entity.SocksEntity;
import ru.backspark.testing.repository.SocksRepository;
import ru.backspark.testing.service.interfaces.SocksService;
import ru.backspark.testing.util.XlsParser;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SocksServiceImpl implements SocksService {

    private final SocksRepository socksRepository;
    private final SocksConverter socksConverter;
    private final XlsParser xlsParser;

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

    @Override
    public SocksDto update(Long id, SocksDto dto) {
        var entity = socksRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("Socks with id=" + id + " not found")
        );
        entity.setColor(dto.getColor());
        entity.setCottonPercentContent(dto.getCottonPercentContent());
        entity.setCount(dto.getCount());
        return socksConverter.toDto(socksRepository.save(entity));
    }

    @Override
    public void uploadBatch(MultipartFile file) {
        checkFile(file);
        var socks = xlsParser.xlsxToSocksEntity(file);
        this.saveBatch(socks);
    }

    private void checkFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (!filename.endsWith(".xlsx")) {
            throw new InvalidDataException("Invalid file format. Only .xlsx is allowed");
        }
        if (file.isEmpty()) {
            throw new InvalidDataException("The file must not be empty");
        }
    }

    private int calculateCount(int before, int income) {
        return before + income;
    }

    private void saveBatch(List<SocksEntity> entities) {
        List<SocksEntity> listForSave = new ArrayList<>();
        entities.forEach(socks -> {
            var optionalFromDb = socksRepository.findByColorIgnoreCaseAndCottonPercentContent(socks.getColor(), socks.getCottonPercentContent());
            if (optionalFromDb.isEmpty()) {
                listForSave.add(socks);
            } else {
                var socksFromDb = optionalFromDb.get();
                socksFromDb.setCount(calculateCount(socksFromDb.getCount(), socks.getCount()));
                listForSave.add(socksFromDb);
            }
        });
        socksRepository.saveAll(listForSave);
    }
}
