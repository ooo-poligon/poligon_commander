package utils;

/**
 * Created by Igor Klekotnev on 27.01.2016.
 */
public class NewFunctionsProducts {
    private Integer functionID;
    private Integer productID;

    public NewFunctionsProducts(Integer functionID, Integer productID) {
        this.functionID = functionID;
        this.productID  = productID;
    }

    public Integer getFunctionID() {
        return functionID;
    }

    public void setFunctionID(Integer functionID) {
        this.functionID = functionID;
    }

    public Integer getProductID() {
        return productID;
    }

    public void setProductID(Integer productID) {
        this.productID = productID;
    }
}
