package new_items;

/**
 * Created by kataev on 21.01.2016.
 */
public class NewFunction {
    private String title;
    private String symbol;
    private String description;
    private String pictureName;
    private String picturePath;

    public NewFunction() {
        this.title = null;
        this.symbol = null;
        this.description = null;
        this.pictureName = null;
        this.picturePath = null;
    }

    public NewFunction(String title, String symbol) {
        this.title = title;
        this.symbol = symbol;
    }

    public NewFunction(String title, String symbol, String description, String pictureName, String picturePath) {
        this.title = title;
        this.symbol = symbol;
        this.description = description;
        this.pictureName = pictureName;
        this.picturePath = picturePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }
}
