package tableviews;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Igor Klekotnev on 22.12.2015.
 */
public class PropertiesTableView {
    private final StringProperty title;
    private final StringProperty cond;
    private final StringProperty value;

    public PropertiesTableView(String title, String cond, String value) {
        this.title = new SimpleStringProperty(title);
        this.cond = new SimpleStringProperty(cond);
        this.value = new SimpleStringProperty(value);
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

    public String getCond() {
        return cond.get();
    }

    public StringProperty condProperty() {
        return cond;
    }

    public void setCond(String cond) {
        this.cond.set(cond);
    }

    public String getValue() {
        return value.get();
    }

    public StringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }
}

