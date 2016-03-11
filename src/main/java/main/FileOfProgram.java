package main;

/**
 * Created by Igor Klekotnev on 03.03.2016.
 */
public class FileOfProgram {
    private int id;
    private String name;
    private String path;
    private String description;
    private int file_type_id;
    private int owner_id;

    public FileOfProgram(int id, String name, String path, String description, int file_type_id, int owner_id) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.description = description;
        this.file_type_id = file_type_id;
        this.owner_id = owner_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFile_type_id() {
        return file_type_id;
    }

    public void setFile_type_id(int file_type_id) {
        this.file_type_id = file_type_id;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }
}
