package utils;

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
        str = str.substring(8, 15).replace(',', '.');
        value = Double.parseDouble(str);
        return value;
    }
}
