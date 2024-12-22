package ru.backspark.testing.util;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.backspark.testing.exception.InvalidDataException;
import ru.backspark.testing.model.entity.SocksEntity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@Component
public class XlsParser {

    private static final String[] FILE_HEADERS_FORMAT = {"Цвет", "Содержание хлопка", "Колтичество"};

    public List<SocksEntity> xlsxToSocksEntity(MultipartFile xlsxFile) {
        List<SocksEntity> socks = new ArrayList<>();
        try (InputStream inputStream = new BufferedInputStream(xlsxFile.getInputStream());
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (!validateHeaders(headerRow)) {
                throw new InvalidDataException("Invalid headers in Excel file");
            }

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String color = row.getCell(0).getStringCellValue();
                int cottonPercent = (int) row.getCell(1).getNumericCellValue();
                int count = (int) row.getCell(2).getNumericCellValue();
                socks.add(new SocksEntity(color, count, cottonPercent));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error processing the Excel file", e);
        }
        return socks;
    }

    private boolean validateHeaders(Row headerRow) {
        if (headerRow == null || headerRow.getPhysicalNumberOfCells() != FILE_HEADERS_FORMAT.length) {
            return false;
        }
        for (int i = 0; i < FILE_HEADERS_FORMAT.length; i++) {
            String cellValue = headerRow.getCell(i).getStringCellValue();
            if (!cellValue.equalsIgnoreCase(FILE_HEADERS_FORMAT[i])) {
                return false;
            }
        }
        return true;
    }
}
