package utils;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import netscape.javascript.JSException;

import java.io.File;
import java.net.MalformedURLException;

/**
 * Created by Igor Klekotnev on 29.01.2016. (with a little help of https://gist.github.com/JaDogg/1204d6f28e89e22f7b7b)
 */
public class ExtendHtmlEditor {

    final static FileChooser fileChooser = new FileChooser();


    public static void addPictureFunction(HTMLEditor htmlEditor, AnchorPane editorAnchorPane) {

        Node toolNode = htmlEditor.lookup(".top-toolbar");
        Node webNode = htmlEditor.lookup(".web-view");

        if (toolNode instanceof ToolBar && webNode instanceof WebView) {
            ToolBar bar = (ToolBar) toolNode;
            WebView webView = (WebView) webNode;
            WebEngine engine = webView.getEngine();

            Button btnCaretAddImage = new Button("Добавить картинку");
            btnCaretAddImage.setMinSize(120.0, 24.0);
            btnCaretAddImage.setMaxSize(120.0, 24.0);

            bar.getItems().addAll(btnCaretAddImage);
            String jsCodeInsertHtml = "function insertHtmlAtCursor(html) {\n" +
                    "    var range, node;\n" +
                    "    if (window.getSelection && window.getSelection().getRangeAt) {\n" +
                    "        range = window.getSelection().getRangeAt(0);\n" +
                    "        node = range.createContextualFragment(html);\n" +
                    "        range.insertNode(node);\n" +
                    "    } else if (document.selection && document.selection.createRange) {\n" +
                    "        document.selection.createRange().pasteHTML(html);\n" +
                    "    }\n" +
                    "}insertHtmlAtCursor('####html####')";
            btnCaretAddImage.setOnAction((ActionEvent event) -> {
                try {
                    File file = fileChooser.showOpenDialog(editorAnchorPane.getScene().getWindow());
                    String localUrl = file.toURI().toURL().toString();
                    String img = "<img src=\"" + localUrl + "\" />";

                    engine.executeScript(jsCodeInsertHtml.replace("####html####", img));
                } catch (JSException e) {} catch (MalformedURLException e) {}
            });
        }

    }
}
