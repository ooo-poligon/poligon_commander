package tableviews;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Igor Klekotnev on 28.01.2016.
 */
public class AnalogsTableView {

    private final StringProperty title;
    private final StringProperty vendor;

    public AnalogsTableView() {
        this(null, null);
    }

    public AnalogsTableView(String title, String vendor) {
        this.title  = new SimpleStringProperty(title);
        this.vendor = new SimpleStringProperty(vendor);
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

    public String getVendor() {
        return vendor.get();
    }

    public void setVendor(String vendor) {
        this.vendor.set(vendor);
    }

    public StringProperty vendorProperty() {
        return vendor;
    }
}
