package utils;

/**
 * Created by Igor Klekotnev on 22.01.2016.
 */
public class NewProductKind {
    String title;

    public NewProductKind () {
        this.title = null;
    }

    public NewProductKind (String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
