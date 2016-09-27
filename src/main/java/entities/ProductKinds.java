/*
 * 
 * 
 */
package entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author kataev
 */
@Entity
@Table(name = "product_kinds")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "ProductKinds.findAll", query = "SELECT p FROM ProductKinds p"),
        @NamedQuery(name = "ProductKinds.findById", query = "SELECT p FROM ProductKinds p WHERE p.id = :id"),
        @NamedQuery(name = "ProductKinds.findByTitle", query = "SELECT p FROM ProductKinds p WHERE p.title = :title")})
public class ProductKinds implements Serializable {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productKindId")
    private Collection<ProductKindsPropertyTypes> productKindsPropertyTypesCollection;
    @OneToMany(mappedBy = "productKindId")
    private Collection<Functions> functionsCollection;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "title")
    private String title;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productKindId")
    private Collection<Products> productsCollection;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productKindId")
    private Collection<Properties> propertiesCollection;

    public ProductKinds() {
    }

    public ProductKinds(Integer id) {
        this.id = id;
    }

    public ProductKinds(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlTransient
    public Collection<Products> getProductsCollection() {
        return productsCollection;
    }

    public void setProductsCollection(Collection<Products> productsCollection) {
        this.productsCollection = productsCollection;
    }

    public Collection<Properties> getPropertiesCollection() {
        return propertiesCollection;
    }

    public void setPropertiesCollection(Collection<Properties> propertiesCollection) {
        this.propertiesCollection = propertiesCollection;
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
        if (!(object instanceof ProductKinds)) {
            return false;
        }
        ProductKinds other = (ProductKinds) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.ProductKinds[ id=" + id + " ]";
    }

    @XmlTransient
    public Collection<Functions> getFunctionsCollection() {
        return functionsCollection;
    }

    public void setFunctionsCollection(Collection<Functions> functionsCollection) {
        this.functionsCollection = functionsCollection;
    }

    @XmlTransient
    public Collection<ProductKindsPropertyTypes> getProductKindsPropertyTypesCollection() {
        return productKindsPropertyTypesCollection;
    }

    public void setProductKindsPropertyTypesCollection(Collection<ProductKindsPropertyTypes> productKindsPropertyTypesCollection) {
        this.productKindsPropertyTypesCollection = productKindsPropertyTypesCollection;
    }

}
