/*
 *
 *
 */
package entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Igor Klekotnev
 */
@Entity
@Table(name = "series_items")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "SeriesItems.findAll", query = "SELECT s FROM SeriesItems s"),
        @NamedQuery(name = "SeriesItems.findById", query = "SELECT s FROM SeriesItems s WHERE s.id = :id"),
        @NamedQuery(name = "SeriesItems.findByTitle", query = "SELECT s FROM SeriesItems s WHERE s.title = :title")})
public class SeriesItems implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id")
    private Integer id;

    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "description")
    private String description;

    @JoinColumn(name = "vendor_id", referencedColumnName = "id")
    @ManyToOne(optional = true)
    private Vendors vendorId;

    @OneToMany(mappedBy = "seriesItemId")
    private Collection<Products> productsCollection;

    public SeriesItems() {
    }

    public SeriesItems(Integer id) {
        this.id = id;
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

    public Vendors getVendorId() {
        return vendorId;
    }

    public void setVendorId(Vendors vendorId) {
        this.vendorId = vendorId;
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
        if (!(object instanceof SeriesItems)) {
            return false;
        }
        SeriesItems other = (SeriesItems) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.SeriesItems[ id=" + id + " ]";
    }



    @XmlTransient
    public Collection<Products> getProductsCollection() {
        return productsCollection;
    }

    public void setProductsCollection(Collection<Products> productsCollection) {
        this.productsCollection = productsCollection;
    }
}

