package com.cyberphysical.system.Services;

import com.cyberphysical.system.Models.Record;
import com.cyberphysical.system.Repos.RecordRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.Date;
import java.util.Iterator;

@Component
public class FileUploading {
    @Autowired
    RecordRepository recordRepository;

    public void uploadData(File file, String region, int yearValue, int monthValue) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

        for (Sheet sheet : workbook) {
            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                Cell dayCell = row.getCell(0);
                Cell timeCell = row.getCell(1);
                Cell tempCell = row.getCell(2);
                Cell windCell = row.getCell(3);
                Cell speedCell = row.getCell(4);

                int dayValue = (int) dayCell.getNumericCellValue();
                Time timeValue = new Time(timeCell.getDateCellValue().getTime());
                int tempValue = (int) tempCell.getNumericCellValue();
                String windValue = windCell.getStringCellValue();
                int speedValue = (int) speedCell.getNumericCellValue();

                Date date = new Date(yearValue-1900, monthValue-1, dayValue, timeValue.getHours(), timeValue.getMinutes());

                Record record = new Record(region, date, tempValue, windValue, speedValue);
                recordRepository.save(record);
            }
        }
    }
}
