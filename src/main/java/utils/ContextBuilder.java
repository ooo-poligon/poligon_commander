package utils;

import entities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.event.ActionEvent;
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
    private static Integer selectedPropertyTypeID = 0;

    public static void createNewCategory() {

    }
    public static void updateTheCategory() {

    }
    public static void deleteTheCategory() {

    }

    public static void createNewProductKind() {
        Dialog<NewProductKind> dialog = new Dialog<>();
        dialog.setTitle("Добавление нового типа устройств.");
        dialog.setHeaderText("Введите название нового типа устройств.");
        dialog.setResizable(false);

        Label label1 = new Label("Название:  ");
        TextField text1 = new TextField();
        GridPane grid = new GridPane();

        grid.add(label1, 1, 1);
        grid.add(text1, 2, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewProductKind(text1.getText());
            }
            return null;
        });
        Optional<NewProductKind> result = dialog.showAndWait();
        if (result.isPresent()) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            ProductKinds productKind = new ProductKinds();
            productKind.setTitle(result.get().getTitle());
            session.saveOrUpdate(productKind);
            tx.commit();
            session.close();
        }
    }
    public static void updateTheProductKind(ListView<String> productKindsList) {
        String selectedProductKind = productKindsList.getSelectionModel().getSelectedItem();
        ProductKinds productKind = new ProductKinds();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from ProductKinds where title =\'" + selectedProductKind + "\'").list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            productKind = (ProductKinds) iterator.next();
        }
        session.close();

        Dialog<NewProductKind> dialog = new Dialog<>();
        dialog.setTitle("Редактирование выбранного типа устройств.");
        dialog.setHeaderText("Здесь выможете изменить параметры\nвыбранного типа устройств.");
        dialog.setResizable(false);

        Label label1 = new Label("Название:  ");
        TextField text1 = new TextField();
        text1.setText(productKind.getTitle());

        GridPane grid = new GridPane();

        grid.add(label1, 1, 1);
        grid.add(text1, 2, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Изменить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewProductKind(text1.getText());
            }
            return null;
        });
        Optional<NewProductKind> result = dialog.showAndWait();
        if (result.isPresent()) {
            Session session1 = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session1.beginTransaction();
            productKind.setTitle(result.get().getTitle());
            session1.saveOrUpdate(productKind);
            tx.commit();
            session1.close();
        }
    }
    public static void deleteTheProductKind(ListView<String> productKindsList) {
        String selectedProductKind = productKindsList.getSelectionModel().getSelectedItem();
        ProductKinds productKind = new ProductKinds();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from ProductKinds where title =\'" + selectedProductKind + "\'").list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            productKind = (ProductKinds) iterator.next();
        }
        session.close();

        Dialog<NewProductKind> dialog = new Dialog<>();
        dialog.setTitle("Удаление выбранного типа устройств.");
        dialog.setHeaderText("Внимание! Выбранный тип устройств будет удалён из базы данных.");
        dialog.setResizable(false);

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
                return new NewProductKind();
            }
            return null;
        });
        Optional<NewProductKind> result = dialog.showAndWait();
        if (result.isPresent()) {
            Session session1 = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session1.beginTransaction();

            Query q = session1.createQuery("delete ProductKinds where id =" + productKind.getId());
            q.executeUpdate();

            tx.commit();
            session1.close();
        }
    }

    public static void createNewProperty(ListView<String> productKindsList) {
        ComboBox<String> propertyTypesComboBox = new ComboBox<>();
        String selectedProductKind = productKindsList.getSelectionModel().getSelectedItem();
        ArrayList<PropertyTypes> propertyTypesList = new ArrayList<>();
        ObservableList<String> propertyTypesTitlesList = FXCollections.observableArrayList();


        Session session2 = HibernateUtil.getSessionFactory().openSession();
        List res2 = session2.createQuery("from PropertyTypes").list();
        for (Iterator iterator = res2.iterator(); iterator.hasNext();) {
            PropertyTypes propertyType = (PropertyTypes) iterator.next();
            propertyTypesList.add(propertyType);
        }
        session2.close();

        propertyTypesList.stream().forEach((type) -> {
            propertyTypesTitlesList.add(type.getTitle());
        });

        propertyTypesComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                for (PropertyTypes pt: propertyTypesList) {
                    if (pt.getTitle().equals(propertyTypesComboBox.getValue())) {
                        selectedPropertyTypeID = pt.getId();
                    }
                }
            }
        });

        Dialog<NewProperty> dialog = new Dialog<>();
        dialog.setTitle("Добавление нового свойства.");
        dialog.setHeaderText("Укажите параметры нового свойства.");
        dialog.setResizable(false);

        Label label1 = new Label("Выберите тип свойства: ");
        Label label2 = new Label("Назовите новое свойство: ");
        Label label3 = new Label("");

        propertyTypesComboBox.setItems(propertyTypesTitlesList);
        TextField text2 = new TextField();

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(propertyTypesComboBox, 2, 1);
        grid.add(label3, 1, 2);
        grid.add(label2, 1, 3);
        grid.add(text2, 2, 3);
        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewProperty(text2.getText(), selectedPropertyTypeID);
            }
            return null;
        });
        Optional<NewProperty> result = dialog.showAndWait();
        if (result.isPresent()) {

            Session session3 = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session3.beginTransaction();
            Properties property = new Properties();
            property.setTitle("test");
            property.setValueId(new PropertyValues());
            property.setProductId(new Products());
            property.setPropertyTypeId(new PropertyTypes(selectedPropertyTypeID, propertyTypesComboBox.getValue()));
            session3.save(property);
            tx.commit();
            session3.close();
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

        ButtonType buttonTypeOk = new ButtonType("Изменить", ButtonBar.ButtonData.OK_DONE);
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
