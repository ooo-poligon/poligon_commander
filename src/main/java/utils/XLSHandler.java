/**
 * Created by Igor Klekotnev on 25.09.2015.
 */

package utils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import entities.*;
import entities.Properties;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import jxl.*;
import jxl.read.biff.BiffException;

import jxl.Workbook;
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
import main.Product;
import modalwindows.AlertWindow;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javafx.concurrent.Task;

public class XLSHandler {
    private static DBConnection connection = new DBConnection("local");
    //метод для считывания содержимого таблицы
    public static ArrayList<ArrayList<String>> grabData(String inFile, ProgressBar progressBarImportXLS) throws IOException {
        ArrayList<ArrayList<String>> allColumnsContent = new ArrayList<>();
        Task task = new Task<Void>() {
            @Override public Void call() {
                File inputWorkbook = new File(inFile);

                WorkbookSettings ws = new WorkbookSettings();
                ws.setEncoding("Cp1252");

                Workbook w;
                try {
                    w = Workbook.getWorkbook(inputWorkbook, ws);
                    // получаем первый лист
                    Sheet sheet = w.getSheet(0);
                    for (int c = 0; c < sheet.getColumns(); c++) {
                        ArrayList<String> recentColumnContent = new ArrayList<>();
                        for (int r = 0; r < sheet.getRows(); r++) {
                            Cell cellContent = sheet.getCell(c, r);
                            recentColumnContent.add(cellContent.getContents().trim());
                        }
                        allColumnsContent.add(recentColumnContent);
                        updateProgress(c, sheet.getColumns());
                    }
                } catch (BiffException e) {
                } catch (IOException e) {
                    e.printStackTrace();
                }
                updateProgress(0, 0);
                Platform.runLater(() -> {
                    AlertWindow.showInfo("Файл для обработки данных загружен.\nВыберите номер строки с заголовками колонок.");
                });
                return null;
            }
        };

        progressBarImportXLS.progressProperty().bind(task.progressProperty());
        new Thread(task).start();

        ////////////////////////////////////////////////////////////////////////////
        return allColumnsContent;
    }
    public static void exportPropertiesByProductKinds(String selectedProductKind, String targetDir, ProgressBar exportImportProgressBar) {
        Task task = new Task<Void>() {
            @Override public Void call() {
                ProductKinds pk = new ProductKinds();
                Session session = HibernateUtil.getSessionFactory().openSession();
                Query query = session.createQuery("from ProductKinds where title = :title");
                query.setParameter("title", selectedProductKind);
                List<ProductKinds> list = query.list();
                for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                    pk = (ProductKinds) iterator.next();
                }
                session.close();
                if (pk != null) {
                    ArrayList<Products> prodList = new ArrayList<>();
                    Session session1 = HibernateUtil.getSessionFactory().openSession();
                    List<ProductKinds> list1 = session1.createQuery("from Products where productKindId=" + pk.getId()).list();
                    for (Iterator iterator = list1.iterator(); iterator.hasNext();) {
                        Products p = (Products) iterator.next();
                        prodList.add(p);
                    }
                    session1.close();

                    ArrayList<Properties> propList = new ArrayList<>();
                    Session session2 = HibernateUtil.getSessionFactory().openSession();
                    List<ProductKinds> list2 = session2.createQuery("from Properties where productKindId=" + pk.getId()).list();
                    for (Iterator iterator = list2.iterator(); iterator.hasNext();) {
                        Properties pr = (Properties) iterator.next();
                        propList.add(pr);
                    }
                    session2.close();

                    ArrayList<PropertyValues> pvList = new ArrayList<>();
                    Session session3 = HibernateUtil.getSessionFactory().openSession();
                    List<PropertyValues> list3 = session3.createQuery("from PropertyValues").list();
                    for (Iterator iterator = list3.iterator(); iterator.hasNext();) {
                        PropertyValues pv = (PropertyValues) iterator.next();
                        pvList.add(pv);
                    }
                    session3.close();

                    if (prodList.size() < 252) {
                        String pkTitle = pk.getTitle().replace(" ", "_").replace("+", "_").replace("\\", "_").replace("/", "_");
                        System.out.println("OS is " + System.getProperty("os.name"));
                        String targetPath = "";
                        if (System.getProperty("os.name").contains("Windows")) {
                            targetPath = targetDir + "\\" + pkTitle + ".xls";
                        } else {
                            targetPath = targetDir + "/" + pkTitle + ".xls";
                        }
                        // uncomment next line for windows
                        //String targetPath = targetDir + "\\" + pkTitle + ".xls";
                        // comment out next line for *nix
                        //String targetPath = targetDir + "/" + pkTitle + ".xls";
                        File file = new File(targetPath);
                        WorkbookSettings wbSettings = new WorkbookSettings();
                        wbSettings.setLocale(new Locale("ru", "RU"));

                        WritableWorkbook workbook = null;
                        try {
                            workbook = Workbook.createWorkbook(file, wbSettings);
                            workbook.createSheet("Properties by product kind", 0);
                            WritableSheet excelSheet = workbook.getSheet(0);
                            // arguments(column_number, row_number, cell_content)

                            excelSheet.addCell(new Label(0, 0, pk.getTitle()));

                            if (prodList.size() <= propList.size()) {
                                for (int r = 0; r < propList.size(); r++) {
                                    excelSheet.addCell(new Label(0, r + 1, propList.get(r).getTitle()));
                                    excelSheet.addCell(new Label(1, r + 1, propList.get(r).getOptional()));
                                    excelSheet.addCell(new Label(2, r + 1, propList.get(r).getSymbol()));
                                    for (int c = 0; c < prodList.size(); c++) {
                                        excelSheet.addCell(new Label(c + 3, 0, prodList.get(c).getTitle()));
                                        for( PropertyValues pv : pvList) {
                                            int pvPropId = pv.getPropertyId().getId();
                                            int pvProdId = pv.getProductId().getId();
                                            int propId = propList.get(r).getId();
                                            int prodId = prodList.get(c).getId();
                                            if ((pvPropId == propId) && (pvProdId == prodId)) {
                                                excelSheet.addCell(new Label (c+3, r+1, pv.getValue()));
                                            }
                                        }

                                    }
                                    updateProgress(r, propList.size());
                                }
                            } else {
                                for (int c = 0; c < prodList.size(); c++) {
                                    excelSheet.addCell(new Label(c + 3, 0, prodList.get(c).getTitle()));
                                    for (int r = 0; r < propList.size(); r++) {
                                        excelSheet.addCell(new Label(0, r + 1, propList.get(r).getTitle()));
                                        excelSheet.addCell(new Label(1, r + 1, propList.get(r).getOptional()));
                                        excelSheet.addCell(new Label(2, r + 1, propList.get(r).getSymbol()));
                                        for( PropertyValues pv : pvList) {
                                            int pvPropId = pv.getPropertyId().getId();
                                            int pvProdId = pv.getProductId().getId();
                                            int propId = propList.get(r).getId();
                                            int prodId = prodList.get(c).getId();
                                            if ((pvPropId == propId) && (pvProdId == prodId)) {
                                                excelSheet.addCell(new Label (c+3, r+1, pv.getValue()));
                                            }
                                        }
                                    }
                                    updateProgress(c, prodList.size());
                                }
                            }
                            workbook.write();
                            workbook.close();
                        } catch (WriteException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        AlertWindow.tooManyColumnsForExport();
                    }
                }
                updateProgress(0, 0);
                Platform.runLater(() -> {
                    AlertWindow.taskComplete("Экспорт");
                });
                return null;
            }
        };
        exportImportProgressBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }
    public static void importPropertyValues(String inputFile, ProgressBar exportImportProgressBar) throws IOException, BiffException {
        Task task = new Task<Void>() {
            @Override public Void call() {
                String productKindTitle = new String();
                ProductKinds productKind = new ProductKinds();
                ArrayList<ArrayList<String>> propertyValuesArrays  = new ArrayList<>();
                ArrayList<Integer> propIdsToDelete = new ArrayList<>();

                File inputWorkbook = new File(inputFile);
                Workbook w;

                WorkbookSettings ws = new WorkbookSettings();
                ws.setEncoding("Cp1252");

                try {
                    w = Workbook.getWorkbook(inputWorkbook, ws);
                    Sheet sheet = w.getSheet(0);

                    productKindTitle = sheet.getCell(0, 0).getContents();
                    int colQuantity = sheet.getColumns();
                    int rowQuantity = sheet.getRows();
                    if (colQuantity > rowQuantity) {
                        for (int c = 3; c < colQuantity; c++) {
                            for (int r = 1; r < rowQuantity; r++) {
                                ArrayList<String> pVal = new ArrayList<>();
                                //get pv value [0]
                                pVal.add(sheet.getCell(c, r).getContents());
                                //get pv property title [1]
                                pVal.add(sheet.getCell(0, r).getContents());
                                //get pv property optional [2]
                                pVal.add(sheet.getCell(1, r).getContents());
                                //get pv property symbol [3]
                                pVal.add(sheet.getCell(2, r).getContents());
                                //get pv product title [4]
                                pVal.add(sheet.getCell(c, 0).getContents());
                                propertyValuesArrays.add(pVal);
                            }
                            updateProgress(c, colQuantity);
                        }
                    } else {
                        for (int r = 1; r < rowQuantity; r++) {
                            for (int c = 3; c < colQuantity; c++) {
                                ArrayList<String> pVal = new ArrayList<>();
                                //get pv value [0]
                                pVal.add(sheet.getCell(c, r).getContents());
                                //get pv property title [1]
                                pVal.add(sheet.getCell(0, r).getContents());
                                //get pv property optional [2]
                                pVal.add(sheet.getCell(1, r).getContents());
                                //get pv property symbol [3]
                                pVal.add(sheet.getCell(2, r).getContents());
                                //get pv product title [4]
                                pVal.add(sheet.getCell(c, 0).getContents());
                                propertyValuesArrays.add(pVal);
                            }
                            updateProgress(r, rowQuantity);
                        }
                    }
                } catch (BiffException e) {} catch (IOException e) {}

                //берём экземпляр типа продуктов
                Session session = HibernateUtil.getSessionFactory().openSession();
                Query query = session.createQuery("from ProductKinds where title = :title");
                query.setParameter("title", productKindTitle);
                List<ProductKinds> list = query.list();
                for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                    productKind = (ProductKinds) iterator.next();
                }
                session.close();

                if (productKind.getId() == null) {
                    updateProgress(0, 0);
                    Platform.runLater(() -> {
                        AlertWindow.productKindNotFound();
                    });

                } else {
                    Session session6 = HibernateUtil.getSessionFactory().openSession();
                    List<Properties> list6 = session6.createQuery("from Properties where productKindId = " + productKind.getId()).list();
                    for (Iterator iterator = list6.iterator(); iterator.hasNext();) {
                        Properties property = (Properties) iterator.next();
                        propIdsToDelete.add(property.getId());
                    }
                    session6.close();

                    Session session5 = HibernateUtil.getSessionFactory().openSession();
                    Transaction tx5 = session5.beginTransaction();
                    for (Integer propertyId : propIdsToDelete) {
                        Properties prop = (Properties) session5.load(Properties.class, propertyId);
                        Query query5 = session5.createQuery("delete PropertyValues where propertyId = " + prop.getId());
                        query5.executeUpdate();
                    }
                    tx5.commit();
                    session5.close();

                    Session session4 = HibernateUtil.getSessionFactory().openSession();
                    ProductKinds pk = (ProductKinds) session4.load(ProductKinds.class, productKind.getId());
                    Transaction tx4 = session4.beginTransaction();
                    Query query4 = session4.createQuery("delete Properties where productKindId =" + pk.getId());
                    query4.executeUpdate();
                    tx4.commit();
                    session4.close();


                    int orderNumber = 1;;
                    ArrayList<String> propTitles = new ArrayList<>();
                    int counter = 0;
                    for (ArrayList<String> pva : propertyValuesArrays) {
                        if (!propTitles.contains(pva.get(1))) {
                            Session session0 = HibernateUtil.getSessionFactory().openSession();
                            Transaction tx0 = session0.beginTransaction();

                            Properties newProperty = new Properties();
                            newProperty.setTitle(pva.get(1));
                            newProperty.setOptional(pva.get(2));
                            newProperty.setSymbol(pva.get(3));
                            newProperty.setOrderNumber(orderNumber);
                            newProperty.setProductKindId(productKind);
                            session0.save(newProperty);

                            tx0.commit();
                            session0.close();
                            orderNumber++;

                            propTitles.add(pva.get(1));
                        }
                        updateProgress(counter, propertyValuesArrays.size());
                        counter ++;
                    }

                    counter = 0;
                    for (ArrayList<String> pva : propertyValuesArrays) {
                        Products product = new Products();
                        Properties property = new Properties();
                        PropertyValues pv = new PropertyValues();

                        Session session2 = HibernateUtil.getSessionFactory().openSession();

                        Query query3 = session2.createQuery("from Properties where title = :title");
                        query3.setParameter("title", pva.get(1));
                        List<ProductKinds> list3 = query3.list();
                        for (Iterator iterator = list3.iterator(); iterator.hasNext(); ) {
                            property = (Properties) iterator.next();
                        }
                        session2.save(property);

                        Query query2 = session2.createQuery("from Products where title = :title");
                        query2.setParameter("title", pva.get(4));
                        List<ProductKinds> list2 = query2.list();
                        for (Iterator iterator = list2.iterator(); iterator.hasNext(); ) {
                            product = (Products) iterator.next();
                        }
                        session2.save(product);

                        Transaction tx = session2.beginTransaction();
                        pv.setValue(pva.get(0));
                        pv.setPropertyId(property);
                        pv.setProductId(product);
                        session2.save(pv);
                        tx.commit();
                        session2.close();

                        updateProgress(counter, propertyValuesArrays.size());
                        counter ++;
                    }

                    updateProgress(0, 0);
                    Platform.runLater(() -> {
                        AlertWindow.taskComplete("Импорт");
                    });
                }
                return null;
            }
        };
        exportImportProgressBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }
    public static void exportDBTableTo(String dbTable, String targetPath, ProgressBar progressBarImportXLS) {
        Task task = new Task<Void>() {
            @Override public Void call() {
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
                        Label  caption15 = new Label (15, 0, "special"); excelSheet.addCell(caption15);
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
                            Number  vendor_id         = new Number (13, i, PCGUIController.allProductsList.get(i).getVendorId());
                            excelSheet.addCell(vendor_id);
                            Number currency_id        = new Number(14, i, PCGUIController.allProductsList.get(i).getCurrencyId());
                            excelSheet.addCell(currency_id);
                            Number accessory_owner_id = new Number(15, i, PCGUIController.allProductsList.get(i).getSpecial());
                            excelSheet.addCell(accessory_owner_id);
                            Number rate               = new Number(16, i, PCGUIController.allProductsList.get(i).getRate());
                            excelSheet.addCell(rate);
                            Number discount1          = new Number(17, i, PCGUIController.allProductsList.get(i).getDiscount1());
                            excelSheet.addCell(discount1);
                            Number discount2          = new Number(18, i, PCGUIController.allProductsList.get(i).getDiscount2());
                            excelSheet.addCell(discount2);
                            Number discount3          = new Number(19, i, PCGUIController.allProductsList.get(i).getDiscount3());
                            excelSheet.addCell(discount3);
                            updateProgress(i, PCGUIController.allProductsList.size());
                        }
                        workbook.write();
                        workbook.close();
                        //System.out.println("exportDBTableTo starts with " + dbTable + " -> " + targetPath + "PCGUIController.allProductsList.size() " + PCGUIController.allProductsList.size());
                    } else {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Внимание!");
                            alert.setHeaderText("На текущий момент реализован только экспорт таблицы 'products'.");
                            alert.setContentText("Экспорт остальных таблиц будет реализован позже (если потребуется...).");
                            alert.show();
                        });

                    }
                } catch (IOException e) {} catch (WriteException e) {}
                updateProgress(0, 0);
                Platform.runLater(() -> {
                    AlertWindow.taskComplete("Экспорт");
                });
                return null;
            }
        };
        progressBarImportXLS.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }
    public static void exportDBPricesTo(ArrayList<Product> allProductsList, String targetDir, ProgressBar progressBarImportXLS) {
        Task task = new Task<Void>() {
            @Override public Void call() {
                ArrayList<Vendors> vendors_list = new ArrayList<>();
                ArrayList<Integer> vendors_ids = new ArrayList<>();
                Session session = HibernateUtil.getSessionFactory().openSession();
                List result = session.createQuery("From Vendors").list();
                for(Iterator iterator = result.iterator(); iterator.hasNext();) {
                    Vendors vendor = (Vendors) iterator.next();
                    if ((!vendor.getTitle().equals("не указан")) && (!vendor.getTitle().equals("ПОЛИГОН"))) {
                        vendors_list.add(vendor);
                    }
                }
                session.close();
                int counter = 0;
                for(Vendors vendor: vendors_list) {
                    String targetPath = targetDir + "\\" + vendor.getTitle() + ".xls";
                    File file = new File(targetPath);
                    WorkbookSettings wbSettings = new WorkbookSettings();
                    wbSettings.setLocale(new Locale("ru", "RU"));
                    WritableWorkbook workbook = null;
                    try {
                        workbook = Workbook.createWorkbook(file, wbSettings);
                        workbook.createSheet("Prices for " + vendor.getTitle(), 0);
                        WritableSheet excelSheet = workbook.getSheet(0);

                        Label  caption0 = new Label ( 0, 0, "Артикул"); excelSheet.addCell(caption0);
                        Label  caption1 = new Label ( 1, 0, "Название"); excelSheet.addCell(caption1);
                        Label  caption6 = new Label ( 2, 0, "Краткое описание"); excelSheet.addCell(caption6);
                        Label  caption2 = new Label ( 3, 0, "Базовая цена"); excelSheet.addCell(caption2);
                        Label  caption3 = new Label ( 4, 0, "10+"); excelSheet.addCell(caption3);
                        Label  caption4 = new Label ( 5, 0, "Опт"); excelSheet.addCell(caption4);
                        Label  caption5 = new Label ( 6, 0, "Дилер"); excelSheet.addCell(caption5);
                        int j = 1;
                        // iteration begins from 1 because row with number 0 occupies captions row, made just before
                        for (int i = 1; i < allProductsList.size(); i++) {
                            if (allProductsList.get(i).getVendorId() == (vendor.getId())) {
                                Label  article = new Label ( 0, j, allProductsList.get(i).getArticle());
                                Label  title = new Label ( 1, j, allProductsList.get(i).getTitle());
                                Label  description = new Label ( 2, j, allProductsList.get(i).getDescription());
                                excelSheet.addCell(article);
                                excelSheet.addCell(title);
                                excelSheet.addCell(description);

                                Double retail_price = allProductsList.get(i).getPrice() * allProductsList.get(i).getRate();
                                Double discount_ten_plus = retail_price - (retail_price / 100) * allProductsList.get(i).getDiscount1();
                                Double discount_opt = retail_price - (retail_price / 100) * allProductsList.get(i).getDiscount2();
                                Double discount_dealer = retail_price - (retail_price / 100) * allProductsList.get(i).getDiscount3();
                                Number price = new Number( 3, j, round2(retail_price));
                                excelSheet.addCell(price);
                                if (allProductsList.get(i).getSpecial() != 0) {
                                    retail_price = retail_price - (retail_price / 100) * allProductsList.get(i).getSpecial();
                                    price = new Number( 3, j, round2(retail_price));
                                    excelSheet.addCell(price);
                                }
                                retail_price = allProductsList.get(i).getPrice() * allProductsList.get(i).getRate();
                                if (allProductsList.get(i).getSpecial() > allProductsList.get(i).getDiscount1()) {
                                    discount_ten_plus = retail_price - (retail_price / 100) * allProductsList.get(i).getSpecial();
                                }
                                if (allProductsList.get(i).getSpecial() > allProductsList.get(i).getDiscount2()) {
                                    discount_opt = retail_price - (retail_price / 100) * allProductsList.get(i).getSpecial();
                                }
                                if (allProductsList.get(i).getSpecial() > allProductsList.get(i).getDiscount3()) {
                                    discount_dealer = retail_price - (retail_price / 100) * allProductsList.get(i).getSpecial();
                                }

                                Number discount1 = new Number( 4, j, round2(discount_ten_plus));
                                excelSheet.addCell(discount1);

                                Number discount2 = new Number( 5, j, round2(discount_opt));
                                excelSheet.addCell(discount2);

                                Number discount3 = new Number( 6, j, round2(discount_dealer));
                                excelSheet.addCell(discount3);
                                j++;
                            } else {
                                continue;
                            }
                        }
                        workbook.write();
                        workbook.close();
                    } catch (IOException e) {} catch (WriteException e) {}
                    updateProgress(counter, vendors_list.size());
                    counter ++;
                }
                ///////////////////////

                ///////////////////////
                updateProgress(0, 0);
                Platform.runLater(() -> {
                    AlertWindow.taskComplete("Экспорт");
                });
                return null;
            }
        };
        progressBarImportXLS.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }
    private static Double round2(Double val) {
        return new BigDecimal(val.toString()).setScale(2, RoundingMode.UP).doubleValue();
    }
}

