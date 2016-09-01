package entities;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by developer on 01.09.16.
 */
@Entity
@Table(name = "product_kinds_properties")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "ProductKindsProperties.findAll",  query = "SELECT k FROM ProductKindsProperties k")})

public class ProductKindsProperties implements Serializable {

    @JoinColumn(name = "product_kind_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ProductKinds productKindId;
    @JoinColumn(name = "property_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Properties propertyId;

    public ProductKindsProperties() {
    }

    public ProductKinds getProductKindId() {
        return productKindId;
    }

    public void setProductKindId(ProductKinds productKindId) {
        this.productKindId = productKindId;
    }

    public Properties getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Properties propertyId) {
        this.propertyId = propertyId;
    }

}
