package main;

/**
 * Created by Igor Klekotnev on 14.03.2016.
 */
public class QuantityOfProduct {
    int id, stock, reserved, ordered, minimum, pieces_per_pack, product_id;
    String product_title;

    public QuantityOfProduct(int id, int stock, int reserved, int ordered, int minimum, int pieces_per_pack, int product_id, String product_title) {
        this.id = id;
        this.stock = stock;
        this.reserved = reserved;
        this.ordered = ordered;
        this.minimum = minimum;
        this.pieces_per_pack = pieces_per_pack;
        this.product_id = product_id;
        this.product_title = product_title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public int getOrdered() {
        return ordered;
    }

    public void setOrdered(int ordered) {
        this.ordered = ordered;
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public int getPieces_per_pack() {
        return pieces_per_pack;
    }

    public void setPieces_per_pack(int pieces_per_pack) {
        this.pieces_per_pack = pieces_per_pack;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getProduct_title() {
        return product_title;
    }

    public void setProduct_title(String product_title) {
        this.product_title = product_title;
    }
}
