
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
@Table(name = "products_functions")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "ProductsFunctions.findAll", query = "SELECT p FROM ProductsFunctions p"),
        @NamedQuery(name = "ProductsFunctions.findById", query = "SELECT p FROM ProductsFunctions p WHERE p.id = :id")})
public class ProductsFunctions implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Products productId;
    @JoinColumn(name = "function_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Functions functionId;

    public ProductsFunctions() {
    }

    public ProductsFunctions(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Products getProductId() {
        return productId;
    }

    public void setProductId(Products productId) {
        this.productId = productId;
    }

    public Functions getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Functions functionId) {
        this.functionId = functionId;
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
        if (!(object instanceof ProductsFunctions)) {
            return false;
        }
        ProductsFunctions other = (ProductsFunctions) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.ProductsFunctions[ id=" + id + " ]";
    }

}

