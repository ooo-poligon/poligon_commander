package utils;

import entities.*;
import entities.Properties;
import entities.Settings;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import main.PCGUIController;
import main.Product;
import modalwindows.AlertWindow;
import org.apache.commons.io.FileUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.*;
import tableviews.ProductPropertiesForKindTableView;
import tableviews.ProductPropertiesTableView;
import tableviews.ProductsTableView;

import java.awt.event.InputEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Collator;
import java.util.*;
import java.nio.file.Files;

import static utils.SshUtils.sftp;

/**
 * Created by Igor Klekotnev on 11.03.2016.
 */
public class UtilPack {
    public static int getVendorIdFromTitle (String title) {
        int id = 0;
        try {
            ResultSet resultSet = PCGUIController.connection.getResult(
                    "select id from vendors where title =\"" +
                            title.replace("\"", "\\\"") + "\"" );
            while (resultSet.next()) {
                id = resultSet.getInt("id");
            }
        } catch (SQLException e) {}
        return id;
    }

    public static int getCategoryImagePathFromTitle (String title) {
        int id = 0;
        try {
            ResultSet resultSet = PCGUIController.connection.getResult(
                    "select image_path from categories where title =\"" +
                            title.replace("\"", "\\\"") + "\"" );
            while (resultSet.next()) {
                id = resultSet.getInt("id");
            }
        } catch (SQLException e) {}
        return id;
    }

    public static int getCategoryIdFromTitle (String title) {
        int id = 0;
        try {
            ResultSet resultSet = PCGUIController.connection.getResult(
                    "select id from categories where title =\"" +
                            title.replace("\"", "\\\"") + "\"" );
            while (resultSet.next()) {
                id = resultSet.getInt("id");
            }
        } catch (SQLException e) {}
        return id;
    }

    public static String getCategoryTitleFromId (int id) {
        String title = "";
        try {
            ResultSet resultSet = PCGUIController.connection.getResult(
                    "select title from categories where id =" + id );
            while (resultSet.next()) {
                title = resultSet.getString("title");
            }
        } catch (SQLException e) {}
        return title;
    }

    public static String getCategoryVendorFromId (int id) {
        int parentCatId = 0;
        try {
            parentCatId = getParentCatId(id);
        } catch (SQLException e) {}
        if((parentCatId == 1) || (parentCatId == 2)) {
            if((parentCatId == 1) && (id == 5650)) {
                return "VEMER";
            } else if((parentCatId == 1) && (id == 5414)) {
                return "GRAESSLIN";
            } else if((parentCatId == 1) && (id == 6441)) {
                return "TEHNOPLAST";
            } else if((parentCatId == 1) && (id == 5933)) {
                return "RELECO";
            } else if((parentCatId == 1) && (id == 74)) {
                return "CITEL";
            } else if((parentCatId == 1) && (id == 5512)) {
                return "CBI";
            } else if((parentCatId == 1) && (id == 142)) {
                return "TELE";
            } else if((parentCatId == 1) && (id == 5535)) {
                return "SONDER";
            } else if((parentCatId == 1) && (id == 6321)) {
                return "Poligonspb";
            } else if((parentCatId == 1) && (id == 5818)) {
                return "RELEQUICK";
            } else if((parentCatId == 1) && (id == 5583)) {
                return "EMKO";
            } else if((parentCatId == 1) && (id == 5094)) {
                return "BENEDICT";
            } else if((parentCatId == 2) && (id == 4847)) {
                return "HUBER+SUHNER";
            } else {
                return getCategoryVendorFromId (parentCatId);
            }
        } else if((parentCatId == 0) || (parentCatId == 3)) {
            return "ANY_VENDORS";
        } else {
            return getCategoryVendorFromId (parentCatId);
        }
    }

    public static Integer getParentCatId(String categoryTitle) throws SQLException {
        Integer parentId = 0;
        try {
            ResultSet resultSet = PCGUIController.connection.getResult(
                    "select id from categories where title =\"" +
                            categoryTitle.replace("\"", "\\\"") + "\"" );
            while (resultSet.next()) {
                parentId = resultSet.getInt("id");
            }
        } catch (SQLException e) {}
        return parentId;
    }

    public static Integer getParentCatId(Integer categoryId) throws SQLException {
        Integer parentId = 0;
        try {
            ResultSet resultSet = PCGUIController.connection.getResult(
                    "select parent from categories where id =" + categoryId );
            while (resultSet.next()) {
                parentId = resultSet.getInt("parent");
            }
        } catch (SQLException e) {}
        return parentId;
    }

    public static ArrayList<Integer> getPropertyIdFromTitle (String title) {
        ArrayList<Integer> ids = new ArrayList<>();
        try {
            ResultSet resultSet = PCGUIController.connection.getResult(
                    "select id from properties where title =\"" +
                            title.replace("\"", "\\\"") + "\"" );
            while (resultSet.next()) {
                ids.add(resultSet.getInt("id"));
            }
        } catch (SQLException e) {}
        return ids;
    }

    public static Integer getPropertyTypeIdFromTitle (String title) {
        Integer id = 0;
        try {
            ResultSet resultSet = PCGUIController.connection.getResult(
                    "select id from property_types where title =\"" +
                            title.replace("\"", "\\\"") + "\"" );
            while (resultSet.next()) {
                id = resultSet.getInt("id");
            }
        } catch (SQLException e) {}
        return id;
    }

    public static int getProductKindIdFromTitle (String title) {
        int id = 0;
        try {
            ResultSet resultSet = PCGUIController.connection.getResult(
                    "select id from product_kinds where title =\"" +
                            title.replace("\"", "\\\"") + "\"" );
            while (resultSet.next()) {
                id = resultSet.getInt("id");
            }
        } catch (SQLException e) {}
        return id;
    }

    public static String getVendorFromProductTitle (String title) {
        String vendor_title = "";
        try {
            ResultSet resultSet = PCGUIController.connection.getResult(
                    "select title from vendors where id = (select vendor_id from products where title =\"" +
                            title.replace("\"", "\\\"") + "\")" );
            while (resultSet.next()) {
                vendor_title = resultSet.getString("title");
            }
        } catch (SQLException e) {
            AlertWindow.showErrorMessage(e.getMessage());
        }
        return vendor_title;
    }

    public static String getFilesPathByOwnerIdAndFileTypeId(String ownerTitle, ArrayList<Product> allProductsList, int fileTypeId) {
        String files_path = "";
        entities.Files file = new entities.Files();
        int ownerId = UtilPack.getProductIdFromTitle(ownerTitle, allProductsList);
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("from Files where ownerId =" +
                ownerId + " and fileTypeId =" + fileTypeId);
        List list = query.list();
        for(Object o : list) {
            file = (entities.Files) o;
        }
        session.close();
        /*try {
            ResultSet resultSet = PCGUIController.connection.getResult(
                    "select path from files where owner_id =" +
                            ownerId +
                            " and file_type_id =" + fileTypeId);
            while (resultSet.next()) {
                files_path = resultSet.getString("path");
            }
        } catch (SQLException e) {
            AlertWindow.showErrorMessage(e.getMessage());
        }*/
        return file.getPath();
    }

    public static Integer getProductIdFromTitle (String title, ArrayList<Product> allProductsList) {
        final Integer[] id = {0};
        allProductsList.stream().forEach((product) -> {
            if(product.getTitle().equals(title)) id[0] = product.getId();
        });
        return id[0];
    }

    public static String getProductTitleFromId (int id, ArrayList<Product> allProductsList) {
        final String[] title = {""};
        allProductsList.stream().forEach((product) -> {
            if(product.getId() == id) title[0] = product.getTitle();
        });
        return title[0];
    }

    public static Integer getProductCurrency (String title, ArrayList<Product> allProductsList) {
        final Integer[] id = {0};
        allProductsList.stream().forEach((product) -> {
            if(product.getTitle().equals(title)) id[0] = product.getCurrencyId();
        });
        return id[0];
    }

    public static int getCurrencyIdFromTitle (String title) {
        int id = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("from Currencies where title = :title");
        query.setParameter("title", title);
        List result = query.list();
        for (Iterator iterator = result.iterator(); iterator.hasNext();) {
            Currencies currency = (Currencies) iterator.next();
            id = currency.getId();
        }
        return id;
    }

    public static String normalize(String string) {
        //проверить корректность работы "normalize" позже
        if (string.contains("\t") || (string.contains("\n"))) {
            string.replace('\t', ' ');
            string.replace('\n', ' ');
        }
        String s = "";
        if ((string.contains("\""))) {
            String[] sub = string.split("\"");
            for (int i = 0; i < sub.length; i++) {
                if (i == 0) {
                    s = sub[i] + "\"";
                } else if (i == sub.length - 1) {
                    s = "\"" + sub[i];
                } else {
                    s = "\"" + sub[i] + "\"";
                }
                string += s;
            }
        }
        return string;
    }

    public static ArrayList<String> getAllProductTypes() {
        ArrayList<String> productTypes = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List result = session.createQuery("from ProductKinds").list();
        for(Iterator iterator = result.iterator(); iterator.hasNext();) {
            ProductKinds productKind = (ProductKinds) iterator.next();
            productTypes.add(productKind.getTitle());
        }
        session.close();
        return productTypes;
    }

    public static ArrayList<String> getAllCurrencies() {
        ArrayList<String> currencies = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List result = session.createQuery("from Currencies ").list();
        for(Iterator iterator = result.iterator(); iterator.hasNext();) {
            Currencies currency = (Currencies) iterator.next();
            currencies.add(currency.getTitle());
        }
        session.close();
        return currencies;
    }

    public static ArrayList<String> getAllVendors() {
        ArrayList<String> vendors = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List result = session.createQuery("from Vendors").list();
        for(Iterator iterator = result.iterator(); iterator.hasNext();) {
            Vendors vendor = (Vendors) iterator.next();
            vendors.add(vendor.getTitle());
        }
        session.close();
        return vendors;
    }

    public static ArrayList<Vendors> getAllVendorsEntities() {
        ArrayList<Vendors> vendors = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List result = session.createQuery("from Vendors").list();
        for(Iterator iterator = result.iterator(); iterator.hasNext();) {
            Vendors vendor = (Vendors) iterator.next();
            vendors.add(vendor);
        }
        session.close();
        return vendors;
    }

    public static ArrayList<SeriesItems> getAllSeries() {
        ArrayList<SeriesItems> seriesItems = new ArrayList();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List result = session.createQuery("from SeriesItems").list();
        for(Iterator iterator = result.iterator(); iterator.hasNext();) {
            SeriesItems s = (SeriesItems) iterator.next();
            seriesItems.add(s);
        }
        session.close();
        return seriesItems;
    }

    public static ArrayList<Integer> arrayChildren(Integer parent) {
        ArrayList<Integer> children = new ArrayList<>();
        try {
            ResultSet resultSet = PCGUIController.connection.getResult(
                    "select id from categories where parent =" + parent );
            while (resultSet.next()) {
                children.add(resultSet.getInt("parent"));
            }
        } catch (SQLException e) {}
        return children;
    }

    public static String cleanHtml(String rawHtml) {
        String cleanHtml = new String();
        String cutString1 = "<html dir=\"ltr\"><head></head><body contenteditable=\"true\">";
        String cutString2 = "</body></html>";
        cleanHtml = rawHtml.replace(cutString1, "");
        cleanHtml = cleanHtml.replace(cutString2, "");
        return cleanHtml;
    }

    public static void checkItemsSelected(CheckBoxTreeItem rootItem){
        for(Object item : rootItem.getChildren()){
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery("from Categories where title = :title");
            query.setParameter("title", ((CheckBoxTreeItem) item).getValue());
            List result = query.list();
            for (Iterator iterator = result.iterator(); iterator.hasNext();) {
                Categories category = (Categories) iterator.next();
                category.setPublished(((CheckBoxTreeItem) item).selectedProperty().getValue() ? 1 : 0);
                session.saveOrUpdate(category);
                tx.commit();
            }
            session.close();
        }
    }

    public static void copyFile(File source, File dest) throws IOException {
        Files.copy(source.toPath(), dest.toPath());
    }

    public static void startFTP(String filePath, String remotePlace){
        StandardFileSystemManager manager = new StandardFileSystemManager();
        String sshHost = "";
        String sshUser = "";
        String sshPass = "";
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("from Settings where title = :title and kind = :kind");
            query.setParameter("title", "serverSFTP");
            query.setParameter("kind", "SFTPSettings");
            List list = query.list();
            for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                Settings setting = (Settings) iterator.next();
                sshHost = setting.getTextValue();
            }
            Query query1 = session.createQuery("from Settings where title = :title and kind = :kind");
            query1.setParameter("title", "userSFTP");
            query1.setParameter("kind", "SFTPSettings");
            List list1 = query1.list();
            for (Iterator iterator = list1.iterator(); iterator.hasNext();) {
                Settings setting = (Settings) iterator.next();
                sshUser = setting.getTextValue();
            }
            Query query2 = session.createQuery("from Settings where title = :title and kind = :kind");
            query2.setParameter("title", "passwordSFTP");
            query2.setParameter("kind", "SFTPSettings");
            List list2 = query2.list();
            for (Iterator iterator = list2.iterator(); iterator.hasNext();) {
                Settings setting = (Settings) iterator.next();
                sshPass = setting.getTextValue();
            }
            session.close();

            sftp(("file://" + filePath.replace("\\", "/")), ("ssh://" + sshUser + ":" + sshPass + "@" + sshHost + remotePlace));
            AlertWindow.showInfo("Файл загружен на удаленный сервер.");
        }
        catch (Exception ex) {
            AlertWindow.showErrorMessage(ex.getMessage());
        }
        finally {
            manager.close();
        }
    }

    public static int stringToIntByChars(String string) {
        char[] chars = string.toCharArray();
        int orderNumber = 0;
        for (int i = 0; i < chars.length; i++) {
            int digit = ((int)chars[i] & 0xF);
            for (int j = 0; j < chars.length-1-i; j++) {
                digit *= 10;
            }
            orderNumber += digit;
        }
        return orderNumber;
    }

    public static SortedList<String> sortRussianList(ObservableList<String> items) {
        Collator collator = Collator.getInstance(Locale.forLanguageTag("ru-RU"));
        Collections.sort(items, collator);
        return new SortedList(items);
    }
    public static void click(javafx.scene.control.Control control) {
        java.awt.Point originalLocation = java.awt.MouseInfo.getPointerInfo().getLocation();
        javafx.geometry.Point2D buttonLocation = control.localToScreen(control.getLayoutBounds().getMinX(), control.getLayoutBounds().getMinY());
        try {
            java.awt.Robot robot = new java.awt.Robot();
            robot.mouseMove((int)buttonLocation.getX(), (int)buttonLocation.getY());
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
            robot.mouseMove((int) originalLocation.getX(), (int)originalLocation.getY());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
