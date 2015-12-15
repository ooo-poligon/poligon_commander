/*
 * 
 * 
 */
package tableviews;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Igor Klekotnev
 */
public class AnalogsTableView {

    private final StringProperty analog_title;
    private final StringProperty analog_vendor;  
    
    public AnalogsTableView() {
        this(null, null);
    }    

    public AnalogsTableView(String analog_title, String analog_vendor) {
        this.analog_title  = new SimpleStringProperty(analog_title);
        this.analog_vendor = new SimpleStringProperty(analog_vendor);
    }
  
    public String getAnalog_title() {
        return analog_title.get();
    }

    public void setAnalog_title(String analog_title) {
        this.analog_title.set(analog_title);
    }

    public StringProperty analog_titleProperty() {
        return analog_title;
    }

    public String getAnalog_vendor() {
        return analog_vendor.get();
    }

    public void setAnalog_vendor(String analog_vendor) {
        this.analog_vendor.set(analog_vendor);
    }
    
    public StringProperty analog_vendorProperty() {
        return analog_vendor;
    }
}
