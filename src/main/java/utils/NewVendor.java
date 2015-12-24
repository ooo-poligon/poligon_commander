/*
 *
 *
 */
package utils;


/**
 *
 * @author Igor Klekotnev
 */
public class NewVendor {
    private String title;
    private String description;
    private String currency;
    private String address;
    private Double rate;

    public NewVendor() {
        this.title= "";
        this.description = "";
    }

    public NewVendor(String title, String description, String currency, String address, Double rate) {
        this.title= title;
        this.description = description;
        this.currency = currency;
        this.address = address;
        this.rate = rate;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}
