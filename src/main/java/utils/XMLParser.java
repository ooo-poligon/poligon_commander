package utils;

import javafx.application.Platform;
import modalwindows.AlertWindow;

import java.util.ArrayList;

/**
 * Created by Igor Klekotnev on 20.01.2016.
 */
public class XMLParser {
    ArrayList<String> content;

    public XMLParser (ArrayList<String> content) {
        this.content = content;
    }

    public Double getValueOfCurrency(String currencyName){
        Double value = 0.0;
        String str = "";
        int counter = 0;
        for (int i = 0; i < content.size(); i++) {
            if (content.get(i).contains("EUR")) {
                str = content.get(i+3);
                break;
            }
            counter++;
        }
        try {
            str = str.substring(8, 15).replace(',', '.');
            value = Double.parseDouble(str);
        } catch (StringIndexOutOfBoundsException e) {
            AlertWindow.alertNoRbcServerConnection();
            AlertWindow.showErrorMessage("Не удалось установить соединение с сервером rbc.ru для получения актуальных курсов валют.\n" +
                    "Обеспечьте соединение с интернетом и повторите запуск программы.");
            Platform.exit();
            System.exit(0);
        }

        return value;
    }
}
