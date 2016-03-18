package entities;

/**
 * Created by Igor Klekotnev on 27.01.2016.
 */


import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "products_analogs")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "ProductsAnalogs.findAll", query = "SELECT p FROM ProductsAnalogs p"),
        @NamedQuery(name = "ProductsAnalogs.findById", query = "SELECT p FROM ProductsAnalogs p WHERE p.id = :id"),
        @NamedQuery(name = "ProductsAnalogs.findByProductId", query = "SELECT p FROM ProductsAnalogs p WHERE p.productId = :productId"),
        @NamedQuery(name = "ProductsAnalogs.findByAnalogsId", query = "SELECT p FROM ProductsAnalogs p WHERE p.analogId = :analogId")})
public class ProductsAnalogs implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "product_id")
    private int productId;
    @Basic(optional = false)
    @Column(name = "analog_id")
    private int analogId;

    public ProductsAnalogs() {
    }

    public ProductsAnalogs(Integer id) {
        this.id = id;
    }

    public ProductsAnalogs(Integer id, int productId, int analogId) {
        this.id = id;
        this.productId = productId;
        this.analogId = analogId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getAnalogId() {
        return analogId;
    }

    public void setAnalogId(int analogId) {
        this.analogId = analogId;
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
        if (!(object instanceof ProductsAnalogs)) {
            return false;
        }
        ProductsAnalogs other = (ProductsAnalogs) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() { return "entities.ProductsAnalogs[ id=" + id + " ]"; }
}


