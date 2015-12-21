/*
 * 
 * 
 */
package tableviews;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author kataev
 */

public class DeliveryTimeTableView {
    public void build(String selectedProduct, TableColumn<ProductsTableView, String> deliveryTime, TableView<ProductsTableView> deliveryTable ) {
        ObservableList<ProductsTableView> data = FXCollections.observableArrayList();        
        deliveryTime.setCellValueFactory(new PropertyValueFactory<>("delivery_time"));
        deliveryTable.setItems(data);         
    }    
}
