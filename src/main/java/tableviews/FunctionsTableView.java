package tableviews;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by kataev on 11.01.2016.
 */
public class FunctionsTableView {
    private final StringProperty title;

    public FunctionsTableView(String title) {
        this.title = new SimpleStringProperty(title);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }
}
