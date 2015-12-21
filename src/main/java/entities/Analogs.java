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
import javax.persistence.Lob;
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
@Table(name = "analogs")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Analogs.findAll", query = "SELECT a FROM Analogs a"),
    @NamedQuery(name = "Analogs.findById", query = "SELECT a FROM Analogs a WHERE a.id = :id"),
    @NamedQuery(name = "Analogs.findByTitle", query = "SELECT a FROM Analogs a WHERE a.title = :title")})
public class Analogs implements Serializable {
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
    @JoinColumn(name = "vendor", referencedColumnName = "title")
    @ManyToOne
    private Vendors vendor;
    @JoinColumn(name = "prototype_id", referencedColumnName = "id")
    @ManyToOne
    private Products prototypeId;

    public Analogs() {
    }

    public Analogs(Integer id) {
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

    public Vendors getVendor() {
        return vendor;
    }

    public void setVendor(Vendors vendor) {
        this.vendor = vendor;
    }

    public Products getPrototypeId() {
        return prototypeId;
    }

    public void setPrototypeId(Products prototypeId) {
        this.prototypeId = prototypeId;
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
        if (!(object instanceof Analogs)) {
            return false;
        }
        Analogs other = (Analogs) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Analogs[ id=" + id + " ]";
    }
    
}
