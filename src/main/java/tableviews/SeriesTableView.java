package tableviews;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Igor Klekotnev on 18.03.2016.
 */
public class SeriesTableView {
    private final StringProperty title;
    private final StringProperty vendor;

    public SeriesTableView(String title, String vendor) {
        this.title = new SimpleStringProperty(title);
        this.vendor = new SimpleStringProperty(vendor);
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

    public String getVendor() {
        return vendor.get();
    }

    public StringProperty vendorProperty() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor.set(vendor);
    }
}
