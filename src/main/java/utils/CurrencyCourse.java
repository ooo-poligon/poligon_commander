package utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Igor Klekotnev on 20.01.2016.
 */
public class CurrencyCourse {
    String name;
    Double value;

    public CurrencyCourse(String name) {
        this.name = name;
        this.value = 0.0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public ArrayList getValueFromCBR() throws IOException {
        ArrayList result = new ArrayList();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String currentDate = dateFormat.format(date);
        final String urlAddress = "http://cbr.ru/scripts/XML_daily.asp?date_req=" + currentDate;
        ArrayList<String> content = null;
        try {
            content = URLReader.getContentFromAddress(new URL(urlAddress));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        XMLParser xmlParser = new XMLParser(content);
        result.add(xmlParser.getValueOfCurrency("EUR"));
        result.add(currentDate);
        return result;
    }
}
