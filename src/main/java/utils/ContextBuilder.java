package utils;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;

/**
 * Created by Igor Klekotnev on 13.01.2016.
 */
public class ContextBuilder {

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

    public static void createNewFunction() {

    }
    public static void updateTheFunction() {

    }
    public static void deleteTheFunction() {

    }
}
