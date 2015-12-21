/*
 * 
 * 
 */
package tableviews;

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
public class DatasheetTableView {

    private final StringProperty name;
    
    public DatasheetTableView() {
        this(null);
    }    

    public DatasheetTableView(String name) {
        this.name = new SimpleStringProperty(name);
    }    
  
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }
    public void build(String selectedProduct, TableColumn<DatasheetTableView, String> datasheetFileName, TableView<DatasheetTableView> datasheetFileTable) {
        ObservableList<DatasheetTableView> data = FXCollections.observableArrayList();
        datasheetFileName.setCellValueFactory(new PropertyValueFactory<>("name"));       
        datasheetFileTable.setItems(data);         
    }  
}

