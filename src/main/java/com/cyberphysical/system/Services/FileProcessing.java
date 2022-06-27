package com.cyberphysical.system.Services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class FileProcessing {
    File file;

    public FileProcessing(File file) {
        this.file = file;
    }

    public ArrayList<Integer> showMistakes() throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

        ArrayList<Integer> mistakeRow = new ArrayList<>();

        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                for (int i = 0; i < 5; i++) {
                    Cell cell = row.getCell(i);
                    if (cell == null || cell.getCellTypeEnum() == CellType.BLANK ||
                            cell.getCellTypeEnum() == CellType._NONE || cell.getCellTypeEnum() == CellType.ERROR) {
                        mistakeRow.add(row.getRowNum());
                        i = 5;
                    }
                }
            }
        }
        return mistakeRow;
    }

    public void processData() throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

        Iterator<Sheet> sheetIterator = workbook.iterator();
        while (sheetIterator.hasNext()) {
            Sheet sheet = sheetIterator.next();

            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell[] cells = new Cell[3];

                for (int i = 0; i < cells.length; i++) {
                    int cellNum = i+2;
                    cells[i] = row.getCell(cellNum);

                    if (cells[i] == null || cells[i].getCellTypeEnum() == CellType.BLANK)  {
                        correctBlanks(row.getRowNum(), cellNum, sheet, workbook);
                    }
                }

                if (cells[2].getNumericCellValue() == 0) {
                    System.out.println("zero " + row.getRowNum());
                    correctZeroes(row.getRowNum(), 4, sheet, workbook);
                }

//                if (row.getCell(0) == null || row.getCell(1) == null ||
//                        row.getCell(0).getCellTypeEnum() == CellType.BLANK ||
//                        row.getCell(1).getCellTypeEnum() == CellType.BLANK) {
//                    sheet.removeRow(row);
//                }
            }
            System.out.println("Sheet done!");
        }
        inputStream.close();
        System.out.println("Finish");
    }

    public void correctBlanks(int currentRowNum, int cellNum, Sheet sheet, Workbook workbook) throws IOException {
        Cell upCell;
        Cell downCell;
        int upRowNum, downRowNum;
        upRowNum = currentRowNum - 1;

        upCell = sheet.getRow(upRowNum).getCell(cellNum);

        do {
            downCell = sheet.getRow(currentRowNum).getCell(cellNum);
            currentRowNum++;
        } while ((downCell == null || downCell.getCellTypeEnum() == CellType.BLANK) && sheet.getRow(currentRowNum) != null);
        downRowNum = currentRowNum - 1;

        Cell newCell;
        if(upRowNum < 1) {
            currentRowNum = downRowNum - 1;

            while (currentRowNum > 0) {
                Row currentRow = sheet.getRow(currentRowNum);

                if(currentRow.getCell(cellNum) == null) {
                    assert downCell != null;
                    newCell = currentRow.createCell(cellNum, downCell.getCellTypeEnum());
                } else {
                    assert downCell != null;
                    newCell = currentRow.getCell(cellNum);
                }

                switch (downCell.getCellTypeEnum()) {
                    case NUMERIC:
                        newCell.setCellValue(downCell.getNumericCellValue());
                        break;
                    case STRING:
                        newCell.setCellValue(downCell.getStringCellValue());
                        break;
                }
                currentRowNum--;
            }

        } else if (sheet.getRow(downRowNum + 1) == null) {
            currentRowNum = upRowNum + 1;

            do {
                Row currentRow = sheet.getRow(currentRowNum);

                if(currentRow.getCell(cellNum) == null) {
                    newCell = currentRow.createCell(cellNum, upCell.getCellTypeEnum());
                } else {
                    newCell = currentRow.getCell(cellNum);
                }

                switch (upCell.getCellTypeEnum()) {
                    case NUMERIC:
                        newCell.setCellValue(upCell.getNumericCellValue());
                        break;
                    case STRING:
                        newCell.setCellValue(upCell.getStringCellValue());
                        break;
                }
                currentRowNum++;

            } while (sheet.getRow(currentRowNum) != null);

        } else {
            int firstInterval = (downRowNum - upRowNum - 1) / 2;
            int secondInterval = (downRowNum - upRowNum - 1) - firstInterval;

            currentRowNum = upRowNum + 1;

            for (int i = 0; i < firstInterval; i++) {
                Row currentRow = sheet.getRow(currentRowNum);

                if(currentRow.getCell(cellNum) == null) {
                    newCell = currentRow.createCell(cellNum, upCell.getCellTypeEnum());
                } else {
                    newCell = currentRow.getCell(cellNum);
                }

                switch (upCell.getCellTypeEnum()) {
                    case NUMERIC:
                        newCell.setCellValue(upCell.getNumericCellValue());
                        break;
                    case STRING:
                        newCell.setCellValue(upCell.getStringCellValue());
                        break;
                }
                currentRowNum++;
            }

            for (int i = 0; i < secondInterval; i++) {
                Row currentRow = sheet.getRow(currentRowNum);

                if(currentRow.getCell(cellNum) == null) {
                    assert downCell != null;
                    newCell = currentRow.createCell(cellNum, downCell.getCellTypeEnum());
                } else {
                    newCell = currentRow.getCell(cellNum);
                    assert downCell != null;
                }

                switch (downCell.getCellTypeEnum()) {
                    case NUMERIC:
                        newCell.setCellValue(downCell.getNumericCellValue());
                        break;
                    case STRING:
                        newCell.setCellValue(downCell.getStringCellValue());
                        break;
                }
                currentRowNum++;
            }
        }

        FileOutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        outputStream.close();
    }

    public void correctZeroes(int currentRowNum, int cellNum, Sheet sheet, Workbook workbook) throws IOException {
        Cell upCell;
        Cell downCell;
        int upRowNum, downRowNum;
        upRowNum = currentRowNum - 1;

        upCell = sheet.getRow(upRowNum).getCell(cellNum);

        do {
            downCell = sheet.getRow(currentRowNum).getCell(cellNum);
            currentRowNum++;
        } while ((downCell.getNumericCellValue() == 0) && sheet.getRow(currentRowNum) != null);
        downRowNum = currentRowNum - 1;

        Cell newCell;
        if(upRowNum < 1) {
            currentRowNum = downRowNum - 1;

            while (currentRowNum > 0) {
                Row currentRow = sheet.getRow(currentRowNum);
                newCell = currentRow.getCell(cellNum);
                newCell.setCellValue(downCell.getNumericCellValue());
                currentRowNum--;
            }

        } else if (sheet.getRow(downRowNum + 1) == null) {
            currentRowNum = upRowNum + 1;

            do {
                Row currentRow = sheet.getRow(currentRowNum);
                newCell = currentRow.getCell(cellNum);
                newCell.setCellValue(upCell.getNumericCellValue());
                currentRowNum++;
            } while (sheet.getRow(currentRowNum) != null);

        } else {
            int firstInterval = (downRowNum - upRowNum - 1) / 2;
            int secondInterval = (downRowNum - upRowNum - 1) - firstInterval;

            currentRowNum = upRowNum + 1;

            for (int i = 0; i < firstInterval; i++) {
                Row currentRow = sheet.getRow(currentRowNum);
                newCell = currentRow.getCell(cellNum);
                newCell.setCellValue(upCell.getNumericCellValue());
                currentRowNum++;
            }

            for (int i = 0; i < secondInterval; i++) {
                Row currentRow = sheet.getRow(currentRowNum);
                newCell = currentRow.getCell(cellNum);
                newCell.setCellValue(downCell.getNumericCellValue());
                currentRowNum++;
            }
        }

        FileOutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        outputStream.close();
    }
}