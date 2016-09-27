package new_items;

/**
 * Created by Igor Klekotnev on 16.03.2016.
 */
public class NewAnalog {

    String title;
    String description;
    String vendor;
    Integer prototypeId;

    public NewAnalog(String title, String description, String vendor, Integer prototypeId) {
        this.title = title;
        this.description = description;
        this.vendor = vendor;
        this.prototypeId = prototypeId;
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

    public Integer getPrototypeId() {
        return prototypeId;
    }

    public void setPrototypeId(Integer prototypeId) {
        this.prototypeId = prototypeId;
    }
}
