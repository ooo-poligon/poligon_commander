package utils;

import entities.Functions;
import entities.ProductKinds;
import entities.Products;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import modalwindows.SetRatesWindow;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import tableviews.FunctionsTableView;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Created by Igor Klekotnev on 13.01.2016.
 */
public class ContextBuilder {
    private static String picPath = "";
    private static String picName = "";

    public static void createNewCategory() {

    }
    public static void updateTheCategory() {

    }
    public static void deleteTheCategory() {

    }

    public static void createNewProductKind() {

    }
    public static void updateTheProductKind() {

    }
    public static void deleteTheProductKind() {

    }

    public static void createNewProperty() {
        Dialog<NewProperty> dialog = new Dialog<>();
        dialog.setTitle("Добавление нового свойства.");
        dialog.setHeaderText("Введите параметры нового свойства.");
        dialog.setResizable(false);

        Label label1 = new Label("Введите название:  ");
        Label label2 = new Label("Описание (необязательно):  ");
        TextField text1 = new TextField();
        TextArea text2 = new TextArea();

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(text1, 2, 1);
        grid.add(label2, 1, 2);
        grid.add(text2, 2, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewProperty();
            }
            return null;
        });
        Optional<NewProperty> result = dialog.showAndWait();
        if (result.isPresent()) {
            //newVendorTitle = result.get().getTitle();
            //newVendorDescription = result.get().getDescription();
            //newVendorCurrency = result.get().getCurrency();
            //newVendorAddress = result.get().getAddress();
            //newVendorRate = result.get().getRate();
            //createNewVendor();
            //buildVendorsTable();
        }
    }
    public static void updateTheProperty() {

    }
    public static void deleteTheProperty() {

    }

    public static void createNewFunction(Integer selectedPropertiesKindID) {

        ProductKinds productKind = new ProductKinds();

        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from ProductKinds where id =" + selectedPropertiesKindID).list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            productKind = (ProductKinds) iterator.next();
        }
        session.close();

        Dialog<NewFunction> dialog = new Dialog<>();
        dialog.setTitle("Добавление новой функции.");
        dialog.setHeaderText("Введите параметры новой функции.");
        dialog.setResizable(true);

        Label label1 = new Label("Название:  ");
        Label label2 = new Label("Символ:  ");
        Label label3 = new Label("Описание:  ");
        Label label4 = new Label("");
        Label label5 = new Label("");

        Button setImageButton = new Button("Выбрать изображение");

        TextField text1 = new TextField();
        TextField text2 = new TextField();
        TextArea text3 = new TextArea();
        text3.setWrapText(true);
        ImageView image1 = new ImageView();
        image1.setFitWidth(300.0);
        image1.setPreserveRatio(true);

        GridPane grid = new GridPane();
        GridPane grid1 = new GridPane();

        grid.add(label1, 1, 1);
        grid.add(text1, 2, 1);

        grid.add(label2, 1, 2);
        grid.add(text2, 2, 2);

        grid.add(label3, 1, 3);
        grid.add(text3, 2, 3);

        grid.add(label5, 1, 4);

        grid.add(grid1, 2, 5);
        grid1.add(image1, 0, 0);
        grid.add(label4, 1, 6);

        grid.add(setImageButton, 2, 7);

        dialog.getDialogPane().setContent(grid);

        setImageButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {

            @Override
            public void handle(javafx.event.ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showOpenDialog(grid.getScene().getWindow());
                if (file != null) {
                    ProductImage.open(new File(file.getAbsolutePath()), grid1, image1);
                }
                picPath = file.getAbsolutePath();
                picName = picPath.replace('\\', '@').split("@")[(picPath.replace('\\', '@')).split("@").length - 1];
            }
        });

        ButtonType buttonTypeOk = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewFunction(text1.getText(), text2.getText(), text3.getText(), picName, picPath);
            }
            return null;
        });
        Optional<NewFunction> result = dialog.showAndWait();
        if (result.isPresent()) {
            Session session1 = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session1.beginTransaction();

            Functions function = new Functions();
            function.setTitle(result.get().getTitle());
            function.setDescription(result.get().getDescription());
            function.setSymbol(result.get().getSymbol());
            function.setPictureName(result.get().getPictureName());
            function.setPicturePath(result.get().getPicturePath());
            function.setProductKindId(productKind);

            session1.saveOrUpdate(function);

            tx.commit();
            session1.close();
        }
    }
    public static void updateTheFunction(Integer selectedPropertiesKindID, TableView<FunctionsTableView> functionsTable) {
        ProductKinds productKind = new ProductKinds();

        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from ProductKinds where id =" + selectedPropertiesKindID).list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            productKind = (ProductKinds) iterator.next();
        }
        session.close();
        Functions function = new Functions();
        String selectedFunction = ((FunctionsTableView)functionsTable.getSelectionModel().getSelectedItem()).getTitle();

        Session session2 = HibernateUtil.getSessionFactory().openSession();
        List res2 = session2.createQuery("from Functions where title =\'" + selectedFunction + "\'").list();
        for (Iterator iterator = res2.iterator(); iterator.hasNext();) {
            function = (Functions) iterator.next();
        }
        session2.close();

        Dialog<NewFunction> dialog = new Dialog<>();
        dialog.setTitle("Редактирование параметров выбранной функции.");
        dialog.setHeaderText("Здесь выможете изменить параметры выбранной функции.");
        dialog.setResizable(true);

        Label label1 = new Label("Название:  ");
        Label label2 = new Label("Символ:  ");
        Label label3 = new Label("Описание:  ");
        Label label4 = new Label("");
        Label label5 = new Label("");

        Button setImageButton = new Button("Выбрать изображение");

        TextField text1 = new TextField();
        text1.setText(function.getTitle());
        TextField text2 = new TextField();
        text2.setText(function.getSymbol());
        TextArea text3 = new TextArea();
        text3.setText(function.getDescription());
        text3.setWrapText(true);
        ImageView image1 = new ImageView();
        image1.setFitWidth(300.0);
        image1.setPreserveRatio(true);

        GridPane grid = new GridPane();
        GridPane grid1 = new GridPane();

        grid.add(label1, 1, 1);
        grid.add(text1, 2, 1);

        grid.add(label2, 1, 2);
        grid.add(text2, 2, 2);

        grid.add(label3, 1, 3);
        grid.add(text3, 2, 3);

        grid.add(label5, 1, 4);

        grid.add(grid1, 2, 5);
        grid1.add(image1, 0, 0);
        grid.add(label4, 1, 6);

        grid.add(setImageButton, 2, 7);

        dialog.getDialogPane().setContent(grid);
        ProductImage.open(new File(function.getPicturePath()), grid1, image1);

        setImageButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {

            @Override
            public void handle(javafx.event.ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showOpenDialog(grid.getScene().getWindow());
                if (file != null) {
                    ProductImage.open(new File(file.getAbsolutePath()), grid1, image1);
                }
                picPath = file.getAbsolutePath();
                picName = picPath.replace('\\', '@').split("@")[(picPath.replace('\\', '@')).split("@").length - 1];
            }
        });

        ButtonType buttonTypeOk = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewFunction(text1.getText(), text2.getText(), text3.getText(), picName, picPath);
            }
            return null;
        });
        Optional<NewFunction> result = dialog.showAndWait();
        if (result.isPresent()) {
            Session session1 = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session1.beginTransaction();

            //Functions function1 = new Functions();
            function.setTitle(result.get().getTitle());
            function.setDescription(result.get().getDescription());
            function.setSymbol(result.get().getSymbol());
            function.setPictureName(result.get().getPictureName());
            function.setPicturePath(result.get().getPicturePath());
            function.setProductKindId(productKind);

            session1.saveOrUpdate(function);

            tx.commit();
            session1.close();
        }
    }
    public static void deleteTheFunction(TableView<FunctionsTableView> functionsTable) {
        Functions function = new Functions();
        String selectedFunction = ((FunctionsTableView)functionsTable.getSelectionModel().getSelectedItem()).getTitle();

        Session session2 = HibernateUtil.getSessionFactory().openSession();
        List res2 = session2.createQuery("from Functions where title =\'" + selectedFunction + "\'").list();
        for (Iterator iterator = res2.iterator(); iterator.hasNext();) {
            function = (Functions) iterator.next();
        }
        session2.close();

        Dialog<NewFunction> dialog = new Dialog<>();
        dialog.setTitle("Удаление выбранной функции.");
        dialog.setHeaderText("Внимание! Выбранная функция будет удалена из базы данных.");
        dialog.setResizable(true);

        Label label1 = new Label("Вы действительно хотите выполнить удаление?");

        GridPane grid = new GridPane();

        grid.add(label1, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Удалить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewFunction();
            }
            return null;
        });
        Optional<NewFunction> result = dialog.showAndWait();
        if (result.isPresent()) {
            Session session1 = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session1.beginTransaction();

            Query q = session1.createQuery("delete Functions where id =" + function.getId());
            q.executeUpdate();

            tx.commit();
            session1.close();
        }
    }

}
