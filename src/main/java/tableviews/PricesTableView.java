/*
 * 
 * 
 */
package tableviews;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author Igor Klekotnev
 */
public class PricesTableView {   

    private final StringProperty type;
    private final DoubleProperty price;
    private final DoubleProperty priceR;
    
    /*
    private Double basePrice;
    private final Double course = 74.5; 
    private final Map<String, Double> allPrices = new HashMap<>();
    */
    
    public PricesTableView() {
        this(null, null, null);
    }    

    public PricesTableView(String type, Double price, Double priceR) {
        this.type  = new SimpleStringProperty(type);        
        this.price = new SimpleDoubleProperty(price);
        this.priceR = new SimpleDoubleProperty(priceR);
    }

    public Double getPrice() {
        return price.get();
    }

    public void setPrice(Double price) {
        this.price.set(price);
    }

    public DoubleProperty priceProperty() {
        return price;
    } 
    
    public Double getPriceR() {
        return priceR.get();
    }

    public void setPriceR(Double priceR) {
        this.priceR.set(priceR);
    }

    public DoubleProperty priceRProperty() {
        return priceR;
    }    
  
    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public StringProperty typeProperty() {
        return type;
    }

}
