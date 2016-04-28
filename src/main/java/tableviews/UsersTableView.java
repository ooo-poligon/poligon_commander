package tableviews;

import javafx.beans.property.*;

/**
 * Created by Igor Klekotnev on 27.04.2016.
 */
public class UsersTableView {
    private final int id;
    private final StringProperty name;
    private final StringProperty email;
    private final StringProperty encrypted_password;
    private final StringProperty group;
    private final StringProperty company;
    private final StringProperty position;
    private final StringProperty phone;

    public UsersTableView(int id, String name, String email) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.encrypted_password = null;
        this.group = null;
        this.company = null;
        this.position = null;
        this.phone = null;
    }

    public UsersTableView(int id, String name, String email, String encrypted_password, String group, String company, String position,
                          String phone) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.encrypted_password = new SimpleStringProperty(encrypted_password);
        this.group = new SimpleStringProperty(group);
        this.company = new SimpleStringProperty(company);
        this.position = new SimpleStringProperty(position);
        this.phone = new SimpleStringProperty(phone);
    }

    public int getId() {
        return id;
    }

    public String getEncrypted_password() {
        return encrypted_password.get();
    }

    public StringProperty encrypted_passwordProperty() {
        return encrypted_password;
    }

    public void setEncrypted_password(String encrypted_password) {
        this.encrypted_password.set(encrypted_password);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
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

    public String getEncryptedPassword() {
        return encrypted_password.get();
    }

    public StringProperty encryptedPasswordProperty() {
        return encrypted_password;
    }

    public void setEncryptedPassword(String encrypted_password) {
        this.encrypted_password.set(encrypted_password);
    }

    public String getGroup() {
        return group.get();
    }

    public StringProperty groupProperty() {
        return group;
    }

    public void setGroup(String group) {
        this.group.set(group);
    }

    public String getCompany() {
        return company.get();
    }

    public StringProperty companyProperty() {
        return company;
    }

    public void setCompany(String company) {
        this.company.set(company);
    }

    public String getPosition() {
        return position.get();
    }

    public StringProperty positionProperty() {
        return position;
    }

    public void setPosition(String position) {
        this.position.set(position);
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
}
