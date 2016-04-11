/*
 * 
 * 
 */
package entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author kataev
 */
@Entity
@Table(name = "products")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Products.findAll", query = "SELECT p FROM Products p"),
        @NamedQuery(name = "Products.findById", query = "SELECT p FROM Products p WHERE p.id = :id"),
        @NamedQuery(name = "Products.findByTitle", query = "SELECT p FROM Products p WHERE p.title = :title"),
        @NamedQuery(name = "Products.findByArticle", query = "SELECT p FROM Products p WHERE p.article = :article"),
        @NamedQuery(name = "Products.findByEan", query = "SELECT p FROM Products p WHERE p.ean = :ean"),
        @NamedQuery(name = "Products.findByAvailable", query = "SELECT p FROM Products p WHERE p.available = :available"),
        @NamedQuery(name = "Products.findByOutdated", query = "SELECT p FROM Products p WHERE p.outdated = :outdated"),
        @NamedQuery(name = "Products.findByPrice", query = "SELECT p FROM Products p WHERE p.price = :price"),
        @NamedQuery(name = "Products.findByRate", query = "SELECT p FROM Products p WHERE p.rate = :rate"),
        @NamedQuery(name = "Products.findByDiscount1", query = "SELECT p FROM Products p WHERE p.discount1 = :discount1"),
        @NamedQuery(name = "Products.findByDiscount2", query = "SELECT p FROM Products p WHERE p.discount2 = :discount2"),
        @NamedQuery(name = "Products.findByDiscount3", query = "SELECT p FROM Products p WHERE p.discount3 = :discount3"),
        })
public class Products implements Serializable {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productId")
    private Collection<ProductsFunctions> productsFunctionsCollection;
    @Basic(optional = false)

    @Column(name = "plugin_owner_id")
    private int pluginOwnerId;
    @Basic(optional = false)

    @Column(name = "special")
    private double special;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productId")
    private Collection<PropertyValues> propertyValuesCollection;
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "title")
    private String title;
    @Column(name = "article")
    private String article;
    @Column(name = "ean")
    private String ean;
    @Lob
    @Column(name = "description")
    private String description;
    @Lob
    @Column(name = "anons")
    private String anons;
    @Lob
    @Column(name = "delivery_time")
    private String deliveryTime;
    @Column(name = "available")
    private Integer available;
    @Column(name = "outdated")
    private Integer outdated;
    @Basic(optional = false)
    @Column(name = "price")
    private double price;
    @Column(name = "rate")
    private double rate;
    @Column(name = "discount1")
    private double discount1;
    @Column(name = "discount2")
    private double discount2;
    @Column(name = "discount3")
    private double discount3;
    @OneToMany(mappedBy = "productId")
    private Collection<Quantity> quantityCollection;
    @JoinColumn(name = "serie", referencedColumnName = "title")
    @ManyToOne
    private Series serie;
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    @ManyToOne
    private Categories categoryId;
    @JoinColumn(name = "product_kind_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ProductKinds productKindId;

    @JoinColumn(name = "vendor", referencedColumnName = "title")
    @ManyToOne(optional = true)
    private Vendors vendor;

    @OneToMany(mappedBy = "prototypeId")
    private Collection<Analogs> analogsCollection;
    @OneToMany(mappedBy = "ownerId")
    private Collection<Files> filesCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productId")
    private Collection<Properties> propertiesCollection;

    public Products() {
    }

    public Products(Integer id) {
        this.id = id;
    }

    public Products(Integer id, String title, double price) {
        this.id = id;
        this.title = title;
        this.price = price;
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

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAnons() {
        return anons;
    }

    public void setAnons(String anons) {
        this.anons = anons;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }

    public Integer getOutdated() {
        return outdated;
    }

    public void setOutdated(Integer outdated) {
        this.outdated = outdated;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getDiscount1() {
        return discount1;
    }

    public void setDiscount1(double discount1) {
        this.discount1 = discount1;
    }

    public double getDiscount2() {
        return discount2;
    }

    public void setDiscount2(double discount2) {
        this.discount2 = discount2;
    }

    public double getDiscount3() {
        return discount3;
    }

    public void setDiscount3(double discount3) {
        this.discount3 = discount3;
    }

    @XmlTransient
    public Collection<Quantity> getQuantityCollection() {
        return quantityCollection;
    }

    public void setQuantityCollection(Collection<Quantity> quantityCollection) {
        this.quantityCollection = quantityCollection;
    }

    public Series getSerie() {
        return serie;
    }

    public void setSerie(Series serie) {
        this.serie = serie;
    }

    public Categories getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Categories categoryId) {
        this.categoryId = categoryId;
    }

    public ProductKinds getProductKindId() {
        return productKindId;
    }

    public void setProductKindId(ProductKinds productKindId) {
        this.productKindId = productKindId;
    }

    public Vendors getVendor() {
        return vendor;
    }

    public void setVendor(Vendors vendor) {
        this.vendor = vendor;
    }

    @XmlTransient
    public Collection<Analogs> getAnalogsCollection() {
        return analogsCollection;
    }

    public void setAnalogsCollection(Collection<Analogs> analogsCollection) {
        this.analogsCollection = analogsCollection;
    }

    @XmlTransient
    public Collection<Files> getFilesCollection() {
        return filesCollection;
    }

    public void setFilesCollection(Collection<Files> filesCollection) {
        this.filesCollection = filesCollection;
    }

    @XmlTransient
    public Collection<Properties> getPropertiesCollection() {
        return propertiesCollection;
    }

    public void setPropertiesCollection(Collection<Properties> propertiesCollection) {
        this.propertiesCollection = propertiesCollection;
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
        if (!(object instanceof Products)) {
            return false;
        }
        Products other = (Products) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Products[ id=" + id + " ]";
    }

    @XmlTransient
    public Collection<PropertyValues> getPropertyValuesCollection() {
        return propertyValuesCollection;
    }

    public void setPropertyValuesCollection(Collection<PropertyValues> propertyValuesCollection) {
        this.propertyValuesCollection = propertyValuesCollection;
    }

    public int getPluginOwnerId() {
        return pluginOwnerId;
    }

    public void setPluginOwnerId(int pluginOwnerId) {
        this.pluginOwnerId = pluginOwnerId;
    }

    public double getSpecial() {
        return special;
    }

    public void setSpecial(double special) {
        this.special = special;
    }

    @XmlTransient
    public Collection<ProductsFunctions> getProductsFunctionsCollection() {
        return productsFunctionsCollection;
    }

    public void setProductsFunctionsCollection(Collection<ProductsFunctions> productsFunctionsCollection) {
        this.productsFunctionsCollection = productsFunctionsCollection;
    }

}
