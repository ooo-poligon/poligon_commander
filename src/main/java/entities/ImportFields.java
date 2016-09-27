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
@Table(name = "import_fields")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ImportFields.findAll", query = "SELECT i FROM ImportFields i"),
    @NamedQuery(name = "ImportFields.findById", query = "SELECT i FROM ImportFields i WHERE i.id = :id"),
    @NamedQuery(name = "ImportFields.findByTitle", query = "SELECT i FROM ImportFields i WHERE i.title = :title")})
    //@NamedQuery(name = "ImportFields.findByTableName", query = "SELECT i FROM ImportFields i WHERE i.tableName = :tableName")})
    //@NamedQuery(name = "ImportFields.findByField", query = "SELECT i FROM ImportFields i WHERE i.field = :field")})
public class ImportFields implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "title")
    private String title;
    
    @Basic(optional = false)
    @Column(name = "tableName")
    private String tableName;
    
    @Basic(optional = false)
    @Column(name = "field")
    private String field;    

    public ImportFields() {
    }

    public ImportFields(Integer id) {
        this.id = id;
    }

    public ImportFields(Integer id, String title, String tableName, String field) {
        this.id = id;
        this.title = title;
        this.tableName = tableName;
        this.field = field;
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

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
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
        if (!(object instanceof ImportFields)) {
            return false;
        }
        ImportFields other = (ImportFields) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.ImportFields[ id=" + id + " ]";
    }
    
}
