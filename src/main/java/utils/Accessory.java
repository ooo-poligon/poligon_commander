package utils;

import entities.ProductsAccessories;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import main.PCGUIController;
import new_items.NewProductsAccessories;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import tableviews.AccessoriesTableView;
import tableviews.ProductsTableView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Created by Igor Klekotnev on 27.01.2016.
 */
public class Accessory {
    private static Integer accessoryId = 0;

    public static void addToSelectedOn(TableView productsTable) {
        ObservableList<ProductsTableView> selectedItems = (
                ObservableList<ProductsTableView>) productsTable.getSelectionModel().getSelectedItems();
        ArrayList<Integer> selectedItemsIds = new ArrayList<>();
        ComboBox<String> accessoriesComboBox = new ComboBox<>();
        accessoriesComboBox.setItems(PCGUIController.allProductsTitles);
        AutoCompleteComboBoxListener autoCompleteComboBoxListener = new AutoCompleteComboBoxListener(accessoriesComboBox);
        accessoriesComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                PCGUIController.allProductsList.stream().forEach(product -> {
                    if (product.getTitle().equals(accessoriesComboBox.getValue())) {
                        accessoryId = product.getId();
                    }
                });
            }
        });

        Dialog<NewProductsAccessories> dialog = new Dialog<>();
        dialog.setTitle("Добавление аксессуара к выбранным устройствам");
        dialog.setHeaderText("Выберите аксессуар из списка:");
        dialog.setResizable(false);

        Label label3 = new Label("");

        GridPane grid = new GridPane();
        grid.add(accessoriesComboBox, 2, 1);
        grid.add(label3, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewProductsAccessories();
            }
            return null;
        });
        Optional<NewProductsAccessories> result = dialog.showAndWait();
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
                        "from ProductsAccessories where productId=" + id + " and accessoryId=" + accessoryId).list();
                for (Iterator iterator = res.iterator(); iterator.hasNext();) {
                    ProductsAccessories pa = (ProductsAccessories) iterator.next();
                    existIds.add(pa.getId());
                }
                if (existIds.isEmpty()) {
                    Transaction tx = session.beginTransaction();
                    ProductsAccessories pa = new ProductsAccessories();
                    pa.setProductId(id);
                    pa.setAccessoryId(accessoryId);
                    session.save(pa);
                    tx.commit();
                }
                session.close();
            }
        }
    }

    public static void addToSelectedOn(String selectedProduct) {
        ComboBox<String> accessoriesComboBox = new ComboBox<>();
        final Integer[] selectedProductID = {0};
        accessoriesComboBox.setItems(PCGUIController.allProductsTitles);
        AutoCompleteComboBoxListener autoCompleteComboBoxListener = new AutoCompleteComboBoxListener(accessoriesComboBox);
        accessoriesComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                PCGUIController.allProductsList.stream().forEach(product -> {
                    if (product.getTitle().equals(accessoriesComboBox.getValue())) {
                        accessoryId = product.getId();
                    }
                });
            }
        });

        Dialog<NewProductsAccessories> dialog = new Dialog<>();
        dialog.setTitle("Добавление аксессуара к выбранным устройствам");
        dialog.setHeaderText("Выберите аксессуар из списка:");
        dialog.setResizable(false);

        Label label3 = new Label("");

        GridPane grid = new GridPane();
        grid.add(accessoriesComboBox, 2, 1);
        grid.add(label3, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewProductsAccessories();
            }
            return null;
        });
        Optional<NewProductsAccessories> result = dialog.showAndWait();
        if (result.isPresent()) {
            PCGUIController.allProductsList.stream().forEach(product -> {
                if (product.getTitle().equals(selectedProduct)) {
                    selectedProductID[0] = product.getId();
                }
            });

            ArrayList<Integer> existIds = new ArrayList<>();
            Session session2 = HibernateUtil.getSessionFactory().openSession();
            List res2 = session2.createQuery(
                    "from ProductsAccessories where productId=" + selectedProductID[0] + " and accessoryId=" + accessoryId).list();
            for (Iterator iterator = res2.iterator(); iterator.hasNext();) {
                ProductsAccessories pa = (ProductsAccessories) iterator.next();
                existIds.add(pa.getId());
            }
            if (existIds.isEmpty()) {
                Transaction tx = session2.beginTransaction();
                ProductsAccessories pa = new ProductsAccessories();
                pa.setProductId(selectedProductID[0]);
                pa.setAccessoryId(accessoryId);
                session2.save(pa);
                tx.commit();
            }
            session2.close();
        }
    }

    public static void removeAccessoryFrom(TableView accessoriesTable, String selectedProduct) {
        AccessoriesTableView accessoriesTableView = (AccessoriesTableView) accessoriesTable.getSelectionModel().getSelectedItem();
        Integer selectedAccessoryId =  accessoriesTableView.getId();
        final Integer[] selectedProductID = {0};
        PCGUIController.allProductsList.stream().forEach(product -> {
            if (product.getTitle().equals(selectedProduct)) {
                selectedProductID[0] = product.getId();
            }
        });

        Dialog<NewProductsAccessories> dialog = new Dialog<>();
        dialog.setTitle("Удаление аксессуара из списка");
        dialog.setHeaderText("Выбранный аксессуар будет удалён из списка аксессуаров,\n" +
                "относящихся к этому устройству. Удаление не повлечет за собой\n" +
                "удаления выбранного аксессуара из базы данных.\n" +
                "В дальнейшем можно будет закрепить этот аксессуар за любым\n" +
                "выбранным устройством или группой устройств.");
        dialog.setResizable(false);

        Label label1 = new Label("Удалить аксессуар из списка?");
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
                return new NewProductsAccessories();
            }
            return null;
        });
        Optional<NewProductsAccessories> result = dialog.showAndWait();
        if (result.isPresent()) {
            Session session1 = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session1.beginTransaction();
            Query q = session1.createQuery("delete ProductsAccessories where accessoryId=" + selectedAccessoryId + " and productId=" + selectedProductID[0]);
            q.executeUpdate();
            tx.commit();
            session1.close();
        }
    }
}
