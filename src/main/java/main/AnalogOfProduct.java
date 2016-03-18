package main;

/**
 * Created by Igor Klekotnev on 14.03.2016.
 */
public class AnalogOfProduct {
    int id, prototype_id;
    String description, title, vendor;

    public AnalogOfProduct(int id, int prototype_id, String description, String title, String vendor) {
        this.id = id;
        this.prototype_id = prototype_id;
        this.description = description;
        this.title = title;
        this.vendor = vendor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrototype_id() {
        return prototype_id;
    }

    public void setPrototype_id(int prototype_id) {
        this.prototype_id = prototype_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
}
