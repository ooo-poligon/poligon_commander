package tableviews;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Igor Klekotnev on 28.04.2016.
 */
public class GroupsTableView {
    private final StringProperty title;
    private final StringProperty description;

    public GroupsTableView()  {
        this(null, null);
    }

    public GroupsTableView(String title) {
        this.title = new SimpleStringProperty(title);
        this.description =null;
    }

    public GroupsTableView(String title, String description) {
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
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

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }
}
