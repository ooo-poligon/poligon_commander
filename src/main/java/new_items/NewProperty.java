package new_items;

/**
 * Created by Igor Klekotnev on 11.01.2016.
 */
public class NewProperty {

    private String title;
    private Integer propertyTypeID;

    public NewProperty() {
        this.title = "";
        this.propertyTypeID = 0;
    }

    public NewProperty(String title, Integer propertyTypeID) {
        this.title = title;
        this.propertyTypeID = propertyTypeID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPropertyTypeID() {
        return propertyTypeID;
    }

    public void setPropertyTypeID(Integer propertyTypeID) {
        this.propertyTypeID = propertyTypeID;
    }

}
