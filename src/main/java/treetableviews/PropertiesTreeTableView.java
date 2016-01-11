package treetableviews;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by kataev on 11.01.2016.
 */
public class PropertiesTreeTableView {
    private StringProperty title;
    public PropertiesTreeTableView (String title) {
        this.title = new SimpleStringProperty(title);
    }
    public void setTitle(String value) { titleProperty().set(value); }
    public String getTitle() { return titleProperty().get(); }
    public StringProperty titleProperty() {
        if (title == null) title = new SimpleStringProperty(this, "title");
        return title;
    }
}
