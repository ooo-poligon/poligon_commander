package new_items;

/**
 * Created by Igor Klekotnev on 25.01.2016.
 */
public class NewPropertyValue {

    private String cond;
    private String value;
    private Integer propertyID;
    private Integer productID;
    private Integer measureID;

    public NewPropertyValue(Integer propertyID, Integer productID, Integer measureID) {
        this.cond = "";
        this.value = "";
        this.propertyID = propertyID;
        this.productID = productID;
        this.measureID = measureID;
    }

    public NewPropertyValue(String cond, String value, Integer propertyID, Integer productID, Integer measureID) {
        this.cond = cond;
        this.value = value;
        this.propertyID = propertyID;
        this.productID = productID;
        this.measureID = measureID;
    }

    public String getCond() {
        return cond;
    }

    public void setCond(String cond) {
        this.cond = cond;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getPropertyID() {
        return propertyID;
    }

    public void setPropertyID(Integer propertyID) {
        this.propertyID = propertyID;
    }

    public Integer getProductID() {
        return productID;
    }

    public void setProductID(Integer productID) {
        this.productID = productID;
    }

    public Integer getMeasureID() {
        return measureID;
    }

    public void setMeasureID(Integer measureID) {
        this.measureID = measureID;
    }
}
