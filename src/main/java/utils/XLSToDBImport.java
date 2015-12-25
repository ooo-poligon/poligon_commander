/*
 * 
 * 
 */
package utils;

import deprecated.DBConnection;
import entities.*;

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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
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
    private ArrayList<String> categoriesColumn = new ArrayList<>();

    public XLSToDBImport(ArrayList<ArrayList<String>> allCompareDetails) {
        this.allCompareDetails = allCompareDetails;
    }
    public void startImport(ArrayList<ArrayList<String>> allImportXLSContent, ObservableList<ImportFields> importFields, ObservableList<String> allProducts) {
        ArrayList<String> newProductsTitles = new ArrayList<>();
        ArrayList<String> titlesColumnUnchecked = new ArrayList<>();
        allCompareDetails.stream().forEach((compareDetail) -> {
            if (compareDetail.get(1).equals("Наименование продукта")) {
                titlesColumnUnchecked.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
            }
        });
        titlesColumnUnchecked.stream().forEach((item) -> {
            if (item.contains("?")) {
                System.out.println(item);
                item.replace('?', '_');
                titlesColumn.add(item);
            } else if (item.contains(":")) {
                item.replace(':', ' ');
                titlesColumn.add(item);
            } else if (item.contains(":")) {
                item.replace(':', ' ');
                titlesColumn.add(item);
            } else {
                titlesColumn.add(item);
            }
        });
        for (int i = 0; i < titlesColumn.size(); i++) {
            if (!allProducts.contains(titlesColumn.get(i))) {
                newProductsTitles.add(titlesColumn.get(i));
            }
        }
        if (!newProductsTitles.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Внимание!");
            alert.setContentText("Обнаружена номенклатура, не содержащаяся в базе данных.\n\nЕсли Вы добавляете новые " +
                    "позиции, убедитесь, что \nв каталоге продукции создана категория для данной номенклатуры, и что эта категория " +
                    "не содержит других вложенных категорий.\nПосле этого оформите файл для импорта таким образом,\nчтобы в нём содержалась " +
                    "колонка с заголовком \"parent\" (без кавычек, латиницей, в нижнем регистре), содержащая названия категорий, сопоставленные " +
                    "каждому новому элементу номенклатуры.(На элементы номенклатуры, уже находящиеся в базе данных, содержимое этой колонки НЕ ПОВЛИЯЕТ).\n" +
                    "В случае, если название категории обнаружено не будет,\nили не будет идентично созданному Вами на предыдущем шаге процесса импорта, номенклатура будет " +
                    "размещена\nв категории \"Без названия\", находящейся в корне каталога продукции.\nТакже, в случае импорта новой номенклатуры, " +
                    "необходимо сопоставить с базой данных колонки, содержащие производителей и базовые цены для каждого нового товара. Их наименование в файле может быть произвольным.\n\n Если эти условия выполнены, и Вы " +
                    "желаете продолжить процедуру импорта, нажмите \"OK\". Для отмены нажмите \"Cancel\".");
            alert.setTitle("Обнаружена новая номенклатура!");
            alert.showAndWait();
            if (alert.getResult().equals(ButtonType.APPLY.OK)) {
                importNewProducts(newProductsTitles);
                updateExistProducts(allImportXLSContent);
            } else {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setHeaderText("Внимание!");
                alert1.setContentText("Процесс импорта будет остановлен.");
                alert1.showAndWait();
            }
            } else {
                updateExistProducts(allImportXLSContent);
        }
    }

    private void updateExistProducts(ArrayList<ArrayList<String>> allImportXLSContent) {
        try {
            allCompareDetails.stream().forEach((compareDetail) -> {
                switch (compareDetail.get(1)) {
                    case ("Краткое описание продукта"):
                        descriptionsColumn.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
                        for (int i = 0; i < descriptionsColumn.size(); i++) {
                            updateProducts("description", descriptionsColumn.get(i), titlesColumn.get(i));
                        }
                        break;
                    case ("Анонс продукта"):
                        anonsesColumn.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
                        for (int i = 0; i < anonsesColumn.size(); i++) {
                            updateProducts("anons", anonsesColumn.get(i), titlesColumn.get(i));
                        }
                        break;
                    case ("Артикул продукта"):
                        articlesColumn.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
                        for (int i = 0; i < articlesColumn.size(); i++) {
                            //System.out.println("article - "+ articlesColumn.get(i) + " - " + titlesColumn.get(i));
                            updateProducts("article", articlesColumn.get(i), titlesColumn.get(i));
                        }
                        break;
                    case ("Доступность для заказа"):
                        availableColumn.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
                        for (int i = 0; i < availableColumn.size(); i++) {
                            if (!(availableColumn.get(i).equals(""))) {
                                updateProducts("available", Integer.parseInt(availableColumn.get(i)), titlesColumn.get(i));
                            } else {
                                updateProducts("available", 0, titlesColumn.get(i));
                            }
                        }
                        break;
                    case ("Сроки поставки"):
                        deliveryTimeColumn.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
                        for (int i = 0; i < deliveryTimeColumn.size(); i++) {
                            updateProducts("delivery_time", deliveryTimeColumn.get(i), titlesColumn.get(i));
                        }
                        break;
                    case ("EAN продукта"):
                        eanColumn.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
                        for (int i = 0; i < eanColumn.size(); i++) {
                            updateProducts("ean", eanColumn.get(i), titlesColumn.get(i));
                        }
                        break;
                    case ("Снято с производства"):
                        outdatedColumn.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
                        for (int i = 0; i < outdatedColumn.size(); i++) {
                            if (!(outdatedColumn.get(i).equals(""))) {
                                updateProducts("outdated", Integer.parseInt(outdatedColumn.get(i)), titlesColumn.get(i));
                            } else {
                                updateProducts("outdated", 0, titlesColumn.get(i));
                            }
                        }
                        break;
                    case ("Базовая стоимость"):
                        pricesColumn.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
                        for (int i = 0; i < pricesColumn.size(); i++) {
                            if (!(pricesColumn.get(i).equals(""))) {
                                updateProducts("price", Double.parseDouble(pricesColumn.get(i).replace(",", ".")), titlesColumn.get(i));
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
                    case ("Производитель продукта"):
                        vendorsColumn.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
                        for (int i = 0; i < vendorsColumn.size(); i++) {
                            updateVendors(vendorsColumn.get(i), titlesColumn.get(i));
                        }
                        break;
                    case ("Серия продукта"):
                        seriesColumn.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
                        for (int i = 0; i < seriesColumn.size(); i++) {
                            updateSeries(seriesColumn.get(i), titlesColumn.get(i));
                        }
                        break;
                    case ("Тип продукта"):
                        kindsColumn.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
                        for (int i = 0; i < kindsColumn.size(); i++) {
                            updateKinds(kindsColumn.get(i), titlesColumn.get(i));
                        }
                        break;
                    case ("Категория продукта"):
                        categoriesColumn.addAll(getColumnByHeader(allImportXLSContent, compareDetail.get(0)));
                        for (int i = 0; i < categoriesColumn.size(); i++) {
                            updateCategories(categoriesColumn.get(i), titlesColumn.get(i));
                        }
                        break;
                }
            });
        } catch (NumberFormatException ne) {
        }
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
            List ids = session.createSQLQuery("select id from products where title=\"" + productTitle.replace(':', ' ').replace('?', '_') + "\"").list();
            for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
                id = (Integer) iterator.next();
            }
            if (!(id == 0)) {
                Products product = (Products) session.load(Products.class, id);
                for (Vendors vendor: vendorsList) {
                    try {
                        if (vendor.getTitle().equals(value)) {
                            product.setVendor(vendor);
                            session.save(product);
                        }
                    } catch (NullPointerException ne) {}                        
                }
            } else {
                notInDBProducts.add(productTitle);
            }
            session.getTransaction().commit();
            session.close();
        }
    }
    private Vendors setVendors(String value, String productTitle) {
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
            List ids = session.createSQLQuery("select id from products where title=\"" + productTitle.replace(':', ' ').replace('?', '_') + "\"").list();
            for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
                id = (Integer) iterator.next();
            }
            if (!(id == 0)) {
                Products product = (Products) session.load(Products.class, id);
                for (Vendors vendor: vendorsList) {
                    try {
                        if (vendor.getTitle().equals(value)) {
                            return vendor;
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
        return null;
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
            List ids = session.createSQLQuery("select id from products where title=\"" + productTitle.replace(':', ' ').replace('?', '_') + "\"").list();
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
            List ids = session.createSQLQuery("select id from products where title=\"" + productTitle.replace(':', ' ').replace('?', '_') + "\"").list();
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

    private ProductKinds setKinds(String value, String productTitle) {
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
            List ids = session.createSQLQuery("select id from products where title=\"" + productTitle.replace(':', ' ').replace('?', '_') + "\"").list();
            for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
                id = (Integer) iterator.next();
            }
            if (!(id == 0)) {
                Products product = (Products) session.get(Products.class, id);
                for (ProductKinds kind: kindsList) {
                    try {
                        if (kind.getTitle().equals(value)) {
                            return kind;
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
        return null;
    }

    private void updateCategories(String value, String productTitle) {
        if (!(value.equals("") || value.equals(null))) {
            ArrayList<String> notInDBProducts = new ArrayList<>();
            ArrayList<Categories> catList = new ArrayList<>();
            Integer id = 0;
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Long countL = (Long) session.createQuery("select count(*) from Categories").uniqueResult();
            Integer count = (int)(long) countL;
            for (int i = 0; i < count; i++) {
                Categories cat = (Categories) session.get(Categories.class, i+1);
                catList.add(cat);
            }
            List ids = session.createSQLQuery("select id from products where title=\"" + productTitle.replace(':', ' ').replace('?', '_') + "\"").list();
            for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
                id = (Integer) iterator.next();
            }
            if (!(id == 0)) {
                Products product = (Products) session.get(Products.class, id);
                for (Categories cat: catList) {
                    try {
                        if (cat.getTitle().equals(value)) {
                            product.setCategoryId(cat);
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

    private Categories setCategoryId(String value, String productTitle) {
        if (!(value.equals("") || value.equals(null))) {
            ArrayList<String> notInDBProducts = new ArrayList<>();
            ArrayList<Categories> catList = new ArrayList<>();
            Integer id = 0;
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Long countL = (Long) session.createQuery("select count(*) from Categories").uniqueResult();
            Integer count = (int)(long) countL;
            for (int i = 0; i < count; i++) {
                Categories cat = (Categories) session.get(Categories.class, i+1);
                catList.add(cat);
            }
            List ids = session.createSQLQuery("select id from products where title=\"" + productTitle.replace(':', ' ').replace('?', '_') + "\"").list();
            for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
                id = (Integer) iterator.next();
            }
            if (!(id == 0)) {
                Products product = (Products) session.get(Products.class, id);
                for (Categories cat: catList) {
                    try {
                        if (cat.getTitle().equals(value)) {
                            return cat;
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
        return null;
    }

    private Integer getCategoryIdFromTitle (String title) {
        Integer id = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List ids = session.createSQLQuery("select id from categories where title=\"" + title + "\"").list();
            for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
                id = (Integer) iterator.next();
                return id;
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }
        return id;
    }

    private void importNewProducts(ArrayList<String> newProductsTitles) {
        for (int i = 0; i < newProductsTitles.size(); i++) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Products newProduct = new Products();
            newProduct.setTitle(newProductsTitles.get(i));
            newProduct.setPrice(0.0);
            newProduct.setCategoryId(new Categories(4, "Без названия"));
            newProduct.setProductKindId(new ProductKinds(1, "без определения"));
            newProduct.setVendor(new Vendors(1, "TELE"));
            session.save(newProduct);
            session.getTransaction().commit();
            session.close();
        }
    }
}
