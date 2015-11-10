/*
 * 
 * 
 */
package poligoncommander;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Igor Klekotnev
 */
public class Serie {

    private final IntegerProperty id;
    private final StringProperty title;
    private final StringProperty description;
    
    public Serie() {
        this(null, null, null);
    }    

    public Serie(Integer id, String title, String description) {
        this.id          = new SimpleIntegerProperty(id);
        this.title       = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
    }

    public Integer getId() {
        return id.get();
    }

    public void setId(Integer id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
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
    
}
