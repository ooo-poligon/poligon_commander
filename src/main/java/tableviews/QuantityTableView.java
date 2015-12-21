/*
 * 
 * 
 */
package tableviews;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author Igor Klekotnev
 */
public class QuantityTableView {

    private final IntegerProperty stock;
    private final IntegerProperty reserved;
    private final IntegerProperty ordered;
    private final IntegerProperty minimum;
    private final IntegerProperty pieces_per_pack;

    public QuantityTableView() {
        this(null, null, null, null, null);
    }

    public QuantityTableView(Integer stock, Integer reserved, Integer ordered, Integer minimum, Integer pieces_per_pack) {
        this.stock = new SimpleIntegerProperty(stock);
        this.reserved = new SimpleIntegerProperty(reserved);
        this.ordered = new SimpleIntegerProperty(ordered);
        this.minimum = new SimpleIntegerProperty(minimum);
        this.pieces_per_pack = new SimpleIntegerProperty(pieces_per_pack);
    }

    public Integer getStock() {
        return stock.get();
    }

    public void setStock(Integer stock) {
        this.stock.set(stock);
    }

    public IntegerProperty stockProperty() {
        return stock;
    }

    public Integer getReserved() {
        return reserved.get();
    }

    public void setReserved(Integer reserved) {
        this.reserved.set(reserved);
    }

    public IntegerProperty reservedProperty() {
        return reserved;
    }

    public Integer getOrdered() {
        return ordered.get();
    }

    public void setOrdered(Integer ordered) {
        this.ordered.set(ordered);
    }

    public IntegerProperty orderedProperty() {
        return ordered;
    }

    public Integer getMinimum() {
        return minimum.get();
    }

    public void setMinimum(Integer minimum) {
        this.minimum.set(minimum);
    }

    public IntegerProperty minimumProperty() {
        return minimum;
    }

    public Integer getPiecesPerPack() {
        return pieces_per_pack.get();
    }

    public void setPiecesPerPack(Integer pieces_per_pack) {
        this.pieces_per_pack.set(pieces_per_pack);
    }

    public IntegerProperty pieces_per_packProperty() {
        return pieces_per_pack;
    }

    private ObservableList<QuantityTableView> getQuantities(String productName) {
        ObservableList<QuantityTableView> data = FXCollections.observableArrayList();
        return data;
    }

    public void build(String selectedProduct, TableView<QuantityTableView> quantitiesTable,
            TableColumn<QuantityTableView, String> quantityStock,
            TableColumn<QuantityTableView, String> quantityReserved,
            TableColumn<QuantityTableView, String> quantityOrdered,
            TableColumn<QuantityTableView, String> quantityMinimum,
            TableColumn<QuantityTableView, String> quantityPiecesPerPack) {
        ObservableList<QuantityTableView> quantities = getQuantities(selectedProduct);
        quantityStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        quantityReserved.setCellValueFactory(new PropertyValueFactory<>("reserved"));
        quantityOrdered.setCellValueFactory(new PropertyValueFactory<>("ordered"));
        quantityMinimum.setCellValueFactory(new PropertyValueFactory<>("minimum"));
        quantityPiecesPerPack.setCellValueFactory(new PropertyValueFactory<>("pieces_per_pack"));
        quantitiesTable.setItems(quantities);
    }
}
