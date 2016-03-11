package modalwindows;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import main.PCGUIController;

import java.util.Optional;

/**
 * Created by Igor Klekotnev on 11.03.2016.
 */
public class AlertWindow {
    public static void alertNoRbcServerConnection () {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText("Нет связи с сервером \"CBR\"!");
        alert.setContentText("Актуальные данные курсов валют, а следовательно,\n" +
                "все цены могут быть неверны!");
        alert.showAndWait();
    }
    public static void notLowestCategoryAlert() {
        Alert notLowestCategoryAlert = new Alert(Alert.AlertType.WARNING);
        notLowestCategoryAlert.setTitle("Внимание!");
        notLowestCategoryAlert.setHeaderText("Выбранная категория содержит вложенные категории " +
                "и не может содержать отдельные элементы каталога.");
        notLowestCategoryAlert.setContentText("Создайте в этой категории новую подкатегорию " +
                "для выбранных элементов, а затем перенесите в неё эти элементы. Вы также можете перенести " +
                "выбранные элементы в любую другую категорию, не содержащую подкатегорий.");
        notLowestCategoryAlert.show();
    }
    public static void showWarning(String warningText) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText("Неправильная настройка импорта данных.");
        alert.setContentText(warningText);
        alert.showAndWait();
    }
    public static void illegalAction() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText("Недопустимое действие!");
        alert.setContentText("В таблице цен можно устанавливать только закупочную валютную цену.\n" +
                "Все остальные цены являются расчётными и редактированию не подлежат.");
        alert.showAndWait();
    }
    public static Optional<ButtonType> categoryDeleteAttention(String catTitle) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Внимание!");
        alert.setTitle("Удаление категории");
        String s = "Категория \"" + catTitle + "\" будет удалена из каталога. " +
                "Все элементы, принадлежащие этой категории будут перенесены в ближайшшую вышестоящую категорию.";
        alert.setContentText(s);
        return alert.showAndWait();
    }
    public static Optional<ButtonType> changeProductCategoryDialog(
            StackPane stackPaneModal, ButtonType buttonTypeOk, ButtonType buttonTypeCancel) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        DialogPane dialog = new DialogPane();
        alert.setDialogPane(dialog);
        alert.setTitle("Переместить выбранные элементы");
        alert.setHeaderText("Укажите категорию, в которую следует \nпереместить выбранные элементы:");
        alert.setResizable(false);
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(stackPaneModal);
        stackPaneModal.setAlignment(Pos.CENTER);

        dialog.setContent(anchorPane);
        dialog.getButtonTypes().add(buttonTypeOk);
        dialog.getButtonTypes().add(buttonTypeCancel);
        return alert.showAndWait();
    }
    public static void showError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("Ошибка соединения с базой данных.\nРекомендуется перезапустить приложение.");
        alert.show();
    }

}
