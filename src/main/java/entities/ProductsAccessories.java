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
@Table(name = "products_accessories")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "ProductsAccessories.findAll", query = "SELECT p FROM ProductsAccessories p"),
        @NamedQuery(name = "ProductsAccessories.findById", query = "SELECT p FROM ProductsAccessories p WHERE p.id = :id"),
        @NamedQuery(name = "ProductsAccessories.findByProductId", query = "SELECT p FROM ProductsAccessories p WHERE p.productId = :productId"),
        @NamedQuery(name = "ProductsAccessories.findByAccessoryId", query = "SELECT p FROM ProductsAccessories p WHERE p.accessoryId = :accessoryId")})
public class ProductsAccessories implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;
    @Basic(optional = false)
    @Column(name = "product_id")
    private int productId;
    @Basic(optional = false)
    @Column(name = "accessory_id")
    private int accessoryId;

    public ProductsAccessories() {
    }

    public ProductsAccessories(Integer id) {
        this.id = id;
    }

    public ProductsAccessories(Integer id, int productId, int accessoryId) {
        this.id = id;
        this.productId = productId;
        this.accessoryId = accessoryId;
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

    public int getAccessoryId() {
        return accessoryId;
    }

    public void setAccessoryId(int accessoryId) {
        this.accessoryId = accessoryId;
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
        if (!(object instanceof ProductsAccessories)) {
            return false;
        }
        ProductsAccessories other = (ProductsAccessories) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.ProductsAccessories[ id=" + id + " ]";
    }

}

