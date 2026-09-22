package com.prasad_v.testdata;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.testng.annotations.DataProvider;

import com.prasad_v.exceptions.APIException;
import com.prasad_v.logging.CustomLogger;

/**
 * ExcelDataProvider provides utilities to read test data from Excel files.
 * It supports reading data as key-value pairs or as DataProvider for TestNG.
 */
public class ExcelDataProvider {

    private static final CustomLogger logger = new CustomLogger(ExcelDataProvider.class);

    /**
     * Get test data from Excel file as a list of maps
     *
     * @param filePath Path to Excel file
     * @param sheetName Sheet name to read from
     * @return List of Map with test data where each map represents a row
     * @throws APIException If there's an error reading the Excel file
     */
    public List<Map<String, Object>> getTestDataFromExcel(String filePath, String sheetName) throws APIException {
        List<Map<String, Object>> testDataList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new APIException("Sheet '" + sheetName + "' not found in Excel file: " + filePath);
            }

            // Get header row
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new APIException("Header row not found in sheet: " + sheetName);
            }

            // Extract header column names
            List<String> headers = new ArrayList<>();
            Iterator<Cell> cellIterator = headerRow.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                headers.add(cell.getStringCellValue());
            }

            // Process data rows
            int rowCount = sheet.getLastRowNum();
            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Map<String, Object> rowData = new HashMap<>();
                boolean hasData = false;

                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (cell != null) {
                        Object cellValue = getCellValue(cell);
                        rowData.put(headers.get(j), cellValue);
                        hasData = true;
                    }
                }

                if (hasData) {
                    testDataList.add(rowData);
                }
            }

            logger.info("Read " + testDataList.size() + " rows of test data from: " + filePath + ", sheet: " + sheetName);
            return testDataList;

        } catch (IOException e) {
            logger.error("Error reading Excel file: " + filePath, e);
            throw new APIException("Failed to read Excel file: " + e.getMessage(), e);
        }
    }

    /**
     * Get cell value based on cell type
     *
     * @param cell Excel cell
     * @return Cell value as appropriate Java type
     */
    private Object getCellValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return cell.getNumericCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                switch (cell.getCachedFormulaResultType()) {
                    case STRING:
                        return cell.getStringCellValue();
                    case NUMERIC:
                        return cell.getNumericCellValue();
                    case BOOLEAN:
                        return cell.getBooleanCellValue();
                    default:
                        return null;
                }
            default:
                return null;
        }
    }

    /**
     * TestNG DataProvider that reads test data from Excel
     *
     * @param filePath Path to Excel file
     * @param sheetName Sheet name to read from
     * @return Object array for TestNG DataProvider
     * @throws APIException If there's an error reading the Excel file
     */
    @DataProvider(name = "excelDataProvider")
    public Object[][] getDataFromExcel(String filePath, String sheetName) throws APIException {
        List<Map<String, Object>> testDataList = getTestDataFromExcel(filePath, sheetName);

        Object[][] data = new Object[testDataList.size()][1];
        for (int i = 0; i < testDataList.size(); i++) {
            data[i][0] = testDataList.get(i);
        }

        return data;
    }

    /**
     * Filter test data by a condition
     *
     * @param testDataList Original test data list
     * @param filterKey Key to filter on
     * @param filterValue Value to match
     * @return Filtered test data list
     */
    public List<Map<String, Object>> filterTestData(List<Map<String, Object>> testDataList,
                                                    String filterKey, Object filterValue) {
        List<Map<String, Object>> filteredList = new ArrayList<>();

        for (Map<String, Object> data : testDataList) {
            if (data.containsKey(filterKey) && data.get(filterKey).equals(filterValue)) {
                filteredList.add(data);
            }
        }

        return filteredList;
    }

    /**
     * Get single test data row that matches a condition
     *
     * @param filePath Path to Excel file
     * @param sheetName Sheet name to read from
     * @param filterKey Key to filter on
     * @param filterValue Value to match
     * @return Map with matched test data row or empty map if not found
     * @throws APIException If there's an error reading the Excel file
     */
    public Map<String, Object> getTestDataByFilter(String filePath, String sheetName,
                                                   String filterKey, Object filterValue) throws APIException {
        List<Map<String, Object>> testDataList = getTestDataFromExcel(filePath, sheetName);
        List<Map<String, Object>> filteredList = filterTestData(testDataList, filterKey, filterValue);

        if (!filteredList.isEmpty()) {
            return filteredList.get(0);
        }

        return new HashMap<>();
    }
}