/*
 * 
 * 
 */
package new_items;


/**
 *
 * @author Igor Klekotnev
 */
public class NewCategory {
    private String title;
    private String description;
    private String imagePath;
    
    public NewCategory() {
        this.title= "";
        this.description = "";        
    }

    public NewCategory(String title, String description) {
        this.title= title;
        this.description = description;        
    }

    public NewCategory(String title, String description, String imagePath) {
        this.title= title;
        this.description = description;
        this.imagePath = imagePath;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
