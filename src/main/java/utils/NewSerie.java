package utils;

/**
 * Created by Igor Klekotnev on 18.03.2016.
 */
public class NewSerie {
    private String title;
    private String description;
    private String vendor;

    public NewSerie(String title, String description, String vendor) {
        this.title = title;
        this.description = description;
        this.vendor = vendor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
}
