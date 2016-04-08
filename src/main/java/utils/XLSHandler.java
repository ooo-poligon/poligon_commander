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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javafx.scene.control.Alert;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import main.PCGUIController;
import modalwindows.AlertWindow;
import org.hibernate.Session;

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
    public static void exportDBTableTo(String dbTable, String targetPath) {
        File file = new File(targetPath);
        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("ru", "RU"));

        WritableWorkbook workbook = null;
        try {
            //System.out.println("exportDBTableTo starts with " + dbTable + " -> " + targetPath);
            workbook = Workbook.createWorkbook(file, wbSettings);
            workbook.createSheet("Data from table \"" + dbTable + "\"", 0);
            WritableSheet excelSheet = workbook.getSheet(0);
            if (dbTable.equals("products")) {
                Label  caption0  = new Label ( 0, 0, "id"); excelSheet.addCell(caption0);
                Label  caption1  = new Label ( 1, 0, "category_id"); excelSheet.addCell(caption1);
                Label  caption2  = new Label ( 2, 0, "title"); excelSheet.addCell(caption2);
                Label  caption3  = new Label ( 3, 0, "description"); excelSheet.addCell(caption3);
                Label  caption4  = new Label ( 4, 0, "anons"); excelSheet.addCell(caption4);
                Label  caption5  = new Label ( 5, 0, "article"); excelSheet.addCell(caption5);
                Label  caption6  = new Label ( 6, 0, "available"); excelSheet.addCell(caption6);
                Label  caption7  = new Label ( 7, 0, "delivery_time"); excelSheet.addCell(caption7);
                Label  caption8  = new Label ( 8, 0, "ean"); excelSheet.addCell(caption8);
                Label  caption9  = new Label ( 9, 0, "outdated"); excelSheet.addCell(caption9);
                Label  caption10 = new Label (10, 0, "price"); excelSheet.addCell(caption10);
                Label  caption11 = new Label (11, 0, "serie"); excelSheet.addCell(caption11);
                Label  caption12 = new Label (12, 0, "product_kind_id"); excelSheet.addCell(caption12);
                Label  caption13 = new Label (13, 0, "vendor"); excelSheet.addCell(caption13);
                Label  caption14 = new Label (14, 0, "plugin_owner_id"); excelSheet.addCell(caption14);
                Label  caption15 = new Label (15, 0, "accessory_owner_id"); excelSheet.addCell(caption15);
                Label  caption16 = new Label (16, 0, "rate"); excelSheet.addCell(caption16);
                Label  caption17 = new Label (17, 0, "discount1"); excelSheet.addCell(caption17);
                Label  caption18 = new Label (18, 0, "discount2"); excelSheet.addCell(caption18);
                Label  caption19 = new Label (19, 0, "discount3"); excelSheet.addCell(caption19);
                for (int i = 1; i < PCGUIController.allProductsList.size(); i++) {
                    Number id                 = new Number( 0, i, PCGUIController.allProductsList.get(i).getId());
                    excelSheet.addCell(id);
                    Number category_id        = new Number( 1, i, PCGUIController.allProductsList.get(i).getCategoryId());
                    excelSheet.addCell(category_id);
                    Label  title              = new Label ( 2, i, PCGUIController.allProductsList.get(i).getTitle());
                    excelSheet.addCell(title);
                    Label  description        = new Label ( 3, i, PCGUIController.allProductsList.get(i).getDescription());
                    excelSheet.addCell(description);
                    Label  anons              = new Label ( 4, i, PCGUIController.allProductsList.get(i).getAnons());
                    excelSheet.addCell(anons);
                    Label  article            = new Label ( 5, i, PCGUIController.allProductsList.get(i).getArticle());
                    excelSheet.addCell(article);
                    Number available          = new Number( 6, i, PCGUIController.allProductsList.get(i).getAvailable());
                    excelSheet.addCell(available);
                    Label  delivery_time      = new Label ( 7, i, PCGUIController.allProductsList.get(i).getDeliveryTime());
                    excelSheet.addCell(delivery_time);
                    Label  ean                = new Label ( 8, i, PCGUIController.allProductsList.get(i).getEan());
                    excelSheet.addCell(ean);
                    Number outdated           = new Number( 9, i, PCGUIController.allProductsList.get(i).getOutdated());
                    excelSheet.addCell(outdated);
                    Number price              = new Number(10, i, PCGUIController.allProductsList.get(i).getPrice());
                    excelSheet.addCell(price);
                    Label  serie              = new Label (11, i, PCGUIController.allProductsList.get(i).getSerie());
                    excelSheet.addCell(serie);
                    Number product_kind_id    = new Number(12, i, PCGUIController.allProductsList.get(i).getProductKindId());
                    excelSheet.addCell(product_kind_id);
                    Label  vendor             = new Label (13, i, PCGUIController.allProductsList.get(i).getVendor());
                    excelSheet.addCell(vendor);
                    Number plugin_owner_id    = new Number(14, i, PCGUIController.allProductsList.get(i).getPluginOwnerId());
                    excelSheet.addCell(plugin_owner_id);
                    Number accessory_owner_id = new Number(15, i, PCGUIController.allProductsList.get(i).getAccessoryOwnerId());
                    excelSheet.addCell(accessory_owner_id);
                    Number rate               = new Number(16, i, PCGUIController.allProductsList.get(i).getRate());
                    excelSheet.addCell(rate);
                    Number discount1          = new Number(17, i, PCGUIController.allProductsList.get(i).getDiscount1());
                    excelSheet.addCell(discount1);
                    Number discount2          = new Number(18, i, PCGUIController.allProductsList.get(i).getDiscount2());
                    excelSheet.addCell(discount2);
                    Number discount3          = new Number(19, i, PCGUIController.allProductsList.get(i).getDiscount3());
                    excelSheet.addCell(discount3);
                }
                workbook.write();
                workbook.close();
                //System.out.println("exportDBTableTo starts with " + dbTable + " -> " + targetPath + "PCGUIController.allProductsList.size() " + PCGUIController.allProductsList.size());
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Внимание!");
                alert.setHeaderText("На текущий момент реализован только экспорт таблицы 'products'.");
                alert.setContentText("Экспорт остальных таблиц будет реализован позже (если потребуется...).");
                alert.show();
            }
        } catch (IOException e) {} catch (WriteException e) {}
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

