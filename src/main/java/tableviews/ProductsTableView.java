/*
 *
 *
 */
package tableviews;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Igor Klekotnev
 */
public class ProductsTableView {

    private final StringProperty article;
    private final StringProperty title;
    private final StringProperty description;
    private final StringProperty delivery_time;
    
    public ProductsTableView() {
        this(null, null, null, null);
    }

    public ProductsTableView(String article, String title) {
        this.article       = new SimpleStringProperty(article);
        this.title       = new SimpleStringProperty(title);
        this.description = null;
        this.delivery_time = null;
    }
    
    public ProductsTableView(String delivery_time) {
        this.article       = null;
        this.title       = null;
        this.description = null;
        this.delivery_time = new SimpleStringProperty(delivery_time);        
    }    

    public ProductsTableView(String article, String title, String description, String delivery_time) {
        this.article       = new SimpleStringProperty(article);
        this.title       = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.delivery_time = new SimpleStringProperty(delivery_time);        
    }
   
    public String getArticle() {
        return article.get();
    }

    public void setArticle(String article) {
        this.article.set(article);
    }

    public StringProperty articleProperty() {
        return article;
    }    
  
    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }
    
    public StringProperty descriptionProperty() {
        return description;
    }
    
    public String getDeliveryTime() {
        return delivery_time.get();
    }

    public void setDeliveryTime(String delivery_time) {
        this.delivery_time.set(delivery_time);
    }
    
    public StringProperty delivery_timeProperty() {
        return delivery_time;
    } 

}