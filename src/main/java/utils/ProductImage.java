/*
 * 
 * 
 */
package utils;

import entities.FileTypes;
import entities.Files;
import entities.Products;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import main.PCGUIController;
import modalwindows.AlertWindow;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.TransientObjectException;
import org.hibernate.cfg.Settings;
import settings.SFTPSettings;

import static utils.SshUtils.sftp;

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
            //if (!System.getProperty("os.name").contains("Windows")) {
            //    String unixPath = "/" + file.getAbsolutePath();
            //    file = new File(unixPath);
            //    URI uri = file.toURI();
            //    URL url = uri.toURL();
            //    localUrl = url.toString().replace("file:", "file://");
            //}
            imageView.setImage(new Image(localUrl));
            imageView.setFitHeight(220);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setCache(true);
            gridPane.getChildren().add(imageView);
        } catch (MalformedURLException ex) {}
    }
    public static void save(File inFile, String selectedProduct) {
        File file = null;
        try {
            file = new File(copyToPlace(inFile, selectedProduct, "device", null));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Integer ownerId = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createSQLQuery("select id from products where title= :title");
        query.setParameter("title", selectedProduct);
        List ids = query.list();
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

    public static void save(String inPicPath, String selectedProduct) {
        String picPath = null;
        try {
            picPath = copyToPlace(new File(inPicPath), selectedProduct, "device", null);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        } catch (TransientObjectException e) {
            AlertWindow.showErrorMessage("TransientObjectException");
        }
    }

    public static void saveDimImage(File inFile, String selectedProduct) {
        File file = null;
        try {
            file = new File(copyToPlace(inFile, selectedProduct, "dim", null));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Integer ownerId = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        List ids = session.createSQLQuery("select id from products where title=\"" + selectedProduct + "\"").list();
        for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
            ownerId = (Integer) iterator.next();
        }
        List pictureList = session.createQuery("From Files where ownerId=" + ownerId + "and file_type_id=" + 4).list();
        if (pictureList.isEmpty()) {
            Files pictureFile = new Files(file.getName(), file.getPath(), "Это схема габаритов для " + selectedProduct, (new FileTypes(4)), (new Products(ownerId)));
            session.saveOrUpdate(pictureFile);
        } else {
            for (Iterator iterator = pictureList.iterator(); iterator.hasNext();) {
                Files pic = (Files) iterator.next();
                if ((pic.getFileTypeId().getId() == 4) &&
                        ((!pic.getName().equals(file.getName())) ||
                                (!pic.getPath().equals(file.getPath())))) {
                    pic.setName(file.getName());
                    pic.setPath(file.getPath());
                    pic.setDescription("Это схема габаритов для " + selectedProduct);
                    session.saveOrUpdate(pic);
                }
            }
        }
        tx.commit();
        session.close();
    }

    public static void savePlugImage(File inFile, String selectedProduct, int plugNumber) {
        File file = null;
        try {
            file = new File(copyToPlace(inFile, selectedProduct, "plug", plugNumber));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Integer ownerId = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        List ids = session.createSQLQuery("select id from products where title=\"" + selectedProduct + "\"").list();
        for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
            ownerId = (Integer) iterator.next();
        }
        int fileTypeId = 3;
        if (plugNumber == 2) fileTypeId = 7;
        List pictureList = session.createQuery("From Files where ownerId=" + ownerId + "and file_type_id=" + fileTypeId).list();
        if (pictureList.isEmpty()) {
            Files pictureFile = new Files(file.getName(), file.getPath(), "Это схема подключения №" + plugNumber + " для " + selectedProduct, (new FileTypes(fileTypeId)), (new Products(ownerId)));
            session.saveOrUpdate(pictureFile);
        } else {
            for (Iterator iterator = pictureList.iterator(); iterator.hasNext();) {
                Files pic = (Files) iterator.next();
                if ((pic.getFileTypeId().getId() == fileTypeId) &&
                        ((!pic.getName().equals(file.getName())) ||
                                (!pic.getPath().equals(file.getPath())))) {
                    pic.setName(file.getName());
                    pic.setPath(file.getPath());
                    pic.setDescription("Это схема подключения №" + plugNumber + " для " + selectedProduct);
                    session.saveOrUpdate(pic);
                }
            }
        }
        tx.commit();
        session.close();
    }

    private static String picName(String picPath) {
        ArrayList<String> pathParts = new ArrayList<>();
        for (int i = 0; i < picPath.split("/").length; i++) {
            pathParts.add(picPath.split("/")[i]);
        }
        String name = pathParts.get(picPath.split("/").length - 1);
        return name;
    }

    private static String localWindowsPath(String picPath) {
        String localPath = "\\\\Server03\\бд_сайта\\poligon_images\\catalog";
        //if (!System.getProperty("os.name").contains("Windows")) {
        //    localPath = localPath.replace("\\", "/");
        //}
        if (picPath.split("/").length==0) {
            return "";
        } else {
            for (int i = 0; i < picPath.split("/").length; i++) {
                localPath += "\\" + picPath.split("/")[i];
            }
        }
        return localPath;
    }

    private static String copyToPlace(File file, String selectedProduct, String picType, Integer plugNumber) throws IOException {
        String vendorTitle = "";
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("from Products where title = :title");
        query.setParameter("title", selectedProduct);
        List result = query.list();
        for(Iterator iterator = result.iterator(); iterator.hasNext();) {
            Products product = (Products) iterator.next();
            if (product.getVendorId().getTitle().equals("COMAT/RELECO")) {
                vendorTitle = "RELECO";
            } else {
                vendorTitle = product.getVendorId().getTitle();
            }
        }
        session.close();
        String resPath = "";
        String fileName = "";
        String fileType = "";
        if( plugNumber != null) {
            fileName = selectedProduct.replace(" ", "_").replace("/", "_").replace(",", "_") + "_" + picType + plugNumber + ".jpg";
        } else {
            fileName = selectedProduct.replace(" ", "_").replace("/", "_").replace(",", "_") + "_" + picType + ".jpg";
        }
        fileType = picType;
        resPath = "\\\\Server03\\бд_сайта\\poligon_images\\catalog\\" + vendorTitle + "\\" + fileType + "s\\" + fileName ;
        //if (!System.getProperty("os.name").contains("Windows")) {
        //    resPath = resPath.replace("\\", "/");
        //}
        copyFile(file, new File(resPath));
        String sshUser = "";
        String sshPass = "";
        String sshHost = "";

        Session session1 = HibernateUtil.getSessionFactory().openSession();
        Query query1 = session1.createQuery("from entities.Settings where kind = :kind and title = :title");
        query1.setParameter("kind", "SFTPSettings");
        query1.setParameter("title", "userSFTP");
        List result1 = query1.list();
        for(Iterator iterator = result1.iterator(); iterator.hasNext();) {
            entities.Settings setting = (entities.Settings) iterator.next();
            sshUser = setting.getTextValue();
        }
        session1.close();
        Session session2 = HibernateUtil.getSessionFactory().openSession();
        Query query2 = session2.createQuery("from entities.Settings where kind = :kind and title = :title");
        query2.setParameter("kind", "SFTPSettings");
        query2.setParameter("title", "passwordSFTP");
        List result2 = query2.list();
        for(Iterator iterator = result2.iterator(); iterator.hasNext();) {
            entities.Settings setting = (entities.Settings) iterator.next();
            sshPass = setting.getTextValue();
        }
        session2.close();
        Session session3 = HibernateUtil.getSessionFactory().openSession();
        Query query3 = session3.createQuery("from entities.Settings where kind = :kind and title = :title");
        query3.setParameter("kind", "SFTPSettings");
        query3.setParameter("title", "serverSFTP");
        List result3 = query3.list();
        for(Iterator iterator = result3.iterator(); iterator.hasNext();) {
            entities.Settings setting = (entities.Settings) iterator.next();
            sshHost = setting.getTextValue();
        }
        session3.close();
        String remotePlace = "/var/www/poligon/data/www/poligon.info/images/catalog/" + vendorTitle + "/" + fileType + "s/" ;
        sftp(("file://" + resPath.replace("\\", "/")), ("ssh://" + sshUser + ":" + sshPass + "@" + sshHost + remotePlace));

        return resPath;
    }

    private static void copyFile(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (NullPointerException ne) {

        } finally {
            try {
                is.close();
                os.close();
            } catch (NullPointerException ne) {
                AlertWindow.showErrorMessage("Ошибка копирования!");
            }
        }
    }
}
