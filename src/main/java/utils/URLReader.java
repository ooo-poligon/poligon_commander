package utils;

/**
 * Created by Igor Klekotnev on 20.01.2016.
 */
import modalwindows.AlertWindow;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class URLReader {

    public static ArrayList<String> getContentFromAddress(URL url) throws IOException {
        ArrayList<String> result = new ArrayList<>();
        String inputLine;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((inputLine = in.readLine()) != null) result.add(inputLine);
            in.close();
        } catch (IOException e) {
            //AlertWindow.alertNoRbcServerConnection();
        }
        return result;
    }
}
