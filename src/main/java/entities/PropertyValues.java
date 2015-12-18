/*
 * 
 * 
 */
package entities;

import java.io.Serializable;
import java.util.Collection;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author kataev
 */
@Entity
@Table(name = "property_values")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PropertyValues.findAll", query = "SELECT p FROM PropertyValues p"),
    @NamedQuery(name = "PropertyValues.findById", query = "SELECT p FROM PropertyValues p WHERE p.id = :id"),
    @NamedQuery(name = "PropertyValues.findByCondition", query = "SELECT p FROM PropertyValues p WHERE p.condition = :condition"),
    @NamedQuery(name = "PropertyValues.findByValue", query = "SELECT p FROM PropertyValues p WHERE p.value = :value")})
public class PropertyValues implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "condition")
    private String condition;
    @Column(name = "value")
    private String value;
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Products productId;
    @JoinColumn(name = "property_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Properties propertyId;
    @OneToMany(mappedBy = "valueId")
    private Collection<Properties> propertiesCollection;

    public PropertyValues() {
    }

    public PropertyValues(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Products getProductId() {
        return productId;
    }

    public void setProductId(Products productId) {
        this.productId = productId;
    }

    public Properties getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Properties propertyId) {
        this.propertyId = propertyId;
    }

    @XmlTransient
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
        if (!(object instanceof PropertyValues)) {
            return false;
        }
        PropertyValues other = (PropertyValues) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.PropertyValues[ id=" + id + " ]";
    }
    
}
