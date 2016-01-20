package utils;

/**
 * Created by Igor Klekotnev on 20.01.2016.
 */
import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class URLReader {

    public static ArrayList<String> getContentFromAddress(URL url) {
        ArrayList<String> result = new ArrayList<>();
        String inputLine;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((inputLine = in.readLine()) != null) result.add(inputLine);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
