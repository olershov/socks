package ru.backspark.testing.util.parser;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import ru.backspark.testing.exception.InvalidDataException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class XlsParser<T> {

    private final String[] fileHeadersFormat;
    private final Function<Row, T> rowMapper;

    public XlsParser(String[] fileHeadersFormat, Function<Row, T> rowMapper) {
        this.fileHeadersFormat = fileHeadersFormat;
        this.rowMapper = rowMapper;
    }

    public List<T> xlsxToEntity(MultipartFile xlsxFile) {
        List<T> entities = new ArrayList<>();
        try (InputStream inputStream = new BufferedInputStream(xlsxFile.getInputStream());
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (!validateHeaders(headerRow)) {
                throw new InvalidDataException("Invalid headers in Excel file. The file must contain the headers: " + Arrays.toString(fileHeadersFormat));
            }

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                T entity = rowMapper.apply(row);
                if (entity != null) {
                    entities.add(entity);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error processing the Excel file", e);
        }
        return entities;
    }

    private boolean validateHeaders(Row headerRow) {
        if (headerRow == null || headerRow.getPhysicalNumberOfCells() != fileHeadersFormat.length) {
            return false;
        }
        for (int i = 0; i < fileHeadersFormat.length; i++) {
            String cellValue = headerRow.getCell(i).getStringCellValue();
            if (!cellValue.equalsIgnoreCase(fileHeadersFormat[i])) {
                return false;
            }
        }
        return true;
    }
}
