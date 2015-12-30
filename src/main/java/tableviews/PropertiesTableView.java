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
    private final StringProperty condition;
    private final StringProperty value;

    public PropertiesTableView(String title, String condition, String value) {
        this.title = new SimpleStringProperty(title);
        this.condition = new SimpleStringProperty(condition);
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

    public String getCondition() {
        return condition.get();
    }

    public StringProperty conditionProperty() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition.set(condition);
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

