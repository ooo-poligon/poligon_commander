package utils;

import entities.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import main.PCGUIController;
import new_items.NewProductsAnalogs;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import tableviews.AnalogsTableView;
import tableviews.ProductsTableView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Created by Igor Klekotnev on 27.01.2016.
 */
public class Analog {
    private static Integer analogId = 0;

    public static void addToSelectedOn(TableView productsTable) {
        ObservableList<ProductsTableView> selectedItems = (
                ObservableList<ProductsTableView>) productsTable.getSelectionModel().getSelectedItems();
        ArrayList<Integer> selectedItemsIds = new ArrayList<>();
        ComboBox<String> analogsComboBox = new ComboBox<>();
        analogsComboBox.setItems(PCGUIController.allProductsTitles);
        AutoCompleteComboBoxListener autoCompleteComboBoxListener = new AutoCompleteComboBoxListener(analogsComboBox);
        analogsComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                PCGUIController.allProductsList.stream().forEach(product -> {
                    if (product.getTitle().equals(analogsComboBox.getValue())) {
                        analogId = product.getId();
                    }
                });
            }
        });

        Dialog<NewProductsAnalogs> dialog = new Dialog<>();
        dialog.setTitle("Добавление аналога к выбранным устройствам");
        dialog.setHeaderText("Выберите аналог из списка:");
        dialog.setResizable(false);

        Label label3 = new Label("");

        GridPane grid = new GridPane();
        grid.add(analogsComboBox, 2, 1);
        grid.add(label3, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewProductsAnalogs();
            }
            return null;
        });
        Optional<NewProductsAnalogs> result = dialog.showAndWait();
        if (result.isPresent()) {
            for (ProductsTableView product: selectedItems) {
                PCGUIController.allProductsList.stream().forEach(p -> {
                    if (p.getTitle().equals(product.getTitle())) {
                        selectedItemsIds.add(p.getId());
                    }
                });
            }
            for (Integer id: selectedItemsIds) {
                ArrayList<Integer> existIds = new ArrayList<>();
                Session session = HibernateUtil.getSessionFactory().openSession();
                List res = session.createQuery(
                        "from ProductsAnalogs where productId=" + id + " and analogId=" + analogId).list();
                for (Iterator iterator = res.iterator(); iterator.hasNext();) {
                    ProductsAnalogs pa = (ProductsAnalogs) iterator.next();
                    existIds.add(pa.getId());
                }
                if (existIds.isEmpty()) {
                    Transaction tx = session.beginTransaction();
                    ProductsAnalogs pa = new ProductsAnalogs();
                    pa.setProductId(id);
                    pa.setAnalogId(analogId);
                    session.save(pa);
                    tx.commit();
                }
                session.close();
            }
        }
    }

    public static void addToSelectedOn(String selectedProduct) {
        ComboBox<String> analogsComboBox = new ComboBox<>();
        final Integer[] selectedProductID = {0};
        analogsComboBox.setItems(PCGUIController.allProductsTitles);
        AutoCompleteComboBoxListener autoCompleteComboBoxListener = new AutoCompleteComboBoxListener(analogsComboBox);
        analogsComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                PCGUIController.allProductsList.stream().forEach(product -> {
                    if (product.getTitle().equals(analogsComboBox.getValue())) {
                        analogId = product.getId();
                    }
                });
            }
        });

        Dialog<NewProductsAnalogs> dialog = new Dialog<>();
        dialog.setTitle("Добавление аналога к выбранным устройствам");
        dialog.setHeaderText("Выберите аналог из списка:");
        dialog.setResizable(false);

        Label label3 = new Label("");

        GridPane grid = new GridPane();
        grid.add(analogsComboBox, 2, 1);
        grid.add(label3, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewProductsAnalogs();
            }
            return null;
        });
        Optional<NewProductsAnalogs> result = dialog.showAndWait();
        if (result.isPresent()) {
            PCGUIController.allProductsList.stream().forEach(product -> {
                if (product.getTitle().equals(selectedProduct)) {
                    selectedProductID[0] = product.getId();
                }
            });

            ArrayList<Integer> existIds = new ArrayList<>();
            Session session2 = HibernateUtil.getSessionFactory().openSession();
            List res2 = session2.createQuery(
                    "from ProductsAnalogs where productId=" + selectedProductID[0] + " and analogId=" + analogId).list();
            for (Iterator iterator = res2.iterator(); iterator.hasNext();) {
                ProductsAnalogs pa = (ProductsAnalogs) iterator.next();
                existIds.add(pa.getId());
            }
            if (existIds.isEmpty()) {
                Transaction tx = session2.beginTransaction();
                ProductsAnalogs pa = new ProductsAnalogs();
                pa.setProductId(selectedProductID[0]);
                pa.setAnalogId(analogId);
                session2.save(pa);
                tx.commit();
            }
            session2.close();
        }
    }

    public static void removeAnalogFrom(TableView analogsTable, String selectedProduct) {
        AnalogsTableView analogsTableView = (AnalogsTableView) analogsTable.getSelectionModel().getSelectedItem();
        Integer selectedAnalogId = UtilPack.getProductIdFromTitle(analogsTableView.getTitle(), PCGUIController.allProductsList);
        final Integer[] selectedProductID = {0};
        PCGUIController.allProductsList.stream().forEach(product -> {
            if (product.getTitle().equals(selectedProduct)) {
                selectedProductID[0] = product.getId();
            }
        });

        Dialog<NewProductsAnalogs> dialog = new Dialog<>();
        dialog.setTitle("Удаление аналога из списка");
        dialog.setHeaderText("Выбранный аналог будет удалён из списка аналогов,\n" +
                "относящихся к этому устройству. Удаление не повлечет за собой\n" +
                "удаления выбранного аналога из базы данных.\n" +
                "В дальнейшем можно будет закрепить этот аналог за любым\n" +
                "выбранным устройством или группой устройств.");
        dialog.setResizable(false);

        Label label1 = new Label("Удалить аналог из списка?");
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
                return new NewProductsAnalogs();
            }
            return null;
        });
        Optional<NewProductsAnalogs> result = dialog.showAndWait();
        if (result.isPresent()) {
            Session session1 = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session1.beginTransaction();
            Query q = session1.createQuery("delete ProductsAnalogs where analogId=" + selectedAnalogId + " and productId=" + selectedProductID[0]);
            q.executeUpdate();
            tx.commit();
            session1.close();
        }
    }
}

