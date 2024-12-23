package ru.backspark.testing.util.parser;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;
import ru.backspark.testing.model.entity.SocksEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class SocksEntityParser {

    private final XlsParser<SocksEntity> parser;
    public static final String[] FILE_HEADERS_FORMAT = {"Цвет", "Содержание хлопка", "Количество"};

    public SocksEntityParser() {
        this.parser = new XlsParser<>(FILE_HEADERS_FORMAT, this::mapRowToSocksEntity);
    }

    public List<SocksEntity> parse(MultipartFile xlsxFile) {
        return parser.xlsxToEntity(xlsxFile);
    }

    private SocksEntity mapRowToSocksEntity(Row row) {
        String color = row.getCell(0) != null ? row.getCell(0).getStringCellValue() : null;
        Integer cottonPercent = row.getCell(1) != null && row.getCell(1).getCellType() == CellType.NUMERIC
                ? (int) row.getCell(1).getNumericCellValue() : null;

        if (color == null || color.trim().isEmpty() || cottonPercent == null) {
            return null;
        }

        int count = row.getCell(2) != null && row.getCell(2).getCellType() == CellType.NUMERIC
                ? (int) row.getCell(2).getNumericCellValue() : 0;

        return new SocksEntity(color.toLowerCase(), count, cottonPercent);
    }
}
