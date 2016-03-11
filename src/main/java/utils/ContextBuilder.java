package utils;

import entities.*;
import entities.Properties;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import modalwindows.SetRatesWindow;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import tableviews.FunctionsTableView;
import javafx.event.ActionEvent;
import tableviews.ProductsTableView;
import tableviews.VendorsTableView;
import treetableviews.PropertiesTreeTableView;

import javax.swing.text.*;
import java.io.File;
import java.util.*;

/**
 * Created by Igor Klekotnev on 13.01.2016.
 */
public class ContextBuilder {
    private static String picPath = "";
    private static String picName = "";
    private static Integer selectedPropertyTypeID = 0;
    private static Integer selectedProductKindID  = 0;
    private static Integer selectedProductID  = 0;
    private static Integer selectedPropertyID = 0;
    private static Integer selectedMeasureID  = 1000000; // В БД в тавблице Measures определил такой id для свойств без ед. изм.
    private static Integer selectedPropertyValueID = 0;
    private static Integer selectedFunctionID = 0;
    private static Integer selectedProductsFunctionsID = 0;
    private static String selectedFunctionTitle = "";
    private static Vendors selectedVendor = new Vendors();

    public static void createNewPropertyValue(Label productTabTitle, TreeView<String> propertiesTree) {
        String selectedProduct = productTabTitle.getText();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from Products where title=\'" + selectedProduct + "\'").list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            Products product = (Products) iterator.next();
            selectedProductID = product.getId();
        }
        session.close();

        String selectedPropertyType = propertiesTree.getSelectionModel().getSelectedItem().getValue();
        Session session1 = HibernateUtil.getSessionFactory().openSession();
        List res1 = session1.createQuery("from PropertyTypes where title=\'" + selectedPropertyType + "\'").list();
        for (Iterator iterator = res1.iterator(); iterator.hasNext();) {
            PropertyTypes pt = (PropertyTypes) iterator.next();
            selectedPropertyTypeID = pt.getId();
        }
        session1.close();

        ArrayList<Properties> propertyList = new ArrayList<>();
        Session session2 = HibernateUtil.getSessionFactory().openSession();
        List res2 = session2.createQuery("from Properties where propertyTypeId=" + selectedPropertyTypeID).list();
        for (Iterator iterator = res2.iterator(); iterator.hasNext();) {
            Properties prop = (Properties) iterator.next();
            propertyList.add(prop);
        }
        session2.close();

        ObservableList<String> propertyTitles = FXCollections.observableArrayList();
        propertyList.stream().forEach((p) -> {
            propertyTitles.add(p.getTitle());
        });
        ComboBox<String> propertiesComboBox = new ComboBox<>();
        propertiesComboBox.setItems(propertyTitles);
        propertiesComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (Properties prop: propertyList) {
                    if (prop.getTitle().equals(propertiesComboBox.getValue())) {
                        selectedPropertyID = prop.getId();
                    }
                }
            }
        });

        ArrayList<Measures> measuresList = new ArrayList<>();
        Session session3 = HibernateUtil.getSessionFactory().openSession();
        List res3 = session3.createQuery("from Measures").list();
        for (Iterator iterator = res3.iterator(); iterator.hasNext();) {
            Measures measure = (Measures) iterator.next();
            measuresList.add(measure);
        }
        session3.close();

        ObservableList<String> measureTitles = FXCollections.observableArrayList();
        measuresList.stream().forEach((m) -> {
            measureTitles.add(m.getTitle());
        });
        ComboBox<String> measuresComboBox = new ComboBox<>();

        measuresComboBox.getItems().clear();
        measuresComboBox.getItems().addAll(measureTitles);
        AutoCompleteComboBoxListener autoCompleteComboBoxListener = new AutoCompleteComboBoxListener(measuresComboBox);
        // measuresComboBox.setItems(measureTitles);
        measuresComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (Measures measure: measuresList) {
                    if (measure.getTitle().equals(measuresComboBox.getValue())) {
                        selectedMeasureID = measure.getId();
                    }
                }
            }
        });

        Dialog<NewPropertyValue> dialog = new Dialog<>();
        dialog.setTitle("Добавление нового свойства.");
        dialog.setHeaderText("Укажите значение .");
        dialog.setResizable(false);

        Label label1 = new Label("Выберите свойство: ");
        Label label2 = new Label("Укажите значение: ");
        Label label3 = new Label("");
        Label label4 = new Label("");
        Label label5 = new Label("Укажите условие:\n(не обязательно) ");
        Label label6 = new Label("");
        TextField text2 = new TextField();
        TextField text3 = new TextField();

        Label label7 = new Label("Выберите единицу измерения:\n" +
                "(только для измеряемых свойств) ");
        Label label8 = new Label("");

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(propertiesComboBox, 2, 1);
        grid.add(label3, 1, 2);

        grid.add(label5, 1, 3);
        grid.add(text3, 2, 3);
        grid.add(label4, 1, 4);

        grid.add(label2, 1, 5);
        grid.add(text2, 2, 5);
        grid.add(label6, 1, 6);

        grid.add(label7, 1, 7);
        grid.add(measuresComboBox, 2, 7);
        grid.add(label8, 1, 8);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                if (text3.getText().isEmpty()) {
                    return new NewPropertyValue("", text2.getText(), selectedPropertyID, selectedProductID, selectedMeasureID);
                } else {
                    return new NewPropertyValue(text3.getText(), text2.getText(), selectedPropertyID, selectedProductID, selectedMeasureID);
                }
            }
            return null;
        });
        Optional<NewPropertyValue> result = dialog.showAndWait();
        if (result.isPresent()) {
            Session session4 = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session4.beginTransaction();
            PropertyValues pv = new PropertyValues();
            pv.setCond(result.get().getCond());
            pv.setValue(result.get().getValue());
            pv.setPropertyId(new Properties(selectedPropertyID));
            pv.setProductId(new Products(selectedProductID));
            pv.setMeasureId(new Measures(selectedMeasureID));
            session4.save(pv);
            tx.commit();
            session4.close();
        }
    }
    public static void updateThePropertyValue(Label productTabTitle, TreeView<String> propertiesTree, TreeTableView propertiesTable) {
        String selectedProduct = productTabTitle.getText();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from Products where title=\'" + selectedProduct + "\'").list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            Products product = (Products) iterator.next();
            selectedProductID = product.getId();
        }
        session.close();

        String selectedPropertyType = propertiesTree.getSelectionModel().getSelectedItem().getValue();
        Session session1 = HibernateUtil.getSessionFactory().openSession();
        List res1 = session1.createQuery("from PropertyTypes where title=\'" + selectedPropertyType + "\'").list();
        for (Iterator iterator = res1.iterator(); iterator.hasNext();) {
            PropertyTypes pt = (PropertyTypes) iterator.next();
            selectedPropertyTypeID = pt.getId();
        }
        session1.close();

        ArrayList<Properties> propertyList = new ArrayList<>();
        Session session2 = HibernateUtil.getSessionFactory().openSession();
        List res2 = session2.createQuery("from Properties where propertyTypeId=" + selectedPropertyTypeID).list();
        for (Iterator iterator = res2.iterator(); iterator.hasNext();) {
            Properties prop = (Properties) iterator.next();
            propertyList.add(prop);
        }
        session2.close();

        ObservableList<String> propertyTitles = FXCollections.observableArrayList();
        propertyList.stream().forEach((p) -> {
            propertyTitles.add(p.getTitle());
        });
        ComboBox<String> propertiesComboBox = new ComboBox<>();
        propertiesComboBox.setItems(propertyTitles);
        propertiesComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (Properties prop: propertyList) {
                    if (prop.getTitle().equals(propertiesComboBox.getValue())) {
                        selectedPropertyID = prop.getId();
                    }
                }
            }
        });

        ArrayList<Measures> measuresList = new ArrayList<>();
        Session session3 = HibernateUtil.getSessionFactory().openSession();
        List res3 = session3.createQuery("from Measures").list();
        for (Iterator iterator = res3.iterator(); iterator.hasNext();) {
            Measures measure = (Measures) iterator.next();
            measuresList.add(measure);
        }
        session3.close();

        ObservableList<String> measureTitles = FXCollections.observableArrayList();
        measuresList.stream().forEach((m) -> {
            measureTitles.add(m.getTitle());
        });
        ComboBox<String> measuresComboBox = new ComboBox<>();

        measuresComboBox.getItems().clear();
        measuresComboBox.getItems().addAll(measureTitles);
        AutoCompleteComboBoxListener autoCompleteComboBoxListener = new AutoCompleteComboBoxListener(measuresComboBox);
        measuresComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (Measures measure: measuresList) {
                    if (measure.getTitle().equals(measuresComboBox.getValue())) {
                        selectedMeasureID = measure.getId();
                    }
                }
            }
        });

        ObservableList<TreeTablePosition> treeTablePositions = propertiesTable.getSelectionModel().getSelectedCells();
        for (TreeTablePosition treeTablePosition: treeTablePositions) {
            PropertiesTreeTableView propertiesTreeTableView = (PropertiesTreeTableView)treeTablePosition.getTreeItem().getValue();
            selectedPropertyValueID = propertiesTreeTableView.getPropertyValueID();
        }

        PropertyValues propertyValue = new PropertyValues();
        Session session5 = HibernateUtil.getSessionFactory().openSession();
        List res5 = session5.createQuery("from PropertyValues where id=" + selectedPropertyValueID).list();
        for (Iterator iterator = res5.iterator(); iterator.hasNext();) {
            propertyValue = (PropertyValues) iterator.next();
        }
        session5.close();

        Dialog<NewPropertyValue> dialog = new Dialog<>();
        dialog.setTitle("Редактирование выбранного свойства.");
        dialog.setHeaderText("Здесь можно отредактировать значение выбранного свойства.");
        dialog.setResizable(false);

        Label label1 = new Label("Выберите новое свойство:\n" +
                "(не выбирайте, если хотите сохранить текущее. ");
        Label label2 = new Label("Укажите новое значение: ");
        Label label3 = new Label("");
        Label label4 = new Label("");
        Label label5 = new Label("Укажите новое условие:\n(не обязательно) ");
        Label label6 = new Label("");
        TextField text2 = new TextField();
        text2.setText(propertyValue.getValue());
        TextField text3 = new TextField();
        text3.setText(propertyValue.getCond());

        propertiesComboBox.setValue(propertyValue.getPropertyId().getTitle());
        selectedPropertyID = propertyValue.getPropertyId().getId();
        measuresComboBox.setValue(propertyValue.getMeasureId().getTitle());
        selectedMeasureID = propertyValue.getMeasureId().getId();

        Label label7 = new Label("Выберите новую единицу измерения:\n" +
                "(только для измеряемых свойств) ");
        Label label8 = new Label("");

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(propertiesComboBox, 2, 1);
        grid.add(label3, 1, 2);

        grid.add(label5, 1, 3);
        grid.add(text3, 2, 3);
        grid.add(label4, 1, 4);

        grid.add(label2, 1, 5);
        grid.add(text2, 2, 5);
        grid.add(label6, 1, 6);

        grid.add(label7, 1, 7);
        grid.add(measuresComboBox, 2, 7);
        grid.add(label8, 1, 8);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                if (text3.getText().isEmpty()) {
                    return new NewPropertyValue("", text2.getText(), selectedPropertyID, selectedProductID, selectedMeasureID);
                } else {
                    return new NewPropertyValue(text3.getText(), text2.getText(), selectedPropertyID, selectedProductID, selectedMeasureID);
                }
            }
            return null;
        });
        Optional<NewPropertyValue> result = dialog.showAndWait();
        if (result.isPresent()) {
            Session session4 = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session4.beginTransaction();
            propertyValue.setCond(result.get().getCond());
            propertyValue.setValue(result.get().getValue());
            propertyValue.setPropertyId(new Properties(selectedPropertyID));
            propertyValue.setProductId(new Products(selectedProductID));
            propertyValue.setMeasureId(new Measures(selectedMeasureID));
            session4.saveOrUpdate(propertyValue);
            tx.commit();
            session4.close();
        }
    }
    public static void deleteThePropertyValue(Label productTabTitle, TreeView<String> propertiesTree, TreeTableView propertiesTable) {
        String selectedProduct = productTabTitle.getText();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from Products where title=\'" + selectedProduct + "\'").list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            Products product = (Products) iterator.next();
            selectedProductID = product.getId();
        }
        session.close();

        String selectedPropertyType = propertiesTree.getSelectionModel().getSelectedItem().getValue();
        Session session1 = HibernateUtil.getSessionFactory().openSession();
        List res1 = session1.createQuery("from PropertyTypes where title=\'" + selectedPropertyType + "\'").list();
        for (Iterator iterator = res1.iterator(); iterator.hasNext();) {
            PropertyTypes pt = (PropertyTypes) iterator.next();
            selectedPropertyTypeID = pt.getId();
        }
        session1.close();

        ObservableList<TreeTablePosition> treeTablePositions = propertiesTable.getSelectionModel().getSelectedCells();
        for (TreeTablePosition treeTablePosition: treeTablePositions) {
            PropertiesTreeTableView propertiesTreeTableView = (PropertiesTreeTableView)treeTablePosition.getTreeItem().getValue();
            selectedPropertyValueID = propertiesTreeTableView.getPropertyValueID();
        }

        PropertyValues propertyValue = new PropertyValues();
        Session session5 = HibernateUtil.getSessionFactory().openSession();
        List res5 = session5.createQuery("from PropertyValues where id=" + selectedPropertyValueID).list();
        for (Iterator iterator = res5.iterator(); iterator.hasNext();) {
            propertyValue = (PropertyValues) iterator.next();
        }
        session5.close();

        Dialog<NewPropertyValue> dialog = new Dialog<>();
        dialog.setTitle("Удаление выбранного свойства.");
        dialog.setHeaderText("Внимание! Выбранное свойство будет удалено из базы данных.");
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
                return new NewPropertyValue(selectedPropertyID, selectedProductID, selectedMeasureID);
            }
            return null;
        });
        Optional<NewPropertyValue> result = dialog.showAndWait();
        if (result.isPresent()) {
            Session session6 = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session6.beginTransaction();

            Query q = session6.createQuery("delete PropertyValues where id =" + selectedPropertyValueID);
            q.executeUpdate();

            tx.commit();
            session6.close();
        }
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

    public static void addPropertyType(ListView<String> productKindsList) {
        String selectedProductKind = productKindsList.getSelectionModel().getSelectedItem();
        ProductKinds productKind = new ProductKinds();

        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from ProductKinds where title=\'" + selectedProductKind + "\'").list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            ProductKinds pk = (ProductKinds) iterator.next();
            productKind = pk;
        }
        session.close();

        ComboBox<String> propertyTypesComboBox = new ComboBox<>();
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

        Dialog<NewPropertyType> dialog = new Dialog<>();
        dialog.setTitle("Добавление нового набора характеристик.");
        dialog.setHeaderText("Выберите набор характеристик из списка:");
        dialog.setResizable(false);

        Label label1 = new Label("Добавить набор: ");
        Label label3 = new Label("");

        propertyTypesComboBox.setItems(propertyTypesTitlesList);

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(propertyTypesComboBox, 2, 1);
        grid.add(label3, 1, 2);
        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewPropertyType(selectedPropertyTypeID);
            }
            return null;
        });
        Optional<NewPropertyType> result = dialog.showAndWait();
        if (result.isPresent()) {
            Session session3 = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session3.beginTransaction();
            KindsTypes kindsTypes = new KindsTypes();
            kindsTypes.setProductKindId(productKind);
            kindsTypes.setPropertyTypeId(new PropertyTypes(result.get().getPropertyTypeID()));
            session3.save(kindsTypes);
            tx.commit();
            session3.close();
        }
    }
    public static void removePropertyType(ListView<String> productKindsList, TreeTableView<PropertiesTreeTableView>  propertiesTreeTable) {
        KindsTypes selectedKindsTypes = new KindsTypes();
        String selectedProductKind  = productKindsList.getSelectionModel().getSelectedItem();
        String selectedPropertyType = propertiesTreeTable.getSelectionModel().getSelectedItem().getValue().getTitle();

        Dialog<NewKindsTypes> dialog = new Dialog<>();
        dialog.setTitle("Удаление выбранного набора характеристик.");
        dialog.setHeaderText("Выбранный набор характеристик не удалится из базы данных,\n" +
                "но перестанет принадлежать выбранному типу устройств.");
        dialog.setResizable(false);

        Label label1 = new Label("Удалить выбранный набор?");
        Label label3 = new Label("");

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(label3, 1, 2);
        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                ProductKinds productKind = new ProductKinds();
                PropertyTypes propertyType = new PropertyTypes();
                Session session5 = HibernateUtil.getSessionFactory().openSession();
                List res5 = session5.createQuery("from PropertyTypes where title=\'" + selectedPropertyType + "\'").list();
                for (Iterator iterator = res5.iterator(); iterator.hasNext();) {
                    propertyType = (PropertyTypes) iterator.next();
                    selectedPropertyTypeID = propertyType.getId();
                }
                session5.close();

                Session session6 = HibernateUtil.getSessionFactory().openSession();
                List res6 = session6.createQuery("from ProductKinds where title=\'" + selectedProductKind + "\'").list();
                for (Iterator iterator = res6.iterator(); iterator.hasNext();) {
                    productKind = (ProductKinds) iterator.next();
                    selectedProductKindID = productKind.getId();
                }
                session6.close();
                return new NewKindsTypes(selectedProductKindID, selectedPropertyTypeID);
            }
            return null;
        });
        Optional<NewKindsTypes> result = dialog.showAndWait();
        if (result.isPresent()) {

            Session session = HibernateUtil.getSessionFactory().openSession();
            List res = session.createQuery("from KindsTypes where productKindId =" + result.get().getProductKindID() +
                    " and propertyTypeId =" + result.get().getPropertyTypeID()).list();
            for (Iterator iterator = res.iterator(); iterator.hasNext();) {
                selectedKindsTypes = (KindsTypes) iterator.next();
            }
            session.close();

            Session session3 = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session3.beginTransaction();
            System.out.println("delete KindsTypes where id =" + selectedKindsTypes.getId());
            Query q = session3.createQuery("delete KindsTypes where id =" + selectedKindsTypes.getId());
            q.executeUpdate();
            tx.commit();
            session3.close();
            System.out.println("selectedProductKind " + selectedProductKind);
            System.out.println("selectedPropertyType " + selectedPropertyType);
            System.out.println("selectedProductKindID " + selectedProductKindID);
            System.out.println("selectedPropertyTypeID " + selectedPropertyTypeID);
            System.out.println("selectedKindsTypes.getId() " + selectedKindsTypes.getId());
        }
    }

    public static void createNewProperty() {
        ComboBox<String> propertyTypesComboBox = new ComboBox<>();
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
            property.setTitle(text2.getText());
            //property.setValueId(new PropertyValues());
            //property.setProductId(new Products());
            property.setPropertyTypeId(new PropertyTypes(selectedPropertyTypeID, propertyTypesComboBox.getValue()));
            session3.save(property);
            tx.commit();
            session3.close();
        }
    }
    public static void updateTheProperty(TreeTableView<PropertiesTreeTableView>  propertiesTreeTable) {
        Properties property = new Properties();
        String propertyTitle = propertiesTreeTable.getSelectionModel().getSelectedItem().getValue().getTitle();
        ComboBox<String> propertyTypesComboBox = new ComboBox<>();
        ArrayList<PropertyTypes> propertyTypesList = new ArrayList<>();
        ObservableList<String> propertyTypesTitlesList = FXCollections.observableArrayList();

        Session sess = HibernateUtil.getSessionFactory().openSession();
        List res = sess.createQuery("from Properties where title=\'" + propertyTitle + "\'").list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            property = (Properties) iterator.next();
            selectedPropertyTypeID = property.getPropertyTypeId().getId();
        }
        sess.close();

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
        dialog.setTitle("Редактирование свойства.");
        dialog.setHeaderText("Здесь вы можете отредактировать\nпараметры выбранного свойства.");
        dialog.setResizable(false);

        Label label1 = new Label("Изменить тип свойства: ");
        Label label2 = new Label("Изменить название свойства: ");
        Label label3 = new Label("");

        propertyTypesComboBox.setItems(propertyTypesTitlesList);
        TextField text2 = new TextField();
        text2.setText(propertyTitle);

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

            property.setTitle(result.get().getTitle());
            property.setPropertyTypeId(new PropertyTypes(selectedPropertyTypeID));

            session3.saveOrUpdate(property);

            tx.commit();
            session3.close();
        }
    }
    public static void deleteTheProperty(TreeTableView<PropertiesTreeTableView>  propertiesTreeTable) {
        Properties property = new Properties();
        String selectedProperty = propertiesTreeTable.getSelectionModel().getSelectedItem().getValue().getTitle();

        Session session2 = HibernateUtil.getSessionFactory().openSession();
        List res2 = session2.createQuery("from Properties where title =\'" + selectedProperty + "\'").list();
        for (Iterator iterator = res2.iterator(); iterator.hasNext();) {
            property = (Properties) iterator.next();
        }
        session2.close();

        Dialog<NewProperty> dialog = new Dialog<>();
        dialog.setTitle("Удаление выбранного свойства.");
        dialog.setHeaderText("Внимание! Выбранное свойство будет удалено из базы данных.");
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
                return new NewProperty();
            }
            return null;
        });
        Optional<NewProperty> result = dialog.showAndWait();
        if (result.isPresent()) {
            Session session1 = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session1.beginTransaction();

            Query q = session1.createQuery("delete Properties where id =" + property.getId());
            q.executeUpdate();

            tx.commit();
            session1.close();
        }
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

    public static String addFunctionToProduct(TableView functionsTable1, Label productTabTitle, Label productTabKind) {
        String selectedProduct = productTabTitle.getText();
        String selectedProductKind  = productTabKind.getText();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from Products where title=\'" + selectedProduct + "\'").list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            Products product = (Products) iterator.next();
            selectedProductID = product.getId();
        }
        session.close();

        Session session2 = HibernateUtil.getSessionFactory().openSession();
        List res2 = session2.createQuery("from ProductKinds where title=\'" + selectedProductKind + "\'").list();
        for (Iterator iterator = res2.iterator(); iterator.hasNext();) {
            ProductKinds productKind = (ProductKinds) iterator.next();
            selectedProductKindID = productKind.getId();
        }
        session2.close();

        ArrayList<Functions> functionsList = new ArrayList<>();
        Session session3 = HibernateUtil.getSessionFactory().openSession();
        List res3 = session3.createQuery("from Functions where productKindId=" + selectedProductKindID).list();
        for (Iterator iterator = res3.iterator(); iterator.hasNext();) {
            Functions function = (Functions) iterator.next();
            functionsList.add(function);
        }
        session3.close();

        ObservableList<String> functionTitles = FXCollections.observableArrayList();
        functionsList.stream().forEach((f) -> {
            functionTitles.add(f.getTitle() + " (" + f.getSymbol() + ")");
        });
        ComboBox<String> functionsComboBox = new ComboBox<>();

        functionsComboBox.getItems().clear();
        functionsComboBox.getItems().addAll(functionTitles);
        AutoCompleteComboBoxListener autoCompleteComboBoxListener = new AutoCompleteComboBoxListener(functionsComboBox);
        functionsComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (Functions function: functionsList) {
                    if ((function.getTitle() + " (" + function.getSymbol() + ")").equals(functionsComboBox.getValue())) {
                        selectedFunctionID = function.getId();
                        selectedFunctionTitle = function.getTitle();
                    }
                }
            }
        });

        Dialog<NewFunctionsProducts> dialog = new Dialog<>();
        dialog.setTitle("Добавление новой функции к устройству");
        dialog.setHeaderText("Выберите требуемую функцию из списка функций,\n" +
                "доступных для данного типа устройств.");
        dialog.setResizable(false);

        Label label1 = new Label("Выберите функцию: ");
        Label label3 = new Label("");

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(functionsComboBox, 2, 1);
        grid.add(label3, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                System.out.println(selectedFunctionID);
                System.out.println(selectedProductKindID);
                return new NewFunctionsProducts(selectedFunctionID, selectedProductID);
            }
            return null;
        });
        Optional<NewFunctionsProducts> result = dialog.showAndWait();
        if (result.isPresent()) {
            Session session4 = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session4.beginTransaction();
            ProductsFunctions nfp = new ProductsFunctions();
            nfp.setFunctionId(new Functions(selectedFunctionID));
            nfp.setProductId(new Products(selectedProductID));
            session4.save(nfp);
            tx.commit();
            session4.close();
        }
        return selectedFunctionTitle;
    }
    public static void editFunctionOfProduct() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText("Редактирование функции для отдельного устройства не допускается.");
        alert.setContentText("Вы можете добавлять, редактировать и удалять описания функций устройств" +
                " только на вкладке \"Настройки\" данного приложения.");
        alert.showAndWait();
    }
    public static void removeFunctionFromProduct(TableView functionsTable1, Label productTabTitle) {
        String selectedProduct = productTabTitle.getText();
        FunctionsTableView functionsTableView = (FunctionsTableView)functionsTable1.getSelectionModel().getSelectedItem();
        selectedFunctionID = functionsTableView.getId();

        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from Products where title=\'" + selectedProduct + "\'").list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            Products product = (Products) iterator.next();
            selectedProductID = product.getId();
        }
        session.close();

        Session session3 = HibernateUtil.getSessionFactory().openSession();
        List res3 = session3.createQuery("from ProductsFunctions where productId=" + selectedProductID + " and functionId=" + selectedFunctionID).list();
        for (Iterator iterator = res3.iterator(); iterator.hasNext();) {
            ProductsFunctions pf = (ProductsFunctions) iterator.next();
            selectedProductsFunctionsID = pf.getId();
        }
        session3.close();

        Dialog<NewFunctionsProducts> dialog = new Dialog<>();
        dialog.setTitle("Удаление функции из списка");
        dialog.setHeaderText("Выбранная функция будет удалена из списка функций,\n" +
                "принадлежащих этому устройству. Удаление не повлечет за собой\n" +
                "удаления отписания данной функции из базы данных.\n" +
                "В дальнейшем можно будет закрепить эту функцию за любым\n" +
                "выбранным устройством соответствующего типа.");
        dialog.setResizable(false);

        Label label1 = new Label("Удалить функцию из списка?");
        Label label3 = new Label("");

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Удалить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewFunctionsProducts(selectedFunctionID, selectedProductID);
            }
            return null;
        });
        Optional<NewFunctionsProducts> result = dialog.showAndWait();
        if (result.isPresent()) {
            Session session4 = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session4.beginTransaction();
            Query q = session4.createQuery("delete ProductsFunctions where id=" + selectedProductsFunctionsID);
            q.executeUpdate();
            tx.commit();
            session4.close();
        }
    }

    public static void makeNewsItem() {
        Dialog<NewNewsItem> dialog = new Dialog<>();
        dialog.setTitle("Добавить новость");
        dialog.setHeaderText("После добавления новости, её содержимое можно\nотредактировать и сохранить в окне редактора.");
        dialog.setResizable(false);

        Label label1 = new Label("Введите название новости: ");
        TextField textField1 = new TextField();

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(textField1, 2, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewNewsItem();
            }
            return null;
        });
        Optional<NewNewsItem> result = dialog.showAndWait();
        if (result.isPresent()) {
            Date currentDate = new Date();
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            NewsItems ni = new NewsItems();
            ni.setTitle(textField1.getText());
            ni.setCreatedAt(currentDate);
            ni.setUpdatedAt(currentDate);
            session.save(ni);
            tx.commit();
            session.close();
        }
    }
    public static void makeArticle() {
        Dialog<NewArticle> dialog = new Dialog<>();
        dialog.setTitle("Добавить статью");
        dialog.setHeaderText("После добавления статьи, её содержимое можно\nотредактировать и сохранить в окне редактора.");
        dialog.setResizable(false);

        Label label1 = new Label("Введите название статьи: ");
        TextField textField1 = new TextField();

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(textField1, 2, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewArticle();
            }
            return null;
        });
        Optional<NewArticle> result = dialog.showAndWait();
        if (result.isPresent()) {
            Date currentDate = new Date();
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            Articles a = new Articles();
            a.setTitle(textField1.getText());
            a.setCreatedAt(currentDate);
            a.setUpdatedAt(currentDate);
            session.save(a);
            tx.commit();
            session.close();
        }
    }
    public static void makeVideo() {
        Dialog<NewVideo> dialog = new Dialog<>();
        dialog.setTitle("Добавить статью");
        dialog.setHeaderText("После добавления статьи, её содержимое можно\nотредактировать и сохранить в окне редактора.");
        dialog.setResizable(false);

        Label label1 = new Label("Введите название статьи: ");
        TextField textField1 = new TextField();

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(textField1, 2, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewVideo();
            }
            return null;
        });
        Optional<NewVideo> result = dialog.showAndWait();
        if (result.isPresent()) {
            Date currentDate = new Date();
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            Videos v = new Videos();
            v.setTitle(textField1.getText());
            v.setCreatedAt(currentDate);
            v.setUpdatedAt(currentDate);
            session.save(v);
            tx.commit();
            session.close();
        }
    }
    public static void makeReview() {
        Dialog<NewReview> dialog = new Dialog<>();
        dialog.setTitle("Добавить статью");
        dialog.setHeaderText("После добавления статьи, её содержимое можно\nотредактировать и сохранить в окне редактора.");
        dialog.setResizable(false);

        Label label1 = new Label("Введите название статьи: ");
        TextField textField1 = new TextField();

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(textField1, 2, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewReview();
            }
            return null;
        });
        Optional<NewReview> result = dialog.showAndWait();
        if (result.isPresent()) {
            Date currentDate = new Date();
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            Reviews r = new Reviews();
            r.setTitle(textField1.getText());
            r.setCreatedAt(currentDate);
            r.setUpdatedAt(currentDate);
            session.save(r);
            tx.commit();
            session.close();
        }
    }
    public static void makeAddition() {
        Dialog<NewAddition> dialog = new Dialog<>();
        dialog.setTitle("Добавить статью");
        dialog.setHeaderText("После добавления статьи, её содержимое можно\nотредактировать и сохранить в окне редактора.");
        dialog.setResizable(false);

        Label label1 = new Label("Введите название статьи: ");
        TextField textField1 = new TextField();

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(textField1, 2, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewAddition();
            }
            return null;
        });
        Optional<NewAddition> result = dialog.showAndWait();
        if (result.isPresent()) {
            Date currentDate = new Date();
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            Additions ad = new Additions();
            ad.setTitle(textField1.getText());
            ad.setCreatedAt(currentDate);
            ad.setUpdatedAt(currentDate);
            session.save(ad);
            tx.commit();
            session.close();
        }
    }
    public static void makeContent() {
        Dialog<NewContent> dialog = new Dialog<>();
        dialog.setTitle("Добавить страницу со статическим содержимым");
        dialog.setHeaderText("После добавления страницы, её содержимое можно\nотредактировать и сохранить в окне редактора.");
        dialog.setResizable(false);

        Label label1 = new Label("Введите название страницы: ");
        Label label2 = new Label("Введите название категории: ");
        Label label3 = new Label("Введите заглоловок содержимого страницы: ");
        TextField textField1 = new TextField();
        TextField textField2 = new TextField();
        TextField textField3 = new TextField();
        Label space1 = new Label();
        Label space2 = new Label();

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(textField1, 2, 1);
        grid.add(space1, 1, 2);
        grid.add(label2, 1, 3);
        grid.add(textField2, 2, 3);
        grid.add(space2, 1, 4);
        grid.add(label3, 1, 5);
        grid.add(textField3, 2, 5);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewContent();
            }
            return null;
        });
        Optional<NewContent> result = dialog.showAndWait();
        if (result.isPresent()) {
            Date currentDate = new Date();
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            StaticContents sc = new StaticContents();
            sc.setPage(textField1.getText());
            sc.setDirectory(textField2.getText());
            sc.setTitle(textField3.getText());
            sc.setCreatedAt(currentDate);
            sc.setUpdatedAt(currentDate);
            session.save(sc);
            tx.commit();
            session.close();
        }
    }
    public static void deleteNewsItem(ListView newsListView) {
        NewsItems newsItem = new NewsItems();
        String selectedNewsItem = (String)newsListView.getSelectionModel().getSelectedItem();

        Session session2 = HibernateUtil.getSessionFactory().openSession();
        List res2 = session2.createQuery("from NewsItems where title =\'" + selectedNewsItem + "\'").list();
        for (Iterator iterator = res2.iterator(); iterator.hasNext();) {
            newsItem = (NewsItems) iterator.next();
        }
        session2.close();

        Dialog<NewNewsItem> dialog = new Dialog<>();
        dialog.setTitle("Удаление выбранной новости.");
        dialog.setHeaderText("Внимание! Выбранная новость будет удалена из базы данных.");
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
                return new NewNewsItem();
            }
            return null;
        });
        Optional<NewNewsItem> result = dialog.showAndWait();
        if (result.isPresent()) {
            Session session1 = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session1.beginTransaction();

            Query q = session1.createQuery("delete NewsItems where id =" + newsItem.getId());
            q.executeUpdate();

            tx.commit();
            session1.close();
        }
    }
    public static void deleteArticle(ListView articlesListView) {
        Articles article = new Articles();
        String selectedArticle = (String)articlesListView.getSelectionModel().getSelectedItem();

        Session session2 = HibernateUtil.getSessionFactory().openSession();
        List res2 = session2.createQuery("from Articles where title =\'" + selectedArticle + "\'").list();
        for (Iterator iterator = res2.iterator(); iterator.hasNext();) {
            article = (Articles) iterator.next();
        }
        session2.close();

        Dialog<NewArticle> dialog = new Dialog<>();
        dialog.setTitle("Удаление выбранной статьи.");
        dialog.setHeaderText("Внимание! Выбранная статья будет удалена из базы данных.");
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
                return new NewArticle();
            }
            return null;
        });
        Optional<NewArticle> result = dialog.showAndWait();
        if (result.isPresent()) {
            Session session1 = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session1.beginTransaction();

            Query q = session1.createQuery("delete Articles where id =" + article.getId());
            q.executeUpdate();

            tx.commit();
            session1.close();
        }
    }
    public static void deleteVideo(ListView videosListView) {
        Videos video = new Videos();
        String selectedVideo = (String)videosListView.getSelectionModel().getSelectedItem();

        Session session2 = HibernateUtil.getSessionFactory().openSession();
        List res2 = session2.createQuery("from Videos where title =\'" + selectedVideo + "\'").list();
        for (Iterator iterator = res2.iterator(); iterator.hasNext();) {
            video = (Videos) iterator.next();
        }
        session2.close();

        Dialog<NewVideo> dialog = new Dialog<>();
        dialog.setTitle("Удаление выбранной статьи.");
        dialog.setHeaderText("Внимание! Выбранная статья будет удалена из базы данных.");
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
                return new NewVideo();
            }
            return null;
        });
        Optional<NewVideo> result = dialog.showAndWait();
        if (result.isPresent()) {
            Session session1 = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session1.beginTransaction();

            Query q = session1.createQuery("delete Videos where id =" + video.getId());
            q.executeUpdate();

            tx.commit();
            session1.close();
        }
    }
    public static void deleteReview(ListView reviewsListView) {
        Reviews review = new Reviews();
        String selectedReview = (String)reviewsListView.getSelectionModel().getSelectedItem();

        Session session2 = HibernateUtil.getSessionFactory().openSession();
        List res2 = session2.createQuery("from Reviews where title =\'" + selectedReview + "\'").list();
        for (Iterator iterator = res2.iterator(); iterator.hasNext();) {
            review = (Reviews) iterator.next();
        }
        session2.close();

        Dialog<NewReview> dialog = new Dialog<>();
        dialog.setTitle("Удаление выбранной статьи.");
        dialog.setHeaderText("Внимание! Выбранная статья будет удалена из базы данных.");
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
                return new NewReview();
            }
            return null;
        });
        Optional<NewReview> result = dialog.showAndWait();
        if (result.isPresent()) {
            Session session1 = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session1.beginTransaction();

            Query q = session1.createQuery("delete Reviews where id =" + review.getId());
            q.executeUpdate();

            tx.commit();
            session1.close();
        }
    }
    public static void deleteAddition(ListView additionsListView) {
        Additions addition = new Additions();
        String selectedAddition = (String)additionsListView.getSelectionModel().getSelectedItem();

        Session session2 = HibernateUtil.getSessionFactory().openSession();
        List res2 = session2.createQuery("from Additions where title =\'" + selectedAddition + "\'").list();
        for (Iterator iterator = res2.iterator(); iterator.hasNext();) {
            addition = (Additions) iterator.next();
        }
        session2.close();

        Dialog<NewAddition> dialog = new Dialog<>();
        dialog.setTitle("Удаление выбранной статьи.");
        dialog.setHeaderText("Внимание! Выбранная статья будет удалена из базы данных.");
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
                return new NewAddition();
            }
            return null;
        });
        Optional<NewAddition> result = dialog.showAndWait();
        if (result.isPresent()) {
            Session session1 = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session1.beginTransaction();

            Query q = session1.createQuery("delete Additions where id =" + addition.getId());
            q.executeUpdate();

            tx.commit();
            session1.close();
        }
    }
    public static void deleteContent(ListView contentsListView) {
        StaticContents staticContent = new StaticContents();
        String selectedStaticContent = (String)contentsListView.getSelectionModel().getSelectedItem();

        Session session2 = HibernateUtil.getSessionFactory().openSession();
        List res2 = session2.createQuery("from StaticContents where title =\'" + selectedStaticContent + "\'").list();
        for (Iterator iterator = res2.iterator(); iterator.hasNext();) {
            staticContent = (StaticContents) iterator.next();
        }
        session2.close();

        Dialog<NewContent> dialog = new Dialog<>();
        dialog.setTitle("Удаление выбранной страницы статического содержимого.");
        dialog.setHeaderText("Внимание! Выбранная страница статического содержимого\nбудет удалена из базы данных.");
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
                return new NewContent();
            }
            return null;
        });
        Optional<NewContent> result = dialog.showAndWait();
        if (result.isPresent()) {
            Session session1 = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session1.beginTransaction();

            Query q = session1.createQuery("delete StaticContents where id =" + staticContent.getId());
            q.executeUpdate();

            tx.commit();
            session1.close();
        }
    }
    public static void changeProductVendor(TableView<ProductsTableView> productsTable) {
        ObservableList<ProductsTableView> selectedProducts = productsTable.getSelectionModel().getSelectedItems();
        ObservableList<Vendors> vendorsList = FXCollections.observableArrayList();
        ComboBox<String> vendorsComboBox = new ComboBox<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from Vendors").list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            Vendors vendor = (Vendors) iterator.next();
            vendorsList.add(vendor);
        }
        session.close();
        ObservableList<String> vendorsTitles = FXCollections.observableArrayList();
        vendorsList.stream().forEach((vendors) -> {
            vendorsTitles.add(vendors.getTitle());
        });
        vendorsComboBox.setItems(vendorsTitles);
        vendorsComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (Vendors vendor : vendorsList) {
                    if (vendor.getTitle().equals(vendorsComboBox.getValue())) {
                        selectedVendor = vendor;
                    }
                }
            }
        });

        Dialog<VendorsTableView> dialog = new Dialog<>();
        dialog.setTitle("Изменить производителя устройства");
        dialog.setHeaderText("");
        dialog.setResizable(false);

        Label label1 = new Label("Выберите производителя из списка:");
        Label br = new Label("");

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);;
        grid.add(br, 2, 1);
        grid.add(vendorsComboBox, 3, 1);
        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Установить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                VendorsTableView selectedVendorsTableView = new VendorsTableView(
                        selectedVendor.getTitle(), selectedVendor.getAddress(), selectedVendor.getRate());
                return selectedVendorsTableView;
            }
            return null;
        });
        Optional<VendorsTableView> result = dialog.showAndWait();
        Session session1 = HibernateUtil.getSessionFactory().openSession();
        for (ProductsTableView selectedProduct : selectedProducts) {
            Transaction tx = session1.beginTransaction();
            Query query = session1.createQuery("update Products set vendor = :vendor where title = :title");
            query.setParameter("vendor", selectedVendor);
            query.setParameter("title", selectedProduct.getTitle());
            query.executeUpdate();
            tx.commit();
        }
        session1.close();
    }
}
