package tableviews;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Igor Klekotnev on 28.01.2016.
 */
public class AccessoriesTableView {

    private final StringProperty title;
    private final StringProperty description;
    private Integer id;

    public AccessoriesTableView() {
        this(null, null, null);
    }

    public AccessoriesTableView(String title, String description, Integer id) {
        this.title  = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.id = id;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
