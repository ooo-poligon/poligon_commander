/*
 *
 *
 */
package poligoncommander;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Igor Klekotnev
 */
public class Product {

    private final StringProperty vendor;
    private final StringProperty serie;
    private final StringProperty title;
    private final StringProperty description;
    
    public Product() {
        this(null, null, null, null);
    }    

    public Product(String vendor, String serie, String title, String description) {
        this.vendor      = new SimpleStringProperty(vendor);
        this.serie       = new SimpleStringProperty(serie);
        this.title       = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
    }

    public String getVendor() {
        return vendor.get();
    }

    public void setVendor(String vendor) {
        this.vendor.set(vendor);
    }

    public StringProperty vendorProperty() {
        return vendor;
    }
    
    public String getSerie() {
        return serie.get();
    }

    public void setSerie(String serie) {
        this.serie.set(serie);
    }

    public StringProperty serieProperty() {
        return serie;
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

