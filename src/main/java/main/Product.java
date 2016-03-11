package main;

/**
 * Created by Igor Klekotnev on 02.03.2016.
 */
public class Product {
    private int id;
    private int categoryId;
    private String title;
    private String description;
    private String anons;
    private String article;
    private int available;
    private String deliveryTime;
    private String ean;
    private int outdated;
    private double price;
    private String serie;
    private int productKindId;
    private String vendor;
    private int pluginOwnerId;
    private int accessoryOwnerId;
    private double rate;
    private double discount1;
    private double discount2;
    private double discount3;

    public Product(int id, int categoryId, String title, String description, String anons,
                   String article, int available, String deliveryTime, String ean, int outdated, double price,
                   String serie, int productKindId, String vendor, int pluginOwnerId, int accessoryOwnerId,
                   double rate, double discount1, double discount2, double discount3) {
        this.id = id;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.anons = anons;
        this.article = article;
        this.available = available;
        this.deliveryTime = deliveryTime;
        this.ean = ean;
        this.outdated = outdated;
        this.price = price;
        this.serie = serie;
        this.productKindId = productKindId;
        this.vendor = vendor;
        this.pluginOwnerId = pluginOwnerId;
        this.accessoryOwnerId = accessoryOwnerId;
        this.rate = rate;
        this.discount1 = discount1;
        this.discount2 = discount2;
        this.discount3 = discount3;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAnons() {
        return anons;
    }

    public void setAnons(String anons) {
        this.anons = anons;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public int getOutdated() {
        return outdated;
    }

    public void setOutdated(int outdated) {
        this.outdated = outdated;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public int getProductKindId() {
        return productKindId;
    }

    public void setProductKindId(int productKindId) {
        this.productKindId = productKindId;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public int getPluginOwnerId() {
        return pluginOwnerId;
    }

    public void setPluginOwnerId(int pluginOwnerId) {
        this.pluginOwnerId = pluginOwnerId;
    }

    public int getAccessoryOwnerId() {
        return accessoryOwnerId;
    }

    public void setAccessoryOwnerId(int accessoryOwnerId) {
        this.accessoryOwnerId = accessoryOwnerId;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getDiscount1() {
        return discount1;
    }

    public void setDiscount1(double discount1) {
        this.discount1 = discount1;
    }

    public double getDiscount2() {
        return discount2;
    }

    public void setDiscount2(double discount2) {
        this.discount2 = discount2;
    }

    public double getDiscount3() {
        return discount3;
    }

    public void setDiscount3(double discount3) {
        this.discount3 = discount3;
    }
}
