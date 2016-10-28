/*
 * 
 * 
 */
package utils;

import entities.*;
import imgscalr.Scalr;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import main.PCGUIController;
import main.PoligonCommander;
import modalwindows.AlertWindow;
import modalwindows.ImageWindow;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import static utils.SshUtils.sftp;

/**
 * @author Igor Klekotnev
 */
public class ImageFile {

    public static String makeTemporaryResizedImage(File file) {
        String picPath = file.getAbsolutePath();
        String picName = picPath.replace('\\', '@').split("@")[(picPath.replace('\\', '@')).split("@").length - 1];
        String newPicPath = PoligonCommander.tmpDir.getAbsolutePath() + "\\" + picName;
        BufferedImage in = null;
        try {
            in = ImageIO.read(file);
        } catch (IOException e) {
            AlertWindow.showErrorMessage("Невозможно открыть файл с изображением.\n" +
                    "Проверьте, что нужный файл существует\nпо указанному пути:" + file.getAbsolutePath());
        }
        BufferedImage out = Scalr.resize(in, 768, 768);
        int pad = Math.abs(out.getHeight() - out.getWidth());
        if (out.getHeight() > out.getWidth()) {
            out = Scalr.pad(out, pad, new Color(255, 255, 255));
            out = Scalr.crop(out, pad / 2, pad, 768, 768);
        } else if (out.getHeight() < out.getWidth()) {
            out = Scalr.pad(out, pad, new Color(255, 255, 255));
            out = Scalr.crop(out, pad, pad / 2, 768, 768);
        }
        out = Scalr.pad(out, 30, new Color(255, 255, 255));
        out = Scalr.resize(out, 768, 768);

        File outputFile = new File(newPicPath);
        try {
            if (picName.endsWith(".jpg") || picName.endsWith(".JPG")) {
                ImageIO.write(out, "jpg", outputFile);
            } else if (picName.endsWith(".gif") || picName.endsWith(".GIF")) {
                ImageIO.write(out, "gif", outputFile);
            } else if (picName.endsWith(".png") || picName.endsWith(".PNG")) {
                ImageIO.write(out, "png", outputFile);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return outputFile.toURI().toURL().toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void open(File file, GridPane gridPane, ImageView imageView, String imageType) {
        if (gridPane.getChildren().size() != 0) {
            gridPane.getChildren().clear();
        }
        String localUrl = "";
        switch (imageType) {
            case "deviceImage": {
                try {
                    localUrl = file.toURI().toURL().toString();
                } catch (MalformedURLException e) {
                    AlertWindow.showErrorMessage(e.getMessage());
                }
                break;
            }
            case "categoryImage": {
                try {
                    localUrl = file.toURI().toURL().toString();
                } catch (MalformedURLException e) {
                    AlertWindow.showErrorMessage(e.getMessage());
                }
                break;
            }
            case "dimsImage": {
                try {
                    localUrl = file.toURI().toURL().toString();
                } catch (MalformedURLException e) {
                    AlertWindow.showErrorMessage(e.getMessage());
                }
                break;
            }
            case "plugsImage": {
                try {
                    localUrl = file.toURI().toURL().toString();
                } catch (MalformedURLException e) {
                    AlertWindow.showErrorMessage(e.getMessage());
                }
                break;
            }
            case "functionImage": {
                try {
                    localUrl = file.toURI().toURL().toString();
                } catch (MalformedURLException e) {
                    AlertWindow.showErrorMessage(e.getMessage());
                }
                break;
            }
            case "contentImage": {
                try {
                    localUrl = file.toURI().toURL().toString();
                } catch (MalformedURLException e) {
                    AlertWindow.showErrorMessage(e.getMessage());
                }
                break;
            }
            case "deviceImageSetter": {
                localUrl = makeTemporaryResizedImage(file);
                break;
            }
            case "categoryImageSetter": {
                localUrl = makeTemporaryResizedImage(file);
                break;
            }
            case "dimsImageSetter": {
                localUrl = makeTemporaryResizedImage(file);
                break;
            }
            case "plugsImageSetter": {
                localUrl = makeTemporaryResizedImage(file);
                break;
            }
            case "functionImageSetter": {
                localUrl = makeTemporaryResizedImage(file);
                break;
            }
        }

        imageView.setImage(new Image(localUrl));
        imageView.setFitHeight(194);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    ImageWindow imageWindow = new ImageWindow(imageView);
                    imageWindow.show();
                }
            }
        });
        gridPane.getChildren().add(imageView);
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
        Query query = session.createSQLQuery("SELECT id FROM products WHERE title= :title");
        query.setParameter("title", selectedProduct);
        List ids = query.list();
        for (Object id : ids) {
            ownerId = (Integer) id;
        }
        int fileTypeId = 1;
        List pictureList = session.createQuery("From Files where fileTypeId=" + fileTypeId + " and ownerId=" + ownerId).list();
        if (pictureList.isEmpty()) {
            Files pictureFile = null;
            if (file != null)
                pictureFile = new Files(file.getName(), file.getPath(), "Это изображение для " + selectedProduct, (new FileTypes(1)), (new Products(ownerId)));
            session.saveOrUpdate(pictureFile);
            try {
                String queryRemote = "insert into files set (description,name,path,file_type_id,owner_id) values" +
                        " (\"Это изображение для " + selectedProduct + "\",\"" + file.getName() +
                        "\",\"" + file.getPath().replace("\"", "\\\"").replace("\\", "\\\\") +
                        "\",1," + ownerId + ")";
                PCGUIController.siteConnection.getUpdateResult(queryRemote);
            } catch (SQLException e) {
                AlertWindow.showErrorMessage(e.getMessage());
            }
        } else {
            for (Object aPictureList : pictureList) {
                Files pic = (Files) aPictureList;
                if (file != null && (pic.getFileTypeId().getId() == 1) && ((!pic.getName().equals(file.getName())) || (!pic.getPath().equals(file.getPath())))) {
                    pic.setName(file.getName());
                    pic.setPath(file.getPath());
                    pic.setDescription("Это изображение для " + selectedProduct);
                    session.saveOrUpdate(pic);
                }
                try {
                    String queryRemote = "update files set name=\"" + file.getName() +
                            "\", path=\"" + file.getPath().replace("\"", "\\\"").replace("\\", "\\\\")  +
                            "\", description=\"Это изображение для " + selectedProduct +
                            "\" where owner_id =" + ownerId + " and file_type_id=" + 1;
                    PCGUIController.siteConnection.getUpdateResult(queryRemote);
                } catch (SQLException e) {
                    AlertWindow.showErrorMessage(e.getMessage());
                }
            }
        }
        tx.commit();
        session.close();
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
        List ids = session.createSQLQuery(String.format("select id from products where title=\"%s\"", selectedProduct)).list();
        for (Object id : ids) {
            ownerId = (Integer) id;
        }
        List pictureList = session.createQuery("From Files where ownerId=" + ownerId + "and file_type_id=" + 4).list();
        if (pictureList.isEmpty()) {
            Files pictureFile = null;
            if (file != null) {
                pictureFile = new Files(file.getName(), file.getPath(), "Это схема габаритов для " + selectedProduct, (new FileTypes(4)), (new Products(ownerId)));
            }
            session.saveOrUpdate(pictureFile);
            try {
                String queryRemote = "insert into files set (description,name,path,file_type_id,owner_id) values" +
                        " (\"Это схема габаритов для " + selectedProduct + "\",\"" + file.getName() +
                        "\",\"" + file.getPath().replace("\"", "\\\"").replace("\\", "\\\\") +
                        "\",4," + ownerId + ")";
                PCGUIController.siteConnection.getUpdateResult(queryRemote);
            } catch (SQLException e) {
                AlertWindow.showErrorMessage(e.getMessage());
            }
        } else {
            for (Object aPictureList : pictureList) {
                Files pic = (Files) aPictureList;
                if (file != null && (pic.getFileTypeId().getId() == 4) &&
                        ((!pic.getName().equals(file.getName())) ||
                                (!pic.getPath().equals(file.getPath())))) {
                    pic.setName(file.getName());
                    pic.setPath(file.getPath());
                    pic.setDescription("Это схема габаритов для " + selectedProduct);
                    session.saveOrUpdate(pic);
                }
                try {
                    String queryRemote = "update files set name=\"" + file.getName() +
                            "\", path=\"" + file.getPath().replace("\"", "\\\"").replace("\\", "\\\\") +
                            "\", description=\"Это схема габаритов для " + selectedProduct +
                            "\" where owner_id =" + ownerId + " and file_type_id =" + 4;
                    PCGUIController.siteConnection.getUpdateResult(queryRemote);
                } catch (SQLException e) {
                    AlertWindow.showErrorMessage(e.getMessage());
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
        List ids = session.createSQLQuery(String.format("select id from products where title=\"%s\"", selectedProduct)).list();
        for (Object id : ids) {
            ownerId = (Integer) id;
        }
        int fileTypeId = 3;
        if (plugNumber == 2) fileTypeId = 7;
        List pictureList = session.createQuery("From Files where ownerId=" + ownerId + "and file_type_id=" + fileTypeId).list();
        if (pictureList.isEmpty()) {
            Files pictureFile = null;
            if (file != null) {
                pictureFile = new Files(file.getName(), file.getPath(), "Это схема подключения №" + plugNumber + " для " + selectedProduct, (new FileTypes(fileTypeId)), (new Products(ownerId)));
            }
            session.saveOrUpdate(pictureFile);
            try {
                String queryRemote = "insert into files set (description,name,path,file_type_id,owner_id) values" +
                        " (\"Это схема подключения №" + plugNumber + " для " + selectedProduct + "\",\"" + file.getName() +
                        "\",\"" + file.getPath().replace("\"", "\\\"").replace("\\", "\\\\") +
                        "\"," + fileTypeId + "," + ownerId + ")";
                PCGUIController.siteConnection.getUpdateResult(queryRemote);
            } catch (SQLException e) {
                AlertWindow.showErrorMessage(e.getMessage());
            }
        } else {
            for (Object aPictureList : pictureList) {
                Files pic = (Files) aPictureList;
                if (file != null && (pic.getFileTypeId().getId() == fileTypeId) &&
                        ((!pic.getName().equals(file.getName())) ||
                                (!pic.getPath().equals(file.getPath())))) {
                    pic.setName(file.getName());
                    pic.setPath(file.getPath());
                    pic.setDescription("Это схема подключения №" + plugNumber + " для " + selectedProduct);
                    session.saveOrUpdate(pic);
                }
                try {
                    String queryRemote = "update files set name=\"" + file.getName() +
                            "\", path=\"" + file.getPath().replace("\"", "\\\"").replace("\\", "\\\\") +
                            "\", description=\"Это схема подключения №" + plugNumber + " для " + selectedProduct +
                            "\" where owner_id =" + ownerId + " and file_type_id=" + fileTypeId;
                    PCGUIController.siteConnection.getUpdateResult(queryRemote);
                } catch (SQLException e) {
                    AlertWindow.showErrorMessage(e.getMessage());
                }
            }
        }
        tx.commit();
        session.close();
    }

    public static void saveContentImage(File inFile, String contentType, String contentTitle) {
        File file = null;
        try {
            file = new File(copyToPlace(inFile, (contentType + "@" + inFile.getName()), "content", null));
        } catch (IOException e) {
            AlertWindow.showErrorMessage(e.getMessage());
        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        switch (contentType) {
            case "news":
                NewsItems item = new NewsItems();
                Query query = session.createQuery("from NewsItems where title = :title");
                query.setParameter("title", contentTitle);
                List list = query.list();
                for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                    item = (NewsItems) iterator.next();
                }
                if (file != null) {
                    item.setImagePath(file.getAbsolutePath());
                    session.saveOrUpdate(item);
                    try {
                        String queryRemote = "update news_items set image_path=\"" + "\\\\\\\\Server03\\\\бд_сайта\\\\poligon_images\\\\content\\\\" + contentType + "\\\\" + file.getName() +
                                "\" where id =" + item.getId();
                        PCGUIController.siteConnection.getUpdateResult(queryRemote);
                    } catch (SQLException e) {
                        AlertWindow.showErrorMessage(e.getMessage());
                    }
                }
                break;
            case "articles":
                Articles item1 = new Articles();
                Query query1 = session.createQuery("from Articles where title = :title");
                query1.setParameter("title", contentTitle);
                List list1 = query1.list();
                for (Iterator iterator = list1.iterator(); iterator.hasNext();) {
                    item1 = (Articles) iterator.next();
                }
                if (file != null) {
                    item1.setImagePath(file.getAbsolutePath());
                    session.saveOrUpdate(item1);
                }
                try {
                    String queryRemote = "update articles set image_path=\"" + "\\\\\\\\Server03\\\\бд_сайта\\\\poligon_images\\\\content\\\\" + contentType + "\\\\" + file.getName() +
                            "\" where id =" + item1.getId();
                    PCGUIController.siteConnection.getUpdateResult(queryRemote);
                } catch (SQLException e) {
                    AlertWindow.showErrorMessage(e.getMessage());
                }
                break;
            case "videos":
                Videos item2 = new Videos();
                Query query2 = session.createQuery("from Videos where title = :title");
                query2.setParameter("title", contentTitle);
                List list2 = query2.list();
                for (Iterator iterator = list2.iterator(); iterator.hasNext();) {
                    item2 = (Videos) iterator.next();
                }
                if (file != null) {
                    item2.setImagePath(file.getAbsolutePath());
                    session.saveOrUpdate(item2);
                }
                try {
                    String queryRemote = "update videos set image_path=\"" + "\\\\\\\\Server03\\\\бд_сайта\\\\poligon_images\\\\content\\\\" + contentType + "\\\\" + file.getName() +
                            "\" where id =" + item2.getId();
                    PCGUIController.siteConnection.getUpdateResult(queryRemote);
                } catch (SQLException e) {
                    AlertWindow.showErrorMessage(e.getMessage());
                }
                break;
            case "reviews":
                Reviews item3 = new Reviews();
                Query query3 = session.createQuery("from Reviews where title = :title");
                query3.setParameter("title", contentTitle);
                List list3 = query3.list();
                for (Iterator iterator = list3.iterator(); iterator.hasNext();) {
                    item3 = (Reviews) iterator.next();
                }
                if (file != null) {
                    item3.setImagePath(file.getAbsolutePath());
                    session.saveOrUpdate(item3);
                }
                try {
                    String queryRemote = "update reviews set image_path=\"" + "\\\\\\\\Server03\\\\бд_сайта\\\\poligon_images\\\\content\\\\" + contentType + "\\\\" + file.getName() +
                            "\" where id =" + item3.getId();
                    PCGUIController.siteConnection.getUpdateResult(queryRemote);
                } catch (SQLException e) {
                    AlertWindow.showErrorMessage(e.getMessage());
                }
                break;
            case "additions":
                Additions item4 = new Additions();
                Query query4 = session.createQuery("from Additions where title = :title");
                query4.setParameter("title", contentTitle);
                List list4 = query4.list();
                for (Iterator iterator = list4.iterator(); iterator.hasNext();) {
                    item4 = (Additions) iterator.next();
                }
                if (file != null) {
                    item4.setImagePath(file.getAbsolutePath());
                    session.saveOrUpdate(item4);
                }
                try {
                    String queryRemote = "update additions set image_path=\"" + "\\\\\\\\Server03\\\\бд_сайта\\\\poligon_images\\\\content\\\\" + contentType + "\\\\" + file.getName() +
                            "\" where id =" + item4.getId();
                    PCGUIController.siteConnection.getUpdateResult(queryRemote);
                } catch (SQLException e) {
                    AlertWindow.showErrorMessage(e.getMessage());
                }
                break;
        }
        tx.commit();
        session.close();
    }

    private static String copyToPlace(File file, String selectedProduct, String picType, Integer plugNumber) throws IOException {
        String resPath;
        String fileName;
        String fileType = picType;
        String vendorTitle = "";
        String remotePlace = "";
        if (picType != "content") {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("from Products where title = :title");
            query.setParameter("title", selectedProduct);
            List result = query.list();
            for (Object aResult : result) {
                Products product = (Products) aResult;
                if (product.getVendorId().getTitle().equals("COMAT/RELECO")) {
                    vendorTitle = "RELECO";
                } else {
                    vendorTitle = product.getVendorId().getTitle();
                }
            }
            session.close();

            if (plugNumber != null) {
                fileName = selectedProduct.replace(" ", "_").replace("/", "_").replace(",", "_") + "_" + picType + plugNumber + ".jpg";
            } else {
                fileName = selectedProduct.replace(" ", "_").replace("/", "_").replace(",", "_") + "_" + picType + ".jpg";
            }

            resPath = "\\\\Server03\\бд_сайта\\poligon_images\\catalog\\" + vendorTitle + "\\" + fileType + "s\\" + fileName;
            remotePlace = "/var/www/poligon/data/www/poligon.info/images/catalog/" + vendorTitle + "/" + fileType + "s/";
        } else {
            String contentType = selectedProduct.split("@")[0];
            String picture = selectedProduct.split("@")[1];
            resPath = "\\\\Server03\\бд_сайта\\poligon_images\\" + picType + "\\" + contentType + "\\" + picture;
            remotePlace = "/var/www/poligon/data/www/poligon.info/images/"+ picType + "/" + contentType + "/";
        }

        copyFile(file, new File(resPath));
        String sshUser = "";
        String sshPass = "";
        String sshHost = "";

        Session session1 = HibernateUtil.getSessionFactory().openSession();
        Query query1 = session1.createQuery("from entities.Settings where kind = :kind and title = :title");
        query1.setParameter("kind", "SFTPSettings");
        query1.setParameter("title", "userSFTP");
        List result1 = query1.list();
        for (Object aResult1 : result1) {
            entities.Settings setting = (entities.Settings) aResult1;
            sshUser = setting.getTextValue();
        }
        session1.close();
        Session session2 = HibernateUtil.getSessionFactory().openSession();
        Query query2 = session2.createQuery("from entities.Settings where kind = :kind and title = :title");
        query2.setParameter("kind", "SFTPSettings");
        query2.setParameter("title", "passwordSFTP");
        List result2 = query2.list();
        for (Object aResult2 : result2) {
            entities.Settings setting = (entities.Settings) aResult2;
            sshPass = setting.getTextValue();
        }
        session2.close();
        Session session3 = HibernateUtil.getSessionFactory().openSession();
        Query query3 = session3.createQuery("from entities.Settings where kind = :kind and title = :title");
        query3.setParameter("kind", "SFTPSettings");
        query3.setParameter("title", "serverSFTP");
        List result3 = query3.list();
        for (Object aResult3 : result3) {
            entities.Settings setting = (entities.Settings) aResult3;
            sshHost = setting.getTextValue();
        }
        session3.close();

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
        } catch (NullPointerException ignored) {

        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (NullPointerException ne) {
                AlertWindow.showErrorMessage("Ошибка копирования!");
            }
        }
    }
}
