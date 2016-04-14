/*
 *
 *
 */
package tableviews;

import javafx.beans.property.*;

/**
 *
 * @author Igor Klekotnev
 */
public class ProductsTableView {

    private final BooleanProperty available;
    private final BooleanProperty outdated;
    private final StringProperty article;
    private final StringProperty title;
    private final StringProperty description;
    private final StringProperty delivery_time;
    
    public ProductsTableView() {
        this(null, null, null, null, false, false);
    }

    public ProductsTableView(String article, String title) {
        this.article       = new SimpleStringProperty(article);
        this.title       = new SimpleStringProperty(title);
        this.description = null;
        this.delivery_time = null;
        this.available = new SimpleBooleanProperty();
        this.outdated = new SimpleBooleanProperty();
    }
    
    public ProductsTableView(String delivery_time) {
        this.article       = null;
        this.title       = null;
        this.description = null;
        this.delivery_time = new SimpleStringProperty(delivery_time);
        this.available = new SimpleBooleanProperty();
        this.outdated = new SimpleBooleanProperty();
    }

    public ProductsTableView(BooleanProperty available, BooleanProperty outdated) {
        this.article       = null;
        this.title       = null;
        this.description = null;
        this.delivery_time = null;
        this.available = available;
        this.outdated = outdated;
    }

    public ProductsTableView(String article, String title, String description, String delivery_time,
                             Boolean available, Boolean outdated) {
        this.article       = new SimpleStringProperty(article);
        this.title       = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.delivery_time = new SimpleStringProperty(delivery_time);
        this.available = new SimpleBooleanProperty(available);
        this.outdated = new SimpleBooleanProperty(outdated);
    }

    public boolean getAvailable() {
        return available.get();
    }

    public BooleanProperty availableProperty() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available.set(available);
    }

    public boolean getOutdated() {
        return outdated.get();
    }

    public BooleanProperty outdatedProperty() {
        return outdated;
    }

    public void setOutdated(boolean outdated) {
        this.outdated.set(outdated);
    }

    public String getDelivery_time() {
        return delivery_time.get();
    }

    public void setDelivery_time(String delivery_time) {
        this.delivery_time.set(delivery_time);
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