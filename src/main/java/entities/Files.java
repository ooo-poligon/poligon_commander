/*
 * 
 * 
 */
package entities;

import main.PCGUIController;
import modalwindows.AlertWindow;
import utils.UtilPack;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
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
@Table(name = "files")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Files.findAll", query = "SELECT f FROM Files f"),
    @NamedQuery(name = "Files.findById", query = "SELECT f FROM Files f WHERE f.id = :id"),
    @NamedQuery(name = "Files.findByName", query = "SELECT f FROM Files f WHERE f.name = :name"),
    @NamedQuery(name = "Files.findByPath", query = "SELECT f FROM Files f WHERE f.path = :path"),
    @NamedQuery(name = "Files.findByOwnerId", query = "SELECT f FROM Files f WHERE ownerId = :ownerId"),
    @NamedQuery(name = "Files.findByDescription", query = "SELECT f FROM Files f WHERE f.description = :description")})
public class Files implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "path")
    private String path;
    @Column(name = "description")
    private String description;
    @JoinColumn(name = "file_type_id", referencedColumnName = "id")
    @ManyToOne
    private FileTypes fileTypeId;
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    @ManyToOne
    private Products ownerId;

    public Files() {
    }

    public Files(Integer id) {
        this.id = id;
    }
    
    public Files(String name, String path, String description, FileTypes fileTypeId, Products ownerId) {
        this.name = name;
        this.path = path;
        this.description = description;
        this.fileTypeId = fileTypeId;
        this.ownerId = ownerId;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FileTypes getFileTypeId() {
        return fileTypeId;
    }

    public void setFileTypeId(FileTypes fileTypeId) {
        this.fileTypeId = fileTypeId;
    }

    public Products getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Products ownerId) {
        this.ownerId = ownerId;
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
        if (!(object instanceof Files)) {
            return false;
        }
        Files other = (Files) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "entities.Files[ id=" + id + " ]";
    }
    
}
