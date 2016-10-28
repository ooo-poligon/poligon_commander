package new_items;

import java.util.Date;

/**
 * Created by Igor Klekotnev on 01.02.2016.
 */
public class NewNewsItem {
    String title;
    String preview;
    String content;
    String imagePath;
    String createdAt;
    String updatedAt;

    public NewNewsItem(){
        this.title = null;
        this.preview = null;
        this.content = null;
        this.imagePath = null;
        this.createdAt = null;
        this.updatedAt = null;
    }

    public NewNewsItem(
            String title,
            String preview,
            String content,
            String imagePath,
            String createdAt,
            String updatedAt
    ) {
        this.title = title;
        this.preview = preview;
        this.content = content;
        this.imagePath = imagePath;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
