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
@Table(name = "currencies")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Currencies.findAll", query = "SELECT c FROM Currencies c"),
    @NamedQuery(name = "Currencies.findById", query = "SELECT c FROM Currencies c WHERE c.id = :id"),
    @NamedQuery(name = "Currencies.findByTitle", query = "SELECT c FROM Currencies c WHERE c.title = :title")})
public class Currencies implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "title")
    private String title;
    @OneToMany(mappedBy = "currencyId")
    private Collection<Vendors> vendorsCollection;

    public Currencies() {
    }

    public Currencies(Integer id) {
        this.id = id;
    }

    public Currencies(Integer id, String title) {
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
    public Collection<Vendors> getVendorsCollection() {
        return vendorsCollection;
    }

    public void setVendorsCollection(Collection<Vendors> vendorsCollection) {
        this.vendorsCollection = vendorsCollection;
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
        if (!(object instanceof Currencies)) {
            return false;
        }
        Currencies other = (Currencies) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Currencies[ id=" + id + " ]";
    }
    
}
