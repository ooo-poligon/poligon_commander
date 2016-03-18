/*
 * 
 * 
 */
package utils;

import entities.FileTypes;
import entities.Files;
import entities.Products;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.TransientObjectException;

/**
 *
 * @author Igor Klekotnev
 */
public class ProductImage {
    public static void open(File file, GridPane gridPane, ImageView imageView) {
        if (gridPane.getChildren().size()!=0) {
            gridPane.getChildren().clear();
        }
        try {
            String localUrl = file.toURI().toURL().toString();
            imageView.setImage(new Image(localUrl));
            imageView.setFitHeight(250);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setCache(true);
            gridPane.getChildren().add(imageView);
        } catch (MalformedURLException ex) {}
    }
    public static void save(File file, String selectedProduct) {
        Integer ownerId = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        List ids = session.createSQLQuery("select id from products where title=\"" + selectedProduct + "\"").list();
        for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
            ownerId = (Integer) iterator.next();
        }
        List pictureList = session.createQuery("From Files where ownerId=" + ownerId).list();
        if (pictureList.isEmpty()) {
            Files pictureFile = new Files(file.getName(), file.getPath(), "Это изображение для " + selectedProduct, (new FileTypes(1)), (new Products(ownerId)));
            session.saveOrUpdate(pictureFile);
        } else {
            for (Iterator iterator = pictureList.iterator(); iterator.hasNext();) {
                Files pic = (Files) iterator.next();
                if ((pic.getFileTypeId().getId() == 1) && ((!pic.getName().equals(file.getName())) || (!pic.getPath().equals(file.getPath())))) {
                    pic.setName(file.getName());
                    pic.setPath(file.getPath());
                    pic.setDescription("Это изображение для " + selectedProduct);
                    session.saveOrUpdate(pic);
                }
            }            
        }
        tx.commit();
        session.close();
    }

    public static void save(String picPath, String selectedProduct) {
        Products product = new Products();
        Session sess = HibernateUtil.getSessionFactory().openSession();
        Query query = sess.createQuery("from Products where title = :title");
        query.setParameter("title", selectedProduct);
        List ids = query.list();
        for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
            Products p = (Products) iterator.next();
            product = p;
        }
        sess.close();
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            List pictureList = session.createQuery("From Files where ownerId=" + product.getId() + " and fileTypeId=" + 1).list();
            if (pictureList.isEmpty()) {
                Files pictureFile = new Files(picName(picPath), localWindowsPath(picPath), "Это изображение для " + selectedProduct, (new FileTypes(1)), product);
                session.save(pictureFile);
            } else {
                for (Iterator iterator = pictureList.iterator(); iterator.hasNext(); ) {
                    Files pic = (Files) iterator.next();
                    if ((pic.getFileTypeId().getId() == 1) && ((!pic.getName().equals(picName(picPath))) || (!pic.getPath().equals(localWindowsPath(picPath))))) {
                        pic.setName(picName(picPath));
                        pic.setPath(localWindowsPath(picPath));
                        pic.setDescription("Это изображение для " + selectedProduct);
                        pic.setOwnerId(product);
                        pic.setFileTypeId(new FileTypes(1));
                        session.saveOrUpdate(pic);
                    }
                }
            }
            tx.commit();
            session.close();
        } catch (TransientObjectException e) {}
    }

    private static String picName(String picPath) {
        ArrayList<String> pathParts = new ArrayList<>();
        for (int i = 0; i < picPath.split("/").length; i++) {
            pathParts.add(picPath.split("/")[i]);
        }
        return pathParts.get(picPath.split("/").length - 1);
    }

    private static String localWindowsPath(String picPath) {
        ArrayList<String> pathParts = new ArrayList<>();
        String localPath = "c:\\poligon_images";
        if (picPath.split("/").length==0) {
            return "";
        } else {
            for (int i = 0; i < picPath.split("/").length; i++) {
                localPath += "\\" + picPath.split("/")[i];
            }
        }
        return localPath;
    }
}
