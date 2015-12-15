/*
 * 
 * 
 */
package utils;

import deprecated.DBConnection;
import entities.ImportFields;
import entities.ProductKinds;
import entities.Products;
import entities.Series;
import entities.Vendors;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import main.PCGUIController;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;

/**
 *
 * @author Igor Klekotnev
 */
public class XLSToDBImport {

    private final ArrayList<ArrayList<String>> allCompareDetails;
    private ArrayList<String> titlesColumn = new ArrayList<>();
    private ArrayList<String> descriptionsColumn = new ArrayList<>();
    private ArrayList<String> anonsesColumn = new ArrayList<>();
    private ArrayList<String> articlesColumn = new ArrayList<>();
    private ArrayList<String> availableColumn = new ArrayList<>();
    private ArrayList<String> deliveryTimeColumn = new ArrayList<>();
    private ArrayList<String> eanColumn = new ArrayList<>();
    private ArrayList<String> outdatedColumn = new ArrayList<>();
    private ArrayList<String> pricesColumn = new ArrayList<>();
    private ArrayList<String> vendorsColumn = new ArrayList<>();
    private ArrayList<String> seriesColumn = new ArrayList<>();
    private ArrayList<String> kindsColumn = new ArrayList<>();

    public XLSToDBImport(ArrayList<ArrayList<String>> allCompareDetails) {
        this.allCompareDetails = allCompareDetails;
    }
    public void startImport(ArrayList<ArrayList<String>> allImportXLSContent, ObservableList<ImportFields> importFields, ObservableList<String> allProducts) {
        allCompareDetails.stream().forEach((compareDetail) -> {
            if (compareDetail.get(1).equals("Наименование продукта")) {
                titlesColumn.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
            }
        });
        //try {
        allCompareDetails.stream().forEach((compareDetail) -> {
            switch (compareDetail.get(1)) {
            case("Краткое описание продукта"):
                descriptionsColumn.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
                for (int i = 0; i < descriptionsColumn.size(); i++) {
                    if (allProducts.contains(titlesColumn.get(i))) {
                        updateProducts("description", descriptionsColumn.get(i), titlesColumn.get(i));                        
                    } else {
                        insertProduct(titlesColumn.get(i));
                        updateProducts("description", descriptionsColumn.get(i), titlesColumn.get(i));                        
                    }
                }                    
                break;
            case("Анонс продукта"):
                anonsesColumn.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
                for (int i = 0; i < anonsesColumn.size(); i++) {
                    updateProducts("anons", anonsesColumn.get(i), titlesColumn.get(i));
                }
                break;
            case("Артикул продукта"):
                articlesColumn.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
                for (int i = 0; i < articlesColumn.size(); i++) {
                    //System.out.println("article - "+ articlesColumn.get(i) + " - " + titlesColumn.get(i));
                    updateProducts("article", articlesColumn.get(i), titlesColumn.get(i));
                }                    
                break;
            case("Доступность для заказа"):
                availableColumn.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
                for (int i = 0; i < availableColumn.size(); i++) {
                    if (!(availableColumn.get(i).equals(""))) {
                        updateProducts("available", Integer.parseInt(availableColumn.get(i)), titlesColumn.get(i));                        
                    } else {
                    updateProducts("available", 0, titlesColumn.get(i));                        
                    }
                }                    
                break; 
            case("Сроки поставки"):
                deliveryTimeColumn.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
                for (int i = 0; i < deliveryTimeColumn.size(); i++) {
                    updateProducts("delivery_time", deliveryTimeColumn.get(i), titlesColumn.get(i));
                }                    
                break;
            case("EAN продукта"):
                eanColumn.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
                for (int i = 0; i < eanColumn.size(); i++) {
                    updateProducts("ean", eanColumn.get(i), titlesColumn.get(i));
                }                    
                break;
            case("Снято с производства"):
                outdatedColumn.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
                for (int i = 0; i < outdatedColumn.size(); i++) {
                    if (!(outdatedColumn.get(i).equals(""))) {
                        updateProducts("outdated", Integer.parseInt(outdatedColumn.get(i)), titlesColumn.get(i));                        
                    } else {
                    updateProducts("outdated", 0, titlesColumn.get(i));                        
                    }                    
                }                    
                break;
            case("Базовая стоимость"):
                pricesColumn.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
                for (int i = 0; i < pricesColumn.size(); i++) {
                    
                    
                    if (!(pricesColumn.get(i).equals(""))) {
                        updateProducts("price", Double.parseDouble(pricesColumn.get(i).replace(",",".")), titlesColumn.get(i));
                        /*
                        if (!(pricesColumn.get(i).contains(" "))) {
                            //System.out.println(Double.parseDouble(pricesColumn.get(i)));
                            updateProducts("price", Double.parseDouble(pricesColumn.get(i)), titlesColumn.get(i));                            
                        } else {
                            ArrayList<String> arr = new ArrayList<>();
                            for (int k = 0; k < pricesColumn.get(i).split(" ").length; k++) {
                                arr.add(pricesColumn.get(i).split(" ")[k]);
                            }
                            for (int m = 0; m < arr.size(); m++) {
                                if (
                                    arr.get(m).startsWith("0") ||
                                    arr.get(m).startsWith("1") ||
                                    arr.get(m).startsWith("2") ||
                                    arr.get(m).startsWith("3") ||
                                    arr.get(m).startsWith("4") ||
                                    arr.get(m).startsWith("5") ||
                                    arr.get(m).startsWith("6") ||
                                    arr.get(m).startsWith("7") ||
                                    arr.get(m).startsWith("8") ||
                                    arr.get(m).startsWith("9")) {
                                    if (!arr.get(m).contains(",")) {
                                        //System.out.println(Double.parseDouble(element));
                                        updateProducts("price", Double.parseDouble(arr.get(m)), titlesColumn.get(i));                                        
                                    } else {
                                        //System.out.println(Double.parseDouble(element.replace(",",".")));
                                        updateProducts("price", Double.parseDouble(arr.get(m).replace(",",".")), titlesColumn.get(i));
                                    }
                                }
                            }
                        }*/
                    } else {
                    updateProducts("price", 0.0, titlesColumn.get(i));                        
                    }
                    
                }                    
                break; 
            case("Производитель продукта"):
                vendorsColumn.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
                for (int i = 0; i < vendorsColumn.size(); i++) {
                    updateVendors(vendorsColumn.get(i), titlesColumn.get(i));
                }                    
                break;   
            case("Серия продукта"):
                seriesColumn.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
                for (int i = 0; i < seriesColumn.size(); i++) {
                    updateSeries(seriesColumn.get(i), titlesColumn.get(i));
                }                    
                break;
            case("Тип продукта"):
                kindsColumn.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
                for (int i = 0; i < kindsColumn.size(); i++) {
                    updateKinds(kindsColumn.get(i), titlesColumn.get(i));
                }                    
                break;
            }
        });
        //} catch (NumberFormatException ne) {}
    }
    private ArrayList<String> getColumnByHeader(ArrayList<ArrayList<String>> allImportXLSContent, String header) {
        Integer keyIndex = 0;
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < allImportXLSContent.size(); i++) {
            for (int j = 0; j < allImportXLSContent.get(i).size(); j++) {
                if (allImportXLSContent.get(i).get(j).equals(header)) {
                    keyIndex = i;
                }                
            }
        }
        list.addAll(allImportXLSContent.get(keyIndex));
        list.remove(header);
        return list;
    }         
    private void updateProducts(String field, String value, String productTitle) {
        if (!(value.equals("") || value.equals(null))) {
            Session session = HibernateUtil.getSessionFactory().openSession();   
            Transaction tx = session.beginTransaction();
            try {        
                String hql = "UPDATE Products set " + field + " = :value "  + 
                             "WHERE title = :title";
                Query query = session.createQuery(hql);
                query.setParameter("value", value);
                query.setParameter("title", productTitle);
                query.executeUpdate();        
            } catch (HibernateException e) {
            } finally {
                tx.commit();
                session.close();
            }        
        }
    }
    private void updateProducts(String field, Integer value, String productTitle) {
        if (!(value.equals("") || value.equals(null))) {
            Session session = HibernateUtil.getSessionFactory().openSession();   
            Transaction tx = session.beginTransaction();
            try {        
                String hql = "UPDATE Products set " + field + " = :value "  + 
                             "WHERE title = :title";
                Query query = session.createQuery(hql);
                query.setParameter("value", value);
                query.setParameter("title", productTitle);
                query.executeUpdate();        
            } catch (HibernateException e) {
            } finally {
                tx.commit();
                session.close();
            }        
        }
    }
    private void updateProducts(String field, Double value, String productTitle) {
        if (!(value.equals("") || value.equals(null))) {
            Session session = HibernateUtil.getSessionFactory().openSession();   
            Transaction tx = session.beginTransaction();
            try {        
                String hql = "UPDATE Products set " + field + " = :value "  + 
                             "WHERE title = :title";
                Query query = session.createQuery(hql);
                query.setParameter("value", value);
                query.setParameter("title", productTitle);
                query.executeUpdate();        
            } catch (HibernateException e) {
            } finally {
                tx.commit();
                session.close();
            }        
        }
    }    
    private void updateVendors(String value, String productTitle) {
        if (!(value.equals("") || value.equals(null))) {
            ArrayList<String> notInDBProducts = new ArrayList<>();
            ArrayList<Vendors> vendorsList = new ArrayList<>();
            Integer id = 0;            
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Long countL = (Long) session.createQuery("select count(*) from Vendors").uniqueResult();
            Integer count = (int)(long) countL;
            for (int i = 0; i < count; i++) {
                Vendors vendor = (Vendors) session.get(Vendors.class, i+1);
                vendorsList.add(vendor);
            }            
            List ids = session.createSQLQuery("select id from products where title=\"" + productTitle + "\"").list();
            for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
                id = (Integer) iterator.next();
            }
            if (!(id == 0)) {
                Products product = (Products) session.load(Products.class, id);
                for (Vendors vendor: vendorsList) {
                    try {
                        if (vendor.getTitle().equals(value)) {
                            product.setVendor(vendor);  
                        }
                    } catch (NullPointerException ne) {}                        
                }
                session.save(product);                
            } else {
                notInDBProducts.add(productTitle);
            }
            session.getTransaction().commit();
            session.close();
        }
    }
    private void updateSeries(String value, String productTitle) {
        if (!(value.equals("") || value.equals(null))) {
            ArrayList<String> notInDBProducts = new ArrayList<>();
            ArrayList<Series> seriesList = new ArrayList<>();
            Integer id = 0;            
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Long countL = (Long) session.createQuery("select count(*) from Series").uniqueResult();
            Integer count = (int)(long) countL;
            for (int i = 0; i < count; i++) {
                Series serie = (Series) session.get(Series.class, i+1);
                seriesList.add(serie);
            }            
            List ids = session.createSQLQuery("select id from products where title=\"" + productTitle + "\"").list();
            for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
                id = (Integer) iterator.next();
            }
            if (!(id == 0)) {
                Products product = (Products) session.get(Products.class, id);
                for (Series serie: seriesList) {
                    try {
                        if (serie.getTitle().equals(value)) {
                            product.setSerie(serie);  
                        }
                    } catch (NullPointerException ne) {}                    
                }
                session.save(product);
            } else {
                notInDBProducts.add(productTitle);
            }
            session.getTransaction().commit();
            session.close();
        }
    }
    private void updateKinds(String value, String productTitle) {
        if (!(value.equals("") || value.equals(null))) {
            ArrayList<String> notInDBProducts = new ArrayList<>();
            ArrayList<ProductKinds> kindsList = new ArrayList<>();
            Integer id = 0;
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Long countL = (Long) session.createQuery("select count(*) from ProductKinds").uniqueResult();
            Integer count = (int)(long) countL;
            for (int i = 0; i < count; i++) {
                ProductKinds kind = (ProductKinds) session.get(ProductKinds.class, i+1);
                kindsList.add(kind);
            }            
            List ids = session.createSQLQuery("select id from products where title=\"" + productTitle + "\"").list();
            for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
                id = (Integer) iterator.next();
            }
            if (!(id == 0)) {
                Products product = (Products) session.get(Products.class, id);
                for (ProductKinds kind: kindsList) {
                    try {
                        if (kind.getTitle().equals(value)) {
                            product.setProductKindId(kind);  
                        }
                    } catch (NullPointerException ne) {}
                }
                session.save(product);                
            } else {
                notInDBProducts.add(productTitle);
            }
            session.getTransaction().commit();
            session.close();
        }
    } 
    
    private void insertProduct(String productTitle) {
        /*
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        
        Products newProduct = new Products();
        newProduct.setTitle(productTitle);
         
        //Save the employee in database
        session.save(newProduct);
 
        //Commit the transaction
        session.getTransaction().commit();
        HibernateUtil.shutdown(); 
                */
    }
}
