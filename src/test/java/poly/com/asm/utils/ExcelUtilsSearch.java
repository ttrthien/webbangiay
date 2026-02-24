package poly.com.asm.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtilsSearch {
    private static Workbook workbook;
    private static Sheet sheet;

    public static Object[][] getSearchData(String filePath, String sheetName) throws Exception {
        FileInputStream excelFile = new FileInputStream(filePath);
        workbook = new XSSFWorkbook(excelFile);
        sheet = workbook.getSheet(sheetName);
        
        int totalRows = sheet.getLastRowNum(); 
        List<Object[]> dataList = new ArrayList<>();

        for (int i = 1; i <= totalRows; i++) {
            Row row = sheet.getRow(i);
            if (row == null || getCellValue(row.getCell(0)).isEmpty()) {
                continue;
            }
            
            Object[] rowData = new Object[5];
            rowData[0] = getCellValue(row.getCell(1)); 
            rowData[1] = getCellValue(row.getCell(2)); 
            rowData[2] = getCellValue(row.getCell(3)); 
            rowData[3] = getCellValue(row.getCell(4)); 
            rowData[4] = i; 
            dataList.add(rowData);
        }
        workbook.close();
        
        return dataList.toArray(new Object[0][0]);
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        cell.setCellType(CellType.STRING); 
        return cell.getStringCellValue().trim();
    }

    public static void setSearchResults(String filePath, String sheetName, String actual, String status, int rowNum) throws Exception {
        FileInputStream fileIn = new FileInputStream(filePath);
        workbook = new XSSFWorkbook(fileIn);
        sheet = workbook.getSheet(sheetName);
        
        Row row = sheet.getRow(rowNum);
        if (row == null) row = sheet.createRow(rowNum);
        
        row.createCell(6).setCellValue(actual); 
        row.createCell(7).setCellValue(status); 
        
        FileOutputStream fileOut = new FileOutputStream(filePath);
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }
}