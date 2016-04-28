package tableviews;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Igor Klekotnev on 28.04.2016.
 */
public class CompaniesTableView {
    private final BooleanProperty dealer;
    private final StringProperty title;
    private final StringProperty address;
    private final StringProperty phone;
    private final StringProperty email;
    private final StringProperty site;
    private final StringProperty fax;

    public CompaniesTableView()  {
        this(false, null, null, null, null, null, null);
    }

    public CompaniesTableView(Boolean dealer, String title) {
        this.dealer = new SimpleBooleanProperty(dealer);
        this.title = new SimpleStringProperty(title);
        this.address = null;
        this.phone = null;
        this.email = null;
        this.site = null;
        this.fax = null;
    }

    public CompaniesTableView(Boolean dealer, String title, String address, String phone, String email, String site, String fax) {
        this.dealer = new SimpleBooleanProperty(dealer);
        this.title = new SimpleStringProperty(title);
        this.address = new SimpleStringProperty(address);
        this.phone = new SimpleStringProperty(phone);
        this.email = new SimpleStringProperty(email);
        this.site = new SimpleStringProperty(site);
        this.fax = new SimpleStringProperty(fax);
    }

    public boolean getDealer() {
        return dealer.get();
    }

    public BooleanProperty dealerProperty() {
        return dealer;
    }

    public void setDealer(boolean dealer) {
        this.dealer.set(dealer);
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

    public String getPhone() {
        return phone.get();
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getSite() {
        return site.get();
    }

    public StringProperty siteProperty() {
        return site;
    }

    public void setSite(String site) {
        this.site.set(site);
    }

    public String getFax() {
        return fax.get();
    }

    public StringProperty faxProperty() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax.set(fax);
    }
}
