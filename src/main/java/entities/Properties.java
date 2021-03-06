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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Igor Klekotnev
 */
@Entity
@Table(name = "properties")
@XmlRootElement

@NamedQueries({
        @NamedQuery(name = "Properties.findAll", query = "SELECT p FROM Properties p"),
        @NamedQuery(name = "Properties.findById", query = "SELECT p FROM Properties p WHERE p.id = :id"),
        @NamedQuery(name = "Properties.findByTitle", query = "SELECT p FROM Properties p WHERE p.title = :title")})
public class Properties implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "order_number")
    private Integer orderNumber;

    @Column(name = "title")
    private String title;

    @Column(name = "optional")
    private String optional;

    @Column(name = "symbol")
    private String symbol;

    @JoinColumn(name = "product_id", referencedColumnName = "id")
    @ManyToOne(optional = true)
    private Products productId;

    @JoinColumn(name = "product_kind_id", referencedColumnName = "id")
    @ManyToOne(optional = true)
    private ProductKinds productKindId;

    @JoinColumn(name = "property_type_id", referencedColumnName = "id")
    @ManyToOne(optional = true)
    private PropertyTypes propertyTypeId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyId")
    private Collection<PropertyValues> propertyValuesCollection;

    @JoinColumn(name = "value_id", referencedColumnName = "id")
    @ManyToOne
    private PropertyValues valueId;

    public Properties() {}

    public Properties(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOptional() {
        return optional;
    }

    public void setOptional(String optional) {
        this.optional = optional;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Products getProductId() {
        return productId;
    }

    public void setProductId(Products productId) {
        this.productId = productId;
    }

    public ProductKinds getProductKindId() {
        return productKindId;
    }

    public void setProductKindId(ProductKinds productKindId) {
        this.productKindId = productKindId;
    }

    public PropertyTypes getPropertyTypeId() {
        return propertyTypeId;
    }

    public void setPropertyTypeId(PropertyTypes propertyTypeId) {
        this.propertyTypeId = propertyTypeId;
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
        if (!(object instanceof Properties)) {
            return false;
        }
        Properties other = (Properties) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Properties[ id=" + id + " ]";
    }

    @XmlTransient
    public Collection<PropertyValues> getPropertyValuesCollection() {
        return propertyValuesCollection;
    }

    public void setPropertyValuesCollection(Collection<PropertyValues> propertyValuesCollection) {
        this.propertyValuesCollection = propertyValuesCollection;
    }

    public PropertyValues getValueId() {
        return valueId;
    }

    public void setValueId(PropertyValues valueId) {
        this.valueId = valueId;
    }

}
