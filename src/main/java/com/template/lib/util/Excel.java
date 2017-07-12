package com.template.lib.util;

import com.template.lib.datajack.Stash;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by sbt-velichko-aa on 27.09.2016.
 */
public class Excel {

    public static void inputDataIntoStash(XSSFWorkbook workbook, int sheetIndex) {
        try {
            workbook.setActiveSheet(sheetIndex);
            XSSFSheet sheet = workbook.getSheetAt(sheetIndex);

            //remember headers
            Iterator<Row> rowIterator = sheet.iterator();
            Row rowHeaders = rowIterator.next();
            HashMap<Integer, String> headers = new HashMap<>();
            for (int i = 0; i < rowHeaders.getLastCellNum(); i++) {
                headers.put(i, rowHeaders.getCell(i).toString());
            }

            //remember data
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    System.out.println("put into stash. key: " + headers.get(i) + ", value: " + row.getCell(i).toString());
                    Stash.put(headers.get(i), row.getCell(i).toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
