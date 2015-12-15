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
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Igor Klekotnev
 */
public class ProductImage {
    public static void open(File file, GridPane gridPane, ImageView imageView) {
            gridPane.getChildren().clear();        
        try {
            String localUrl = file.toURI().toURL().toString();
            imageView.setImage(new Image(localUrl));
            //imageView.setFitWidth(gridPane.getWidth());
            imageView.setFitHeight(250);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setCache(true);
            gridPane.getChildren().add(imageView);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ProductImage.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    }
}
