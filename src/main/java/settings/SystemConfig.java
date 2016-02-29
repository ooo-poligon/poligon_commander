package settings;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by Igor Klekotnev on 02.02.2016.
 */

public class SystemConfig {
    public static String URL;
    public static String USERNAME;
    public static String PASSWORD;

    private static TextField text1 = new TextField();
    private static TextField text2 = new TextField();
    private static TextField text3 = new TextField();
    private static TextField text4 = new TextField();
    private static PasswordField text5 = new PasswordField();

    public static void getSettingsDialog() {

        Dialog<SystemConfig> dialog = new Dialog<>();
        dialog.setTitle("Установка соединения с БД");
        dialog.setHeaderText("Задайте параметры соединения с БД.");
        dialog.setResizable(false);

        Label label1 = new Label("Адрес БД: ");
        Label label2 = new Label("Порт БД: ");
        Label label3 = new Label("Имя БД ");
        Label label4 = new Label("Пользователь: ");
        Label label5 = new Label("Пароль: ");


        text1.setText(getSavedDBSettings().get(0));
        text2.setText(getSavedDBSettings().get(1));
        text3.setText(getSavedDBSettings().get(2));
        text4.setText(getSavedDBSettings().get(3));
        text5.setText(getSavedDBSettings().get(4));


        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(text1, 2, 1);

        grid.add(label2, 1, 2);
        grid.add(text2, 2, 2);

        grid.add(label3, 1, 3);
        grid.add(text3, 2, 3);

        grid.add(label4, 1, 4);
        grid.add(text4, 2, 4);

        grid.add(label5, 1, 5);
        grid.add(text5, 2, 5);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Запуск", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Выход", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new SystemConfig();
            } else {
                Platform.exit();
                System.exit(0);
            }
            return null;
        });
        Optional<SystemConfig> result = dialog.showAndWait();
        if (result.isPresent()) {
            URL = text1.getText() + ":" + text2.getText() + "/" + text3.getText();
            USERNAME = text4.getText();
            PASSWORD = text5.getText();
        }
    }

    private static ArrayList<String> getSavedDBSettings() {
        ArrayList<String> settings = new ArrayList<>();
        settings.add("localhost");
        settings.add("3306");
        settings.add("poligon_commander");
        settings.add("root");
        settings.add("poligon");
        return settings;
    }
}
