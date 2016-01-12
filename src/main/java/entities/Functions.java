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
import javax.persistence.Lob;
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
@Table(name = "functions")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Functions.findAll", query = "SELECT f FROM Functions f"),
        @NamedQuery(name = "Functions.findById", query = "SELECT f FROM Functions f WHERE f.id = :id"),
        @NamedQuery(name = "Functions.findByTitle", query = "SELECT f FROM Functions f WHERE f.title = :title"),
        @NamedQuery(name = "Functions.findByPictureName", query = "SELECT f FROM Functions f WHERE f.pictureName = :pictureName"),
        @NamedQuery(name = "Functions.findByPicturePath", query = "SELECT f FROM Functions f WHERE f.picturePath = :picturePath")})
public class Functions implements Serializable {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "functionId")
    private Collection<ProductsFunctions> productsFunctionsCollection;
    @Column(name = "symbol")
    private String symbol;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "title")
    private String title;
    @Lob
    @Column(name = "description")
    private String description;
    @Column(name = "picture_name")
    private String pictureName;
    @Column(name = "picture_path")
    private String picturePath;
    @JoinColumn(name = "product_kind_id", referencedColumnName = "id")
    @ManyToOne
    private ProductKinds productKindId;

    public Functions() {
    }

    public Functions(Integer id) {
        this.id = id;
    }

    public Functions(Integer id, String title) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public ProductKinds getProductKindId() {
        return productKindId;
    }

    public void setProductKindId(ProductKinds productKindId) {
        this.productKindId = productKindId;
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
        if (!(object instanceof Functions)) {
            return false;
        }
        Functions other = (Functions) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Functions[ id=" + id + " ]";
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @XmlTransient
    public Collection<ProductsFunctions> getProductsFunctionsCollection() {
        return productsFunctionsCollection;
    }

    public void setProductsFunctionsCollection(Collection<ProductsFunctions> productsFunctionsCollection) {
        this.productsFunctionsCollection = productsFunctionsCollection;
    }

}
