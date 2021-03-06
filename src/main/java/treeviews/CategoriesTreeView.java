/*
 * 
 * 
 */
package treeviews;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Igor Klekotnev
 */
public class CategoriesTreeView {

    private final IntegerProperty id;
    private final StringProperty title;
    private final IntegerProperty parent;
    private final IntegerProperty published;
    
    public CategoriesTreeView() {
        this(null, null, null, null);
    }
    
    public CategoriesTreeView(String title) {
        this.id     = null;
        this.title  = new SimpleStringProperty(title);
        this.parent = null;
        this.published = null;
    }

    public CategoriesTreeView(Integer id, String title, Integer parent) {
        this.id     = new SimpleIntegerProperty(id);
        this.title  = new SimpleStringProperty(title);
        this.parent = new SimpleIntegerProperty(parent);
        this.published = null;
    }

    public CategoriesTreeView(Integer id, String title, Integer parent, Integer published) {
        this.id     = new SimpleIntegerProperty(id);
        this.title  = new SimpleStringProperty(title);
        this.parent = new SimpleIntegerProperty(parent);
        this.published = new SimpleIntegerProperty(published);
    }

    public Integer getId() {
        return id.get();
    }

    public void setId(Integer id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
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

    public Integer getParent() {
        return parent.get();
    }

    public void setParent(Integer parent) {
        this.parent.set(parent);
    }
    
    public IntegerProperty parentProperty() {
        return parent;
    }

    public int getPublished() {
        return published.get();
    }

    public IntegerProperty publishedProperty() {
        return published;
    }

    public void setPublished(int published) {
        this.published.set(published);
    }
}
