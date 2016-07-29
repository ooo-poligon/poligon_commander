package tableviews;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Igor Klekotnev on 11.01.2016.
 */
public class ProductPropertiesTableView {
    private StringProperty title;
    private StringProperty cond;
    private StringProperty value;
    private StringProperty measure;

    private Integer propertyValueID;

    public ProductPropertiesTableView(String title) {
        this.title = new SimpleStringProperty(title);
        this.cond  = null;
        this.value = null;
        this.measure = null;
    }

    public ProductPropertiesTableView(String title, String cond, String value, String measure) {
        this.title   = new SimpleStringProperty(title);
        this.cond    = new SimpleStringProperty(cond);
        this.value   = new SimpleStringProperty(value);
        this.measure = new SimpleStringProperty(measure);
    }

    public ProductPropertiesTableView(String title, String cond, String value, String measure, Integer propertyValueID) {
        this.title   = new SimpleStringProperty(title);
        this.cond    = new SimpleStringProperty(cond);
        this.value   = new SimpleStringProperty(value);
        this.measure = new SimpleStringProperty(measure);
        this.propertyValueID = propertyValueID;
    }

    public void setTitle(String title) { titleProperty().set(title); }
    public String getTitle() { return titleProperty().get(); }
    public StringProperty titleProperty() {
        if (title == null) title = new SimpleStringProperty(this, "title");
        return title;
    }

    public void setCond(String cond) { condProperty().set(cond); }
    public String getCond() { return condProperty().get(); }
    public StringProperty condProperty() {
        if (cond == null) cond = new SimpleStringProperty(this, "cond");
        return cond;
    }

    public void setValue(String value) { valueProperty().set(value); }
    public String getValue() { return valueProperty().get(); }
    public StringProperty valueProperty() {
        if (value == null) value = new SimpleStringProperty(this, "value");
        return value;
    }

    public void setMeasure(String measure) { measureProperty().set(measure); }
    public String getMeasure() { return measureProperty().get(); }
    public StringProperty measureProperty() {
        if (measure == null) measure = new SimpleStringProperty(this, "measure");
        return measure;
    }

    public Integer getPropertyValueID() {
        return propertyValueID;
    }

    public void setPropertyValueID(Integer propertyValueID) {
        this.propertyValueID = propertyValueID;
    }
}
