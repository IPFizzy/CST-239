package model;

import java.math.BigDecimal;

/**
 * Simple data model used for reading and writing inventory items in JSON.
 *
 * This class is intentionally plain so Jackson can serialize and deserialize it easily.
 * The "type" field controls which SalableProduct subclass should be created.
 */
public class InventoryItemData {

    private String type;
    private String name;
    private String description;
    private BigDecimal price;
    private int quantity;

    /**
     * Default constructor required by Jackson.
     */
    public InventoryItemData() {
        // Required for JSON deserialization.
    }

    /**
     * Convenience constructor for building JSON-ready inventory data.
     *
     * @param type item type (WEAPON, ARMOR, HEALTH)
     * @param name product name
     * @param description product description
     * @param price product price
     * @param quantity product quantity
     */
    public InventoryItemData(String type, String name, String description, BigDecimal price, int quantity) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    /**
     * @return item type (WEAPON, ARMOR, HEALTH)
     */
    public String getType() {
        return type;
    }

    /**
     * @param type item type (WEAPON, ARMOR, HEALTH)
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return item name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name item name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return item description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description item description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return item price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * @param price item price
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * @return item quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @param quantity item quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
