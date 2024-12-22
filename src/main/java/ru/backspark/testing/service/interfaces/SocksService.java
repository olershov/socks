package ru.backspark.testing.service.interfaces;

import org.springframework.web.multipart.MultipartFile;
import ru.backspark.testing.model.dto.SocksDto;

public interface SocksService {

    SocksDto calculateIncome(SocksDto dto);
    SocksDto calculateOutcome(SocksDto dto);
    SocksDto update(Long id, SocksDto dto);
    void uploadBatch(MultipartFile file);
}
