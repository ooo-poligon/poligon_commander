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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kataev
 */
@Entity
@Table(name = "settings")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Settings.findAll", query = "SELECT s FROM Settings s"),
    @NamedQuery(name = "Settings.findById", query = "SELECT s FROM Settings s WHERE s.id = :id"),
    @NamedQuery(name = "Settings.findByTitle", query = "SELECT s FROM Settings s WHERE s.title = :title"),
    @NamedQuery(name = "Settings.findByKind", query = "SELECT s FROM Settings s WHERE s.kind = :kind"),
    @NamedQuery(name = "Settings.findByIntValue", query = "SELECT s FROM Settings s WHERE s.intValue = :intValue"),
    @NamedQuery(name = "Settings.findByTextValue", query = "SELECT s FROM Settings s WHERE s.textValue = :textValue")})
public class Settings implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "title")
    private String title;
    @Basic(optional = false)
    @Column(name = "kind")
    private String kind;
    @Column(name = "int_value")
    private Integer intValue;
    @Column(name = "text_value")
    private String textValue;

    public Settings() {
    }

    public Settings(Integer id) {
        this.id = id;
    }

    public Settings(Integer id, String title, String kind) {
        this.id = id;
        this.title = title;
        this.kind = kind;
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

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
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
        if (!(object instanceof Settings)) {
            return false;
        }
        Settings other = (Settings) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Settings[ id=" + id + " ]";
    }
    
}
