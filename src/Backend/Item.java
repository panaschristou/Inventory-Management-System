package Backend;

import java.util.Objects;

public class Item {
    private int itemID; // Unique identifier for the item
    private String name;
    private String category;
    private String producer;
    private String countryOfOrigin;
    private double sellingUnitPrice;
    private int stock; // This might refer to a summary or specific stock info
    private String facilityID; // Facility where the stock is stored
    private String dateReceived; // Date the stock was received
    private double totalBoughtPrice; // Total price paid for the stock received
    private double boughtUnitPrice; // Price per unit bought

    public Item(int itemID, String name, String category, String producer, String countryOfOrigin, double sellingUnitPrice, int stock, String facilityID, String dateReceived, double totalBoughtPrice, double boughtUnitPrice) {
        this.itemID = itemID;
        this.name = name;
        this.category = category;
        this.producer = producer;
        this.countryOfOrigin = countryOfOrigin;
        this.sellingUnitPrice = sellingUnitPrice;
        this.stock = stock;
        this.facilityID = facilityID;
        this.dateReceived = dateReceived;
        this.totalBoughtPrice = totalBoughtPrice;
        this.boughtUnitPrice = boughtUnitPrice;
    }

    // Getters and Setters
    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public double getSellingUnitPrice() {
        return sellingUnitPrice;
    }

    public void setSellingUnitPrice(double sellingUnitPrice) {
        this.sellingUnitPrice = sellingUnitPrice;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        if (stock >= 0) {
            this.stock = stock;
        } else {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }
    }

    public String getFacilityID() {
        return facilityID;
    }

    public void setFacilityID(String facilityID) {
        this.facilityID = facilityID;
    }

    public String getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(String dateReceived) {
        this.dateReceived = dateReceived;
    }

    public double getTotalBoughtPrice() {
        return totalBoughtPrice;
    }

    public void setTotalBoughtPrice(double totalBoughtPrice) {
        this.totalBoughtPrice = totalBoughtPrice;
    }

    public double getBoughtUnitPrice() {
        return boughtUnitPrice;
    }

    public void setBoughtUnitPrice(double boughtUnitPrice) {
        this.boughtUnitPrice = boughtUnitPrice;
    }

    @Override
    public String toString() {
        return "Item{" +
               "itemID=" + itemID +
               ", name='" + name + '\'' +
               ", category='" + category + '\'' +
               ", producer='" + producer + '\'' +
               ", countryOfOrigin='" + countryOfOrigin + '\'' +
               ", sellingUnitPrice=" + sellingUnitPrice +
               ", stock=" + stock +
               ", facilityID='" + facilityID + '\'' +
               ", dateReceived='" + dateReceived + '\'' +
               ", totalBoughtPrice=" + totalBoughtPrice +
               ", boughtUnitPrice=" + boughtUnitPrice +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return itemID == item.itemID &&
               Double.compare(item.sellingUnitPrice, sellingUnitPrice) == 0 &&
               stock == item.stock &&
               Double.compare(item.totalBoughtPrice, totalBoughtPrice) == 0 &&
               Double.compare(item.boughtUnitPrice, boughtUnitPrice) == 0 &&
               Objects.equals(name, item.name) &&
               Objects.equals(category, item.category) &&
               Objects.equals(producer, item.producer) &&
               Objects.equals(countryOfOrigin, item.countryOfOrigin) &&
               Objects.equals(facilityID, item.facilityID) &&
               Objects.equals(dateReceived, item.dateReceived);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemID, name, category, producer, countryOfOrigin, sellingUnitPrice, stock, facilityID, dateReceived, totalBoughtPrice, boughtUnitPrice);
    }
}
