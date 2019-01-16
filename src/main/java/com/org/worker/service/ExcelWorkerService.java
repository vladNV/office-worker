package com.org.worker.service;

import com.org.worker.service.model.ExcelSheet;

import java.util.List;

public interface ExcelWorkerService {

    /**
     * List of sheets
     * Each sheet has list of data
     * @return list of sheets contains rows
     */
    List<ExcelSheet> excelData(String path);

}
