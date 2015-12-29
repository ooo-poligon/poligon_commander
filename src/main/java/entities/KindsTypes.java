package entities;

/*
 *
 *
 */

        import entities.ProductKinds;
        import entities.PropertyTypes;
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
@Table(name = "kinds_types")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "KindsTypes.findAll", query = "SELECT k FROM KindsTypes k"),
        @NamedQuery(name = "KindsTypes.findById", query = "SELECT k FROM KindsTypes k WHERE k.id = :id")})
public class KindsTypes implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "product_kind_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ProductKinds productKindId;
    @JoinColumn(name = "property_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private PropertyTypes propertyTypeId;

    public KindsTypes() {
    }

    public KindsTypes(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
        if (!(object instanceof KindsTypes)) {
            return false;
        }
        KindsTypes other = (KindsTypes) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "agreements.KindsTypes[ id=" + id + " ]";
    }

}

