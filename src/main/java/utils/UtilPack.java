package utils;

import entities.*;
import entities.Properties;
import entities.Settings;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeItem;
import main.Product;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.nio.file.Files;

/**
 * Created by Igor Klekotnev on 11.03.2016.
 */
public class UtilPack {
    public static int getCategoryIdFromTitle (String title) {
        int id = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("from Categories where title = :title");
        query.setParameter("title", title);
        List result = query.list();
        for (Iterator iterator = result.iterator(); iterator.hasNext();) {
            Categories category = (Categories) iterator.next();
            id = category.getId();
        }
        return id;
    }
    public static String getCategoryTitleFromId (int id) {
        String title = "";
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("from Categories where id = :id");
        query.setParameter("id", id);
        List result = query.list();
        for (Iterator iterator = result.iterator(); iterator.hasNext();) {
            Categories category = (Categories) iterator.next();
            title = category.getTitle();
        }
        return title;
    }
    public static String getCategoryVendorFromId (int id) {
        int parentCatId = 0;
        try {
            parentCatId = getParentCatId(id);
        } catch (SQLException e) {}
        System.out.println("parentCatId is " + parentCatId);
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
        Integer id = UtilPack.getCategoryIdFromTitle(categoryTitle);
        Integer parentId = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List response = session.createQuery("From Categories where id=" + id).list();
            for (Iterator iterator = response.iterator(); iterator.hasNext();) {
                Categories categorie = (Categories) iterator.next();
                parentId = categorie.getParent();
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }
        return parentId;
    }
    public static Integer getParentCatId(Integer categoryId) throws SQLException {
        Integer id = categoryId;
        Integer parentId = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List response = session.createQuery("From Categories where id=" + id).list();
            for (Iterator iterator = response.iterator(); iterator.hasNext();) {
                Categories categorie = (Categories) iterator.next();
                parentId = categorie.getParent();
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }
        return parentId;
    }
    public static Integer getPropertyIdFromTitle (String title) {
        Integer id = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("from Properties where title = :title");
        query.setParameter("title", title);
        List result = query.list();
        for (Iterator iterator = result.iterator(); iterator.hasNext();) {
            Properties property = (Properties) iterator.next();
            id = property.getId();
        }
        return id;
    }
    public static Integer getPropertyTypeIdFromTitle (String title) {
        Integer id = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("from PropertyTypes where title = :title");
        query.setParameter("title", title);
        List result = query.list();
        for (Iterator iterator = result.iterator(); iterator.hasNext();) {
            PropertyTypes propertyType = (PropertyTypes) iterator.next();
            id = propertyType.getId();
        }
        return id;
    }
    public static Integer getPropertyKindIdFromTitle(String selectedPropertiesKind) {
        Integer id = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("from ProductKinds where title = :title");
        query.setParameter("title", selectedPropertiesKind);
        List result = query.list();
        for (Iterator iterator = result.iterator(); iterator.hasNext();) {
            ProductKinds productKind = (ProductKinds) iterator.next();
            id = productKind.getId();
        }
        return id;
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
    public static int getProductKindIdFromTitle (String title) {
        int id = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("from ProductKinds where title = :title");
        query.setParameter("title", title);
        List result = query.list();
        for (Iterator iterator = result.iterator(); iterator.hasNext();) {
            ProductKinds productKind = (ProductKinds) iterator.next();
            id = productKind.getId();
        }
        return id;
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
    public static ArrayList<Series> getAllSeries() {
        ArrayList<Series> series = new ArrayList();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List result = session.createQuery("from Series").list();
        for(Iterator iterator = result.iterator(); iterator.hasNext();) {
            Series serie = (Series) iterator.next();
            series.add(serie);
        }
        session.close();
        return series;
    }
    public static ArrayList<TreeItem> treeItemChildren(TreeItem<ProductPropertiesTableView> item) {
        String itemTitle = item.getValue().getTitle();
        ArrayList<TreeItem> childTreeItems = new ArrayList<>(10);
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from Properties where propertyTypeId=" + UtilPack.getPropertyTypeIdFromTitle(itemTitle)).list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            Properties property = (Properties) iterator.next();
            childTreeItems.add(new TreeItem(new ProductPropertiesTableView(property.getTitle())));
        }
        session.close();
        return childTreeItems;
    }
    public static ArrayList<Integer> arrayChildren(Integer parent) {
        ArrayList<Integer> children = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("From Categories where parent=" + parent).list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            Categories cat = (Categories) iterator.next();
            children.add(cat.getId());
        }
        session.close();
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


            /*
            try {
                PCGUIController.connection.getUpdateResult("update categories set published=" +
                        (((CheckBoxTreeItem) item).selectedProperty().getValue() ? 1 : 0) + "where title=" +
                        ((CheckBoxTreeItem) item).getValue());
            } catch (SQLException e) {}
            */
            //System.out.println(((CheckBoxTreeItem) item).getValue() + " " + ((CheckBoxTreeItem) item).selectedProperty().getValue());

            //checkItemsSelected((CheckBoxTreeItem) item);

        }

    }
    public static void copyFile(File source, File dest) throws IOException {
        Files.copy(source.toPath(), dest.toPath());
    }
    public static boolean startFTP(String filepath, String remotePath){
        StandardFileSystemManager manager = new StandardFileSystemManager();
        String serverAddress = "";
        String userId = "";
        String password = "";
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            List list = session.createQuery("from Settings where title = \"serverSFTP\" and kind = \"SFTPSettings\"").list();
            for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                Settings setting = (Settings) iterator.next();
                serverAddress = setting.getTextValue();
            }
            session.close();

            Session session1 = HibernateUtil.getSessionFactory().openSession();
            List list1 = session1.createQuery("from Settings where title = \"userSFTP\" and kind = \"SFTPSettings\"").list();
            for (Iterator iterator = list1.iterator(); iterator.hasNext();) {
                Settings setting = (Settings) iterator.next();
                userId = setting.getTextValue();
            }
            session1.close();

            Session session2 = HibernateUtil.getSessionFactory().openSession();
            List list2 = session2.createQuery("from Settings where title = \"passwordSFTP\" and kind = \"SFTPSettings\"").list();
            for (Iterator iterator = list2.iterator(); iterator.hasNext();) {
                Settings setting = (Settings) iterator.next();
                password = setting.getTextValue();
            }
            session2.close();

            //check if the file exists
            File file = new File(filepath);
            if (!file.exists())
                throw new RuntimeException("Error. Local file not found");

            //Initializes the file manager
            manager.init();

            //Setup our SFTP configuration
            FileSystemOptions opts = new FileSystemOptions();
            SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");
            SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true);
            SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 10000);

            //Create the SFTP URI using the host name, userid, password,  remote path and file name
            String sftpUri = "sftp://" + userId + ":" + password +  "@" + serverAddress + "/" +
                    remotePath;

            // Create local file object
            FileObject localFile = manager.resolveFile(file.getAbsolutePath());

            // Create remote file object
            FileObject remoteFile = manager.resolveFile(sftpUri, opts);

            // Copy local file to sftp server
            remoteFile.copyFrom(localFile, Selectors.SELECT_SELF);
            System.out.println("File upload successful");

        }
        catch (Exception ex) {
            return false;
        }
        finally {
            manager.close();
        }
        return true;
    }
}
