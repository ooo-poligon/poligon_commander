package entities;

/**
 * Created by Igor Klekotnev on 26.01.2016.
 */

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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

@Entity
@Table(name = "measures")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Measures.findAll", query = "SELECT m FROM Measures m"),
        @NamedQuery(name = "Measures.findById", query = "SELECT m FROM Measures m WHERE m.id = :id"),
        @NamedQuery(name = "Measures.findByTitle", query = "SELECT m FROM Measures m WHERE m.title = :title"),
        @NamedQuery(name = "Measures.findBySymbolEn", query = "SELECT m FROM Measures m WHERE m.symbolEn = :symbolEn"),
        @NamedQuery(name = "Measures.findBySymbolRu", query = "SELECT m FROM Measures m WHERE m.symbolRu = :symbolRu"),

        @NamedQuery(name = "Measures.findByNumberCode", query = "SELECT m FROM Measures m WHERE m.numberCode = :numberCode"),
        @NamedQuery(name = "Measures.findByLetterCodeEn", query = "SELECT m FROM Measures m WHERE m.letterCodeEn = :letterCodeE"),
        @NamedQuery(name = "Measures.findByLetterCodeRu", query = "SELECT m FROM Measures m WHERE m.letterCodeRu = :letterCodeRu"),

        @NamedQuery(name = "Measures.findByDescription", query = "SELECT m FROM Measures m WHERE m.description = :description")})
public class Measures implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "title")
    private String title;
    @Column(name = "symbol_en")
    private String symbolEn;
    @Column(name = "symbol_ru")
    private String symbolRu;

    @Column(name = "number_code")
    private String numberCode;
    @Column(name = "letter_code_en")
    private String letterCodeEn;
    @Column(name = "letter_code_ru")
    private String letterCodeRu;

    @Column(name = "description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "measureId")
    private Collection<PropertyValues> propertyValuesCollection;

    public Measures() {
    }

    public Measures(Integer id) {
        this.id = id;
    }

    public Measures(Integer id, String title) {
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

    public String getSymbolEn() {
        return symbolEn;
    }

    public void setSymbolEn(String symbolEn) {
        this.symbolEn = symbolEn;
    }

    public String getSymbolRu() {
        return symbolRu;
    }

    public void setSymbolRu(String symbolRu) {
        this.symbolRu = symbolRu;
    }

    public String getNumberCode() {
        return numberCode;
    }

    public void setNumberCode(String numberCode) {
        this.numberCode = numberCode;
    }

    public String getLetterCodeEn() {
        return letterCodeEn;
    }

    public void setLetterCodeEn(String letterCodeEn) {
        this.letterCodeEn = letterCodeEn;
    }

    public String getLetterCodeRu() {
        return letterCodeRu;
    }

    public void setLetterCodeRu(String letterCodeRu) {
        this.letterCodeRu = letterCodeRu;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public Collection<PropertyValues> getPropertyValuesCollection() {
        return propertyValuesCollection;
    }

    public void setPropertyValuesCollection(Collection<PropertyValues> propertyValuesCollection) {
        this.propertyValuesCollection = propertyValuesCollection;
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
        if (!(object instanceof Measures)) {
            return false;
        }
        Measures other = (Measures) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Measures[ id=" + id + " ]";
    }

}

