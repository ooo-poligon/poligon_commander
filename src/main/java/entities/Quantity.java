/*
 * 
 * 
 */
package entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kataev
 */
@Entity
@Table(name = "quantity")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Quantity.findAll", query = "SELECT q FROM Quantity q"),
    @NamedQuery(name = "Quantity.findById", query = "SELECT q FROM Quantity q WHERE q.id = :id"),
    @NamedQuery(name = "Quantity.findByStock", query = "SELECT q FROM Quantity q WHERE q.stock = :stock"),
    @NamedQuery(name = "Quantity.findByReserved", query = "SELECT q FROM Quantity q WHERE q.reserved = :reserved"),
    @NamedQuery(name = "Quantity.findByOrdered", query = "SELECT q FROM Quantity q WHERE q.ordered = :ordered"),
    @NamedQuery(name = "Quantity.findByMinimum", query = "SELECT q FROM Quantity q WHERE q.minimum = :minimum"),
    @NamedQuery(name = "Quantity.findByPiecesPerPack", query = "SELECT q FROM Quantity q WHERE q.piecesPerPack = :piecesPerPack")})
public class Quantity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "stock")
    private int stock;
    @Basic(optional = false)
    @Column(name = "reserved")
    private int reserved;
    @Basic(optional = false)
    @Column(name = "ordered")
    private int ordered;
    @Basic(optional = false)
    @Column(name = "minimum")
    private int minimum;
    @Basic(optional = false)
    @Column(name = "pieces_per_pack")
    private int piecesPerPack;
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    @ManyToOne
    private Products productId;

    public Quantity() {
    }

    public Quantity(Integer id) {
        this.id = id;
    }

    public Quantity(Integer id, int stock, int reserved, int ordered, int minimum, int piecesPerPack) {
        this.id = id;
        this.stock = stock;
        this.reserved = reserved;
        this.ordered = ordered;
        this.minimum = minimum;
        this.piecesPerPack = piecesPerPack;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public int getPiecesPerPack() {
        return piecesPerPack;
    }

    public void setPiecesPerPack(int piecesPerPack) {
        this.piecesPerPack = piecesPerPack;
    }

    public Products getProductId() {
        return productId;
    }

    public void setProductId(Products productId) {
        this.productId = productId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Quantity)) {
            return false;
        }
        Quantity other = (Quantity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Quantity[ id=" + id + " ]";
    }
    
}
