package entities;

/**
 * Created by Igor Klekotnev on 29.01.2016.
 */
/*
 *
 *
 */

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Igor Klekotnev
 */
@Entity
@Table(name = "reviews")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Reviews.findAll", query = "SELECT r FROM Reviews r"),
        @NamedQuery(name = "Reviews.findById", query = "SELECT r FROM Reviews r WHERE r.id = :id"),
        @NamedQuery(name = "Reviews.findByCreatedAt", query = "SELECT r FROM Reviews r WHERE r.createdAt = :createdAt"),
        @NamedQuery(name = "Reviews.findByUpdatedAt", query = "SELECT r FROM Reviews r WHERE r.updatedAt = :updatedAt"),
        @NamedQuery(name = "Reviews.findByTitle", query = "SELECT r FROM Reviews r WHERE r.title = :title")})
public class Reviews implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Basic(optional = false)
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Column(name = "title")
    private String title;
    @Lob
    @Column(name = "content")
    private String content;

    public Reviews() {
    }

    public Reviews(Integer id) {
        this.id = id;
    }

    public Reviews(Integer id, Date createdAt, Date updatedAt) {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
        if (!(object instanceof Reviews)) {
            return false;
        }
        Reviews other = (Reviews) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Reviews[ id=" + id + " ]";
    }

}

