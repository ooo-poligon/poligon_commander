package utils;

import entities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import main.Product;
import org.hibernate.Query;
import org.hibernate.Session;
import treetableviews.PropertiesTreeTableView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    public static ArrayList<TreeItem> treeItemChildren(TreeItem<PropertiesTreeTableView> item) {
        String itemTitle = item.getValue().getTitle();
        ArrayList<TreeItem> childTreeItems = new ArrayList<>(10);
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from Properties where propertyTypeId=" + UtilPack.getPropertyTypeIdFromTitle(itemTitle)).list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            Properties property = (Properties) iterator.next();
            childTreeItems.add(new TreeItem(new PropertiesTreeTableView(property.getTitle())));
        }
        session.close();
        return childTreeItems;
    }
    public static String cleanHtml(String rawHtml) {
        String cleanHtml = new String();
        String cutString1 = "<html dir=\"ltr\"><head></head><body contenteditable=\"true\">";
        String cutString2 = "</body></html>";
        cleanHtml = rawHtml.replace(cutString1, "");
        cleanHtml = cleanHtml.replace(cutString2, "");
        return cleanHtml;
    }
}
