/*
 *
 *
 */
package entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author klekotnev
 */
@Entity
@Table(name = "companies")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Companies.findAll", query = "SELECT c FROM Companies c"),
        @NamedQuery(name = "Companies.findById", query = "SELECT c FROM Companies c WHERE c.id = :id"),
        @NamedQuery(name = "Companies.findByTitle", query = "SELECT c FROM Companies c WHERE c.title = :title"),
        @NamedQuery(name = "Companies.findByAddress", query = "SELECT c FROM Companies c WHERE c.address = :address"),
        @NamedQuery(name = "Companies.findByPhone", query = "SELECT c FROM Companies c WHERE c.phone = :phone"),
        @NamedQuery(name = "Companies.findByEmail", query = "SELECT c FROM Companies c WHERE c.email = :email"),
        @NamedQuery(name = "Companies.findBySite", query = "SELECT c FROM Companies c WHERE c.site = :site"),
        @NamedQuery(name = "Companies.findByDealer", query = "SELECT c FROM Companies c WHERE c.dealer = :dealer"),
        @NamedQuery(name = "Companies.findByCreatedAt", query = "SELECT c FROM Companies c WHERE c.createdAt = :createdAt"),
        @NamedQuery(name = "Companies.findByUpdatedAt", query = "SELECT c FROM Companies c WHERE c.updatedAt = :updatedAt"),
        @NamedQuery(name = "Companies.findByFax", query = "SELECT c FROM Companies c WHERE c.fax = :fax")})
public class Companies implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "title")
    private String title;
    @Column(name = "address")
    private String address;
    @Column(name = "phone")
    private String phone;
    @Column(name = "email")
    private String email;
    @Column(name = "site")
    private String site;
    @Column(name = "dealer")
    private Boolean dealer;
    @Basic(optional = false)
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Basic(optional = false)
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Column(name = "fax")
    private String fax;
    @OneToMany(mappedBy = "companyId")
    private Collection<Users> usersCollection;

    public Companies() {
    }

    public Companies(Integer id) {
        this.id = id;
    }

    public Companies(Integer id, Date createdAt, Date updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Boolean getDealer() {
        return dealer;
    }

    public void setDealer(Boolean dealer) {
        this.dealer = dealer;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    @XmlTransient
    public Collection<Users> getUsersCollection() {
        return usersCollection;
    }

    public void setUsersCollection(Collection<Users> usersCollection) {
        this.usersCollection = usersCollection;
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
        if (!(object instanceof Companies)) {
            return false;
        }
        Companies other = (Companies) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "main.java.entities.Companies[ id=" + id + " ]";
    }

}
