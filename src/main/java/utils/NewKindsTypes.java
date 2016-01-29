package utils;

/**
 * Created by Igor Klekotnev on 25.01.2016.
 */
public class NewKindsTypes {
    private Integer productKindID;
    private Integer propertyTypeID;

    public NewKindsTypes(Integer prodTypeID, Integer propTypeID) {
        this.productKindID = prodTypeID;
        this.propertyTypeID= propTypeID;
    }

    public Integer getProductKindID() {
        return productKindID;
    }

    public void setProductKindID(Integer productKindID) {
        this.productKindID = productKindID;
    }

    public Integer getPropertyTypeID() {
        return propertyTypeID;
    }

    public void setPropertyTypeID(Integer propertyTypeID) {
        this.propertyTypeID = propertyTypeID;
    }
}
