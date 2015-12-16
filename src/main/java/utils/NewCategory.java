/*
 * 
 * 
 */
package utils;


/**
 *
 * @author kataev
 */
public class NewCategory {
    private String title;
    private String description;
    
    public NewCategory() {
        this.title= "";
        this.description = "";        
    }

    public NewCategory(String title, String description) {
        this.title= title;
        this.description = description;        
    }    
    
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
}
