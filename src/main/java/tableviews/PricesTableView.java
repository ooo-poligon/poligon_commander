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
    
    private Double basePrice;
    private final Double course = 74.5; 
    private final Map<String, Double> allPrices = new HashMap<>();    
    
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
    private void getAllPrices() {        
    }
    
    public void build(String selectedProduct,
            TableView<PricesTableView> pricesTable,
            TableColumn<PricesTableView, String> priceType,
            TableColumn<PricesTableView, Double> priceValue,
            TableColumn<PricesTableView, Double> priceValueRub) {

        getAllPrices();
        allPrices.entrySet().stream().filter((entry) -> (entry.getKey().equals(selectedProduct))).map((entry) -> {
            return entry;
        }).forEach((entry) -> {
            basePrice = entry.getValue();
        });       

        ObservableList<PricesTableView> data = FXCollections.observableArrayList(); 
        String[] priceTypes = {
                "Розничная",
                "Мелкий опт",
                "Оптовая",
                "Диллер1",
                "Диллер2",
                "Диллер3",
                "Базовая",
                "Входная"
        };
        
        for (String ptype : priceTypes) { 
            try {
               
            switch (ptype) {
                case "Розничная":
                    data.add(new PricesTableView(ptype, ((new BigDecimal(basePrice * 1.5)).setScale(2, RoundingMode.UP)).doubleValue(),
                                                  ((new BigDecimal(basePrice * 1.5 * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                    break;
                case "Мелкий опт":
                    data.add(new PricesTableView(ptype, ((new BigDecimal(basePrice * 1.4)).setScale(2, RoundingMode.UP)).doubleValue(),
                                                  ((new BigDecimal(basePrice * 1.4 * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                    break; 
                case "Оптовая":
                    data.add(new PricesTableView(ptype, ((new BigDecimal(basePrice * 1.3)).setScale(2, RoundingMode.UP)).doubleValue(),
                                                  ((new BigDecimal(basePrice * 1.3 * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                    break;  
                case "Диллер1":
                    data.add(new PricesTableView(ptype, ((new BigDecimal(basePrice * 1.2)).setScale(2, RoundingMode.UP)).doubleValue(),
                                                  ((new BigDecimal(basePrice * 1.2 * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                    break; 
                case "Диллер2":
                    data.add(new PricesTableView(ptype, ((new BigDecimal(basePrice * 1.1)).setScale(2, RoundingMode.UP)).doubleValue(),
                                                  ((new BigDecimal(basePrice * 1.1 * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                    break;  
                case "Диллер3":
                    data.add(new PricesTableView(ptype, ((new BigDecimal(basePrice * 1.05)).setScale(2, RoundingMode.UP)).doubleValue(),
                                                  ((new BigDecimal(basePrice * 1.05 * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                    break;                     
                case "Базовая":
                    data.add(new PricesTableView(ptype, ((new BigDecimal(basePrice * 1.0)).setScale(2, RoundingMode.UP)).doubleValue(),
                                                  ((new BigDecimal(basePrice * 1.0 * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                    break;     
                case "Входная":
                    data.add(new PricesTableView(ptype, ((new BigDecimal(basePrice * 0.85)).setScale(2, RoundingMode.UP)).doubleValue(),
                                                  ((new BigDecimal(basePrice * 0.85 * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                    break;                     
                default:
                    break;
            }
            } catch (NullPointerException nex) {
                System.out.println("nulled pricesTable!!!");
            }
        }          
               
        priceType.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceValue.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceValueRub.setCellValueFactory(new PropertyValueFactory<>("priceR"));
        pricesTable.setItems(data);               
    }    
}
