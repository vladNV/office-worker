package com.org.worker.service;

import com.org.worker.exception.FileWriterException;
import com.org.worker.service.model.ExcelSheet;
import com.org.worker.service.model.ExcelSheetStyle;
import com.org.worker.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class ExcelWorkerServiceImpl implements ExcelWorkerService {

    @Override
    public List<ExcelSheet> excelData(@NotNull final String path) {
        return sheets(openExcelWorkbook(path));
    }

    private Workbook openExcelWorkbook(String path) {
        try (FileInputStream fis = new FileInputStream(new File(path))){
            LOG.info("About to open workbook '{}'", path);
            return WorkbookFactory.create(fis);
        } catch (IOException | InvalidFormatException e) {
            throw new FileWriterException("Error occurred in file template", e);
        }
    }

    private List<ExcelSheet> sheets(Workbook workbook) {
        Assert.notNull(workbook, "woorbook must not be  null");
        int number = workbook.getNumberOfSheets();
        LOG.info("Number of sheets '{}'", number);

        ExcelSheet sheetSecondRow = populateSheetRow(workbook.getSheetAt(0));
        ExcelSheet sheetTable = populateSheetTable(workbook.getSheetAt(1));

        return Arrays.asList(sheetSecondRow, sheetTable);
    }

    private ExcelSheet populateSheetRow(Sheet sheet) {
        ExcelSheet excelSheet = new ExcelSheet();

        excelSheet.setName(sheet.getSheetName());
        excelSheet.setRows(StreamSupport
                .stream(sheet.spliterator(), false)
                .map(row -> row.getCell(1))
                .filter(Objects::nonNull)
                .map(Objects::toString)
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList()));
        excelSheet.setExcelSheetStyle(ExcelSheetStyle.SINGLE_COLUMN);

        return excelSheet;
    }

    private ExcelSheet populateSheetTable(Sheet sheet) {
        ExcelSheet excelSheet = new ExcelSheet();

        excelSheet.setName(sheet.getSheetName());
        excelSheet.setRows(populateRows(sheet));
        excelSheet.setExcelSheetStyle(ExcelSheetStyle.TABLE);

        return excelSheet;
    }

    private List<String> populateRows(Sheet sheet) {
        return StreamSupport.stream(sheet.spliterator(), false)
                .map(row -> join(row.spliterator()))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    private String join(Spliterator<Cell> cellSpliterator) {
        return StreamSupport.stream(cellSpliterator, false)
                .map(Object::toString)
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(FileUtils.SEPARATOR));
    }

}
