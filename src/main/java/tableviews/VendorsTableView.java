package tableviews;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Igor Klekotnev on 22.12.2015.
 */
public class VendorsTableView {
    private final StringProperty title;
    private final StringProperty address;
    private final DoubleProperty rate;

    public VendorsTableView(String title, String address, Double rate) {
        this.title = new SimpleStringProperty(title);
        this.address = new SimpleStringProperty(address);
        this.rate = new SimpleDoubleProperty(rate);
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

    public String getAddress() {
        return address.get();
    }

    public StringProperty addressProperty() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public double getRate() {
        return rate.get();
    }

    public DoubleProperty rateProperty() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate.set(rate);
    }
}
