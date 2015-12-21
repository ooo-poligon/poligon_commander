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
@Table(name = "property_types")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PropertyTypes.findAll", query = "SELECT p FROM PropertyTypes p"),
    @NamedQuery(name = "PropertyTypes.findById", query = "SELECT p FROM PropertyTypes p WHERE p.id = :id"),
    @NamedQuery(name = "PropertyTypes.findByTitle", query = "SELECT p FROM PropertyTypes p WHERE p.title = :title"),
    @NamedQuery(name = "PropertyTypes.findByParent", query = "SELECT p FROM PropertyTypes p WHERE p.parent = :parent")})
public class PropertyTypes implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "title")
    private String title;
    @Column(name = "parent")
    private Integer parent;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyTypeId")
    private Collection<Properties> propertiesCollection;

    public PropertyTypes() {
    }

    public PropertyTypes(Integer id) {
        this.id = id;
    }

    public PropertyTypes(Integer id, String title) {
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

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
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
        if (!(object instanceof PropertyTypes)) {
            return false;
        }
        PropertyTypes other = (PropertyTypes) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.PropertyTypes[ id=" + id + " ]";
    }
    
}
