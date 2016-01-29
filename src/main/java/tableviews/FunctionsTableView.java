package tableviews;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by kataev on 11.01.2016.
 */
public class FunctionsTableView {
    private final StringProperty title;
    private final StringProperty symbol;
    private Integer id;

    public FunctionsTableView(String title) {
        this.title = new SimpleStringProperty(title);
        this.symbol = new SimpleStringProperty("");
    }

    public FunctionsTableView(String title, String symbol, Integer id) {
        this.title =  new SimpleStringProperty(title);
        this.symbol = new SimpleStringProperty(symbol);
        this.id = id;
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

    public String getSymbol() {
        return symbol.get();
    }

    public StringProperty symbolProperty() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol.set(symbol);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
