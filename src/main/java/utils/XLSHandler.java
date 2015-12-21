/*
 * 
 * 
 */
package utils;

/**
 *
 * @author kataev
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * Created by Igor Klekotnev on 25.09.2015.
 */
public class XLSHandler {
    //метод для считывания содержимого таблицы
    public static ArrayList<ArrayList<String>> grabData(String inFile) throws IOException {
        ArrayList<ArrayList<String>> allColumnsContent = new ArrayList<>();  
        File inputWorkbook = new File(inFile);
        Workbook w;
        try {
            w = Workbook.getWorkbook(inputWorkbook);
            // получаем первый лист
            Sheet sheet = w.getSheet(0);
            for (int c = 0; c < sheet.getColumns(); c++) {
                ArrayList<String> recentColumnContent = new ArrayList<>();
                for (int r = 0; r < sheet.getRows(); r++) { 
                    Cell cellContent = sheet.getCell(c, r);
                    recentColumnContent.add(cellContent.getContents());
                }
                allColumnsContent.add(recentColumnContent);
            }
        } catch (BiffException e) {
        }
        return allColumnsContent;
    }
    // не работает пока...
    private static String cleanUp(String string) {
        String str = new String();
        if (string.contains("\t") || (string.contains("\n"))) {
            str = string.replace('\t', ' ');
            str = str.replace('\n', ' ');
        }
        return str;
    }
}

