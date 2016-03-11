package utils;

import entities.Categories;
import entities.ProductKinds;
import entities.PropertyTypes;
import main.Product;
import org.hibernate.Query;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Igor Klekotnev on 11.03.2016.
 */
public class UtilPack {
    public static int getCategoryIdFromTitle (String title) throws SQLException {
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
    public static String getCategoryTitleFromId (int id) throws SQLException {
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
    public static Integer getPropertyTypeIdFromTitle (String title) throws SQLException {
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
    public static Integer getPropertyKindIdFromTitle(String selectedPropertiesKind) throws SQLException {
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
    public static Integer getProductIdFromTitle (String title, ArrayList<Product> allProductsList) throws SQLException {
        final Integer[] id = {0};
        allProductsList.stream().forEach((product) -> {
            if(product.getTitle().equals(title)) id[0] = product.getId();
        });
        return id[0];
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
}
