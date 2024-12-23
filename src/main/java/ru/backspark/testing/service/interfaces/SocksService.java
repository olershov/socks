package ru.backspark.testing.service.interfaces;

import org.springframework.web.multipart.MultipartFile;
import ru.backspark.testing.model.dto.SocksDto;
import ru.backspark.testing.model.dto.SocksFilterParams;

import java.util.List;

public interface SocksService {

    SocksDto calculateIncome(SocksDto dto);
    SocksDto calculateOutcome(SocksDto dto);
    SocksDto update(Long id, SocksDto dto);
    void uploadBatch(MultipartFile file);
    List<SocksDto> findWithFilter(SocksFilterParams params);
}
