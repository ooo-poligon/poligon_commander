package tableviews;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Igor Klekotnev on 01.09.16.
 */
public class ProductKindPropertiesTableView {
    private final StringProperty title;
    private final StringProperty optional;
    private final StringProperty symbol;

    public ProductKindPropertiesTableView() {
        this.title  = null;
        this.optional = null;
        this.symbol = null;
    }

    public ProductKindPropertiesTableView(String title, String optional, String symbol) {
        this.title  = new SimpleStringProperty(title);
        this.optional = new SimpleStringProperty(optional);
        this.symbol = new SimpleStringProperty(symbol);
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

    public String getOptional() {
        return optional.get();
    }

    public StringProperty optionalProperty() {
        return optional;
    }

    public void setOptional(String optional) {
        this.optional.set(optional);
    }

    public String getSymbol() {
        return symbol.get();
    }

    public StringProperty symbolProperty() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol.set(symbol);
    }
}
