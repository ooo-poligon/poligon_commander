/*
 *
 *
 */
package entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author klekotnev
 */
@Entity
@Table(name = "users")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u"),
        @NamedQuery(name = "Users.findById", query = "SELECT u FROM Users u WHERE u.id = :id"),
        @NamedQuery(name = "Users.findByName", query = "SELECT u FROM Users u WHERE u.name = :name"),
        @NamedQuery(name = "Users.findByPhone", query = "SELECT u FROM Users u WHERE u.phone = :phone"),
        @NamedQuery(name = "Users.findByEmail", query = "SELECT u FROM Users u WHERE u.email = :email"),
        @NamedQuery(name = "Users.findByEncryptedPassword", query = "SELECT u FROM Users u WHERE u.encryptedPassword = :encryptedPassword"),
        @NamedQuery(name = "Users.findByRememberCreatedAt", query = "SELECT u FROM Users u WHERE u.rememberCreatedAt = :rememberCreatedAt"),
        @NamedQuery(name = "Users.findBySignInCount", query = "SELECT u FROM Users u WHERE u.signInCount = :signInCount"),
        @NamedQuery(name = "Users.findByCurrentSignInAt", query = "SELECT u FROM Users u WHERE u.currentSignInAt = :currentSignInAt"),
        @NamedQuery(name = "Users.findByLastSignInAt", query = "SELECT u FROM Users u WHERE u.lastSignInAt = :lastSignInAt"),
        @NamedQuery(name = "Users.findByCurrentSignInIp", query = "SELECT u FROM Users u WHERE u.currentSignInIp = :currentSignInIp"),
        @NamedQuery(name = "Users.findByLastSignInIp", query = "SELECT u FROM Users u WHERE u.lastSignInIp = :lastSignInIp"),
        @NamedQuery(name = "Users.findByCreatedAt", query = "SELECT u FROM Users u WHERE u.createdAt = :createdAt"),
        @NamedQuery(name = "Users.findByUpdatedAt", query = "SELECT u FROM Users u WHERE u.updatedAt = :updatedAt"),
        @NamedQuery(name = "Users.findByFax", query = "SELECT u FROM Users u WHERE u.fax = :fax"),
        @NamedQuery(name = "Users.findByPosition", query = "SELECT u FROM Users u WHERE u.position = :position")})
public class Users implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "phone")
    private String phone;
    @Basic(optional = false)
    @Column(name = "email")
    private String email;
    @Basic(optional = false)
    @Column(name = "encrypted_password")
    private String encryptedPassword;
    @Column(name = "remember_created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date rememberCreatedAt;
    @Basic(optional = false)
    @Column(name = "sign_in_count")
    private int signInCount;
    @Column(name = "current_sign_in_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date currentSignInAt;
    @Column(name = "last_sign_in_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastSignInAt;
    @Column(name = "current_sign_in_ip")
    private String currentSignInIp;
    @Column(name = "last_sign_in_ip")
    private String lastSignInIp;
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
    @Column(name = "position")
    private String position;
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    @ManyToOne
    private Companies companyId;
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    @ManyToOne
    private Groups groupId;

    public Users() {
    }

    public Users(Integer id) {
        this.id = id;
    }

    public Users(Integer id, String email, String encryptedPassword, int signInCount, Date createdAt, Date updatedAt) {
        this.id = id;
        this.email = email;
        this.encryptedPassword = encryptedPassword;
        this.signInCount = signInCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public Date getRememberCreatedAt() {
        return rememberCreatedAt;
    }

    public void setRememberCreatedAt(Date rememberCreatedAt) {
        this.rememberCreatedAt = rememberCreatedAt;
    }

    public int getSignInCount() {
        return signInCount;
    }

    public void setSignInCount(int signInCount) {
        this.signInCount = signInCount;
    }

    public Date getCurrentSignInAt() {
        return currentSignInAt;
    }

    public void setCurrentSignInAt(Date currentSignInAt) {
        this.currentSignInAt = currentSignInAt;
    }

    public Date getLastSignInAt() {
        return lastSignInAt;
    }

    public void setLastSignInAt(Date lastSignInAt) {
        this.lastSignInAt = lastSignInAt;
    }

    public String getCurrentSignInIp() {
        return currentSignInIp;
    }

    public void setCurrentSignInIp(String currentSignInIp) {
        this.currentSignInIp = currentSignInIp;
    }

    public String getLastSignInIp() {
        return lastSignInIp;
    }

    public void setLastSignInIp(String lastSignInIp) {
        this.lastSignInIp = lastSignInIp;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Companies getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Companies companyId) {
        this.companyId = companyId;
    }

    public Groups getGroupId() {
        return groupId;
    }

    public void setGroupId(Groups groupId) {
        this.groupId = groupId;
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
        if (!(object instanceof Users)) {
            return false;
        }
        Users other = (Users) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "main.java.entities.Users[ id=" + id + " ]";
    }

}
